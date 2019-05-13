/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */

package com.sportradar.unifiedodds.sdk;

import com.sportradar.unifiedodds.sdk.custombetentities.Selection;
import com.sportradar.unifiedodds.sdk.impl.custombetentities.SelectionImpl;
import com.sportradar.utils.URN;

/**
 * The basic implementation of the {@link SelectionBuilder}
 */
public class SelectionBuilderImpl implements SelectionBuilder {
    private URN eventId;
    private int marketId;
    private String specifiers;
    private String outcomeId;

    @Override
    public SelectionBuilder setEventId(URN eventId) {
        this.eventId = eventId;
        return this;
    }

    @Override
    public SelectionBuilder setMarketId(int marketId) {
        this.marketId = marketId;
        return this;
    }

    @Override
    public SelectionBuilder setSpecifiers(String specifiers) {
        this.specifiers = specifiers;
        return this;
    }

    @Override
    public SelectionBuilder setOutcomeId(String outcomeId) {
        this.outcomeId = outcomeId;
        return this;
    }

    @Override
    public Selection build() {
        Selection selection = new SelectionImpl(eventId, marketId, specifiers, outcomeId);
        eventId = null;
        marketId = 0;
        specifiers = null;
        outcomeId = null;
        return selection;
    }

    @Override
    public Selection build(URN eventId, int marketId, String specifiers, String outcomeId) {
        this.eventId = eventId;
        this.marketId = marketId;
        this.specifiers = specifiers;
        this.outcomeId = outcomeId;
        return build();
    }
}
