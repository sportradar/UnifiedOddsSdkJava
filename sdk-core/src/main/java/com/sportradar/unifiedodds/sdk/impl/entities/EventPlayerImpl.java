/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */

package com.sportradar.unifiedodds.sdk.impl.entities;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableMap;
import com.sportradar.unifiedodds.sdk.caching.ci.EventPlayerCI;
import com.sportradar.unifiedodds.sdk.entities.EventPlayer;
import com.sportradar.unifiedodds.sdk.entities.Player;
import com.sportradar.utils.URN;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

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
     * @param eventPlayerCI - the {@link EventPlayerCI} instance
     */
    EventPlayerImpl(EventPlayerCI eventPlayerCI, Locale dataLocale) {
        super(eventPlayerCI.getId(), new HashMap<>());
        Preconditions.checkNotNull(eventPlayerCI);

        super.names.put(dataLocale, eventPlayerCI.getName());
        this.bench = eventPlayerCI.getBench();
    }

    /**
     * Returns a {@link String} describing the current {@link Player} instance
     *
     * @return - a {@link String} describing the current {@link Player} instance
     */
    @Override
    public String toString() {
        return "EventPlayerImpl{" +
                "id=" + super.getId() +
                ", names=" + names +
                ", bench=" + bench +
                '}';
    }

    @Override
    public String getBench() {
        return bench;
    }
}
