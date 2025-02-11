/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */
package com.sportradar.unifiedodds.sdk.common.internal;

import static java.util.Optional.ofNullable;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.sportradar.unifiedodds.sdk.cfg.Environment;
import com.sportradar.unifiedodds.sdk.cfg.UofConfiguration;
import com.sportradar.unifiedodds.sdk.cfg.UofUsageConfiguration;
import java.util.Optional;

class UofConfigurationsForUsage {

    static class BuilderForUsageUsingMocks {

        private final UofConfiguration uofConfiguration = mock(UofConfiguration.class);
        private UofUsageConfiguration usageConfiguration;

        public static BuilderForUsageUsingMocks uofConfigurationForUsageTelemetry() {
            return new BuilderForUsageUsingMocks();
        }

        public BuilderForUsageUsingMocks withAccessToken(String token) {
            when(uofConfiguration.getAccessToken()).thenReturn(token);
            return this;
        }

        public UofConfiguration build() {
            when(uofConfiguration.getUsage())
                .thenReturn(ofNullable(usageConfiguration).orElse(mock(UofUsageConfiguration.class)));
            return uofConfiguration;
        }

        public BuilderForUsageUsingMocks withEnvironment(Environment environment) {
            when(uofConfiguration.getEnvironment()).thenReturn(environment);
            return this;
        }

        public BuilderForUsageUsingMocks withNodeId(int nodeId) {
            when(uofConfiguration.getNodeId()).thenReturn(nodeId);
            return this;
        }

        public BuilderForUsageUsingMocks withUsageConfiguration(UofUsageConfiguration usage) {
            this.usageConfiguration = usage;
            return this;
        }

        static class UsageBuilderUsingMocks {

            private UofUsageConfiguration usageConfiguration = mock(UofUsageConfiguration.class);

            public static UsageBuilderUsingMocks usageConfigurationForUsageTelemetry() {
                return new UsageBuilderUsingMocks();
            }

            public UsageBuilderUsingMocks withHost(String host) {
                when(usageConfiguration.getHost()).thenReturn(host);
                return this;
            }

            public UsageBuilderUsingMocks withExportEnabled(boolean exportEnabled) {
                when(usageConfiguration.isExportEnabled()).thenReturn(exportEnabled);
                return this;
            }

            public UsageBuilderUsingMocks withExportIntervalInSec(int exportIntervalSeconds) {
                when(usageConfiguration.getExportIntervalInSec()).thenReturn(exportIntervalSeconds);
                return this;
            }

            public UsageBuilderUsingMocks withExportTimeoutInSec(int exportTimeoutSeconds) {
                when(usageConfiguration.getExportTimeoutInSec()).thenReturn(exportTimeoutSeconds);
                return this;
            }

            public UofUsageConfiguration build() {
                return usageConfiguration;
            }
        }
    }
}
