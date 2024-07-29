/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */

package com.sportradar.unifiedodds.sdk.impl.entities.status;

import static java.util.Arrays.asList;

import com.google.common.base.Preconditions;
import com.sportradar.unifiedodds.sdk.entities.status.MatchStatistics;
import com.sportradar.unifiedodds.sdk.entities.status.PeriodStatistics;
import com.sportradar.unifiedodds.sdk.entities.status.SoccerStatistics;
import com.sportradar.unifiedodds.sdk.entities.status.TeamStatistics;
import com.sportradar.unifiedodds.sdk.impl.dto.PeriodStatisticsDto;
import com.sportradar.unifiedodds.sdk.impl.dto.SportEventStatisticsDto;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class MatchStatisticsImpl implements MatchStatistics {

    private final SportEventStatisticsDto statisticsDto;

    MatchStatisticsImpl(SportEventStatisticsDto statisticsDto) {
        Preconditions.checkNotNull(statisticsDto);

        this.statisticsDto = statisticsDto;
    }

    /**
     * team statistics
     */
    @Override
    public List<TeamStatistics> getTotalStatistics() {
        return statisticsDto
            .getTotalStatisticsDtos()
            .stream()
            .map(TeamStatisticsImpl::new)
            .collect(Collectors.toList());
    }

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
