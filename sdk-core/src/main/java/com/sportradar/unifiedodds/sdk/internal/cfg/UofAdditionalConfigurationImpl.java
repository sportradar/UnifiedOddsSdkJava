/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */

package com.sportradar.unifiedodds.sdk.internal.cfg;

import com.sportradar.unifiedodds.sdk.cfg.UofAdditionalConfiguration;
import java.time.Duration;
import java.util.StringJoiner;

public class UofAdditionalConfigurationImpl implements UofAdditionalConfiguration {

    private boolean omitMarketMappings;
    private Duration statisticsInterval;

    UofAdditionalConfigurationImpl() {
        omitMarketMappings = false;
        statisticsInterval = Duration.ofMinutes(ConfigLimit.STATISTICS_INTERVAL_MINUTES_DEFAULT);
    }

    @Override
    public boolean omitMarketMappings() {
        return omitMarketMappings;
    }

    @Override
    public Duration getStatisticsInterval() {
        return statisticsInterval;
    }

    public void setOmitMarketMappings(boolean newOmitMarketMappings) {
        this.omitMarketMappings = newOmitMarketMappings;
    }

    public void setStatisticsInterval(int statisticsIntervalInMinutes) {
        if (
            statisticsIntervalInMinutes >= ConfigLimit.STATISTICS_INTERVAL_MINUTES_MIN &&
            statisticsIntervalInMinutes <= ConfigLimit.STATISTICS_INTERVAL_MINUTES_MAX
        ) {
            this.statisticsInterval = Duration.ofMinutes(statisticsIntervalInMinutes);
            return;
        }

        String msg = String.format(
            "Invalid timeout value for StatisticsInterval: %s min",
            statisticsIntervalInMinutes
        );
        throw new IllegalArgumentException(msg);
    }

    @Override
    public String toString() {
        return new StringJoiner(", ", "AdditionalConfiguration{", "}")
            .add("omitMarketMappings=" + omitMarketMappings)
            .add("statisticsInterval=" + statisticsInterval.toMinutes())
            .toString();
    }
}
