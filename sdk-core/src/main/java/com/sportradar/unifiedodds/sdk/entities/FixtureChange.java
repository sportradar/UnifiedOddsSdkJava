/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */

package com.sportradar.unifiedodds.sdk.entities;

import com.sportradar.utils.Urn;
import java.util.Date;

/**
 * Defines methods used to access data of a fixture change
 */
public interface FixtureChange {
    /**
     * Returns the {@link Urn} instance specifying the sport event
     *
     * @return - the {@link Urn} instance specifying the sport event
     */
    Urn getSportEventId();

    /**
     * Returns the {@link Date} instance specifying the last update time
     *
     * @return - the {@link Date} instance specifying the last update time
     */
    Date getUpdateTime();
}
