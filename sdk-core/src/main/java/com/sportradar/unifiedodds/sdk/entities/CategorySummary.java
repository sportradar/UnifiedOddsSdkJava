/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */

package com.sportradar.unifiedodds.sdk.entities;

import com.sportradar.utils.Urn;
import java.util.Locale;
import java.util.Map;

/**
 * Defines methods implemented by classes representing sport category
 */
public interface CategorySummary {
    /**
     * Returns an {@link Urn} uniquely identifying the current {@link CategorySummary} instance
     *
     * @return - an {@link Urn} uniquely identifying the current {@link CategorySummary} instance
     */
    Urn getId();

    /**
     * Returns the current {@link CategorySummary} instance name in the specified language
     *
     * @param l - a {@link Locale} in which the name should be returned
     * @return - the translated name
     */
    String getName(Locale l);

    /**
     * Returns an unmodifiable {@link Map} containing translated category name
     *
     * @return - an unmodifiable {@link Map} containing translated category name
     */
    Map<Locale, String> getNames();

    /**
     * Returns a {@link String} representation of a country code
     *
     * @return - a {@link String} representation of a country code
     */
    String getCountryCode();
}
