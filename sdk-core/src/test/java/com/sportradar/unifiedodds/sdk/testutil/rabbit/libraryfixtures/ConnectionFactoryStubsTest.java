/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */
package com.sportradar.unifiedodds.sdk.testutil.rabbit.libraryfixtures;

import static com.sportradar.unifiedodds.sdk.testutil.rabbit.libraryfixtures.ConnectionFactoryStubs.stubSingleChannelFactory;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import java.io.IOException;
import java.util.concurrent.TimeoutException;
import lombok.val;
import org.junit.jupiter.api.Test;

public class ConnectionFactoryStubsTest {

    @Test
    public void shouldNotCreateWithNullConnection() {
        assertThatThrownBy(() -> stubSingleChannelFactory(null, mock(Channel.class)))
            .isInstanceOf(NullPointerException.class)
            .hasMessageContaining("connection");
    }

    @Test
    public void shouldNotCreateWithNullChannel() {
        assertThatThrownBy(() -> stubSingleChannelFactory(mock(Connection.class), null))
            .isInstanceOf(NullPointerException.class)
            .hasMessageContaining("channel");
    }

    @Test
    public void shouldIssueStubbedConnection() throws IOException, TimeoutException {
        val connection = mock(Connection.class);

        val factory = stubSingleChannelFactory(connection, mock(Channel.class));

        assertEquals(connection, factory.newConnection());
    }

    @Test
    public void shouldIssueStubbedChannel() throws IOException, TimeoutException {
        val channel = mock(Channel.class);

        val factory = stubSingleChannelFactory(mock(Connection.class), channel);

        assertEquals(channel, factory.newConnection().createChannel());
    }
}
