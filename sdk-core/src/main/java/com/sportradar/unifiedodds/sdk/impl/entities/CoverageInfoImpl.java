/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */

package com.sportradar.unifiedodds.sdk.impl.entities;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableList;
import com.sportradar.unifiedodds.sdk.caching.ci.CoverageInfoCi;
import com.sportradar.unifiedodds.sdk.caching.exportable.ExportableCoverageInfoCi;
import com.sportradar.unifiedodds.sdk.entities.CoverageInfo;
import com.sportradar.unifiedodds.sdk.entities.CoveredFrom;
import java.util.ArrayList;
import java.util.List;

/**
 * Provides coverage information
 */
@SuppressWarnings({ "LineLength", "NeedBraces", "UnnecessaryParentheses" })
public class CoverageInfoImpl implements CoverageInfo {

    /**
     * The level of the available coverage
     */
    private final String level;

    /**
     * A boolean value indicating whether the coverage represented by current instance is live coverage
     */
    private final boolean isLive;

    /**
     * An immutable {@link List} specifying what is included in the coverage represented by the
     * current {@link CoverageInfo} instance
     */
    private final List<String> includes;

    /**
     * The coverage location field
     */
    private final CoveredFrom coveredFrom;

    /**
     * Initializes a new instance of {@link CoverageInfoImpl}
     *
     * @param level - a {@link String}
     * @param isLive - a value indicating whether the coverage represented by current instance is live coverage
     * @param includes - a {@link List} specifying what is included in the coverage represented by the current {@link CoverageInfo} instance
     * @param coveredFrom - a {@link String} describing coverage location
     */
    CoverageInfoImpl(String level, boolean isLive, List<String> includes, String coveredFrom) {
        this.level = level;
        this.isLive = isLive;
        this.includes = includes == null ? null : ImmutableList.copyOf(includes);
        this.coveredFrom = mapCoveredFrom(coveredFrom);
    }

    CoverageInfoImpl(CoverageInfoCi ci) {
        Preconditions.checkNotNull(ci);
        this.level = ci.getLevel();
        this.isLive = ci.isLive();
        this.includes = ci.getIncludes() != null ? ImmutableList.copyOf(ci.getIncludes()) : null;
        this.coveredFrom = ci.getCoveredFrom();
    }

    CoverageInfoImpl(ExportableCoverageInfoCi exportable) {
        Preconditions.checkNotNull(exportable);
        this.level = exportable.getLevel();
        this.isLive = exportable.isLive();
        this.includes =
            exportable.getIncludes() != null ? ImmutableList.copyOf(exportable.getIncludes()) : null;
        this.coveredFrom = exportable.getCoveredFrom();
    }

    /**
     * Returns the level of the available coverage
     *
     * @return - the level of the available coverage
     */
    @Override
    public String getLevel() {
        return level;
    }

    /**
     * Returns a value indicating whether the coverage represented by current instance is live coverage
     *
     * @return - a value indicating whether the coverage represented by current instance is live coverage
     */
    @Override
    public boolean isLive() {
        return isLive;
    }

    /**
     * Returns an unmodifiable {@link List} specifying what is included in the coverage
     *
     * @return - if available, an unmodifiable {@link List} specifying what is included in the coverage;
     *           otherwise null
     */
    @Override
    public List<String> getIncludes() {
        return includes;
    }

    /**
     * Returns coverage location
     *
     * @return - coverage location
     */
    @Override
    public CoveredFrom getCoveredFrom() {
        return coveredFrom;
    }

    /**
     * Returns a {@link String} describing the current {@link CoverageInfo} instance
     *
     * @return - a {@link String} describing the current {@link CoverageInfo} instance
     */
    @Override
    public String toString() {
        return (
            "CoverageInfoImpl{" +
            "level='" +
            level +
            '\'' +
            ", isLive=" +
            isLive +
            ", includes=" +
            includes +
            ", coveredFrom=" +
            coveredFrom +
            '}'
        );
    }

    private static CoveredFrom mapCoveredFrom(String value) {
        if (value == null || value.trim().isEmpty()) return null;
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
        return new ExportableCoverageInfoCi(
            level,
            isLive,
            includes != null ? new ArrayList<>(includes) : null,
            coveredFrom
        );
    }
}
