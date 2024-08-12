/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */
package com.sportradar.unifiedodds.sdk.testutil.rabbit.libraryfixtures;

import static com.google.common.collect.Iterables.getOnlyElement;
import static com.sportradar.unifiedodds.sdk.testutil.generic.concurrent.SignallingOnPollingQueue.createSignallingOnPollingQueue;
import static com.sportradar.unifiedodds.sdk.testutil.rabbit.libraryfixtures.WaitingRabbitMqConsumerDi.createWaitingRabbitMqConsumerFactory;
import static com.sportradar.utils.time.TimeInterval.seconds;
import static java.util.concurrent.TimeUnit.SECONDS;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.Assert.assertSame;
import static org.mockito.Mockito.mock;

import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.Envelope;
import com.sportradar.unifiedodds.sdk.impl.TimeUtilsImpl;
import com.sportradar.unifiedodds.sdk.testutil.generic.concurrent.AtomicActionPerformer;
import com.sportradar.unifiedodds.sdk.testutil.generic.concurrent.FluentExecutor;
import com.sportradar.unifiedodds.sdk.testutil.generic.concurrent.SignallingOnPollingQueue;
import com.sportradar.utils.time.TimeUtilsStub;
import java.time.Instant;
import java.util.concurrent.TimeUnit;
import lombok.val;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;

@Timeout(1)
public class WaitingRabbitMqConsumerTest {

    private static final long MIDNIGHT_TIMESTAMP_MILLIS = 1664402400000L;
    private static final Instant INSTANT_AT_MIDNIGHT = Instant.ofEpochMilli(MIDNIGHT_TIMESTAMP_MILLIS);

    private final Envelope anyEnvelope = mock(Envelope.class);
    private final AMQP.BasicProperties anyProperties = mock(AMQP.BasicProperties.class);
    private final byte[] anyBody = new byte[] { 'a', 'n', 'y' };
    private final String any = "anyString";

    private final TimeUtilsStub timeUtils = TimeUtilsStub
        .threadSafe(new AtomicActionPerformer())
        .withCurrentTime(INSTANT_AT_MIDNIGHT);
    private FluentExecutor executor = new FluentExecutor();

    @Test
    public void shouldReceiveDeliveryIfItWasDeliveredEvenBeforeStartedWaiting() {
        val consumer = createWaitingRabbitMqConsumerFactory().expectingMessage();

        consumer.handleDelivery(any, anyEnvelope, anyProperties, anyBody);

        assertThat(consumer.waitForFirstDelivery()).isPresent();
    }

    @Test
    public void shouldReceiveDeliveryIfItWasDeliveredWithin1SecondFromStartedWaitingForIt() {
        val time = new TimeUtilsImpl();
        val queue = SignallingOnPollingQueue.<Delivery>createSignallingOnPollingQueue(time);
        val consumer = new WaitingRabbitMqConsumer.Factory(queue).expectingMessage();

        executor.executeInAnotherThread(() -> {
            queue.getWaiterForStartingToPoll().await(1, SECONDS);
            consumer.handleDelivery(any, anyEnvelope, anyProperties, anyBody);
        });

        assertThat(consumer.waitForFirstDelivery()).isPresent();
    }

    @Test
    public void shouldAccessTheEnvelopeOfMessageTransferred() {
        val consumer = createWaitingRabbitMqConsumerFactory().expectingMessage();

        val envelopeSent = mock(Envelope.class);
        consumer.handleDelivery(any, envelopeSent, anyProperties, anyBody);

        val delivery = consumer.waitForFirstDelivery().get();
        assertSame(envelopeSent, delivery.getEnvelope());
    }

    @Test
    public void shouldAccessThePropertiesOfMessageTransferred() {
        val consumer = createWaitingRabbitMqConsumerFactory().expectingMessage();

        val propertiesSent = mock(AMQP.BasicProperties.class);
        consumer.handleDelivery(any, anyEnvelope, propertiesSent, anyBody);

        val delivery = consumer.waitForFirstDelivery().get();
        assertSame(propertiesSent, delivery.getProperties());
    }

    @Test
    public void shouldAccessBodyOfMessageTransferred() {
        val consumer = createWaitingRabbitMqConsumerFactory().expectingMessage();

        val bodySent = new byte[] { 'b', 'o', 'd', 'y' };
        consumer.handleDelivery(any, anyEnvelope, anyProperties, bodySent);

        val delivery = consumer.waitForFirstDelivery().get();
        assertSame(bodySent, delivery.getBody());
    }

    @Test
    public void shouldFailIfMessageWasNotReceivedForMoreThan1Sec() {
        final SignallingOnPollingQueue<Delivery> queue = createSignallingOnPollingQueue(timeUtils);
        val consumer = new WaitingRabbitMqConsumer.Factory(queue).expectingMessage();

        executor.executeInAnotherThread(() -> {
            queue.getWaiterForStartingToPoll().await(1, TimeUnit.SECONDS);
            timeUtils.tick(seconds(2));
        });

        assertThatThrownBy(() -> consumer.waitForFirstDelivery())
            .isInstanceOf(AssertionError.class)
            .hasMessageContaining("Message was not received");
    }

    @Test
    public void shouldReceiveOnlyFirstDeliveryInCaseMultipleDeliveriesOccurred() {
        val consumer = createWaitingRabbitMqConsumerFactory().expectingMessage();
        val firstEnvelope = mock(Envelope.class);
        val secondEnvelope = mock(Envelope.class);

        consumer.handleDelivery(any, firstEnvelope, anyProperties, anyBody);
        consumer.handleDelivery(any, secondEnvelope, anyProperties, anyBody);

        val delivery = consumer.waitForFirstDelivery().get();
        assertSame(firstEnvelope, delivery.getEnvelope());
    }

    @Test
    public void shouldBeAbleToReceiveAnotherDelivery() {
        val consumer = createWaitingRabbitMqConsumerFactory().expectingMessage();
        val firstEnvelope = mock(Envelope.class);
        val secondEnvelope = mock(Envelope.class);

        consumer.handleDelivery(any, firstEnvelope, anyProperties, anyBody);
        consumer.handleDelivery(any, secondEnvelope, anyProperties, anyBody);

        consumer.waitForFirstDelivery().get();
        val delivery = consumer.waitForFirstDelivery().get();

        assertSame(secondEnvelope, delivery.getEnvelope());
    }
}
