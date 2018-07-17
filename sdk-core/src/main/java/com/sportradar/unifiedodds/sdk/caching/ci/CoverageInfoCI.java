/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */

package com.sportradar.unifiedodds.sdk.caching.ci;

import com.google.common.base.Preconditions;
import com.google.common.base.Strings;
import com.google.common.collect.ImmutableList;
import com.sportradar.uf.sportsapi.datamodel.SAPICoverage;
import com.sportradar.uf.sportsapi.datamodel.SAPICoverageInfo;

import java.util.List;
import java.util.stream.Collectors;

/**
 * A coverage info representation used by caching components
 */
public class CoverageInfoCI {
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
     * Initializes a new instance of the {@link SAPICoverageInfo} class.
     *
     * @param coverageInfo - {@link SAPICoverageInfo} containing information about the competitor
     */
    public CoverageInfoCI(SAPICoverageInfo coverageInfo) {
        Preconditions.checkNotNull(coverageInfo);
        Preconditions.checkArgument(!Strings.isNullOrEmpty(coverageInfo.getLevel()));

        merge(coverageInfo);
    }

    /**
     * Merges the information from the provided {@link SAPICoverageInfo} into the current instance
     *
     * @param coverageInfo - {@link SAPICoverageInfo} containing information about the competitor
     */
    public void merge(SAPICoverageInfo coverageInfo) {
        Preconditions.checkNotNull(coverageInfo);
        Preconditions.checkArgument(!Strings.isNullOrEmpty(coverageInfo.getLevel()));

        level = coverageInfo.getLevel();
        isLive = coverageInfo.isLiveCoverage();

        if (coverageInfo.getCoverage() != null) {
            includes = coverageInfo.getCoverage().stream().
                            map(SAPICoverage::getIncludes).collect(Collectors.toList());
        }
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
}
