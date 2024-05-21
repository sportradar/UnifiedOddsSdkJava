/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */

package com.sportradar.unifiedodds.sdk.caching.markets;

import com.sportradar.unifiedodds.sdk.domain.language.Languages;
import com.sportradar.unifiedodds.sdk.entities.markets.MarketDescription;
import com.sportradar.unifiedodds.sdk.exceptions.internal.CacheItemNotFoundException;
import com.sportradar.unifiedodds.sdk.exceptions.internal.IllegalCacheStateException;

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
