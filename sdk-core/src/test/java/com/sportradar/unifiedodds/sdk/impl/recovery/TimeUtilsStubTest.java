package com.sportradar.unifiedodds.sdk.impl.recovery;

import org.junit.Test;

import java.time.Instant;

import static org.junit.Assert.assertEquals;

public class TimeUtilsStubTest {
    @Test
    public void shouldRespectStubbedTime() {
        Instant instant = Instant.now();
        TimeUtilsStub timeUtils = TimeUtilsStub.withCurrentTime(instant);

        assertEquals(instant, timeUtils.nowInstant());
        assertEquals(instant.toEpochMilli(), timeUtils.now());
    }
}
