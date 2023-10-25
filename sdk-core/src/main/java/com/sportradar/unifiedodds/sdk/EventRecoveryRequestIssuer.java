/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */

package com.sportradar.unifiedodds.sdk;

import com.sportradar.unifiedodds.sdk.oddsentities.Producer;
import com.sportradar.utils.Urn;

/**
 * Defines utility methods used to handle specific event recovery requests
 */
@SuppressWarnings({ "LineLength" })
public interface EventRecoveryRequestIssuer {
    /**
     * Initiates a odds recovery procedure for the event associated with the provided {@link Urn} identifier on the requested {@link Producer}
     * (The odds recovery procedure re-sends all odds for all markets for a sport event)
     *
     * Note: The event must be either {@link com.sportradar.unifiedodds.sdk.entities.EventStatus#NotStarted} or {@link com.sportradar.unifiedodds.sdk.entities.EventStatus#Live}
     *
     * @param producer the producer on which the recovery should be initialized
     * @param eventId the {@link Urn} identifier of the event for which the recovery is needed
     * @return the identifier of the recovery request, if the request executed successfully; otherwise null
     */
    Long initiateEventOddsMessagesRecovery(Producer producer, Urn eventId);

    /**
     * Initiates a stateful recovery procedure for the event associated with the provided {@link Urn} identifier on the requested {@link Producer}
     * (The stateful message recovery procedure re-sends all stateful-messages (BetSettlement, RollbackBetSettlement, BetCancel, UndoBetCancel) for a sport event)
     *
     * Note: The event can be up to 30 days in the past
     *
     * @param producer the producer on which the recovery should be initialized
     * @param eventId the {@link Urn} identifier of the event for which the recovery is needed
     * @return the identifier of the recovery request, if the request executed successfully; otherwise null
     */
    Long initiateEventStatefulMessagesRecovery(Producer producer, Urn eventId);
}
