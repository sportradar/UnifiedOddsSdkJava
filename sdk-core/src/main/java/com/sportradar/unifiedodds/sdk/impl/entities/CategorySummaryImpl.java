/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */

package com.sportradar.unifiedodds.sdk.impl.entities;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableMap;
import com.sportradar.unifiedodds.sdk.entities.CategorySummary;
import com.sportradar.utils.URN;

import java.util.Locale;
import java.util.Map;
import java.util.Optional;

/**
 * Represents a category summary
 */
public class CategorySummaryImpl implements CategorySummary {
    /**
     * An {@link URN} uniquely identifying the current {@link CategorySummary} instance
     */
    private final URN id;

    /**
     * An unmodifiable {@link Map} containing translated category name
     */
    private final Map<Locale, String> names;

    /**
     * A {@link String} representation of a country code
     */
    private final String countryCode;

    /**
     * Initializes a new instance of the {@link CategorySummaryImpl}
     *
     * @param id - an {@link URN} uniquely identifying the current {@link CategorySummary} instance
     * @param names - a {@link Map} containing translated category name
     * @param countryCode - a {@link String} representation of a country code
     */
    public CategorySummaryImpl(URN id, Map<Locale, String> names, String countryCode) {
        Preconditions.checkNotNull(id);
        Preconditions.checkNotNull(names);
        Preconditions.checkArgument(!names.isEmpty());

        this.id = id;
        this.names = ImmutableMap.copyOf(names);
        this.countryCode = countryCode;
    }


    /**
     * Returns an {@link URN} uniquely identifying the current {@link CategorySummary} instance
     *
     * @return - an {@link URN} uniquely identifying the current {@link CategorySummary} instance
     */
    @Override
    public URN getId() {
        return id;
    }

    /**
     * Returns the current {@link CategorySummary} instance name in the specified language
     * @see Optional
     *
     * @param l - a {@link Locale} in which the name should be returned
     * @return - the translated name
     */
    @Override
    public String getName(Locale l){
        return names.get(l);
    }

    /**
     * Returns an unmodifiable {@link Map} containing translated category name
     *
     * @return - an unmodifiable {@link Map} containing translated category name
     */
    @Override
    public Map<Locale, String> getNames() {
        return names;
    }

    /**
     * Returns a {@link String} representation of a country code
     *
     * @return - a {@link String} representation of a country code
     */
    @Override
    public String getCountryCode() {
        return countryCode;
    }

    /**
     * Returns a {@link String} describing the current {@link CategorySummary} instance
     *
     * @return - a {@link String} describing the current {@link CategorySummary} instance
     */
    @Override
    public String toString() {
        return "CategorySummaryImpl{" +
                "id=" + id +
                ", names=" + names +
                ", countryCode=" + countryCode +
                '}';
    }
}
