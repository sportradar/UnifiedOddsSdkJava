/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */

package com.sportradar.unifiedodds.example.player;

import com.google.common.base.Preconditions;
import com.google.common.base.Strings;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

/**
 * Created on 08/01/2018.
 * // TODO @eti: Javadoc
 */
class MessagePublisher {
    private static final String EXCHANGE = "unifiedfeed";

    private final ConnectionFactory connectionFactory;
    private final String host;
    private final String password;

    private boolean isOpen = false;

    private Connection connection;
    private Channel channel;

    MessagePublisher(String host, String password, int bookmakerId) {
        Preconditions.checkNotNull(host);

        this.connectionFactory = new ConnectionFactory();
        this.connectionFactory.setVirtualHost("/unifiedfeed/" + bookmakerId);

        this.host = host;
        this.password = password;
    }

    public void init() {
        if (isOpen) {
            return;
        }

        connectionFactory.setHost(host);

        if (!Strings.isNullOrEmpty(password)) {
            connectionFactory.setPassword(password);
        }

        try {
            connection = connectionFactory.newConnection();
            channel = connection.createChannel();
            channel.exchangeDeclare(EXCHANGE, "topic");
        } catch (IOException | TimeoutException e) {
            throw new IllegalStateException("Failed to initialize the message publisher", e);
        }

        isOpen = true;
    }

    public void destroy() {
        if (!isOpen) {
            return;
        }

        if (connection != null) {
            try {
                connection.close();
            } catch (IOException e) {
                e.printStackTrace();
                throw new IllegalStateException("Connection closure failed", e);
            }
        }
    }

    public void publishMessage(String routingKey, byte[] body) {
        Preconditions.checkNotNull(routingKey);
        Preconditions.checkNotNull(body);

        if (!isOpen) {
            throw new IllegalStateException("MessagePublisher accessed, but its not opened");
        }

        try {
            channel.basicPublish(EXCHANGE, routingKey, null, body);
        } catch (IOException e) {
            throw new IllegalStateException("Message publishing failed, routing key: " + routingKey, e);
        }
    }
}
