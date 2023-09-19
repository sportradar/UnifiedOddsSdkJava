/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */

package com.sportradar.unifiedodds.sdk.impl.entities;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableMap;
import com.sportradar.unifiedodds.sdk.entities.SportSummary;
import com.sportradar.utils.Urn;
import java.util.Locale;
import java.util.Map;

/**
 * Represents a basic sport summary
 */
public class SportSummaryImpl implements SportSummary {

    /**
     * An {@link Urn} uniquely identifying the sport represented by the current instance
     */
    private final Urn id;

    /**
     * An unmodifiable {@link Map} containing translated sport names
     */
    private final Map<Locale, String> names;

    /**
     * Initializes a new instance of {@link SportImpl}
     *
     * @param id - an {@link Urn} uniquely identifying the sport represented by the current instance
     * @param names - a {@link Map} containing translated sport names
     */
    SportSummaryImpl(Urn id, Map<Locale, String> names) {
        Preconditions.checkNotNull(id);
        Preconditions.checkNotNull(names);
        Preconditions.checkArgument(!names.isEmpty());

        this.id = id;
        this.names = ImmutableMap.copyOf(names);
    }

    /**
     * Returns an {@link Urn} uniquely identifying the sport represented by the current instance
     *
     * @return - an {@link Urn} uniquely identifying the sport represented by the current instance
     */
    @Override
    public Urn getId() {
        return id;
    }

    /**
     * Returns the name of the current {@link SportSummary} instance in the specified language
     *
     * @param l - a {@link Locale} specifying the language in which the name should be translated
     * @return - the name of the current {@link SportSummary} instance in the specified language
     */
    @Override
    public String getName(Locale l) {
        return names.get(l);
    }

    /**
     * Returns an unmodifiable {@link Map} containing translated sport names
     *
     * @return - an unmodifiable {@link Map} containing translated sport names
     */
    @Override
    public Map<Locale, String> getNames() {
        return names;
    }

    /**
     * Returns a {@link String} describing the current {@link SportSummary} instance
     *
     * @return - a {@link String} describing the current {@link SportSummary} instance
     */
    @Override
    public String toString() {
        return "SportSummaryImpl{" + "id=" + id + ", names=" + names + '}';
    }
}
