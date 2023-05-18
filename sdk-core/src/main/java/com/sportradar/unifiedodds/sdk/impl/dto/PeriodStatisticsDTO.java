/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */

package com.sportradar.unifiedodds.sdk.impl.dto;

import com.google.common.base.Preconditions;
import com.sportradar.uf.sportsapi.datamodel.SAPIMatchPeriod;
import com.sportradar.unifiedodds.sdk.entities.HomeAway;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Period statistics data transfer object
 */
@SuppressWarnings({ "AbbreviationAsWordInName" })
public class PeriodStatisticsDTO {

    private final String periodName;
    private final List<TeamStatisticsDTO> teamStatisticDTOS;

    PeriodStatisticsDTO(SAPIMatchPeriod p, Map<HomeAway, String> homeAwayMap) {
        Preconditions.checkNotNull(p);

        periodName = p.getName();

        teamStatisticDTOS =
            (p.getTeams() != null)
                ? p
                    .getTeams()
                    .get(0)
                    .getTeam()
                    .stream()
                    .map(t -> new TeamStatisticsDTO(t, homeAwayMap))
                    .collect(Collectors.toList())
                : null;
    }

    public String getPeriodName() {
        return periodName;
    }

    public List<TeamStatisticsDTO> getTeamStatisticDTOs() {
        return teamStatisticDTOS;
    }
}
