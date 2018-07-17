/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */

package com.sportradar.unifiedodds.sdk.caching.ci;

import com.google.common.base.Preconditions;
import com.sportradar.uf.sportsapi.datamodel.SAPISeasonCoverageInfo;
import com.sportradar.utils.URN;

/**
 * A season coverage representation used by caching components
 */
public class SeasonCoverageCI {
    /**
     * The identifier of the season
     */
    private final URN seasonId;

    /**
     * The {@link String} representation of the maximum coverage available for the season associated with
     * the current instance
     */
    private final String maxCoverageLevel;

    /**
     * The {@link String} representation of the minimum coverage guaranteed for the season associated with
     * the current instance
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
     * Initializes a new instance of the {@link SeasonCoverageCI} class
     *
     * @param season - {@link SAPISeasonCoverageInfo} containing information about the season coverage
     */
    public SeasonCoverageCI(SAPISeasonCoverageInfo season) {
        Preconditions.checkNotNull(season);

        seasonId = URN.parse(season.getSeasonId());
        maxCoverageLevel = season.getMaxCoverageLevel();
        minCoverageLevel = season.getMinCoverageLevel();
        maxCovered = season.getMaxCovered();
        played = season.getPlayed();
        scheduled = season.getScheduled();
    }

    /**
     * Returns the identifier of the season
     *
     * @return - the identifier of the season
     */
    public URN getSeasonId() {
        return seasonId;
    }

    /**
     * Returns the {@link String} representation of the maximum coverage available for the season associated with
     * the current instance
     *
     * @return - the {@link String} representation of the maximum coverage available for the season associated with
     * the current instance
     */
    public String getMaxCoverageLevel() {
        return maxCoverageLevel;
    }

    /**
     * Returns the {@link String} representation of the minimum coverage guaranteed for the season associated with
     * the current instance
     *
     * @return - the {@link String} representation of the minimum coverage guaranteed for the season associated with
     * the current instance
     */
    public String getMinCoverageLevel() {
        return minCoverageLevel;
    }

    /**
     * Returns the max covered value
     *
     * @return - the max covered value if available; otherwise null
     */
    public Integer getMaxCovered() {
        return maxCovered;
    }

    /**
     * Returns the played value
     *
     * @return - the played value
     */
    public int getPlayed() {
        return played;
    }

    /**
     * Returns the scheduled value
     *
     * @return - the scheduled value
     */
    public int getScheduled() {
        return scheduled;
    }
}
