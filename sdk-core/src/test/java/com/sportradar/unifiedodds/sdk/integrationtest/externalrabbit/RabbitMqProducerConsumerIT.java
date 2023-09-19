/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */
package com.sportradar.unifiedodds.sdk.integrationtest.externalrabbit;

import static com.google.common.collect.Iterables.getOnlyElement;
import static com.sportradar.unifiedodds.sdk.testutil.rabbit.integration.RabbitMqClientFactory.createRabbitMqClient;
import static com.sportradar.unifiedodds.sdk.testutil.rabbit.integration.RabbitMqConsumers.connectToExchange;
import static com.sportradar.unifiedodds.sdk.testutil.rabbit.integration.RabbitMqProducer.connectDeclaringExchange;
import static com.sportradar.unifiedodds.sdk.testutil.rabbit.libraryfixtures.WaitingRabbitMqConsumerDi.createWaitingRabbitMqConsumerFactory;
import static java.nio.charset.StandardCharsets.UTF_8;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.http.client.Client;
import com.sportradar.unifiedodds.sdk.impl.Constants;
import com.sportradar.unifiedodds.sdk.impl.TimeUtils;
import com.sportradar.unifiedodds.sdk.impl.TimeUtilsImpl;
import com.sportradar.unifiedodds.sdk.testutil.rabbit.integration.*;
import com.sportradar.unifiedodds.sdk.testutil.rabbit.libraryfixtures.Delivery;
import com.sportradar.unifiedodds.sdk.testutil.rabbit.libraryfixtures.WaitingRabbitMqConsumer;
import java.util.Optional;
import lombok.val;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class RabbitMqProducerConsumerIT {

    public static final String DEFAULT_ADMIN_USERNAME_IN_DOCKER_IMAGE = "guest";
    public static final String DEFAULT_ADMIN_PASSWORD_IN_DOCKER_IMAGE = "guest";
    private final Credentials producerCredentials = Credentials.with("producer1", "producer1_P4ssw0rd");
    private final Credentials consumerCredentials = Credentials.with("consumer1", "consumer1_P4ssw0rd");
    private final Credentials adminCredentials = Credentials.with(
        DEFAULT_ADMIN_USERNAME_IN_DOCKER_IMAGE,
        DEFAULT_ADMIN_PASSWORD_IN_DOCKER_IMAGE
    );
    private final VhostLocation vhostLocation = VhostLocation.at(Constants.RABBIT_IP, "/testhost");
    private final String exchange = "test_exchange";
    private final String routingKey = "specifiedRoutingKey";
    private final String messageToDeliver = "specifiedMessage";
    private final ExchangeLocation exchangeLocation = ExchangeLocation.at(vhostLocation, exchange);
    private final ConnectionFactory factory = new ConnectionFactory();
    private final WaitingRabbitMqConsumer.Factory consumerFactory = createWaitingRabbitMqConsumerFactory();
    private final TimeUtils time = new TimeUtilsImpl();
    private final Client rabbit = createRabbitMqClient(
        vhostLocation.getHost(),
        adminCredentials,
        Client::new
    );
    private final RabbitMqUserSetup mqUsers = RabbitMqUserSetup.create(vhostLocation, rabbit);

    public RabbitMqProducerConsumerIT() throws Exception {}

    @Before
    public void createUsers() {
        mqUsers.setupUser(producerCredentials);
        mqUsers.setupUser(consumerCredentials);
    }

    @After
    public void deleteCreatedResources() {
        mqUsers.revertChangesMade();
    }

    @Test
    public void shouldTransferMessage() throws Exception {
        try (
            val producer = connectDeclaringExchange(exchangeLocation, producerCredentials, factory, time);
            val consumers = connectToExchange(exchangeLocation, consumerCredentials, factory)
        ) {
            val consumer = consumerFactory.expectingMessage();
            consumers.registerConsumer(routingKey, consumer);

            producer.send(messageToDeliver, routingKey);

            val actualDelivery = consumer.waitForFirstDelivery();
            assertMessagesAreSame(messageToDeliver, actualDelivery);
        }
    }

    @Test
    public void dashSymbolInTopicIsNormalSymbolAndDoesNotRepresentWildcard() throws Exception {
        try (
            val producer = connectDeclaringExchange(exchangeLocation, producerCredentials, factory, time);
            val consumers = connectToExchange(exchangeLocation, consumerCredentials, factory)
        ) {
            val consumer = consumerFactory.expectingMessage();
            consumers.registerConsumer("-.0", consumer);

            producer.send(messageToDeliver, "-.-");
            producer.send(messageToDeliver, "-.0");

            val actualDelivery = consumer.waitForFirstDelivery();
            assertMessagesAreSame(messageToDeliver, actualDelivery);
        }
    }

    private static void assertMessagesAreSame(String expectedMessage, Optional<Delivery> actualDelivery) {
        assertThat(actualDelivery).isPresent();
        assertNotNull(actualDelivery.get().getBody());
        assertEquals(expectedMessage, new String(actualDelivery.get().getBody(), UTF_8));
    }
}
