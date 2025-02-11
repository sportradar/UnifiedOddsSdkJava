/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */
package com.sportradar.unifiedodds.sdk.caching.markets;

import com.sportradar.unifiedodds.sdk.entities.markets.MarketDescription;
import com.sportradar.unifiedodds.sdk.internal.caching.markets.MarketDescriptionProvider;
import com.sportradar.unifiedodds.sdk.internal.exceptions.CacheItemNotFoundException;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public class SingleMarketProviderStub implements MarketDescriptionProvider {

    private final MarketDescription marketDescription;

    @Override
    public MarketDescription getMarketDescription(
        int marketId,
        Map<String, String> marketSpecifiers,
        List<Locale> locales,
        boolean fetchVariantDescriptions
    ) throws CacheItemNotFoundException {
        return marketDescription;
    }

    @Override
    public boolean reloadMarketDescription(int marketId, Map<String, String> marketSpecifiers) {
        return false;
    }
}
