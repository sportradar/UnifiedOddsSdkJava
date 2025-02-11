/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */

package com.sportradar.unifiedodds.sdk.internal.impl.custombetentities;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableList;
import com.sportradar.uf.custombet.datamodel.CapiFilteredMarketType;
import com.sportradar.unifiedodds.sdk.entities.custombet.MarketFilter;
import com.sportradar.unifiedodds.sdk.entities.custombet.OutcomeFilter;
import java.util.List;

/**
 * Implements methods used to access available selections for the market
 */
public class MarketFilterImpl implements MarketFilter {

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
    private final List<OutcomeFilter> outcomes;

    /**
     * The value indicating if the market is in conflict
     */
    private final Boolean isConflict;

    MarketFilterImpl(CapiFilteredMarketType market) {
        Preconditions.checkNotNull(market);

        this.id = market.getId();
        this.specifiers = market.getSpecifiers();
        this.outcomes =
            market
                .getOutcomes()
                .stream()
                .map(m -> new OutcomeFilterImpl(m))
                .collect(ImmutableList.toImmutableList());
        this.isConflict = market.isConflict();
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
    public List<OutcomeFilter> getOutcomes() {
        return outcomes;
    }

    @Override
    public Boolean isConflict() {
        return isConflict;
    }
}
