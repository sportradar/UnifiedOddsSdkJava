/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */
package com.sportradar.unifiedodds.sdk.cfg;

import java.time.Duration;

public class UofCacheConfigurationStub implements UofCacheConfiguration {

    private Duration sportEventStatusCacheTimeout;
    private Duration sportEventCacheTimeout;
    private Duration profileCacheTimetout;
    private Duration variantMarketDescriptionCacheTimeout;
    private Duration ignoreBetPalTimelineSportEventStatusCacheTimeout;

    public void setSportEventCacheTimeout(Duration duration) {
        sportEventCacheTimeout = duration;
    }

    @Override
    public Duration getSportEventCacheTimeout() {
        return sportEventCacheTimeout;
    }

    public void setSportEventStatusCacheTimeout(Duration duration) {
        sportEventStatusCacheTimeout = duration;
    }

    @Override
    public Duration getSportEventStatusCacheTimeout() {
        return sportEventStatusCacheTimeout;
    }

    public void setProfileCacheTimeout(Duration timeout) {
        profileCacheTimetout = timeout;
    }

    @Override
    public Duration getProfileCacheTimeout() {
        return profileCacheTimetout;
    }

    public void setVariantMarketDescriptionCacheTimeout(Duration timeout) {
        variantMarketDescriptionCacheTimeout = timeout;
    }

    @Override
    public Duration getVariantMarketDescriptionCacheTimeout() {
        return variantMarketDescriptionCacheTimeout;
    }

    public void setIgnoreBetPalTimelineSportEventStatusCacheTimeout(Duration timeout) {
        this.ignoreBetPalTimelineSportEventStatusCacheTimeout = timeout;
    }

    @Override
    public Duration getIgnoreBetPalTimelineSportEventStatusCacheTimeout() {
        return ignoreBetPalTimelineSportEventStatusCacheTimeout;
    }

    @Override
    public boolean getIgnoreBetPalTimelineSportEventStatus() {
        return false;
    }
}
