/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */
package com.sportradar.unifiedodds.sdk.internal.common.telemetry;

import com.google.common.base.Preconditions;
import io.opentelemetry.api.OpenTelemetry;
import io.opentelemetry.api.metrics.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.function.Consumer;

public class TelemetryImpl implements Telemetry {

    private final ConcurrentMap<MeterDescriptor, LongHistogram> histograms;
    private final Meter sdkMeter;

    public TelemetryImpl(OpenTelemetry openTelemetry, String instrumentationScopeName, String sdkVersion) {
        Preconditions.checkNotNull(openTelemetry, "openTelemetry cannot be a null reference");
        Preconditions.checkNotNull(
            instrumentationScopeName,
            "instrumentationScopeName cannot be a null reference"
        );
        Preconditions.checkNotNull(sdkVersion, "sdkVersion cannot be a null reference");
        sdkMeter =
            openTelemetry
                .meterBuilder(instrumentationScopeName)
                .setInstrumentationVersion(sdkVersion)
                .build();
        histograms = new ConcurrentHashMap<>();
    }

    @SuppressWarnings("ReturnCount")
    @Override
    public LongHistogram getHistogram(MeterDescriptor meter) {
        return histograms.computeIfAbsent(meter, this::getLongHistogram);
    }

    @Override
    public ObservableLongGauge getGauge(MeterDescriptor gauge, Consumer<ObservableLongMeasurement> callback) {
        return sdkMeter
            .gaugeBuilder(gauge.getId())
            .setDescription(gauge.getDescription())
            .ofLongs()
            .buildWithCallback(callback);
    }

    private LongHistogram getLongHistogram(MeterDescriptor histogram) {
        return sdkMeter
            .histogramBuilder(histogram.getId())
            .setDescription(histogram.getDescription())
            .setUnit("ms")
            .ofLongs()
            .build();
    }
}
