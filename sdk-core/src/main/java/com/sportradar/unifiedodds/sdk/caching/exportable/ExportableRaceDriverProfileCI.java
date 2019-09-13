/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */

package com.sportradar.unifiedodds.sdk.caching.exportable;

public class ExportableRaceDriverProfileCI extends ExportableCI {
    private String raceDriverId;
    private String raceTeamId;
    private ExportableCarCI car;

    public ExportableRaceDriverProfileCI(String raceDriverId, String raceTeamId, ExportableCarCI car) {
        super(null, null);
        this.raceDriverId = raceDriverId;
        this.raceTeamId = raceTeamId;
        this.car = car;
    }

    public String getRaceDriverId() {
        return raceDriverId;
    }

    public void setRaceDriverId(String raceDriverId) {
        this.raceDriverId = raceDriverId;
    }

    public String getRaceTeamId() {
        return raceTeamId;
    }

    public void setRaceTeamId(String raceTeamId) {
        this.raceTeamId = raceTeamId;
    }

    public ExportableCarCI getCar() {
        return car;
    }

    public void setCar(ExportableCarCI car) {
        this.car = car;
    }
}
