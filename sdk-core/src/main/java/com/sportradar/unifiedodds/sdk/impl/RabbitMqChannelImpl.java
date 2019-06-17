/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */

package com.sportradar.unifiedodds.sdk.impl;

import com.google.common.base.Preconditions;
import com.google.inject.Inject;
import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.DefaultConsumer;
import com.rabbitmq.client.Envelope;
import com.rabbitmq.client.Recoverable;
import com.sportradar.unifiedodds.sdk.impl.apireaders.WhoAmIReader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeoutException;
import java.util.function.Supplier;

/**
 * An implementation of the {@link RabbitMqChannel}
 */
public class RabbitMqChannelImpl implements RabbitMqChannel {
    private static final Logger logger = LoggerFactory.getLogger(RabbitMqChannelImpl.class);

    /**
     * The broken exchange name
     */
    private static final String INFO_EXCHANGE = System.getProperty("sportradar.receiving.exchange", "unifiedfeed");

    /**
     * The AMQP channel supplier
     */
    private final Supplier<Channel> channelSupplier;

    /**
     * A {@link RabbitMqSystemListener} instance used to dispatch system events
     */
    private final RabbitMqSystemListener rabbitMqSystemListener;

    /**
     * The SDK MDC context map
     */
    private final Map<String, String> sdkMdcContextDescription;

    /**
     * A {@link ChannelMessageConsumer} instance used to dispatch received message payloads
     */
    private ChannelMessageConsumer channelMessageConsumer;

    /**
     * A {@link Channel} instance used by this instance
     */
    private Channel channel;

    /**
     * An indication if the current channel is opened
     */
    private boolean isOpened = false;


    /**
     * Initializes a new instance of the {@link RabbitMqChannelImpl}
     *
     * @param channelSupplier a {@link Supplier} of {@link Channel}s
     * @param rabbitMqSystemListener a {@link RabbitMqSystemListener} instance used to dispatch system events
     * @param whoAmIReader a {@link WhoAmIReader} used to access current SDK instance context information
     */
    @Inject
    public RabbitMqChannelImpl(Supplier<Channel> channelSupplier, RabbitMqSystemListener rabbitMqSystemListener, WhoAmIReader whoAmIReader) {
        Preconditions.checkNotNull(channelSupplier);
        Preconditions.checkNotNull(rabbitMqSystemListener);
        Preconditions.checkNotNull(whoAmIReader);

        this.channelSupplier = channelSupplier;
        this.rabbitMqSystemListener = rabbitMqSystemListener;
        this.sdkMdcContextDescription = whoAmIReader.getAssociatedSdkMdcContextMap();
    }

    /**
     * Opens the current channel and binds the created queue to the provided routing keys
     *
     * @param routingKeys - a {@link List} of routing keys which should be binded
     * @param channelMessageConsumer - a {@link ChannelMessageConsumer} which consumes the received payloads
     * @throws IOException if the routing keys bind failed
     */
    @Override
    public synchronized void open(List<String> routingKeys, ChannelMessageConsumer channelMessageConsumer) throws IOException {
        Preconditions.checkNotNull(routingKeys);
        Preconditions.checkArgument(!routingKeys.isEmpty());
        Preconditions.checkNotNull(channelMessageConsumer);

        if (isOpened) {
            return;
        }

        this.channel = channelSupplier.get();
        if (this.channel == null) {
            throw new IOException("Provided AMQP channel is null");
        }

        this.channelMessageConsumer = channelMessageConsumer;
        try {
            initChannelQue(routingKeys);
            isOpened = true;
        } catch (IOException e) {
            throw new IOException("Channel queue declaration failed, ex: ", e);
        }
    }

    /**
     * Initializes a new queue in the provided channel and binds the provided routing keys
     *
     * @param routingKeys - a {@link List} of routing keys which should be binded
     * @throws IOException - thrown if any queue operation fails to complete
     */
    private void initChannelQue(List<String> routingKeys) throws IOException {
        Preconditions.checkNotNull(channel);

        String qName = channel.queueDeclare().getQueue();
        for (String routingKey : routingKeys) {
            logger.debug("Binding queue={} with routingKey={}", qName, routingKey);
            channel.queueBind(qName, INFO_EXCHANGE, routingKey);
        }

        DefaultConsumer consumer = new DefaultConsumer(channel) {
            @Override
            public synchronized void handleDelivery(String tag, Envelope envelope, AMQP.BasicProperties properties, byte[] body) {
                MDC.setContextMap(sdkMdcContextDescription);
                try {
                    channelMessageConsumer.onMessageReceived(envelope.getRoutingKey(), body, properties, new TimeUtilsImpl().now());
                } catch (Exception e) {
                    logger.error("An exception occurred while processing AMQP message. Routing key: '{}', body: '{}'",
                            envelope.getRoutingKey(),
                            body == null ? "null" : new String(body),
                            e
                    );
                }
                MDC.clear();
            }
        };

        channel.addShutdownListener(rabbitMqSystemListener);
        ((Recoverable) channel).addRecoveryListener(rabbitMqSystemListener);
        channel.basicConsume(qName, true, consumer);
    }

    /**
     * Terminates the current channel
     *
     * @throws IOException if the channel closure failed
     */
    @Override
    public synchronized void close() throws IOException {
        if (!isOpened) {
            logger.warn("Attempting to close an already closed channel");
            return;
        }
        try {
            isOpened = false;

            if (channel != null) {
                channel.close();
            }
        } catch (TimeoutException | IOException e) {
            throw new IOException("Channel closure failed: ", e);
        }
    }

    /**
     * Indicates if the associated channel instance is opened
     *
     * @return - <code>true</code> if the channel is opened; <code>false</code> otherwise
     */
    @Override
    public synchronized boolean isOpened() {
        return channel != null && channel.isOpen();
    }
}
