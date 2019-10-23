/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */

package com.sportradar.unifiedodds.sdk.caching.exportable;

import java.io.Serializable;

public class ExportableCarCI implements Serializable {
    private String name;
    private String chassis;
    private String engineName;

    public ExportableCarCI(String name, String chassis, String engineName) {
        this.name = name;
        this.chassis = chassis;
        this.engineName = engineName;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getChassis() {
        return chassis;
    }

    public void setChassis(String chassis) {
        this.chassis = chassis;
    }

    public String getEngineName() {
        return engineName;
    }

    public void setEngineName(String engineName) {
        this.engineName = engineName;
    }
}
