/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */

package com.sportradar.unifiedodds.sdk.entities;

/**
 * Defines methods implemented by classes representing a competing team
 */
public interface TeamCompetitor extends Competitor {
    /**
     * Returns the qualifier additionally describing the competitor (e.g. home, away, ...)
     *
     * @return - the qualifier additionally describing the competitor (e.g. home, away, ...)
     */
    String getQualifier();

    /**
     * Returns division of the competitor
     *
     * @return the division of the competitor if available; otherwise null
     */
    default Integer getDivision() {
        throw new UnsupportedOperationException("Method not implemented. Use derived type.");
    }
}
