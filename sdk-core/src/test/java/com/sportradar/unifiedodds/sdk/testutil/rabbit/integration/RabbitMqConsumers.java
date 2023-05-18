/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */
package com.sportradar.unifiedodds.sdk.testutil.rabbit.integration;

import com.rabbitmq.client.*;
import java.io.IOException;
import java.util.concurrent.TimeoutException;
import lombok.NonNull;
import lombok.val;

public class RabbitMqConsumers implements AutoCloseable {

    private final Connection connection;
    private final Channel channel;
    private ConnectionFactory factory;
    private final String exchangeName;

    private RabbitMqConsumers(
        @NonNull final ExchangeLocation exchangeLocation,
        @NonNull final Credentials credentials,
        @NonNull final ConnectionFactory connectionFactory
    ) throws IOException, TimeoutException {
        this.factory = connectionFactory;
        connection = createConnection(credentials, exchangeLocation.getVhostLocation());
        channel = connection.createChannel();
        exchangeName = exchangeLocation.getExchangeName();
    }

    public static RabbitMqConsumers connectToExchange(
        final ExchangeLocation exchangeLocation,
        final Credentials credentials,
        final ConnectionFactory connectionFactory
    ) throws IOException, TimeoutException {
        return new RabbitMqConsumers(exchangeLocation, credentials, connectionFactory);
    }

    @Override
    public void close() throws IOException, TimeoutException {
        channel.close();
        connection.close();
    }

    public void registerConsumer(final String routingKey, final Consumer consumer) throws Exception {
        val queueName = channel.queueDeclare().getQueue();
        channel.queueBind(queueName, exchangeName, routingKey);
        channel.basicConsume(queueName, true, consumer);
    }

    private Connection createConnection(Credentials credentials, VhostLocation vhostLocation)
        throws IOException, TimeoutException {
        factory.setVirtualHost(vhostLocation.getVirtualHostname());
        factory.setHost(vhostLocation.getHost());
        factory.setUsername(credentials.getUsername());
        factory.setPassword(credentials.getPassword());
        return factory.newConnection();
    }
}
