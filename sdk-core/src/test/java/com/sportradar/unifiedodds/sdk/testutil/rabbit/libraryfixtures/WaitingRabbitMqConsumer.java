/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */
package com.sportradar.unifiedodds.sdk.testutil.rabbit.libraryfixtures;

import static java.util.Optional.ofNullable;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.Envelope;
import com.sportradar.unifiedodds.sdk.testutil.generic.concurrent.SignallingOnPollingQueue;
import java.util.Optional;
import java.util.concurrent.TimeUnit;
import lombok.val;

public class WaitingRabbitMqConsumer extends NoOpConsumer {

    private final SignallingOnPollingQueue<Delivery> receivedMessages;

    private WaitingRabbitMqConsumer(final SignallingOnPollingQueue<Delivery> receivedMessages) {
        this.receivedMessages = receivedMessages;
    }

    protected SignallingOnPollingQueue<Delivery> getReceivedMessages() {
        return receivedMessages;
    }

    @Override
    public void handleDelivery(
        String consumerTag,
        Envelope envelope,
        AMQP.BasicProperties properties,
        byte[] body
    ) {
        getReceivedMessages().add(new Delivery(envelope, properties, body));
    }

    public Optional<Delivery> waitForFirstDelivery() {
        val message = ofNullable(receivedMessages.poll(1, TimeUnit.SECONDS));
        assertTrue("Message was not received", message.isPresent());
        return message;
    }

    public void waitExpectingNoDelivery() {
        val message = ofNullable(receivedMessages.poll(1, TimeUnit.SECONDS));
        assertFalse("Message was received", message.isPresent());
    }

    public static class Factory {

        private final SignallingOnPollingQueue<Delivery> receivedMessages;

        Factory(final SignallingOnPollingQueue<Delivery> receivedMessages) {
            this.receivedMessages = receivedMessages;
        }

        public WaitingRabbitMqConsumer expectingMessage() {
            return new WaitingRabbitMqConsumer(receivedMessages);
        }
    }
}
