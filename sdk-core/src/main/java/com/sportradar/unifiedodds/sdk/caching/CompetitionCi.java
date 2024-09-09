/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */

package com.sportradar.unifiedodds.sdk.caching;

import com.sportradar.unifiedodds.sdk.caching.ci.ReferenceIdCi;
import com.sportradar.unifiedodds.sdk.caching.ci.SportEventConditionsCi;
import com.sportradar.unifiedodds.sdk.caching.ci.VenueCi;
import com.sportradar.unifiedodds.sdk.entities.BookingStatus;
import com.sportradar.unifiedodds.sdk.entities.Competitor;
import com.sportradar.unifiedodds.sdk.entities.Reference;
import com.sportradar.unifiedodds.sdk.entities.SportEventType;
import com.sportradar.unifiedodds.sdk.impl.dto.SportEventStatusDto;
import com.sportradar.utils.Urn;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * Defines methods used to access competition type data
 */
@SuppressWarnings({ "LineLength" })
public interface CompetitionCi extends SportEventCi {
    /**
     * Returns a {@link BookingStatus} enum member providing booking status of the current instance
     *
     * @return a {@link BookingStatus} enum member providing booking status of the current instance
     */
    BookingStatus getBookingStatus();

    /**
     * Returns a {@link List} of competitor identifiers that participate in the sport event
     * associated with the current instance
     *
     * @param locales a {@link List} of {@link Locale} in which the competitor data should be provided
     * @return a {@link List} of competitor identifiers that participate in the sport event
     * associated with the current instance
     */
    List<Urn> getCompetitorIds(List<Locale> locales);

    /**
     * Returns a {@link VenueCi} instance representing a venue where the sport event associated with the
     * current instance will take place
     *
     * @param locales a {@link List} of {@link Locale} specifying the languages to which the returned instance should be translated
     * @return a {@link VenueCi} instance representing a venue where the associated sport event
     */
    VenueCi getVenue(List<Locale> locales);

    /**
     * Returns a {@link SportEventConditionsCi} instance representing live conditions of the sport event associated with the current instance
     *
     * @param locales a {@link List} of {@link Locale} specifying the languages to which the returned instance should be translated
     * @return a {@link SportEventConditionsCi} instance representing live conditions of the sport event associated with the current instance
     */
    SportEventConditionsCi getConditions(List<Locale> locales);

    /**
     * Fetch a {@link SportEventStatusDto} via event summary
     */
    void fetchSportEventStatus();

    /**
     * Method that gets triggered when the associated event gets booked trough the {@link com.sportradar.unifiedodds.sdk.BookingManager}
     */
    void onEventBooked();

    /**
     * Returns list of {@link Urn} of {@link Competitor} and associated {@link Reference} for this sport event
     * @return list of {@link Urn} of {@link Competitor} and associated {@link Reference} for this sport event
     */
    Map<Urn, ReferenceIdCi> getCompetitorsReferences();

    /**
     * Returns the liveOdds
     * @param locales the {@link Locale}s in which the data should be provided
     * @return the liveOdds
     */
    String getLiveOdds(List<Locale> locales);

    /**
     * Returns a {@link SportEventType} indicating the type of the associated event
     * @param locales the {@link Locale}s in which the data should be provided
     * @return a {@link SportEventType} indicating the type of the associated event
     */
    SportEventType getSportEventType(List<Locale> locales);
}
