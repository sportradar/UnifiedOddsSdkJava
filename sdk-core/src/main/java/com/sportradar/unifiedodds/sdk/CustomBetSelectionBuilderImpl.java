/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */

package com.sportradar.unifiedodds.sdk;

import com.sportradar.unifiedodds.sdk.custombetentities.Selection;
import com.sportradar.unifiedodds.sdk.impl.custombetentities.SelectionImpl;
import com.sportradar.utils.URN;

/**
 * The basic implementation of the {@link CustomBetSelectionBuilder}
 */
public class CustomBetSelectionBuilderImpl implements CustomBetSelectionBuilder {
    private URN eventId;
    private int marketId;
    private String outcomeId;
    private String specifiers;

    @Override
    public CustomBetSelectionBuilder setEventId(URN eventId) {
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
    public Selection build() {
        Selection selection = new SelectionImpl(eventId, marketId, outcomeId, specifiers);
        eventId = null;
        marketId = 0;
        outcomeId = null;
        specifiers = null;
        return selection;
    }

    @Override
    public Selection build(URN eventId, int marketId, String specifiers, String outcomeId) {
        this.eventId = eventId;
        this.marketId = marketId;
        this.outcomeId = outcomeId;
        this.specifiers = specifiers;
        return build();
    }
}
