/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */
package com.sportradar.unifiedodds.sdk.integrationtest.externalrabbit;

import static com.sportradar.unifiedodds.sdk.impl.Constants.*;
import static com.sportradar.unifiedodds.sdk.integrationtest.preconditions.PreconditionsForProxiedRabbitIntegrationTests.shouldMavenRunToxiproxyIntegrationTests;
import static com.sportradar.unifiedodds.sdk.testutil.rabbit.integration.RabbitMqClientFactory.createRabbitMqClient;
import static com.sportradar.unifiedodds.sdk.testutil.rabbit.integration.RabbitMqConsumers.connectToExchange;
import static com.sportradar.unifiedodds.sdk.testutil.rabbit.integration.RabbitMqProducer.connectDeclaringExchange;
import static com.sportradar.unifiedodds.sdk.testutil.rabbit.libraryfixtures.WaitingRabbitMqConsumerDi.createWaitingRabbitMqConsumerFactory;
import static java.nio.charset.StandardCharsets.UTF_8;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assume.assumeThat;

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

@SuppressWarnings({ "ClassFanOutComplexity", "MultipleStringLiterals" })
public class RabbitMqProducerConsumerIT {

    private static final String DEFAULT_ADMIN_USERNAME_IN_DOCKER_IMAGE = ADMIN_USERNAME;
    private static final String DEFAULT_ADMIN_PASSWORD_IN_DOCKER_IMAGE = ADMIN_PASSWORD;
    private static final int ENOUGH_TIME_FOR_CONSUMER_TO_REESTABLISH_CONNECTION = 5000;
    private final Credentials producerCredentials = Credentials.with("producer1", "producer1_P4ssw0rd");
    private final Credentials consumerCredentials = Credentials.with("consumer1", "consumer1_P4ssw0rd");
    private final Credentials adminCredentials = Credentials.with(
        DEFAULT_ADMIN_USERNAME_IN_DOCKER_IMAGE,
        DEFAULT_ADMIN_PASSWORD_IN_DOCKER_IMAGE
    );
    private final VhostLocation vhostLocation = VhostLocation.at(RABBIT_BASE_URL, "/testhost");
    private final VhostLocation proxiedVhostLocation = VhostLocation.at(PROXIED_RABBIT_BASE_URL, "/testhost");
    private final String exchange = "test_exchange";
    private final String routingKey = "specifiedRoutingKey";
    private final String messageToDeliver = "specifiedMessage";
    private final ExchangeLocation exchangeLocation = ExchangeLocation.at(vhostLocation, exchange);
    private final ExchangeLocation proxiedExchangeLocation = ExchangeLocation.at(
        proxiedVhostLocation,
        exchange
    );
    private final ConnectionFactory factory = new ConnectionFactory();
    private final WaitingRabbitMqConsumer.Factory consumerFactory = createWaitingRabbitMqConsumerFactory();
    private final TimeUtils time = new TimeUtilsImpl();
    private final Client rabbit = createRabbitMqClient(
        vhostLocation.getBaseUrl().getHost(),
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
            val consumers = connectToExchange(exchangeLocation, consumerCredentials, factory);
        ) {
            val consumer = consumerFactory.expectingMessage();
            consumers.registerConsumer(routingKey, consumer);

            producer.send(messageToDeliver, routingKey);

            val actualDelivery = consumer.waitForFirstDelivery();
            assertMessagesAreSame(messageToDeliver, actualDelivery);
        }
    }

    @Test
    public void shouldTransferMessageOverProxy() throws Exception {
        assumeThat("see developerREADME", shouldMavenRunToxiproxyIntegrationTests(), equalTo(true));
        try (
            val proxiedRabbit = ProxiedRabbit.proxyRabbit();
            val producer = connectDeclaringExchange(exchangeLocation, producerCredentials, factory, time);
            val consumers = connectToExchange(proxiedExchangeLocation, consumerCredentials, factory);
        ) {
            val consumer = consumerFactory.expectingMessage();
            consumers.registerConsumer(routingKey, consumer);

            producer.send(messageToDeliver, routingKey);

            val actualDelivery = consumer.waitForFirstDelivery();
            assertMessagesAreSame(messageToDeliver, actualDelivery);

            proxiedRabbit.disable();

            producer.send("secondMessage", routingKey);
            consumer.waitExpectingNoDelivery();

            proxiedRabbit.enable();

            Thread.sleep(ENOUGH_TIME_FOR_CONSUMER_TO_REESTABLISH_CONNECTION);

            producer.send("thirdMessage", routingKey);
            assertMessagesAreSame("thirdMessage", consumer.waitForFirstDelivery());
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
