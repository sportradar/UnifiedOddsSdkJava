/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */

package com.sportradar.unifiedodds.sdk.entities;

import com.sportradar.utils.Urn;

/**
 * Defines methods representing season coverage info
 */
@SuppressWarnings({ "LineLength" })
public interface SeasonCoverage {
    /**
     * Returns the unique identifier of the season
     *
     * @return - the unique identifier of the season
     */
    Urn getSeasonId();

    /**
     * Returns the string representation of the maximum coverage available for the season associated with the current instance
     *
     * @return - the string representation of the maximum coverage available for the season associated with the current instance
     */
    String getMaxCoverageLevel();

    /**
     * Returns the name of the minimum coverage guaranteed for the season associated with the current instance
     *
     * @return - the name of the minimum coverage guaranteed for the season associated with the current instance
     */
    String getMinCoverageLevel();

    /**
     * Returns the max covered value
     *
     * @return - the max covered value
     */
    Integer getMaxCovered();

    /**
     * Returns the played value
     *
     * @return - the played value
     */
    int getPlayed();

    /**
     * Returns the scheduled value
     *
     * @return - the scheduled value
     */
    int getScheduled();
}
