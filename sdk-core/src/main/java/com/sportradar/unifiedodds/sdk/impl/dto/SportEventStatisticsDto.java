/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */

package com.sportradar.unifiedodds.sdk.impl.dto;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableList;
import com.sportradar.uf.datamodel.UfStatisticsType;
import com.sportradar.uf.sportsapi.datamodel.SapiMatchStatistics;
import com.sportradar.unifiedodds.sdk.entities.HomeAway;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * A data-transfer-object representation for sport event status statistics. The status can be receiver trough messages or fetched
 * from the API
 */
@SuppressWarnings({ "BooleanExpressionComplexity", "CyclomaticComplexity", "NPathComplexity" })
public class SportEventStatisticsDto {

    private final List<TeamStatisticsDto> totalStatisticsDtos;
    private final List<PeriodStatisticsDto> periodStatisticDtos;

    /**
     * Constructs a new statistics instance from the data obtained from the API
     *
     * @param statistics the API response statistics data object
     * @param homeAwayMap a map containing data about home/away competitors, this data is available only for events of type match
     */
    SportEventStatisticsDto(SapiMatchStatistics statistics, Map<HomeAway, String> homeAwayMap) {
        Preconditions.checkNotNull(statistics);

        totalStatisticsDtos =
            (
                    statistics.getTotals() != null &&
                    statistics.getTotals().getTeams().size() == 1 &&
                    statistics.getTotals().getTeams().get(0).getTeam().size() == 2
                )
                ? statistics
                    .getTotals()
                    .getTeams()
                    .get(0)
                    .getTeam()
                    .stream()
                    .map(t -> new TeamStatisticsDto(t, homeAwayMap))
                    .collect(Collectors.toList())
                : null;

        periodStatisticDtos =
            (statistics.getPeriods() != null)
                ? statistics
                    .getPeriods()
                    .getPeriod()
                    .stream()
                    .map(p -> new PeriodStatisticsDto(p, homeAwayMap))
                    .collect(Collectors.toList())
                : null;
    }

    /**
     * Constructs a new statistics instance from the data obtained from an AMQP message
     *
     * @param statistics the message statistics data object
     */
    SportEventStatisticsDto(UfStatisticsType statistics) {
        Preconditions.checkNotNull(statistics);

        totalStatisticsDtos = new ArrayList<>();
        totalStatisticsDtos.add(
            new TeamStatisticsDto(
                null,
                null,
                HomeAway.Home,
                statistics.getYellowCards() == null ? null : statistics.getYellowCards().getHome(),
                statistics.getRedCards() == null ? null : statistics.getRedCards().getHome(),
                statistics.getYellowRedCards() == null ? null : statistics.getYellowRedCards().getHome(),
                statistics.getCorners() == null ? null : statistics.getCorners().getHome(),
                statistics.getGreenCards() == null ? null : statistics.getGreenCards().getHome()
            )
        );
        totalStatisticsDtos.add(
            new TeamStatisticsDto(
                null,
                null,
                HomeAway.Away,
                statistics.getYellowCards() == null ? null : statistics.getYellowCards().getAway(),
                statistics.getRedCards() == null ? null : statistics.getRedCards().getAway(),
                statistics.getYellowRedCards() == null ? null : statistics.getYellowRedCards().getAway(),
                statistics.getCorners() == null ? null : statistics.getCorners().getAway(),
                statistics.getGreenCards() == null ? null : statistics.getGreenCards().getAway()
            )
        );

        periodStatisticDtos = null;
    }

    public SportEventStatisticsDto(
        List<TeamStatisticsDto> totalStatisticsDtos,
        List<PeriodStatisticsDto> periodStatisticDtos
    ) {
        this.totalStatisticsDtos = totalStatisticsDtos;
        this.periodStatisticDtos = periodStatisticDtos;
    }

    public List<TeamStatisticsDto> getTotalStatisticsDtos() {
        return totalStatisticsDtos == null ? null : ImmutableList.copyOf(totalStatisticsDtos);
    }

    public List<PeriodStatisticsDto> getPeriodStatisticDtos() {
        return periodStatisticDtos == null ? null : ImmutableList.copyOf(periodStatisticDtos);
    }
}
