/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */

package com.sportradar.unifiedodds.sdk.impl.custombetentities;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableList;
import com.sportradar.uf.custombet.datamodel.CAPIAvailableSelections;
import com.sportradar.uf.custombet.datamodel.CAPIEventType;
import com.sportradar.uf.custombet.datamodel.CAPIMarketsType;
import com.sportradar.unifiedodds.sdk.custombetentities.AvailableSelections;
import com.sportradar.unifiedodds.sdk.custombetentities.Market;
import com.sportradar.utils.URN;
import java.util.List;

/**
 * Implements methods used to access available selections for the event
 */
@SuppressWarnings({ "HiddenField" })
public class AvailableSelectionsImpl implements AvailableSelections {

    /**
     * An {@link URN} specifying the id of the event
     */
    private final URN event;

    /**
     * An {@link List} specifying available markets for the event
     */
    private final List<Market> markets;

    private final String generatedAt;

    public AvailableSelectionsImpl(CAPIAvailableSelections availableSelections) {
        Preconditions.checkNotNull(availableSelections);

        this.event = URN.parse(availableSelections.getEvent().getId());

        CAPIMarketsType markets = availableSelections.getEvent().getMarkets();
        this.markets =
            (markets != null)
                ? markets.getMarkets().stream().map(MarketImpl::new).collect(ImmutableList.toImmutableList())
                : ImmutableList.of();

        this.generatedAt = availableSelections.getGeneratedAt();
    }

    public AvailableSelectionsImpl(CAPIEventType eventType, String generatedAt) {
        Preconditions.checkNotNull(eventType);

        this.event = URN.parse(eventType.getId());

        CAPIMarketsType markets = eventType.getMarkets();
        this.markets =
            (markets != null)
                ? markets.getMarkets().stream().map(MarketImpl::new).collect(ImmutableList.toImmutableList())
                : ImmutableList.of();

        this.generatedAt = generatedAt;
    }

    @Override
    public URN getEvent() {
        return event;
    }

    @Override
    public List<Market> getMarkets() {
        return markets;
    }
}
