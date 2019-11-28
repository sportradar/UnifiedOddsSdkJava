/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */

package com.sportradar.unifiedodds.sdk.entities;

import com.sportradar.utils.URN;

/**
 * Defines methods implemented by classes representing the replay sport event
 */
public interface ReplaySportEvent {
    /**
     * Returns an {@link URN} uniquely identifying the sport event
     *
     * @return - an {@link URN} uniquely identifying the sport event
     */
    URN getId();

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
