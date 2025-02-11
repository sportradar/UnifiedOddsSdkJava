/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */

package com.sportradar.unifiedodds.sdk.internal.caching;

import com.sportradar.unifiedodds.sdk.internal.exceptions.CacheItemNotFoundException;
import com.sportradar.unifiedodds.sdk.internal.exceptions.IllegalCacheStateException;
import com.sportradar.unifiedodds.sdk.managers.BookingManager;
import com.sportradar.utils.Urn;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * Created on 20/10/2017.
 * // TODO @eti: Javadoc
 */
@SuppressWarnings({ "LineLength" })
public interface SportEventCache {
    /**
     * Returns a {@link SportEventCi} instance representing a cached sport event data
     *
     * @param id an {@link Urn} specifying the id of the sport event
     * @return a {@link SportEventCi} instance representing cached sport event data
     */
    SportEventCi getEventCacheItem(Urn id) throws CacheItemNotFoundException;

    /**
     * Returns a {@link List} containing id's of sport events, which belong to a specific tournament
     *
     * @param tournamentId an {@link Urn} specifying the id of the tournament to which the events should relate
     * @param locale the locale to fetch the data
     * @return a {@link List} containing id's of sport events, which belong to the specified tournament
     */
    List<Urn> getEventIds(Urn tournamentId, Locale locale) throws IllegalCacheStateException;

    /**
     * Returns a {@link List} containing id's of sport events, which are scheduled for a specific date - if provided;
     * otherwise a {@link List} of currently live events is returned
     *
     * @param date an optional {@link Date} for which the data is provided
     * @param locale the locale to fetch the data
     * @return a {@link List} of events that are happening on the specified {@link Date};
     *           or a {@link List} of currently live events
     */
    List<Urn> getEventIds(Date date, Locale locale) throws IllegalCacheStateException;

    /**
     * Purges an item from the {@link SportEventCache}
     *
     * @param id The {@link Urn} specifying the event which should be purged
     */
    void purgeCacheItem(Urn id);

    /**
     * Method that gets triggered when the associated event gets booked trough the {@link BookingManager}
     *
     * @param id the {@link Urn} of the event that was successfully booked
     */
    void onEventBooked(Urn id);

    /**
     * Adds fixture timestamp to cache so that the next fixture calls for the event goes through non-cached fixture provider
     *
     * @param id the {@link Urn} of the event
     */
    void addFixtureTimestamp(Urn id);

    /**
     * Deletes the sport events from cache which are scheduled before specified date
     * @param before the scheduled Date used to delete sport events from cache
     * @return number of deleted items
     */
    Integer deleteSportEventsFromCache(Date before);
}
