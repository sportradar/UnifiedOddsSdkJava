/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */

package com.sportradar.unifiedodds.sdk.caching;

import com.sportradar.unifiedodds.sdk.exceptions.internal.CacheItemNotFoundException;
import com.sportradar.unifiedodds.sdk.exceptions.internal.IllegalCacheStateException;
import com.sportradar.utils.URN;
import java.util.List;
import java.util.Locale;

/**
 * Defines methods implemented by caches used to store information about player and competitor profiles
 */
@SuppressWarnings({ "LineLength" })
public interface ProfileCache {
    /**
     * Returns a {@link PlayerProfileCI} associated with the provided {@link URN}
     *
     * @param id - the unique {@link URN} identifier of the player
     * @param locales - a {@link List} of locales in which the data is required
     * @param possibleAssociatedCompetitorIds - a list of possible associated competitors, used to prefetch competitor profiles
     * @return - a {@link PlayerProfileCI} associated with the provided {@link URN}
     */
    PlayerProfileCI getPlayerProfile(URN id, List<Locale> locales, List<URN> possibleAssociatedCompetitorIds)
        throws IllegalCacheStateException, CacheItemNotFoundException;

    /**
     * Returns a {@link CompetitorCI} associated with the provided {@link URN}
     *
     * @param id - the unique {@link URN} identifier of the competitor
     * @param locales - a {@link List} of locales in which the data is required
     * @return - a {@link CompetitorCI} associated with the provided {@link URN}
     */
    CompetitorCI getCompetitorProfile(URN id, List<Locale> locales)
        throws IllegalCacheStateException, CacheItemNotFoundException;

    /**
     * Purges the associated competitor cache item
     *
     * @param competitorId the identifier of the cache item to purge
     */
    void purgeCompetitorProfileCacheItem(URN competitorId);

    /**
     * Purges the associated player profile cache item
     *
     * @param playerId the identifier of the cache item to purge
     */
    void purgePlayerProfileCacheItem(URN playerId);
}
