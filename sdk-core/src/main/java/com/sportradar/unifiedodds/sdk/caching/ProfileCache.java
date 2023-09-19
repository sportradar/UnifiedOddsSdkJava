/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */

package com.sportradar.unifiedodds.sdk.caching;

import com.sportradar.unifiedodds.sdk.exceptions.internal.CacheItemNotFoundException;
import com.sportradar.unifiedodds.sdk.exceptions.internal.IllegalCacheStateException;
import com.sportradar.utils.Urn;
import java.util.List;
import java.util.Locale;

/**
 * Defines methods implemented by caches used to store information about player and competitor profiles
 */
@SuppressWarnings({ "LineLength" })
public interface ProfileCache {
    /**
     * Returns a {@link PlayerProfileCi} associated with the provided {@link Urn}
     *
     * @param id - the unique {@link Urn} identifier of the player
     * @param locales - a {@link List} of locales in which the data is required
     * @param possibleAssociatedCompetitorIds - a list of possible associated competitors, used to prefetch competitor profiles
     * @return - a {@link PlayerProfileCi} associated with the provided {@link Urn}
     */
    PlayerProfileCi getPlayerProfile(Urn id, List<Locale> locales, List<Urn> possibleAssociatedCompetitorIds)
        throws IllegalCacheStateException, CacheItemNotFoundException;

    /**
     * Returns a {@link CompetitorCi} associated with the provided {@link Urn}
     *
     * @param id - the unique {@link Urn} identifier of the competitor
     * @param locales - a {@link List} of locales in which the data is required
     * @return - a {@link CompetitorCi} associated with the provided {@link Urn}
     */
    CompetitorCi getCompetitorProfile(Urn id, List<Locale> locales)
        throws IllegalCacheStateException, CacheItemNotFoundException;

    /**
     * Purges the associated competitor cache item
     *
     * @param competitorId the identifier of the cache item to purge
     */
    void purgeCompetitorProfileCacheItem(Urn competitorId);

    /**
     * Purges the associated player profile cache item
     *
     * @param playerId the identifier of the cache item to purge
     */
    void purgePlayerProfileCacheItem(Urn playerId);
}
