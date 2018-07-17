/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */

package com.sportradar.unifiedodds.sdk.impl.entities;

import com.google.common.base.Preconditions;
import com.sportradar.unifiedodds.sdk.caching.ci.SeasonCoverageCI;
import com.sportradar.unifiedodds.sdk.entities.SeasonCoverage;
import com.sportradar.utils.URN;

/**
 * Provides information about season coverage
 */
public class SeasonCoverageImpl implements SeasonCoverage {
    /**
     * The unique identifier of the season
     */
    private final URN seasonId;

    /**
     * The string representation of the maximum coverage available for the season associated with the current instance
     */
    private final String maxCoverageLevel;

    /**
     * The name of the minimum coverage guaranteed for the season associated with the current instance
     */
    private final String minCoverageLevel;

    /**
     * The max covered value
     */
    private final Integer maxCovered;

    /**
     * The played value
     */
    private final int played;

    /**
     * The scheduled value
     */
    private final int scheduled;


    /**
     * Initializes a new instance of {@link SeasonCoverageImpl}
     *
     * @param seasonCoverageCI - a {@link SeasonCoverageCI} used to make the instance
     */
    public SeasonCoverageImpl(SeasonCoverageCI seasonCoverageCI) {
        Preconditions.checkNotNull(seasonCoverageCI);

        this.seasonId = seasonCoverageCI.getSeasonId();
        this.maxCoverageLevel = seasonCoverageCI.getMaxCoverageLevel();
        this.minCoverageLevel = seasonCoverageCI.getMinCoverageLevel();
        this.maxCovered = seasonCoverageCI.getMaxCovered();
        this.played = seasonCoverageCI.getPlayed();
        this.scheduled = seasonCoverageCI.getScheduled();
    }


    /**
     * Returns the unique identifier of the season
     *
     * @return - the unique identifier of the season
     */
    @Override
    public URN getSeasonId() {
        return seasonId;
    }

    /**
     * Returns the string representation of the maximum coverage available for the season associated with the current instance
     *
     * @return - the string representation of the maximum coverage available for the season associated with the current instance
     */
    @Override
    public String getMaxCoverageLevel() {
        return maxCoverageLevel;
    }

    /**
     * Returns the name of the minimum coverage guaranteed for the season associated with the current instance
     *
     * @return - the name of the minimum coverage guaranteed for the season associated with the current instance
     */
    @Override
    public String getMinCoverageLevel() {
        return minCoverageLevel;
    }

    /**
     * Returns the max covered value
     *
     * @return - the max covered value
     */
    @Override
    public Integer getMaxCovered() {
        return maxCovered;
    }

    /**
     * Returns the played value
     *
     * @return - the played value
     */
    @Override
    public int getPlayed() {
        return played;
    }

    /**
     * Returns the scheduled value
     *
     * @return - the scheduled value
     */
    @Override
    public int getScheduled() {
        return scheduled;
    }

    /**
     * Returns a {@link String} describing the current {@link SeasonCoverage} instance
     *
     * @return - a {@link String} describing the current {@link SeasonCoverage} instance
     */
    @Override
    public String toString() {
        return "SeasonCoverageImpl{" +
                "seasonId=" + seasonId +
                ", maxCoverageLevel='" + maxCoverageLevel + '\'' +
                ", minCoverageLevel='" + minCoverageLevel + '\'' +
                ", maxCovered=" + maxCovered +
                ", played=" + played +
                ", scheduled=" + scheduled +
                '}';
    }
}
