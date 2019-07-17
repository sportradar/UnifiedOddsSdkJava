/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */

package com.sportradar.unifiedodds.sdk.entities;

/**
 * An interface providing methods to access car data
 */
public interface Car {
    /**
     * Returns the name of a car
     *
     * @return the name of a car
     */
    String getName();

    /**
     * Returns the chassis of a car
     *
     * @return the chassis of a car
     */
    String getChassis();

    /**
     * Returns the engine name of a car
     *
     * @return the engine name of a car
     */
    String getEngineName();
}
