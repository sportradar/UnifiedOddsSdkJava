/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */

package com.sportradar.unifiedodds.sdk.impl.entities;

import com.google.common.base.Preconditions;
import com.sportradar.unifiedodds.sdk.entities.SportEvent;
import com.sportradar.utils.Urn;

/**
 * Represents all sport events(races, matches, tournaments, ....)
 */
@SuppressWarnings({ "VisibilityModifier" })
abstract class SportEventImpl implements SportEvent {

    /**
     * An {@link Urn} uniquely identifying the tournament associated with the current instance
     */
    final Urn id;

    /**
     * A {@link Urn} uniquely identifying the sport to which the event is related
     */
    final Urn sportId;

    /**
     * Initializes a new instance of {@link SportEventImpl}
     *
     * @param id an {@link Urn} uniquely identifying the tournament
     * @param sportId the identifier of the sport to which the event belongs
     */
    SportEventImpl(Urn id, Urn sportId) {
        Preconditions.checkNotNull(id);

        this.id = id;
        this.sportId = sportId;
    }

    /**
     * Returns an {@link Urn} uniquely identifying the tournament associated with the current instance
     *
     * @return - an {@link Urn} uniquely identifying the tournament associated with the current instance
     */
    @Override
    public Urn getId() {
        return id;
    }

    /**
     * Returns the unique sport identifier to which this event is associated
     *
     * @return - the unique sport identifier to which this event is associated
     */
    @Override
    public Urn getSportId() {
        return sportId;
    }
}
