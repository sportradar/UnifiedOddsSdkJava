/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */

package com.sportradar.unifiedodds.sdk.entities;

import com.sportradar.utils.URN;

import java.util.Date;
import java.util.Locale;
import java.util.Map;

/**
 * Defines methods implemented by classes providing season information about an entity (sport, category, season, ...)
 */
public interface SeasonInfo {
    /**
     * Returns a {@link URN} uniquely identifying the current {@link SeasonInfo} instance
     *
     * @return - a {@link URN} uniquely identifying the current {@link SeasonInfo} instance
     */
    URN getId();

    /**
     * Returns the name of the season in the specified language
     *
     * @param locale - a {@link Locale} specifying the language of the returned name
     * @return - the name of the season in the specified language
     */
    String getName(Locale locale);

    /**
     * Returns an unmodifiable {@link Map} containing translated names
     *
     * @return - an unmodifiable {@link Map} containing translated names
     */
    Map<Locale, String> getNames();

    /**
     * Returns the {@link Date} specifying the start date of the season
     *
     * @return - the {@link Date} specifying the start date of the season
     */
    default Date getStartDate() {
        return null;
    }

    /**
     * Returns the {@link Date} specifying the end date of the season
     *
     * @return - the {@link Date} specifying the end date of the season
     */
    default Date getEndDate() {
        return null;
    }

    /**
     * Returns the {@link String} representation the year of the season
     *
     * @return - the {@link String} representation the year of the season
     */
    default String getYear() {
        return null;
    }

    /**
     * Returns the associated tournament identifier
     *
     * @return the associated tournament identifier
     */
    default URN getTournamentId() {
        return null;
    }
}
