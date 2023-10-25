/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */

package com.sportradar.unifiedodds.sdk.caching;

import com.sportradar.unifiedodds.sdk.caching.impl.SportData;
import com.sportradar.unifiedodds.sdk.exceptions.internal.CacheItemNotFoundException;
import com.sportradar.unifiedodds.sdk.exceptions.internal.IllegalCacheStateException;
import com.sportradar.utils.Urn;
import java.util.List;
import java.util.Locale;

/**
 * Defines methods used to access sports data cache items
 */
@SuppressWarnings({ "LineLength" })
public interface SportsDataCache {
    /**
     * Returns a {@link List} sports supported by the feed.
     *
     * @param locales a {@link List} of {@link Locale} specifying the languages in which the data is returned
     * @return a {@link List} sports supported by the feed
     */
    List<SportData> getSports(List<Locale> locales) throws IllegalCacheStateException;

    /**
     * Returns a {@link SportData} instance representing the sport associated with the provided {@link Urn} identifier
     *
     * @param sportId a {@link Urn} specifying the id of the sport
     * @param locales a {@link List} of {@link Locale} specifying the languages in which the data is returned
     * @return a {@link SportData} containing information about the requested sport
     */
    SportData getSport(Urn sportId, List<Locale> locales)
        throws IllegalCacheStateException, CacheItemNotFoundException;

    /**
     * Returns the associated category cache item
     *
     * @param categoryId the identifier of the category
     * @param locales the locales in which to provide the data
     * @return the CI of the category associated with the provided identifier
     * @throws IllegalCacheStateException if the cache load failed
     * @throws CacheItemNotFoundException if the cache item could not be found - category does not exists in the cache/api
     */
    CategoryCi getCategory(Urn categoryId, List<Locale> locales)
        throws IllegalCacheStateException, CacheItemNotFoundException;
}
