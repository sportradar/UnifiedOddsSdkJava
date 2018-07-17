/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */

package com.sportradar.unifiedodds.sdk.entities;

/**
 * Defines methods ued to access bonus info data
 */
public interface BonusInfo {
    /**
     * Returns the number of bonus balls
     *
     * @return the number of bonus balls
     */
    Integer getBonusBalls();

    /**
     * Returns a description of the bonus drum
     *
     * @return a description of the bonus drum
     */
    BonusDrumType getBonusDrumType();

    /**
     * Returns the bonus range
     *
     * @return the bonus range
     */
    String getBonusRange();
}
