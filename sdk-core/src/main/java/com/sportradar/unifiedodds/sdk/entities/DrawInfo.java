/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */

package com.sportradar.unifiedodds.sdk.entities;

/**
 * Defines methods used to access draw info data
 */
public interface DrawInfo {
    /**
     * Returns the draw type
     *
     * @return the draw type
     */
    DrawType getDrawType();

    /**
     * Returns the draw time type
     *
     * @return the draw time type
     */
    TimeType getTimeType();

    /**
     * Returns the draw game type
     *
     * @return the draw game type
     */
    String getGameType();
}
