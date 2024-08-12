/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */
package com.sportradar.utils.time;

import static com.sportradar.utils.time.TimeInterval.minutes;
import static com.sportradar.utils.time.TimeInterval.seconds;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.Assert.assertEquals;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

@SuppressWarnings({ "MagicNumber", "StaticVariableName" })
public final class TimeIntervalTest {

    public static final long SECONDS_IN_MINUTE = 60L;
    public static final long MILLIS_IN_SECOND = 1000L;

    private static long MIDNIGHT = 1664402400000L;

    private TimeIntervalTest() {}

    @Nested
    public class Minutes {

        public static final long MILLIS_IN_MINUTE = SECONDS_IN_MINUTE * MILLIS_IN_SECOND;

        @Test
        public void getsUnderlyingValue() {
            assertEquals(MILLIS_IN_MINUTE, minutes(1).getInMillis());
        }

        @Test
        public void adds0() {
            assertEquals(MILLIS_IN_MINUTE, minutes(1).plus(minutes(0)).getInMillis());
        }

        @Test
        public void adds2() {
            assertEquals(3L * MILLIS_IN_MINUTE, minutes(1).plus(minutes(2)).getInMillis());
        }

        @Test
        public void subtracts0() {
            assertEquals(3L * MILLIS_IN_MINUTE, minutes(3).minus(minutes(0)).getInMillis());
        }

        @Test
        public void subtracts2() {
            assertEquals(MILLIS_IN_MINUTE, minutes(3).minus(minutes(2)).getInMillis());
        }

        @Test
        public void allowsToHaveIntervalEqualToPointInTime() {
            assertEquals(0, minutes(3).minus(minutes(3)).getInMillis());
        }

        @Test
        public void preventsIntervalFromBecomingNegative() {
            assertThatThrownBy(() -> minutes(3).minus(minutes(4)))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Interval cannot be negative");
        }

        @Test
        public void shouldBeImmutable() {
            TimeInterval interval = minutes(1);
            assertEquals(2L * MILLIS_IN_SECOND * SECONDS_IN_MINUTE, interval.plus(minutes(1)).getInMillis());
            assertEquals(2L * MILLIS_IN_SECOND * SECONDS_IN_MINUTE, interval.plus(minutes(1)).getInMillis());
        }
    }

    @Nested
    public class Seconds {

        @Test
        public void getsUnderlyingValue() {
            assertEquals(MILLIS_IN_SECOND, seconds(1).getInMillis());
        }

        @Test
        public void adds0() {
            assertEquals(MILLIS_IN_SECOND, seconds(1).plus(seconds(0)).getInMillis());
        }

        @Test
        public void adds2() {
            assertEquals(3L * MILLIS_IN_SECOND, seconds(1).plus(seconds(2)).getInMillis());
        }

        @Test
        public void subtracts0() {
            assertEquals(3L * MILLIS_IN_SECOND, seconds(3).minus(seconds(0)).getInMillis());
        }

        @Test
        public void subtracts2() {
            assertEquals(MILLIS_IN_SECOND, seconds(3).minus(seconds(2)).getInMillis());
        }

        @Test
        public void allowsToHaveIntervalEqualToPointInTime() {
            assertEquals(0, seconds(3).minus(seconds(3)).getInMillis());
        }

        @Test
        public void preventsIntervalFromBecomingNegative() {
            assertThatThrownBy(() -> seconds(3).minus(seconds(4)))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Interval cannot be negative");
        }

        @Test
        public void shouldBeImmutable() {
            TimeInterval interval = seconds(1);
            assertEquals(2L * MILLIS_IN_SECOND, interval.plus(seconds(1)).getInMillis());
            assertEquals(2L * MILLIS_IN_SECOND, interval.plus(seconds(1)).getInMillis());
        }
    }
}
