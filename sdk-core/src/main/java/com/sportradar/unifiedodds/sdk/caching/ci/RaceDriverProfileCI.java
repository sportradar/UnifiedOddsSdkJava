/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */

package com.sportradar.unifiedodds.sdk.caching.ci;

import com.google.common.base.Preconditions;
import com.sportradar.unifiedodds.sdk.caching.exportable.ExportableRaceDriverProfileCI;
import com.sportradar.utils.URN;

/**
 * A cache representation of race driver profile
 */
@SuppressWarnings({ "AbbreviationAsWordInName" })
public class RaceDriverProfileCI {

    private final URN raceDriverId;
    private final URN raceTeamId;
    private final CarCI car;

    public RaceDriverProfileCI(URN raceDriverId, URN raceTeamId, CarCI car) {
        this.raceDriverId = raceDriverId;
        this.raceTeamId = raceTeamId;
        this.car = car;
    }

    public RaceDriverProfileCI(ExportableRaceDriverProfileCI exportable) {
        Preconditions.checkNotNull(exportable);

        this.raceDriverId =
            exportable.getRaceDriverId() != null ? URN.parse(exportable.getRaceDriverId()) : null;
        this.raceTeamId = exportable.getRaceTeamId() != null ? URN.parse(exportable.getRaceTeamId()) : null;
        this.car = exportable.getCar() != null ? new CarCI(exportable.getCar()) : null;
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

    public ExportableRaceDriverProfileCI export() {
        return new ExportableRaceDriverProfileCI(
            raceDriverId != null ? raceDriverId.toString() : null,
            raceTeamId != null ? raceTeamId.toString() : null,
            car != null ? car.export() : null
        );
    }
}
