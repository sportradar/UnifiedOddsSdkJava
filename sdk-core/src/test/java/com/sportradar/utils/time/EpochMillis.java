package com.sportradar.utils.time;

@SuppressWarnings({ "MagicNumber" })
public class EpochMillis {

    private long epochMillis;

    public EpochMillis(long epochMillis) {
        this.epochMillis = epochMillis;
    }

    public EpochMillis plus(TimeInterval interval) {
        return new EpochMillis(epochMillis + interval.getInMillis());
    }

    public long get() {
        return epochMillis;
    }

    public EpochMillis minus(TimeInterval interval) {
        return new EpochMillis(epochMillis - interval.getInMillis());
    }

    public EpochMillis plusSeconds(int amount) {
        return new EpochMillis(epochMillis + amount * 1000L);
    }

    public EpochMillis minusSeconds(int amount) {
        return new EpochMillis(epochMillis - amount * 1000L);
    }
}
