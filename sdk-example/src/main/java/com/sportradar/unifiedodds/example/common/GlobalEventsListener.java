/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */

package com.sportradar.unifiedodds.example.common;

import com.sportradar.unifiedodds.sdk.UofGlobalEventsListener;
import com.sportradar.unifiedodds.sdk.oddsentities.ProducerStatus;
import com.sportradar.unifiedodds.sdk.oddsentities.RecoveryInitiated;
import com.sportradar.utils.Urn;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@SuppressWarnings({ "LineLength" })
public class GlobalEventsListener implements UofGlobalEventsListener {

    private final Logger logger = LoggerFactory.getLogger(this.getClass().getName());

    /**
     * Invoked when the producer status changes, some examples:
     * <p>
     * <ul>
     * <li>Producer up -> producer down</li>
     * <li>Producer down(processing queue delay) -> producer down(alive interval violation)</li>
     * <li>Producer down -> producer up</li>
     * <li>...</li>
     * </ul>
     * </p>
     *
     * @param producerStatus the new {@link ProducerStatus}
     * @since v2.0.8
     */
    @Override
    public void onProducerStatusChange(ProducerStatus producerStatus) {
        logger.warn(
            "Received a producer status change notification. Producer: {}, Reason: {}, isDown: {}, isDelayed: {}",
            producerStatus.getProducer().getId(),
            producerStatus.getProducerStatusReason(),
            producerStatus.isDown(),
            producerStatus.isDelayed()
        );
    }

    @Override
    public void onRecoveryInitiated(RecoveryInitiated recoveryInitiated) {}

    /**
     * Invoked when a connection to the feed is closed
     */
    @Override
    public void onConnectionDown() {
        logger.warn("Lost the connection to Betradar - all markets should be deactivated");
    }

    /**
     * Method invoked when a requested event recovery completes
     *
     * @param eventId the associated event identifier
     * @param requestId the identifier of the recovery request
     */
    @Override
    public void onEventRecoveryCompleted(Urn eventId, long requestId) {
        logger.info("Received onEventRecoveryCompleted for event[{}], requestId: {}", eventId, requestId);
    }

    /**
     * Invoked when an exception is thrown inside connection loop
     *
     * @param throwable that caused connection loop to fail
     */
    @Override
    public void onConnectionException(Throwable throwable) {
        logger.warn("Received onConnectionException", throwable);
    }
}
