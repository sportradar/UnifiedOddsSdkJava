/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */

package com.sportradar.unifiedodds.sdk.caching.ci;

import com.google.common.base.Preconditions;
import com.sportradar.unifiedodds.sdk.caching.exportable.ExportableCI;
import com.sportradar.unifiedodds.sdk.caching.exportable.ExportableCacheItem;
import com.sportradar.unifiedodds.sdk.caching.exportable.ExportableCarCI;

/**
 * A cache representation of race car
 */
public class CarCI implements ExportableCacheItem {
    private final String name;
    private final String chassis;
    private final String engineName;

    public CarCI(String name, String chassis, String engineName) {
        this.name = name;
        this.chassis = chassis;
        this.engineName = engineName;
    }

    public CarCI(ExportableCarCI exportable) {
        Preconditions.checkNotNull(exportable);
        this.name = exportable.getName();
        this.chassis = exportable.getChassis();
        this.engineName = exportable.getEngineName();
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

    @Override
    public ExportableCI export() {
        return new ExportableCarCI(
                name,
                chassis,
                engineName
        );
    }
}
