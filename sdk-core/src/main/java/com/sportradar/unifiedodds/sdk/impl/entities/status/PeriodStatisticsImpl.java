/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */

package com.sportradar.unifiedodds.sdk.impl.entities.status;

import com.google.common.base.Preconditions;
import com.sportradar.unifiedodds.sdk.entities.status.PeriodStatistics;
import com.sportradar.unifiedodds.sdk.entities.status.TeamStatistics;
import com.sportradar.unifiedodds.sdk.impl.dto.PeriodStatisticsDto;
import com.sportradar.unifiedodds.sdk.impl.dto.TeamStatisticsDto;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Provides methods used to access period specifics statistics
 */
@SuppressWarnings({ "MagicNumber", "UnnecessaryParentheses" })
class PeriodStatisticsImpl implements PeriodStatistics {

    private final PeriodStatisticsDto stats;

    PeriodStatisticsImpl(PeriodStatisticsDto stats) {
        Preconditions.checkNotNull(stats);

        this.stats = stats;
    }

    /**
     * Returns the name of the round to which the statistics belong to
     *
     * @return the name of the round to which the statistics belong to
     */
    @Override
    public String getPeriodName() {
        return stats.getPeriodName();
    }

    /**
     * Returns a list of specific team statistics related to the round indicated by the {@link #getPeriodName()}
     *
     * @return a list of specific team statistics related to the associated round
     * @since 2.0.1
     */
    @Override
    public List<TeamStatistics> getTeamStatistics() {
        return stats.getTeamStatisticDtos() == null
            ? null
            : stats.getTeamStatisticDtos().stream().map(TeamStatisticsImpl::new).collect(Collectors.toList());
    }

    @Override
    public String toString() {
        if (stats == null) {
            return "PeriodStatisticsImpl{}";
        }

        String teamStatisticsResult = "";
        for (TeamStatisticsDto teamStatistics : stats.getTeamStatisticDtos()) {
            teamStatisticsResult += " | " + teamStatistics.toString();
        }
        if (teamStatisticsResult.length() > 3) {
            teamStatisticsResult = teamStatisticsResult.substring(3);
        }

        return (
            "PeriodStatisticsImpl{" +
            "periodName=" +
            stats.getPeriodName() +
            ", teamStatistics=[" +
            teamStatisticsResult +
            "]" +
            '}'
        );
    }
}
