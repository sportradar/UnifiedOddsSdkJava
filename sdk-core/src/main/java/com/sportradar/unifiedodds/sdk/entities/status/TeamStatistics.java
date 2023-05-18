/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */

package com.sportradar.unifiedodds.sdk.entities.status;

import com.sportradar.unifiedodds.sdk.entities.HomeAway;

/**
 * Defines methods used to access team statistics
 */
@SuppressWarnings({ "LineLength" })
public interface TeamStatistics {
    /**
     * Returns an indication if the statistics are for the home or away team
     *
     * @return {@link HomeAway#Home} if the statistics are for the home team; {@link HomeAway#Away} if the statistics are for the away team
     */
    HomeAway getHomeAway();

    /**
     * Returns the total count of received cards
     *
     * @return the total count of received cards, could be null
     */
    Integer getCards();

    /**
     * Returns the received yellow cards number
     *
     * @return the received yellow cards number, could be null
     */
    Integer getYellowCards();

    /**
     * Returns the received red cards number
     *
     * @return the received red cards number, could be null
     */
    Integer getRedCards();

    /**
     * Returns the received yellow-red cards number
     *
     * @return the received yellow-red cards number, could be null
     */
    Integer getYellowRedCards();

    /**
     * Returns the total amount of played corner kicks
     *
     * @return the total amount of played corner kicks
     */
    Integer getCornerKicks();

    /**
     * Returns the received green cards number
     *
     * @return the received green cards number, could be null
     */
    default Integer getGreenCards() {
        return null;
    }

    /**
     * Returns the name
     *
     * @return the name
     */
    default String getName() {
        return null;
    }
}
