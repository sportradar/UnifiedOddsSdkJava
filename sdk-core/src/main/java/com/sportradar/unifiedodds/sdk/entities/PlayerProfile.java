/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */

package com.sportradar.unifiedodds.sdk.entities;

import java.util.Date;
import java.util.Locale;
import java.util.Map;

/**
 * Defines methods implemented by classes representing a player profile
 */
public interface PlayerProfile extends Player {
    /**
     * Returns the player full name in the specified language
     *
     * @param locale - {@link Locale} specifying the language of the returned player name
     * @return - The player full name in the specified language if it exists. Null otherwise.
     */
    String getFullName(Locale locale);

    /**
     * Returns the value describing the type(e.g. forward, defense, ...) of the player represented by current instance
     *
     * @return - the value describing the type(e.g. forward, defense, ...) of the player represented by current instance
     */
    String getType();

    /**
     * Returns the {@link Date} specifying the date of birth of the player associated with the current instance
     *
     * @return - the {@link Date} specifying the date of birth of the player associated with the current instance
     */
    Date getDateOfBirth();

    /**
     * Returns the height in centimeters of the player represented by the current instance or a null reference if height is not known
     *
     * @return - the height in centimeters of the player represented by the current instance or a null reference if height is not known
     */
    Integer getHeight();

    /**
     * Returns the weight in kilograms of the player represented by the current instance or a null reference if weight is not known
     *
     * @return - the weight in kilograms of the player represented by the current instance or a null reference if weight is not known
     */
    Integer getWeight();

    /**
     * Returns a {@link String} representation of a country code
     *
     * @return - a {@link String} representation of a country code
     */
    String getCountryCode();

    /**
     * Returns the nationality of the player in the requested {@link Locale}
     *
     * @param locale - the {@link Locale} in which to return the nationality
     * @return - the nationality of the player in the requested {@link Locale}
     */
    String getNationality(Locale locale);

    /**
     * Returns an unmodifiable {@link Map} containing player's nationality in different languages
     *
     * @return - an unmodifiable {@link Map} containing player's nationality in different languages
     */
    Map<Locale, String> getNationalities();

    /**
     * Returns the player jersey number
     *
     * @return the jersey number if available; otherwise null
     */
    Integer getJerseyNumber();

    /**
     * Returns the player nickname
     *
     * @return the player nickname if available; otherwise null
     */
    String getNickname();
}
