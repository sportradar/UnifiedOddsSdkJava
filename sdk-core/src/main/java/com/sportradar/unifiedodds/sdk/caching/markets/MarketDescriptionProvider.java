/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */

package com.sportradar.unifiedodds.sdk.caching.markets;

import com.sportradar.unifiedodds.sdk.entities.markets.MarketDescription;
import com.sportradar.unifiedodds.sdk.entities.markets.Specifier;
import com.sportradar.unifiedodds.sdk.exceptions.internal.CacheItemNotFoundException;

import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * Created on 07/07/2017.
 * // TODO @eti: Javadoc
 */
public interface MarketDescriptionProvider {
    MarketDescription getMarketDescription(int marketId, Map<String, String> marketSpecifiers, List<Locale> locales, boolean fetchVariantDescriptions) throws CacheItemNotFoundException;

    /**
     * Reloads market description (single or list)
     * @param marketId the market identifier
     * @param specifiers a list of specifiers or a null reference if market is invariant
     * @param sourceCache the source cache cache item belongs to
     * @return true if succeeded, false otherwise
     */
    boolean reloadMarketDescription(int marketId, List<Specifier> specifiers, String sourceCache);
}
