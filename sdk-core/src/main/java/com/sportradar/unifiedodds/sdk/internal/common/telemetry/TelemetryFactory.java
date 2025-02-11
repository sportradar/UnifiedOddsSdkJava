/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */
package com.sportradar.unifiedodds.sdk.internal.common.telemetry;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableMap;
import com.sportradar.unifiedodds.sdk.internal.impl.TimeUtils;
import io.opentelemetry.api.common.Attributes;
import io.opentelemetry.api.common.AttributesBuilder;
import io.opentelemetry.api.metrics.LongHistogram;
import io.opentelemetry.api.metrics.ObservableLongGauge;
import java.util.Map;
import java.util.function.Supplier;

public class TelemetryFactory {

    private final TimeUtils timeUtils;
    private final Telemetry telemetry;

    public TelemetryFactory(Telemetry telemetry, TimeUtils timeUtils) {
        Preconditions.checkNotNull(telemetry);
        Preconditions.checkNotNull(timeUtils);

        this.telemetry = telemetry;
        this.timeUtils = timeUtils;
    }

    public LatencyTracker latencyHistogram(MeterDescriptor meter) {
        return latencyHistogram(meter, Attributes.empty());
    }

    public LatencyTracker latencyHistogram(MeterDescriptor meter, String tagKey, String tagValue) {
        Preconditions.checkNotNull(tagKey);

        Attributes tags = Attributes.builder().put(tagKey, tagValue).build();

        return latencyHistogram(meter, tags);
    }

    public LatencyTracker latencyHistogram(MeterDescriptor meter, Map<String, String> mapTags) {
        Attributes tags = getAttributesFromMap(mapTags);
        return latencyHistogram(meter, tags);
    }

    private LatencyTracker latencyHistogram(MeterDescriptor meter, Attributes tags) {
        Preconditions.checkNotNull(meter, "meter type cannot be null");
        LongHistogram telemetryHistogram = telemetry.getHistogram(meter);
        return new LatencyTracker(telemetryHistogram, tags, timeUtils);
    }

    private Attributes getAttributesFromMap(Map<String, String> mapTags) {
        AttributesBuilder builder = Attributes.builder();
        mapTags.forEach(builder::put);
        return builder.build();
    }

    public ObservableLongGauge gauge(MeterDescriptor meter, Supplier<GaugeValue> valueReader) {
        return telemetry.getGauge(
            meter,
            result -> {
                GaugeValue gaugeValue = valueReader.get();
                Attributes attributes = getAttributesFromMap(gaugeValue.tags);
                result.record(gaugeValue.value, attributes);
            }
        );
    }

    public static final class GaugeValue {

        private final Long value;
        private final Map<String, String> tags;

        private GaugeValue(Long value, Map<String, String> tags) {
            this.value = value;
            this.tags = ImmutableMap.copyOf(tags);
        }

        public static GaugeValue gaugeValue(Long value, Map<String, String> tags) {
            return new GaugeValue(value, tags);
        }
    }
}
