/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */

package com.sportradar.unifiedodds.sdk.entities;

import java.util.Date;

/**
 * Defines methods used to access timeline vent properties
 */
public interface TimelineEvent {
    /**
     * Returns the timeline event identifier
     *
     * @return the timeline event identifier
     */
    int getId();

    /**
     * Returns the timeline event away score
     *
     * @return the away score
     */
    Double getAwayScore();

    /**
     * Returns the timeline event home score
     *
     * @return the home score
     */
    Double getHomeScore();

    /**
     * Returns the timeline event match time
     *
     * @return the match time
     */
    Integer getMatchTime();

    /**
     * Returns the name of the period to which the timeline event belongs to
     *
     * @return the period name
     */
    String getPeriodName();

    /**
     * Returns an indication of which team was the cause for the timeline event
     *
     * @return an indication of which team was the cause for the timeline event
     */
    HomeAway getTeam();

    /**
     * Returns the timeline event type
     *
     * @return the timeline event type
     */
    String getType();

    /**
     * Returns the timeline event time
     *
     * @return the timeline event time
     */
    Date getTime();
}
