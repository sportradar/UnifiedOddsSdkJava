/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */

package com.sportradar.unifiedodds.sdk.internal.impl.custombetentities;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableList;
import com.sportradar.uf.custombet.datamodel.CapiAvailableSelections;
import com.sportradar.uf.custombet.datamodel.CapiEventType;
import com.sportradar.uf.custombet.datamodel.CapiMarketsType;
import com.sportradar.unifiedodds.sdk.entities.custombet.AvailableSelections;
import com.sportradar.unifiedodds.sdk.entities.custombet.Market;
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
    private final Urn eventId;

    /**
     * An {@link List} specifying available markets for the event
     */
    private final List<Market> markets;

    private final String generatedAt;

    public AvailableSelectionsImpl(CapiAvailableSelections availableSelections) {
        Preconditions.checkNotNull(availableSelections);

        this.eventId = Urn.parse(availableSelections.getEvent().getId());

        CapiMarketsType capiMarkets = availableSelections.getEvent().getMarkets();
        this.markets =
            (capiMarkets != null)
                ? capiMarkets
                    .getMarkets()
                    .stream()
                    .map(MarketImpl::new)
                    .collect(ImmutableList.toImmutableList())
                : ImmutableList.of();

        this.generatedAt = availableSelections.getGeneratedAt();
    }

    public AvailableSelectionsImpl(CapiEventType eventType, String generatedAt) {
        Preconditions.checkNotNull(eventType);

        this.eventId = Urn.parse(eventType.getId());

        CapiMarketsType capiMarkets = eventType.getMarkets();
        this.markets =
            (capiMarkets != null)
                ? capiMarkets
                    .getMarkets()
                    .stream()
                    .map(MarketImpl::new)
                    .collect(ImmutableList.toImmutableList())
                : ImmutableList.of();

        this.generatedAt = generatedAt;
    }

    @Override
    public Urn getEventId() {
        return eventId;
    }

    @Override
    public List<Market> getMarkets() {
        return markets;
    }
}
