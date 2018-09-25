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
     * @param makeApiCall should the API call be made if necessary
     * @return a {@link SportEventStatusDTO} instance describing the last known event status
     */
    SportEventStatusDTO getSportEventStatusDTO(URN eventId, boolean makeApiCall);

    /**
     * Purges the sport event status associated with the provided event id
     *
     * @param id the id of the event that you want to purge the sport event status
     */
    void purgeSportEventStatus(URN id);
}
