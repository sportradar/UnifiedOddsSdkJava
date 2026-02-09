/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */

package com.sportradar.unifiedodds.sdk.internal.impl;

import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.wireMockConfig;
import static com.sportradar.unifiedodds.sdk.fixtures.api.generic.GenericApiSimulator.ApiStubDelay.toBeDelayedBy;
import static com.sportradar.unifiedodds.sdk.fixtures.api.generic.GenericApiSimulator.HeaderEquality.*;
import static com.sportradar.unifiedodds.sdk.fixtures.api.generic.GenericApiSimulator.MappingBuilderFromMethod.*;
import static com.sportradar.unifiedodds.sdk.internal.commoniam.OAuth2TokenCacheFixtures.builder;
import static com.sportradar.unifiedodds.sdk.internal.commoniam.OAuth2TokenCacheFixtures.providingBearerToken;
import static com.sportradar.unifiedodds.sdk.internal.impl.HttpDataFetchers.createHttpHelperBuilder;
import static java.time.temporal.ChronoUnit.SECONDS;
import static org.assertj.core.api.Assertions.*;
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
import com.sportradar.unifiedodds.sdk.fixtures.api.generic.GenericApiSimulator.MappingBuilderFromMethod;
import com.sportradar.unifiedodds.sdk.internal.commoniam.OAuth2TokenCache;
import com.sportradar.unifiedodds.sdk.internal.commoniam.OAuth2TokenCacheFixtures;
import com.sportradar.unifiedodds.sdk.internal.impl.apireaders.HttpHelper;
import com.sportradar.unifiedodds.sdk.internal.impl.apireaders.HttpHelper.ResponseData;
import com.sportradar.unifiedodds.sdk.internal.impl.rabbitconnection.LogsMock;
import java.io.IOException;
import java.net.SocketTimeoutException;
import java.util.List;
import java.util.stream.Collectors;
import lombok.SneakyThrows;
import lombok.val;
import org.apache.hc.core5.http.HttpStatus;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

@SuppressWarnings(
    { "ClassFanOutComplexity", "ConstantName", "MagicNumber", "MultipleStringLiterals", "LineLength" }
)
public class HttpHelperHeadersTest {

    @RegisterExtension
    private static final WireMockExtension wireMock = WireMockExtension
        .newInstance()
        .options(
            wireMockConfig()
                .dynamicPort()
                .notifier(new ConsoleNotifier(true))
                .extensions(new TwoRequestDelayingBarrier())
        )
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
    class RetryRequestsWithClientAuthentication {

        @ParameterizedTest
        @EnumSource(MappingBuilderFromMethod.class)
        void requestRetriesWithNewTokenAfter401unauthorized(MappingBuilderFromMethod httpVerb)
            throws Exception {
            val deprecatedConfiguration = createConfig();
            val config = new UofConfigurationStub();
            config.setClientAuthentication(new UofPrivateKeyJwtAuthenticationStub());

            val tokenCache = builder()
                .providingBearerToken("firstToken")
                .afterFirstInvalidationProviding("secondToken")
                .build();

            val httpHelper = createHttpHelperBuilder()
                .with(deprecatedConfiguration)
                .with(config)
                .with(tokenCache)
                .build();

            apiSimulator.stubUnauthorizedRequest(
                httpVerb,
                SOME_PATH,
                requiringHeader(AUTHORIZATION, "Bearer firstToken")
            );

            apiSimulator.stubStatus200For(
                httpVerb,
                SOME_PATH,
                requiringHeader(AUTHORIZATION, "Bearer secondToken")
            );

            val responseData = sendRequestUsing(httpHelper, httpVerb, baseUrl + SOME_PATH);

            assertThat(responseData.getStatusCode()).isEqualTo(200);
        }

        @ParameterizedTest
        @EnumSource(MappingBuilderFromMethod.class)
        void requestRetriesExactlyOnceWithNewTokenAfterInitial401(MappingBuilderFromMethod httpVerb)
            throws Exception {
            val deprecatedConfiguration = createConfig();
            val config = new UofConfigurationStub();
            config.setClientAuthentication(new UofPrivateKeyJwtAuthenticationStub());

            val tokenCache = builder()
                .providingBearerToken("firstToken")
                .afterFirstInvalidationProviding("secondToken")
                .build();
            val httpHelper = createHttpHelperBuilder()
                .with(deprecatedConfiguration)
                .with(config)
                .with(tokenCache)
                .build();

            apiSimulator.stubUnauthorizedRequest(
                httpVerb,
                SOME_PATH,
                requiringHeader(AUTHORIZATION, "Bearer firstToken")
            );
            apiSimulator.stubStatus200For(
                httpVerb,
                SOME_PATH,
                requiringHeader(AUTHORIZATION, "Bearer secondToken")
            );

            sendRequestUsing(httpHelper, httpVerb, baseUrl + SOME_PATH);

            apiSimulator.verifyTotalCalls(2, SOME_PATH);
        }

        @ParameterizedTest
        @EnumSource(MappingBuilderFromMethod.class)
        void requestDoesNotRetryAfter401WhenSsoTokenIsConfigured(MappingBuilderFromMethod httpVerb)
            throws Exception {
            val deprecatedConfiguration = createConfig();
            val config = new UofConfigurationStub();
            config.setAccessToken("access_token");

            val httpHelper = createHttpHelperBuilder().with(deprecatedConfiguration).with(config).build();

            apiSimulator.stubUnauthorizedRequest(
                httpVerb,
                SOME_PATH,
                requiringHeader(X_ACCESS_TOKEN, "access_token")
            );

            sendRequestUsing(httpHelper, httpVerb, baseUrl + SOME_PATH);

            apiSimulator.verifyTotalCalls(1, SOME_PATH);
        }

        @ParameterizedTest
        @EnumSource(MappingBuilderFromMethod.class)
        void requestRetriesOnceWhenApiKeepsFailingWith401(MappingBuilderFromMethod httpVerb)
            throws Exception {
            val deprecatedConfiguration = createConfig();
            val config = new UofConfigurationStub();
            config.setClientAuthentication(new UofPrivateKeyJwtAuthenticationStub());

            val tokenCache = providingBearerToken("token-for-failing-request");
            val httpHelper = createHttpHelperBuilder()
                .with(deprecatedConfiguration)
                .with(config)
                .with(tokenCache)
                .build();

            apiSimulator.stubUnauthorizedRequest(
                httpVerb,
                SOME_PATH,
                requiringHeader(AUTHORIZATION, "Bearer token-for-failing-request")
            );

            val response = sendRequestUsing(httpHelper, httpVerb, baseUrl + SOME_PATH);

            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.SC_UNAUTHORIZED);
            apiSimulator.verifyTotalCalls(2, SOME_PATH);
        }

        @ParameterizedTest
        @EnumSource(MappingBuilderFromMethod.class)
        void requestSendsUniqueTraceIdWhenRetries(MappingBuilderFromMethod httpVerb) throws Exception {
            val deprecatedConfiguration = createConfig();
            val config = new UofConfigurationStub();
            config.setClientAuthentication(new UofPrivateKeyJwtAuthenticationStub());

            val tokenCache = builder()
                .providingBearerToken("firstToken")
                .afterFirstInvalidationProviding("secondToken")
                .build();
            val httpHelper = createHttpHelperBuilder()
                .with(deprecatedConfiguration)
                .with(config)
                .with(tokenCache)
                .build();

            apiSimulator.stubUnauthorizedRequest(
                httpVerb,
                SOME_PATH,
                requiringHeader(AUTHORIZATION, "Bearer firstToken")
            );
            apiSimulator.stubStatus200For(
                httpVerb,
                SOME_PATH,
                requiringHeader(AUTHORIZATION, "Bearer secondToken")
            );

            sendRequestUsing(httpHelper, httpVerb, baseUrl + SOME_PATH);

            val traceIds = getUniqueTraceIdsFromWiremockRequests();
            assertThat(traceIds).hasSize(2);
        }

        @NotNull
        private List<String> getUniqueTraceIdsFromWiremockRequests() {
            val events = wireMock.getAllServeEvents();
            return events
                .stream()
                .map(e -> e.getRequest().getHeader(TRACE_HEADER_NAME))
                .distinct()
                .collect(Collectors.toList());
        }

        @ParameterizedTest
        @EnumSource(MappingBuilderFromMethod.class)
        void attemptingToGetTokenFailsWithDataProviderExceptionWrappedAroundOAuthExceptionAfterInvalidation(
            MappingBuilderFromMethod httpVerb
        ) {
            val deprecatedConfiguration = createConfig();
            val config = new UofConfigurationStub();
            config.setClientAuthentication(new UofPrivateKeyJwtAuthenticationStub());

            val tokenCache = OAuth2TokenCacheFixtures.failingWithOAuth2TokenRetrievalExceptionAfterInvalidationOf(
                "aToken"
            );

            val httpHelper = createHttpHelperBuilder()
                .with(deprecatedConfiguration)
                .with(config)
                .with(tokenCache)
                .build();
            apiSimulator.stubUnauthorizedRequest(
                httpVerb,
                SOME_PATH,
                requiringHeader(AUTHORIZATION, "Bearer aToken")
            );

            assertThatThrownBy(() -> sendRequestUsing(httpHelper, httpVerb, baseUrl + SOME_PATH))
                .isInstanceOf(CommunicationException.class)
                .hasRootCauseInstanceOf(OAuth2TokenCache.OAuth2TokenRetrievalException.class);
        }

        @ParameterizedTest
        @EnumSource(MappingBuilderFromMethod.class)
        void attemptingToGetTokenFailsWithDataProviderExceptionWrapperAroundOAuthHttpExceptionAfterInvalidation(
            MappingBuilderFromMethod httpVerb
        ) {
            val deprecatedConfiguration = createConfig();
            val config = new UofConfigurationStub();
            config.setClientAuthentication(new UofPrivateKeyJwtAuthenticationStub());

            val tokenCache = OAuth2TokenCacheFixtures.failingWithOAuth2TokenRetrievalHttpExceptionAfterInvalidationOf(
                "aToken"
            );

            val httpHelper = createHttpHelperBuilder()
                .with(deprecatedConfiguration)
                .with(config)
                .with(tokenCache)
                .build();
            apiSimulator.stubUnauthorizedRequest(
                httpVerb,
                SOME_PATH,
                requiringHeader(AUTHORIZATION, "Bearer aToken")
            );

            assertThatThrownBy(() -> sendRequestUsing(httpHelper, httpVerb, baseUrl + SOME_PATH))
                .isInstanceOf(CommunicationException.class)
                .hasRootCauseInstanceOf(OAuth2TokenCache.OAuth2TokenRetrievalHttpException.class);
        }

        @ParameterizedTest
        @EnumSource(MappingBuilderFromMethod.class)
        void concurrentRequestsDoNotAccidentallyInvalidateNewlyRefreshedTokens(
            MappingBuilderFromMethod httpVerb
        ) throws Exception {
            val deprecatedConfiguration = createConfig();
            val config = new UofConfigurationStub();
            config.setClientAuthentication(new UofPrivateKeyJwtAuthenticationStub());

            val tokenCache = builder()
                .providingBearerToken("firstToken")
                .afterFirstInvalidationProviding("secondToken")
                .afterSecondInvalidationProviding("thirdToken")
                .build();
            val httpHelper = createHttpHelperBuilder()
                .with(deprecatedConfiguration)
                .with(config)
                .with(tokenCache)
                .build();

            apiSimulator.accumulates2UnauthorizedRequestsBeforeReleasingThem1SecondApartFromEachOther(
                httpVerb,
                SOME_PATH,
                requiringHeader(AUTHORIZATION, "Bearer firstToken")
            );

            val thread1 = new Thread(() -> sendRequestUsing(httpHelper, httpVerb, baseUrl + SOME_PATH));

            val thread2 = new Thread(() -> sendRequestUsing(httpHelper, httpVerb, baseUrl + SOME_PATH));

            thread1.start();
            thread2.start();

            thread1.join();
            thread2.join();

            tokenCache.verifyCalledWithSingleToken();
        }
    }

    @Nested
    class AuthorizationRelated {

        private static final String ACCESS_TOKEN = "access_token";

        @Test
        void clientAuthorizationIsPreferredAuthenticationMethodOverAccessToken()
            throws CommunicationException {
            val deprecatedConfiguration = createConfig();
            val config = new UofConfigurationStub();
            config.setClientAuthentication(new UofPrivateKeyJwtAuthenticationStub());
            config.setAccessToken(ACCESS_TOKEN);

            val tokenCache = providingBearerToken("some_jwt_token");

            val httpHelper = createHttpHelperBuilder()
                .with(deprecatedConfiguration)
                .with(config)
                .with(tokenCache)
                .build();

            apiSimulator.stubStatus200For(
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

            apiSimulator.stubStatus200For(
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

            apiSimulator.stubStatus200For(POST, SOME_PATH, requiringNoHeader(AUTHORIZATION));

            val responseData = httpHelper.post(baseUrl + SOME_PATH);

            assertThat(responseData.getStatusCode()).isEqualTo(200);
        }

        @ParameterizedTest
        @EnumSource(MappingBuilderFromMethod.class)
        void attemptingToGetTokenFailsWithDataProviderExceptionWrapperAroundOAuthException(
            MappingBuilderFromMethod httpVerb
        ) {
            val deprecatedConfiguration = createConfig();
            val config = new UofConfigurationStub();
            config.setClientAuthentication(new UofPrivateKeyJwtAuthenticationStub());

            val tokenCache = OAuth2TokenCacheFixtures.failingWithOAuth2TokenRetrievalException();

            val httpHelper = createHttpHelperBuilder()
                .with(deprecatedConfiguration)
                .with(config)
                .with(tokenCache)
                .build();

            assertThatThrownBy(() -> sendRequestUsing(httpHelper, httpVerb, baseUrl + SOME_PATH))
                .isInstanceOf(CommunicationException.class)
                .hasRootCauseInstanceOf(OAuth2TokenCache.OAuth2TokenRetrievalException.class);
        }

        @ParameterizedTest
        @EnumSource(MappingBuilderFromMethod.class)
        void attemptingToGetTokenFailsWithDataProviderExceptionWrapperAroundOAuthHttpException(
            MappingBuilderFromMethod httpVerb
        ) {
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

            assertThatThrownBy(() -> sendRequestUsing(httpHelper, httpVerb, baseUrl + SOME_PATH))
                .isInstanceOf(CommunicationException.class)
                .hasRootCauseInstanceOf(OAuth2TokenCache.OAuth2TokenRetrievalHttpException.class);
        }

        @Test
        void sendsAccessTokenHeader() throws Exception {
            val deprecatedConfiguration = createConfig();
            val config = new UofConfigurationStub();
            config.setAccessToken(ACCESS_TOKEN);

            val httpHelper = createHttpHelperBuilder().with(deprecatedConfiguration).with(config).build();

            apiSimulator.stubStatus200For(POST, SOME_PATH, requiringHeader(X_ACCESS_TOKEN, ACCESS_TOKEN));

            val responseData = httpHelper.post(baseUrl + SOME_PATH);

            assertThat(responseData.getStatusCode()).isEqualTo(200);
        }

        @Test
        void doesNotSendAccessTokenHeaderWhenAccessTokenIsNotConfigured() throws CommunicationException {
            val deprecatedConfiguration = createConfig();
            val config = new UofConfigurationStub();

            val httpHelper = createHttpHelperBuilder().with(deprecatedConfiguration).with(config).build();

            apiSimulator.stubStatus200For(POST, SOME_PATH, requiringNoHeader(X_ACCESS_TOKEN));

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

            apiSimulator.stubStatus200For(
                POST,
                SOME_PATH,
                requiringHeaderWithAnyUuidValue(TRACE_HEADER_NAME)
            );

            val responseData = httpHelper.post(baseUrl + SOME_PATH);

            assertThat(responseData.getStatusCode()).isEqualTo(200);
            logsMock.loggedLineContains(getTheOnlySubmittedTraceIdForPath(SOME_PATH));
        }

        @Test
        void sendNewTraceIdHeaderPerRequest() throws CommunicationException {
            val deprecatedConfiguration = createConfig();
            val config = new UofConfigurationStub();

            val httpHelper = createHttpHelperBuilder().with(deprecatedConfiguration).with(config).build();

            apiSimulator.stubStatus200For(
                POST,
                SOME_PATH,
                requiringHeaderWithAnyUuidValue(TRACE_HEADER_NAME)
            );
            val responseData1 = httpHelper.post(baseUrl + SOME_PATH);
            apiSimulator.stubStatus200For(
                POST,
                SOME_PATH,
                requiringHeaderWithAnyUuidValue(TRACE_HEADER_NAME)
            );
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

            apiSimulator.stubStatus200For(PUT, SOME_PATH, requiringHeaderWithAnyUuidValue(TRACE_HEADER_NAME));

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

            apiSimulator.stubStatus200For(
                DELETE,
                SOME_PATH,
                requiringHeaderWithAnyUuidValue(TRACE_HEADER_NAME)
            );

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

            apiSimulator.stubStatus200For(
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

            apiSimulator.stubStatus200For(
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

            apiSimulator.stubStatus200For(
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
        return createConfig(30);
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

    @SneakyThrows
    private static ResponseData sendRequestUsing(
        HttpHelper httpHelper,
        MappingBuilderFromMethod httpVerb,
        String url
    ) {
        switch (httpVerb) {
            case POST:
                return httpHelper.post(url);
            case PUT:
                return httpHelper.put(url);
            case DELETE:
                return httpHelper.delete(url);
            default:
                throw new IllegalArgumentException("Unsupported HTTP verb: " + httpVerb);
        }
    }
}
