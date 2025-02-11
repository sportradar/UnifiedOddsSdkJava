/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */
package com.sportradar.unifiedodds.sdk.common.internal;

import static com.sportradar.unifiedodds.sdk.internal.common.telemetry.TelemetryFactory.GaugeValue.gaugeValue;
import static java.util.Collections.emptyMap;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;

import com.google.common.collect.ImmutableMap;
import com.sportradar.unifiedodds.sdk.di.UsageTelemetryFactories;
import com.sportradar.unifiedodds.sdk.internal.common.telemetry.LatencyTracker;
import com.sportradar.unifiedodds.sdk.internal.common.telemetry.LongSdkHistogram;
import com.sportradar.unifiedodds.sdk.internal.common.telemetry.UsageGauge;
import com.sportradar.unifiedodds.sdk.testutil.generic.concurrent.AtomicActionPerformer;
import com.sportradar.utils.time.TimeInterval;
import com.sportradar.utils.time.TimeUtilsStub;
import java.io.IOException;
import java.time.Instant;
import java.util.concurrent.atomic.AtomicLong;
import lombok.val;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

@SuppressWarnings({ "ClassFanOutComplexity", "MagicNumber", "MultipleStringLiterals" })
class TelemetryFactoryWithInMemoryOpenTelemetryTest {

    private TimeUtilsStub timeUtils;

    @BeforeEach
    void setUp() {
        timeUtils = TimeUtilsStub.threadSafe(new AtomicActionPerformer()).withCurrentTime(Instant.now());
    }

    @Nested
    class Histograms {

        @Test
        void histogramExecutedWithoutHistogramTypeShouldThrow() {
            val factory = UsageTelemetryFactories.createInstance().build();

            assertThatExceptionOfType(NullPointerException.class)
                .isThrownBy(() -> factory.latencyHistogram(null));
        }

        @ParameterizedTest
        @EnumSource(LongSdkHistogram.class)
        void histogramIsNotUpdatedWhenCloseIsCalledAgain(LongSdkHistogram histogram) {
            val observableTelemetry = ObservableOpenTelemetry.create();
            val factory = UsageTelemetryFactories
                .createInstance()
                .withTimeUtils(timeUtils)
                .withOpenTelemetry(observableTelemetry)
                .build();

            val latencyTracker = factory.latencyHistogram(histogram);

            timeUtils.tick(TimeInterval.milliseconds(23));
            latencyTracker.close();

            observableTelemetry.verify(histogram).hasLongValue(23);

            timeUtils.tick(TimeInterval.milliseconds(2000));
            latencyTracker.close();

            observableTelemetry.verify(histogram).hasLongValue(23);
        }

        @ParameterizedTest
        @EnumSource(LongSdkHistogram.class)
        void histogramRecordsExecutionTime(LongSdkHistogram histogram) {
            val observableTelemetry = ObservableOpenTelemetry.create();
            val factory = UsageTelemetryFactories
                .createInstance()
                .withTimeUtils(timeUtils)
                .withOpenTelemetry(observableTelemetry)
                .build();

            val latencyTracker = factory.latencyHistogram(histogram);
            timeUtils.tick(TimeInterval.milliseconds(200));
            latencyTracker.close();

            observableTelemetry
                .verify(histogram)
                .hasName(histogram.getId())
                .hasDescription(histogram.getDescription())
                .hasLongValue(200);
        }

        @ParameterizedTest
        @EnumSource(LongSdkHistogram.class)
        void histogramCanIncludeAttributes(LongSdkHistogram histogram) {
            val observableTelemetry = ObservableOpenTelemetry.create();
            val factory = UsageTelemetryFactories
                .createInstance()
                .withTimeUtils(timeUtils)
                .withOpenTelemetry(observableTelemetry)
                .build();

            val latencyTracker = factory.latencyHistogram(
                histogram,
                ImmutableMap.of("tag-key", "some-value", "other-key", "other-value")
            );
            latencyTracker.close();

            observableTelemetry
                .verify(histogram)
                .theOnlyOneDataPoint()
                .hasAttributes("tag-key", "some-value", "other-key", "other-value");
        }

        @ParameterizedTest
        @EnumSource(LongSdkHistogram.class)
        void histogramRecordsExecutionTimeEvenWithUncheckedException(LongSdkHistogram histogram) {
            val observableTelemetry = ObservableOpenTelemetry.create();
            val factory = UsageTelemetryFactories
                .createInstance()
                .withTimeUtils(timeUtils)
                .withOpenTelemetry(observableTelemetry)
                .build();

            int executionTime = 100;
            assertThatExceptionOfType(IllegalStateException.class)
                .isThrownBy(() -> {
                    try (LatencyTracker ignored = factory.latencyHistogram(histogram)) {
                        timeUtils.tick(TimeInterval.milliseconds(executionTime));
                        throw new IllegalStateException("test-exception");
                    }
                });

            observableTelemetry.verify(histogram).hasLongValue(executionTime);
        }

        @ParameterizedTest
        @EnumSource(LongSdkHistogram.class)
        void histogramRecordsExecutionTimeEvenWithCheckedException(LongSdkHistogram histogram) {
            val observableTelemetry = ObservableOpenTelemetry.create();
            val factory = UsageTelemetryFactories
                .createInstance()
                .withTimeUtils(timeUtils)
                .withOpenTelemetry(observableTelemetry)
                .build();

            int executionTime = 433;
            assertThatExceptionOfType(IOException.class)
                .isThrownBy(() -> {
                    try (LatencyTracker ignored = factory.latencyHistogram(histogram)) {
                        timeUtils.tick(TimeInterval.milliseconds(executionTime));
                        throw new IOException("test-exception");
                    }
                });

            observableTelemetry.verify(histogram).hasLongValue(executionTime);
        }
    }

    @Nested
    class Gauges {

        @ParameterizedTest
        @EnumSource(UsageGauge.class)
        void gaugeRecordsValueChangesFromOneMetricReadToAnother(UsageGauge gauge) {
            val observableTelemetry = ObservableOpenTelemetry.create();
            val factory = UsageTelemetryFactories
                .createInstance()
                .withTimeUtils(timeUtils)
                .withOpenTelemetry(observableTelemetry)
                .build();

            val observed = new AtomicLong(0);

            factory.gauge(gauge, () -> gaugeValue(observed.get(), emptyMap()));

            observableTelemetry.verify(gauge).hasLongValue(0);

            observed.incrementAndGet();
            observed.incrementAndGet();

            observableTelemetry.verify(gauge).hasLongValue(2);
        }

        @ParameterizedTest
        @EnumSource(UsageGauge.class)
        void gaugeCanIncludeAttributes(UsageGauge gauge) {
            val observableTelemetry = ObservableOpenTelemetry.create();
            val factory = UsageTelemetryFactories
                .createInstance()
                .withTimeUtils(timeUtils)
                .withOpenTelemetry(observableTelemetry)
                .build();

            val observed = new AtomicLong(0);

            factory.gauge(
                gauge,
                () ->
                    gaugeValue(
                        observed.get(),
                        ImmutableMap.of("first-key", "first-value", "second-key", "second-value")
                    )
            );

            observableTelemetry
                .verify(gauge)
                .theOnlyOneDataPoint()
                .hasAttributes("first-key", "first-value", "second-key", "second-value");
        }

        @Test
        void differentGaugesCanReportValuesWithDifferentTags() throws Exception {
            val observableTelemetry = ObservableOpenTelemetry.create();
            val factory = UsageTelemetryFactories
                .createInstance()
                .withTimeUtils(timeUtils)
                .withOpenTelemetry(observableTelemetry)
                .build();

            factory.gauge(
                UsageGauge.PRODUCER_STATUS,
                () -> gaugeValue(1L, ImmutableMap.of("first-key", "first-value"))
            );

            factory.gauge(
                UsageGauge.PRODUCER_STATUS,
                () -> gaugeValue(2L, ImmutableMap.of("second-key", "second-value"))
            );

            observableTelemetry
                .verify(UsageGauge.PRODUCER_STATUS)
                .theOnlyDataPointWithAttributes("first-key", "first-value")
                .hasLongValue(1L);

            observableTelemetry
                .verify(UsageGauge.PRODUCER_STATUS)
                .theOnlyDataPointWithAttributes("second-key", "second-value")
                .hasLongValue(2L);
        }
    }
}
