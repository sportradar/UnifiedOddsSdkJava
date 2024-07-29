/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */

package com.sportradar.unifiedodds.sdk.entities.status;

import java.util.List;

/**
 * Defines methods to access soccer match specific statistics
 *
 * @deprecated Soccer was considered a special sport, and the only sport exposing statistics
 *   however currently @MatchStatistics also provides total and period statistics,
 *   making this class redundant
 */
@Deprecated
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
