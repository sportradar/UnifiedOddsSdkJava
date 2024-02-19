/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */
package com.sportradar.unifiedodds.sdk.caching.markets;

import com.sportradar.unifiedodds.sdk.entities.markets.MarketDescription;
import com.sportradar.unifiedodds.sdk.exceptions.internal.CacheItemNotFoundException;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class NoMarketDescribingProvider implements MarketDescriptionProvider {

    @Override
    public MarketDescription getMarketDescription(
        int marketId,
        Map<String, String> marketSpecifiers,
        List<Locale> locales,
        boolean fetchVariantDescriptions
    ) throws CacheItemNotFoundException {
        throw new CacheItemNotFoundException("any message");
    }

    @Override
    public boolean reloadMarketDescription(int marketId, Map<String, String> marketSpecifiers) {
        return false;
    }
}
