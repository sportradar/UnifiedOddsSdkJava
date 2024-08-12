/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */
package com.sportradar.utils.time;

import static com.sportradar.utils.time.TimeInterval.minutes;
import static org.junit.Assert.assertEquals;

import org.junit.jupiter.api.Test;

@SuppressWarnings({ "MagicNumber", "StaticVariableName" })
public final class EpochMillisTest {

    public static final long SECONDS_IN_MINUTE = 60L;
    public static final long SECOND = 1000L;
    public static final long MINUTE = SECONDS_IN_MINUTE * SECOND;

    private static long MIDNIGHT = 1664402400000L;

    private EpochMillisTest() {}

    @Test
    public void shouldAdd0Minutes() {
        assertEquals(MIDNIGHT, new EpochMillis(MIDNIGHT).plus(minutes(0)).get());
    }

    @Test
    public void shouldAdd2Minutes() {
        assertEquals(MIDNIGHT + 2L * MINUTE, new EpochMillis(MIDNIGHT).plus(minutes(2)).get());
    }

    @Test
    public void shouldSubtract0Minutes() {
        assertEquals(MIDNIGHT, new EpochMillis(MIDNIGHT).minus(minutes(0)).get());
    }

    @Test
    public void shouldSubtract2Minutes() {
        assertEquals(MIDNIGHT - 2L * MINUTE, new EpochMillis(MIDNIGHT).minus(minutes(2)).get());
    }

    @Test
    public void shouldBeImmutable() {
        EpochMillis midnight = new EpochMillis(MIDNIGHT);
        assertEquals(MIDNIGHT + MINUTE, midnight.plus(minutes(1)).get());
        assertEquals(MIDNIGHT + MINUTE, midnight.plus(minutes(1)).get());
    }
}
