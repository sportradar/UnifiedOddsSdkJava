/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */

package com.sportradar.unifiedodds.sdk.entities;

import com.sportradar.utils.Urn;

/**
 * Defines methods implemented by classes representing the replay sport event
 */
public interface ReplaySportEvent {
    /**
     * Returns an {@link Urn} uniquely identifying the sport event
     *
     * @return - an {@link Urn} uniquely identifying the sport event
     */
    Urn getId();

    /**
     * Returns an {@link Integer} specifying position in the queue
     *
     * @return - an {@link Integer} specifying position in the queue
     */
    Integer getPosition();

    /**
     * Returns an {@link Integer} specifying the start time
     *
     * @return - an {@link Integer} specifying the start time
     */
    Integer getStartTime();
}
