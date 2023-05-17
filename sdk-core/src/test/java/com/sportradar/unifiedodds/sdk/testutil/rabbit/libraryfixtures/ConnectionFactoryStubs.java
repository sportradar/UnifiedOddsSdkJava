/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */
package com.sportradar.unifiedodds.sdk.testutil.rabbit.libraryfixtures;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import java.io.IOException;
import java.util.concurrent.TimeoutException;
import lombok.NonNull;

public class ConnectionFactoryStubs {

    private ConnectionFactoryStubs() {}

    public static ConnectionFactory stubSingleChannelFactory(
        @NonNull final Connection connection,
        @NonNull final Channel channel
    ) throws IOException, TimeoutException {
        ConnectionFactory factory = mock(ConnectionFactory.class);
        when(factory.newConnection()).thenReturn(connection);
        when(connection.createChannel()).thenReturn(channel);
        return factory;
    }
}
