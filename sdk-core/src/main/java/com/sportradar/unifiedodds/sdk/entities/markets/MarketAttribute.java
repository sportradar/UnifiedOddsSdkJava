/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */

package com.sportradar.unifiedodds.sdk.entities.markets;

/**
 * Contains a market attribute name and description
 */
public interface MarketAttribute {
    /**
     * Returns the attribute name
     *
     * @return - the attribute name
     */
    String getName();

    /**
     * Returns the attribute description
     *
     * @return - the attribute description
     */
    String getDescription();
}
