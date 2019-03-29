/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */

package com.sportradar.unifiedodds.sdk.impl.custombetentities;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableList;
import com.sportradar.uf.custombet.datamodel.CAPIMarketType;
import com.sportradar.uf.custombet.datamodel.CAPIOutcomeType;
import com.sportradar.unifiedodds.sdk.custombetentities.Market;

import java.util.List;

/**
 * Implements methods used to access available selections for the market
 */
public class MarketImpl implements Market {
    /**
     * the id of the market
     */
    private final int id;

    /**
     * The specifiers for this market
     */
    private final String specifiers;

    /**
     * The outcomes for this market
     */
    private final List<String> outcomes;

    MarketImpl(CAPIMarketType market) {
        Preconditions.checkNotNull(market);

        this.id = market.getId();
        this.specifiers = market.getSpecifiers();
        this.outcomes = market.getOutcomes().stream()
                .map(CAPIOutcomeType::getId)
                .collect(ImmutableList.toImmutableList());
    }

    @Override
    public int getId() {
        return id;
    }

    @Override
    public String getSpecifiers() {
        return specifiers;
    }

    @Override
    public List<String> getOutcomes() {
        return outcomes;
    }
}
