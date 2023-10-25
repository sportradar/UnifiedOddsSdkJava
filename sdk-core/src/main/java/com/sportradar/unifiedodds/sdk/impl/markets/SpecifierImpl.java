/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */

package com.sportradar.unifiedodds.sdk.impl.markets;

import com.sportradar.unifiedodds.sdk.caching.ci.markets.MarketSpecifierCi;
import com.sportradar.unifiedodds.sdk.entities.markets.Specifier;

/**
 * Created on 14/06/2017.
 * // TODO @eti: Javadoc
 */
public class SpecifierImpl implements Specifier {

    private final MarketSpecifierCi ci;

    public SpecifierImpl(MarketSpecifierCi s) {
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

    @Override
    public String toString() {
        return "SpecifierImpl{" + ci.getName() + "=" + ci.getType() + '}';
    }
}
