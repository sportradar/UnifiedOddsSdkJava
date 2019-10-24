/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */

package com.sportradar.unifiedodds.sdk.impl.dto;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableList;
import com.sportradar.uf.datamodel.UFStatisticsType;
import com.sportradar.uf.sportsapi.datamodel.SAPIMatchStatistics;
import com.sportradar.unifiedodds.sdk.entities.HomeAway;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * A data-transfer-object representation for sport event status statistics. The status can be receiver trough messages or fetched
 * from the API
 */
public class SportEventStatisticsDTO {
    private final List<TeamStatisticsDTO> totalStatisticsDTOs;
    private final List<PeriodStatisticsDTO> periodStatisticDTOs;

    /**
     * Constructs a new statistics instance from the data obtained from the API
     *
     * @param statistics the API response statistics data object
     * @param homeAwayMap a map containing data about home/away competitors, this data is available only for events of type match
     */
    SportEventStatisticsDTO(SAPIMatchStatistics statistics, Map<HomeAway, String> homeAwayMap) {
        Preconditions.checkNotNull(statistics);

        totalStatisticsDTOs = (statistics.getTotals() != null &&
                                statistics.getTotals().getTeams() != null &&
                                statistics.getTotals().getTeams().size() == 1 &&
                                statistics.getTotals().getTeams().get(0).getTeam() != null &&
                                statistics.getTotals().getTeams().get(0).getTeam().size() == 2) ?
                statistics.getTotals().getTeams().get(0).getTeam().stream().map(t -> new TeamStatisticsDTO(t, homeAwayMap)).collect(Collectors.toList()) :
                null;

        periodStatisticDTOs = (statistics.getPeriods() != null &&
                                statistics.getPeriods().getPeriod() != null) ?
                statistics.getPeriods().getPeriod().stream().map(p -> new PeriodStatisticsDTO(p, homeAwayMap)).collect(Collectors.toList()) :
                null;
    }

    /**
     * Constructs a new statistics instance from the data obtained from an AMQP message
     *
     * @param statistics the message statistics data object
     */
    SportEventStatisticsDTO(UFStatisticsType statistics) {
        Preconditions.checkNotNull(statistics);

        totalStatisticsDTOs = new ArrayList<>();
        totalStatisticsDTOs.add(new TeamStatisticsDTO(
                HomeAway.Home,
                statistics.getYellowCards().getHome(),
                statistics.getRedCards().getHome(),
                statistics.getYellowRedCards().getHome(),
                statistics.getCorners().getHome(),
                statistics.getGreenCards().getHome()
        ));
        totalStatisticsDTOs.add(new TeamStatisticsDTO(
                HomeAway.Away,
                statistics.getYellowCards().getAway(),
                statistics.getRedCards().getAway(),
                statistics.getYellowRedCards().getAway(),
                statistics.getCorners().getAway(),
                statistics.getGreenCards().getAway()
        ));

        periodStatisticDTOs = null;
    }

    public List<TeamStatisticsDTO> getTotalStatisticsDTOs() {
        return totalStatisticsDTOs == null ? null : ImmutableList.copyOf(totalStatisticsDTOs);
    }

    public List<PeriodStatisticsDTO> getPeriodStatisticDTOs() {
        return periodStatisticDTOs == null ? null : ImmutableList.copyOf(periodStatisticDTOs);
    }
}