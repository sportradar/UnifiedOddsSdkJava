/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */

package com.sportradar.unifiedodds.sdk.entities;

import java.math.BigDecimal;
import java.math.BigInteger;

/**
 * Defines methods used by classes that provide event result information
 */
public interface EventResult {
    /**
     * Returns the id of the event result
     *
     * @return the id of the event result
     */
    String getId();

    /**
     * Returns the position of the result
     *
     * @return the position of the result
     */
    BigInteger getPosition();

    /**
     * Returns the points of the result
     *
     * @return the points of the result
     */
    BigInteger getPoints();

    /**
     * Returns the time of the result
     *
     * @return the time of the result
     */
    String getTime();

    /**
     * Returns the time ranking
     *
     * @return thr time ranking
     */
    BigInteger getTimeRanking();

    /**
     * Returns the state of the result
     *
     * @return the state of the result
     */
    String getStatus();

    /**
     * Returns the status comment
     *
     * @return the status comment
     */
    String getStatusComment();

    /**
     * Returns the sprint of the result
     *
     * @return the sprint of the result
     */
    BigInteger getSprint();

    /**
     * Returns the sprint ranking
     *
     * @return the sprint ranking
     */
    BigInteger getSprintRanking();

    /**
     * Returns the climber
     *
     * @return the climber
     */
    BigInteger getClimber();

    /**
     * Returns the climber ranking
     *
     * @return the climber ranking
     */
    BigInteger getClimberRanking();

    /**
     * Returns the match status
     *
     * @return the match status
     */
    Integer getMatchStatus();

    /**
     * Returns the home score
     *
     * @return the home score
     */
    BigDecimal getHomeScore();

    /**
     * Returns the away score
     *
     * @return the away score
     */
    BigDecimal getAwayScore();

    /**
     * Returns the points in a decimal format
     *
     * @return the points in a decimal format
     */
    Double getPointsDecimal();

    /**
     * Returns the sprint value in a decimal format
     *
     * @return the sprint value in a decimal format
     */
    Double getSprintDecimal();

    /**
     * Returns the climber value in a decimal format
     *
     * @return the climber value in a decimal format
     */
    Double getClimberDecimal();

    /**
     * Returns the wc points
     *
     * @return the wc points
     */
    Double getWcPoints();

    /**
     * Returns the grid value
     *
     * @return the grid value
     */
    default Integer getGrid()
    {
        throw new UnsupportedOperationException("Method not implemented. Use derived type.");
    }
}
