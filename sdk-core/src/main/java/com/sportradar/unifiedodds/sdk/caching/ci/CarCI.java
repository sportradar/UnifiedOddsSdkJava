/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */

package com.sportradar.unifiedodds.sdk.caching.ci;

/**
 * A cache representation of race car
 */
public class CarCI {
    private final String name;
    private final String chassis;
    private final String engineName;

    public CarCI(String name, String chassis, String engineName) {
        this.name = name;
        this.chassis = chassis;
        this.engineName = engineName;
    }

    public String getName() {
        return name;
    }

    public String getChassis() {
        return chassis;
    }

    public String getEngineName() {
        return engineName;
    }
}
