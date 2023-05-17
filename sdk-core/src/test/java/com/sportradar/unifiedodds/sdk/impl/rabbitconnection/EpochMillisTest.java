package com.sportradar.unifiedodds.sdk.impl.rabbitconnection;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

@SuppressWarnings({ "MagicNumber", "StaticVariableName" })
public class EpochMillisTest {

    private static long MIDNIGHT = 1664402400000L;

    @Test
    public void shouldGetUnderlyingValue() {
        assertEquals(MIDNIGHT, new EpochMillis(MIDNIGHT).get());
    }

    @Test
    public void shouldAdd0Minutes() {
        assertEquals(MIDNIGHT, new EpochMillis(MIDNIGHT).plusMinutes(0));
    }

    @Test
    public void shouldAdd2Minutes() {
        assertEquals(MIDNIGHT + 2L * 60L * 1000L, new EpochMillis(MIDNIGHT).plusMinutes(2));
    }

    @Test
    public void shouldSubtract0Minutes() {
        assertEquals(MIDNIGHT, new EpochMillis(MIDNIGHT).minusMinutes(0));
    }

    @Test
    public void shouldSubtract2Minutes() {
        assertEquals(MIDNIGHT - 2L * 60L * 1000L, new EpochMillis(MIDNIGHT).minusMinutes(2));
    }

    @Test
    public void shouldBeImmutable() {
        EpochMillis midnight = new EpochMillis(MIDNIGHT);
        assertEquals(MIDNIGHT + 60L * 1000L, midnight.plusMinutes(1));
        assertEquals(MIDNIGHT + 60L * 1000L, midnight.plusMinutes(1));
    }
}
