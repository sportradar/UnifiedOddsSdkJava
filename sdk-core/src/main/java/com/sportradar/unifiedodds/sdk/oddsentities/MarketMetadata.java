/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */

package com.sportradar.unifiedodds.sdk.oddsentities;

/**
 * Defines methods used to access market metadata values
 */
public interface MarketMetadata {
    /**
     * Returns a timestamp in UTC when to betstop the associated market. Typically used for outrights and typically is
     * the start-time of the event the market refers to.
     *
     * @return a timestamp in UTC in which to bestop the associated market.
     */
    Long getNextBetstop();
}
