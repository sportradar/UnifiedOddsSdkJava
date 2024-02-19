/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */
package com.sportradar.utils.time;

public class TimeInterval {

    private static final long MILLIS_IN_SECOND = 1000L;
    private static final long SECONDS_IN_MINUTE = 60L;
    public static final long MILLIS_IN_MINUTE = MILLIS_IN_SECOND * SECONDS_IN_MINUTE;
    private final long currentInMillis;

    private TimeInterval(final long millis) {
        if (millis < 0) {
            throw new IllegalArgumentException("Interval cannot be negative");
        }
        this.currentInMillis = millis;
    }

    public static TimeInterval seconds(int count) {
        return new TimeInterval(count * MILLIS_IN_SECOND);
    }

    public static TimeInterval minutes(int count) {
        return new TimeInterval(count * MILLIS_IN_SECOND * SECONDS_IN_MINUTE);
    }

    public long getInMillis() {
        return currentInMillis;
    }

    public TimeInterval plus(TimeInterval interval) {
        return new TimeInterval(interval.getInMillis() + currentInMillis);
    }

    public TimeInterval minus(TimeInterval interval) {
        return new TimeInterval(currentInMillis - interval.getInMillis());
    }
}
