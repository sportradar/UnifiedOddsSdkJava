/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */

package com.sportradar.unifiedodds.sdk.impl;

import com.rabbitmq.client.AMQP;

/**
 * Defines methods implemented by classes that can handle message payloads
 */
public interface ChannelMessageConsumer {
    /**
     * Opens the channel message consumer and prepares the required instances
     *
     * @param messageConsumer the parsed/prepared messages
     */
    void open(MessageConsumer messageConsumer);

    /**
     * Consumes the provided message payload
     *
     * @param routingKey - the source routing key of the payload
     * @param body - the message payload
     * @param properties - the BasicProperties associated to the message
     * @param receivedAt - the time when message was received (in milliseconds since EPOCH UTC)
     */
    void onMessageReceived(String routingKey, byte[] body, AMQP.BasicProperties properties, long receivedAt);

    String getConsumerDescription();
}
