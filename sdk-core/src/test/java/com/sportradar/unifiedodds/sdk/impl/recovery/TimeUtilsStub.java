package com.sportradar.unifiedodds.sdk.impl.recovery;

import com.sportradar.unifiedodds.sdk.impl.TimeUtils;

import java.time.Instant;

public class TimeUtilsStub implements TimeUtils {

    private Instant instant;

    private TimeUtilsStub (Instant instant) {
        this.instant = instant;
    }

    static TimeUtilsStub withCurrentTime(Instant instant) {
        return new TimeUtilsStub(instant);
    }

    @Override
    public long now() {
        return instant.toEpochMilli();
    }

    @Override
    public Instant nowInstant() {
        return instant;
    }
}
