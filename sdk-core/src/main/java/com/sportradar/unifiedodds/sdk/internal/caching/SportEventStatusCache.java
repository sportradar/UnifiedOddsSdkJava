/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */

package com.sportradar.unifiedodds.sdk.internal.caching;

import com.sportradar.unifiedodds.sdk.internal.impl.dto.SportEventStatusDto;
import com.sportradar.utils.Urn;

/**
 * Defines methods implemented by classes used to cache {@link SportEventStatusDto} instances
 */
@SuppressWarnings({ "LineLength" })
public interface SportEventStatusCache {
    /**
     * Returns the status of the event associated with the provided identifier. If the instance associated with
     * the specified event is not found, a {@link SportEventStatusCi} instance indicating a 'not started' event is returned.
     *
     * @param eventId the event identifier
     * @param makeApiCall should the API call be made if necessary
     * @return a {@link SportEventStatusCi} instance describing the last known event status
     */
    SportEventStatusCi getSportEventStatusCi(Urn eventId, boolean makeApiCall);

    /**
     * Purges the sport event status associated with the provided event id
     *
     * @param id the id of the event that you want to purge the sport event status
     */
    void purgeSportEventStatus(Urn id);

    /**
     * Adds the event identifier for timeline ignore
     * Used for BetPal events to have ignored timeline event status cache
     * @param eventId the event identifier
     * @param producerId the producer identifier
     * @param messageType type of the feed message
     */
    void addEventIdForTimelineIgnore(Urn eventId, int producerId, String messageType);
}
