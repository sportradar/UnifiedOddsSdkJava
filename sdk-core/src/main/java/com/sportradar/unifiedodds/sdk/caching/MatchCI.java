/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */

package com.sportradar.unifiedodds.sdk.caching;

import com.sportradar.unifiedodds.sdk.caching.ci.DelayedInfoCI;
import com.sportradar.unifiedodds.sdk.caching.ci.EventTimelineCI;
import com.sportradar.unifiedodds.sdk.caching.ci.RoundCI;
import com.sportradar.unifiedodds.sdk.caching.ci.SeasonCI;
import com.sportradar.unifiedodds.sdk.entities.Fixture;
import com.sportradar.utils.URN;

import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * Defines methods used to access match type properties
 */
public interface MatchCI extends CompetitionCI {
    /**
     * Returns a {@link Map} of translated sport event names
     * The match object name is composed from the home and away team(eg. Home vs Away)
     *
     * @param locales the {@link Locale}s in which the name should be provided
     * @return the sport event name if available; otherwise null
     */
    Map<Locale, String> getNames(List<Locale> locales);

    /**
     * Returns the {@link URN} specifying the id of the tournament to which the sport event belongs to
     *
     * @return - the {@link URN} specifying the id of the tournament to which the sport event belongs to
     */
    URN getTournamentId();

    /**
     * Returns a {@link RoundCI} instance describing the tournament round to which the
     * sport event associated with current instance belongs to
     *
     * @param locales - a {@link List} of {@link Locale} specifying the languages to which the returned instance should be translated
     * @return - a {@link RoundCI} instance describing the tournament round
     */
    RoundCI getTournamentRound(List<Locale> locales);

    /**
     * Returns a {@link SeasonCI} instance providing basic information about
     * the season to which the sport event associated with the current instance belongs to
     *
     * @param locales - a {@link List} of {@link Locale} specifying the languages to which the returned instance should be translated
     * @return -  {@link SeasonCI} instance providing basic information about the associated season
     */
    SeasonCI getSeason(List<Locale> locales);

    /**
     * Returns the {@link Fixture} instance containing information about the arranged sport event
     * <i>A Fixture is a sport event that has been arranged for a particular time and place</i>
     *
     * @param locales - a {@link List} of {@link Locale} specifying the languages to which the returned instance should be translated
     * @return - the {@link Fixture} instance containing information about the arranged sport event
     */
    Fixture getFixture(List<Locale> locales);

    /**
     * Returns a map of available team qualifiers
     *
     * @return a map of available team qualifiers
     */
    Map<URN, String> getCompetitorQualifiers();

    /**
     * Returns a {@link DelayedInfoCI} instance describing possible information about a delay
     *
     * @param locales the {@link Locale}s in which the data should be provided
     * @return a {@link DelayedInfoCI} instance describing information about a possible delay
     */
    DelayedInfoCI getDelayedInfo(List<Locale> locales);

    /**
     * Returns the associated event timeline
     * (the timeline is cached only after the event status indicates that the event has finished)
     *
     * @param locale the locale in which the timeline should be provided
     * @return the associated event timeline
     */
    EventTimelineCI getEventTimeline(Locale locale);
}
