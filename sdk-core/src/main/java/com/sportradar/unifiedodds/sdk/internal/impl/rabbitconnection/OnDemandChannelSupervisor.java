/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */

package com.sportradar.unifiedodds.sdk.internal.impl.rabbitconnection;

import com.sportradar.unifiedodds.sdk.internal.impl.ChannelMessageConsumer;
import java.io.IOException;
import java.util.List;

/**
 * Defines methods implemented by classes used to connect to the RabbitMQ broker
 */
public interface OnDemandChannelSupervisor {
    /**
     * Opens the current channel and binds the created queue to the provided routing keys
     *
     * @param routingKeys - a {@link List} of routing keys which should be binded
     * @param channelMessageConsumer - a {@link ChannelMessageConsumer} which consumes the received payloads
     * @param messageInterest message interest this channel is linked to
     * @throws IOException if the routing keys bind failed
     */
    void open(
        List<String> routingKeys,
        ChannelMessageConsumer channelMessageConsumer,
        String messageInterest
    ) throws IOException;

    /**
     * Terminates the current channel
     *
     * @throws IOException if the channel closure failed
     */
    void close() throws IOException;

    /**
     * Checks channel status on demand
     */

    ChannelStatus checkStatus();
}
