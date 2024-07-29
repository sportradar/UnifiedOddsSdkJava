/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */

package com.sportradar.unifiedodds.sdk.entities.status;

import java.util.List;

/**
 * Defines methods to access match specific statistics
 */
public interface MatchStatistics extends CompetitionStatistics {
    List<TeamStatistics> getTotalStatistics();

    List<PeriodStatistics> getPeriodStatistics();
}
