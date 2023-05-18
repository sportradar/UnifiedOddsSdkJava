/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */

package com.sportradar.unifiedodds.sdk.impl.custombetentities;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableList;
import com.sportradar.uf.custombet.datamodel.CAPIFilteredMarketType;
import com.sportradar.uf.custombet.datamodel.CAPIFilteredOutcomeType;
import com.sportradar.uf.custombet.datamodel.CAPIMarketType;
import com.sportradar.uf.custombet.datamodel.CAPIOutcomeType;
import com.sportradar.unifiedodds.sdk.custombetentities.Market;
import com.sportradar.unifiedodds.sdk.custombetentities.MarketFilter;
import com.sportradar.unifiedodds.sdk.custombetentities.OutcomeFilter;
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

    MarketFilterImpl(CAPIFilteredMarketType market) {
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
