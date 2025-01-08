/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */
package com.sportradar.unifiedodds.sdk.impl.recovery;

import static com.sportradar.unifiedodds.sdk.conn.SapiProducers.liveOddsProducer;
import static com.sportradar.unifiedodds.sdk.impl.apireaders.HttpHelperStubs.acceptingPostRequests;
import static com.sportradar.unifiedodds.sdk.impl.apireaders.HttpHelperStubs.verifyUrlPostedTo;
import static com.sportradar.unifiedodds.sdk.impl.recovery.ProducerDataProviders.providingSuccessfully;
import static com.sportradar.unifiedodds.sdk.impl.recovery.RecoveryAndProducerManagers.BuilderStubbingOutProducerDataProviderAndHttp.stubbingOutProducerDataProviderAndHttpClient;
import static com.sportradar.unifiedodds.sdk.testutil.generic.concurrent.WaiterForEvents.WaitingStatus.EVENT_HAPPENED;
import static com.sportradar.unifiedodds.sdk.testutil.generic.concurrent.WaiterForEvents.WaitingStatus.EVENT_NOT_HAPPENED;
import static com.sportradar.unifiedodds.sdk.testutil.generic.concurrent.WaiterForEvents.createWaiterForEvents;
import static com.sportradar.utils.time.TimeInterval.minutes;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.sportradar.unifiedodds.sdk.impl.apireaders.HttpHelper;
import com.sportradar.unifiedodds.sdk.testutil.generic.concurrent.AtomicActionPerformer;
import com.sportradar.utils.time.TimeInterval;
import com.sportradar.utils.time.TimeUtilsStub;
import java.time.Instant;
import lombok.val;
import org.junit.Test;

public class RecoveryAndProducerManagersTest {

    private static final int TEN_MINUTES = 10;
    private final long midnightTimestampMillis = 1664402400000L;
    private final int oneHourMaxWindow = 60;

    private final TimeUtilsStub time = TimeUtilsStub
        .threadSafe(new AtomicActionPerformer())
        .withCurrentTime(Instant.ofEpochMilli(midnightTimestampMillis));

    @Test
    public void withoutAliveRecoveriesAreNotIssued() {
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
    @SuppressWarnings("MagicNumber")
    public void firstRecoveryIsIssuedOnAliveWithRequestedTimestamp() {
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
    @SuppressWarnings("MagicNumber")
    public void producerRecoveryTimestampInitiallyCannotBeSetFurtherAwayInThePastThanMaxAllowedByThatProducer() {
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
    @SuppressWarnings("MagicNumber")
    public void asProducerRecoveryWindowExpandsTooMuchItIsSilentlyReducedBy10MinutesBelowMaxAllowByTheProducer() {
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
}
