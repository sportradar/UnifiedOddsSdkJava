/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */
package com.sportradar.unifiedodds.sdk.common.internal;

import com.sportradar.unifiedodds.sdk.internal.common.telemetry.MeterDescriptor;
import io.opentelemetry.api.OpenTelemetry;
import io.opentelemetry.api.metrics.MeterProvider;
import io.opentelemetry.api.trace.TracerProvider;
import io.opentelemetry.context.propagation.ContextPropagators;
import io.opentelemetry.sdk.OpenTelemetrySdk;
import io.opentelemetry.sdk.metrics.SdkMeterProvider;
import io.opentelemetry.sdk.metrics.data.MetricData;
import io.opentelemetry.sdk.testing.exporter.InMemoryMetricReader;

public class ObservableOpenTelemetry implements OpenTelemetry {

    private final InMemoryMetricReader reader;
    private final OpenTelemetry openTelemetry;

    public ObservableOpenTelemetry() {
        reader = InMemoryMetricReader.create();
        openTelemetry =
            OpenTelemetrySdk
                .builder()
                .setMeterProvider(SdkMeterProvider.builder().registerMetricReader(reader).build())
                .build();
    }

    @Override
    public TracerProvider getTracerProvider() {
        return openTelemetry.getTracerProvider();
    }

    @Override
    public ContextPropagators getPropagators() {
        return openTelemetry.getPropagators();
    }

    @Override
    public MeterProvider getMeterProvider() {
        return openTelemetry.getMeterProvider();
    }

    public static ObservableOpenTelemetry create() {
        return new ObservableOpenTelemetry();
    }

    public MetricDataAssert verify(MeterDescriptor meter) {
        return MetricDataAssert.assertThat(getMetricData(meter));
    }

    private MetricData getMetricData(MeterDescriptor meter) {
        return reader
            .collectAllMetrics()
            .stream()
            .filter(m -> m.getName().equals(meter.getId()))
            .findFirst()
            .orElseThrow(() -> new AssertionError("No metric data found for  " + meter.getId()));
    }
}
