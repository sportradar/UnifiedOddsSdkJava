/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */

package com.sportradar.unifiedodds.sdk.impl.entities.status;

import com.google.common.base.Preconditions;
import com.sportradar.unifiedodds.sdk.entities.HomeAway;
import com.sportradar.unifiedodds.sdk.entities.status.TeamStatistics;
import com.sportradar.unifiedodds.sdk.impl.dto.TeamStatisticsDTO;

/**
 * Provides methods used to access team statistics
 */
class TeamStatisticsImpl implements TeamStatistics {
    private final TeamStatisticsDTO stats;


    TeamStatisticsImpl(TeamStatisticsDTO stats) {
        Preconditions.checkNotNull(stats);

        this.stats = stats;
    }

    /**
     * Returns an indication if the statistics are for the home or away team
     *
     * @return {@link HomeAway#Home} if the statistics are for the home team; {@link HomeAway#Away} if the statistics are for the away team
     */
    @Override
    public HomeAway getHomeAway() {
        return stats.getHomeAway();
    }

    /**
     * Returns the total count of received cards
     *
     * @return the total count of received cards, could be null
     */
    @Override
    public Integer getCards() {
        return stats.getCards();
    }

    /**
     * Returns the received yellow cards number
     *
     * @return the received yellow cards number, could be null
     */
    @Override
    public Integer getYellowCards() {
        return stats.getYellowCards();
    }

    /**
     * Returns the received red cards number
     *
     * @return the received red cards number, could be null
     */
    @Override
    public Integer getRedCards() {
        return stats.getRedCards();
    }

    /**
     * Returns the received yellow-red cards number
     *
     * @return the received yellow-red cards number, could be null
     */
    @Override
    public Integer getYellowRedCards() {
        return stats.getYellowRedCards();
    }

    /**
     * Returns the total amount of played corner kicks
     *
     * @return the total amount of played corner kicks
     */
    @Override
    public Integer getCornerKicks() {
        return stats.getCornerKicks();
    }

    /**
     * Returns the received green cards number
     *
     * @return the received green cards number, could be null
     */
    @Override
    public Integer getGreenCards(){ return stats.getGreenCards(); }

    /**
     * Returns the name
     *
     * @return the name
     */
    @Override
    public String getName() {
        return stats.getName();
    }
}
