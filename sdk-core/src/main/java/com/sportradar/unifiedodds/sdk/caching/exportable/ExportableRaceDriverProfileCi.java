/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */

package com.sportradar.unifiedodds.sdk.caching.exportable;

@SuppressWarnings({ "HiddenField" })
public class ExportableRaceDriverProfileCi extends ExportableCi {

    private String raceDriverId;
    private String raceTeamId;
    private ExportableCarCi car;

    public ExportableRaceDriverProfileCi(String raceDriverId, String raceTeamId, ExportableCarCi car) {
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

    public ExportableCarCi getCar() {
        return car;
    }

    public void setCar(ExportableCarCi car) {
        this.car = car;
    }
}
