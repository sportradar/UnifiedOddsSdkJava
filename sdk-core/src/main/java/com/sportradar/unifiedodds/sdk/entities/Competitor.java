/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */

package com.sportradar.unifiedodds.sdk.entities;

import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * An interface providing methods to access competitor data
 */
@SuppressWarnings({ "MultipleStringLiterals" })
public interface Competitor extends Player {
    /**
     * Returns an unmodifiable map of available translated competitor country names
     *
     * @return an unmodifiable map of available translated competitor country names
     */
    Map<Locale, String> getCountries();

    /**
     * Returns an unmodifiable map of available translated competitor abbreviations
     *
     * @return an unmodifiable map of available translated competitor abbreviations
     */
    Map<Locale, String> getAbbreviations();

    /**
     * Returns a value indicating whether the current instance represents a placeholder team
     * @return - a value indicating whether the current instance represents a placeholder team
     */
    boolean isVirtual();

    /**
     * Returns the reference ids
     *
     * @return - the reference ids
     */
    Reference getReferences();

    /**
     * Returns a {@link String} representation of a country code
     *
     * @return - a {@link String} representation of a country code
     */
    String getCountryCode();

    /**
     * Returns the translated competitor country name
     *
     * @param locale - a {@link Locale} specifying the language in which to get the country name
     * @return - the translated competitor country name
     */
    String getCountry(Locale locale);

    /**
     * Returns the translated competitor abbreviation
     *
     * @param locale - a {@link Locale} specifying the language in which to get the abbreviation
     * @return - the translated competitor abbreviation
     */
    String getAbbreviation(Locale locale);

    /**
     * Returns a {@link List} of associated players
     *
     * @return {@link List} of associated players
     */
    List<Player> getPlayers();

    /**
     * Returns a {@link List} of known competitor jerseys
     *
     * @return {@link List} of known competitor jerseys
     */
    List<Jersey> getJerseys();

    /**
     * Returns the associated competitor manager
     *
     * @return the associated competitor manager
     */
    Manager getManager();

    /**
     * Return the associated competitor home venue
     *
     * @return the associated competitor home venue
     */
    Venue getVenue();

    /**
     * Returns gender of the competitor
     *
     * @return the gender of the competitor if available; otherwise null
     */
    default String getGender() {
        throw new UnsupportedOperationException("Method not implemented. Use derived type.");
    }

    /**
     * Returns race driver of the competitor
     *
     * @return the race driver of the competitor if available; otherwise null
     */
    default RaceDriverProfile getRaceDriver() {
        throw new UnsupportedOperationException("Method not implemented. Use derived type.");
    }

    /**
     * Returns age group of the competitor
     *
     * @return the age group of the competitor if available; otherwise null
     */
    default String getAgeGroup() {
        throw new UnsupportedOperationException("Method not implemented. Use derived type.");
    }

    /**
     * Returns the state (default method)
     *
     * @return state
     *
     * @throws UnsupportedOperationException if underlying implementation doesn't provide the state
     */
    default String getState() {
        throw new UnsupportedOperationException("This interface method is missing the implementation.");
    }

    /**
     * Returns associated sport
     * @return sport if available; otherwise null
     */
    default Sport getSport() {
        throw new UnsupportedOperationException("Method not implemented. Use derived type.");
    }

    /**
     * Returns associated category
     * @return category if available; otherwise null
     */
    default CategorySummary getCategory() {
        throw new UnsupportedOperationException("Method not implemented. Use derived type.");
    }

    /**
     * Returns the short name
     * @return the short name if available; otherwise null
     */
    default String getShortName() {
        throw new UnsupportedOperationException("Method not implemented. Use derived type.");
    }

    Division getDivision();
}
