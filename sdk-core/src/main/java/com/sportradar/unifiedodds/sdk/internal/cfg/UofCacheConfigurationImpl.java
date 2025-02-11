/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */

package com.sportradar.unifiedodds.sdk.internal.cfg;

import com.sportradar.unifiedodds.sdk.cfg.UofCacheConfiguration;
import java.time.Duration;
import java.util.StringJoiner;

public class UofCacheConfigurationImpl implements UofCacheConfiguration {

    private Duration sportEventCacheTimeout;
    private Duration sportEventStatusCacheTimeout;
    private Duration profileCacheTimeout;
    private Duration variantMarketDescriptionCacheTimeout;
    private Duration ignoreBetPalTimelineSportEventStatusCacheTimeout;
    private boolean ignoreBetPalTimelineSportEventStatus;

    UofCacheConfigurationImpl() {
        sportEventCacheTimeout = Duration.ofHours(ConfigLimit.SPORTEVENTCACHE_TIMEOUT_DEFAULT);
        sportEventStatusCacheTimeout =
            Duration.ofMinutes(ConfigLimit.SPORTEVENTSTATUSCACHE_TIMEOUT_MINUTES_DEFAULT);
        profileCacheTimeout = Duration.ofHours(ConfigLimit.PROFILECACHE_TIMEOUT_DEFAULT);
        variantMarketDescriptionCacheTimeout =
            Duration.ofHours(ConfigLimit.SINGLEVARIANTMARKET_TIMEOUT_DEFAULT);
        ignoreBetPalTimelineSportEventStatusCacheTimeout =
            Duration.ofHours(ConfigLimit.IGNOREBETPALTIMELINE_TIMEOUT_DEFAULT);
        ignoreBetPalTimelineSportEventStatus = false;
    }

    @Override
    public Duration getSportEventCacheTimeout() {
        return sportEventCacheTimeout;
    }

    @Override
    public Duration getSportEventStatusCacheTimeout() {
        return sportEventStatusCacheTimeout;
    }

    @Override
    public Duration getProfileCacheTimeout() {
        return profileCacheTimeout;
    }

    @Override
    public Duration getVariantMarketDescriptionCacheTimeout() {
        return variantMarketDescriptionCacheTimeout;
    }

    @Override
    public Duration getIgnoreBetPalTimelineSportEventStatusCacheTimeout() {
        return ignoreBetPalTimelineSportEventStatusCacheTimeout;
    }

    @Override
    public boolean getIgnoreBetPalTimelineSportEventStatus() {
        return ignoreBetPalTimelineSportEventStatus;
    }

    void setSportEventCacheTimeout(int timeout) {
        if (
            timeout >= ConfigLimit.SPORTEVENTCACHE_TIMEOUT_MIN &&
            timeout <= ConfigLimit.SPORTEVENTCACHE_TIMEOUT_MAX
        ) {
            sportEventCacheTimeout = Duration.ofHours(timeout);
            return;
        }

        String msg = String.format("Invalid timeout value for SportEventCacheTimeout: %s hours", timeout);
        throw new IllegalArgumentException(msg);
    }

    void setSportEventStatusCacheTimeout(int timeoutInMinutes) {
        if (
            timeoutInMinutes >= ConfigLimit.SPORTEVENTSTATUSCACHE_TIMEOUT_MINUTES_MIN &&
            timeoutInMinutes <= ConfigLimit.SPORTEVENTSTATUSCACHE_TIMEOUT_MINUTES_MAX
        ) {
            sportEventStatusCacheTimeout = Duration.ofMinutes(timeoutInMinutes);
            return;
        }

        String msg = String.format(
            "Invalid timeout value for SportEventStatusCacheTimeout: %s min",
            timeoutInMinutes
        );
        throw new IllegalArgumentException(msg);
    }

    /**
     * Sets the profile cache timeout
     * @param timeout timeout value
     */
    void setProfileCacheTimeout(int timeout) {
        if (
            timeout >= ConfigLimit.PROFILECACHE_TIMEOUT_MIN && timeout <= ConfigLimit.PROFILECACHE_TIMEOUT_MAX
        ) {
            profileCacheTimeout = Duration.ofHours(timeout);
            return;
        }

        String msg = String.format("Invalid timeout value for ProfileCacheTimeout: %s hours", timeout);
        throw new IllegalArgumentException(msg);
    }

    /**
     * Sets the variant market description cache timeout
     * @param timeout timeout value
     */
    void setVariantMarketDescriptionCacheTimeout(int timeout) {
        if (
            timeout >= ConfigLimit.SINGLEVARIANTMARKET_TIMEOUT_MIN &&
            timeout <= ConfigLimit.SINGLEVARIANTMARKET_TIMEOUT_MAX
        ) {
            variantMarketDescriptionCacheTimeout = Duration.ofHours(timeout);
            return;
        }

        String msg = String.format(
            "Invalid timeout value for VariantMarketDescriptionCacheTimeout: %s hours",
            timeout
        );
        throw new IllegalArgumentException(msg);
    }

    /**
     * Sets ignore BetPal timeline sport event status cache timeout. How long should the event id from BetPal be cached.
     * SportEventStatus from timeline endpoint for these events are ignored.
     * @param timeout timeout value
     */
    void setIgnoreBetPalTimelineSportEventStatusCacheTimeout(int timeout) {
        if (
            timeout >= ConfigLimit.IGNOREBETPALTIMELINE_TIMEOUT_MIN &&
            timeout <= ConfigLimit.IGNOREBETPALTIMELINE_TIMEOUT_MAX
        ) {
            ignoreBetPalTimelineSportEventStatusCacheTimeout = Duration.ofHours(timeout);
            return;
        }

        String msg = String.format(
            "Invalid timeout value for IgnoreBetPalTimelineSportEventStatusCacheTimeout: %s hours",
            timeout
        );
        throw new IllegalArgumentException(msg);
    }

    /**
     * Sets the value indicating whether to ignore sport event status from timeline endpoint
     * for sport events on BetPal producer
     * @param ignore ignore value
     */
    void setIgnoreBetPalTimelineSportEventStatus(boolean ignore) {
        ignoreBetPalTimelineSportEventStatus = ignore;
    }

    @Override
    public String toString() {
        return new StringJoiner(", ", "CacheConfiguration{", "}")
            .add("sportEventCacheTimeout=" + sportEventCacheTimeout.toHours())
            .add("sportEventStatusCacheTimeout=" + sportEventStatusCacheTimeout.toMinutes() + " (minutes)")
            .add("profileCacheTimeout=" + profileCacheTimeout.toHours())
            .add("variantMarketDescriptionCacheTimeout=" + variantMarketDescriptionCacheTimeout.toHours())
            .add(
                "ignoreBetPalTimelineSportEventStatusCacheTimeout=" +
                ignoreBetPalTimelineSportEventStatusCacheTimeout.toHours()
            )
            .add("ignoreBetPalTimelineSportEventStatus=" + ignoreBetPalTimelineSportEventStatus)
            .toString();
    }
}
