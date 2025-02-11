/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */
package com.sportradar.unifiedodds.sdk.internal.impl.recovery;

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

import com.sportradar.unifiedodds.sdk.MessageInterest;
import com.sportradar.unifiedodds.sdk.common.internal.ObservableOpenTelemetry;
import com.sportradar.unifiedodds.sdk.internal.common.telemetry.UsageGauge;
import com.sportradar.unifiedodds.sdk.internal.impl.apireaders.HttpHelper;
import com.sportradar.unifiedodds.sdk.testutil.generic.concurrent.AtomicActionPerformer;
import com.sportradar.utils.time.TimeInterval;
import com.sportradar.utils.time.TimeUtilsStub;
import java.time.Instant;
import lombok.val;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

@SuppressWarnings({ "MagicNumber", "ClassFanOutComplexity", "MultipleStringLiterals" })
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
        void producersAreMarkedAsDownInitiallyWithoutReasonAttributeSet() throws Exception {
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
        void afterSuccessfulRecoveryProducerIsMarkedAsUp() throws Exception {
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
        void afterProcessingAlivesAndMessageInTimelyFashionProducerIsStillMarkedAsUpOnTheNextTick()
            throws Exception {
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
        void afterSpottingAliveIntervalViolationProducersAreMarkedAsDown() throws Exception {
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
        void afterSpottingProcessingDelayProducersAreMarkedAsDown() throws Exception {
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
}
