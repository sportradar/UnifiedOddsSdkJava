/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */
package com.sportradar.unifiedodds.sdk.testutil.rabbit.integration;

import static org.awaitility.Awaitility.await;

import com.rabbitmq.http.client.domain.ConnectionInfo;
import com.sportradar.unifiedodds.sdk.testutil.generic.concurrent.ThrowingConsumer;
import com.sportradar.unifiedodds.sdk.testutil.generic.concurrent.VoidCallables;
import com.sportradar.unifiedodds.sdk.testutil.rabbit.integration.RabbitConnections.ExpectationForConnectionDueToManagementApiLaggingBehind;
import com.sportradar.unifiedodds.sdk.testutil.rabbit.integration.RabbitConnections.ExpectationForConnectionDueToManagementApiLaggingBehind.ExpectationForConnectionDueToManagementApiLaggingBehindBuilder;
import java.util.Collections;
import java.util.Set;
import lombok.val;

public class RabbitConnectionInfoAssert {

    private final String username;
    private final RabbitConnections connectionUtils;
    private ConnectionInfo actual;

    private RabbitConnectionInfoAssert(String username, RabbitConnections connectionUtils) {
        this.username = username;
        this.actual = connectionUtils.getConnectionForUser(username);
        this.connectionUtils = connectionUtils;
    }

    static RabbitConnectionInfoAssert assertThat(String username, RabbitConnections connectionUtils) {
        return new RabbitConnectionInfoAssert(username, connectionUtils);
    }

    public RabbitConnectionInfoAssert hasNumberOfChannels(int expectedNumberOfChannels) {
        org.assertj.core.api.Assertions.assertThat(actual.getChannels()).isEqualTo(expectedNumberOfChannels);
        return this;
    }

    public RabbitConnectionInfoAssert exists() {
        org.assertj.core.api.Assertions.assertThat(actual).isNotNull();
        return this;
    }

    public RabbitConnectionInfoAssert hasChannelsWithUniqueNames() {
        val channels = connectionUtils.getChannelsForConnectionWithName(actual.getName());
        org.assertj.core.api.Assertions
            .assertThat(channels.stream().distinct().count())
            .isEqualTo(channels.size());
        return this;
    }

    public RabbitConnectionInfoAssert hasSameQueueNamesBeforeAndAfter(
        SetterOfExpectationForConnectionDueToManagementApiLaggingBehind additionalExpectationsSetter,
        VoidCallables.ThrowingRunnable runnable
    ) throws Exception {
        val additionalExpectations = additionalExpectationsSetter
            .apply(ExpectationForConnectionDueToManagementApiLaggingBehind.builder())
            .build();
        Set<String> queueNamesBefore = connectionUtils.getQueueNamesForConnectionWithName(
            actual.getName(),
            additionalExpectations
        );

        runnable.run();

        this.actual = connectionUtils.getConnectionForUser(username);
        Set<String> queueNamesAfter = connectionUtils.getQueueNamesForConnectionWithName(
            actual.getName(),
            additionalExpectations
        );

        org.assertj.core.api.Assertions.assertThat(queueNamesBefore).isEqualTo(queueNamesAfter);
        return this;
    }

    public RabbitConnectionInfoAssert hasReconnectedAndAllQueueNamesDifferentBeforeAndAfter(
        SetterOfExpectationForConnectionDueToManagementApiLaggingBehind additionalExpectationsSetter,
        ThrowingConsumer<AwaitUntil> consumer
    ) throws Exception {
        val additionalExpectations = additionalExpectationsSetter
            .apply(ExpectationForConnectionDueToManagementApiLaggingBehind.builder())
            .build();
        Set<String> queueNamesBefore = connectionUtils.getQueueNamesForConnectionWithName(
            actual.getName(),
            additionalExpectations
        );

        consumer.accept(new AwaitUntil(queueNamesBefore));

        this.actual = connectionUtils.getConnectionForUser(username);
        Set<String> queueNamesAfter = connectionUtils.getQueueNamesForConnectionWithName(
            actual.getName(),
            additionalExpectations
        );

        org.assertj.core.api.Assertions
            .assertThat(queueNamesBefore)
            .doesNotContainAnyElementsOf(queueNamesAfter);
        return this;
    }

    public class AwaitUntil {

        private final Set<String> queueNamesAtStart;

        private AwaitUntil(Set<String> currentQueueNames) {
            this.queueNamesAtStart = currentQueueNames;
        }

        public AwaitUntil connectionRecreated() {
            val oldConnectionName = actual.getName();

            await()
                .until(() -> {
                    ConnectionInfo connectionForUser = connectionUtils.getConnectionForUser(username);
                    if (connectionForUser.getName().equals(oldConnectionName)) {
                        return false;
                    } else {
                        actual = connectionForUser;
                        return true;
                    }
                });
            return this;
        }

        @SuppressWarnings("LambdaBodyLength")
        public AwaitUntil queuesRecreated(
            SetterOfExpectationForConnectionDueToManagementApiLaggingBehind additionalExpectationsSetter
        ) {
            val additionalExpectations = additionalExpectationsSetter
                .apply(ExpectationForConnectionDueToManagementApiLaggingBehind.builder())
                .build();
            await()
                .until(() -> {
                    Set<String> latestQueueNames = connectionUtils.getQueueNamesForConnectionWithName(
                        actual.getName(),
                        additionalExpectations
                    );
                    boolean areOldQueuesDeleted = Collections.disjoint(queueNamesAtStart, latestQueueNames);
                    boolean areNewQueuesReadyToConsume = areChannelsReadyToConsumeMessagesFromQueues(
                        latestQueueNames,
                        additionalExpectations
                    );
                    return areOldQueuesDeleted && areNewQueuesReadyToConsume;
                });
            return this;
        }

        private boolean areChannelsReadyToConsumeMessagesFromQueues(
            Set<String> latestQueueNames,
            ExpectationForConnectionDueToManagementApiLaggingBehind additionalExpectations
        ) {
            for (String queueName : latestQueueNames) {
                if (!isChannelReadyToConsumeMessagesFromQueue(queueName, additionalExpectations)) {
                    return false;
                }
            }
            return true;
        }

        private boolean isChannelReadyToConsumeMessagesFromQueue(
            String queueName,
            ExpectationForConnectionDueToManagementApiLaggingBehind additionalExpectations
        ) {
            return !connectionUtils.getConsumersForQueue(queueName, additionalExpectations).isEmpty();
        }
    }

    public interface SetterOfExpectationForConnectionDueToManagementApiLaggingBehind {
        ExpectationForConnectionDueToManagementApiLaggingBehindBuilder apply(
            ExpectationForConnectionDueToManagementApiLaggingBehindBuilder builder
        );
    }
}
