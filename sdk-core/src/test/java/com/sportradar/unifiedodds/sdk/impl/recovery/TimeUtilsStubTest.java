/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */
package com.sportradar.unifiedodds.sdk.impl.recovery;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;

import com.sportradar.unifiedodds.sdk.testutil.generic.concurrent.AtomicActionPerformer;
import java.time.Instant;
import lombok.val;
import org.junit.Test;

public class TimeUtilsStubTest {

    private static final long MIDNIGHT_TIMESTAMP_MILLIS = 1664402400000L;
    private static final long MIDNIGHT_PLUS_1_SEC_MILLIS = 1664402400000L + 1000L;

    @Test
    public void shouldRespectStubbedTime() {
        Instant instant = Instant.ofEpochMilli(MIDNIGHT_TIMESTAMP_MILLIS);
        TimeUtilsStub timeUtils = TimeUtilsStub
            .threadSafe(new AtomicActionPerformer())
            .withCurrentTime(instant);

        assertEquals(instant, timeUtils.nowInstant());
        assertEquals(instant.toEpochMilli(), timeUtils.now());
    }

    @Test
    public void shouldMoveTime1SecondForward() {
        Instant instant = Instant.ofEpochMilli(MIDNIGHT_TIMESTAMP_MILLIS);
        TimeUtilsStub timeUtils = TimeUtilsStub
            .threadSafe(new AtomicActionPerformer())
            .withCurrentTime(instant);

        timeUtils.fastForwardSeconds(1);

        val expectedInstant = Instant.ofEpochMilli(MIDNIGHT_PLUS_1_SEC_MILLIS);
        assertEquals(expectedInstant, timeUtils.nowInstant());
        assertEquals(expectedInstant.toEpochMilli(), timeUtils.now());
    }

    @Test
    public void shouldEnsureStateModificationIsSynchronisedWhenFastForwarding() {
        Instant instant = Instant.ofEpochMilli(MIDNIGHT_TIMESTAMP_MILLIS);
        AtomicActionPerformer noOpAction = mock(AtomicActionPerformer.class);
        TimeUtilsStub timeUtils = TimeUtilsStub.threadSafe(noOpAction).withCurrentTime(instant);

        timeUtils.fastForwardSeconds(1);

        val expectedInstantNotChanged = instant;
        assertEquals(expectedInstantNotChanged, timeUtils.nowInstant());
        assertEquals(expectedInstantNotChanged.toEpochMilli(), timeUtils.now());
    }

    @Test
    public void timeTravelingShouldChangeCurrentTime() {
        Instant instant = Instant.ofEpochMilli(MIDNIGHT_TIMESTAMP_MILLIS);
        Instant newTimeInstant = Instant.ofEpochMilli(MIDNIGHT_PLUS_1_SEC_MILLIS);
        TimeUtilsStub time = TimeUtilsStub.threadSafe(new AtomicActionPerformer()).withCurrentTime(instant);

        time.travelTo(newTimeInstant);

        val expectedInstant = Instant.ofEpochMilli(MIDNIGHT_PLUS_1_SEC_MILLIS);
        assertEquals(expectedInstant, time.nowInstant());
        assertEquals(expectedInstant.toEpochMilli(), time.now());
    }

    @Test
    public void shouldEnsureStateModificationIsSynchronisedWhenTimeTravelling() {
        Instant instant = Instant.ofEpochMilli(MIDNIGHT_TIMESTAMP_MILLIS);
        Instant newTimeInstant = Instant.ofEpochMilli(MIDNIGHT_PLUS_1_SEC_MILLIS);
        AtomicActionPerformer noOpAction = mock(AtomicActionPerformer.class);
        TimeUtilsStub time = TimeUtilsStub.threadSafe(noOpAction).withCurrentTime(instant);

        time.travelTo(newTimeInstant);

        val expectedInstantNotChanged = instant;
        assertEquals(expectedInstantNotChanged, time.nowInstant());
        assertEquals(expectedInstantNotChanged.toEpochMilli(), time.now());
    }
}
