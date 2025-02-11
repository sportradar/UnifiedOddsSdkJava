/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */

package com.sportradar.unifiedodds.sdk.internal.caching.ci;

import com.google.common.base.Preconditions;
import com.sportradar.uf.sportsapi.datamodel.SapiSeasonCoverageInfo;
import com.sportradar.unifiedodds.sdk.oddsentities.exportable.ExportableSeasonCoverageCi;
import com.sportradar.utils.Urn;

/**
 * A season coverage representation used by caching components
 */
public class SeasonCoverageCi {

    /**
     * The identifier of the season
     */
    private final Urn seasonId;

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
     * Initializes a new instance of the {@link SeasonCoverageCi} class
     *
     * @param season - {@link SapiSeasonCoverageInfo} containing information about the season coverage
     */
    public SeasonCoverageCi(SapiSeasonCoverageInfo season) {
        Preconditions.checkNotNull(season);

        seasonId = Urn.parse(season.getSeasonId());
        maxCoverageLevel = season.getMaxCoverageLevel();
        minCoverageLevel = season.getMinCoverageLevel();
        maxCovered = season.getMaxCovered();
        played = season.getPlayed();
        scheduled = season.getScheduled();
    }

    public SeasonCoverageCi(ExportableSeasonCoverageCi exportable) {
        Preconditions.checkNotNull(exportable);

        seasonId = Urn.parse(exportable.getSeasonId());
        maxCoverageLevel = exportable.getMaxCoverageLevel();
        minCoverageLevel = exportable.getMinCoverageLevel();
        maxCovered = exportable.getMaxCovered();
        played = exportable.getPlayed();
        scheduled = exportable.getScheduled();
    }

    /**
     * Returns the identifier of the season
     *
     * @return - the identifier of the season
     */
    public Urn getSeasonId() {
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

    public ExportableSeasonCoverageCi export() {
        return new ExportableSeasonCoverageCi(
            seasonId.toString(),
            maxCoverageLevel,
            minCoverageLevel,
            maxCovered,
            played,
            scheduled
        );
    }
}
