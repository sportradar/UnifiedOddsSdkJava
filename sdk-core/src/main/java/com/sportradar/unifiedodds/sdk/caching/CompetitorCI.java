/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */

package com.sportradar.unifiedodds.sdk.caching;

import com.sportradar.unifiedodds.sdk.caching.ci.JerseyCI;
import com.sportradar.unifiedodds.sdk.caching.ci.ManagerCI;
import com.sportradar.unifiedodds.sdk.caching.ci.ReferenceIdCI;
import com.sportradar.unifiedodds.sdk.caching.ci.VenueCI;
import com.sportradar.utils.URN;

import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * Defines methods used to access competitor information
 */
public interface CompetitorCI extends CacheItem {
    /**
     * Returns the {@link Map} containing translated country names
     *
     * @param locales a {@link List} specifying the required languages
     * @return the {@link Map} containing translated country names
     */
    Map<Locale, String> getCountryNames(List<Locale> locales);

    /**
     * Returns the {@link Map} containing translated competitor abbreviations
     *
     * @param locales a {@link List} specifying the required languages
     * @return the {@link Map} containing translated competitor abbreviations
     */
    Map<Locale, String> getAbbreviations(List<Locale> locales);

    /**
     * Returns a value indicating whether represented competitor is virtual
     *
     * @return - a value indicating whether represented competitor is virtual
     */
    boolean isVirtual();

    /**
     * Returns a {@link String} representation of a country code
     *
     * @return - a {@link String} representation of a country code
     */
    String getCountryCode();

    /**
     * Returns the reference ids associated with the current instance
     *
     * @return - the reference ids associated with the current instance
     */
    ReferenceIdCI getReferenceId();

    /**
     * Returns a {@link List} of associated player ids
     *
     * @param locales the locales in which the players data should be pre-fetched
     * @return {@link List} of associated player ids
     */
    List<URN> getAssociatedPlayerIds(List<Locale> locales);

    /**
     * Returns a {@link List} of known competitor jerseys
     *
     * @return {@link List} of known competitor jerseys
     */
    List<JerseyCI> getJerseys();

    /**
     * Returns the associated competitor manager
     *
     * @param locales the locales in which the data should be translated
     * @return the associated competitor manager
     */
    ManagerCI getManager(List<Locale> locales);

    /**
     * Return the associated competitor home venue
     *
     * @param locales the locales in which the data should be translated
     * @return the associated competitor home venue
     */
    VenueCI getVenue(List<Locale> locales);

    /**
     * Get the gender of the player
     * @return the gender
     */
    String getGender();
}
