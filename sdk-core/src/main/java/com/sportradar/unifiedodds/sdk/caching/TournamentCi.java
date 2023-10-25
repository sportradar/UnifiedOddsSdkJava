/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */

package com.sportradar.unifiedodds.sdk.caching;

import com.sportradar.unifiedodds.sdk.caching.ci.*;
import com.sportradar.unifiedodds.sdk.caching.impl.ci.TournamentCoverageCi;
import com.sportradar.unifiedodds.sdk.entities.Competitor;
import com.sportradar.unifiedodds.sdk.entities.Reference;
import com.sportradar.utils.Urn;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * Defines methods used to access cached tournament data
 */
@SuppressWarnings({ "LineLength" })
public interface TournamentCi extends SportEventCi {
    /**
     * Returns the {@link Urn} specifying the id of the parent category
     *
     * @return the {@link Urn} specifying the id of the parent category
     */
    Urn getCategoryId();

    /**
     * Returns a {@link SeasonCi} representing the current season of the tournament
     *
     * @param locales a {@link List} of {@link Locale} specifying the languages to which the returned instance should be translated
     * @return a {@link SeasonCi} representing the current season of the tournament
     */
    SeasonCi getCurrentSeason(List<Locale> locales);

    /**
     * Returns a {@link SeasonCoverageCi} containing information about the tournament coverage
     *
     * @return a {@link SeasonCoverageCi} containing information about the tournament coverage
     */
    SeasonCoverageCi getSeasonCoverage();

    /**
     * Returns the associated endpoint season
     *
     * @param locales the locales in which the data should be available
     * @return the associated season cache item
     */
    SeasonCi getSeason(List<Locale> locales);

    /**
     * Returns a {@link List} of the associated tournament competitor ids
     *
     * @param locales a {@link List} of {@link Locale} specifying the languages to which the returned instance should be translated
     * @return - if available a {@link List} of the associated tournament competitor ids; otherwise null
     */
    List<Urn> getCompetitorIds(List<Locale> locales);

    /**
     * Returns a list of groups related to the current instance
     *
     * @param locales a {@link List} of {@link Locale} specifying the languages to which the returned instance should be translated
     * @return a list of groups related to the current instance
     */
    List<GroupCi> getGroups(List<Locale> locales);

    /**
     * Returns the rounds related to the current instance
     *
     * @param locales a {@link List} of {@link Locale} specifying the languages to which the returned instance should be translated
     * @return the rounds related to the current instance
     */
    RoundCi getRound(List<Locale> locales);

    /**
     * Returns the current tournament coverage information
     *
     * @return a {@link TournamentCoverageCi} instance describing the current coverage indication
     */
    TournamentCoverageCi getTournamentCoverage();

    /**
     * Returns a list of associated season identifiers
     *
     * @return a list of associated season identifiers
     */
    List<Urn> getSeasonIds();

    /**
     * Returns list of {@link Urn} of {@link Competitor} and associated {@link Reference} for this sport event
     * @return list of {@link Urn} of {@link Competitor} and associated {@link Reference} for this sport event
     */
    Map<Urn, ReferenceIdCi> getCompetitorsReferences();

    /**
     * Returns the {@link Boolean} specifying if the tournament is exhibition game
     * @return if available, the {@link Boolean} specifying if the tournament is exhibition game
     */
    Boolean isExhibitionGames();
}
