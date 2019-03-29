/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */

package com.sportradar.unifiedodds.sdk.custombetentities;

/**
 * Provides a probability calculation
 */
public interface Calculation {

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
}
