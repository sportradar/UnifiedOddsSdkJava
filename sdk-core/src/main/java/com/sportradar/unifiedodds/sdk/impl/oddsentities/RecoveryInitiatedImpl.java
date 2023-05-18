/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */

package com.sportradar.unifiedodds.sdk.impl.oddsentities;

import com.google.common.base.Preconditions;
import com.sportradar.unifiedodds.sdk.oddsentities.*;
import com.sportradar.utils.URN;

/**
 * A basic implementation of the {@link RecoveryInitiated}
 */
class RecoveryInitiatedImpl implements RecoveryInitiated {

    private final Producer producer;
    private final long requestId;
    private final Long after;
    private final URN eventId;
    private final String message;
    private final long timestamp;

    RecoveryInitiatedImpl(
        Producer producer,
        long requestId,
        Long after,
        URN eventId,
        String message,
        long timestamp
    ) {
        Preconditions.checkNotNull(producer);
        Preconditions.checkState(requestId > 0);

        this.producer = producer;
        this.requestId = requestId;
        this.after = after;
        this.eventId = eventId;
        this.message = message;
        this.timestamp = timestamp;
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
     * @return when was this message created in milliseconds since EPOCH UTC
     */
    @Override
    public long getTimestamp() {
        return timestamp;
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

    /**
     * Returns the identifier of the recovery request
     *
     * @return the identifier of the recovery request
     */
    @Override
    public long getRequestId() {
        return requestId;
    }

    /**
     * Returns the after timestamp if applied
     *
     * @return the after timestamp if applied
     */
    @Override
    public Long getAfterTimestamp() {
        return after;
    }

    /**
     * Gets the associated event identifier
     *
     * @return the associated event identifier
     */
    @Override
    public URN getEventId() {
        return eventId;
    }

    /**
     * Returns the message associated with the recovery request
     *
     * @return the message associated with the recovery request
     */
    @Override
    public String getMessage() {
        return message;
    }
}
