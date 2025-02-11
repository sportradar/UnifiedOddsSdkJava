/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */

package com.sportradar.unifiedodds.sdk.internal.cfg;

import com.sportradar.unifiedodds.sdk.cfg.UofUsageConfiguration;
import com.sportradar.utils.SdkHelper;
import org.junit.Assert;
import org.junit.Test;

public class UofUsageConfigurationTests {

    @Test
    public void defaultImplementationUsesDefaultValues() {
        UofUsageConfiguration config = new UofUsageConfigurationImpl();

        Assert.assertTrue(config.isExportEnabled());
        Assert.assertEquals(ConfigLimit.USAGE_EXPORT_TIMEOUT_SEC, config.getExportTimeoutInSec());
        Assert.assertEquals(ConfigLimit.USAGE_EXPORT_INTERVAL_SEC, config.getExportIntervalInSec());
        Assert.assertTrue(SdkHelper.stringIsNullOrEmpty(config.getHost()));
    }

    @Test
    public void settingExportEnabledToTrue() {
        UofUsageConfigurationImpl config = new UofUsageConfigurationImpl();

        config.setExportEnabled(true);

        Assert.assertTrue(config.isExportEnabled());
    }

    @Test
    public void settingExportEnabledToFalse() {
        UofUsageConfigurationImpl config = new UofUsageConfigurationImpl();

        config.setExportEnabled(false);

        Assert.assertFalse(config.isExportEnabled());
    }

    @Test
    public void toStringHasAllTheValues() {
        String summary = new UofUsageConfigurationImpl().toString();

        Assert.assertNotNull(summary);
        Assert.assertTrue(summary.contains("UsageConfiguration"));
        Assert.assertTrue(summary.contains("isExportEnabled="));
        Assert.assertTrue(summary.contains("exportIntervalInSec"));
        Assert.assertTrue(summary.contains("exportTimeoutInSec"));
        Assert.assertTrue(summary.contains("host"));
    }
}
