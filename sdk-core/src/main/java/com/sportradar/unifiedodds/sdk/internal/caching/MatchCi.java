/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */

package com.sportradar.unifiedodds.sdk.internal.caching;

import com.sportradar.unifiedodds.sdk.entities.Competitor;
import com.sportradar.unifiedodds.sdk.entities.CoverageInfo;
import com.sportradar.unifiedodds.sdk.entities.Fixture;
import com.sportradar.unifiedodds.sdk.internal.caching.ci.*;
import com.sportradar.utils.Urn;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * Defines methods used to access match type properties
 */
@SuppressWarnings({ "LineLength" })
public interface MatchCi extends CompetitionCi {
    /**
     * Returns a {@link Map} of translated sport event names
     * The match object name is composed from the home and away team(eg. Home vs Away)
     *
     * @param locales the {@link Locale}s in which the name should be provided
     * @return the sport event name if available; otherwise null
     */
    Map<Locale, String> getNames(List<Locale> locales);

    /**
     * Returns the {@link Urn} specifying the id of the tournament to which the sport event belongs to
     *
     * @return - the {@link Urn} specifying the id of the tournament to which the sport event belongs to
     */
    Urn getTournamentId();

    /**
     * Returns a {@link RoundCi} instance describing the tournament round to which the
     * sport event associated with current instance belongs to
     *
     * @param locales - a {@link List} of {@link Locale} specifying the languages to which the returned instance should be translated
     * @return - a {@link RoundCi} instance describing the tournament round
     */
    RoundCi getTournamentRound(List<Locale> locales);

    /**
     * Returns a {@link SeasonCi} instance providing basic information about
     * the season to which the sport event associated with the current instance belongs to
     *
     * @param locales - a {@link List} of {@link Locale} specifying the languages to which the returned instance should be translated
     * @return -  {@link SeasonCi} instance providing basic information about the associated season
     */
    SeasonCi getSeason(List<Locale> locales);

    /**
     * Returns the {@link Fixture} instance containing information about the arranged sport event
     * <i>A Fixture is a sport event that has been arranged for a particular time and place</i>
     *
     * @param locales - a {@link List} of {@link Locale} specifying the languages to which the returned instance should be translated
     * @return - the {@link Fixture} instance containing information about the arranged sport event
     */
    Fixture getFixture(List<Locale> locales);

    /**
     * Returns a {@link DelayedInfoCi} instance describing possible information about a delay
     *
     * @param locales the {@link Locale}s in which the data should be provided
     * @return a {@link DelayedInfoCi} instance describing information about a possible delay
     */
    DelayedInfoCi getDelayedInfo(List<Locale> locales);

    /**
     * Returns a {@link CoverageInfo} instance
     *
     * @param locales the {@link Locale}s in which the data should be provided
     * @return a {@link CoverageInfo} instance
     */
    CoverageInfoCi getCoverageInfo(List<Locale> locales);

    /**
     * Returns the associated event timeline
     * (the timeline is cached only after the event status indicates that the event has finished)
     *
     * @param locale the locale in which the timeline should be provided
     * @param makeApiCall should the API call be made if necessary
     * @return the associated event timeline
     */
    EventTimelineCi getEventTimeline(Locale locale, boolean makeApiCall);

    /**
     * Returns list of {@link Urn} of {@link Competitor} and associated qualifier for this sport event
     * @return list of {@link Urn} of {@link Competitor} and associated qualifier for this sport event
     */
    Map<Urn, String> getCompetitorsQualifiers();

    /**
     * Returns list of {@link Urn} of {@link CompetitorCi} and associated division for this sport event
     *
     * @return list of {@link Urn} of {@link CompetitorCi} and associated division for this sport event
     */
    Map<Urn, Integer> getCompetitorsDivisions();
}
