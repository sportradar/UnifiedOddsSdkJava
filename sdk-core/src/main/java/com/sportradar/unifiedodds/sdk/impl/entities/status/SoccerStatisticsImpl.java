/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */

package com.sportradar.unifiedodds.sdk.impl.entities.status;

import com.google.common.base.Preconditions;
import com.sportradar.unifiedodds.sdk.entities.status.PeriodStatistics;
import com.sportradar.unifiedodds.sdk.entities.status.SoccerStatistics;
import com.sportradar.unifiedodds.sdk.entities.status.TeamStatistics;
import com.sportradar.unifiedodds.sdk.impl.dto.SportEventStatisticsDto;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Provides access to soccer match specific statistics
 */
public class SoccerStatisticsImpl implements SoccerStatistics {

    private final SportEventStatisticsDto statisticsDto;

    SoccerStatisticsImpl(SportEventStatisticsDto statisticsDto) {
        Preconditions.checkNotNull(statisticsDto);

        this.statisticsDto = statisticsDto;
    }

    /**
     * Returns a list of complete team statistics data
     *
     * @return a list of complete team statistics data
     */
    @Override
    public List<TeamStatistics> getTotalStatistics() {
        return statisticsDto.getTotalStatisticsDtos() == null
            ? null
            : statisticsDto
                .getTotalStatisticsDtos()
                .stream()
                .map(TeamStatisticsImpl::new)
                .collect(Collectors.toList());
    }

    /**
     * Returns a list of separate period statistics
     *
     * @return a list of separate period statistics
     */
    @Override
    public List<PeriodStatistics> getPeriodStatistics() {
        return statisticsDto.getPeriodStatisticDtos() == null
            ? null
            : statisticsDto
                .getPeriodStatisticDtos()
                .stream()
                .map(PeriodStatisticsImpl::new)
                .collect(Collectors.toList());
    }
}
