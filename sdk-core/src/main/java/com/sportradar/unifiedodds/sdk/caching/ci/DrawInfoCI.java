/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */

package com.sportradar.unifiedodds.sdk.caching.ci;

import com.google.common.base.Preconditions;
import com.sportradar.uf.sportsapi.datamodel.SAPIDrawType;
import com.sportradar.uf.sportsapi.datamodel.SAPILottery;
import com.sportradar.uf.sportsapi.datamodel.SAPITimeType;
import com.sportradar.unifiedodds.sdk.entities.DrawType;
import com.sportradar.unifiedodds.sdk.entities.TimeType;

/**
 * A basic draw info cache representation
 */
public class DrawInfoCI {

    private final DrawType drawType;
    private final TimeType timeType;
    private final String gameType;

    public DrawInfoCI(SAPILottery.SAPIDrawInfo drawInfo) {
        Preconditions.checkNotNull(drawInfo);

        drawType = map(drawInfo.getDrawType());
        timeType = map(drawInfo.getTimeType());
        gameType = drawInfo.getGameType();
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

    private static DrawType map(SAPIDrawType type) {
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

    private static TimeType map(SAPITimeType timeType) {
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
}
