/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */
package com.sportradar.unifiedodds.sdk.conn;

import com.rabbitmq.client.AMQP;
import lombok.AllArgsConstructor;

@AllArgsConstructor(access = lombok.AccessLevel.PRIVATE)
public class ListenerCollectingRabbitMessages implements RabbitMqMessageListener {

    private final RabbitMessagesInMemoryStorage messageStorage;

    public static ListenerCollectingRabbitMessages to(RabbitMessagesInMemoryStorage messageStorage) {
        return new ListenerCollectingRabbitMessages(messageStorage);
    }

    @Override
    public void onMessageReceived(
        String routingKey,
        byte[] body,
        AMQP.BasicProperties properties,
        long receivedAt
    ) {
        messageStorage.append(new ReceivedRabbitMessage(routingKey, body, properties, receivedAt));
    }
}
