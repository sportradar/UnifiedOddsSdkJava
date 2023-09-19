/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */

package com.sportradar.unifiedodds.sdk.entities;

import com.sportradar.utils.Urn;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * Defines methods implemented by classes representing a sport event venue
 */
public interface Venue {
    /**
     * Returns a unique {@link Urn} identifier representing the current {@link Venue} instance
     *
     * @return - a unique {@link Urn} identifier representing the current {@link Venue} instance
     */
    Urn getId();

    /**
     * Returns the name of the venue in the specified language
     *
     * @param locale - a {@link Locale} specifying the language of the returned venue name
     * @return - the name of the venue in the specified language
     */
    String getName(Locale locale);

    /**
     * Returns the city name in the specified language
     *
     * @param locale - a {@link Locale} specifying the language of the returned city name
     * @return - the city name in the specified language
     */
    String getCity(Locale locale);

    /**
     * Returns the country name in the specified language
     *
     * @param locale - a {@link Locale} specifying the language of the returned country name
     * @return - the country name in the specified language
     */
    String getCountry(Locale locale);

    /**
     * Returns an unmodifiable {@link Map} containing venue's names in different languages
     *
     * @return - an unmodifiable {@link Map} containing venue's names in different languages
     */
    Map<Locale, String> getNames();

    /**
     * Returns an unmodifiable {@link Map} containing venue's city names in different languages
     *
     * @return - an unmodifiable {@link Map} containing venue's city names in different languages
     */
    Map<Locale, String> getCities();

    /**
     * Returns an unmodifiable {@link Map} containing venue's country names in different languages
     *
     * @return - an unmodifiable {@link Map} containing venue's country names in different languages
     */
    Map<Locale, String> getCountries();

    /**
     * Returns the capacity of the venue associated with current {@link Venue} instance
     *
     * @return - the capacity of the venue, or a null if the capacity is not specified
     */
    Integer getCapacity();

    /**
     * Returns the map coordinates specifying the exact location of the venue represented by current {@link Venue}
     *
     * @return - the map coordinates specifying the exact location of the venue
     */
    String getCoordinates();

    /**
     * Returns the associated country code
     *
     * @return the associated country code
     */
    String getCountryCode();

    /**
     * Returns state/province of the country
     *
     * @return state
     * @throws UnsupportedOperationException when method isn't implemented
     */
    default String getState() {
        throw new UnsupportedOperationException("This method MUST be implemented.");
    }

    /**
     * Gets the list of the course holes
     * @return the list of the course holes
     */
    default List<Hole> getCourse() {
        throw new UnsupportedOperationException("This method MUST be implemented.");
    }
}
