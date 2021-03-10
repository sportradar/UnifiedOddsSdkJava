/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */

package com.sportradar.unifiedodds.sdk.oddsentities;

import java.time.Instant;
import java.util.Date;

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

    /**
     * Returns the start time of the event (as epoch timestamp)
     * @return the start time of the event (as epoch timestamp)
     */
    default Long getStartTime() { return null; }

    /**
     * Returns the end time of the event (as epoch timestamp)
     * @return the end time of the event (as epoch timestamp)
     */
    default Long getEndTime() { return null; }

    /**
     * Returns the Italian AAMS id for this outright
     * @return the Italian AAMS id for this outright
     */
    default Long getAamsId()  { return null; }
}
