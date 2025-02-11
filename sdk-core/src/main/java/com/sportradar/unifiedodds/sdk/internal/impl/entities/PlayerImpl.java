/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */

package com.sportradar.unifiedodds.sdk.internal.impl.entities;

import com.google.common.base.Preconditions;
import com.sportradar.unifiedodds.sdk.entities.Player;
import com.sportradar.utils.Urn;
import java.util.Locale;
import java.util.Map;

/**
 * Represents a player or driver in a sport event
 */
@SuppressWarnings({ "DeclarationOrder", "VisibilityModifier" })
public class PlayerImpl implements Player {

    /**
     * The unique {@link Urn} identifier representing the current {@link Player} instance
     */
    private final Urn id;

    /**
     * An unmodifiable {@link Map} containing the available player name translations
     * @see com.google.common.collect.ImmutableMap
     */
    protected final Map<Locale, String> names;

    /**
     * Initializes a new instance of the {@link PlayerImpl} class
     *
     * @param id - the unique {@link Urn} identifier representing the current {@link Player} instance
     * @param names - a {@link Map} containing the available player name translations
     */
    PlayerImpl(Urn id, Map<Locale, String> names) {
        Preconditions.checkNotNull(id);
        Preconditions.checkNotNull(names);

        this.id = id;
        this.names = names;
    }

    /**
     * Returns the unique {@link Urn} identifier representing the current {@link Player} instance
     *
     * @return - the unique {@link Urn} identifier representing the current {@link Player} instance
     */
    @Override
    public Urn getId() {
        return id;
    }

    /**
     * Returns an unmodifiable map of available translated names
     *
     * @return - an unmodifiable map of available translated names
     * @see com.google.common.collect.ImmutableMap
     */
    @Override
    public Map<Locale, String> getNames() {
        return names;
    }

    /**
     * Returns the name of the player in the specified language
     *
     * @param locale - a {@link Locale} specifying the language of the returned name
     * @return - the name of the player in the specified language
     */
    @Override
    public String getName(Locale locale) {
        return names.get(locale);
    }

    /**
     * Returns a {@link String} describing the current {@link Player} instance
     *
     * @return - a {@link String} describing the current {@link Player} instance
     */
    @Override
    public String toString() {
        return "PlayerImpl{" + "id=" + id + ", names=" + names + '}';
    }
}
