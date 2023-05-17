/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */

package com.sportradar.unifiedodds.sdk.impl;

import java.io.IOException;
import java.util.List;

/**
 * Defines methods implemented by classes capable of receiving messages from the feed
 */
public interface MessageReceiver {
    /**
     * Opens the current instance so it starts receiving messages
     *
     * @param routingKeys - a {@link List} of requested routing keys
     * @param messageConsumer - a {@link MessageConsumer} instance which will receive messages
     * @throws IOException if the channel failed to open
     */
    void open(List<String> routingKeys, MessageConsumer messageConsumer) throws IOException;

    /**
     * Closes the current instance so it will no longer receive messages
     *
     * @throws IOException if the channel closure encountered a problem
     */
    void close() throws IOException;
}
