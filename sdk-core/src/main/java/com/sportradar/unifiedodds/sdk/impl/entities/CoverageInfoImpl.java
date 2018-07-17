/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */

package com.sportradar.unifiedodds.sdk.impl.entities;

import com.google.common.collect.ImmutableList;
import com.sportradar.unifiedodds.sdk.entities.CoverageInfo;

import java.util.List;

/**
 * Provides coverage information
 */
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
     * Initializes a new instance of {@link CoverageInfoImpl}
     *
     * @param level - a {@link String}
     * @param isLive - a value indicating whether the coverage represented by current instance is live coverage
     * @param includes - a {@link List} specifying what is included in the coverage represented by the current {@link CoverageInfo} instance
     */
    CoverageInfoImpl(String level, boolean isLive, List<String> includes) {
        this.level = level;
        this.isLive = isLive;
        this.includes = includes == null ?  null : ImmutableList.copyOf(includes);
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
     * Returns a {@link String} describing the current {@link CoverageInfo} instance
     *
     * @return - a {@link String} describing the current {@link CoverageInfo} instance
     */
    @Override
    public String toString() {
        return "CoverageInfoImpl{" +
                "level='" + level + '\'' +
                ", isLive=" + isLive +
                ", includes=" + includes +
                '}';
    }
}
