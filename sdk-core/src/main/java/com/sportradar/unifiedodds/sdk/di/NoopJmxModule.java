/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */

package com.sportradar.unifiedodds.sdk.di;

import com.google.inject.Binder;
import com.google.inject.Module;
import com.google.inject.Provides;
import com.google.inject.Singleton;
import com.sportradar.unifiedodds.sdk.impl.UnifiedOddsStatistics;

public class NoopJmxModule implements Module {

    @Override
    public void configure(Binder binder) {}

    /**
     * Returns the statistics object but not attached to any MBeanServer
     *
     * @return the statistics object but not attached to any MBeanServer
     */
    @Provides
    @Singleton
    private UnifiedOddsStatistics provideUnifiedOddsStatisticsNotAttachedToAnyMbeanServer() {
        return new UnifiedOddsStatistics();
    }
}
