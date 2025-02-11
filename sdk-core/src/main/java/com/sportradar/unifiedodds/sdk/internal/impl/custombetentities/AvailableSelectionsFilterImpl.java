/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */

package com.sportradar.unifiedodds.sdk.internal.impl.custombetentities;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableList;
import com.sportradar.uf.custombet.datamodel.CapiFilteredEventType;
import com.sportradar.unifiedodds.sdk.entities.custombet.AvailableSelectionsFilter;
import com.sportradar.unifiedodds.sdk.entities.custombet.MarketFilter;
import com.sportradar.utils.Urn;
import java.util.List;

/**
 * Implements methods used to access available selections for the event
 */
public class AvailableSelectionsFilterImpl implements AvailableSelectionsFilter {

    /**
     * An {@link Urn} specifying the id of the event
     */
    private final Urn eventId;

    /**
     * An {@link List} specifying available markets for the event
     */
    private final List<MarketFilter> markets;

    private final String generatedAt;

    public AvailableSelectionsFilterImpl(CapiFilteredEventType eventType, String generatedAt) {
        Preconditions.checkNotNull(eventType);

        this.eventId = Urn.parse(eventType.getId());

        this.markets =
            (eventType.getMarkets() != null)
                ? eventType
                    .getMarkets()
                    .getMarkets()
                    .stream()
                    .map(MarketFilterImpl::new)
                    .collect(ImmutableList.toImmutableList())
                : ImmutableList.of();

        this.generatedAt = generatedAt;
    }

    @Override
    public Urn getEventId() {
        return eventId;
    }

    @Override
    public List<MarketFilter> getMarkets() {
        return markets;
    }
}
