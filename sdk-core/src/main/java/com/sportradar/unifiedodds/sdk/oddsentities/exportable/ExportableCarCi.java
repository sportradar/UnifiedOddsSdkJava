/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */

package com.sportradar.unifiedodds.sdk.oddsentities.exportable;

import java.io.Serializable;

@SuppressWarnings({ "HiddenField" })
public class ExportableCarCi implements Serializable {

    private String name;
    private String chassis;
    private String engineName;

    public ExportableCarCi(String name, String chassis, String engineName) {
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
