/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */

package com.sportradar.unifiedodds.sdk.entities;

import java.util.Date;
import java.util.List;

/**
 * Defines methods used to access timeline event properties
 */
@SuppressWarnings({ "MultipleStringLiterals" })
public interface TimelineEvent {
    /**
     * Returns the timeline event identifier
     *
     * @return the timeline event identifier
     */
    int getId();

    /**
     * Returns the timeline event home score
     *
     * @return the home score
     */
    Double getHomeScore();

    /**
     * Returns the timeline event away score
     *
     * @return the away score
     */
    Double getAwayScore();

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

    /**
     * Returns the period to which the timeline event belongs to
     *
     * @return the period
     */
    default String getPeriod() {
        throw new UnsupportedOperationException("Method not implemented. Use derived type.");
    }

    /**
     * Returns the points
     *
     * @return the points
     */
    default String getPoints() {
        throw new UnsupportedOperationException("Method not implemented. Use derived type.");
    }

    /**
     * Returns the stoppage time
     *
     * @return the stoppage time
     */
    default String getStoppageTime() {
        throw new UnsupportedOperationException("Method not implemented. Use derived type.");
    }

    /**
     * Returns the value
     *
     * @return the value
     */
    default String getValue() {
        throw new UnsupportedOperationException("Method not implemented. Use derived type.");
    }

    /**
     * Returns the X value
     *
     * @return the X value
     */
    default Integer getX() {
        throw new UnsupportedOperationException("Method not implemented. Use derived type.");
    }

    /**
     * Returns the Y value
     *
     * @return the Y value
     */
    default Integer getY() {
        throw new UnsupportedOperationException("Method not implemented. Use derived type.");
    }

    /**
     * Returns the match status code
     *
     * @return the match status code
     */
    default Integer getMatchStatusCode() {
        throw new UnsupportedOperationException("Method not implemented. Use derived type.");
    }

    /**
     * Returns the match clock
     *
     * @return the match clock
     */
    default String getMatchClock() {
        throw new UnsupportedOperationException("Method not implemented. Use derived type.");
    }

    /**
     * Returns the period to which the timeline event belongs to
     *
     * @return the period
     */
    default GoalScorer getGoalScorer() {
        throw new UnsupportedOperationException("Method not implemented. Use derived type.");
    }

    /**
     * Returns the period to which the timeline event belongs to
     *
     * @return the period
     */
    default EventPlayer getPlayer() {
        throw new UnsupportedOperationException("Method not implemented. Use derived type.");
    }

    /**
     * Returns the period to which the timeline event belongs to
     *
     * @return the period
     */
    default List<Assist> getAssists() {
        throw new UnsupportedOperationException("Method not implemented. Use derived type.");
    }
}
