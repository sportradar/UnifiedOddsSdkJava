/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */
package com.sportradar.unifiedodds.sdk.testutil.rabbit.integration;

import static java.lang.String.format;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertEquals;

import com.rabbitmq.client.Channel;
import com.rabbitmq.http.client.Client;
import com.rabbitmq.http.client.domain.ChannelInfo;
import com.rabbitmq.http.client.domain.ConnectionInfo;
import com.sportradar.unifiedodds.sdk.impl.Constants;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import lombok.val;
import org.assertj.core.api.AbstractFileAssert;

public class RabbitConnectionUtils {

    private final Client rabbitClient;

    public RabbitConnectionUtils(final Client rabbitClient) {
        this.rabbitClient = rabbitClient;
    }

    public boolean isConnectionExistsForUsername(String username) {
        return rabbitClient.getConnections().stream().anyMatch(c -> c.getUser().equals(username));
    }

    public void killExistingConnectionForUser(String username) {
        ConnectionInfo connection = getConnectionForUser(username);
        rabbitClient.closeConnection(connection.getName(), "Killed by test");
    }

    public Integer getNumberOfConnections() {
        return rabbitClient.getConnections().size();
    }

    public RabbitConnectionInfoAssert assertThatConnectionForUsername(String username) {
        return RabbitConnectionInfoAssert.assertThat(getConnectionForUser(username), this);
    }

    ConnectionInfo getConnectionForUser(String username) {
        val existingConnectionNames = rabbitClient
            .getConnections()
            .stream()
            .map(c -> c.getUser())
            .collect(Collectors.toList());
        val connection = rabbitClient
            .getConnections()
            .stream()
            .filter(c -> c.getUser().equals(username))
            .findFirst();
        assertThat(connection)
            .as(
                format(
                    "Connection for user %s was not found. Existing connections: %s",
                    username,
                    existingConnectionNames
                )
            )
            .isPresent();
        return connection.get();
    }

    List<ChannelInfo> getChannelsForConnectionWithName(String connectionName) {
        return rabbitClient.getChannels(connectionName);
    }
}
