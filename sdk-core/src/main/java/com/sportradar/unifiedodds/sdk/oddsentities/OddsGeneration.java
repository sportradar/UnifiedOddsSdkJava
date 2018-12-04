/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */

package com.sportradar.unifiedodds.sdk.oddsentities;

/**
 * Provided by the prematch odds producer only, and contains a few key-parameters that can be used in a client’s own special odds model, or even offer spread betting bets based on it.
 */
public interface OddsGeneration {

    /**
     * Returns the expected totals (how many goals are expected in total?)
     * @return the expected totals (how many goals are expected in total?)
     */
    Double getExpectedTotals();

    /**
     * Returns the expected supremacy (how big is the expected goal supremacy)
     * @return the expected supremacy (how big is the expected goal supremacy)
     */
    Double getExpectedSupremacy();
}
