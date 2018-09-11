/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */

package com.sportradar.unifiedodds.sdk.caching;

import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * Defines methods used to access cached player data
 */
public interface PlayerProfileCI extends CacheItem  {
    /**
     * Returns the {@link Map} containing translated full names of the player
     *
     * @param locales a {@link List} specifying the required languages
     * @return the {@link Map} containing translated full names of the player
     */
    Map<Locale, String> getFullNames(List<Locale> locales);

    /**
     * Returns the {@link Map} containing translated nationalities of the player
     *
     * @param locales a {@link List} specifying the required languages
     * @return the {@link Map} containing translated nationalities of the player
     */
    Map<Locale, String> getNationalities(List<Locale> locales);

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

    /**
     * Returns the {@link Map} containing translated abbreviations of the player
     *
     * @param locales a {@link List} specifying the required languages
     * @return the {@link Map} containing translated abbreviations of the player
     */
    Map<Locale, String> getAbbreviations(List<Locale> locales);
}
