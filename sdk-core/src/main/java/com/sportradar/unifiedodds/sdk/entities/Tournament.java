/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */

package com.sportradar.unifiedodds.sdk.entities;

import java.util.List;

/**
 * Defines methods implemented by classes providing information about a tournament
 */
public interface Tournament extends LongTermEvent {
    /**
     * Returns a {@link CategorySummary} representing the category associated with the current instance
     *
     * @return - a {@link CategorySummary} representing the category associated with the current instance
     */
    CategorySummary getCategory();

    /**
     * Returns a {@link CurrentSeasonInfo} which contains data about the season in which the current instance
     * tournament is happening
     *
     * @return - a {@link CurrentSeasonInfo} which provides data about the season in
     *           which the current instance tournament is happening
     */
    CurrentSeasonInfo getCurrentSeason();

    /**
     * Returns a list of associated tournament seasons
     *
     * @return a list of associated tournament seasons
     */
    List<Season> getSeasons();

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
    default List<Competition> getSchedule() {
        return null;
    }
}
