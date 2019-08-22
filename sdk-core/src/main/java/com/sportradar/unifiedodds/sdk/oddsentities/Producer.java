/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */

package com.sportradar.unifiedodds.sdk.oddsentities;

import com.sportradar.unifiedodds.sdk.ProducerScope;

import java.util.Set;

/**
 * Represents a Sportradar message producer(Live Odds, Betradar Ctrl, Premium Cricket,...)
 */
public interface Producer {
    /**
     * Returns the unique producer identifier
     *
     * @return the unique producer identifier
     */
    int getId();

    /**
     * Returns the name of the producer
     *
     * @return the name of the producer
     */
    String getName();

    /**
     * Returns a short description of the producer
     *
     * @return a short description of the producer
     */
    String getDescription();

    /**
     * Returns a timestamp indicating when was the last message received from the associated producer
     *
     * @return a timestamp indicating when was the last message received
     */
    long getLastMessageTimestamp();

    /**
     * An indication if the producer is available with the token provided with the {@link com.sportradar.unifiedodds.sdk.cfg.OddsFeedConfiguration}
     *
     * @return <code>true</code> if the producer is available, otherwise <code>false</code>
     */
    boolean isAvailable();

    /**
     * An indication if the producer is enabled.
     *
     * The producer gets by default enabled based on the {@link #isAvailable()},
     * but it can be disabled trough the {@link com.sportradar.unifiedodds.sdk.ProducerManager}
     *
     * @return <code>true</code> if the producer is enabled, otherwise <code>false</code>
     */
    boolean isEnabled();

    /**
     * An indication if the producer is down/out of sync/invalid state
     *
     * @return <code>false</code> if the producer is up, otherwise <code>true</code>
     */
    boolean isFlaggedDown();

    /**
     * Returns the producer API url.
     *
     * This url may be used to perform additional requests that are specific per producer,
     * as an example, the SDK uses this url to perform individual recovery requests
     *
     * @return the producer API url
     */
    String getApiUrl();

    /**
     * Returns a {@link Set} of possible {@link ProducerScope}s.
     *
     * {@link ProducerScope}s indicate what type of event messages will be dispatched by the producer.
     *
     * @return a {@link Set} of possible {@link ProducerScope}s
     */
    Set<ProducerScope> getProducerScopes();

    /**
     * Returns the last processed message generation timestamp
     *
     * @return the last processed message generation timestamp
     */
    long getLastProcessedMessageGenTimestamp();

    /**
     * Returns the messaging queue processing delay in milliseconds (current time - message generation timestamp difference)
     *
     * @return the messaging queue processing delay in milliseconds (current time - message generation timestamp difference)
     */
    long getProcessingQueDelay();

    /**
     * Returns a timestamp which indicates the last known verified time in which the SDK was in sync with the feed.
     * The returned timestamp should be used to initiate the initial SDK recovery after a restart, this can be done trough
     * the {@link com.sportradar.unifiedodds.sdk.ProducerManager#setProducerRecoveryFromTimestamp(int, long)} method.
     *
     * @return the last timestamp in which the SDK was in sync with the feed
     */
    long getTimestampForRecovery();

    /**
     * Returns the max allowed stateful recovery window in minutes
     *
     * @return the max allowed stateful recovery window in minutes
     */
    int getStatefulRecoveryWindowInMinutes();

    /**
     * Gets the recovery info about last recovery attempt
     * @return the recovery info about last recovery attempt
     */
    default RecoveryInfo getRecoveryInfo(){
        throw new UnsupportedOperationException("Method not implemented. Use derived type.");
    }
}
