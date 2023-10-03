/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */

package com.sportradar.unifiedodds.sdk;

import com.sportradar.utils.Urn;

/**
 * Defines methods used as callbacks to notify the client about event recovery updates
 */
public interface SdkEventRecoveryStatusListener {
    /**
     * Method invoked when a requested event recovery completes
     *
     * @param eventId the associated event identifier
     * @param requestId the identifier of the recovery request
     */
    void onEventRecoveryCompleted(Urn eventId, long requestId);
}
