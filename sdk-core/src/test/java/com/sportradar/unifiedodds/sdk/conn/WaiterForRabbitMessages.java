/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */
package com.sportradar.unifiedodds.sdk.conn;

import static java.util.concurrent.TimeUnit.SECONDS;

import java.util.List;
import java.util.concurrent.Callable;
import java.util.function.Function;
import java.util.stream.Collectors;
import lombok.*;
import org.awaitility.Awaitility;

public class WaiterForRabbitMessages {

    private final RabbitMessagesInMemoryStorage messagesStorage;

    public WaiterForRabbitMessages(RabbitMessagesInMemoryStorage messagesStorage) {
        this.messagesStorage = messagesStorage;
    }

    public ReceivedRabbitMessage theOnlyAliveMessage() {
        final int tenSecondsForSlowMachines = 10;
        Awaitility.await().atMost(tenSecondsForSlowMachines, SECONDS).until(anyAliveReceived());
        List<ReceivedRabbitMessage> allMessages = messagesStorage.findAlivesOf();
        if (allMessages.size() != 1) {
            throw new IllegalStateException("Expected 1 alive message, but found " + allMessages.size());
        }
        return allMessages.get(0);
    }

    private Callable<Boolean> anyAliveReceived() {
        return () -> !messagesStorage.findAlivesOf().isEmpty();
    }

    public ReceivedRabbitMessage nthMessage(
        int n,
        Function<Filter.FilterBuilder, Filter.FilterBuilder> configureFilter
    ) {
        final int tenSecondsForSlowMachines = 10;
        Awaitility.await().atMost(tenSecondsForSlowMachines, SECONDS).until(anyAliveReceived());
        val allMessages = messagesStorage.allMessages();
        val filter = configureFilter.apply(new Filter.FilterBuilder()).build();
        val filteredMessages = filter.filter(allMessages);
        val filteredMessagesCount = filteredMessages.size();
        if (filteredMessagesCount < n) {
            throw new IllegalStateException(
                "Expected at least " + n + " message, but found " + filteredMessagesCount
            );
        }
        int index = n - 1;
        return allMessages.get(index);
    }

    @Builder
    @AllArgsConstructor(access = AccessLevel.PRIVATE)
    public static class Filter {

        private String routingKey;

        private List<ReceivedRabbitMessage> filter(List<ReceivedRabbitMessage> unfilteredMessages) {
            return unfilteredMessages
                .stream()
                .filter(m -> routingKey == null || m.getRoutingKey().equals(routingKey))
                .collect(Collectors.toList());
        }
    }
}
