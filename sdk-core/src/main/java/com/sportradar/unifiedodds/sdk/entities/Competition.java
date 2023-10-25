/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */

package com.sportradar.unifiedodds.sdk.entities;

import com.sportradar.unifiedodds.sdk.entities.status.CompetitionStatus;
import java.util.List;
import java.util.Locale;
import java.util.Optional;

/**
 * Defines methods implemented by classes representing sport events regardless to which sport they belong
 */
public interface Competition extends SportEvent {
    /**
     * Returns a {@link CompetitionStatus} containing information about the progress of the sport event
     * associated with the current instance
     *
     * @return - a {@link CompetitionStatus} containing information about the progress of the sport event
     * associated with the current instance
     */
    CompetitionStatus getStatus();

    /**
     * Returns a {@link CompetitionStatus} containing information about the progress of the sport event
     * associated with the current instance if already cached (does not make API call)
     *
     * @return - a {@link CompetitionStatus} containing information about the progress of the sport event
     * associated with the current instance if already cached (does not make API call)
     */
    default Optional<CompetitionStatus> getStatusIfPresent() {
        throw new UnsupportedOperationException("Method not implemented. Use derived type.");
    }

    /**
     * Returns a {@link BookingStatus} enum member providing booking status of the current instance
     *
     * @return - a {@link BookingStatus} enum member providing booking status of the current instance
     */
    BookingStatus getBookingStatus();

    /**
     * Returns the venue where the sport event associated with the current instance will take place
     *
     * @return - the {@link Venue} where the sport event associated with the current instance will take place
     */
    Venue getVenue();

    /**
     * Returns a {@link SportEventConditions} representing live conditions of the sport event associated
     * with the current instance
     *
     * @return - the {@link SportEventConditions} representing live conditions of the sport event associated
     * with the current instance
     */
    SportEventConditions getConditions();

    /**
     * Returns a {@link List} of competitors that participate in the sport event
     * associated with the current instance
     *
     * @return - a {@link List} of competitors that participate in the sport event
     * associated with the current instance
     */
    List<Competitor> getCompetitors();

    /**
     * Get the event status
     * @return the event status
     */
    default EventStatus getEventStatus() {
        throw new UnsupportedOperationException("Method not implemented. Use derived type.");
    }

    /**
     * Returns the liveOdds
     * @return the liveOdds
     */
    default String getLiveOdds() {
        return null;
    }

    /**
     * Returns a {@link SportEventType} indicating the type of the associated event
     * @return a {@link SportEventType} indicating the type of the associated event
     */
    default SportEventType getSportEventType() {
        return null;
    }
}
