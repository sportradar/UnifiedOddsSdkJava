/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */

package com.sportradar.unifiedodds.sdk.internal.cfg;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.Assert;
import org.junit.Test;

public class UofAdditionalConfigurationTests {

    @Test
    public void defaultImplementationUsesDefaultValues() {
        UofAdditionalConfigurationImpl config = new UofAdditionalConfigurationImpl();

        Assert.assertFalse(config.omitMarketMappings());
        Assert.assertEquals(
            ConfigLimit.STATISTICS_INTERVAL_MINUTES_DEFAULT,
            config.getStatisticsInterval().toMinutes()
        );
    }

    @Test
    public void settingOmitMarketMappings() {
        UofAdditionalConfigurationImpl config = new UofAdditionalConfigurationImpl();

        config.setOmitMarketMappings(true);

        Assert.assertTrue(config.omitMarketMappings());
    }

    @Test
    public void settingStatisticsIntervalWithValidValue() {
        final int newStatisticsInterval = 1000;
        UofAdditionalConfigurationImpl config = new UofAdditionalConfigurationImpl();

        config.setStatisticsInterval(newStatisticsInterval);

        Assert.assertEquals(newStatisticsInterval, config.getStatisticsInterval().toMinutes());
    }

    @Test
    public void settingStatisticsIntervalWithZeroValue() {
        final int newStatisticsInterval = 0;
        UofAdditionalConfigurationImpl config = new UofAdditionalConfigurationImpl();

        config.setStatisticsInterval(newStatisticsInterval);

        Assert.assertEquals(newStatisticsInterval, config.getStatisticsInterval().toMinutes());
    }

    @Test
    public void settingStatisticsIntervalWithNegativeValueIsIgnored() {
        final int newStatisticsInterval = -1000;
        UofAdditionalConfigurationImpl config = new UofAdditionalConfigurationImpl();

        assertThatThrownBy(() -> config.setStatisticsInterval(newStatisticsInterval))
            .isInstanceOf(IllegalArgumentException.class);

        Assert.assertEquals(
            ConfigLimit.STATISTICS_INTERVAL_MINUTES_DEFAULT,
            config.getStatisticsInterval().toMinutes()
        );
    }

    @Test
    public void toStringHasAllTheValues() {
        String summary = new UofAdditionalConfigurationImpl().toString();

        Assert.assertNotNull(summary);
        Assert.assertTrue(summary.contains("AdditionalConfiguration"));
        Assert.assertTrue(summary.contains("omitMarketMappings="));
        Assert.assertTrue(summary.contains("statisticsInterval"));
    }
}
