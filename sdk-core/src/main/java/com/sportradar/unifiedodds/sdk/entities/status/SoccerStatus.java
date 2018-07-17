/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */

package com.sportradar.unifiedodds.sdk.entities.status;

/**
 * Defines methods used to access soccer match specific status attributes
 */
public interface SoccerStatus extends MatchStatus {
    /**
     * Returns the associated soccer match statistics
     *
     * @return the associated soccer match statistics, if available; otherwise null;
     */
    SoccerStatistics getStatistics();
}
