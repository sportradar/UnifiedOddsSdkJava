/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */

package com.sportradar.unifiedodds.sdk.internal.impl.custombetentities;

import com.google.common.base.Preconditions;
import com.sportradar.uf.custombet.datamodel.CapiFilteredOutcomeType;
import com.sportradar.unifiedodds.sdk.entities.custombet.OutcomeFilter;

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

    OutcomeFilterImpl(CapiFilteredOutcomeType outcomeType) {
        Preconditions.checkNotNull(outcomeType);

        this.id = outcomeType.getId();
        this.isConflict = outcomeType.isConflict();
    }

    @Override
    public String getId() {
        return id;
    }

    @Override
    public Boolean isConflict() {
        return isConflict;
    }
}
