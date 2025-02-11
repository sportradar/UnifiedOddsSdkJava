/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */

package com.sportradar.unifiedodds.sdk.internal.caching.ci;

import com.google.common.base.Preconditions;
import com.sportradar.unifiedodds.sdk.oddsentities.exportable.ExportableRaceDriverProfileCi;
import com.sportradar.utils.Urn;

/**
 * A cache representation of race driver profile
 */
public class RaceDriverProfileCi {

    private final Urn raceDriverId;
    private final Urn raceTeamId;
    private final CarCi car;

    public RaceDriverProfileCi(Urn raceDriverId, Urn raceTeamId, CarCi car) {
        this.raceDriverId = raceDriverId;
        this.raceTeamId = raceTeamId;
        this.car = car;
    }

    public RaceDriverProfileCi(ExportableRaceDriverProfileCi exportable) {
        Preconditions.checkNotNull(exportable);

        this.raceDriverId =
            exportable.getRaceDriverId() != null ? Urn.parse(exportable.getRaceDriverId()) : null;
        this.raceTeamId = exportable.getRaceTeamId() != null ? Urn.parse(exportable.getRaceTeamId()) : null;
        this.car = exportable.getCar() != null ? new CarCi(exportable.getCar()) : null;
    }

    public Urn getRaceDriverId() {
        return raceDriverId;
    }

    public Urn getRaceTeamId() {
        return raceTeamId;
    }

    public CarCi getCar() {
        return car;
    }

    public ExportableRaceDriverProfileCi export() {
        return new ExportableRaceDriverProfileCi(
            raceDriverId != null ? raceDriverId.toString() : null,
            raceTeamId != null ? raceTeamId.toString() : null,
            car != null ? car.export() : null
        );
    }
}
