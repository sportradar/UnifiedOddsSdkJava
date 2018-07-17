/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */

package com.sportradar.unifiedodds.sdk.impl;

/**
 * Defines methods implemented by classes that can handle message payloads
 */
public interface ChannelMessageConsumer {

    /**
     * Opens the channel message consumer and prepares the required instances
     *
     * @param messageConsumer the parsed/prepared messages
     */
    default void open(MessageConsumer messageConsumer) {
        // NO-OP mode, this is an optional method that should be implemented only if the implementation requires it
    }

    /**
     * Consumes the provided message payload
     *
     * @param routingKey - the source routing key of the payload
     * @param body - the message payload
     */
    void onMessageReceived(String routingKey, byte[] body);
}
