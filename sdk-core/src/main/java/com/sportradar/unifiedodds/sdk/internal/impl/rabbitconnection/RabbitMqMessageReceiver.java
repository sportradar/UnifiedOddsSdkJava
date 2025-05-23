/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */

package com.sportradar.unifiedodds.sdk.internal.impl.rabbitconnection;

import com.google.common.base.Preconditions;
import com.google.inject.Inject;
import com.sportradar.unifiedodds.sdk.internal.impl.ChannelMessageConsumer;
import com.sportradar.unifiedodds.sdk.internal.impl.MessageConsumer;
import com.sportradar.unifiedodds.sdk.internal.impl.MessageReceiver;
import java.io.IOException;
import java.util.List;

/**
 * A RabbitMQ message receiver
 */
public class RabbitMqMessageReceiver implements MessageReceiver {

    /**
     * The {@link ChannelSupervisor} instance which will provide the message payloads
     */
    private final ChannelSupervisor channelSupervisor;

    /**
     * The raw message consumer
     */
    private final ChannelMessageConsumer channelMessageConsumer;

    /**
     * Initializes a new instance of {@link RabbitMqMessageReceiver}
     *
     * @param channelSupervisor a {@link ChannelSupervisor} instance which will provide the message payloads
     * @param channelMessageConsumer the raw message consumer
     */
    @Inject
    RabbitMqMessageReceiver(
        ChannelSupervisor channelSupervisor,
        ChannelMessageConsumer channelMessageConsumer
    ) {
        Preconditions.checkNotNull(channelSupervisor);

        this.channelSupervisor = channelSupervisor;
        this.channelMessageConsumer = channelMessageConsumer;
    }

    /**
     * Opens the current instance so it starts receiving messages
     *
     * @param routingKeys a {@link List} of requested routing keys
     * @param messageConsumer a {@link MessageConsumer} instance which will receive messages
     * @throws IOException if the RabbitMq channel failed to open
     */
    @Override
    public void open(List<String> routingKeys, MessageConsumer messageConsumer) throws IOException {
        channelMessageConsumer.open(messageConsumer);

        channelSupervisor.openChannel(
            routingKeys,
            channelMessageConsumer,
            messageConsumer.getMessageInterest().toShortString()
        );
    }

    /**
     * Closes the current instance so it will no longer receive messages
     *
     * @throws IOException if the RabbitMq channel closure encountered a problem
     */
    @Override
    public void close() throws IOException {
        channelSupervisor.closeChannel();
    }
}
