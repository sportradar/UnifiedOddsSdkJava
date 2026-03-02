/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */
package com.sportradar.unifiedodds.sdk.testutil.rabbit.integration;

import com.rabbitmq.http.client.domain.ConnectionInfo;
import lombok.val;
import org.assertj.core.api.AbstractAssert;

public class RabbitConnectionInfoAssert extends AbstractAssert<RabbitConnectionInfoAssert, ConnectionInfo> {

    private final RabbitConnectionUtils connectionUtils;

    private RabbitConnectionInfoAssert(ConnectionInfo connectionInfo, RabbitConnectionUtils connectionUtils) {
        super(connectionInfo, RabbitConnectionInfoAssert.class);
        this.connectionUtils = connectionUtils;
    }

    static RabbitConnectionInfoAssert assertThat(
        ConnectionInfo connection,
        RabbitConnectionUtils connectionUtils
    ) {
        return new RabbitConnectionInfoAssert(connection, connectionUtils);
    }

    public RabbitConnectionInfoAssert hasNumberOfChannels(int expectedNumberOfChannels) {
        org.assertj.core.api.Assertions.assertThat(actual.getChannels()).isEqualTo(expectedNumberOfChannels);
        return this;
    }

    public void exists() {
        // already retrieved connection is injected to this class if it exists
    }

    public RabbitConnectionInfoAssert hasChannelsWithUniqueNames() {
        val channels = connectionUtils.getChannelsForConnectionWithName(actual.getName());
        org.assertj.core.api.Assertions
            .assertThat(channels.stream().distinct().count())
            .isEqualTo(channels.size());
        return this;
    }
}
