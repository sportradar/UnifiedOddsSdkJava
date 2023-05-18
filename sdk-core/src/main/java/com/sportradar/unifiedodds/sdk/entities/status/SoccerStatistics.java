/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */

package com.sportradar.unifiedodds.sdk.entities.status;

import java.util.List;

/**
 * Defines methods to access soccer match specific statistics
 */
public interface SoccerStatistics extends MatchStatistics {
    /**
     * Returns a list of complete team statistics data
     *
     * @return a list of complete team statistics data
     */
    List<TeamStatistics> getTotalStatistics();

    /**
     * Returns a list of separate period statistics
     *
     * @return a list of separate period statistics
     */
    List<PeriodStatistics> getPeriodStatistics();
}
