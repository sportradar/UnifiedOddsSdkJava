/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */

package com.sportradar.unifiedodds.sdk.entities;

import com.sportradar.utils.Urn;
import java.util.Locale;
import java.util.Map;

/**
 * Defines methods implemented by classes representing the sport event referee
 */
public interface Referee {
    /**
     * Returns the unique identifier of the current {@link Referee} instance
     *
     * @return - the unique identifier of the current {@link Referee} instance
     */
    Urn getId();

    /**
     * Returns the name of the referee represented by the current {@link Referee} instance
     *
     * @return - the name of the referee represented by the current {@link Referee} instance
     */
    String getName();

    /**
     * Returns the nationality in the requested locale
     *
     * @param locale - a {@link Locale} in which the nationality is requested
     * @return - the nationality in the requested locale
     */
    String getNationality(Locale locale);

    /**
     * Returns an unmodifiable {@link Map} containing referee nationality in different languages
     *
     * @return - an unmodifiable {@link Map} containing referee nationality in different languages
     */
    Map<Locale, String> getNationalities();
}
