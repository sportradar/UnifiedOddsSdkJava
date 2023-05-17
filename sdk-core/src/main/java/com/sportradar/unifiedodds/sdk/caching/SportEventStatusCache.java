/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */

package com.sportradar.unifiedodds.sdk.caching;

import com.sportradar.unifiedodds.sdk.impl.dto.SportEventStatusDTO;
import com.sportradar.utils.URN;

/**
 * Defines methods implemented by classes used to cache {@link SportEventStatusDTO} instances
 */
@SuppressWarnings({ "AbbreviationAsWordInName", "LineLength" })
public interface SportEventStatusCache {
    /**
     * Returns the status of the event associated with the provided identifier. If the instance associated with
     * the specified event is not found, a {@link SportEventStatusCI} instance indicating a 'not started' event is returned.
     *
     * @param eventId the event identifier
     * @param makeApiCall should the API call be made if necessary
     * @return a {@link SportEventStatusCI} instance describing the last known event status
     */
    SportEventStatusCI getSportEventStatusCI(URN eventId, boolean makeApiCall);

    /**
     * Purges the sport event status associated with the provided event id
     *
     * @param id the id of the event that you want to purge the sport event status
     */
    void purgeSportEventStatus(URN id);

    /**
     * Adds the event identifier for timeline ignore
     * Used for BetPal events to have ignored timeline event status cache
     * @param eventId the event identifier
     * @param producerId the producer identifier
     * @param messageType type of the feed message
     */
    void addEventIdForTimelineIgnore(URN eventId, int producerId, String messageType);
}
