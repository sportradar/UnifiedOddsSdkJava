/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */

package com.sportradar.unifiedodds.sdk.impl.entities;

import com.google.common.base.Preconditions;
import com.sportradar.unifiedodds.sdk.caching.ci.RaceDriverProfileCI;
import com.sportradar.unifiedodds.sdk.entities.Car;
import com.sportradar.unifiedodds.sdk.entities.RaceDriverProfile;
import com.sportradar.utils.URN;

/**
 * A basic implementation of the {@link RaceDriverProfile}
 */
public class RaceDriverProfileImpl implements RaceDriverProfile {

    private final URN raceDriverId;
    private final URN raceTeamId;
    private final Car car;

    public RaceDriverProfileImpl(RaceDriverProfileCI raceDriver) {
        Preconditions.checkNotNull(raceDriver);

        this.raceDriverId = raceDriver.getRaceDriverId();
        this.raceTeamId = raceDriver.getRaceTeamId();
        this.car = raceDriver.getCar() != null ? new CarImpl(raceDriver.getCar()) : null;
    }

    /**
     * Returns the race driver id
     *
     * @return the race driver id
     */
    @Override
    public URN getRaceDriverId() {
        return raceDriverId;
    }

    /**
     * Returns the race team id
     *
     * @return the race team id
     */
    @Override
    public URN getRaceTeamId() {
        return raceTeamId;
    }

    /**
     * Returns the car information
     *
     * @return the car information
     */
    @Override
    public Car getCar() {
        return car;
    }
}
