/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */

package com.sportradar.unifiedodds.sdk.internal.commoniam;

import static com.sportradar.unifiedodds.sdk.internal.cfg.BaseUrl.baseUrl;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.inject.Inject;
import com.google.inject.name.Named;
import com.sportradar.unifiedodds.sdk.LoggerDefinitions;
import com.sportradar.unifiedodds.sdk.cfg.UofClientAuthentication;
import com.sportradar.unifiedodds.sdk.cfg.UofConfiguration;
import com.sportradar.unifiedodds.sdk.internal.impl.TimeUtils;
import com.sportradar.utils.jacoco.ExcludeFromJacocoGeneratedReportUntestableCheckedException;
import java.io.UnsupportedEncodingException;
import java.security.interfaces.RSAPrivateKey;
import java.time.Duration;
import java.util.Objects;
import java.util.concurrent.*;
import org.apache.hc.client5.http.async.methods.SimpleHttpResponse;
import org.apache.hc.client5.http.async.methods.SimpleRequestBuilder;
import org.apache.hc.client5.http.impl.async.CloseableHttpAsyncClient;
import org.apache.hc.core5.http.ContentType;
import org.apache.hc.core5.http.HttpStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@SuppressWarnings("ClassFanOutComplexity")
public class CommonIamTokenCache implements OAuth2TokenCache {

    @SuppressWarnings("ConstantName")
    private static final Logger trafficLogger = LoggerFactory.getLogger(
        LoggerDefinitions.UfSdkRestTrafficLog.class
    );

    @SuppressWarnings("ConstantName")
    private static final Logger logger = LoggerFactory.getLogger(CommonIamTokenCache.class);

    private static final long SECONDS_TO_MILLIS = 1000L;
    private static final int ONE_MIN_IN_MILLIS = 60 * 1000;
    private static final String FAILED_TO_RETRIEVE_O_AUTH_TOKEN = "Failed to retrieve OAuth token";
    private static final String JWT_REGEX = "\"[a-zA-Z0-9_-]+\\.[a-zA-Z0-9_-]+\\.[a-zA-Z0-9_-]+\"";
    private static final String OAUTH_TOKEN_PATH = "/oauth/token";
    private final UofConfiguration configuration;
    private final CloseableHttpAsyncClient httpClient;
    private final TimeUtils timeUtils;
    private final ObjectMapper objectMapper;
    private final Object refreshLock = new Object();
    private volatile OAuth2Token cachedToken;
    private volatile long tokenExpirationTime;
    private final CommonIamTokenRetrievalCircuitBreaker circuitBreaker;
    private final ResourceAudience resourceAudience;

    @Inject
    public CommonIamTokenCache(
        UofConfiguration configuration,
        @Named("FastHttpClient") CloseableHttpAsyncClient httpClient,
        TimeUtils timeUtils,
        ObjectMapper objectMapper,
        @Named("UfRestApiAudience") ResourceAudience resourceAudience
    ) {
        this.configuration = configuration;
        this.httpClient = httpClient;
        this.timeUtils = timeUtils;
        this.objectMapper = objectMapper;
        this.circuitBreaker = new CommonIamTokenRetrievalCircuitBreaker(timeUtils);
        this.resourceAudience = resourceAudience;
    }

    @Override
    public OAuth2Token getToken() {
        if (isTokenValid()) {
            return cachedToken;
        }

        synchronized (refreshLock) {
            if (isTokenValid()) {
                return cachedToken;
            }
            return refreshToken();
        }
    }

    @Override
    public void invalidateToken(OAuth2Token token) {
        if (cachedToken != null && cachedToken.equals(token)) {
            cachedToken = null;
            tokenExpirationTime = 0;
        }
    }

    private boolean isTokenValid() {
        return cachedToken != null && timeUtils.now() < tokenExpirationTime;
    }

    @SuppressWarnings("IllegalCatch")
    private OAuth2Token refreshToken() {
        circuitBreaker.throwIfOpen();

        try {
            String requestBody = clientCredentialsWithJwtBearerClientAssertionRequest();
            SimpleHttpResponse response = postAccessTokenRequest(requestBody);
            OAuth2TokenResponse tokenResponse = parse(response);
            validate(tokenResponse);

            cachedToken = new OAuth2Token(tokenResponse.getTokenType(), tokenResponse.getAccessToken());
            tokenExpirationTime = timeUtils.now() + (tokenResponse.getExpiresIn() * SECONDS_TO_MILLIS);
            circuitBreaker.recordSuccess();
            return cachedToken;
        } catch (Exception e) {
            logger.warn(FAILED_TO_RETRIEVE_O_AUTH_TOKEN, e);
            circuitBreaker.recordFailure();
            throw new OAuth2TokenRetrievalException(FAILED_TO_RETRIEVE_O_AUTH_TOKEN, e);
        }
    }

    private static void validate(OAuth2TokenResponse tokenResponse) {
        if (tokenResponse.getAccessToken() == null) {
            throw new OAuth2TokenRetrievalException("Common IAM dit not return access_token value");
        }
        if (tokenResponse.getExpiresIn() == null) {
            throw new OAuth2TokenRetrievalException("Common IAM did not return expires_in value");
        }
        if (tokenResponse.getTokenType() == null) {
            throw new OAuth2TokenRetrievalException("Common IAM did not return token_type value");
        }
    }

    private OAuth2TokenResponse parse(SimpleHttpResponse response) throws JsonProcessingException {
        if (response.getCode() != HttpStatus.SC_OK) {
            String body = Objects.toString(response.getBodyText());
            trafficLogger.info(
                "Request[OAuth2]: {}, response - FAILED(HTTP {}): {}",
                getAuthServerUrl() + OAUTH_TOKEN_PATH,
                response.getCode(),
                anonymizeJwts(body)
            );
            throw new OAuth2TokenRetrievalHttpException(OAUTH_TOKEN_PATH, response.getCode());
        }

        if (trafficLogger.isInfoEnabled()) {
            String body = Objects.toString(response.getBodyText());
            String anonymizedResponse = anonymizeJwts(body);
            trafficLogger.info(
                "Request[OAuth2]: {}, response - OK(HTTP {}): {}",
                getAuthServerUrl() + OAUTH_TOKEN_PATH,
                response.getCode(),
                anonymizedResponse
            );
        }

        return objectMapper.readValue(response.getBodyText(), OAuth2TokenResponse.class);
    }

    private String anonymizeJwts(String responseBody) {
        return responseBody.replaceAll(JWT_REGEX, "***ANONYMIZED***");
    }

    private SimpleHttpResponse postAccessTokenRequest(String requestBody)
        throws ExecutionException, TimeoutException {
        SimpleRequestBuilder request = SimpleRequestBuilder
            .post(getAuthServerUrl() + OAUTH_TOKEN_PATH)
            .setHeader("Content-Type", "application/x-www-form-urlencoded")
            .setBody(requestBody, ContentType.APPLICATION_FORM_URLENCODED);

        Future<SimpleHttpResponse> future = httpClient.execute(request.build(), null);
        return getResponseWithTimeout(future);
    }

    private String clientCredentialsWithJwtBearerClientAssertionRequest()
        throws UnsupportedEncodingException {
        String jwtAssertion = createJwtAssertion();
        return String.format(
            "grant_type=client_credentials&client_assertion_type=%s&client_assertion=%s&audience=%s",
            "urn:ietf:params:oauth:client-assertion-type:jwt-bearer",
            java.net.URLEncoder.encode(jwtAssertion, "UTF-8"),
            resourceAudience.getValue()
        );
    }

    private String getAuthServerUrl() {
        UofClientAuthentication.PrivateKeyJwt auth = configuration.getClientAuthentication();
        return baseUrl()
            .setUseSsl(auth.getUseSsl())
            .setHost(auth.getHost())
            .setPort(auth.getPort())
            .toString();
    }

    private String createJwtAssertion() {
        UofClientAuthentication.PrivateKeyJwt auth = configuration.getClientAuthentication();
        long now = timeUtils.now();
        String audience = getAuthServerUrl() + "/";

        return JWT
            .create()
            .withKeyId(auth.getSigningKeyId())
            .withIssuer(auth.getClientId())
            .withSubject(auth.getClientId())
            .withAudience(audience)
            .withIssuedAt(new java.util.Date(now))
            .withExpiresAt(new java.util.Date(now + ONE_MIN_IN_MILLIS))
            .withJWTId(java.util.UUID.randomUUID().toString())
            .sign(Algorithm.RSA256(null, (RSAPrivateKey) auth.getPrivateKey()));
    }

    private SimpleHttpResponse getResponseWithTimeout(Future<SimpleHttpResponse> future)
        throws ExecutionException, TimeoutException {
        Duration timeout = configuration.getApi().getHttpClientFastFailingTimeout();
        return getSimpleHttpResponseRethrowingInterruptedExceptionAsDomainException(future, timeout);
    }

    @ExcludeFromJacocoGeneratedReportUntestableCheckedException
    private SimpleHttpResponse getSimpleHttpResponseRethrowingInterruptedExceptionAsDomainException(
        Future<SimpleHttpResponse> future,
        Duration timeout
    ) throws ExecutionException, TimeoutException {
        try {
            return future.get(timeout.getSeconds(), TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new OAuth2TokenRetrievalException(FAILED_TO_RETRIEVE_O_AUTH_TOKEN, e);
        }
    }

    private static class OAuth2TokenResponse {

        private final String accessToken;
        private final String tokenType;
        private final Long expiresIn;

        @JsonCreator
        public OAuth2TokenResponse(
            @JsonProperty(value = "access_token", required = true) String accessToken,
            @JsonProperty(value = "token_type", required = true) String tokenType,
            @JsonProperty(value = "expires_in", required = true) Long expiresIn
        ) {
            this.accessToken = accessToken;
            this.tokenType = tokenType;
            this.expiresIn = expiresIn;
        }

        public String getAccessToken() {
            return accessToken;
        }

        public String getTokenType() {
            return tokenType;
        }

        public Long getExpiresIn() {
            return expiresIn;
        }
    }
}
