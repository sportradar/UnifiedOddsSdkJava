/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */

package com.sportradar.unifiedodds.sdk.entities;

import java.util.List;

/**
 * Defines methods implemented by classes providing information about a tournament
 */
public interface BasicTournament extends LongTermEvent {
    /**
     * Returns a {@link CategorySummary} representing the category associated with the current instance
     *
     * @return - a {@link CategorySummary} representing the category associated with the current instance
     */
    CategorySummary getCategory();

    /**
     * Returns a {@link List} of competitors that participate in the sport event
     * associated with the current instance
     *
     * @return - a {@link List} of competitors that participate in the sport event
     * associated with the current instance
     */
    List<Competitor> getCompetitors();

    /**
     * Returns the {@link Boolean} specifying if the tournament is exhibition game
     *
     * @return if available, the {@link Boolean} specifying if the tournament is exhibition game
     */
    default Boolean isExhibitionGames() {
        return null;
    }

    /**
     * Returns a {@link List} of events that belong to the associated tournament
     *
     * @return - a {@link List} of events that belong to the associated tournament
     */
    default List<Competition> getSchedule()
    {
        return null;
    }
}
