/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */

package com.sportradar.unifiedodds.sdk.impl.entities;

import com.google.common.base.Preconditions;
import com.sportradar.unifiedodds.sdk.caching.ci.CarCi;
import com.sportradar.unifiedodds.sdk.entities.Car;

/**
 * A basic implementation of the {@link Car}
 */
public class CarImpl implements Car {

    private final String name;
    private final String chassis;
    private final String engineName;

    public CarImpl(CarCi car) {
        Preconditions.checkNotNull(car);

        this.name = car.getName();
        this.chassis = car.getChassis();
        this.engineName = car.getEngineName();
    }

    /**
     * Returns the name of a car
     *
     * @return the name of a car
     */
    @Override
    public String getName() {
        return name;
    }

    /**
     * Returns the chassis of a car
     *
     * @return the chassis of a car
     */
    @Override
    public String getChassis() {
        return chassis;
    }

    /**
     * Returns the engine name of a car
     *
     * @return the engine name of a car
     */
    @Override
    public String getEngineName() {
        return engineName;
    }
}
