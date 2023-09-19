/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */

package com.sportradar.unifiedodds.sdk.cfg;

import java.time.Duration;

public interface UofCacheConfiguration {
    /**
     * Gets the sport event cache timeout - how long cache items are cached
     * Can be between 1 hour and 48 hours - default 12 hours (sliding expiration)
     * @return the sport event cache timeout
     */
    Duration getSportEventCacheTimeout();

    /**
     * Gets the sport event status cache timeout - how long status is cached
     * Can be between 1 min and 60 min - default 5 min (absolute expiration)
     * @return the sport event status cache timeout
     */
    Duration getSportEventStatusCacheTimeout();

    /**
     * Gets the competitor/player cache timeout - how long cache item is cached
     * Can be between 1 hour and 48 hours - default 24 hours (sliding expiration)
     * @return the competitor/player cache timeout
     */
    Duration getProfileCacheTimeout();

    /**
     * Gets the variant market description cache timeout - how long cache item is cached
     * Can be between 1 hour and 24 hours - default 3 hours (sliding expiration)
     * @return the variant market description cache timeout
     */
    Duration getVariantMarketDescriptionCacheTimeout();

    /**
     * Gets the ignore BetPal timeline sport event status cache timeout - how long cache item is cached.
     * How long should the event id from BetPal producer be cached.
     * SportEventStatus from timeline endpoint for these events are ignored.
     * Can be between 1 hour and 24 hours - default 3 hours (sliding expiration)
     * @return the ignore BetPal timeline sport event status timeout
     */
    Duration getIgnoreBetPalTimelineSportEventStatusCacheTimeout();

    /**
     * Gets a value indicating whether to ignore sport event status from timeline endpoint for
     * sport events on BetPal producer (default: false)
     * @return true if sport event status from timeline endpoint should be ignored, otherwise false
     */
    boolean getIgnoreBetPalTimelineSportEventStatus();
}
