/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */

package com.sportradar.unifiedodds.sdk.impl.markets;

import com.sportradar.unifiedodds.sdk.caching.ci.markets.MarketSpecifierCI;
import com.sportradar.unifiedodds.sdk.entities.markets.Specifier;

/**
 * Created on 14/06/2017.
 * // TODO @eti: Javadoc
 */
public class SpecifierImpl implements Specifier{
    private final MarketSpecifierCI ci;

    public SpecifierImpl(MarketSpecifierCI s) {
        ci = s;
    }

    @Override
    public String getType() {
        return ci.getType();
    }

    @Override
    public String getName() {
        return ci.getName();
    }
}
