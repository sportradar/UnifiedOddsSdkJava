/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */

package com.sportradar.unifiedodds.sdk;

import com.sportradar.unifiedodds.sdk.oddsentities.Producer;
import java.util.Map;

/**
 * The {@link ProducerManager} is used to manage Sportradar message producers
 */
@SuppressWarnings({ "LineLength" })
public interface ProducerManager {
    /**
     * Returns a {@link Map} of all the available Sportradar producers
     *
     * @return -  {@link Map} of all the available Sportradar producers
     */
    Map<Integer, Producer> getAvailableProducers();

    /**
     * Returns a {@link Map} of activated producers for the provided access token
     *
     * @return - a {@link Map} of active producers for the provided access token
     */
    Map<Integer, Producer> getActiveProducers();

    /**
     * Returns the requested {@link Producer}
     *
     * @param id - the unique identifier of a {@link Producer}
     * @return - if the requested producer exists, a valid {@link Producer} instance; otherwise unknown-producer is generated
     */
    Producer getProducer(int id);

    /**
     * Enables te {@link Producer} associated with the provided id
     * (by default all the active producers are enabled)
     *
     * @param producerId - the identifier of the producer that you want to enable
     */
    void enableProducer(int producerId);

    /**
     * Disables te {@link Producer} associated with the provided id
     * (by default all the active producers are enabled)
     *
     * @param producerId - the identifier of the producer that you want to disable
     */
    void disableProducer(int producerId);

    /**
     * Sets the last message received timestamp. The value should be set to the timestamp of the last processed message,
     * which was received while the producer was not marked as down. The timestamp is later used to request the feed recovery.
     * The last valid max timestamp is 3 days ago (recovery limitation).
     * See: {@link Producer#getTimestampForRecovery()}
     *
     * @param producerId - the identifier of the producer to which the last known message timestamp belongs too
     * @param lastMessageTimestamp - the timestamp from which the SDK will request the recovery (in milliseconds)
     */
    void setProducerRecoveryFromTimestamp(int producerId, long lastMessageTimestamp);

    /**
     * An indication if the producer is enabled.
     *
     * @param producerId - the identifier of the producer for which to check
     * @return - <code>true</code> if the producer is enabled, otherwise <code>false</code>
     */
    boolean isProducerEnabled(int producerId);

    /**
     * An indication if the producer is currently marked as down by the SDK
     * @see com.sportradar.unifiedodds.sdk.oddsentities.ProducerDownReason
     *
     * @param producerId - the identifier of the producer for which to check
     * @return - <code>true</code> if the producer is down, otherwise <code>false</code>
     */
    boolean isProducerDown(int producerId);
}
