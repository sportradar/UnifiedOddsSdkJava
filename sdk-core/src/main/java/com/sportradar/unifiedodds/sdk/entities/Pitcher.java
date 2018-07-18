/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */

package com.sportradar.unifiedodds.sdk.entities;

import com.sportradar.utils.URN;

/**
 * Defines methods implemented by classes representing the sport event pitcher
 */
public interface Pitcher {
    /**
     * Returns the unique identifier of the current {@link Pitcher} instance
     *
     * @return - the unique identifier of the current {@link Pitcher} instance
     */
    URN getId();

    /**
     * Returns the name of the pitcher represented by the current {@link Pitcher} instance
     *
     * @return - the name of the pitcher represented by the current {@link Pitcher} instance
     */
    String getName();

    /**
     * Returns indication if the {@link Pitcher} is home or away
     *
     * @return - indication if the {@link Pitcher} is home or away
     */
    HomeAway getCompetitor();

    /**
     * Returns indication if the {@link Pitcher} is left or right handed
     *
     * @return - indication if the {@link Pitcher} is left or right handed
     */
    PitcherHand getHand();
}
