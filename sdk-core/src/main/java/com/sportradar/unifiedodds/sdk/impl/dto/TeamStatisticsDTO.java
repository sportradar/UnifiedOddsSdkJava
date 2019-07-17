/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */

package com.sportradar.unifiedodds.sdk.impl.dto;

import com.google.common.base.Preconditions;
import com.google.common.base.Strings;
import com.sportradar.uf.sportsapi.datamodel.SAPITeamStatistics;
import com.sportradar.unifiedodds.sdk.entities.HomeAway;
import com.sportradar.utils.URN;

import java.util.Map;

/**
 * Team statistics data transfer object
 */
public class TeamStatisticsDTO {
    private final String name;
    private final URN teamId;
    private final HomeAway homeAway;
    private final Integer cards;
    private final Integer yellowCards;
    private final Integer redCards;
    private final Integer yellowRedCards;
    private final Integer cornerKicks;


    TeamStatisticsDTO(SAPITeamStatistics t, Map<HomeAway, String> homeAwayMap) {
        Preconditions.checkNotNull(t);

        name = t.getName();
        teamId = t.getId() != null ? URN.parse(t.getId()) : null;
        homeAway = homeAwayMap != null ?
                homeAwayMap.entrySet().stream()
                        .filter(e -> e.getValue().equals(t.getId()))
                        .map(Map.Entry::getKey)
                        .findAny()
                        .orElse(null) :
                null;

        SAPITeamStatistics.SAPIStatistics statistics = t.getStatistics();
        yellowRedCards = tryParseInt(statistics.getYellowRedCards());
        yellowCards = tryParseInt(statistics.getYellowCards());
        redCards = tryParseInt(statistics.getRedCards());
        cards = tryParseInt(statistics.getCards());
        cornerKicks = tryParseInt(statistics.getCornerKicks());
    }

    TeamStatisticsDTO(HomeAway homeAway, int yellowCards, int redCards, int yellowRedCards, int cornerKicks) {
        Preconditions.checkNotNull(homeAway);

        this.name = "";
        this.teamId = null; // not available on the AMQP message
        this.homeAway = homeAway;
        this.yellowCards = yellowCards;
        this.redCards = redCards;
        this.yellowRedCards = yellowRedCards;
        this.cards = yellowCards + redCards + yellowRedCards;
        this.cornerKicks = cornerKicks;
    }

    public String getName() {
        return name;
    }

    public URN getTeamId() {
        return teamId;
    }

    public HomeAway getHomeAway() {
        return homeAway;
    }

    public Integer getCards() {
        return cards;
    }

    public Integer getYellowCards() {
        return yellowCards;
    }

    public Integer getRedCards() {
        return redCards;
    }

    public Integer getYellowRedCards() {
        return yellowRedCards;
    }

    public Integer getCornerKicks() {
        return cornerKicks;
    }

    private static Integer tryParseInt(String val) {
        if (Strings.isNullOrEmpty(val)) {
            return null;
        }

        try {
            return Integer.valueOf(val);
        } catch (NumberFormatException e) {
            return null;
        }
    }
}
