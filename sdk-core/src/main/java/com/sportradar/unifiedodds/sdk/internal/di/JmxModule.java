/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */

package com.sportradar.unifiedodds.sdk.internal.di;

import com.google.inject.Binder;
import com.google.inject.Module;
import com.google.inject.Provides;
import com.google.inject.Singleton;
import com.sportradar.unifiedodds.sdk.internal.impl.UnifiedOddsStatistics;
import java.lang.management.ManagementFactory;

public class JmxModule implements Module {

    @Override
    public void configure(Binder binder) {}

    /**
     * Returns the statistics collection object used by the sdk
     *
     * @return the statistics collection object used by the sdk
     */
    @Provides
    @Singleton
    private UnifiedOddsStatistics provideUnifiedOddsStatistics(MetricsRegisterer metricsRegisterer) {
        return metricsRegisterer.createUnifiedOddsStatistics();
    }

    /**
     * Returns the JMX metrics registerer
     *
     * @return the JMX metrics registerer
     */
    @Provides
    @Singleton
    private MetricsRegisterer provideMetricsRegisterer() {
        return new MetricsRegisterer(ManagementFactory.getPlatformMBeanServer());
    }
}
