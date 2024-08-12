/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */
package com.sportradar.unifiedodds.sdk.testutil.generic.concurrent;

import static com.sportradar.unifiedodds.sdk.testutil.generic.concurrent.WaiterForEvents.WaitingStatus.EVENT_HAPPENED;
import static com.sportradar.unifiedodds.sdk.testutil.generic.concurrent.WaiterForEvents.WaitingStatus.EVENT_NOT_HAPPENED;
import static com.sportradar.unifiedodds.sdk.testutil.generic.concurrent.WaiterForEvents.createWaiterForEvents;
import static com.sportradar.utils.time.TimeInterval.seconds;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.*;

import com.sportradar.unifiedodds.sdk.impl.TimeUtils;
import com.sportradar.utils.time.TimeUtilsStub;
import java.time.Instant;
import java.util.concurrent.TimeUnit;
import lombok.val;
import org.junit.After;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;

@Timeout(1)
public class SignallingOnPollingQueueTest {

    private static final long MIDNIGHT_TIMESTAMP_MILLIS = 1664402400000L;

    private final Instant instantAtMidnight = Instant.ofEpochMilli(MIDNIGHT_TIMESTAMP_MILLIS);
    private final Object element = new Object();

    private final TimeUtilsStub timeUtils = TimeUtilsStub
        .threadSafe(new AtomicActionPerformer())
        .withCurrentTime(instantAtMidnight);
    private final FluentExecutor executor = new FluentExecutor();

    @After
    public void tearUpStartedCallables() {
        final int enoughSecondsForWaitersToFinish = 3;
        timeUtils.tick(seconds(enoughSecondsForWaitersToFinish));
    }

    @Test
    public void shouldPollElementSuccessfullyIfItWasAddedEvenBeforeStartedPollingForIt() {
        val queue = new SignallingOnPollingQueue(timeUtils, createWaiterForEvents());

        queue.add(element);

        assertEquals(element, queue.poll(1, TimeUnit.SECONDS));
    }

    @Test
    public void shouldRemoveElementAfterSuccessfulPolling() {
        val queue = new SignallingOnPollingQueue(timeUtils, createWaiterForEvents());
        queue.add(element);

        queue.poll(1, TimeUnit.SECONDS);

        assertThat(queue).isEmpty();
    }

    @Test
    public void shouldPollElementSuccessfullyIfItWasAddedWithing1SecondFromStartedPollingForIt() {
        val queue = new SignallingOnPollingQueue(timeUtils, createWaiterForEvents());

        executor.executeInAnotherThread(() -> {
            queue.getWaiterForStartingToPoll().await(1, TimeUnit.SECONDS);
            queue.add(element);
        });

        assertEquals(element, queue.poll(1, TimeUnit.SECONDS));
    }

    @Test
    public void shouldFailToPollIfElementWasNotInsertedInOneSecond() {
        val queue = new SignallingOnPollingQueue(timeUtils, createWaiterForEvents());

        executor.executeInAnotherThread(() -> {
            queue.getWaiterForStartingToPoll().await(1, TimeUnit.SECONDS);
            timeUtils.tick(seconds(2));
        });

        assertThat(queue.poll(1, TimeUnit.SECONDS)).isNull();
    }

    @Test
    public void shouldSignalOnQueueStartedPolling() throws InterruptedException {
        val queue = new SignallingOnPollingQueue(timeUtils, createWaiterForEvents());

        executor.executeInAnotherThread(() -> queue.poll(2, TimeUnit.SECONDS));

        assertEquals(EVENT_HAPPENED, queue.getWaiterForStartingToPoll().await(1, TimeUnit.SECONDS));
    }

    @Test
    public void shouldNotSignalQueueStartedPollingWhenItDidNot() {
        val queue = new SignallingOnPollingQueue(timeUtils, createWaiterForEvents());

        assertEquals(EVENT_NOT_HAPPENED, queue.getWaiterForStartingToPoll().getWaitingStatus());
    }

    @Test
    public void pollingStartTimeShouldBeRecordedBeforeEventWasEmittedToAvoidRaceCondition() {
        val waiterForPollingToStart = mock(WaiterForEvents.class);
        val timeUtilsMock = mock(TimeUtils.class);
        val queue = new SignallingOnPollingQueue(timeUtilsMock, waiterForPollingToStart);

        queue.poll(0, TimeUnit.SECONDS);

        val inOrder = inOrder(timeUtilsMock, waiterForPollingToStart);
        inOrder.verify(timeUtilsMock).now();
        inOrder.verify(waiterForPollingToStart).markEventHappened();
        inOrder.verify(timeUtilsMock, atLeast(0)).now();
    }
}
