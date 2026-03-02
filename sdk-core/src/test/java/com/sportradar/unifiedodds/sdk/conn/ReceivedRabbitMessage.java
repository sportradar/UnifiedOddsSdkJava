/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */
package com.sportradar.unifiedodds.sdk.conn;

import com.rabbitmq.client.AMQP;
import java.nio.charset.StandardCharsets;
import lombok.Getter;

@Getter
public class ReceivedRabbitMessage {

    private final String routingKey;
    private final byte[] messageContentBytes;
    private final String messageContent;
    private final AMQP.BasicProperties properties;
    private final long receivedAt;

    public ReceivedRabbitMessage(
        String routingKey,
        byte[] messageContentBytes,
        AMQP.BasicProperties properties,
        long receivedAt
    ) {
        this.routingKey = routingKey;
        this.messageContentBytes = messageContentBytes;
        this.messageContent = new String(messageContentBytes, StandardCharsets.UTF_8);
        this.properties = properties;
        this.receivedAt = receivedAt;
    }
}
