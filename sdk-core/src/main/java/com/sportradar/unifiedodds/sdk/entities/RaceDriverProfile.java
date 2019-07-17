/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */

package com.sportradar.unifiedodds.sdk.entities;

import com.sportradar.utils.URN;

/**
 * An interface providing methods to access race driver data
 */
public interface RaceDriverProfile {
    /**
     * Returns the race driver id
     *
     * @return the race driver id
     */
    URN getRaceDriverId();

    /**
     * Returns the race team id
     *
     * @return the race team id
     */
    URN getRaceTeamId();

    /**
     * Returns the car information
     *
     * @return the car information
     */
    Car getCar();
}
