/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */
package com.sportradar.unifiedodds.sdk.entities.custombet;

/**
 * Represents a selection for a prebuilt bet
 */
public interface PrebuiltBetSelection {
    /**
     * Returns the market ID
     *
     * @return the market ID
     */
    int getMarketId();

    /**
     * Returns the outcome ID
     *
     * @return the outcome ID
     */
    String getOutcomeId();

    /**
     * Returns the specifiers
     *
     * @return the specifiers, or null if not set
     */
    String getSpecifiers();
}
