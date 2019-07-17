/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */

package com.sportradar.unifiedodds.sdk.caching.ci;

import com.sportradar.utils.URN;

/**
 * A cache representation of race driver profile
 */
public class RaceDriverProfileCI {
    private final URN raceDriverId;
    private final URN raceTeamId;
    private final CarCI car;

    public RaceDriverProfileCI(URN raceDriverId, URN raceTeamId, CarCI car) {
        this.raceDriverId = raceDriverId;
        this.raceTeamId = raceTeamId;
        this.car = car;
    }

    public URN getRaceDriverId() {
        return raceDriverId;
    }

    public URN getRaceTeamId() {
        return raceTeamId;
    }

    public CarCI getCar() {
        return car;
    }
}
