/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */
package com.sportradar.unifiedodds.sdk.testutil.rabbit.integration;

import com.rabbitmq.client.*;
import java.io.IOException;
import java.util.concurrent.TimeoutException;
import lombok.NonNull;
import lombok.val;
import org.apache.commons.lang3.exception.ExceptionUtils;

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
        factory.setHost(vhostLocation.getBaseUrl().getHost());
        factory.setPort(vhostLocation.getBaseUrl().getPort());
        factory.setUsername(credentials.getUsername());
        factory.setPassword(credentials.getPassword());
        factory.setExceptionHandler(getExceptionHandler());
        return factory.newConnection();
    }

    private static ExceptionHandler getExceptionHandler() {
        return new LoggingExceptionHandler();
    }

    private static class LoggingExceptionHandler implements ExceptionHandler {

        @Override
        public void handleUnexpectedConnectionDriverException(Connection connection, Throwable throwable) {
            log(throwable);
        }

        @Override
        public void handleReturnListenerException(Channel channel, Throwable throwable) {
            log(throwable);
        }

        private static void log(Throwable throwable) {
            System.out.println("logging exception: " + ExceptionUtils.getStackTrace(throwable));
        }

        @Override
        public void handleConfirmListenerException(Channel channel, Throwable throwable) {
            log(throwable);
        }

        @Override
        public void handleBlockedListenerException(Connection connection, Throwable throwable) {
            log(throwable);
        }

        @Override
        public void handleConsumerException(
            Channel channel,
            Throwable throwable,
            Consumer consumer,
            String s,
            String s1
        ) {
            log(throwable);
        }

        @Override
        public void handleConnectionRecoveryException(Connection connection, Throwable throwable) {
            log(throwable);
        }

        @Override
        public void handleChannelRecoveryException(Channel channel, Throwable throwable) {
            log(throwable);
        }

        @Override
        public void handleTopologyRecoveryException(
            Connection connection,
            Channel channel,
            TopologyRecoveryException e
        ) {
            log(e);
        }
    }
}
