/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */

package com.sportradar.unifiedodds.sdk.entities;

import java.util.List;

/**
 * An interface providing methods to access coverage information
 */
public interface CoverageInfo {
    /**
     * Returns the level of the available coverage
     *
     * @return - the level of the available coverage
     */
    String getLevel();

    /**
     * Returns a value indicating whether the coverage represented by current instance is live coverage
     *
     * @return - a value indicating whether the coverage represented by current instance is live coverage
     */
    boolean isLive();

    /**
     * Returns an unmodifiable {@link List} specifying what is included in the coverage
     *
     * @return - if available, an unmodifiable {@link List} specifying what is included in the coverage;
     *           otherwise null
     */
    List<String> getIncludes();

    /**
     * Returns coverage location
     *
     * @return - coverage location
     */
    default CoveredFrom getCoveredFrom() {
        return null;
    }
}
