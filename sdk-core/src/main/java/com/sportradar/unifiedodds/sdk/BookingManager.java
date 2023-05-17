/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */

package com.sportradar.unifiedodds.sdk;

import com.sportradar.unifiedodds.sdk.exceptions.internal.CommunicationException;
import com.sportradar.utils.URN;

/**
 * Defines methods used to perform various booking calendar operations
 */
public interface BookingManager {
    /**
     * Performs a request on the API which books the event associated with the provided {@link URN} identifier
     *
     * @param eventId the {@link URN} identifier of the event which needs to be booked
     * @return <code>true</code> if the booking was successful; otherwise <code>false</code>
     */
    boolean bookLiveOddsEvent(URN eventId) throws CommunicationException;
}
