/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */

package com.sportradar.unifiedodds.sdk.impl.oddsentities;

import com.google.common.base.Preconditions;
import com.sportradar.unifiedodds.sdk.oddsentities.Message;
import com.sportradar.unifiedodds.sdk.oddsentities.MessageTimestamp;
import com.sportradar.unifiedodds.sdk.oddsentities.Producer;

/**
 * Created on 22/06/2017.
 * // TODO @eti: Javadoc
 */
abstract class MessageImpl implements Message {

    private final Producer producer;
    private final MessageTimestamp timestamps;

    MessageImpl(Producer producer, MessageTimestamp timestamp) {
        Preconditions.checkNotNull(timestamp);

        this.producer = producer;
        this.timestamps = timestamp;
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
        return timestamps;
    }
}
