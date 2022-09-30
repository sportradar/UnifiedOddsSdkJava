package com.sportradar.unifiedodds.sdk.impl.rabbitconnection;

public class EpochMillis {
    private long epochMillis;

    public EpochMillis(long epochMillis) {
        this.epochMillis = epochMillis;
    }

    public long plusMinutes(int amount) {
        return epochMillis + 60L * amount * 1000L;
    }

    public long get() {
        return epochMillis;
    }

    public long minusMinutes(int amount) {
        return epochMillis - 60L * amount * 1000L;
    }
}
