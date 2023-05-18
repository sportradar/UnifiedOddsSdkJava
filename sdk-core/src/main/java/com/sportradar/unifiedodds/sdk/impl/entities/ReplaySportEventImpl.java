/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */

package com.sportradar.unifiedodds.sdk.impl.entities;

import com.google.common.base.Preconditions;
import com.sportradar.unifiedodds.sdk.entities.ReplaySportEvent;
import com.sportradar.utils.URN;

/**
 * Represents the replay sport event
 */
public class ReplaySportEventImpl implements ReplaySportEvent {

    private final URN id;
    private final Integer position;
    private final Integer startTime;

    /**
     * Creates new instance of {@link ReplaySportEventImpl}
     * @param id the id of the sport event
     * @param position The position of the event in the queue
     * @param startTime The start time specified when the event was added to the queue
     */
    public ReplaySportEventImpl(URN id, Integer position, Integer startTime) {
        Preconditions.checkNotNull(id);

        this.id = id;
        this.position = position;
        this.startTime = startTime;
    }

    /**
     * Returns an {@link URN} uniquely identifying the sport event
     *
     * @return - an {@link URN} uniquely identifying the sport event
     */
    @Override
    public URN getId() {
        return id;
    }

    /**
     * Returns an {@link Integer} specifying position in the queue
     *
     * @return - an {@link Integer} specifying position in the queue
     */
    @Override
    public Integer getPosition() {
        return position;
    }

    /**
     * Returns an {@link Integer} specifying the start time
     *
     * @return - an {@link Integer} specifying the start time
     */
    @Override
    public Integer getStartTime() {
        return startTime;
    }
}
