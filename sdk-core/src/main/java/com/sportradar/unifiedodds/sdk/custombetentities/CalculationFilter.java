/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */

package com.sportradar.unifiedodds.sdk.custombetentities;

import java.util.Date;
import java.util.List;

/**
 * Provides a probability calculation
 */
public interface CalculationFilter {
    /**
     * Gets the odds
     *
     * @return the odds
     */
    double getOdds();

    /**
     * Gets the probability
     *
     * @return the probability
     */
    double getProbability();

    /**
     * Returns list of available selections
     * @return list of available selections
     */
    List<AvailableSelectionsFilter> getAvailableSelections();

    /**
     * Returns the date when API response was generated
     * @return the date when API response was generated
     */
    Date getGeneratedAt();

    /**
     * Returns the value if harmonization method was applied
     *
     * @return the value if harmonization method was applied
     */
    default Boolean isHarmonization() {
        throw new UnsupportedOperationException("Method not implemented");
    }
}
