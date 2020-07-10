/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */

package com.sportradar.unifiedodds.sdk.caching.markets;

import com.sportradar.unifiedodds.sdk.entities.markets.MarketDescription;
import com.sportradar.unifiedodds.sdk.exceptions.internal.CacheItemNotFoundException;
import com.sportradar.unifiedodds.sdk.exceptions.internal.IllegalCacheStateException;

import java.util.List;
import java.util.Locale;

/**
 * Created on 14/06/2017.
 * // TODO @eti: Javadoc
 */
public interface MarketDescriptionCache {
    MarketDescription getMarketDescriptor(int marketId, String variant, List<Locale> locales) throws IllegalCacheStateException, CacheItemNotFoundException;

    boolean loadMarketDescriptions();

    void deleteCacheItem(int marketId, String variant);

    void updateCacheItem(int marketId, String variant);
}
