/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */

package com.sportradar.unifiedodds.sdk.caching.impl;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableMap;
import com.sportradar.utils.Urn;
import java.util.Locale;
import java.util.Map;

/**
 * Contains sport related entity (sport, category, tournament) data
 */
public abstract class SportEntityData {

    /**
     * The {@link Urn} the id of the associated entity
     */
    private final Urn id;

    /**
     * A {@link Map} containing translated entity name
     */
    private final Map<Locale, String> names;

    /**
     * Initializes a new instance of the {@link SportEntityData} class
     *
     * @param id - a {@link Urn} specifying the id of the associated entity
     * @param names - A {@link Map} containing translated entity name
     */
    SportEntityData(Urn id, Map<Locale, String> names) {
        Preconditions.checkNotNull(id);
        Preconditions.checkNotNull(names);

        this.id = id;
        this.names = ImmutableMap.copyOf(names);
    }

    /**
     * Returns the {@link Urn} the id of the associated entity
     *
     * @return - the {@link Urn} the id of the associated entity
     */
    public Urn getId() {
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

    /**
     * Provides translated entity names
     */
    public String getName(Locale language) {
        return names.get(language);
    }
}
