/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */

package com.sportradar.unifiedodds.sdk.entities;

/**
 * Defines methods used to access tournament coverage information
 */
public interface TournamentCoverage {
    /**
     * An indication if live coverage is available
     *
     * @return <code>true</code> if the live coverage is available; otherwise <code>false</code>
     */
    boolean isLiveCoverage();
}
