/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */
package com.sportradar.unifiedodds.sdk.entities.custombet;

import java.util.List;

/**
 * Represents a recommendation for prebuilt bets
 */
public interface Recommendation {
    /**
     * Returns the list of selections for this recommendation
     *
     * @return the list of selections
     */
    List<PrebuiltBetSelection> getSelections();

    /**
     * Returns the odds for this recommendation
     *
     * @return the odds
     */
    double getOdds();

    /**
     * Returns the probability for this recommendation
     *
     * @return the probability
     */
    double getProbability();
}
