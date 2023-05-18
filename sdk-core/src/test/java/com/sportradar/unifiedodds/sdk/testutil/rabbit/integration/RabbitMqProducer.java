/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */
package com.sportradar.unifiedodds.sdk.testutil.rabbit.integration;

import static com.google.common.collect.ImmutableMap.of;

import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.sportradar.unifiedodds.sdk.impl.TimeUtils;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.TimeoutException;
import lombok.NonNull;
import lombok.val;

public class RabbitMqProducer implements AutoCloseable {

    private final Connection connection;
    private final Channel channel;
    private final String exchangeName;
    private ConnectionFactory factory;
    private TimeUtils time;

    private RabbitMqProducer(
        @NonNull final ExchangeLocation exchangeLocation,
        @NonNull final Credentials credentials,
        @NonNull final ConnectionFactory connectionFactory,
        @NonNull final TimeUtils time
    ) throws IOException, TimeoutException {
        this.factory = connectionFactory;
        this.time = time;
        connection = createConnection(credentials, exchangeLocation.getVhostLocation());
        channel = connection.createChannel();
        exchangeName = exchangeLocation.getExchangeName();
        createExchange();
    }

    public static RabbitMqProducer connectDeclaringExchange(
        final ExchangeLocation exchangeLocation,
        final Credentials credentials,
        final ConnectionFactory connectionFactory,
        final TimeUtils time
    ) throws IOException, TimeoutException {
        return new RabbitMqProducer(exchangeLocation, credentials, connectionFactory, time);
    }

    @Override
    public void close() throws IOException, TimeoutException {
        channel.close();
        connection.close();
    }

    public void send(String message, String routingKey) throws IOException {
        byte[] body = message.getBytes(StandardCharsets.UTF_8);
        val properties = asProperties(time.now());
        channel.basicPublish(exchangeName, routingKey, properties, body);
    }

    private static AMQP.BasicProperties asProperties(long timestamp) {
        return new AMQP.BasicProperties.Builder().headers(of("timestamp_in_ms", timestamp)).build();
    }

    private void createExchange() throws IOException {
        channel.exchangeDeclare(exchangeName, "topic");
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
