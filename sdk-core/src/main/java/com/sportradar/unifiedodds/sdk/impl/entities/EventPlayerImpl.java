/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */

package com.sportradar.unifiedodds.sdk.impl.entities;

import com.google.common.base.Preconditions;
import com.sportradar.unifiedodds.sdk.caching.ci.EventPlayerCi;
import com.sportradar.unifiedodds.sdk.entities.EventPlayer;
import com.sportradar.unifiedodds.sdk.entities.Player;
import java.util.HashMap;
import java.util.Locale;

/**
 * Represents a player or driver in a sport event
 */
public class EventPlayerImpl extends PlayerImpl implements EventPlayer {

    /**
     * The bench value
     */
    private final String bench;

    /**
     * Initializes a new instance of the {@link EventPlayerImpl} class
     *
     * @param eventPlayerCi - the {@link EventPlayerCi} instance
     */
    EventPlayerImpl(EventPlayerCi eventPlayerCi, Locale dataLocale) {
        super(eventPlayerCi.getId(), new HashMap<>());
        Preconditions.checkNotNull(eventPlayerCi);

        super.names.put(dataLocale, eventPlayerCi.getName());
        this.bench = eventPlayerCi.getBench();
    }

    /**
     * Returns a {@link String} describing the current {@link Player} instance
     *
     * @return - a {@link String} describing the current {@link Player} instance
     */
    @Override
    public String toString() {
        return "EventPlayerImpl{" + "id=" + super.getId() + ", names=" + names + ", bench=" + bench + '}';
    }

    @Override
    public String getBench() {
        return bench;
    }
}
