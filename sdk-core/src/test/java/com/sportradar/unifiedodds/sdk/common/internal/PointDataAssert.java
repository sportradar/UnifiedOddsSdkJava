/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */
package com.sportradar.unifiedodds.sdk.common.internal;

import static io.opentelemetry.api.common.AttributeKey.stringKey;

import io.opentelemetry.api.common.Attributes;
import io.opentelemetry.sdk.metrics.data.HistogramPointData;
import io.opentelemetry.sdk.metrics.data.LongPointData;
import io.opentelemetry.sdk.metrics.data.PointData;
import org.assertj.core.api.AbstractAssert;

public class PointDataAssert extends AbstractAssert<PointDataAssert, PointData> {

    protected PointDataAssert(PointData pointData, Class<?> selfType) {
        super(pointData, selfType);
    }

    public static PointDataAssert assertThat(PointData pointData) {
        return new PointDataAssert(pointData, PointDataAssert.class);
    }

    public PointDataAssert hasLongValue(long expectedLongValue) {
        isNotNull();

        if (actual instanceof HistogramPointData) {
            HistogramPointData point = (HistogramPointData) actual;
            if (point.getSum() != expectedLongValue) {
                failWithMessage("Expected sum to be <%s> but was <%s>", expectedLongValue, point.getSum());
            }
            return this;
        } else if (actual instanceof LongPointData) {
            LongPointData point = (LongPointData) actual;
            if (point.getValue() != expectedLongValue) {
                failWithMessage(
                    "Expected value to be <%s> but was <%s>",
                    expectedLongValue,
                    point.getValue()
                );
            }
            return this;
        } else {
            throw new IllegalStateException(actual.getClass() + " is not supported");
        }
    }

    public void hasAttributes(String key, String value, String otherKey, String otherValue) {
        isNotNull();

        Attributes expectedAttributes = Attributes.of(stringKey(key), value, stringKey(otherKey), otherValue);
        assertAttributes(expectedAttributes);
    }

    public void hasAttributes(String key, String value) {
        isNotNull();

        Attributes expectedAttributes = Attributes.of(stringKey(key), value);
        assertAttributes(expectedAttributes);
    }

    private void assertAttributes(Attributes expectedAttributes) {
        Attributes actualAttributes = actual.getAttributes();
        if (!actualAttributes.equals(expectedAttributes)) {
            failWithMessage(
                "Expected attributes to be <%s> but was <%s>",
                expectedAttributes,
                actualAttributes
            );
        }
    }
}
