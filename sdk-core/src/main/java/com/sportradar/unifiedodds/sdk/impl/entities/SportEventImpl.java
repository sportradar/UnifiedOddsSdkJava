/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */

package com.sportradar.unifiedodds.sdk.impl.entities;

import com.google.common.base.Preconditions;
import com.sportradar.unifiedodds.sdk.entities.SportEvent;
import com.sportradar.utils.URN;

/**
 * Represents all sport events(races, matches, tournaments, ....)
 */
abstract class SportEventImpl implements SportEvent {
    /**
     * An {@link URN} uniquely identifying the tournament associated with the current instance
     */
    final URN id;

    /**
     * A {@link URN} uniquely identifying the sport to which the event is related
     */
    final URN sportId;


    /**
     * Initializes a new instance of {@link SportEventImpl}
     *
     * @param id an {@link URN} uniquely identifying the tournament
     * @param sportId the identifier of the sport to which the event belongs
     */
    SportEventImpl(URN id, URN sportId) {
        Preconditions.checkNotNull(id);

        this.id = id;
        this.sportId = sportId;
    }


    /**
     * Returns an {@link URN} uniquely identifying the tournament associated with the current instance
     *
     * @return - an {@link URN} uniquely identifying the tournament associated with the current instance
     */
    @Override
    public URN getId() {
        return id;
    }

    /**
     * Returns the unique sport identifier to which this event is associated
     *
     * @return - the unique sport identifier to which this event is associated
     */
    @Override
    public URN getSportId() {
        return sportId;
    }
}
