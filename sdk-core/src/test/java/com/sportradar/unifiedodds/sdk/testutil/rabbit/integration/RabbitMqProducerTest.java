/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */
package com.sportradar.unifiedodds.sdk.testutil.rabbit.integration;

import static com.sportradar.unifiedodds.sdk.testutil.rabbit.integration.ExchangeLocation.at;
import static com.sportradar.unifiedodds.sdk.testutil.rabbit.integration.RabbitMqProducer.connectDeclaringExchange;
import static com.sportradar.unifiedodds.sdk.testutil.rabbit.libraryfixtures.ConnectionFactoryStubs.stubSingleChannelFactory;
import static java.nio.charset.StandardCharsets.UTF_8;
import static java.time.Instant.ofEpochMilli;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.*;

import com.rabbitmq.client.AMQP.BasicProperties;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.sportradar.unifiedodds.sdk.testutil.generic.concurrent.AtomicActionPerformer;
import com.sportradar.utils.time.TimeUtilsStub;
import java.io.IOException;
import java.time.Instant;
import java.util.concurrent.TimeoutException;
import lombok.val;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

public class RabbitMqProducerTest {

    private static final long MIDNIGHT_TIMESTAMP_MILLIS = 1664402400000L;
    private final String any = "any";
    private final String exchange = "givenExchange";
    private final Channel channel = mock(Channel.class);
    private final Connection connection = mock(Connection.class);
    private final ConnectionFactory factory = stubSingleChannelFactory(connection, channel);
    private final Credentials user = Credentials.with("GivenUsername", "GivenPassword");
    private final int givenPort = 987;
    private final VhostLocation vhostLocation = VhostLocation.at(
        BaseUrl.of("GivenHost", givenPort),
        "GivenVhost"
    );

    private final Instant instantAtMidnight = ofEpochMilli(MIDNIGHT_TIMESTAMP_MILLIS);
    private final TimeUtilsStub time = TimeUtilsStub
        .threadSafe(new AtomicActionPerformer())
        .withCurrentTime(instantAtMidnight);

    public RabbitMqProducerTest() throws IOException, TimeoutException {}

    @Test
    public void shouldNotCreateWithNullExchangeLocation() {
        assertThatThrownBy(() -> connectDeclaringExchange(null, Credentials.any(), factory, time))
            .isInstanceOf(NullPointerException.class)
            .hasMessageContaining("exchangeLocation");
    }

    @Test
    public void shouldNotCreateWithNullCredentials() {
        assertThatThrownBy(() -> connectDeclaringExchange(ExchangeLocation.any(), null, factory, time))
            .isInstanceOf(NullPointerException.class)
            .hasMessageContaining("credentials");
    }

    @Test
    public void shouldNotCreateWithNullConnectionFactory() {
        assertThatThrownBy(() ->
                connectDeclaringExchange(ExchangeLocation.any(), Credentials.any(), null, time)
            )
            .isInstanceOf(NullPointerException.class)
            .hasMessageContaining("connectionFactory");
    }

    @Test
    public void shouldNotCreateWithNullTimeUtils() {
        assertThatThrownBy(() ->
                connectDeclaringExchange(ExchangeLocation.any(), Credentials.any(), factory, null)
            )
            .isInstanceOf(NullPointerException.class)
            .hasMessageContaining("time");
    }

    @Test
    public void shouldCreateConnectionWithProvidedCredentials() throws IOException, TimeoutException {
        connectDeclaringExchange(ExchangeLocation.any(), user, factory, time);

        verify(factory).setUsername(user.getUsername());
        verify(factory).setPassword(user.getPassword());
    }

    @Test
    public void shouldCreateConnectionInProvidedVirtualHostLocation() throws IOException, TimeoutException {
        connectDeclaringExchange(at(vhostLocation, any), Credentials.any(), factory, time);

        verify(factory).setVirtualHost(vhostLocation.getVirtualHostname());
        verify(factory).setHost(vhostLocation.getBaseUrl().getHost());
        verify(factory).setPort(vhostLocation.getBaseUrl().getPort());
    }

    @Test
    public void shouldCreateTopicExchangeAsSdkOperatesWithTopics() throws IOException, TimeoutException {
        connectDeclaringExchange(at(VhostLocation.any(), exchange), Credentials.any(), factory, time);

        verify(channel).exchangeDeclare(exchange, "topic");
    }

    @Test
    public void sendingShouldPublishToExchangeUnderRoutingKey() throws IOException, TimeoutException {
        val producer = connectDeclaringExchange(
            at(VhostLocation.any(), exchange),
            Credentials.any(),
            factory,
            time
        );

        val routingKey = "specifiedRoutingKey";
        producer.send(any, routingKey);

        verify(channel).basicPublish(eq(exchange), eq(routingKey), any(), any());
    }

    @Test
    public void sendingShouldPublishMessage() throws IOException, TimeoutException {
        val producer = connectDeclaringExchange(ExchangeLocation.any(), Credentials.any(), factory, time);

        val message = "SpecifiedMessage";
        producer.send(message, any);

        verify(channel).basicPublish(any(), any(), any(), eq(message.getBytes(UTF_8)));
    }

    @Test
    public void sendingShouldPublishGenerationTimestamp() throws IOException, TimeoutException {
        val producer = connectDeclaringExchange(ExchangeLocation.any(), Credentials.any(), factory, time);
        val propsCaptor = ArgumentCaptor.forClass(BasicProperties.class);
        doNothing().when(channel).basicPublish(anyString(), anyString(), propsCaptor.capture(), any());
        time.travelTo(Instant.ofEpochMilli(MIDNIGHT_TIMESTAMP_MILLIS));

        producer.send(any, any);

        val value = propsCaptor.getValue();
        assertEquals(MIDNIGHT_TIMESTAMP_MILLIS, value.getHeaders().get("timestamp_in_ms"));
    }

    @Test
    public void closingShouldCloseBothChannelAndConnection() throws IOException, TimeoutException {
        val producer = connectDeclaringExchange(ExchangeLocation.any(), Credentials.any(), factory, time);

        producer.close();

        verify(channel).close();
        verify(connection).close();
    }

    @Test
    @SuppressWarnings("EmptyBlock")
    public void tryWithResourcesShouldCloseChannelAndConnection() throws IOException, TimeoutException {
        try (
            val producer = connectDeclaringExchange(ExchangeLocation.any(), Credentials.any(), factory, time)
        ) {}

        verify(channel).close();
        verify(connection).close();
    }
}
