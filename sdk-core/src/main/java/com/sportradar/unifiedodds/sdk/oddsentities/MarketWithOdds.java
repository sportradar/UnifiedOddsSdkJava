/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */

package com.sportradar.unifiedodds.sdk.oddsentities;

import java.util.List;

/**
 * This represents the odds changes for one market in the {@link OddsChange} message.
 */
public interface MarketWithOdds extends Market {
    /**
     * An indication if the associated market is active
     *
     * @return an indication if the associated market is active
     */
    MarketStatus getStatus();

    /**
     * Returns a list of odds for the different available market outcomes
     *
     * @return a list of odds for the different outcomes for this market
     */
    List<OutcomeOdds> getOutcomeOdds();

    /**
     * Only applicable if multiple market lines of the same market type is provided in the odds
     * update
     *
     * @return in case we provide multiple lines for the same market - this reports if this line is
     * the recommended one (this often but not always means the most balanced)
     */
    boolean isFavourite();

    /**
     * Returns a {@link MarketMetadata} instance which contains additional market information
     *
     * @return a {@link MarketMetadata} instance which contains additional market information
     */
    MarketMetadata getMarketMetadata();
}
