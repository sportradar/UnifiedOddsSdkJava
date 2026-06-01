/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */

package com.sportradar.unifiedodds.sdk.internal.impl.oddsentities;

import com.google.common.base.Preconditions;
import com.sportradar.unifiedodds.sdk.oddsentities.Message;
import com.sportradar.unifiedodds.sdk.oddsentities.MessageTimestamp;
import com.sportradar.unifiedodds.sdk.oddsentities.Producer;
import java.util.Map;

/**
 * Created on 22/06/2017.
 * // TODO @eti: Javadoc
 */
abstract class MessageImpl implements Message {

    private final Producer producer;
    private final MessageTimestamp timestamps;
    private final Map<String, String> messageHeaders;

    MessageImpl(Producer producer, MessageTimestamp timestamp, Map<String, String> messageHeaders) {
        Preconditions.checkNotNull(timestamp);
        Preconditions.checkNotNull(messageHeaders);

        this.producer = producer;
        this.timestamps = timestamp;
        this.messageHeaders = messageHeaders;
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

    /**
     * Gets the AMQP message headers delivered with the feed message
     * @return the AMQP headers as a string map, never null
     */
    @Override
    public Map<String, String> getMessageHeaders() {
        return messageHeaders;
    }
}
