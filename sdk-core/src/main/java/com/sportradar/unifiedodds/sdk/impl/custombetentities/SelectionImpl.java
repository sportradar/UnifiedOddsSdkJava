/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */

package com.sportradar.unifiedodds.sdk.impl.custombetentities;

import com.google.common.base.Preconditions;
import com.sportradar.unifiedodds.sdk.custombetentities.Selection;
import com.sportradar.utils.URN;

/**
 * Implements methods used to provide an requested selection
 */
public class SelectionImpl implements Selection {

    private final URN eventId;
    private final int marketId;
    private final String specifiers;
    private final String outcomeId;

    public SelectionImpl(URN eventId, int marketId, String specifiers, String outcomeId) {
        Preconditions.checkNotNull(eventId);
        Preconditions.checkNotNull(outcomeId);

        this.eventId = eventId;
        this.marketId = marketId;
        this.specifiers = specifiers;
        this.outcomeId = outcomeId;
    }

    /**
     * Gets the event id
     *
     * @return the {@link URN} of the event
     */
    @Override
    public URN getEventId() {
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
     * Gets the specifiers
     *
     * @return the specifiers
     */
    @Override
    public String getSpecifiers() {
        return specifiers;
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
}
