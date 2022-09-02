/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */

package com.sportradar.unifiedodds.sdk.impl.custombetentities;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableList;
import com.sportradar.uf.custombet.datamodel.CAPIFilteredOutcomeType;
import com.sportradar.uf.custombet.datamodel.CAPIMarketType;
import com.sportradar.uf.custombet.datamodel.CAPIOutcomeType;
import com.sportradar.unifiedodds.sdk.custombetentities.Market;
import com.sportradar.unifiedodds.sdk.custombetentities.OutcomeFilter;

import java.util.List;

/**
 * Implements methods used to access available selections for the market
 */
public class OutcomeFilterImpl implements OutcomeFilter {
    /**
     * the id of the outcome
     */
    private final String id;

    /**
     * The value indicating if the market is in conflict
     */
    private final Boolean isConflict;

    OutcomeFilterImpl(CAPIFilteredOutcomeType outcomeType) {
        Preconditions.checkNotNull(outcomeType);

        this.id = outcomeType.getId();
        this.isConflict = outcomeType.isConflict();
    }

    @Override
    public String getId() {
        return id;
    }

    @Override
    public Boolean isConflict() { return isConflict; }
}
