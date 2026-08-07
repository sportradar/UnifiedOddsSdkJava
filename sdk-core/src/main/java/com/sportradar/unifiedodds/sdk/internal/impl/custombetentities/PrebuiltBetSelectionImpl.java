/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */
package com.sportradar.unifiedodds.sdk.internal.impl.custombetentities;

import com.sportradar.uf.custombet.datamodel.CapiPreBuiltBetsSelectionType;
import com.sportradar.unifiedodds.sdk.entities.custombet.PrebuiltBetSelection;

/**
 * Implementation of {@link PrebuiltBetSelection}
 */
public class PrebuiltBetSelectionImpl implements PrebuiltBetSelection {

    private final int marketId;
    private final String outcomeId;
    private final String specifiers;

    public PrebuiltBetSelectionImpl(CapiPreBuiltBetsSelectionType selection) {
        this.marketId = selection.getMarketId();
        this.outcomeId = selection.getOutcomeId();
        this.specifiers = selection.getSpecifiers();
    }

    @Override
    public int getMarketId() {
        return marketId;
    }

    @Override
    public String getOutcomeId() {
        return outcomeId;
    }

    @Override
    public String getSpecifiers() {
        return specifiers;
    }
}
