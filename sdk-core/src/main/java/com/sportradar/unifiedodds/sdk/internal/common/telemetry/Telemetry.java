/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */
package com.sportradar.unifiedodds.sdk.internal.common.telemetry;

import io.opentelemetry.api.metrics.LongHistogram;
import io.opentelemetry.api.metrics.ObservableLongGauge;
import io.opentelemetry.api.metrics.ObservableLongMeasurement;
import java.util.function.Consumer;

public interface Telemetry {
    LongHistogram getHistogram(MeterDescriptor meter);

    ObservableLongGauge getGauge(MeterDescriptor meter, Consumer<ObservableLongMeasurement> callback);
}
