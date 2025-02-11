/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */

package com.sportradar.unifiedodds.sdk.internal.impl.entities;

import com.google.common.base.Preconditions;
import com.sportradar.unifiedodds.sdk.entities.DrawInfo;
import com.sportradar.unifiedodds.sdk.entities.DrawType;
import com.sportradar.unifiedodds.sdk.entities.TimeType;
import com.sportradar.unifiedodds.sdk.internal.caching.ci.DrawInfoCi;

/**
 * A basic implementation of the {@link DrawInfo}
 */
public class DrawInfoImpl implements DrawInfo {

    private final DrawType drawType;
    private final String gameType;
    private final TimeType timeType;

    DrawInfoImpl(DrawInfoCi drawInfo) {
        Preconditions.checkNotNull(drawInfo);

        drawType = drawInfo.getDrawType();
        gameType = drawInfo.getGameType();
        timeType = drawInfo.getTimeType();
    }

    /**
     * Returns the draw type
     *
     * @return the draw type
     */
    @Override
    public DrawType getDrawType() {
        return drawType;
    }

    /**
     * Returns the draw time type
     *
     * @return the draw time type
     */
    @Override
    public TimeType getTimeType() {
        return timeType;
    }

    /**
     * Returns the draw game type
     *
     * @return the draw game type
     */
    @Override
    public String getGameType() {
        return gameType;
    }
}
