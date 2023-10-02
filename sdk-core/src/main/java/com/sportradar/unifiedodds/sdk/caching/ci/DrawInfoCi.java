/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */

package com.sportradar.unifiedodds.sdk.caching.ci;

import com.google.common.base.Preconditions;
import com.sportradar.uf.sportsapi.datamodel.SapiDrawType;
import com.sportradar.uf.sportsapi.datamodel.SapiLottery;
import com.sportradar.uf.sportsapi.datamodel.SapiTimeType;
import com.sportradar.unifiedodds.sdk.caching.exportable.ExportableDrawInfoCi;
import com.sportradar.unifiedodds.sdk.entities.DrawType;
import com.sportradar.unifiedodds.sdk.entities.TimeType;

/**
 * A basic draw info cache representation
 */
public class DrawInfoCi {

    private final DrawType drawType;
    private final TimeType timeType;
    private final String gameType;

    public DrawInfoCi(SapiLottery.SapiDrawInfo drawInfo) {
        Preconditions.checkNotNull(drawInfo);

        drawType = map(drawInfo.getDrawType());
        timeType = map(drawInfo.getTimeType());
        gameType = drawInfo.getGameType();
    }

    public DrawInfoCi(ExportableDrawInfoCi exportable) {
        Preconditions.checkNotNull(exportable);

        drawType = exportable.getDrawType();
        timeType = exportable.getTimeType();
        gameType = exportable.getGameType();
    }

    public DrawType getDrawType() {
        return drawType;
    }

    public TimeType getTimeType() {
        return timeType;
    }

    public String getGameType() {
        return gameType;
    }

    private static DrawType map(SapiDrawType type) {
        if (type == null) {
            return DrawType.Unknown;
        }

        switch (type) {
            case DRUM:
                return DrawType.Drum;
            case RNG:
                return DrawType.Rng;
            default:
                return DrawType.Unknown;
        }
    }

    private static TimeType map(SapiTimeType timeType) {
        if (timeType == null) {
            return TimeType.Unknown;
        }

        switch (timeType) {
            case FIXED:
                return TimeType.Fixed;
            case INTERVAL:
                return TimeType.Interval;
            default:
                return TimeType.Unknown;
        }
    }

    public ExportableDrawInfoCi export() {
        return new ExportableDrawInfoCi(drawType, timeType, gameType);
    }
}
