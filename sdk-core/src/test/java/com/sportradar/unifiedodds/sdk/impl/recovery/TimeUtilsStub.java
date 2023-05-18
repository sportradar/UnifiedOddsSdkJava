/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */
package com.sportradar.unifiedodds.sdk.impl.recovery;

import com.sportradar.unifiedodds.sdk.impl.TimeUtils;
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

    public void fastForwardSeconds(int amount) {
        atomicActionPerformer.perform(() -> instant = instant.plus(amount, ChronoUnit.SECONDS));
    }

    public void travelTo(final Instant newInstant) {
        atomicActionPerformer.perform(() -> instant = newInstant);
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
