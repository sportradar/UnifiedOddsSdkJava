/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */

package com.sportradar.unifiedodds.sdk.internal.impl;

import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.wireMockConfig;
import static com.sportradar.unifiedodds.sdk.fixtures.api.generic.GenericApiSimulator.ApiStubDelay.toBeDelayedBy;
import static com.sportradar.unifiedodds.sdk.fixtures.api.generic.GenericApiSimulator.HeaderEquality.*;
import static com.sportradar.unifiedodds.sdk.fixtures.api.generic.GenericApiSimulator.MappingBuilderFromMethod.*;
import static com.sportradar.unifiedodds.sdk.internal.commoniam.OAuth2TokenCacheFixtures.providingBearerToken;
import static com.sportradar.unifiedodds.sdk.internal.impl.HttpDataFetchers.createHttpHelperBuilder;
import static java.time.temporal.ChronoUnit.SECONDS;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.github.tomakehurst.wiremock.common.ConsoleNotifier;
import com.github.tomakehurst.wiremock.http.Fault;
import com.github.tomakehurst.wiremock.junit5.WireMockExtension;
import com.sportradar.unifiedodds.sdk.LoggerDefinitions;
import com.sportradar.unifiedodds.sdk.cfg.UofConfigurationStub;
import com.sportradar.unifiedodds.sdk.cfg.UofPrivateKeyJwtAuthenticationStub;
import com.sportradar.unifiedodds.sdk.exceptions.CommunicationException;
import com.sportradar.unifiedodds.sdk.fixtures.api.generic.GenericApiSimulator;
import com.sportradar.unifiedodds.sdk.internal.commoniam.OAuth2TokenCache;
import com.sportradar.unifiedodds.sdk.internal.commoniam.OAuth2TokenCacheFixtures;
import com.sportradar.unifiedodds.sdk.internal.impl.rabbitconnection.LogsMock;
import java.io.IOException;
import java.net.SocketTimeoutException;
import java.util.stream.Collectors;
import lombok.val;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;

@SuppressWarnings({ "ClassFanOutComplexity", "ConstantName", "MagicNumber" })
public class HttpHelperHeadersTest {

    @RegisterExtension
    private static final WireMockExtension wireMock = WireMockExtension
        .newInstance()
        .options(wireMockConfig().dynamicPort().notifier(new ConsoleNotifier(true)))
        .build();

    private static final String SOME_PATH = "/v1/api/any-url.xml";
    private static final String TRACE_HEADER_NAME = "trace-id";
    private static final String X_ACCESS_TOKEN = "x-access-token";
    private static final String AUTHORIZATION = "Authorization";

    private LogsMock logsMock;
    private GenericApiSimulator apiSimulator;
    private String baseUrl;

    @BeforeEach
    void initTestContext() {
        baseUrl = "http://localhost:" + wireMock.getPort();
        apiSimulator = new GenericApiSimulator(wireMock.getRuntimeInfo().getWireMock());
        logsMock = LogsMock.createCapturingFor(LoggerDefinitions.UfSdkRestTrafficLog.class);
    }

    @Nested
    class AuthorizationRelated {

        @Test
        void clientAuthorizationIsPreferredAuthenticationMethodOverAccessToken()
            throws CommunicationException {
            val deprecatedConfiguration = createConfig();
            val config = new UofConfigurationStub();
            config.setClientAuthentication(new UofPrivateKeyJwtAuthenticationStub());
            config.setAccessToken("some_access_token");

            val tokenCache = providingBearerToken("some_jwt_token");

            val httpHelper = createHttpHelperBuilder()
                .with(deprecatedConfiguration)
                .with(config)
                .with(tokenCache)
                .build();

            apiSimulator.stubRequest(
                POST,
                SOME_PATH,
                requiringHeader(AUTHORIZATION, "Bearer some_jwt_token"),
                requiringNoHeader(X_ACCESS_TOKEN)
            );

            val responseData = httpHelper.post(baseUrl + SOME_PATH);

            assertThat(responseData.getStatusCode()).isEqualTo(200);
        }

        @Test
        void sendsOAuthAuthorizationHeaderWhenClientAuthenticationIsConfigured()
            throws CommunicationException {
            val deprecatedConfiguration = createConfig();
            val config = new UofConfigurationStub();
            config.setClientAuthentication(new UofPrivateKeyJwtAuthenticationStub());

            val tokenCache = providingBearerToken("some_jwt_token");

            val httpHelper = createHttpHelperBuilder()
                .with(deprecatedConfiguration)
                .with(config)
                .with(tokenCache)
                .build();

            apiSimulator.stubRequest(
                POST,
                SOME_PATH,
                requiringHeader(AUTHORIZATION, "Bearer some_jwt_token")
            );

            val responseData = httpHelper.post(baseUrl + SOME_PATH);

            assertThat(responseData.getStatusCode()).isEqualTo(200);
        }

        @Test
        void doesNotSendAuthorizationHeaderWhenClientAuthenticationIsNotConfigured()
            throws CommunicationException {
            val deprecatedConfiguration = createConfig();
            val config = new UofConfigurationStub();
            val tokenCache = providingBearerToken("some_jwt_token");

            val httpHelper = createHttpHelperBuilder()
                .with(deprecatedConfiguration)
                .with(config)
                .with(tokenCache)
                .build();

            apiSimulator.stubRequest(POST, SOME_PATH, requiringNoHeader(AUTHORIZATION));

            val responseData = httpHelper.post(baseUrl + SOME_PATH);

            assertThat(responseData.getStatusCode()).isEqualTo(200);
        }

        @Test
        void attemptingToGetTokenFailsWithDataProviderExceptionWrapperAroundOAuthException() {
            val deprecatedConfiguration = createConfig();
            val config = new UofConfigurationStub();
            config.setClientAuthentication(new UofPrivateKeyJwtAuthenticationStub());

            val tokenCache = OAuth2TokenCacheFixtures.failingWithOAuth2TokenRetrievalException();

            val httpHelper = createHttpHelperBuilder()
                .with(deprecatedConfiguration)
                .with(config)
                .with(tokenCache)
                .build();

            assertThatThrownBy(() -> httpHelper.post(baseUrl + SOME_PATH))
                .isInstanceOf(CommunicationException.class)
                .hasRootCauseInstanceOf(OAuth2TokenCache.OAuth2TokenRetrievalException.class);
        }

        @Test
        void attemptingToGetTokenFailsWithDataProviderExceptionWrapperAroundOAuthHttpException() {
            val deprecatedConfiguration = createConfig();
            val config = new UofConfigurationStub();
            config.setClientAuthentication(new UofPrivateKeyJwtAuthenticationStub());

            val tokenCache = OAuth2TokenCacheFixtures.failingWithOAuth2TokenRetrievalHttpException(
                SOME_PATH,
                400
            );

            val httpHelper = createHttpHelperBuilder()
                .with(deprecatedConfiguration)
                .with(config)
                .with(tokenCache)
                .build();

            assertThatThrownBy(() -> httpHelper.post(baseUrl + SOME_PATH))
                .isInstanceOf(CommunicationException.class)
                .hasRootCauseInstanceOf(OAuth2TokenCache.OAuth2TokenRetrievalHttpException.class);
        }

        @Test
        void sendsAccessTokenHeader() throws Exception {
            val deprecatedConfiguration = createConfig();
            val config = new UofConfigurationStub();
            config.setAccessToken("some_access_token");

            val httpHelper = createHttpHelperBuilder().with(deprecatedConfiguration).with(config).build();

            apiSimulator.stubRequest(POST, SOME_PATH, requiringHeader(X_ACCESS_TOKEN, "some_access_token"));

            val responseData = httpHelper.post(baseUrl + SOME_PATH);

            assertThat(responseData.getStatusCode()).isEqualTo(200);
        }

        @Test
        void doesNotSendAccessTokenHeaderWhenAccessTokenIsNotConfigured() throws CommunicationException {
            val deprecatedConfiguration = createConfig();
            val config = new UofConfigurationStub();

            val httpHelper = createHttpHelperBuilder().with(deprecatedConfiguration).with(config).build();

            apiSimulator.stubRequest(POST, SOME_PATH, requiringNoHeader(X_ACCESS_TOKEN));

            val responseData = httpHelper.post(baseUrl + SOME_PATH);

            assertThat(responseData.getStatusCode()).isEqualTo(200);
        }
    }

    @Nested
    public class TraceId {

        @Test
        void sendTraceIdHeaderOnSuccessfulPostRequest() throws Exception {
            val deprecatedConfiguration = createConfig();
            val config = new UofConfigurationStub();

            val httpHelper = createHttpHelperBuilder().with(deprecatedConfiguration).with(config).build();

            apiSimulator.stubRequest(POST, SOME_PATH, requiringHeaderWithAnyUuidValue(TRACE_HEADER_NAME));

            val responseData = httpHelper.post(baseUrl + SOME_PATH);

            assertThat(responseData.getStatusCode()).isEqualTo(200);
            logsMock.loggedLineContains(getTheOnlySubmittedTraceIdForPath(SOME_PATH));
        }

        @Test
        void sendNewTraceIdHeaderPerRequest() throws CommunicationException {
            val deprecatedConfiguration = createConfig();
            val config = new UofConfigurationStub();

            val httpHelper = createHttpHelperBuilder().with(deprecatedConfiguration).with(config).build();

            apiSimulator.stubRequest(POST, SOME_PATH, requiringHeaderWithAnyUuidValue(TRACE_HEADER_NAME));
            val responseData1 = httpHelper.post(baseUrl + SOME_PATH);
            apiSimulator.stubRequest(POST, SOME_PATH, requiringHeaderWithAnyUuidValue(TRACE_HEADER_NAME));
            val responseData2 = httpHelper.post(baseUrl + SOME_PATH);

            val traceIds = getTraceIdsForAllRequestsToPath(SOME_PATH);
            assertThat(traceIds.stream().distinct()).hasSize(traceIds.size());

            assertThat(responseData1.getStatusCode()).isEqualTo(200);
            assertThat(responseData2.getStatusCode()).isEqualTo(200);
            traceIds.forEach(logsMock::loggedLineContains);
        }

        @Test
        void logsTraceIdHeaderOn4xxPostRequest() throws Exception {
            val cfg = createConfig();
            val httpHelper = createHttpHelperBuilder().with(cfg).build();

            apiSimulator.stubBadRequest(POST, SOME_PATH, requiringHeaderWithAnyUuidValue(TRACE_HEADER_NAME));

            val responseData = httpHelper.post(baseUrl + SOME_PATH);

            assertThat(responseData.getStatusCode()).isEqualTo(400);
            logsMock.loggedLineContains(getTheOnlySubmittedTraceIdForPath(SOME_PATH));
        }

        @Test
        void logsTraceIdHeaderOnFailedPostRequest() throws Exception {
            val cfg = createConfig();
            val httpHelper = createHttpHelperBuilder().with(cfg).build();

            apiSimulator.stubFailure(POST, SOME_PATH, Fault.MALFORMED_RESPONSE_CHUNK);

            assertThatThrownBy(() -> httpHelper.post(baseUrl + SOME_PATH))
                .isInstanceOf(CommunicationException.class)
                .hasRootCauseInstanceOf(IOException.class);
            logsMock.loggedLineContains(getTheOnlySubmittedTraceIdForPath(SOME_PATH));
        }

        @Test
        void sendTraceIdHeaderOnSuccessfulPutRequest() throws Exception {
            val cfg = createConfig();
            val httpHelper = createHttpHelperBuilder().with(cfg).build();

            apiSimulator.stubRequest(PUT, SOME_PATH, requiringHeaderWithAnyUuidValue(TRACE_HEADER_NAME));

            val responseData = httpHelper.put(baseUrl + SOME_PATH);

            assertThat(responseData.getStatusCode()).isEqualTo(200);
            logsMock.loggedLineContains(getTheOnlySubmittedTraceIdForPath(SOME_PATH));
        }

        @Test
        void logsTraceIdHeaderOn4xxPutRequest() throws Exception {
            val cfg = createConfig();
            val httpHelper = createHttpHelperBuilder().with(cfg).build();

            apiSimulator.stubBadRequest(PUT, SOME_PATH, requiringHeaderWithAnyUuidValue(TRACE_HEADER_NAME));

            val responseData = httpHelper.put(baseUrl + SOME_PATH);

            assertThat(responseData.getStatusCode()).isEqualTo(400);
            logsMock.loggedLineContains(getTheOnlySubmittedTraceIdForPath(SOME_PATH));
        }

        @Test
        void logsTraceIdHeaderOnFailedPutRequest() throws Exception {
            val cfg = createConfig();
            val httpHelper = createHttpHelperBuilder().with(cfg).build();

            apiSimulator.stubFailure(PUT, SOME_PATH, Fault.MALFORMED_RESPONSE_CHUNK);

            assertThatThrownBy(() -> httpHelper.put(baseUrl + SOME_PATH))
                .isInstanceOf(CommunicationException.class)
                .hasRootCauseInstanceOf(IOException.class);
            logsMock.loggedLineContains(getTheOnlySubmittedTraceIdForPath(SOME_PATH));
        }

        @Test
        void sendTraceIdHeaderOnSuccessfulDeleteRequest() throws Exception {
            val cfg = createConfig();
            val httpHelper = createHttpHelperBuilder().with(cfg).build();

            apiSimulator.stubRequest(DELETE, SOME_PATH, requiringHeaderWithAnyUuidValue(TRACE_HEADER_NAME));

            val responseData = httpHelper.delete(baseUrl + SOME_PATH);

            assertThat(responseData.getStatusCode()).isEqualTo(200);
            logsMock.loggedLineContains(getTheOnlySubmittedTraceIdForPath(SOME_PATH));
        }

        @Test
        void logsTraceIdHeaderOn4xxDeleteRequest() throws Exception {
            val cfg = createConfig();
            val httpHelper = createHttpHelperBuilder().with(cfg).build();

            apiSimulator.stubBadRequest(
                DELETE,
                SOME_PATH,
                requiringHeaderWithAnyUuidValue(TRACE_HEADER_NAME)
            );

            val responseData = httpHelper.delete(baseUrl + SOME_PATH);

            assertThat(responseData.getStatusCode()).isEqualTo(400);
            logsMock.loggedLineContains(getTheOnlySubmittedTraceIdForPath(SOME_PATH));
        }

        @Test
        void logsTraceIdHeaderOnFailedDeleteRequest() throws Exception {
            val cfg = createConfig();
            val httpHelper = createHttpHelperBuilder().with(cfg).build();

            apiSimulator.stubFailure(DELETE, SOME_PATH, Fault.MALFORMED_RESPONSE_CHUNK);

            assertThatThrownBy(() -> httpHelper.delete(baseUrl + SOME_PATH))
                .isInstanceOf(CommunicationException.class)
                .hasRootCauseInstanceOf(IOException.class);
            logsMock.loggedLineContains(getTheOnlySubmittedTraceIdForPath(SOME_PATH));
        }

        @Test
        void sendTraceIdHeaderForPostRequestOnTimeout() throws Exception {
            val cfg = createConfig(1);
            val httpHelper = createHttpHelperBuilder().with(cfg).build();

            apiSimulator.stubRequest(
                POST,
                SOME_PATH,
                requiringHeaderWithAnyUuidValue(TRACE_HEADER_NAME),
                toBeDelayedBy(cfg.getHttpClientTimeout() + 1, SECONDS)
            );

            assertThatThrownBy(() -> httpHelper.post(baseUrl + SOME_PATH))
                .isInstanceOf(CommunicationException.class)
                .hasRootCauseInstanceOf(SocketTimeoutException.class);

            logsMock.loggedLineContains(getTheOnlySubmittedTraceIdForPath(SOME_PATH));
        }

        @Test
        void sendTraceIdHeaderForPutRequestOnTimeout() throws Exception {
            val cfg = createConfig(1);
            val httpHelper = createHttpHelperBuilder().with(cfg).build();

            apiSimulator.stubRequest(
                PUT,
                SOME_PATH,
                requiringHeaderWithAnyUuidValue(TRACE_HEADER_NAME),
                toBeDelayedBy(cfg.getHttpClientTimeout() + 1, SECONDS)
            );

            assertThatThrownBy(() -> httpHelper.put(baseUrl + SOME_PATH))
                .isInstanceOf(CommunicationException.class)
                .hasRootCauseInstanceOf(SocketTimeoutException.class);
            logsMock.loggedLineContains(getTheOnlySubmittedTraceIdForPath(SOME_PATH));
        }

        @Test
        void sendTraceIdHeaderForDeleteRequestOnTimeout() throws Exception {
            val cfg = createConfig(1);
            val httpHelper = createHttpHelperBuilder().with(cfg).build();

            apiSimulator.stubRequest(
                DELETE,
                SOME_PATH,
                requiringHeaderWithAnyUuidValue(TRACE_HEADER_NAME),
                toBeDelayedBy(cfg.getHttpClientTimeout() + 1, SECONDS)
            );

            assertThatThrownBy(() -> httpHelper.delete(baseUrl + SOME_PATH))
                .isInstanceOf(CommunicationException.class)
                .hasRootCauseInstanceOf(SocketTimeoutException.class);
            logsMock.loggedLineContains(getTheOnlySubmittedTraceIdForPath(SOME_PATH));
        }
    }

    private SdkInternalConfiguration createConfig() {
        return createConfig(5);
    }

    private SdkInternalConfiguration createConfig(int timeoutInSeconds) {
        val cfg = mock(SdkInternalConfiguration.class);
        when(cfg.getUseApiSsl()).thenReturn(false);
        when(cfg.getHttpClientTimeout()).thenReturn(timeoutInSeconds);
        when(cfg.getRecoveryHttpClientTimeout()).thenReturn(timeoutInSeconds);
        when(cfg.getFastHttpClientTimeout()).thenReturn((long) timeoutInSeconds);
        return cfg;
    }

    private static String getTheOnlySubmittedTraceIdForPath(String url) {
        val events = wireMock.getAllServeEvents();
        val traceIds = events
            .stream()
            .filter(e -> e.getRequest().getUrl().equals(url))
            .map(e -> e.getRequest().getHeader("trace-id"))
            .collect(Collectors.toList());
        assertThat(traceIds).hasSize(1);
        return traceIds.get(0);
    }

    private static java.util.List<String> getTraceIdsForAllRequestsToPath(String url) {
        val events = wireMock.getAllServeEvents();
        return events
            .stream()
            .filter(e -> e.getRequest().getUrl().equals(url))
            .map(e -> e.getRequest().getHeader("trace-id"))
            .collect(Collectors.toList());
    }
}
