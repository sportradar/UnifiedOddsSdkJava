/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */
package com.sportradar.unifiedodds.sdk.internal.common.telemetry;

import com.google.common.base.Preconditions;
import com.sportradar.unifiedodds.sdk.internal.impl.TimeUtils;
import io.opentelemetry.api.common.Attributes;
import io.opentelemetry.api.metrics.LongHistogram;
import java.time.Duration;
import java.time.Instant;

public class LatencyTracker implements AutoCloseable {

    private final Instant startTime;
    private final LongHistogram histogram;
    private final TimeUtils timeUtils;
    private final Attributes tags;
    private boolean closed;

    public LatencyTracker(LongHistogram histogram, Attributes histogramTags, TimeUtils timeUtils) {
        Preconditions.checkNotNull(histogram);
        Preconditions.checkNotNull(histogramTags);
        Preconditions.checkNotNull(timeUtils);

        this.histogram = histogram;
        this.tags = histogramTags;
        this.timeUtils = timeUtils;
        startTime = timeUtils.nowInstant();
        closed = false;
    }

    @Override
    public void close() {
        if (closed) {
            return;
        }

        Instant endTime = timeUtils.nowInstant();
        long elapsed = Duration.between(startTime, endTime).toMillis();
        histogram.record(elapsed, tags);
        closed = true;
    }
}
