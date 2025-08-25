/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */
package com.sportradar.unifiedodds.sdk.internal.impl.recovery;

import static com.sportradar.unifiedodds.sdk.conn.SapiProducers.buildActiveProducer;
import static com.sportradar.unifiedodds.sdk.conn.SapiProducers.liveOddsProducer;
import static com.sportradar.unifiedodds.sdk.impl.apireaders.HttpHelperStubs.acceptingPostRequests;
import static com.sportradar.unifiedodds.sdk.impl.apireaders.HttpHelperStubs.verifyUrlPostedTo;
import static com.sportradar.unifiedodds.sdk.impl.recovery.ProducerDataProviders.providingSuccessfully;
import static com.sportradar.unifiedodds.sdk.internal.impl.recovery.RecoveryAndProducerManagers.BuilderStubbingOutProducerDataProviderAndHttp.stubbingOutProducerDataProviderAndHttpClient;
import static com.sportradar.unifiedodds.sdk.testutil.generic.concurrent.WaiterForEvents.WaitingStatus.EVENT_HAPPENED;
import static com.sportradar.unifiedodds.sdk.testutil.generic.concurrent.WaiterForEvents.WaitingStatus.EVENT_NOT_HAPPENED;
import static com.sportradar.unifiedodds.sdk.testutil.generic.concurrent.WaiterForEvents.createWaiterForEvents;
import static com.sportradar.utils.time.TimeInterval.minutes;
import static com.sportradar.utils.time.TimeInterval.seconds;
import static java.time.temporal.ChronoUnit.SECONDS;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.platform.commons.util.CollectionUtils.getOnlyElement;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.google.common.collect.ImmutableMap;
import com.sportradar.unifiedodds.sdk.MessageInterest;
import com.sportradar.unifiedodds.sdk.cfg.Environment;
import com.sportradar.unifiedodds.sdk.common.internal.ObservableOpenTelemetry;
import com.sportradar.unifiedodds.sdk.conn.ProducerId;
import com.sportradar.unifiedodds.sdk.internal.common.telemetry.UsageGauge;
import com.sportradar.unifiedodds.sdk.internal.impl.SdkInternalConfiguration;
import com.sportradar.unifiedodds.sdk.internal.impl.apireaders.HttpHelper;
import com.sportradar.unifiedodds.sdk.managers.ProducerManager;
import com.sportradar.unifiedodds.sdk.oddsentities.Producer;
import com.sportradar.unifiedodds.sdk.testutil.generic.concurrent.AtomicActionPerformer;
import com.sportradar.utils.time.TimeInterval;
import com.sportradar.utils.time.TimeUtilsStub;
import java.net.URI;
import java.time.Instant;
import java.util.Map;
import java.util.stream.Stream;
import lombok.Value;
import lombok.val;
import org.junit.jupiter.api.Named;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.platform.commons.util.StringUtils;

@SuppressWarnings(
    { "MagicNumber", "ClassFanOutComplexity", "MultipleStringLiterals", "LineLength", "LambdaBodyLength" }
)
class RecoveryAndProducerManagersTest {

    private static final int TEN_MINUTES = 10;
    private static final int IS_UP = 1;
    private static final int IS_DOWN = 0;
    private final long midnightTimestampMillis = 1664402400000L;
    private final int oneHourMaxWindow = 60;

    private final TimeUtilsStub time = TimeUtilsStub
        .threadSafe(new AtomicActionPerformer())
        .withCurrentTime(Instant.ofEpochMilli(midnightTimestampMillis));

    @Test
    void withoutAliveRecoveriesAreNotIssued() {
        val recoveryInvoked = createWaiterForEvents();
        val producers = providingSuccessfully(
            liveOddsProducer(p -> p.setStatefulRecoveryWindowInMinutes(oneHourMaxWindow))
        );
        val managers = stubbingOutProducerDataProviderAndHttpClient()
            .withOnRecoveryInitiated(ri -> recoveryInvoked.markEventHappened())
            .withProducerDataProvider(producers)
            .withTime(time)
            .build();
        managers.markFeedAsOpened();

        managers.recoveryManager().onTimerElapsed();

        assertThat(recoveryInvoked.getWaitingStatus()).isEqualTo(EVENT_NOT_HAPPENED);
    }

    @Test
    void firstRecoveryIsIssuedOnAliveWithRequestedTimestamp() {
        HttpHelper httpClient = acceptingPostRequests();
        val recoveryInvoked = createWaiterForEvents();
        val producers = providingSuccessfully(
            liveOddsProducer(p -> p.setStatefulRecoveryWindowInMinutes(oneHourMaxWindow))
        );
        val managers = stubbingOutProducerDataProviderAndHttpClient()
            .withOnRecoveryInitiated(ri -> recoveryInvoked.markEventHappened())
            .withProducerDataProvider(producers)
            .withHttpClient(httpClient)
            .withTime(time)
            .build();

        long recoveryTimestamp = midnightTimestampMillis - minutes(59).getInMillis();
        managers.producerManager().setProducerRecoveryFromTimestamp(1, recoveryTimestamp);
        managers.markFeedAsOpened();

        managers
            .recoveryManager()
            .onAliveReceived(1, midnightTimestampMillis, midnightTimestampMillis, true, true);

        assertThat(recoveryInvoked.getWaitingStatus()).isEqualTo(EVENT_HAPPENED);
        verifyUrlPostedTo(httpClient).contains("after=" + recoveryTimestamp);
    }

    @Test
    void recoveryIsIssuedToTheApiUrlReturnedFromProducersApiCall() {
        HttpHelper httpClient = acceptingPostRequests();
        val recoveryInvoked = createWaiterForEvents();
        val producerApiUrl = "https://pcs-odds.sportradar.com:9891/v1/pcs/";
        val producers = providingSuccessfully(
            buildActiveProducer(ProducerId.PREMIUM_CRICKET, producerApiUrl)
        );
        val managers = stubbingOutProducerDataProviderAndHttpClient()
            .withOnRecoveryInitiated(ri -> recoveryInvoked.markEventHappened())
            .withProducerDataProvider(producers)
            .withHttpClient(httpClient)
            .withTime(time)
            .build();

        long recoveryTimestamp = midnightTimestampMillis - minutes(59).getInMillis();
        managers
            .producerManager()
            .setProducerRecoveryFromTimestamp(ProducerId.PREMIUM_CRICKET.get(), recoveryTimestamp);
        managers.markFeedAsOpened();

        managers
            .recoveryManager()
            .onAliveReceived(
                ProducerId.PREMIUM_CRICKET.get(),
                midnightTimestampMillis,
                midnightTimestampMillis,
                true,
                true
            );

        assertThat(recoveryInvoked.getWaitingStatus()).isEqualTo(EVENT_HAPPENED);
        verifyUrlPostedTo(httpClient).startsWith(producerApiUrl + "recovery/initiate_request");
    }

    @Test
    void producerRecoveryTimestampInitiallyCannotBeSetFurtherAwayInThePastThanMaxAllowedByThatProducer() {
        HttpHelper httpClient = acceptingPostRequests();
        val producers = providingSuccessfully(
            liveOddsProducer(p -> p.setStatefulRecoveryWindowInMinutes(oneHourMaxWindow))
        );
        val managers = stubbingOutProducerDataProviderAndHttpClient()
            .withProducerDataProvider(producers)
            .withHttpClient(httpClient)
            .withTime(time)
            .build();

        long minutes61Millis = minutes(61).getInMillis();
        assertThatThrownBy(() ->
                managers
                    .producerManager()
                    .setProducerRecoveryFromTimestamp(1, midnightTimestampMillis - minutes61Millis)
            )
            .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void asProducerRecoveryWindowExpandsTooMuchItIsSilentlyReducedBy10MinutesBelowMaxAllowByTheProducer() {
        HttpHelper httpClient = acceptingPostRequests();
        val recoveryInvoked = createWaiterForEvents();
        val producers = providingSuccessfully(
            liveOddsProducer(p -> p.setStatefulRecoveryWindowInMinutes(oneHourMaxWindow))
        );
        val managers = stubbingOutProducerDataProviderAndHttpClient()
            .withOnRecoveryInitiated(ri -> recoveryInvoked.markEventHappened())
            .withProducerDataProvider(producers)
            .withHttpClient(httpClient)
            .withTime(time)
            .build();
        managers
            .producerManager()
            .setProducerRecoveryFromTimestamp(1, midnightTimestampMillis - minutes(59).getInMillis());
        managers.markFeedAsOpened();

        TimeInterval timePassed = minutes(2);
        time.tick(timePassed);

        managers
            .recoveryManager()
            .onAliveReceived(1, midnightTimestampMillis, midnightTimestampMillis, true, true);

        assertThat(recoveryInvoked.getWaitingStatus()).isEqualTo(EVENT_HAPPENED);
        verifyUrlPostedTo(httpClient)
            .contains(
                "after=" +
                (
                    midnightTimestampMillis +
                    timePassed.getInMillis() -
                    (minutes(oneHourMaxWindow).getInMillis() - minutes(TEN_MINUTES).getInMillis())
                )
            );
    }

    @Nested
    public class ProducerDownUsageMetric {

        @Test
        void producersAreMarkedAsDownInitiallyWithoutReasonAttributeSet() {
            val producers = providingSuccessfully(liveOddsProducer());
            val observableOpenTelemetry = new ObservableOpenTelemetry();
            val managers = stubbingOutProducerDataProviderAndHttpClient()
                .withProducerDataProvider(producers)
                .withTime(time)
                .withOpenTelemetry(observableOpenTelemetry)
                .build();
            managers.markFeedAsOpened();

            assertThat(managers.producerManager().isProducerDown(1)).isTrue();

            observableOpenTelemetry.verify(UsageGauge.PRODUCER_STATUS).hasLongValue(IS_DOWN);
            observableOpenTelemetry
                .verify(UsageGauge.PRODUCER_STATUS)
                .theOnlyOneDataPoint()
                .hasAttributes(
                    "producer",
                    liveOddsProducer().getId() + "-" + liveOddsProducer().getName(),
                    "reason",
                    "unknown"
                );
        }

        @Test
        void afterSuccessfulRecoveryProducerIsMarkedAsUp() {
            HttpHelper httpClient = acceptingPostRequests();
            val producers = providingSuccessfully(liveOddsProducer());
            val observableOpenTelemetry = new ObservableOpenTelemetry();
            val managers = stubbingOutProducerDataProviderAndHttpClient()
                .withProducerDataProvider(producers)
                .withTime(time)
                .withOpenTelemetry(observableOpenTelemetry)
                .withHttpClient(httpClient)
                .build();
            managers.markFeedAsOpened();

            performInitialRecovery(managers);

            observableOpenTelemetry.verify(UsageGauge.PRODUCER_STATUS).hasLongValue(IS_UP);
            observableOpenTelemetry
                .verify(UsageGauge.PRODUCER_STATUS)
                .theOnlyOneDataPoint()
                .hasAttributes("producer", liveOddsProducer().getId() + "-" + liveOddsProducer().getName());
        }

        @Test
        void afterProcessingAlivesAndMessageInTimelyFashionProducerIsStillMarkedAsUpOnTheNextTick() {
            HttpHelper httpClient = acceptingPostRequests();
            val producers = providingSuccessfully(liveOddsProducer());
            val observableOpenTelemetry = new ObservableOpenTelemetry();
            val managers = stubbingOutProducerDataProviderAndHttpClient()
                .withProducerDataProvider(producers)
                .withTime(time)
                .withOpenTelemetry(observableOpenTelemetry)
                .withHttpClient(httpClient)
                .build();
            managers.markFeedAsOpened();

            performInitialRecovery(managers);

            long systemAliveReceivedTime = time.now() - 1000;
            long systemAliveGeneratedTime = time.now() - 2000;
            long userAliveReceivedTime = time.now() - 2000;
            long userAliveGeneratedTime = time.now() - 3000;
            long messageGeneratedTime = time.now() - 2000;

            managers
                .recoveryManager()
                .onAliveReceived(1, systemAliveGeneratedTime, systemAliveReceivedTime, true, true);
            managers
                .recoveryManager()
                .onAliveReceived(1, userAliveGeneratedTime, userAliveReceivedTime, true, false);

            managers.recoveryManager().onMessageProcessingStarted(11, 1, null, time.now());
            managers.recoveryManager().onMessageProcessingEnded(11, 1, messageGeneratedTime, "1");
            managers.recoveryManager().onTimerElapsed();

            observableOpenTelemetry.verify(UsageGauge.PRODUCER_STATUS).hasLongValue(IS_UP);
            observableOpenTelemetry
                .verify(UsageGauge.PRODUCER_STATUS)
                .theOnlyOneDataPoint()
                .hasAttributes("producer", liveOddsProducer().getId() + "-" + liveOddsProducer().getName());
        }

        private void performInitialRecovery(RecoveryAndProducerManagers managers) {
            managers.recoveryManager().onAliveReceived(1, time.now(), time.now(), true, true);

            managers
                .recoveryManager()
                .onSnapshotCompleteReceived(1, time.now(), 1, MessageInterest.AllMessages);
        }

        @Test
        void afterSpottingAliveIntervalViolationProducersAreMarkedAsDown() {
            HttpHelper httpClient = acceptingPostRequests();
            val producers = providingSuccessfully(liveOddsProducer());
            val observableOpenTelemetry = new ObservableOpenTelemetry();
            val managers = stubbingOutProducerDataProviderAndHttpClient()
                .withProducerDataProvider(producers)
                .withTime(time)
                .withOpenTelemetry(observableOpenTelemetry)
                .withHttpClient(httpClient)
                .build();
            managers.markFeedAsOpened();

            performInitialRecovery(managers);

            time.tick(seconds(30));

            long twentySecondsAgo = time.nowInstant().minus(20, SECONDS).toEpochMilli();
            managers.recoveryManager().onAliveReceived(1, twentySecondsAgo, twentySecondsAgo, true, true);
            managers.recoveryManager().onTimerElapsed();

            observableOpenTelemetry.verify(UsageGauge.PRODUCER_STATUS).hasLongValue(IS_DOWN);
            observableOpenTelemetry
                .verify(UsageGauge.PRODUCER_STATUS)
                .theOnlyOneDataPoint()
                .hasAttributes(
                    "producer",
                    liveOddsProducer().getId() + "-" + liveOddsProducer().getName(),
                    "reason",
                    "AliveIntervalViolation"
                );
        }

        @Test
        void afterSpottingProcessingDelayProducersAreMarkedAsDown() {
            HttpHelper httpClient = acceptingPostRequests();
            val producers = providingSuccessfully(liveOddsProducer());
            val observableOpenTelemetry = new ObservableOpenTelemetry();
            val managers = stubbingOutProducerDataProviderAndHttpClient()
                .withProducerDataProvider(producers)
                .withTime(time)
                .withOpenTelemetry(observableOpenTelemetry)
                .withHttpClient(httpClient)
                .build();
            managers.markFeedAsOpened();

            performInitialRecovery(managers);

            long systemAliveReceivedTime = time.now() - 1000;
            long systemAliveGeneratedTime = time.now() - 2000;
            long userAliveReceivedTime = time.now() - 2000;
            long userAliveGeneratedTime = time.now() - 3000;
            long messageGeneratedTimeTwentySecondsAgo = time.now() - 20_000;

            managers
                .recoveryManager()
                .onAliveReceived(1, systemAliveGeneratedTime, systemAliveReceivedTime, true, true);
            managers
                .recoveryManager()
                .onAliveReceived(1, userAliveGeneratedTime, userAliveReceivedTime, true, false);

            managers.recoveryManager().onMessageProcessingStarted(11, 1, null, time.now());
            managers
                .recoveryManager()
                .onMessageProcessingEnded(11, 1, messageGeneratedTimeTwentySecondsAgo, "1");

            managers.recoveryManager().onTimerElapsed();

            observableOpenTelemetry.verify(UsageGauge.PRODUCER_STATUS).hasLongValue(IS_DOWN);
            observableOpenTelemetry
                .verify(UsageGauge.PRODUCER_STATUS)
                .theOnlyOneDataPoint()
                .hasLongValue(0)
                .hasAttributes(
                    "producer",
                    liveOddsProducer().getId() + "-" + liveOddsProducer().getName(),
                    "reason",
                    "ProcessingQueueDelayViolation"
                );
        }
    }

    @Nested
    class CustomizableProducerApiUrls {

        private static final String PRODUCERS_AND_PRODUCER_SUPPLIERS =
            "com.sportradar.unifiedodds.sdk.internal.impl.recovery.RecoveryAndProducerManagersTest#producersAndProducerSuppliers";
        private static final String PRODUCERS =
            "com.sportradar.unifiedodds.sdk.internal.impl.recovery.RecoveryAndProducerManagersTest#producers";

        @Nested
        class WhenProducersAreFetchedFromProducerManager {

            @ParameterizedTest
            @MethodSource(PRODUCERS_AND_PRODUCER_SUPPLIERS)
            void producerApiUrlsForCustomEnvironmentAreChangedToPointToCustomHttpsApi(
                ProducersSupplier producersSupplier,
                ProducerId producerId,
                String producerApiUrl
            ) {
                HttpHelper httpClient = acceptingPostRequests();
                val producer = buildActiveProducer(producerId);
                producer.setApiUrl(producerApiUrl);

                val config = sdkCustomConfigWithApiHostPortAndUseSsl("some.sportradar.com", 2413, true);
                val producerDataProvider = providingSuccessfully(producer);
                val observableOpenTelemetry = new ObservableOpenTelemetry();
                val managers = stubbingOutProducerDataProviderAndHttpClient()
                    .withProducerDataProvider(producerDataProvider)
                    .withTime(time)
                    .withOpenTelemetry(observableOpenTelemetry)
                    .withHttpClient(httpClient)
                    .withSdkInternalConfig(config)
                    .build();

                val producers = producersSupplier.get(managers.producerManager());
                val actualApiUrl = getOnlyElement(producers.values()).getApiUrl();

                assertThat(actualApiUrl).startsWith("https://some.sportradar.com:2413");
                assertThat(queryStringFrom(actualApiUrl)).isEqualTo(queryStringFrom(producerApiUrl));
                assertThat(recoveryPathFor(actualApiUrl)).isEqualTo(recoveryPathFor(producerApiUrl));
            }

            @ParameterizedTest
            @MethodSource(PRODUCERS_AND_PRODUCER_SUPPLIERS)
            void producerApiUrlsForCustomEnvironmentAreChangedToPointToCustomHttpApiOnDefaultHttpPort(
                ProducersSupplier producersSupplier,
                ProducerId producerId,
                String producerApiUrl
            ) {
                HttpHelper httpClient = acceptingPostRequests();
                val producer = buildActiveProducer(producerId);
                producer.setApiUrl(producerApiUrl);

                val config = sdkCustomConfigWithApiHostPortAndUseSsl("other.http.sportradar.com", 80, false);
                val producerDataProvider = providingSuccessfully(producer);
                val observableOpenTelemetry = new ObservableOpenTelemetry();
                val managers = stubbingOutProducerDataProviderAndHttpClient()
                    .withProducerDataProvider(producerDataProvider)
                    .withTime(time)
                    .withOpenTelemetry(observableOpenTelemetry)
                    .withHttpClient(httpClient)
                    .withSdkInternalConfig(config)
                    .build();

                val producers = producersSupplier.get(managers.producerManager());
                val actualApiUrl = getOnlyElement(producers.values()).getApiUrl();

                assertThat(actualApiUrl).startsWith("http://other.http.sportradar.com");
                assertThat(queryStringFrom(actualApiUrl)).isEqualTo(queryStringFrom(producerApiUrl));
                assertThat(recoveryPathFor(actualApiUrl)).isEqualTo(recoveryPathFor(producerApiUrl));
            }

            @ParameterizedTest
            @MethodSource(PRODUCERS_AND_PRODUCER_SUPPLIERS)
            void producerApiUrlsForCustomEnvironmentAreChangedToPointToCustomHttpApiOnNonDefaultHttpPort(
                ProducersSupplier producersSupplier,
                ProducerId producerId,
                String producerApiUrl
            ) {
                HttpHelper httpClient = acceptingPostRequests();
                val producer = buildActiveProducer(producerId);
                producer.setApiUrl(producerApiUrl);

                val config = sdkCustomConfigWithApiHostPortAndUseSsl(
                    "other.non-default.sportradar.com",
                    8081,
                    false
                );
                val producerDataProvider = providingSuccessfully(producer);
                val observableOpenTelemetry = new ObservableOpenTelemetry();
                val managers = stubbingOutProducerDataProviderAndHttpClient()
                    .withProducerDataProvider(producerDataProvider)
                    .withTime(time)
                    .withOpenTelemetry(observableOpenTelemetry)
                    .withHttpClient(httpClient)
                    .withSdkInternalConfig(config)
                    .build();

                val producers = producersSupplier.get(managers.producerManager());
                val actualApiUrl = getOnlyElement(producers.values()).getApiUrl();

                assertThat(actualApiUrl).startsWith("http://other.non-default.sportradar.com:8081");
                assertThat(queryStringFrom(actualApiUrl)).isEqualTo(queryStringFrom(producerApiUrl));
                assertThat(recoveryPathFor(actualApiUrl)).isEqualTo(recoveryPathFor(producerApiUrl));
            }
        }

        @Nested
        class WhenActualRecoveryIsRequested {

            @ParameterizedTest
            @MethodSource(PRODUCERS)
            void customApiIsInvokedWithTheProducerApiUrlRewrittenToPointToTheCustomHostPortAndHttpsProtocol(
                TestProducer producerData
            ) {
                val recoveryInvoked = createWaiterForEvents();
                val httpClient = acceptingPostRequests();
                val producer = buildActiveProducer(producerData.id);
                producer.setApiUrl(producerData.apiUrl);

                val config = sdkCustomConfigWithApiHostPortAndUseSsl("custom.sportradar.com", 2413, true);
                val producerDataProvider = providingSuccessfully(producer);
                val observableOpenTelemetry = new ObservableOpenTelemetry();
                val managers = stubbingOutProducerDataProviderAndHttpClient()
                    .withProducerDataProvider(producerDataProvider)
                    .withOnRecoveryInitiated(ri -> recoveryInvoked.markEventHappened())
                    .withTime(time)
                    .withOpenTelemetry(observableOpenTelemetry)
                    .withHttpClient(httpClient)
                    .withSdkInternalConfig(config)
                    .build();

                long recoveryTimestamp = midnightTimestampMillis - minutes(59).getInMillis();
                managers
                    .producerManager()
                    .setProducerRecoveryFromTimestamp(producerData.id.get(), recoveryTimestamp);
                managers.markFeedAsOpened();

                managers
                    .recoveryManager()
                    .onAliveReceived(
                        producerData.id.get(),
                        midnightTimestampMillis,
                        midnightTimestampMillis,
                        true,
                        true
                    );

                assertThat(recoveryInvoked.getWaitingStatus()).isEqualTo(EVENT_HAPPENED);
                verifyUrlPostedTo(httpClient).startsWith("https://custom.sportradar.com:2413");
                verifyUrlPostedTo(httpClient).hasPathEqualTo(recoveryPathFor(producerData.apiUrl));
            }
        }

        @ParameterizedTest
        @MethodSource(PRODUCERS)
        void customApiIsInvokedWithTheProducerApiUrlRewrittenToPointToTheCustomHostPortAndHttpProtocol(
            TestProducer producerData
        ) {
            val recoveryInvoked = createWaiterForEvents();
            val httpClient = acceptingPostRequests();
            val producer = buildActiveProducer(producerData.id);
            producer.setApiUrl(producerData.apiUrl);

            val config = sdkCustomConfigWithApiHostPortAndUseSsl(
                "other.but.http.sportradar.com",
                5921,
                false
            );

            val producerDataProvider = providingSuccessfully(producer);
            val observableOpenTelemetry = new ObservableOpenTelemetry();
            val managers = stubbingOutProducerDataProviderAndHttpClient()
                .withProducerDataProvider(producerDataProvider)
                .withOnRecoveryInitiated(ri -> recoveryInvoked.markEventHappened())
                .withTime(time)
                .withOpenTelemetry(observableOpenTelemetry)
                .withHttpClient(httpClient)
                .withSdkInternalConfig(config)
                .build();

            long recoveryTimestamp = midnightTimestampMillis - minutes(59).getInMillis();
            managers
                .producerManager()
                .setProducerRecoveryFromTimestamp(producerData.id.get(), recoveryTimestamp);
            managers.markFeedAsOpened();

            managers
                .recoveryManager()
                .onAliveReceived(
                    producerData.id.get(),
                    midnightTimestampMillis,
                    midnightTimestampMillis,
                    true,
                    true
                );

            assertThat(recoveryInvoked.getWaitingStatus()).isEqualTo(EVENT_HAPPENED);
            verifyUrlPostedTo(httpClient).startsWith("http://other.but.http.sportradar.com:5921");
            verifyUrlPostedTo(httpClient).hasPathEqualTo(recoveryPathFor(producerData.apiUrl));
        }

        private SdkInternalConfiguration sdkCustomConfigWithApiHostPortAndUseSsl(
            String apiHost,
            int apiPort,
            boolean useSsl
        ) {
            val config = mock(SdkInternalConfiguration.class);
            when(config.getEnvironment()).thenReturn(Environment.Custom);
            when(config.getApiHost()).thenReturn(apiHost);
            when(config.getApiPort()).thenReturn(apiPort);
            when(config.getUseApiSsl()).thenReturn(useSsl);
            when(config.getApiHostAndPort())
                .thenReturn(apiHost + (apiPort == 80 || apiPort == 0 ? "" : ":" + apiPort));
            return config;
        }

        private String recoveryPathFor(String actualApiUrl) {
            val producerApiPath = URI.create(actualApiUrl).getPath();
            return replaceDoubleSlashes(producerApiPath + "/recovery/initiate_request");
        }

        private String replaceDoubleSlashes(String path) {
            if (StringUtils.isBlank(path)) {
                return path;
            }
            return path.replaceAll("/+", "/");
        }

        private String queryStringFrom(String actualApiUrl) {
            String query = URI.create(actualApiUrl).getQuery();
            return query != null ? query : "";
        }
    }

    @SuppressWarnings("unused")
    static Stream<Arguments> producersAndProducerSuppliers() {
        return producers()
            .flatMap(p ->
                Stream.of(
                    Arguments.of(
                        Named.named(
                            "getAvailableProducers",
                            (ProducersSupplier) (ProducerManager::getAvailableProducers)
                        ),
                        p.getId(),
                        p.getApiUrl()
                    ),
                    Arguments.of(
                        Named.named(
                            "getActiveProducers",
                            (ProducersSupplier) (ProducerManager::getActiveProducers)
                        ),
                        p.getId(),
                        p.getApiUrl()
                    ),
                    Arguments.of(
                        Named.named(
                            "getProducerById",
                            (ProducersSupplier) (
                                manager ->
                                    ImmutableMap.of(p.getId().get(), manager.getProducer(p.getId().get()))
                            )
                        ),
                        p.getId(),
                        p.getApiUrl()
                    )
                )
            );
    }

    static Stream<TestProducer> producers() {
        return Stream.of(
            producer(ProducerId.LIVE_ODDS, "http://stgapi.betradar.com/v1/liveodds/"),
            producer(ProducerId.LIVE_ODDS, "https://stgapi.betradar.com/v1/liveodds/"),
            producer(ProducerId.PREMIUM_CRICKET, "https://pcs-odds.sportradar.com:443/"),
            producer(ProducerId.LIVE_ODDS, "http://live-odds.sportradar.com:8080/path/"),
            producer(ProducerId.BETRADAR_CTRL, "https://api.sportradar.com:9443/"),
            producer(ProducerId.BETRADAR_CTRL, "http://betradar.sportradar.com:8081/"),
            producer(ProducerId.BETRADAR_CTRL, "https://api.sportradar.com:9001/"),
            producer(ProducerId.VIRTUAL_BASEBALL, "http://localhost:9001/"),
            producer(ProducerId.NUMBERS_BETTING, "https://localhost:34567/")
        );
    }

    static TestProducer producer(ProducerId id, String apiUrl) {
        return new TestProducer(id, apiUrl);
    }

    @Value
    @SuppressWarnings("VisibilityModifier")
    private static class TestProducer {

        ProducerId id;
        String apiUrl;

        public String toString() {
            return "{id=" + id + ", apiUrl='" + apiUrl + "'}";
        }
    }

    private interface ProducersSupplier {
        Map<Integer, Producer> get(ProducerManager producerManager);
    }
}
