/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */

package com.sportradar.unifiedodds.sdk.entities;

import com.sportradar.utils.URN;

import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * Defines methods implemented by classes representing a tournament season
 */
public interface CurrentSeasonInfo {
    /**
     * Returns the {@link URN} uniquely identifying the current season
     *
     * @return - the {@link URN} uniquely identifying the current season
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
     * Returns the {@link String} representation the year of the season
     *
     * @return - the {@link String} representation the year of the season
     */
    String getYear();

    /**
     * Returns the {@link Date} specifying the start date of the season
     *
     * @return - the {@link Date} specifying the start date of the season
     */
    Date getStartDate();

    /**
     * Returns the {@link Date} specifying the end date of the season
     *
     * @return - the {@link Date} specifying the end date of the season
     */
    Date getEndDate();

    /**
     * Returns a {@link SeasonCoverage} instance containing information about the available
     * coverage for the associated season
     *
     * @return - a {@link SeasonCoverage} instance containing information about the available coverage
     */
    SeasonCoverage getCoverage();

    /**
     * Returns a {@link List} of groups associated with the current season
     *
     * @return - a {@link List} of groups associated with the current season
     */
    List<Group> getGroups();

    /**
     * Returns a {@link Round} instance specifying the current season round
     *
     * @return - a {@link Round} instance specifying the current season round
     */
    Round getCurrentRound();

    /**
     * Returns a {@link List} of competitors that participate in the sport event
     * associated with the current instance
     *
     * @return - a {@link List} of competitors that participate in the sport event
     * associated with the current instance
     */
    List<Competitor> getCompetitors();

    /**
     * Returns a {@link List} of events that belong to the associated season
     *
     * @return - a {@link List} of events that belong to the associated season
     */
    List<Competition> getSchedule();
}
