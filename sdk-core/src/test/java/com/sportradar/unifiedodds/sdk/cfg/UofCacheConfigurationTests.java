/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */

package com.sportradar.unifiedodds.sdk.cfg;

import org.junit.Assert;
import org.junit.Test;

public class UofCacheConfigurationTests {

    private final UofCacheConfigurationImpl config = new UofCacheConfigurationImpl();

    @Test
    public void defaultImplementationUsesDefaultValues() {
        Assert.assertEquals(
            ConfigLimit.SPORTEVENTCACHE_TIMEOUT_DEFAULT,
            config.getSportEventCacheTimeout().toHours()
        );
        Assert.assertEquals(
            ConfigLimit.SPORTEVENTSTATUSCACHE_TIMEOUT_MINUTES_DEFAULT,
            config.getSportEventStatusCacheTimeout().toMinutes()
        );
        Assert.assertEquals(
            ConfigLimit.PROFILECACHE_TIMEOUT_DEFAULT,
            config.getProfileCacheTimeout().toHours()
        );
        Assert.assertEquals(
            ConfigLimit.SINGLEVARIANTMARKET_TIMEOUT_DEFAULT,
            config.getVariantMarketDescriptionCacheTimeout().toHours()
        );
        Assert.assertEquals(
            ConfigLimit.IGNOREBETPALTIMELINE_TIMEOUT_DEFAULT,
            config.getIgnoreBetPalTimelineSportEventStatusCacheTimeout().toHours()
        );
        Assert.assertFalse(config.getIgnoreBetPalTimelineSportEventStatus());
    }

    @Test
    public void setSportEventCacheTimeout_ValidValue() {
        final int newValue = 25;
        config.setSportEventCacheTimeout(newValue);

        Assert.assertEquals(newValue, config.getSportEventCacheTimeout().toHours());
    }

    @Test
    public void setSportEventCacheTimeout_MinValue() {
        config.setSportEventCacheTimeout(ConfigLimit.SPORTEVENTCACHE_TIMEOUT_MIN);

        Assert.assertEquals(
            ConfigLimit.SPORTEVENTCACHE_TIMEOUT_MIN,
            config.getSportEventCacheTimeout().toHours()
        );
    }

    @Test
    public void setSportEventCacheTimeout_MaxValue() {
        config.setSportEventCacheTimeout(ConfigLimit.SPORTEVENTCACHE_TIMEOUT_MAX);

        Assert.assertEquals(
            ConfigLimit.SPORTEVENTCACHE_TIMEOUT_MAX,
            config.getSportEventCacheTimeout().toHours()
        );
    }

    @Test(expected = IllegalArgumentException.class)
    public void setSportEventCacheTimeout_BelowMinValue() {
        config.setSportEventCacheTimeout(ConfigLimit.SPORTEVENTCACHE_TIMEOUT_MIN - 1);
    }

    @Test(expected = IllegalArgumentException.class)
    public void setSportEventCacheTimeout_OverMaxValue() {
        config.setSportEventCacheTimeout(ConfigLimit.SPORTEVENTCACHE_TIMEOUT_MAX + 1);
    }

    @Test
    public void setSportEventStatusCacheTimeout_ValidValue() {
        final int newValue = 25;
        config.setSportEventStatusCacheTimeout(newValue);

        Assert.assertEquals(newValue, config.getSportEventStatusCacheTimeout().toMinutes());
    }

    @Test
    public void setSportEventStatusCacheTimeout_MinValue() {
        config.setSportEventStatusCacheTimeout(ConfigLimit.SPORTEVENTSTATUSCACHE_TIMEOUT_MINUTES_MIN);

        Assert.assertEquals(
            ConfigLimit.SPORTEVENTSTATUSCACHE_TIMEOUT_MINUTES_MIN,
            config.getSportEventStatusCacheTimeout().toMinutes()
        );
    }

    @Test
    public void setSportEventStatusCacheTimeout_MaxValue() {
        config.setSportEventStatusCacheTimeout(ConfigLimit.SPORTEVENTSTATUSCACHE_TIMEOUT_MINUTES_MAX);

        Assert.assertEquals(
            ConfigLimit.SPORTEVENTSTATUSCACHE_TIMEOUT_MINUTES_MAX,
            config.getSportEventStatusCacheTimeout().toMinutes()
        );
    }

    @Test(expected = IllegalArgumentException.class)
    public void setSportEventStatusCacheTimeout_BelowMinValue() {
        config.setSportEventStatusCacheTimeout(ConfigLimit.SPORTEVENTSTATUSCACHE_TIMEOUT_MINUTES_MIN - 1);
    }

    @Test(expected = IllegalArgumentException.class)
    public void setSportEventStatusCacheTimeout_OverMaxValue() {
        config.setSportEventStatusCacheTimeout(ConfigLimit.SPORTEVENTSTATUSCACHE_TIMEOUT_MINUTES_MAX + 1);
    }

    @Test
    public void setProfileCacheTimeout_ValidValue() {
        final int newValue = 25;
        config.setProfileCacheTimeout(newValue);

        Assert.assertEquals(newValue, config.getProfileCacheTimeout().toHours());
    }

    @Test
    public void setProfileCacheTimeout_MinValue() {
        config.setProfileCacheTimeout(ConfigLimit.PROFILECACHE_TIMEOUT_MIN);

        Assert.assertEquals(ConfigLimit.PROFILECACHE_TIMEOUT_MIN, config.getProfileCacheTimeout().toHours());
    }

    @Test
    public void setProfileCacheTimeout_MaxValue() {
        config.setProfileCacheTimeout(ConfigLimit.PROFILECACHE_TIMEOUT_MAX);

        Assert.assertEquals(ConfigLimit.PROFILECACHE_TIMEOUT_MAX, config.getProfileCacheTimeout().toHours());
    }

    @Test(expected = IllegalArgumentException.class)
    public void setProfileCacheTimeout_BelowMinValue() {
        config.setProfileCacheTimeout(ConfigLimit.PROFILECACHE_TIMEOUT_MIN - 1);
    }

    @Test(expected = IllegalArgumentException.class)
    public void setProfileCacheTimeout_OverMaxValue() {
        config.setProfileCacheTimeout(ConfigLimit.PROFILECACHE_TIMEOUT_MAX + 1);
    }

    @Test
    public void setIgnoreBetPalTimelineSportEventStatusCacheTimeout_ValidValue() {
        final int newValue = 20;
        config.setIgnoreBetPalTimelineSportEventStatusCacheTimeout(newValue);

        Assert.assertEquals(newValue, config.getIgnoreBetPalTimelineSportEventStatusCacheTimeout().toHours());
    }

    @Test
    public void setIgnoreBetPalTimelineSportEventStatusCacheTimeout_MinValue() {
        config.setIgnoreBetPalTimelineSportEventStatusCacheTimeout(
            ConfigLimit.IGNOREBETPALTIMELINE_TIMEOUT_MIN
        );

        Assert.assertEquals(
            ConfigLimit.IGNOREBETPALTIMELINE_TIMEOUT_MIN,
            config.getIgnoreBetPalTimelineSportEventStatusCacheTimeout().toHours()
        );
    }

    @Test
    public void setIgnoreBetPalTimelineSportEventStatusCacheTimeout_MaxValue() {
        config.setIgnoreBetPalTimelineSportEventStatusCacheTimeout(
            ConfigLimit.IGNOREBETPALTIMELINE_TIMEOUT_MAX
        );

        Assert.assertEquals(
            ConfigLimit.IGNOREBETPALTIMELINE_TIMEOUT_MAX,
            config.getIgnoreBetPalTimelineSportEventStatusCacheTimeout().toHours()
        );
    }

    @Test(expected = IllegalArgumentException.class)
    public void setIgnoreBetPalTimelineSportEventStatusCacheTimeout_BelowMinValue() {
        config.setIgnoreBetPalTimelineSportEventStatusCacheTimeout(
            ConfigLimit.IGNOREBETPALTIMELINE_TIMEOUT_MIN - 1
        );
    }

    @Test(expected = IllegalArgumentException.class)
    public void setIgnoreBetPalTimelineSportEventStatusCacheTimeout_OverMaxValue() {
        config.setIgnoreBetPalTimelineSportEventStatusCacheTimeout(
            ConfigLimit.IGNOREBETPALTIMELINE_TIMEOUT_MAX + 1
        );
    }

    @Test
    public void setIgnoreBetPalTimelineSportEventStatusValue() {
        final boolean newValue = true;
        config.setIgnoreBetPalTimelineSportEventStatus(newValue);

        Assert.assertEquals(newValue, config.getIgnoreBetPalTimelineSportEventStatus());
    }

    @Test
    public void toStringHasAllTheValues() {
        String summary = config.toString();

        Assert.assertNotNull(summary);
        Assert.assertTrue(summary.contains("CacheConfiguration"));
        Assert.assertTrue(summary.contains("sportEventCacheTimeout="));
        Assert.assertTrue(summary.contains("sportEventStatusCacheTimeout="));
        Assert.assertTrue(summary.contains("profileCacheTimeout="));
        Assert.assertTrue(summary.contains("ignoreBetPalTimelineSportEventStatusCacheTimeout="));
        Assert.assertTrue(summary.contains("ignoreBetPalTimelineSportEventStatus="));
    }
}
