/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */
package com.sportradar.unifiedodds.sdk.testutil.rabbit.libraryfixtures;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;

import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.Envelope;
import lombok.val;
import org.junit.jupiter.api.Test;

public class DeliveryTest {

    @Test
    public void anyDeliveryShouldContainNonNullEnvelopeAndPropertiesAndBody() {
        assertNotNull(Delivery.any().getEnvelope());
        assertNotNull(Delivery.any().getBody());
        assertNotNull(Delivery.any().getProperties());
    }

    @Test
    public void deliveryShouldPreserveEnvelopeAndPropertiesAndBody() {
        val envelope = mock(Envelope.class);
        val properties = mock(AMQP.BasicProperties.class);
        val body = new byte[] { 'a', 'n', 'y' };

        val delivery = new Delivery(envelope, properties, body);

        assertEquals(envelope, delivery.getEnvelope());
        assertEquals(properties, delivery.getProperties());
        assertEquals(body, delivery.getBody());
    }

    @Test
    public void deliveryShouldNotBeConstructedForNullEnvelope() {
        assertThatThrownBy(() ->
                new Delivery(null, mock(AMQP.BasicProperties.class), new byte[] { 'a', 'n', 'y' })
            )
            .isInstanceOf(NullPointerException.class)
            .hasMessageContaining("envelope");
    }

    @Test
    public void deliveryShouldNotBeConstructedForNullProperties() {
        assertThatThrownBy(() -> new Delivery(mock(Envelope.class), null, new byte[] { 'a', 'n', 'y' }))
            .isInstanceOf(NullPointerException.class)
            .hasMessageContaining("properties");
    }

    @Test
    public void deliveryShouldNotBeConstructedForNullBody() {
        assertThatThrownBy(() -> new Delivery(mock(Envelope.class), mock(AMQP.BasicProperties.class), null))
            .isInstanceOf(NullPointerException.class)
            .hasMessageContaining("body");
    }
}
