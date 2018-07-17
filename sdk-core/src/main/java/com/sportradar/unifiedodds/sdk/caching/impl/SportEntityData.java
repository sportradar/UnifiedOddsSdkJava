/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */

package com.sportradar.unifiedodds.sdk.caching.impl;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableMap;
import com.sportradar.utils.URN;

import java.util.Locale;
import java.util.Map;

/**
 * Contains sport related entity (sport, category, tournament) data
 */
public abstract class SportEntityData {
    /**
     * The {@link URN} the id of the associated entity
     */
    private final URN id;

    /**
     * A {@link Map} containing translated entity name
     */
    private final Map<Locale, String> names;

    /**
     * Initializes a new instance of the {@link SportEntityData} class
     *
     * @param id - a {@link URN} specifying the id of the associated entity
     * @param names - A {@link Map} containing translated entity name
     */
    SportEntityData(URN id, Map<Locale, String> names) {
        Preconditions.checkNotNull(id);
        Preconditions.checkNotNull(names);

        this.id = id;
        this.names = ImmutableMap.copyOf(names);
    }

    /**
     * Returns the {@link URN} the id of the associated entity
     *
     * @return - the {@link URN} the id of the associated entity
     */
    public URN getId() {
        return id;
    }

    /**
     * Returns an unmodifiable {@link Map} containing translated entity names
     *
     * @return - an unmodifiable {@link Map} containing translated entity names
     */
    public Map<Locale, String> getNames() {
        return names;
    }
}
