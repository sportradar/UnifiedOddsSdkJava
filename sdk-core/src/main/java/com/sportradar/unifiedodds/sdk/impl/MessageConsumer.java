/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */

package com.sportradar.unifiedodds.sdk.impl;

import com.sportradar.unifiedodds.sdk.MessageInterest;
import com.sportradar.unifiedodds.sdk.oddsentities.UnmarshalledMessage;
import com.sportradar.utils.URN;

/**
 * Defines methods implemented by classes that can consume messages
 */
public interface MessageConsumer {
    /**
     * Consumes the provided message
     *
     * @param unmarshalledMessage - an unmarshalled message payload
     * @param body - the raw payload (mainly used for logging and user exposure)
     * @param routingKeyInfo - a {@link RoutingKeyInfo} instance describing the message routing key
     */
    void onMessageReceived(UnmarshalledMessage unmarshalledMessage, byte[] body, RoutingKeyInfo routingKeyInfo);

    /**
     * Dispatches the "unparsable message received event"
     *
     * @param rawMessage - the raw message payload
     * @param eventId - if available the related sport event id; otherwise null
     */
    void onMessageDeserializationFailed(byte[] rawMessage, URN eventId);

    /**
     * Returns a {@link String} which describes the consumer
     *
     * @return - a {@link String} which describes the consumer
     */
    String getConsumerDescription();

    /**
     * Returns the consumer {@link MessageInterest}
     * (method most useful in custom consumer implementations)
     *
     * @return the consumer {@link MessageInterest}
     */
    MessageInterest getMessageInterest();
}
