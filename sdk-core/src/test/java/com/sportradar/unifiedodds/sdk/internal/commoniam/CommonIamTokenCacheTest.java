/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */

package com.sportradar.unifiedodds.sdk.internal.commoniam;

import static com.github.tomakehurst.wiremock.client.WireMock.postRequestedFor;
import static com.github.tomakehurst.wiremock.client.WireMock.urlEqualTo;
import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.wireMockConfig;
import static com.sportradar.unifiedodds.sdk.conn.ApiSimulator.HeaderEquality.requiringHeader;
import static com.sportradar.unifiedodds.sdk.conn.CommonIamTokens.*;
import static com.sportradar.unifiedodds.sdk.internal.cfg.TestConfigHelper.getHostFrom;
import static com.sportradar.unifiedodds.sdk.internal.cfg.TestConfigHelper.getPortFrom;
import static com.sportradar.unifiedodds.sdk.internal.commoniam.CommonIamTokenCaches.createCommonIamTokenCache;
import static com.sportradar.unifiedodds.sdk.internal.commoniam.JwtTokenAssertions.assertThatJwt;
import static com.sportradar.unifiedodds.sdk.internal.commoniam.JwtTokenAssertions.assertThatJwtInAssertion;
import static com.sportradar.unifiedodds.sdk.internal.commoniam.Jwts.parseJwtSignedDataComponents;
import static com.sportradar.unifiedodds.sdk.internal.commoniam.KeyPairs.verifySignatureWithPublicKey;
import static com.sportradar.unifiedodds.sdk.internal.commoniam.ResourceAudience.UF_RABBIT_MQ;
import static com.sportradar.unifiedodds.sdk.internal.commoniam.ResourceAudience.UF_REST_API;
import static com.sportradar.unifiedodds.sdk.internal.commoniam.UrlEncodedParams.extractsJwtFrom;
import static com.sportradar.utils.time.TimeInterval.seconds;
import static java.time.Duration.ofSeconds;
import static java.util.concurrent.Executors.newScheduledThreadPool;
import static org.assertj.core.api.Assertions.*;
import static org.awaitility.Awaitility.await;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import ch.qos.logback.classic.Level;
import com.github.tomakehurst.wiremock.common.ConsoleNotifier;
import com.github.tomakehurst.wiremock.junit5.WireMockExtension;
import com.sportradar.unifiedodds.sdk.LoggerDefinitions;
import com.sportradar.unifiedodds.sdk.cfg.UofConfigurationStub;
import com.sportradar.unifiedodds.sdk.cfg.UofPrivateKeyJwtAuthenticationStub;
import com.sportradar.unifiedodds.sdk.conn.ApiSimulator;
import com.sportradar.unifiedodds.sdk.conn.CommonIamSimulator;
import com.sportradar.unifiedodds.sdk.internal.commoniam.OAuth2TokenCache.OAuth2TokenRetrievalException;
import com.sportradar.unifiedodds.sdk.internal.commoniam.OAuth2TokenCache.OAuth2TokenRetrievalHttpException;
import com.sportradar.unifiedodds.sdk.internal.impl.SdkInternalConfiguration;
import com.sportradar.unifiedodds.sdk.internal.impl.rabbitconnection.LogsMock;
import com.sportradar.unifiedodds.sdk.testutil.generic.concurrent.AtomicActionPerformer;
import com.sportradar.unifiedodds.sdk.testutil.rabbit.integration.BaseUrl;
import com.sportradar.utils.time.TimeInterval;
import com.sportradar.utils.time.TimeUtilsStub;
import java.security.KeyPair;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.time.Duration;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Supplier;
import lombok.val;
import org.awaitility.Awaitility;
import org.awaitility.core.ConditionFactory;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.RegisterExtension;

@SuppressWarnings({ "ClassFanOutComplexity", "MagicNumber", "MultipleStringLiterals" })
class CommonIamTokenCacheTest {

    private static final Instant FIXED_TIME = Instant.ofEpochMilli(1664402400000L);
    private static final String CLIENT_ID = "test_client_123";
    private static final int ONE_MINUTE_IN_SECONDS = 60;
    private static final String KEY_ID = "test_key_456";
    private static final TimeInterval FIVE_SECONDS = TimeInterval.seconds(5);
    private static final TimeInterval ONE_SECOND = TimeInterval.seconds(1);

    @RegisterExtension
    private static WireMockExtension wireMock = WireMockExtension
        .newInstance()
        .options(wireMockConfig().dynamicPort().notifier(new ConsoleNotifier(true)))
        .build();

    private BaseUrl apiBaseUrl;
    private CommonIamSimulator commonIamSimulator;
    private final TimeUtilsStub time = TimeUtilsStub
        .threadSafe(new AtomicActionPerformer())
        .withCurrentTime(Instant.MAX);
    private LogsMock logsMock;

    @BeforeEach
    void setup() {
        apiBaseUrl = BaseUrl.of("localhost", wireMock.getPort());
        commonIamSimulator = new CommonIamSimulator(wireMock.getRuntimeInfo().getWireMock());
        logsMock = LogsMock.createCapturingFor(LoggerDefinitions.UfSdkRestTrafficLog.class);
        logsMock.setLevel(Level.INFO);
    }

    @Test
    void returnsValidTokenWhenApiCallSucceeds() throws Exception {
        commonIamSimulator.stubTokenEndpoint(validCommonIamToken());
        CommonIamTokenCache tokenCache = createCommonIamTokenCache()
            .with(uofConfigWith1sFastFailingTimeoutAndOAuthOn(apiBaseUrl.get()))
            .with(internalConfigWith1sTimeoutAndMax1Connection())
            .withResourceAudience(UF_REST_API)
            .build();

        OAuth2Token token = tokenCache.getToken();

        assertThat(token.getTokenType()).isEqualTo("Bearer");
        assertThat(token.getAccessToken()).isEqualTo(validCommonIamToken().getAccessToken());
    }

    @Test
    void cachesTokenOnFirstOfMultipleSubsequentlyIssuedRequestsToAvoidRepeatedApiCalls() throws Exception {
        commonIamSimulator.stubTokenEndpoint(expiringInTenSecondsCommonIamToken());

        time.travelTo(FIXED_TIME);

        CommonIamTokenCache tokenCache = createCommonIamTokenCache()
            .with(uofConfigWith1sFastFailingTimeoutAndOAuthOn(apiBaseUrl.get()))
            .with(internalConfigWith1sTimeoutAndMax1Connection())
            .with(time)
            .withResourceAudience(UF_REST_API)
            .build();

        OAuth2Token firstCall = tokenCache.getToken();
        OAuth2Token secondCall = tokenCache.getToken();

        assertThat(firstCall.getAccessToken())
            .isEqualTo(expiringInTenSecondsCommonIamToken().getAccessToken());
        assertThat(secondCall.getAccessToken())
            .isEqualTo(expiringInTenSecondsCommonIamToken().getAccessToken());
        assertThat(firstCall).isSameAs(secondCall);

        commonIamSimulator.verifyTokenEndpointCalledOnce();
    }

    @Test
    void cachesTokenForSpacedOutTokenRequestsToAvoidRepeatedApiCalls() throws Exception {
        commonIamSimulator.stubTokenEndpoint(expiringInTenSecondsCommonIamToken());

        time.travelTo(FIXED_TIME);

        CommonIamTokenCache tokenCache = createCommonIamTokenCache()
            .with(uofConfigWith1sFastFailingTimeoutAndOAuthOn(apiBaseUrl.get()))
            .with(internalConfigWith1sTimeoutAndMax1Connection())
            .with(time)
            .withResourceAudience(UF_REST_API)
            .build();

        OAuth2Token firstCall = tokenCache.getToken();
        int lessThan90PercentOfExpiration = 8;
        time.travelTo(FIXED_TIME.plusSeconds(lessThan90PercentOfExpiration));
        OAuth2Token secondCall = tokenCache.getToken();

        assertThat(firstCall.getAccessToken())
            .isEqualTo(expiringInTenSecondsCommonIamToken().getAccessToken());
        assertThat(secondCall.getAccessToken())
            .isEqualTo(expiringInTenSecondsCommonIamToken().getAccessToken());
        assertThat(firstCall).isSameAs(secondCall);
        wireMock.verify(1, postRequestedFor(urlEqualTo("/oauth/token")));
    }

    @Nested
    class TokenRefresh {

        private ScheduledExecutorService executor;

        @BeforeEach
        void setup() {
            executor = newScheduledThreadPool(40);
        }

        @AfterEach
        void cleanup() {
            executor.shutdownNow();
        }

        @Test
        void refreshesTokenWhichAreExpiredAtTheTimeOfBeingIssued() throws Exception {
            commonIamSimulator.stubTokenEndpoint(immediatelyExpiredCommonIamToken());

            time.travelTo(FIXED_TIME);

            CommonIamTokenCache tokenCache = createCommonIamTokenCache()
                .with(uofConfigWith1sFastFailingTimeoutAndOAuthOn(apiBaseUrl.get()))
                .with(internalConfigWith1sTimeoutAndMax1Connection())
                .with(time)
                .withResourceAudience(UF_REST_API)
                .build();

            OAuth2Token initialToken = tokenCache.getToken();

            commonIamSimulator.stubTokenEndpoint(refreshedCommonIamToken());

            OAuth2Token refreshedToken = tokenCache.getToken();

            assertThat(initialToken.getAccessToken())
                .isEqualTo(immediatelyExpiredCommonIamToken().getAccessToken());
            assertThat(refreshedToken.getAccessToken()).isEqualTo(refreshedCommonIamToken().getAccessToken());

            assertThatSubmitted2TokenIdsAreUnique(extractsJwtFrom(commonIamSimulator.getAllRequestBodies()));
        }

        @Test
        void refreshesTokenAfterTheirExpiration() throws Exception {
            commonIamSimulator.stubTokenEndpoint(expiringInTenSecondsCommonIamToken());

            time.travelTo(FIXED_TIME);

            CommonIamTokenCache tokenCache = createCommonIamTokenCache()
                .with(uofConfigWith1sFastFailingTimeoutAndOAuthOn(apiBaseUrl.get()))
                .with(internalConfigWith1sTimeoutAndMax1Connection())
                .with(time)
                .withResourceAudience(UF_REST_API)
                .build();

            OAuth2Token initialToken = tokenCache.getToken();

            time.travelTo(FIXED_TIME.plusSeconds(11));
            commonIamSimulator.stubTokenEndpoint(refreshedCommonIamToken());

            OAuth2Token refreshedToken = tokenCache.getToken();

            assertThat(initialToken.getAccessToken())
                .isEqualTo(expiringInTenSecondsCommonIamToken().getAccessToken());
            assertThat(refreshedToken.getAccessToken()).isEqualTo(refreshedCommonIamToken().getAccessToken());

            assertThatSubmitted2TokenIdsAreUnique(extractsJwtFrom(commonIamSimulator.getAllRequestBodies()));
        }

        @Test
        void refreshesTokenProactivelyAfter90PercentOfExpirationTimeHasPassed() throws Exception {
            commonIamSimulator.stubTokenEndpoint(expiringInTenSecondsCommonIamToken());

            time.travelTo(FIXED_TIME);

            CommonIamTokenCache tokenCache = createCommonIamTokenCache()
                .with(uofConfigWith1sFastFailingTimeoutAndOAuthOn(apiBaseUrl.get()))
                .with(internalConfigWith1sTimeoutAndMax1Connection())
                .with(time)
                .withResourceAudience(UF_REST_API)
                .build();

            OAuth2Token initialToken = tokenCache.getToken();

            time.travelTo(FIXED_TIME.plusSeconds(9));
            commonIamSimulator.stubTokenEndpoint(refreshedCommonIamToken());

            OAuth2Token refreshedToken = tokenCache.getToken();

            assertThat(initialToken.getAccessToken())
                .isEqualTo(expiringInTenSecondsCommonIamToken().getAccessToken());
            assertThat(refreshedToken.getAccessToken()).isEqualTo(refreshedCommonIamToken().getAccessToken());
        }

        @Test
        void returnsCachedTokenIfProactiveTokenRefreshFailsRepeatedly() throws Exception {
            commonIamSimulator.stubTokenEndpoint(expiringInTenSecondsCommonIamToken());

            time.travelTo(FIXED_TIME);

            CommonIamTokenCache tokenCache = createCommonIamTokenCache()
                .with(uofConfigWith1sFastFailingTimeoutAndOAuthOn(apiBaseUrl.get()))
                .with(internalConfigWith1sTimeoutAndMax1Connection())
                .with(time)
                .withResourceAudience(UF_REST_API)
                .build();

            OAuth2Token initialToken = tokenCache.getToken();

            time.travelTo(FIXED_TIME.plusSeconds(9));
            commonIamSimulator.stubTokenEndpointWithInternalServerErrorResponse();

            OAuth2Token refreshedToken1 = tokenCache.getToken();
            OAuth2Token refreshedToken2 = tokenCache.getToken();

            assertThat(initialToken).isEqualTo(refreshedToken1);
            assertThat(refreshedToken1).isEqualTo(refreshedToken2);
        }

        @Test
        void preventsHammeringCommonIamDuringProactiveTokenRefreshPeriodWhileReturningCachedToken()
            throws Exception {
            commonIamSimulator.stubTokenEndpoint(expiringInTenSecondsCommonIamToken());

            time.travelTo(FIXED_TIME);

            CommonIamTokenCache tokenCache = createCommonIamTokenCache()
                .with(uofConfigWith1sFastFailingTimeoutAndOAuthOn(apiBaseUrl.get()))
                .with(internalConfigWith1sTimeoutAndMax1Connection())
                .with(time)
                .withResourceAudience(UF_REST_API)
                .build();

            tokenCache.getToken();

            int ninetyPercentOfExpiryPassed = 9;
            time.travelTo(FIXED_TIME.plusSeconds(ninetyPercentOfExpiryPassed));
            commonIamSimulator.stubTokenEndpointWithInternalServerErrorResponse();

            tokenCache.getToken();
            tokenCache.getToken();

            commonIamSimulator.verifyTokenEndpointCalledTimes(2);
        }

        @Test
        void tokenRefreshIsLockingToAvoidOverwhelmingApi() throws Exception {
            time.travelTo(FIXED_TIME);

            commonIamSimulator.stubTokenEndpoint(expiringInTenSecondsCommonIamToken());

            val tokenCache = createCommonIamTokenCache()
                .with(uofConfigWith1sFastFailingTimeoutAndOAuthOn(apiBaseUrl.get()))
                .with(internalConfigWith1sTimeoutAndMax1Connection())
                .with(time)
                .withResourceAudience(UF_REST_API)
                .build();

            Supplier<OAuth2Token> tokenSupplier = tokenCache::getToken;
            scheduleConstantlyRunningTasks(120, tokenSupplier);
            int timeTravelsCount = scheduleConstantlyRunningTaskSchedulingTimeTravels(tokenSupplier).get();
            int initialGetTokenBeforeFirstTimeTravel = 1;
            int expected = timeTravelsCount + initialGetTokenBeforeFirstTimeTravel;
            commonIamSimulator.verifyTokenEndpointCalledTimes(expected);
        }

        private void scheduleConstantlyRunningTasks(int concurrency, Supplier<OAuth2Token> tokenSupplier) {
            for (int i = 0; i < concurrency - 1; i++) {
                executor.scheduleAtFixedRate(tokenSupplier::get, 0, 1, TimeUnit.MILLISECONDS);
            }
        }

        private Future<Integer> scheduleConstantlyRunningTaskSchedulingTimeTravels(
            Supplier<OAuth2Token> tokenSupplier
        ) throws InterruptedException {
            long timeTravelEveryCadence = 50;
            long halfCadence = Math.round(timeTravelEveryCadence * 0.5);
            AtomicLong counter = new AtomicLong(halfCadence);
            CountDownLatch doneTravelingBarrier = new CountDownLatch(1);

            int numberOfTimeTravels = 9;
            executor.scheduleAtFixedRate(
                () -> {
                    if (shouldTimeTravel(counter, timeTravelEveryCadence)) {
                        time.travelTo(FIXED_TIME.plusSeconds(11 * (counter.get() / timeTravelEveryCadence)));
                    }
                    tokenSupplier.get();
                    long i = timeTravelEveryCadence * numberOfTimeTravels + halfCadence;
                    if (isDoneTraveling(counter, i)) {
                        doneTravelingBarrier.countDown();
                    }
                },
                0,
                1,
                TimeUnit.MILLISECONDS
            );
            doneTravelingBarrier.await();
            return CompletableFuture.completedFuture(numberOfTimeTravels);
        }

        private boolean isDoneTraveling(AtomicLong counter, long i) {
            return counter.get() == i;
        }

        private boolean shouldTimeTravel(AtomicLong counter, long timeTravelEveryNumberOfIterations) {
            return counter.incrementAndGet() % timeTravelEveryNumberOfIterations == 0;
        }

        private void assertThatSubmitted2TokenIdsAreUnique(List<String> submittedJwts) {
            assertThat(submittedJwts).hasSize(2);
            assertThatJwt(submittedJwts.get(0)).doesNotHaveSameIdAs(submittedJwts.get(1));
        }
    }

    @Nested
    class TokenInvalidation {

        @Test
        void invalidatesToken() throws Exception {
            commonIamSimulator.stubTokenEndpoint(validCommonIamToken());

            time.travelTo(FIXED_TIME);

            val tokenCache = createCommonIamTokenCache()
                .with(uofConfigWith1sFastFailingTimeoutAndOAuthOn(apiBaseUrl.get()))
                .with(internalConfigWith1sTimeoutAndMax1Connection())
                .with(time)
                .withResourceAudience(UF_REST_API)
                .build();

            val initialToken = tokenCache.getToken();

            commonIamSimulator.stubTokenEndpoint(refreshedCommonIamToken());
            tokenCache.invalidateToken(initialToken);

            val freshToken = tokenCache.getToken();

            assertThat(freshToken.getAccessToken()).isEqualTo(refreshedCommonIamToken().getAccessToken());
            assertThat(freshToken.getAccessToken()).isNotEqualTo(initialToken.getAccessToken());
        }

        @Test
        void doesNotInvalidateIfTokenTypeDoesNotMatch() throws Exception {
            commonIamSimulator.stubTokenEndpoint(validCommonIamToken());

            time.travelTo(FIXED_TIME);

            val tokenCache = createCommonIamTokenCache()
                .with(uofConfigWith1sFastFailingTimeoutAndOAuthOn(apiBaseUrl.get()))
                .with(internalConfigWith1sTimeoutAndMax1Connection())
                .with(time)
                .withResourceAudience(UF_REST_API)
                .build();

            val initialToken = tokenCache.getToken();

            tokenCache.invalidateToken(withNonBearerTokenType(initialToken));

            val tokenAfter = tokenCache.getToken();

            commonIamSimulator.verifyTokenEndpointCalledOnce();
            assertThat(tokenAfter.getAccessToken()).isEqualTo(validCommonIamToken().getAccessToken());
        }

        @Test
        void doesNotInvalidateIfCurrentAccessTokenDoesNotMatchTheOnePassedIn() throws Exception {
            commonIamSimulator.stubTokenEndpoint(validCommonIamToken());

            time.travelTo(FIXED_TIME);

            val tokenCache = createCommonIamTokenCache()
                .with(uofConfigWith1sFastFailingTimeoutAndOAuthOn(apiBaseUrl.get()))
                .with(internalConfigWith1sTimeoutAndMax1Connection())
                .with(time)
                .withResourceAudience(UF_REST_API)
                .build();

            tokenCache.getToken();

            tokenCache.invalidateToken(anyCommonIamToken());

            val tokenAfter = tokenCache.getToken();

            commonIamSimulator.verifyTokenEndpointCalledOnce();
            assertThat(tokenAfter.getAccessToken()).isEqualTo(validCommonIamToken().getAccessToken());
        }

        @Test
        void invalidationOnEmptyCacheHasNoEffect() throws Exception {
            commonIamSimulator.stubTokenEndpoint(validCommonIamToken());

            time.travelTo(FIXED_TIME);

            val tokenCache = createCommonIamTokenCache()
                .with(uofConfigWith1sFastFailingTimeoutAndOAuthOn(apiBaseUrl.get()))
                .with(internalConfigWith1sTimeoutAndMax1Connection())
                .with(time)
                .withResourceAudience(UF_REST_API)
                .build();

            tokenCache.invalidateToken(anyCommonIamToken());

            val tokenAfter = tokenCache.getToken();

            commonIamSimulator.verifyTokenEndpointCalledOnce();
            assertThat(tokenAfter.getAccessToken()).isEqualTo(validCommonIamToken().getAccessToken());
        }

        private OAuth2Token withNonBearerTokenType(OAuth2Token initialToken) {
            return new OAuth2Token("NonBearer", initialToken.getAccessToken());
        }

        private OAuth2Token anyCommonIamToken() {
            return new OAuth2Token("Bearer", "just.any.value");
        }
    }

    @Test
    void returnsTokenWithCustomTokenTypeWhenApiCallSucceeds() throws Exception {
        val customToken = customCommonIamToken("CustomType");
        commonIamSimulator.stubTokenEndpoint(customToken);
        CommonIamTokenCache tokenCache = createCommonIamTokenCache()
            .with(uofConfigWith1sFastFailingTimeoutAndOAuthOn(apiBaseUrl.get()))
            .with(internalConfigWith1sTimeoutAndMax1Connection())
            .withResourceAudience(UF_REST_API)
            .build();

        OAuth2Token token = tokenCache.getToken();

        assertThat(token.getTokenType()).isEqualTo("CustomType");
        assertThat(token.getAccessToken()).isEqualTo(customToken.getAccessToken());
    }

    @Test
    void jwtAssertionTokenSignedWithPrivateKeyIsVerifiable() throws Exception {
        KeyPair keyPair = KeyPairs.rsaKeyPair2048keySize();

        commonIamSimulator.stubTokenEndpoint(validCommonIamToken());

        UofConfigurationStub config = uofConfigWith1sFastFailingTimeoutAndOAuthOn(apiBaseUrl.get());
        config.getClientAuthenticationStub().setPrivateKey(keyPair.getPrivate());

        CommonIamTokenCache tokenCache = createCommonIamTokenCache()
            .with(config)
            .with(internalConfigWith1sTimeoutAndMax1Connection())
            .withResourceAudience(UF_REST_API)
            .build();

        tokenCache.getToken();

        String jwt = UrlEncodedParams.extractJwtFrom(commonIamSimulator.getLastRequestBody());
        assertThatJwt(jwt).hasAlgorithm("RS256").hasTokenType("JWT");
        verifySignatureWithPublicKey(parseJwtSignedDataComponents(jwt), keyPair.getPublic());
    }

    @Test
    void returnsTokenUsingCustomTenantWhenTenantIsConfigured() throws Exception {
        val customTenant = "custom-tenant";
        commonIamSimulator.stubTokenEndpointWhenClientAssertionAudienceIs(
            customTenant,
            validCommonIamToken()
        );
        CommonIamTokenCache tokenCache = createCommonIamTokenCache()
            .with(uofConfigWith1sFastFailingTimeoutAndOAuthOnWithTenant(customTenant, apiBaseUrl.get()))
            .with(internalConfigWith1sTimeoutAndMax1Connection())
            .withResourceAudience(UF_REST_API)
            .build();

        OAuth2Token token = tokenCache.getToken();

        assertThat(token.getAccessToken()).isEqualTo(validCommonIamToken().getAccessToken());
    }

    @Test
    void returnsTokenUsingAuthUrlAsTenantWhenTenantIsNotConfigured() throws Exception {
        val authServerUrl = "http://" + apiBaseUrl.get() + "/";
        commonIamSimulator.stubTokenEndpointWhenClientAssertionAudienceIs(
            authServerUrl,
            validCommonIamToken()
        );
        CommonIamTokenCache tokenCache = createCommonIamTokenCache()
            .with(uofConfigWith1sFastFailingTimeoutAndOAuthOn(apiBaseUrl.get()))
            .with(internalConfigWith1sTimeoutAndMax1Connection())
            .withResourceAudience(UF_REST_API)
            .build();

        OAuth2Token token = tokenCache.getToken();

        assertThat(token.getAccessToken()).isEqualTo(validCommonIamToken().getAccessToken());
    }

    @Test
    void requestsTokenForApiViaClientCredentialsWithJwtBearerClientAssertion() throws Exception {
        commonIamSimulator.stubTokenEndpoint(validCommonIamToken());

        KeyPair keyPair = KeyPairs.rsaKeyPair2048keySize();
        PrivateKey privateKey = keyPair.getPrivate();

        UofConfigurationStub config = uofConfigWith1sFastFailingTimeoutAndOAuthOn(apiBaseUrl.get());
        config.getClientAuthenticationStub().setPrivateKey(privateKey);

        CommonIamTokenCache tokenCache = createCommonIamTokenCache()
            .with(config)
            .with(internalConfigWith1sTimeoutAndMax1Connection())
            .withResourceAudience(UF_REST_API)
            .build();

        tokenCache.getToken();

        String requestBody = commonIamSimulator.getLastRequestBody();
        assertThat(requestBody)
            .matches(
                ClientCredentialsJwtAssertionRequestBodyPatterns::matchesRequestForRestApi,
                "Request pattern for Rest Api"
            );
    }

    @Test
    void requestsTokenForRabbitViaClientCredentialsWithJwtBearerClientAssertion() throws Exception {
        commonIamSimulator.stubTokenEndpoint(validCommonIamToken());

        KeyPair keyPair = KeyPairs.rsaKeyPair2048keySize();
        PrivateKey privateKey = keyPair.getPrivate();

        UofConfigurationStub config = uofConfigWith1sFastFailingTimeoutAndOAuthOn(apiBaseUrl.get());
        config.getClientAuthenticationStub().setPrivateKey(privateKey);

        CommonIamTokenCache tokenCache = createCommonIamTokenCache()
            .with(config)
            .withResourceAudience(UF_RABBIT_MQ)
            .with(internalConfigWith1sTimeoutAndMax1Connection())
            .build();

        tokenCache.getToken();

        String requestBody = commonIamSimulator.getLastRequestBody();
        assertThat(requestBody)
            .matches(
                ClientCredentialsJwtAssertionRequestBodyPatterns::matchesRequestForRabbit,
                "Request pattern for RabbitMQ"
            );
    }

    @Test
    void sendsFormUrlEncodedContentTypeAndBodyFormat() throws Exception {
        commonIamSimulator.stubTokenEndpoint(
            validCommonIamToken(),
            requiringHeader("Content-Type", "application/x-www-form-urlencoded")
        );

        CommonIamTokenCache tokenCache = createCommonIamTokenCache()
            .with(uofConfigWith1sFastFailingTimeoutAndOAuthOn(apiBaseUrl.get()))
            .with(internalConfigWith1sTimeoutAndMax1Connection())
            .withResourceAudience(UF_REST_API)
            .build();

        tokenCache.getToken();
    }

    @Test
    void properlyUrlEncodesJwtAssertionInRequestBody() throws Exception {
        commonIamSimulator.stubTokenEndpoint(validCommonIamToken());

        CommonIamTokenCache tokenCache = createCommonIamTokenCache()
            .with(uofConfigWith1sFastFailingTimeoutAndOAuthOn(apiBaseUrl.get()))
            .with(internalConfigWith1sTimeoutAndMax1Connection())
            .withResourceAudience(UF_REST_API)
            .build();

        tokenCache.getToken();

        String requestBody = commonIamSimulator.getLastRequestBody();
        assertThat(requestBody)
            .matches(
                ClientCredentialsJwtAssertionRequestBodyPatterns::matchesRequestForRestApi,
                "Request pattern for Rest Api"
            );
    }

    @Test
    void cacheRespectsConfiguredTimeout() throws NoSuchAlgorithmException {
        int serverDelayMoreThanOneSecond = 2;
        commonIamSimulator.stubTokenEndpoint(
            validCommonIamToken(),
            ApiSimulator.ApiStubDelay.toBeDelayedBy(serverDelayMoreThanOneSecond, ChronoUnit.SECONDS)
        );

        CommonIamTokenCache tokenCache = createCommonIamTokenCache()
            .with(uofConfigWith1sFastFailingTimeoutAndOAuthOn(apiBaseUrl.get()))
            .with(internalConfigWith1sTimeoutAndMax1Connection())
            .build();

        assertThatThrownBy(tokenCache::getToken)
            .isInstanceOf(OAuth2TokenRetrievalException.class)
            .hasMessageContaining("Failed to retrieve OAuth token");
    }

    @Test
    void throwsExceptionWhenApiCallFails() throws NoSuchAlgorithmException {
        commonIamSimulator.stubTokenEndpointWithInternalServerErrorResponse();
        CommonIamTokenCache tokenCache = createCommonIamTokenCache()
            .with(uofConfigWith1sFastFailingTimeoutAndOAuthOn(apiBaseUrl.get()))
            .with(internalConfigWith1sTimeoutAndMax1Connection())
            .withResourceAudience(UF_REST_API)
            .build();

        assertThatThrownBy(tokenCache::getToken)
            .isInstanceOf(OAuth2TokenRetrievalException.class)
            .hasMessageContaining("Failed to retrieve OAuth token")
            .hasRootCauseInstanceOf(OAuth2TokenRetrievalHttpException.class)
            .hasRootCauseMessage("HTTP: 500 /oauth/token");
    }

    @Test
    void throwsExceptionWhenApiCallReturnsInvalidJson() throws NoSuchAlgorithmException {
        commonIamSimulator.stubTokenEndpointWithInvalidJson();
        CommonIamTokenCache tokenCache = createCommonIamTokenCache()
            .with(uofConfigWith1sFastFailingTimeoutAndOAuthOn(apiBaseUrl.get()))
            .with(internalConfigWith1sTimeoutAndMax1Connection())
            .build();

        assertThatThrownBy(tokenCache::getToken)
            .isInstanceOf(OAuth2TokenRetrievalException.class)
            .hasMessageContaining("Failed to retrieve OAuth token");
    }

    @Test
    void throwsExceptionWhenApiCallReturnsNullAccessToken() throws NoSuchAlgorithmException {
        commonIamSimulator.stubTokenEndpoint(commonIamTokenWithNullAccessToken());
        CommonIamTokenCache tokenCache = createCommonIamTokenCache()
            .with(uofConfigWith1sFastFailingTimeoutAndOAuthOn(apiBaseUrl.get()))
            .with(internalConfigWith1sTimeoutAndMax1Connection())
            .build();

        assertThatThrownBy(tokenCache::getToken)
            .isInstanceOf(OAuth2TokenRetrievalException.class)
            .hasMessageContaining("Failed to retrieve OAuth token");
    }

    @Test
    void throwsExceptionWhenApiCallDoesNotReturnAccessToken() throws NoSuchAlgorithmException {
        commonIamSimulator.stubTokenEndpointWithoutAccessToken();
        CommonIamTokenCache tokenCache = createCommonIamTokenCache()
            .with(uofConfigWith1sFastFailingTimeoutAndOAuthOn(apiBaseUrl.get()))
            .with(internalConfigWith1sTimeoutAndMax1Connection())
            .build();

        assertThatThrownBy(tokenCache::getToken)
            .isInstanceOf(OAuth2TokenRetrievalException.class)
            .hasMessageContaining("Failed to retrieve OAuth token");
    }

    @Test
    void throwsExceptionWhenApiCallReturnsNullExpiresIn() throws NoSuchAlgorithmException {
        commonIamSimulator.stubTokenEndpoint(commonIamTokenWithNullExpiresIn());
        CommonIamTokenCache tokenCache = createCommonIamTokenCache()
            .with(uofConfigWith1sFastFailingTimeoutAndOAuthOn(apiBaseUrl.get()))
            .with(internalConfigWith1sTimeoutAndMax1Connection())
            .build();

        assertThatThrownBy(tokenCache::getToken)
            .isInstanceOf(OAuth2TokenRetrievalException.class)
            .hasMessageContaining("Failed to retrieve OAuth token");
    }

    @Test
    void throwsExceptionWhenApiCallDoesNotReturnExpiryInformationAtAll() throws NoSuchAlgorithmException {
        commonIamSimulator.stubTokenEndpointWithoutExpiresIn();
        CommonIamTokenCache tokenCache = createCommonIamTokenCache()
            .with(uofConfigWith1sFastFailingTimeoutAndOAuthOn(apiBaseUrl.get()))
            .with(internalConfigWith1sTimeoutAndMax1Connection())
            .build();

        assertThatThrownBy(tokenCache::getToken)
            .isInstanceOf(OAuth2TokenRetrievalException.class)
            .hasMessageContaining("Failed to retrieve OAuth token");
    }

    @Test
    void throwsExceptionWhenApiCallReturnsNullTokenType() throws NoSuchAlgorithmException {
        commonIamSimulator.stubTokenEndpoint(commonIamTokenWithNullTokenType());
        CommonIamTokenCache tokenCache = createCommonIamTokenCache()
            .with(uofConfigWith1sFastFailingTimeoutAndOAuthOn(apiBaseUrl.get()))
            .with(internalConfigWith1sTimeoutAndMax1Connection())
            .build();

        assertThatThrownBy(tokenCache::getToken)
            .isInstanceOf(OAuth2TokenRetrievalException.class)
            .hasMessageContaining("Failed to retrieve OAuth token");
    }

    @Test
    void throwsExceptionWhenApiCallReturnsWithoutTokenType() throws NoSuchAlgorithmException {
        commonIamSimulator.stubTokenEndpointWithoutTokenType();
        CommonIamTokenCache tokenCache = createCommonIamTokenCache()
            .with(uofConfigWith1sFastFailingTimeoutAndOAuthOn(apiBaseUrl.get()))
            .with(internalConfigWith1sTimeoutAndMax1Connection())
            .build();

        assertThatThrownBy(tokenCache::getToken)
            .isInstanceOf(OAuth2TokenRetrievalException.class)
            .hasMessageContaining("Failed to retrieve OAuth token");
    }

    @Test
    void jwtSubmittedContainsClientIdAndSigningKeyId() throws Exception {
        commonIamSimulator.stubTokenEndpoint(validCommonIamToken());

        UofConfigurationStub config = uofConfigWith1sFastFailingTimeoutAndOAuthOn(apiBaseUrl.get());
        config.getClientAuthenticationStub().setClientId(CLIENT_ID);
        config.getClientAuthenticationStub().setSigningKeyId(KEY_ID);

        CommonIamTokenCache tokenCache = createCommonIamTokenCache()
            .with(config)
            .with(internalConfigWith1sTimeoutAndMax1Connection())
            .withResourceAudience(UF_REST_API)
            .build();

        tokenCache.getToken();

        assertThatJwtInAssertion(commonIamSimulator.getLastRequestBody())
            .hasKeyId(KEY_ID)
            .hasIssuer(CLIENT_ID)
            .hasSubject(CLIENT_ID);
    }

    @Test
    void jwtSubmittedContainsAudienceEqualToBaseUrlOfAuthServer() throws Exception {
        commonIamSimulator.stubTokenEndpoint(validCommonIamToken());
        UofConfigurationStub config = uofConfigWith1sFastFailingTimeoutAndOAuthOn(apiBaseUrl.get());

        CommonIamTokenCache tokenCache = createCommonIamTokenCache()
            .with(config)
            .with(internalConfigWith1sTimeoutAndMax1Connection())
            .withResourceAudience(UF_REST_API)
            .build();

        tokenCache.getToken();

        assertThatJwtInAssertion(commonIamSimulator.getLastRequestBody())
            .hasAudience("http://localhost:" + apiBaseUrl.getPort() + "/");
    }

    @Test
    void jwtSubmittedContainsCorrectExpirationAndIssueTime() throws Exception {
        commonIamSimulator.stubTokenEndpoint(validCommonIamToken());

        time.travelTo(FIXED_TIME);

        UofConfigurationStub config = uofConfigWith1sFastFailingTimeoutAndOAuthOn(apiBaseUrl.get());
        CommonIamTokenCache tokenCache = createCommonIamTokenCache()
            .with(config)
            .with(internalConfigWith1sTimeoutAndMax1Connection())
            .with(time)
            .withResourceAudience(UF_REST_API)
            .build();

        tokenCache.getToken();

        assertThatJwtInAssertion(commonIamSimulator.getLastRequestBody())
            .hasIssuedAt(FIXED_TIME.getEpochSecond())
            .hasExpirationTime(FIXED_TIME.getEpochSecond() + ONE_MINUTE_IN_SECONDS);
    }

    @Test
    public void submitsAudienceEqualToAuthServerBaseUrl() throws Exception {
        commonIamSimulator.stubTokenEndpoint(validCommonIamToken());

        UofConfigurationStub config = uofConfigWith1sFastFailingTimeoutAndOAuthOn(apiBaseUrl.get());
        config.getClientAuthenticationStub().setClientId(CLIENT_ID);
        config.getClientAuthenticationStub().setSigningKeyId(KEY_ID);

        CommonIamTokenCache tokenCache = createCommonIamTokenCache()
            .with(config)
            .with(internalConfigWith1sTimeoutAndMax1Connection())
            .withResourceAudience(UF_REST_API)
            .build();

        tokenCache.getToken();

        assertThatJwtInAssertion(commonIamSimulator.getLastRequestBody())
            .hasAudience("http://localhost:" + apiBaseUrl.getPort() + "/");
    }

    private UofConfigurationStub uofConfigWith1sFastFailingTimeoutAndOAuthOn(String authorityOfUrl)
        throws NoSuchAlgorithmException {
        val keyPair = KeyPairs.rsaKeyPair2048keySize();

        val auth = new UofPrivateKeyJwtAuthenticationStub();
        auth.setClientId("test_client");
        auth.setPrivateKey(keyPair.getPrivate());
        auth.setSigningKeyId("test_key_id");
        auth.setHost(getHostFrom(authorityOfUrl));
        auth.setPort(getPortFrom(authorityOfUrl));
        auth.setUseSsl(false);

        val config = new UofConfigurationStub();
        config.setClientAuthentication(auth);

        config.getApiStub().setHttpClientFastFailingTimeout(ofSeconds(1));

        return config;
    }

    private UofConfigurationStub uofConfigWith1sFastFailingTimeoutAndOAuthOnWithTenant(
        String tenant,
        String authorityOfUrl
    ) throws NoSuchAlgorithmException {
        val config = uofConfigWith1sFastFailingTimeoutAndOAuthOn(authorityOfUrl);
        config.getClientAuthenticationStub().setTenant(tenant);

        return config;
    }

    private SdkInternalConfiguration internalConfigWith1sTimeoutAndMax1Connection() {
        SdkInternalConfiguration config = mock(SdkInternalConfiguration.class);
        when(config.getHttpClientTimeout()).thenReturn(1);
        when(config.getHttpClientMaxConnTotal()).thenReturn(1);
        when(config.getHttpClientMaxConnPerRoute()).thenReturn(1);
        return config;
    }

    @Test
    void logsSuccessfulOAuth2ResponseWithAnonymizedAccessToken() throws Exception {
        commonIamSimulator.stubTokenEndpoint(validCommonIamToken());

        CommonIamTokenCache tokenCache = createCommonIamTokenCache()
            .with(uofConfigWith1sFastFailingTimeoutAndOAuthOn(apiBaseUrl.get()))
            .with(internalConfigWith1sTimeoutAndMax1Connection())
            .withResourceAudience(UF_REST_API)
            .build();

        tokenCache.getToken();

        logsMock.verifyLoggedLinesCountEqualTo(1);

        logsMock.loggedLineContains("Request[OAuth2]:");
        logsMock.loggedLineContains("/oauth/token");
        logsMock.loggedLineContains("response - OK(HTTP 200):");
        logsMock.loggedLineContains("\"access_token\":***ANONYMIZED***");
        logsMock.verifyNotLoggedLineContaining(validCommonIamToken().getAccessToken());
    }

    @Test
    void skipsLoggingOfSuccessfulOAuth2ResponseWhenLoggingLevelHigherThanInfo() throws Exception {
        logsMock.setLevel(ch.qos.logback.classic.Level.WARN);

        commonIamSimulator.stubTokenEndpoint(validCommonIamToken());

        CommonIamTokenCache tokenCache = createCommonIamTokenCache()
            .with(uofConfigWith1sFastFailingTimeoutAndOAuthOn(apiBaseUrl.get()))
            .with(internalConfigWith1sTimeoutAndMax1Connection())
            .withResourceAudience(UF_REST_API)
            .build();

        tokenCache.getToken();

        logsMock.verifyNoLoggedLines();
    }

    @Test
    void logsFailedOAuth2ResponseWithHttpError() throws Exception {
        commonIamSimulator.stubTokenEndpointWithInternalServerErrorResponse();

        CommonIamTokenCache tokenCache = createCommonIamTokenCache()
            .with(uofConfigWith1sFastFailingTimeoutAndOAuthOn(apiBaseUrl.get()))
            .with(internalConfigWith1sTimeoutAndMax1Connection())
            .withResourceAudience(UF_REST_API)
            .build();

        assertThatThrownBy(tokenCache::getToken).isInstanceOf(OAuth2TokenRetrievalException.class);

        logsMock.verifyLoggedLinesCountEqualTo(1);
        logsMock.loggedLineContains("Request[OAuth2]:");
        logsMock.loggedLineContains("/oauth/token");
        logsMock.loggedLineContains("response - FAILED(HTTP 500)");
    }

    @Nested
    class CircuitBreaker {

        private final Instant now = Instant.ofEpochMilli(1672531200000L);
        private final TimeUtilsStub time = TimeUtilsStub
            .threadSafe(new AtomicActionPerformer())
            .withCurrentTime(now);

        @Test
        void afterFailedCallAllRequestsInTheNextFiveSecondsThrowExceptionWithoutMakingApiCall()
            throws Exception {
            commonIamSimulator.stubTokenEndpointWithInternalServerErrorResponse();

            CommonIamTokenCache tokenCache = createCommonIamTokenCache()
                .with(uofConfigWith1sFastFailingTimeoutAndOAuthOn(apiBaseUrl.get()))
                .with(internalConfigWith1sTimeoutAndMax1Connection())
                .with(time)
                .withResourceAudience(UF_REST_API)
                .build();

            executeEverySecondMovingTheClockForward(
                4,
                () -> {
                    assertThatThrownBy(tokenCache::getToken)
                        .isInstanceOf(OAuth2TokenRetrievalException.class);
                }
            );
            commonIamSimulator.verifyTokenEndpointCalledOnce();

            assertThatThrownBy(tokenCache::getToken)
                .isInstanceOf(OAuth2TokenRetrievalException.class)
                .hasMessageContaining(
                    "Failed to retrieve OAuth token - returning error without calling CommonIAM"
                );
        }

        @Test
        void afterFailedCallNextCallIsLetThroughAfterFiveSeconds() throws Exception {
            commonIamSimulator.stubTokenEndpointWithInternalServerErrorResponse();

            CommonIamTokenCache tokenCache = createCommonIamTokenCache()
                .with(uofConfigWith1sFastFailingTimeoutAndOAuthOn(apiBaseUrl.get()))
                .with(internalConfigWith1sTimeoutAndMax1Connection())
                .with(time)
                .withResourceAudience(UF_REST_API)
                .build();

            assertThatThrownBy(tokenCache::getToken).isInstanceOf(OAuth2TokenRetrievalException.class);

            time.tick(FIVE_SECONDS);

            assertThatThrownBy(tokenCache::getToken)
                .isInstanceOf(OAuth2TokenRetrievalException.class)
                .hasMessage("Failed to retrieve OAuth token");

            commonIamSimulator.verifyTokenEndpointCalledTimes(2);
        }

        @Test
        void afterTenConsecutiveFailedCallsTheTimeBetweenApiCallsIsIncreasedToFiveMinutes() throws Exception {
            commonIamSimulator.stubTokenEndpointWithInternalServerErrorResponse();

            CommonIamTokenCache tokenCache = createCommonIamTokenCache()
                .with(uofConfigWith1sFastFailingTimeoutAndOAuthOn(apiBaseUrl.get()))
                .with(internalConfigWith1sTimeoutAndMax1Connection())
                .with(time)
                .withResourceAudience(UF_REST_API)
                .build();

            executeEveryFiveSecondsMovingTheClockForward(
                10,
                () -> {
                    assertThatThrownBy(tokenCache::getToken)
                        .isInstanceOf(OAuth2TokenRetrievalException.class);
                }
            );
            // already 5 seconds in the 5 minute backoff

            assertThatThrownBy(tokenCache::getToken)
                .isInstanceOf(OAuth2TokenRetrievalException.class)
                .hasMessage("Failed to retrieve OAuth token - returning error without calling CommonIAM");

            time.tick(seconds(10));

            assertThatThrownBy(tokenCache::getToken)
                .isInstanceOf(OAuth2TokenRetrievalException.class)
                .hasMessage("Failed to retrieve OAuth token - returning error without calling CommonIAM");

            time.tick(seconds(285));

            assertThatThrownBy(tokenCache::getToken)
                .isInstanceOf(OAuth2TokenRetrievalException.class)
                .hasMessage("Failed to retrieve OAuth token");

            commonIamSimulator.verifyTokenEndpointCalledTimes(11);
        }

        @Test
        void failureCountIsResetAfterSuccessfulCall() throws Exception {
            commonIamSimulator.stubTokenEndpointWithInternalServerErrorResponse();

            CommonIamTokenCache tokenCache = createCommonIamTokenCache()
                .with(uofConfigWith1sFastFailingTimeoutAndOAuthOn(apiBaseUrl.get()))
                .with(internalConfigWith1sTimeoutAndMax1Connection())
                .with(time)
                .withResourceAudience(UF_REST_API)
                .build();

            executeEveryFiveSecondsMovingTheClockForward(
                9,
                () -> {
                    assertThatThrownBy(tokenCache::getToken)
                        .isInstanceOf(OAuth2TokenRetrievalException.class);
                }
            );

            commonIamSimulator.stubTokenEndpoint(immediatelyExpiredCommonIamToken());
            val token = tokenCache.getToken();
            assertThat(token.getAccessToken()).isEqualTo(immediatelyExpiredCommonIamToken().getAccessToken());

            commonIamSimulator.stubTokenEndpointWithInternalServerErrorResponse();

            executeEveryFiveSecondsMovingTheClockForward(
                10,
                () -> {
                    assertThatThrownBy(tokenCache::getToken)
                        .isInstanceOf(OAuth2TokenRetrievalException.class);
                }
            );

            commonIamSimulator.verifyTokenEndpointCalledTimes(20);
        }

        @Nested
        @SuppressWarnings({ "IllegalCatch", "LambdaBodyLength" })
        class Concurrency {

            private final int threadCount = 20;
            private final int expectedClockTicks = 1000;
            private final AtomicInteger getTokenExecutions = new AtomicInteger(0);
            private final AtomicInteger actualClockTicks = new AtomicInteger(0);
            private final AtomicBoolean tokenCallBeforeTheVeryFirstTickHappened = new AtomicBoolean(false);
            private final ScheduledExecutorService executor = newScheduledThreadPool(40);

            @AfterEach
            void cleanup() {
                executor.shutdownNow();
            }

            @Test
            void circuitBreakerBehavesCorrectlyWhenAccessedConcurrently() throws Exception {
                val tokenCache = createCommonIamTokenCache()
                    .with(uofConfigWith1sFastFailingTimeoutAndOAuthOn(apiBaseUrl.get()))
                    .with(internalConfigWith1sTimeoutAndMax1Connection())
                    .with(time)
                    .withResourceAudience(UF_REST_API)
                    .build();

                commonIamSimulator.stubTokenEndpointWithInternalServerErrorResponse();

                Runnable getToken = () -> {
                    try {
                        getTokenExecutions.incrementAndGet();
                        tokenCache.getToken();
                    } catch (Exception e) {
                        // noop
                    }
                    if (!tokenCallBeforeTheVeryFirstTickHappened.get()) {
                        tokenCallBeforeTheVeryFirstTickHappened.set(true);
                    }
                };

                executeNonStopByMultipleThreads(getToken);

                val tickClockRef = new AtomicReference<Runnable>();
                Runnable tickClock = () -> {
                    if (actualClockTicks.get() == expectedClockTicks) {
                        return;
                    }
                    if (tokenCallBeforeTheVeryFirstTickHappened.get()) {
                        time.tick(fiveSecondsForFirst10CallsAnd5MinutesForTheRest());

                        actualClockTicks.incrementAndGet();

                        waitUntilTokenEndpointFullyInvokedByAtLeastOneThread();
                    }

                    executor.execute(() -> {
                        tickClockRef.get().run();
                    });
                };
                tickClockRef.set(tickClock);

                executor.execute(tickClock);

                await().atMost(ofSeconds(200)).until(() -> actualClockTicks.get() == expectedClockTicks);

                waitUntilTokenEndpointFullyInvokedByAtLeastOneThread();

                int initialTokenCallBeforeTheFirstTick = 1;
                commonIamSimulator.verifyTokenEndpointCalledTimes(
                    actualClockTicks.get() + initialTokenCallBeforeTheFirstTick
                );
            }

            private void waitUntilTokenEndpointFullyInvokedByAtLeastOneThread() {
                getTokenExecutions.set(0);
                await().until(() -> getTokenExecutions.get() > threadCount * 3);
            }

            private TimeInterval fiveSecondsForFirst10CallsAnd5MinutesForTheRest() {
                int checkIsDoneBeforeTickIncrement = 1;
                return seconds(actualClockTicks.get() + checkIsDoneBeforeTickIncrement < 10 ? 5 : 300);
            }

            private ConditionFactory await() {
                return Awaitility.await().pollInterval(Duration.ofMillis(1)).atMost(ofSeconds(2));
            }

            private void executeNonStopByMultipleThreads(Runnable getToken) {
                for (int i = 0; i < threadCount; i++) {
                    executor.scheduleAtFixedRate(getToken, 0, 1, TimeUnit.MILLISECONDS);
                }
            }
        }

        private void executeEveryFiveSecondsMovingTheClockForward(int numberOfTicks, Runnable run) {
            for (int i = 1; i <= numberOfTicks; i++) {
                run.run();
                time.tick(FIVE_SECONDS);
            }
        }

        private void executeEverySecondMovingTheClockForward(int numberOfTicks, Runnable runnable) {
            for (int i = 1; i < numberOfTicks; i++) {
                runnable.run();
                time.tick(ONE_SECOND);
            }
        }
    }
}
