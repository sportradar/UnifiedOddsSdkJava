/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */

package com.sportradar.unifiedodds.sdk.internal.impl.oddsentities;

import com.sportradar.unifiedodds.sdk.entities.SportEvent;
import com.sportradar.unifiedodds.sdk.oddsentities.MessageTimestamp;
import com.sportradar.unifiedodds.sdk.oddsentities.Producer;
import com.sportradar.unifiedodds.sdk.oddsentities.UnparsableMessage;

/**
 * The {@link UnparsableMessage} basic impl
 */
class UnparsableMessageImpl<T extends SportEvent> extends MessageImpl implements UnparsableMessage<T> {

    private final T sportEvent;
    private final byte[] rawMessage;

    UnparsableMessageImpl(T sportEvent, byte[] rawMessage, Producer producer, MessageTimestamp timestamp) {
        super(producer, timestamp);
        this.sportEvent = sportEvent;
        this.rawMessage = rawMessage;
    }

    /**
     * Returns the competition/match/race/outright for which the un-parsable message is triggered.
     * The method will return null if the event data could not be extracted.
     *
     * @return the competition/match this odds update is for
     */
    @Override
    public T getEvent() {
        return sportEvent;
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
}
