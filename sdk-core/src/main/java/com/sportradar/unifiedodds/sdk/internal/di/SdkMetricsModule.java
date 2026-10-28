/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */

package com.sportradar.unifiedodds.sdk.internal.di;

import com.google.inject.Binder;
import com.google.inject.Module;
import com.google.inject.Provides;
import com.google.inject.Singleton;
import com.google.inject.name.Named;
import com.sportradar.unifiedodds.sdk.internal.common.telemetry.TelemetryFactory;
import com.sportradar.unifiedodds.sdk.internal.common.telemetry.TelemetryImpl;
import com.sportradar.unifiedodds.sdk.internal.impl.TimeUtilsImpl;
import io.opentelemetry.api.GlobalOpenTelemetry;

public class SdkMetricsModule implements Module {

    @Override
    public void configure(Binder binder) {
        // nothing needed here
    }

    /**
     * Returns the TelemetryFactory object used by the SDK to publish internal metrics (to GlobalOpenTelemetry)
     *
     * @return the TelemetryFactory object used by the SDK to publish internal metrics (to GlobalOpenTelemetry)
     */
    @Provides
    @Singleton
    @Named("InternalSdkTelemetryFactory")
    private TelemetryFactory provideInternalSdkTelemetryFactory(@Named("version") String sdkVersion) {
        String instrumentationScopeName = "UofSdk-Java";
        TelemetryImpl uofSdkTelemetry = new TelemetryImpl(
            GlobalOpenTelemetry.get(),
            instrumentationScopeName,
            sdkVersion
        );
        return new TelemetryFactory(uofSdkTelemetry, new TimeUtilsImpl());
    }
}
