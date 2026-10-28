/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */
package com.sportradar.unifiedodds.sdk.testutil.rabbit.integration;

import static java.lang.String.format;
import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;
import static org.junit.Assert.assertEquals;

import com.rabbitmq.http.client.Client;
import com.rabbitmq.http.client.domain.ChannelInfo;
import com.rabbitmq.http.client.domain.ConnectionInfo;
import com.rabbitmq.http.client.domain.ConsumerDetails;
import java.util.List;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;
import lombok.AllArgsConstructor;
import lombok.Builder;
import org.awaitility.core.ConditionTimeoutException;

public class RabbitConnections {

    private final Client rabbitClient;
    private final int sdkUses1ConsumerPerQueue = 1;

    public RabbitConnections(final Client rabbitClient) {
        this.rabbitClient = rabbitClient;
    }

    private boolean isConnectionExistsForUsername(String username) {
        return rabbitClient.getConnections().stream().anyMatch(c -> c.getUser().equals(username));
    }

    public void killExistingConnectionForUser(String username) {
        await().until(() -> isConnectionExistsForUsername(username));
        ConnectionInfo connection = getConnectionForUser(username);
        rabbitClient.closeConnection(connection.getName(), "Killed by test");
    }

    public Integer getNumberOfConnections() {
        return rabbitClient.getConnections().size();
    }

    public RabbitConnectionInfoAssert assertThatConnectionForUsername(String username) {
        return RabbitConnectionInfoAssert.assertThat(username, this);
    }

    List<ConsumerDetails> getConsumersForQueue(
        String queueName,
        ExpectationForConnectionDueToManagementApiLaggingBehind expected
    ) {
        return awaitUntilSizeIs(
            sdkUses1ConsumerPerQueue,
            () ->
                rabbitClient
                    .getConsumers()
                    .stream()
                    .filter(c -> c.getQueueDetails().getName().equals(queueName))
                    .collect(Collectors.toList())
        );
    }

    ConnectionInfo getConnectionForUser(String username) {
        return awaitUntilSucceeds(() ->
            rabbitClient.getConnections().stream().filter(c -> c.getUser().equals(username)).findFirst().get()
        );
    }

    Set<String> getQueueNamesForConnectionWithName(
        String connectionName,
        ExpectationForConnectionDueToManagementApiLaggingBehind expected
    ) {
        List<ChannelInfo> channels = awaitUntilSizeIs(
            expected.channels,
            () -> rabbitClient.getChannels(connectionName)
        );

        Set<String> channelNames = channels.stream().map(ChannelInfo::getName).collect(Collectors.toSet());

        List<ConsumerDetails> consumers = awaitUntilSizeIs(
            expected.queues * sdkUses1ConsumerPerQueue,
            rabbitClient::getConsumers
        );

        return consumers
            .stream()
            .filter(c -> channelNames.contains(c.getChannelDetails().getName()))
            .map(c -> c.getQueueDetails().getName())
            .collect(Collectors.toSet());
    }

    List<ChannelInfo> getChannelsForConnectionWithName(String connectionName) {
        return awaitUntilSucceeds(() -> rabbitClient.getChannels(connectionName));
    }

    @SuppressWarnings({ "LambdaBodyLength", "IllegalCatch" })
    private <T> List<T> awaitUntilSizeIs(int collectionSize, Callable<List<T>> provider) {
        AtomicReference<List<T>> collection = new AtomicReference<>();
        AtomicReference<Exception> exception = new AtomicReference<>();

        try {
            await()
                .until(() -> {
                    try {
                        List<T> listFromRecentCall = provider.call();
                        if (listFromRecentCall.size() == collectionSize) {
                            collection.set(listFromRecentCall);
                            return true;
                        }
                        return false;
                    } catch (Exception e) {
                        exception.set(e);
                        return false;
                    }
                });
        } catch (ConditionTimeoutException e) {
            if (exception.get() != null) {
                throw new AssertionError(exception.get());
            } else {
                throw new AssertionError(
                    "Expected collection size: " + collectionSize + ", but was not met within the timeout."
                );
            }
        }
        return collection.get();
    }

    @SuppressWarnings("IllegalCatch")
    private <T> T awaitUntilSucceeds(Callable<T> provider) {
        AtomicReference<T> result = new AtomicReference<>();
        AtomicReference<Exception> exception = new AtomicReference<>();
        try {
            await()
                .until(() -> {
                    try {
                        result.set(provider.call());
                        return true;
                    } catch (Exception e) {
                        exception.set(e);
                        return false;
                    }
                });
        } catch (ConditionTimeoutException e) {
            if (exception.get() != null) {
                throw new AssertionError(exception.get());
            } else {
                throw new AssertionError("Expected to call to succeed, but was not met within the timeout.");
            }
        }
        return result.get();
    }

    @Builder
    public static class ExpectationForConnectionDueToManagementApiLaggingBehind {

        private int queues;
        private int channels;

        public ExpectationForConnectionDueToManagementApiLaggingBehind(int queues, int channels) {
            this.queues = queues;
            this.channels = channels;
            if (queues == 0 || channels == 0) {
                throw new IllegalArgumentException("queues and channels must be greater than zero");
            }
        }
    }
}
