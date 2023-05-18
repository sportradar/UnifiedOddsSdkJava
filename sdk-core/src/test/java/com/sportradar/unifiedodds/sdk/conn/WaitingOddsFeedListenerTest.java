/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */
package com.sportradar.unifiedodds.sdk.conn;

import static com.sportradar.unifiedodds.sdk.testutil.generic.concurrent.SignallingOnPollingQueue.createSignallingOnPollingQueue;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;

import com.sportradar.unifiedodds.sdk.OddsFeedSession;
import com.sportradar.unifiedodds.sdk.entities.SportEvent;
import com.sportradar.unifiedodds.sdk.impl.recovery.TimeUtilsStub;
import com.sportradar.unifiedodds.sdk.oddsentities.OddsChange;
import com.sportradar.unifiedodds.sdk.testutil.generic.concurrent.AtomicActionPerformer;
import com.sportradar.unifiedodds.sdk.testutil.generic.concurrent.FluentExecutor;
import com.sportradar.unifiedodds.sdk.testutil.generic.concurrent.SignallingOnPollingQueue;
import java.time.Instant;
import java.util.concurrent.*;
import lombok.val;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.Timeout;

public class WaitingOddsFeedListenerTest {

    private static final long MIDNIGHT_TIMESTAMP_MILLIS = 1664402400000L;
    private static final Instant INSTANT_AT_MIDNIGHT = Instant.ofEpochMilli(MIDNIGHT_TIMESTAMP_MILLIS);

    @Rule
    public Timeout globalTimeout = Timeout.seconds(1);

    private final TimeUtilsStub timeUtils = TimeUtilsStub
        .threadSafe(new AtomicActionPerformer())
        .withCurrentTime(INSTANT_AT_MIDNIGHT);
    private final SignallingOnPollingQueue<OddsChange<SportEvent>> queue = createSignallingOnPollingQueue(
        timeUtils
    );

    private final FluentExecutor executor = new FluentExecutor();

    @Test
    public void shouldNotFailIfOddsMessageIsReceivedIn1Sec() {
        val listener = new WaitingOddsFeedListener.Factory(queue).expectingOddsChange();
        val message = mock(OddsChange.class);
        listener.onOddsChange(mock(OddsFeedSession.class), message);

        assertSame(message, listener.waitForOddsChange());
    }

    @Test
    public void shouldSucceedIfMessageWasDeliveredShortlyAfterStartedWaitingForIt() {
        val listener = new WaitingOddsFeedListener.Factory(queue).expectingOddsChange();

        OddsChange message = mock(OddsChange.class);
        executor.executeInAnotherThread(() -> {
            queue.getWaiterForStartingToPoll().await(2, TimeUnit.SECONDS);
            listener.onOddsChange(mock(OddsFeedSession.class), message);
        });

        assertSame(message, listener.waitForOddsChange());
    }

    @Test
    public void shouldFailIfNoMessageReceivedIn1Sec() {
        val listener = new WaitingOddsFeedListener.Factory(queue).expectingOddsChange();

        executor.executeInAnotherThread(() -> {
            queue.getWaiterForStartingToPoll().await(2, TimeUnit.SECONDS);
            timeUtils.fastForwardSeconds(2);
        });

        assertThatThrownBy(() -> listener.waitForOddsChange())
            .isInstanceOf(AssertionError.class)
            .hasMessageContaining("Odds change message was not received");
    }
}
