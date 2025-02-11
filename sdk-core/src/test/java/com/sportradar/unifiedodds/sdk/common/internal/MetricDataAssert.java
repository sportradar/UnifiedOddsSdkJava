/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */
package com.sportradar.unifiedodds.sdk.common.internal;

import static io.opentelemetry.api.common.AttributeKey.stringKey;

import com.google.common.collect.Iterables;
import io.opentelemetry.sdk.metrics.data.MetricData;
import io.opentelemetry.sdk.metrics.data.PointData;
import java.util.Collection;
import java.util.Objects;
import lombok.val;
import org.assertj.core.api.AbstractAssert;

public class MetricDataAssert extends AbstractAssert<MetricDataAssert, MetricData> {

    protected MetricDataAssert(MetricData metricData, Class<?> selfType) {
        super(metricData, selfType);
    }

    public static MetricDataAssert assertThat(MetricData metricData) {
        return new MetricDataAssert(metricData, MetricDataAssert.class);
    }

    public MetricDataAssert hasName(String expected) {
        isNotNull();
        if (!actual.getName().equals(expected)) {
            failWithMessage("Expected metric data name to be <%s> but was <%s>", expected, actual.getName());
        }
        return this;
    }

    public MetricDataAssert hasDescription(String description) {
        isNotNull();
        if (!actual.getDescription().equals(description)) {
            failWithMessage(
                "Expected metric data description to be <%s> but was <%s>",
                description,
                actual.getDescription()
            );
        }
        return this;
    }

    public void hasLongValue(long expectedLongValue) {
        isNotNull();

        PointData onlyDataPoint = Iterables.getOnlyElement(actual.getData().getPoints());

        PointDataAssert.assertThat(onlyDataPoint).hasLongValue(expectedLongValue);
    }

    public PointDataAssert theOnlyOneDataPoint() {
        isNotNull();

        if (actual.getData().getPoints().size() != 1) {
            failWithMessage("Expected exactly one data point but found none.");
        }
        PointData onlyDataPoint = Iterables.getOnlyElement(actual.getData().getPoints());
        return PointDataAssert.assertThat(onlyDataPoint);
    }

    @SuppressWarnings("unchecked")
    public PointDataAssert theOnlyDataPointWithAttributes(String key, String value) {
        isNotNull();

        Collection<PointData> dataPoints = (Collection<PointData>) actual.getData().getPoints();

        val needle = dataPoints
            .stream()
            .filter(pointData -> Objects.equals(pointData.getAttributes().get(stringKey(key)), value))
            .findFirst();

        if (!needle.isPresent()) {
            failWithMessage(
                "Expected one data point with attributes {%s='%s'} to exist, but there was none",
                key,
                value
            );
        }
        return PointDataAssert.assertThat(needle.orElseThrow(IllegalStateException::new));
    }
}
