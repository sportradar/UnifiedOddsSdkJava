/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */
package com.sportradar.unifiedodds.sdk.di;

import static java.util.Optional.ofNullable;
import static org.mockito.Mockito.mock;

import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Key;
import com.google.inject.name.Names;
import com.sportradar.unifiedodds.sdk.cfg.UofConfiguration;
import com.sportradar.unifiedodds.sdk.internal.common.telemetry.TelemetryFactory;
import com.sportradar.unifiedodds.sdk.internal.common.telemetry.TelemetryImpl;
import com.sportradar.unifiedodds.sdk.internal.di.MetricsModule;
import com.sportradar.unifiedodds.sdk.internal.impl.TimeUtils;
import io.opentelemetry.api.OpenTelemetry;
import lombok.val;

public class UsageTelemetryFactories {

    public static MetricsModuleBuilder createFromDi() {
        return new MetricsModuleBuilder();
    }

    public static TelemetryFactoriesBuilder createInstance() {
        return new TelemetryFactoriesBuilder();
    }

    public static class MetricsModuleBuilder {

        private String sdkVersion;
        private UofConfiguration uofConfiguration;

        public MetricsModuleBuilder withVersion(String version) {
            this.sdkVersion = version;
            return this;
        }

        public MetricsModuleBuilder withConfiguration(UofConfiguration configuration) {
            this.uofConfiguration = configuration;
            return this;
        }

        public TelemetryFactory build() {
            val injector = Guice.createInjector(
                moduleWithBoundSdkVersionAndUofConfiguration(),
                new MetricsModule()
            );

            return injector.getInstance(
                Key.get(TelemetryFactory.class, Names.named("UsageTelemetryFactory"))
            );
        }

        private AbstractModule moduleWithBoundSdkVersionAndUofConfiguration() {
            return new AbstractModule() {
                @Override
                protected void configure() {
                    bind(String.class).annotatedWith(Names.named("version")).toInstance(sdkVersion);
                    bind(UofConfiguration.class).toInstance(uofConfiguration);
                }
            };
        }
    }

    public static class TelemetryFactoriesBuilder {

        private TimeUtils timeUtils;
        private OpenTelemetry openTelemetry;
        private String sdkVersion;
        private String serviceName;

        public TelemetryFactoriesBuilder withTimeUtils(TimeUtils time) {
            this.timeUtils = time;
            return this;
        }

        public TelemetryFactoriesBuilder withSdkVersion(String version) {
            this.sdkVersion = version;
            return this;
        }

        public TelemetryFactoriesBuilder withServiceName(String name) {
            this.serviceName = name;
            return this;
        }

        public TelemetryFactoriesBuilder withOpenTelemetry(OpenTelemetry telemetry) {
            this.openTelemetry = telemetry;
            return this;
        }

        public TelemetryFactory build() {
            return new TelemetryFactory(
                new TelemetryImpl(
                    ofNullable(openTelemetry).orElse(OpenTelemetry.noop()),
                    ofNullable(serviceName).orElse("test-service"),
                    ofNullable(sdkVersion).orElse("1.0.0")
                ),
                ofNullable(timeUtils).orElse(mock(TimeUtils.class))
            );
        }
    }
}
