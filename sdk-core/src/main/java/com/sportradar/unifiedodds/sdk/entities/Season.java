/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */

package com.sportradar.unifiedodds.sdk.entities;

import java.util.List;

/**
 * Defines methods implemented by classes providing data of a season
 */
public interface Season extends LongTermEvent {
    /**
     * Returns a {@link SeasonCoverage} instance containing information about the available
     * coverage for the associated season
     *
     * @return - a {@link SeasonCoverage} instance containing information about the available coverage
     */
    SeasonCoverage getSeasonCoverage();

    /**
     * Returns a {@link List} of groups associated with the associated season
     *
     * @return - a {@link List} of groups associated with the associated season
     */
    List<Group> getGroups();

    /**
     * Returns a {@link List} of events that belong to the associated season
     *
     * @return - a {@link List} of events that belong to the associated season
     */
    List<Competition> getSchedule();

    /**
     * Returns a {@link Round} instance specifying the current associated season
     *
     * @return - a {@link Round} instance specifying the current associated season
     */
    Round getCurrentRound();

    /**
     * Returns the {@link String} representation the year of the season
     *
     * @return - the {@link String} representation the year of the season
     */
    String getYear();

    /**
     * Returns a {@link TournamentInfo} which contains data of the associated tournament
     *
     * @return a {@link TournamentInfo} which contains data of the associated season
     */
    TournamentInfo getTournamentInfo();

    /**
     * Returns a {@link List} of competitors that participate in the sport event
     * associated with the current instance
     *
     * @return - a {@link List} of competitors that participate in the sport event
     * associated with the current instance
     */
    List<Competitor> getCompetitors();
}
