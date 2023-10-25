/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */

package com.sportradar.unifiedodds.sdk.caching.ci;

import com.google.common.base.Preconditions;
import com.google.common.base.Strings;
import com.google.common.collect.ImmutableList;
import com.sportradar.uf.sportsapi.datamodel.SapiCoverage;
import com.sportradar.uf.sportsapi.datamodel.SapiCoverageInfo;
import com.sportradar.unifiedodds.sdk.caching.exportable.ExportableCoverageInfoCi;
import com.sportradar.unifiedodds.sdk.entities.CoveredFrom;
import java.util.List;
import java.util.stream.Collectors;

/**
 * A coverage info representation used by caching components
 */
public class CoverageInfoCi {

    /**
     * The level property backing field
     */
    private String level;

    /**
     * The isLive property backing field
     */
    private boolean isLive;

    /**
     * The includes property backing field
     */
    private List<String> includes;

    /**
     * The coverage location field
     */
    private CoveredFrom coveredFrom;

    /**
     * Initializes a new instance of the {@link SapiCoverageInfo} class.
     *
     * @param coverageInfo - {@link SapiCoverageInfo} containing information about the competitor
     */
    public CoverageInfoCi(SapiCoverageInfo coverageInfo) {
        Preconditions.checkNotNull(coverageInfo);
        Preconditions.checkArgument(!Strings.isNullOrEmpty(coverageInfo.getLevel()));

        merge(coverageInfo);
    }

    public CoverageInfoCi(ExportableCoverageInfoCi coverageInfo) {
        Preconditions.checkNotNull(coverageInfo);

        level = coverageInfo.getLevel();
        isLive = coverageInfo.isLive();
        includes = coverageInfo.getIncludes();
        coveredFrom = coverageInfo.getCoveredFrom();
    }

    /**
     * Merges the information from the provided {@link SapiCoverageInfo} into the current instance
     *
     * @param coverageInfo - {@link SapiCoverageInfo} containing information about the competitor
     */
    public void merge(SapiCoverageInfo coverageInfo) {
        Preconditions.checkNotNull(coverageInfo);
        Preconditions.checkArgument(!Strings.isNullOrEmpty(coverageInfo.getLevel()));

        level = coverageInfo.getLevel();
        isLive = coverageInfo.isLiveCoverage();
        if (coverageInfo.getCoverage() != null) {
            includes =
                coverageInfo
                    .getCoverage()
                    .stream()
                    .map(SapiCoverage::getIncludes)
                    .collect(Collectors.toList());
        }
        coveredFrom = mapCoveredFrom(coverageInfo.getCoveredFrom());
    }

    /**
     * Returns the level of the coverage scope
     *
     * @return - the level of the coverage scope
     */
    public String getLevel() {
        return level;
    }

    /**
     * Status of the coverage
     *
     * @return - the status of the coverage
     */
    public boolean isLive() {
        return isLive;
    }

    /**
     * Returns the includes of the coverage who's content can't be changed
     *
     * @return - the includes of the coverage who's content can't be changed
     */
    public List<String> getIncludes() {
        return includes == null ? null : ImmutableList.copyOf(includes);
    }

    /**
     * Returns coverage location
     *
     * @return - coverage location
     */
    public CoveredFrom getCoveredFrom() {
        return coveredFrom;
    }

    private static CoveredFrom mapCoveredFrom(String value) {
        if (value == null || value.isEmpty()) {
            return null;
        }
        switch (value) {
            case "tv":
                return CoveredFrom.Tv;
            case "venue":
                return CoveredFrom.Venue;
            default:
                return null;
        }
    }

    public ExportableCoverageInfoCi export() {
        return new ExportableCoverageInfoCi(level, isLive, includes, coveredFrom);
    }
}
