/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */
package com.sportradar.unifiedodds.sdk.testutil.generic.concurrent;

import static com.sportradar.utils.time.TimeInterval.seconds;
import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;

import com.sportradar.unifiedodds.sdk.impl.TimeUtils;
import com.sportradar.utils.time.TimeUtilsStub;
import java.time.Instant;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import lombok.val;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;

public class LatchTest {

    public static final int ALL_TESTS_FINISH_INSTANTLY_BUT_STILL_GIVING_ENOUGH_TIME_TO_FINISH = 3;

    private static final long MIDNIGHT_TIMESTAMP_MILLIS = 1664402400000L;

    private final TimeUtilsStub.Factory timeUtilsStubs = TimeUtilsStub.threadSafe(
        new AtomicActionPerformer()
    );

    @Test
    @Timeout(ALL_TESTS_FINISH_INSTANTLY_BUT_STILL_GIVING_ENOUGH_TIME_TO_FINISH)
    public void shouldSucceedIfNotTimedOut() throws InterruptedException {
        Instant instantAtMidnight = Instant.ofEpochMilli(MIDNIGHT_TIMESTAMP_MILLIS);
        val timeUtils = timeUtilsStubs.withCurrentTime(instantAtMidnight);
        val latch = new Latch(1, timeUtils);

        Executors.newFixedThreadPool(1).submit(() -> latch.countDown());

        assertTrue(latch.await(1, TimeUnit.SECONDS));
    }

    @Test
    @Timeout(ALL_TESTS_FINISH_INSTANTLY_BUT_STILL_GIVING_ENOUGH_TIME_TO_FINISH)
    public void shouldFailIfTimedOut() {
        Instant instantAtMidnight = Instant.ofEpochMilli(MIDNIGHT_TIMESTAMP_MILLIS);
        val timeUtils = timeUtilsStubs.withCurrentTime(instantAtMidnight);
        val latch = new Latch(1, timeUtils);

        Executors.newFixedThreadPool(1).submit(() -> timeUtils.tick(seconds(2)));

        assertFalse(latch.await(1, TimeUnit.SECONDS));
    }

    @Test
    @Timeout(ALL_TESTS_FINISH_INSTANTLY_BUT_STILL_GIVING_ENOUGH_TIME_TO_FINISH)
    public void shouldIndicateLatchHasStartedWaiting() throws InterruptedException {
        Instant instantAtMidnight = Instant.ofEpochMilli(MIDNIGHT_TIMESTAMP_MILLIS);
        val timeUtils = timeUtilsStubs.withCurrentTime(instantAtMidnight);
        val latch = new Latch(1, timeUtils);

        Executors.newFixedThreadPool(1).submit(() -> latch.await(2, TimeUnit.SECONDS));

        assertTrue(latch.getLatchForWaitingStarted().await(1, TimeUnit.SECONDS));
        tearUpStartedCallables(timeUtils);
    }

    private void tearUpStartedCallables(TimeUtilsStub timeUtils) {
        int enoughSecondsForCallablesToFinish = 2;
        timeUtils.tick(seconds(enoughSecondsForCallablesToFinish));
    }

    @Test
    @Timeout(ALL_TESTS_FINISH_INSTANTLY_BUT_STILL_GIVING_ENOUGH_TIME_TO_FINISH)
    public void shouldNotIndicateLatchHasStartedWaitingWhenItIsNot() {
        val latch = new Latch(1, mock(TimeUtils.class));

        assertEquals(1, latch.getLatchForWaitingStarted().getCount());
    }
}
