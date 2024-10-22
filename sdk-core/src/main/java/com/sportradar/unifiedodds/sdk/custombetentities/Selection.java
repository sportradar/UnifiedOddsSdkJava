/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */

package com.sportradar.unifiedodds.sdk.custombetentities;

import com.sportradar.utils.Urn;

/**
 * Provides a requested selection
 */
public interface Selection {
    /**
     * Gets the event id
     *
     * @return the {@link Urn} of the event
     */
    Urn getEventId();

    /**
     * Gets the market id
     *
     * @return the market id
     */
    int getMarketId();

    /**
     * Gets the specifiers
     *
     * @return the specifiers
     */
    String getSpecifiers();

    /**
     * Gets the outcome id
     *
     * @return the outcome id
     */
    String getOutcomeId();

    /**
     * Gets the odds
     *
     * @return the odds
     */
    default Double getOdds() {
        throw new UnsupportedOperationException("Method not implemented");
    }
}
