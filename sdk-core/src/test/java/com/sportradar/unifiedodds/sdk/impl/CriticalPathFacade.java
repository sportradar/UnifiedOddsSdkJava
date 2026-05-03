/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */
package com.sportradar.unifiedodds.sdk.impl;

import com.rabbitmq.client.AMQP;
import com.sportradar.unifiedodds.sdk.CriticalPath;
import com.sportradar.unifiedodds.sdk.UofSessionBuilder;
import com.sportradar.unifiedodds.sdk.internal.impl.ChannelMessageConsumerImpl;
import java.io.IOException;

public class CriticalPathFacade {

    private final CriticalPath criticalPath;
    private final ChannelMessageConsumerImpl channelMessageConsumer;

    public CriticalPathFacade(CriticalPath criticalPath, ChannelMessageConsumerImpl channelMessageConsumer) {
        this.criticalPath = criticalPath;
        this.channelMessageConsumer = channelMessageConsumer;
    }

    public void open() throws IOException {
        criticalPath.open();
    }

    public void close() {
        criticalPath.close();
    }

    public UofSessionBuilder createBuilder() {
        return criticalPath.createBuilder();
    }

    public void onMessageReceived(
        String routingKey,
        byte[] body,
        AMQP.BasicProperties properties,
        long receivedAt
    ) {
        channelMessageConsumer.onMessageReceived(routingKey, body, properties, receivedAt);
    }
}
