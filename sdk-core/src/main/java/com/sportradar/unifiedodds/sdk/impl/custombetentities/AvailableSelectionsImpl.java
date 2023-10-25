/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */

package com.sportradar.unifiedodds.sdk.impl.custombetentities;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableList;
import com.sportradar.uf.custombet.datamodel.CapiAvailableSelections;
import com.sportradar.uf.custombet.datamodel.CapiEventType;
import com.sportradar.uf.custombet.datamodel.CapiMarketsType;
import com.sportradar.unifiedodds.sdk.custombetentities.AvailableSelections;
import com.sportradar.unifiedodds.sdk.custombetentities.Market;
import com.sportradar.utils.Urn;
import java.util.List;

/**
 * Implements methods used to access available selections for the event
 */
@SuppressWarnings({ "HiddenField" })
public class AvailableSelectionsImpl implements AvailableSelections {

    /**
     * An {@link Urn} specifying the id of the event
     */
    private final Urn event;

    /**
     * An {@link List} specifying available markets for the event
     */
    private final List<Market> markets;

    private final String generatedAt;

    public AvailableSelectionsImpl(CapiAvailableSelections availableSelections) {
        Preconditions.checkNotNull(availableSelections);

        this.event = Urn.parse(availableSelections.getEvent().getId());

        CapiMarketsType markets = availableSelections.getEvent().getMarkets();
        this.markets =
            (markets != null)
                ? markets.getMarkets().stream().map(MarketImpl::new).collect(ImmutableList.toImmutableList())
                : ImmutableList.of();

        this.generatedAt = availableSelections.getGeneratedAt();
    }

    public AvailableSelectionsImpl(CapiEventType eventType, String generatedAt) {
        Preconditions.checkNotNull(eventType);

        this.event = Urn.parse(eventType.getId());

        CapiMarketsType markets = eventType.getMarkets();
        this.markets =
            (markets != null)
                ? markets.getMarkets().stream().map(MarketImpl::new).collect(ImmutableList.toImmutableList())
                : ImmutableList.of();

        this.generatedAt = generatedAt;
    }

    @Override
    public Urn getEvent() {
        return event;
    }

    @Override
    public List<Market> getMarkets() {
        return markets;
    }
}
