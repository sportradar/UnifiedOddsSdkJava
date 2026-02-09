/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */
package com.sportradar.unifiedodds.sdk.conn;

import java.util.ArrayList;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.stream.Collectors;

public class RabbitMessagesInMemoryStorage {

    private final Queue<ReceivedRabbitMessage> messages = new LinkedBlockingDeque<>();

    public void append(ReceivedRabbitMessage message) {
        messages.add(message);
    }

    public List<ReceivedRabbitMessage> findAlivesOf() {
        return messages
            .stream()
            .filter(m -> m.getRoutingKey().contains("-.-.-.alive"))
            .collect(Collectors.toList());
    }

    public List<ReceivedRabbitMessage> allMessages() {
        return new ArrayList<>(messages);
    }
}
