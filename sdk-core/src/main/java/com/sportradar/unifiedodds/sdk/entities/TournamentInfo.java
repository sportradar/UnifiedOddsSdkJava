/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */

package com.sportradar.unifiedodds.sdk.entities;

import com.sportradar.utils.Urn;
import java.util.Locale;
import java.util.Map;

/**
 * Defines methods implemented by classes providing data of a tournament
 */
public interface TournamentInfo {
    /**
     * Returns the {@link Urn} uniquely identifying the tournament
     *
     * @return - the {@link Urn} uniquely identifying the tournament
     */
    Urn getId();

    /**
     * Returns the name of the tournament in the specified language
     *
     * @param locale - a {@link Locale} specifying the language of the returned name
     * @return - the name of the tournament in the specified language
     */
    String getName(Locale locale);

    Map<Locale, String> getNames();

    /**
     * Returns a {@link CategorySummary} representing the category associated with the current instance
     *
     * @return - a {@link CategorySummary} representing the category associated with the current instance
     */
    CategorySummary getCategory();

    /**
     * Returns a {@link CurrentSeasonInfo} which contains data about the season in which the current
     * tournament is happening
     *
     * @return - a {@link CurrentSeasonInfo} which provides data about the season in
     *           which the current tournament is happening
     */
    CurrentSeasonInfo getCurrentSeason();
}
