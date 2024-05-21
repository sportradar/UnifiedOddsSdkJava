/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */
package com.sportradar.unifiedodds.sdk.conn;

import com.rabbitmq.client.ConnectionFactory;
import com.sportradar.unifiedodds.sdk.impl.TimeUtils;
import com.sportradar.unifiedodds.sdk.shared.FeedMessageBuilder;
import com.sportradar.unifiedodds.sdk.testutil.rabbit.integration.Credentials;
import com.sportradar.unifiedodds.sdk.testutil.rabbit.integration.ExchangeLocation;
import com.sportradar.unifiedodds.sdk.testutil.rabbit.integration.RabbitMqProducer;
import java.io.IOException;
import java.util.concurrent.TimeoutException;
import java.util.function.Function;

public class MessagingSimulator implements AutoCloseable {

    private final RabbitMqProducer rabbitMqProducer;
    private final FeedMessageBuilder messages;
    private final RoutingKeys routingKeys;

    public MessagingSimulator(
        final ExchangeLocation exchangeLocation,
        final Credentials credentials,
        final ConnectionFactory connectionFactory,
        final TimeUtils time,
        final GlobalVariables globalVariables
    ) throws IOException, TimeoutException {
        rabbitMqProducer =
            RabbitMqProducer.connectDeclaringExchange(exchangeLocation, credentials, connectionFactory, time);
        messages = new FeedMessageBuilder(globalVariables);
        routingKeys = new RoutingKeys(globalVariables);
    }

    @Override
    public void close() throws IOException, TimeoutException {
        rabbitMqProducer.close();
    }

    public void send(
        Function<FeedMessageBuilder, String> chooseMessage,
        Function<RoutingKeys, String> chooseRoutingKey
    ) {
        rabbitMqProducer.send(chooseMessage.apply(messages), chooseRoutingKey.apply(routingKeys));
    }

    public static class Factory {

        private final ExchangeLocation exchangeLocation;
        private final Credentials credentials;
        private final ConnectionFactory connectionFactory;
        private final TimeUtils time;
        private final GlobalVariables globalVariables;

        public Factory(
            final ExchangeLocation exchangeLocation,
            final Credentials credentials,
            final ConnectionFactory connectionFactory,
            final TimeUtils time,
            GlobalVariables globalVariables
        ) {
            this.exchangeLocation = exchangeLocation;
            this.credentials = credentials;
            this.connectionFactory = connectionFactory;
            this.time = time;
            this.globalVariables = globalVariables;
        }

        public MessagingSimulator connectDeclaringExchange() throws IOException, TimeoutException {
            return new MessagingSimulator(
                exchangeLocation,
                credentials,
                connectionFactory,
                time,
                globalVariables
            );
        }
    }
}
