/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */
package com.sportradar.utils.time;

import com.sportradar.unifiedodds.sdk.internal.impl.TimeUtils;
import com.sportradar.unifiedodds.sdk.testutil.generic.concurrent.AtomicActionPerformer;
import java.time.Instant;
import java.time.temporal.ChronoUnit;

public class TimeUtilsStub implements TimeUtils {

    private final AtomicActionPerformer atomicActionPerformer;
    private Instant instant;

    private TimeUtilsStub(final AtomicActionPerformer atomicActionPerformer, final Instant instant) {
        this.atomicActionPerformer = atomicActionPerformer;
        this.instant = instant;
    }

    public static Factory threadSafe(final AtomicActionPerformer atomicActionPerformer) {
        return new Factory(atomicActionPerformer);
    }

    @Override
    public long now() {
        return instant.toEpochMilli();
    }

    @Override
    public Instant nowInstant() {
        return instant;
    }

    public void tick(TimeInterval interval) {
        atomicActionPerformer.perform(() -> instant = instant.plus(interval.getInMillis(), ChronoUnit.MILLIS)
        );
    }

    public void tick() {
        atomicActionPerformer.perform(() -> instant = instant.plus(1, ChronoUnit.MILLIS));
    }

    public void travelTo(final Instant newInstant) {
        atomicActionPerformer.perform(() -> instant = newInstant);
    }

    public void travelTo(final long epochMillis) {
        atomicActionPerformer.perform(() -> instant = Instant.ofEpochMilli(epochMillis));
    }

    public void travelTo(final EpochMillis epochMillis) {
        atomicActionPerformer.perform(() -> instant = Instant.ofEpochMilli(epochMillis.get()));
    }

    public static class Factory {

        private final AtomicActionPerformer atomicActionPerformer;

        private Factory(final AtomicActionPerformer atomicActionPerformer) {
            this.atomicActionPerformer = atomicActionPerformer;
        }

        public TimeUtilsStub withCurrentTime(Instant instant) {
            return new TimeUtilsStub(atomicActionPerformer, instant);
        }
    }
}
