/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */

package com.sportradar.unifiedodds.sdk.entities.custombet;

import java.util.List;

/**
 * Provides a market
 */
public interface MarketFilter {
    /**
     * Returns the id of the market
     *
     * @return the id of the market
     */
    int getId();

    /**
     * Returns the specifiers for this market
     *
     * @return the specifiers for this market
     */
    String getSpecifiers();

    /**
     * Returns outcomes for this market
     *
     * @return outcomes for this market
     */
    List<OutcomeFilter> getOutcomes();

    /**
     * Returns the value indicating if this market is in conflict
     * @return the value indicating if this market is in conflict
     */
    Boolean isConflict();
}
