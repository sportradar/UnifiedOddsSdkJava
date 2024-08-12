/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */
package com.sportradar.unifiedodds.sdk.testutil.rabbit.integration;

import static com.sportradar.unifiedodds.sdk.testutil.rabbit.integration.ExchangeLocation.at;
import static com.sportradar.unifiedodds.sdk.testutil.rabbit.integration.RabbitMqConsumers.connectToExchange;
import static com.sportradar.unifiedodds.sdk.testutil.rabbit.libraryfixtures.ConnectionFactoryStubs.stubSingleChannelFactory;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.Assert.assertSame;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.verify;

import com.rabbitmq.client.*;
import com.sportradar.unifiedodds.sdk.testutil.rabbit.libraryfixtures.ChannelMocks;
import com.sportradar.unifiedodds.sdk.testutil.rabbit.libraryfixtures.NoOpConsumer;
import java.io.IOException;
import java.util.concurrent.TimeoutException;
import lombok.val;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

public class RabbitMqConsumersTest {

    private final String any = "any";
    private final String exchange = "givenExchange";
    private final String queueName = "givenQueueName";
    private final Credentials user = Credentials.with("GivenUsername", "GivenPassword");
    private final VhostLocation vhostLocation = VhostLocation.at(BaseUrl.any(), "GivenVhost");
    private final Connection connection = mock(Connection.class);
    private final Channel channel = ChannelMocks.createDeclaringQueue(queueName);
    private final ConnectionFactory factory = stubSingleChannelFactory(connection, channel);

    public RabbitMqConsumersTest() throws IOException, TimeoutException {}

    @Test
    public void shouldNotCreateWithNullExchangeLocation() {
        assertThatThrownBy(() -> connectToExchange(null, Credentials.any(), factory))
            .isInstanceOf(NullPointerException.class)
            .hasMessageContaining("exchangeLocation");
    }

    @Test
    public void shouldNotCreateWithNullCredentials() {
        assertThatThrownBy(() -> connectToExchange(ExchangeLocation.any(), null, factory))
            .isInstanceOf(NullPointerException.class)
            .hasMessageContaining("credentials");
    }

    @Test
    public void shouldNotCreateWithNullConnectionFactory() {
        assertThatThrownBy(() -> connectToExchange(ExchangeLocation.any(), Credentials.any(), null))
            .isInstanceOf(NullPointerException.class)
            .hasMessageContaining("connectionFactory");
    }

    @Test
    public void shouldCreateConnectionWithProvidedCredentials() throws IOException, TimeoutException {
        connectToExchange(ExchangeLocation.any(), user, factory);

        verify(factory).setUsername(user.getUsername());
        verify(factory).setPassword(user.getPassword());
    }

    @Test
    public void shouldCreateConnectionInProvidedVirtualHostLocation() throws IOException, TimeoutException {
        connectToExchange(at(vhostLocation, any), Credentials.any(), factory);

        verify(factory).setVirtualHost(vhostLocation.getVirtualHostname());
        verify(factory).setHost(vhostLocation.getBaseUrl().getHost());
    }

    @Test
    public void shouldBindGeneratedQueueNameToExchangeViaTheRoutingKey() throws Exception {
        val consumers = connectToExchange(at(VhostLocation.any(), exchange), Credentials.any(), factory);

        val routingKey = "specifiedRoutingKey";
        consumers.registerConsumer(routingKey, new NoOpConsumer());

        verify(channel).queueBind(queueName, exchange, routingKey);
    }

    @Test
    public void registeredConsumerShouldBeTheOneSubscribingToMessages() throws Exception {
        val consumers = connectToExchange(ExchangeLocation.any(), Credentials.any(), factory);
        val consumer = mock(Consumer.class);
        consumers.registerConsumer(any, consumer);

        val consumerCaptor = ArgumentCaptor.forClass(Consumer.class);
        verify(channel).basicConsume(any(), anyBoolean(), consumerCaptor.capture());

        assertSame(consumer, consumerCaptor.getValue());
    }

    @Test
    public void closeShouldCloseBothChannelAndConnection() throws IOException, TimeoutException {
        val consumers = connectToExchange(ExchangeLocation.any(), Credentials.any(), factory);

        consumers.close();

        verify(channel).close();
        verify(connection).close();
    }

    @Test
    @SuppressWarnings("EmptyBlock")
    public void tryWithResourcesShouldCloseBothChannelAndConnection() throws IOException, TimeoutException {
        try (val consumers = connectToExchange(ExchangeLocation.any(), Credentials.any(), factory)) {}

        verify(channel).close();
        verify(connection).close();
    }
}
