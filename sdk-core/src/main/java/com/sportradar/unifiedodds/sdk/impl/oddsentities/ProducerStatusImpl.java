/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */

package com.sportradar.unifiedodds.sdk.impl.oddsentities;

import com.google.common.base.Preconditions;
import com.sportradar.unifiedodds.sdk.oddsentities.MessageTimestamp;
import com.sportradar.unifiedodds.sdk.oddsentities.Producer;
import com.sportradar.unifiedodds.sdk.oddsentities.ProducerStatus;
import com.sportradar.unifiedodds.sdk.oddsentities.ProducerStatusReason;

/**
 * A basic implementation of the {@link ProducerStatus}
 */
class ProducerStatusImpl implements ProducerStatus {

    private final Producer producer;
    private final ProducerStatusReason reason;
    private final boolean isDown;
    private final boolean isDelayed;
    private final long timestamp;

    ProducerStatusImpl(
        Producer producer,
        ProducerStatusReason reason,
        boolean isDown,
        boolean isDelayed,
        long timestamp
    ) {
        Preconditions.checkNotNull(producer);
        Preconditions.checkNotNull(reason);

        this.producer = producer;
        this.reason = reason;
        this.isDown = isDown;
        this.isDelayed = isDelayed;
        this.timestamp = timestamp;
    }

    /**
     * An indication if the associated {@link Producer} is down
     *
     * @return <code>true</code> if the {@link Producer} is down, otherwise <code>false</code>
     */
    @Override
    public boolean isDown() {
        return isDown;
    }

    /**
     * An indication if the associated {@link Producer} is delayed(processing queue is building up)
     *
     * @return <code>true</code> if the {@link Producer} is delayed, otherwise <code>false</code>
     */
    @Override
    public boolean isDelayed() {
        return isDelayed;
    }

    /**
     * Returns the reason of the {@link Producer} status change
     *
     * @return the reason of the status change
     */
    @Override
    public ProducerStatusReason getProducerStatusReason() {
        return reason;
    }

    /**
     * Returns the {@link Producer} that generated this message
     *
     * @return the {@link Producer} that generated this message
     */
    @Override
    public Producer getProducer() {
        return producer;
    }

    /**
     * Gets the timestamps when the message was generated, sent, received and dispatched by the sdk
     *
     * @return gets the timestamps when the message was generated, sent, received and dispatched by the sdk
     */
    @Override
    public MessageTimestamp getTimestamps() {
        return new MessageTimestampImpl(timestamp);
    }
}
