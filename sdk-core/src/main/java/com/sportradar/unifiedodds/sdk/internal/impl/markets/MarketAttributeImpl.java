/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */

package com.sportradar.unifiedodds.sdk.internal.impl.markets;

import com.sportradar.unifiedodds.sdk.entities.markets.MarketAttribute;
import com.sportradar.unifiedodds.sdk.internal.caching.ci.markets.MarketAttributeCi;

/**
 * Created on 14/06/2017.
 * // TODO @eti: Javadoc
 */
public class MarketAttributeImpl implements MarketAttribute {

    private final MarketAttributeCi cacheItem;

    public MarketAttributeImpl(MarketAttributeCi att) {
        this.cacheItem = att;
    }

    @Override
    public String getName() {
        return cacheItem.getName();
    }

    @Override
    public String getDescription() {
        return cacheItem.getDescription();
    }
}
