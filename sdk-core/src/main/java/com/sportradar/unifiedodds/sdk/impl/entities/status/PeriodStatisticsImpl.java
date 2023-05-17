/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */

package com.sportradar.unifiedodds.sdk.impl.entities.status;

import com.google.common.base.Preconditions;
import com.sportradar.unifiedodds.sdk.entities.status.PeriodStatistics;
import com.sportradar.unifiedodds.sdk.entities.status.TeamStatistics;
import com.sportradar.unifiedodds.sdk.impl.dto.PeriodStatisticsDTO;
import com.sportradar.unifiedodds.sdk.impl.dto.TeamStatisticsDTO;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Provides methods used to access period specifics statistics
 */
@SuppressWarnings({ "MagicNumber", "UnnecessaryParentheses" })
class PeriodStatisticsImpl implements PeriodStatistics {

    private final PeriodStatisticsDTO stats;

    PeriodStatisticsImpl(PeriodStatisticsDTO stats) {
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
     */
    @Override
    public List<TeamStatistics> getTeamStatisticDTOS() {
        return getTeamStatistics();
    }

    /**
     * Returns a list of specific team statistics related to the round indicated by the {@link #getPeriodName()}
     *
     * @return a list of specific team statistics related to the associated round
     * @since 2.0.1
     */
    @Override
    public List<TeamStatistics> getTeamStatistics() {
        return stats.getTeamStatisticDTOs() == null
            ? null
            : stats.getTeamStatisticDTOs().stream().map(TeamStatisticsImpl::new).collect(Collectors.toList());
    }

    @Override
    public String toString() {
        if (stats == null) {
            return "PeriodStatisticsImpl{}";
        }

        String teamStatisticsResult = "";
        for (TeamStatisticsDTO teamStatistics : stats.getTeamStatisticDTOs()) {
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
