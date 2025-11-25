/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */
package com.sportradar.unifiedodds.sdk.conn;

import com.rabbitmq.client.AMQP;

public interface RabbitMqMessageListener {
    void onMessageReceived(String routingKey, byte[] body, AMQP.BasicProperties properties, long receivedAt);
}
