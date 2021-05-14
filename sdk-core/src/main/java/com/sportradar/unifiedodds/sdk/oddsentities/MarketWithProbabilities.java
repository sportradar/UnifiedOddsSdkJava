/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */

package com.sportradar.unifiedodds.sdk.oddsentities;

import java.util.List;

/**
 * Defines methods used to access data of markets with probabilities
 */
public interface MarketWithProbabilities extends Market {
    /**
     * Returns a {@link CashOutStatus} enum which indicates the availability of cashout
     *
     * @return if available, a {@link CashOutStatus} enum which indicates the availability of cashout; otherwise null
     */
    CashOutStatus getCashOutStatus();

    /**
     * An indication if the associated market is active
     *
     * @return an indication if the associated market is active
     */
    MarketStatus getStatus();

    /**
     * Returns a list of probabilities for the different available market outcomes
     *
     * @return a list of probabilities for the different outcomes for this market
     */
    List<OutcomeProbabilities> getOutcomeProbabilities();

    /**
     * Returns a {@link MarketMetadata} instance which contains additional market information
     *
     * @return a {@link MarketMetadata} instance which contains additional market information
     */
    default MarketMetadata getMarketMetadata(){ throw new UnsupportedOperationException("Method not implemented. Use derived type."); }
}
