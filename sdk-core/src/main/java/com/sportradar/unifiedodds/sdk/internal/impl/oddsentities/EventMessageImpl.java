/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */

package com.sportradar.unifiedodds.sdk.internal.impl.oddsentities;

import com.google.common.base.Preconditions;
import com.sportradar.unifiedodds.sdk.entities.SportEvent;
import com.sportradar.unifiedodds.sdk.internal.impl.TimeUtilsImpl;
import com.sportradar.unifiedodds.sdk.oddsentities.EventMessage;
import com.sportradar.unifiedodds.sdk.oddsentities.MessageTimestamp;
import com.sportradar.unifiedodds.sdk.oddsentities.Producer;

/**
 * Created on 22/06/2017.
 * // TODO @eti: Javadoc
 */
abstract class EventMessageImpl<T extends SportEvent> implements EventMessage<T> {

    private final Producer producer;
    private final MessageTimestamp timestamp;
    private final T sportEvent;
    private final byte[] rawMessage; // TODO unmodifiable collection
    private final Long requestId;

    EventMessageImpl(
        final T sportEvent,
        final byte[] rawMessage,
        Producer producer,
        final MessageTimestamp timestamp,
        Long requestId
    ) {
        Preconditions.checkNotNull(sportEvent, "sportEvent");
        Preconditions.checkNotNull(rawMessage, "rawMessage");
        Preconditions.checkNotNull(timestamp, "timestamp");
        this.producer = producer;
        this.timestamp =
            new MessageTimestampImpl(
                timestamp.getCreated(),
                timestamp.getSent(),
                timestamp.getReceived(),
                new TimeUtilsImpl().now()
            );
        this.sportEvent = sportEvent;
        this.rawMessage = rawMessage;
        this.requestId = requestId;
    }

    /**
     * Returns the competition/match/race/outright this odds update is for
     *
     * @return the competition/match this odds update is for
     */
    @Override
    public T getEvent() {
        return sportEvent;
    }

    /**
     * Returns the request id of the current message
     *
     * @return the request id of the current message
     */
    @Override
    public Long getRequestId() {
        return requestId;
    }

    /**
     * Returns the raw message as received from the producer
     *
     * @return the raw message as received from the producer
     */
    @Override
    public byte[] getRawMessage() {
        return rawMessage;
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
     * @return gets the timestamps when the message was generated, sent, received and dispatched by the sdk
     */
    @Override
    public MessageTimestamp getTimestamps() {
        return timestamp;
    }
}
