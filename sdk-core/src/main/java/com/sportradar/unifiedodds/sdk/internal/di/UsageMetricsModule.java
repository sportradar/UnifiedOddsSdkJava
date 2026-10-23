/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */

package com.sportradar.unifiedodds.sdk.internal.di;

import static java.util.concurrent.TimeUnit.SECONDS;

import com.google.inject.Binder;
import com.google.inject.Module;
import com.google.inject.Provides;
import com.google.inject.Singleton;
import com.google.inject.name.Named;
import com.sportradar.unifiedodds.sdk.cfg.UofConfiguration;
import com.sportradar.unifiedodds.sdk.internal.common.telemetry.TelemetryFactory;
import com.sportradar.unifiedodds.sdk.internal.common.telemetry.TelemetryImpl;
import com.sportradar.unifiedodds.sdk.internal.impl.TimeUtilsImpl;
import io.opentelemetry.api.OpenTelemetry;
import io.opentelemetry.exporter.otlp.http.metrics.OtlpHttpMetricExporter;
import io.opentelemetry.sdk.OpenTelemetrySdk;
import io.opentelemetry.sdk.metrics.SdkMeterProvider;
import io.opentelemetry.sdk.metrics.export.AggregationTemporalitySelector;
import io.opentelemetry.sdk.metrics.export.PeriodicMetricReader;
import io.opentelemetry.sdk.resources.Resource;
import java.util.Locale;
import java.util.UUID;

@SuppressWarnings("ClassFanOutComplexity")
public class UsageMetricsModule implements Module {

    @Override
    public void configure(Binder binder) {
        // nothing needed here
    }

    @Provides
    @Singleton
    @Named("UsageMeterProvider")
    private SdkMeterProvider provideUsageMeterProvider(
        UofConfiguration configuration,
        @Named("version") String sdkVersion
    ) {
        if (!configuration.getUsage().isExportEnabled()) {
            return SdkMeterProvider.builder().build();
        }
        String usageServiceEndpointUrl = configuration.getUsage().getHost() + "/v1/metrics";
        String environmentName = configuration.getEnvironment().name().toLowerCase(Locale.getDefault());
        String serviceName = "odds-feed-sdk_usage_" + environmentName;
        String serviceInstanceId = UUID.randomUUID().toString();

        OtlpHttpMetricExporter metricExporter = OtlpHttpMetricExporter
            .builder()
            .setEndpoint(usageServiceEndpointUrl)
            .addHeader("x-access-token", configuration.getAccessToken())
            .addHeader("x-environment", environmentName)
            .addHeader("x-node-id", configuration.getNodeId().toString())
            .addHeader("x-sdk-version", sdkVersion)
            .setAggregationTemporalitySelector(AggregationTemporalitySelector.deltaPreferred())
            .setTimeout(configuration.getUsage().getExportTimeoutInSec(), SECONDS)
            .setRetryPolicy(null)
            .build();

        Resource resource = Resource
            .getDefault()
            .merge(
                Resource
                    .builder()
                    .put("service.name", serviceName)
                    .put("service.namespace", "com.sportradar.unifiedodds.sdk")
                    .put("service.version", sdkVersion)
                    .put("nodeId", configuration.getNodeId().toString())
                    .put("environment", environmentName)
                    .put("metricsVersion", "v1")
                    .put("service.instance.id", serviceInstanceId)
                    .put("bookmakerId", String.valueOf(configuration.getBookmakerDetails().getBookmakerId()))
                    .build()
            );

        PeriodicMetricReader periodicMetricReader = PeriodicMetricReader
            .builder(metricExporter)
            .setInterval(configuration.getUsage().getExportIntervalInSec(), SECONDS)
            .build();

        return SdkMeterProvider
            .builder()
            .setResource(resource)
            .registerMetricReader(periodicMetricReader)
            .build();
    }

    /**
     * Returns the TelemetryFactory object used by the SDK to publish Usage Metrics to Usage Service
     *
     * @return the TelemetryFactory object used by the SDK to publish Usage Metrics to Usage Service
     */
    @Provides
    @Singleton
    @Named("UsageTelemetryFactory")
    private TelemetryFactory provideUsageTelemetryFactory(
        UofConfiguration configuration,
        @Named("version") String sdkVersion,
        @Named("UsageMeterProvider") SdkMeterProvider usageMeterProvider
    ) {
        String instrumentationScopeName = "UofSdk-Java";

        OpenTelemetry usageOpenTelemetry = createUsageOpenTelemetry(configuration, usageMeterProvider);
        TelemetryImpl uofSdkTelemetry = new TelemetryImpl(
            usageOpenTelemetry,
            instrumentationScopeName,
            sdkVersion
        );
        return new TelemetryFactory(uofSdkTelemetry, new TimeUtilsImpl());
    }

    public OpenTelemetry createUsageOpenTelemetry(
        UofConfiguration configuration,
        SdkMeterProvider meterProvider
    ) {
        if (!configuration.getUsage().isExportEnabled()) {
            return OpenTelemetry.noop();
        }
        return OpenTelemetrySdk.builder().setMeterProvider(meterProvider).build();
    }
}
