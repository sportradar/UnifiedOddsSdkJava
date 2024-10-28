/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */

package com.sportradar.unifiedodds.sdk.impl.custombetentities;

import com.google.common.base.Preconditions;
import com.sportradar.unifiedodds.sdk.custombetentities.Selection;
import com.sportradar.utils.Urn;

/**
 * Implements methods used to provide a requested selection
 */
public class SelectionImpl implements Selection {

    private final Urn eventId;
    private final int marketId;
    private final String outcomeId;
    private final String specifiers;
    private final Double odds;

    public SelectionImpl(Urn eventId, int marketId, String outcomeId, String specifiers) {
        this(eventId, marketId, outcomeId, specifiers, null);
    }

    public SelectionImpl(Urn eventId, int marketId, String outcomeId, String specifiers, Double odds) {
        Preconditions.checkNotNull(eventId);
        Preconditions.checkArgument(marketId > 0);
        Preconditions.checkNotNull(outcomeId);

        this.eventId = eventId;
        this.marketId = marketId;
        this.outcomeId = outcomeId;
        this.specifiers = specifiers;
        this.odds = odds;
    }

    /**
     * Gets the event id
     *
     * @return the {@link Urn} of the event
     */
    @Override
    public Urn getEventId() {
        return eventId;
    }

    /**
     * Gets the market id
     *
     * @return the market id
     */
    @Override
    public int getMarketId() {
        return marketId;
    }

    /**
     * Gets the outcome id
     *
     * @return the outcome id
     */
    @Override
    public String getOutcomeId() {
        return outcomeId;
    }

    @Override
    public Double getOdds() {
        return odds;
    }

    /**
     * Gets the specifiers
     *
     * @return the specifiers
     */
    @Override
    public String getSpecifiers() {
        return specifiers;
    }
}
