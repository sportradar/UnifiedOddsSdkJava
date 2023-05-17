/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */

package com.sportradar.unifiedodds.sdk.custombetentities;

import java.util.List;

/**
 * Provides a market
 */
public interface Market {
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
    List<String> getOutcomes();
}
