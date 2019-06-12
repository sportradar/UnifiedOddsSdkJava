/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */

package com.sportradar.unifiedodds.sdk.impl;

import com.sportradar.unifiedodds.sdk.MessageInterest;
import com.sportradar.unifiedodds.sdk.oddsentities.MessageTimestamp;
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
     * @param timestamp - all message timestamps
     */
    void onMessageReceived(UnmarshalledMessage unmarshalledMessage, byte[] body, RoutingKeyInfo routingKeyInfo, MessageTimestamp timestamp);

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

    /**
     * Occurs when any feed message arrives
     *
     * @param routingKey the routing key associated with this message
     * @param feedMessage the message received
     * @param timestamp the message timestamps
     * @param messageInterest the associated {@link MessageInterest}
     */
    void onRawFeedMessageReceived(RoutingKeyInfo routingKey, UnmarshalledMessage feedMessage, MessageTimestamp timestamp, MessageInterest messageInterest);
}
