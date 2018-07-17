/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */

package com.sportradar.unifiedodds.sdk.caching;

import com.sportradar.unifiedodds.sdk.impl.dto.SportEventStatusDTO;
import com.sportradar.utils.URN;

/**
 * Defines methods implemented by classes used to cache {@link SportEventStatusDTO} instances
 */
public interface SportEventStatusCache {
    /**
     * Returns the status of the event associated with the provided identifier. If the instance associated with
     * the specified event is not found, a {@link SportEventStatusDTO} instance indicating a 'not started' event is returned.
     *
     * @param eventId the event identifier
     * @return a {@link SportEventStatusDTO} instance describing the last known event status
     */
    SportEventStatusDTO getSportEventStatusDTO(URN eventId);

    /**
     * Adds a new cache entry
     *
     * @param id - the unique identifier of the sport event to which the status belongs to
     * @param status - a {@link SportEventStatusDTO} to store in the cache
     */
    void addSportEventStatus(URN id, SportEventStatusDTO status);

    /**
     * Purges the sport event status associated with the provided event id
     *
     * @param id the id of the event that you want to purge the sport event status
     */
    void purgeSportEventStatus(URN id);
}
