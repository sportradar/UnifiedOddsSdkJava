/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */

package com.sportradar.unifiedodds.sdk.entities;

import com.sportradar.unifiedodds.sdk.entities.status.MatchStatus;
import java.util.Locale;
import java.util.Optional;

/**
 * Defines methods implemented by classes representing sport events of match type
 */
public interface Match extends Competition {
    /**
     * Returns a {@link MatchStatus} containing information about the progress of the match
     * associated with the current instance
     *
     * @return - a {@link MatchStatus} containing information about the progress of the match
     * associated with the current instance
     */
    MatchStatus getStatus();

    /**
     * Returns a {@link SeasonInfo} instance providing basic information about
     * the season to which the sport event associated with the current instance belongs to
     *
     * @return - a {@link SeasonInfo} instance providing basic information about
     * the season
     */
    SeasonInfo getSeason();

    /**
     * Returns a {@link Round} instance describing the tournament round to which the
     * sport event associated with current instance belongs to
     *
     * @return - a {@link Round} instance describing the tournament round
     */
    Round getTournamentRound();

    /**
     * Returns a {@link TeamCompetitor} instance describing the home competitor
     *
     * @return - a {@link TeamCompetitor} instance describing the home competitor
     */
    TeamCompetitor getHomeCompetitor();

    /**
     * Returns a {@link TeamCompetitor} instance describing the away competitor
     *
     * @return - a {@link TeamCompetitor} instance describing the away competitor
     */
    TeamCompetitor getAwayCompetitor();

    /**
     * Returns the tournament associated with the current instance
     * (possible types can be {@link BasicTournament} and {@link Tournament})
     *
     * @return - the tournament associated with the current instance
     */
    LongTermEvent getTournament();

    /**
     * Returns the {@link Fixture} instance containing information about the arranged sport event
     * <i>A Fixture is a sport event that has been arranged for a particular time and place</i>
     *
     * @return - the {@link Fixture} instance containing information about the arranged sport event
     */
    Fixture getFixture();

    /**
     * Returns the associated {@link EventTimeline}
     * (NOTICE: the timeline is cached only after the event status indicates that the event has finished)
     *
     * @param locale the locale in which the timeline should be provided
     * @return the associated event timeline
     */
    EventTimeline getEventTimeline(Locale locale);

    /**
     * Returns the associated {@link EventTimeline} if already cached (does not make API call)
     * (NOTICE: the timeline is cached only after the event status indicates that the event has finished)
     *
     * @param locale the locale in which the timeline should be provided
     * @return - a associated {@link EventTimeline} if already cached (does not make API call)
     */
    default Optional<EventTimeline> getEventTimelineIfPresent(Locale locale) {
        throw new UnsupportedOperationException("Method not implemented. Use derived type.");
    }

    /**
     * Returns a {@link DelayedInfo} instance describing possible information about a delay
     *
     * @return a {@link DelayedInfo} instance describing information about a possible delay
     */
    DelayedInfo getDelayedInfo();

    /**
     * Returns a {@link CoverageInfo} instance
     *
     * @return a {@link CoverageInfo} instance
     */
    default CoverageInfo getCoverageInfo() {
        return null;
    }
}
