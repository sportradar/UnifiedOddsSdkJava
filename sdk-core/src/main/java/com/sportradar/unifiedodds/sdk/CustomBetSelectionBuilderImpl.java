/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */

package com.sportradar.unifiedodds.sdk;

import com.sportradar.unifiedodds.sdk.custombetentities.Selection;
import com.sportradar.unifiedodds.sdk.impl.custombetentities.SelectionImpl;
import com.sportradar.utils.Urn;

/**
 * The basic implementation of the {@link CustomBetSelectionBuilder}
 */
@SuppressWarnings({ "HiddenField", "VariableDeclarationUsageDistance" })
public class CustomBetSelectionBuilderImpl implements CustomBetSelectionBuilder {

    private Urn eventId;
    private int marketId;
    private String outcomeId;
    private String specifiers;
    private Double odds;

    @Override
    public CustomBetSelectionBuilder setEventId(Urn eventId) {
        this.eventId = eventId;
        return this;
    }

    @Override
    public CustomBetSelectionBuilder setMarketId(int marketId) {
        this.marketId = marketId;
        return this;
    }

    @Override
    public CustomBetSelectionBuilder setOutcomeId(String outcomeId) {
        this.outcomeId = outcomeId;
        return this;
    }

    @Override
    public CustomBetSelectionBuilder setSpecifiers(String specifiers) {
        this.specifiers = specifiers;
        return this;
    }

    @Override
    public CustomBetSelectionBuilder setOdds(double odds) {
        this.odds = odds;
        return this;
    }

    @Override
    public Selection build() {
        Selection selection = new SelectionImpl(eventId, marketId, outcomeId, specifiers, odds);
        eventId = null;
        marketId = 0;
        outcomeId = null;
        specifiers = null;
        odds = null;
        return selection;
    }

    @Override
    public Selection build(Urn eventId, int marketId, String specifiers, String outcomeId) {
        return build(eventId, marketId, specifiers, outcomeId, null);
    }

    @Override
    public Selection build(Urn eventId, int marketId, String specifiers, String outcomeId, Double odds) {
        this.eventId = eventId;
        this.marketId = marketId;
        this.outcomeId = outcomeId;
        this.specifiers = specifiers;
        this.odds = odds;
        return build();
    }
}
