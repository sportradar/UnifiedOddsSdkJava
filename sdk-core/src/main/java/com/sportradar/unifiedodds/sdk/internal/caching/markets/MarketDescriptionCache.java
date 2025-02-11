/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */

package com.sportradar.unifiedodds.sdk.internal.caching.markets;

import com.sportradar.unifiedodds.sdk.entities.markets.MarketDescription;
import com.sportradar.unifiedodds.sdk.internal.caching.Languages;
import com.sportradar.unifiedodds.sdk.internal.exceptions.CacheItemNotFoundException;
import com.sportradar.unifiedodds.sdk.internal.exceptions.IllegalCacheStateException;

/**
 * Created on 14/06/2017.
 * // TODO @eti: Javadoc
 */
public interface MarketDescriptionCache {
    MarketDescription getMarketDescriptor(int marketId, String variant, Languages.BestEffort locales)
        throws IllegalCacheStateException, CacheItemNotFoundException;

    boolean loadMarketDescriptions();

    void deleteCacheItem(int marketId, String variant);

    void updateCacheItem(int marketId, String variant);
}
