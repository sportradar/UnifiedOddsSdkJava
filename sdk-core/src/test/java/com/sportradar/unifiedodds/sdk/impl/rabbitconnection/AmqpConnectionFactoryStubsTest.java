/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */
package com.sportradar.unifiedodds.sdk.impl.rabbitconnection;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;

import com.rabbitmq.client.Connection;
import java.io.IOException;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.util.concurrent.TimeoutException;
import org.junit.Test;

public class AmqpConnectionFactoryStubsTest {

    private Connection connection = mock(Connection.class);
    private final long createTimestamp = 1664402403L;

    @Test
    public void shouldBeAbleToProvideConnection()
        throws IOException, NoSuchAlgorithmException, KeyManagementException, TimeoutException {
        AmqpConnectionFactory factory = AmqpConnectionFactoryStubs.holdingConnectionCreatedAt(
            createTimestamp,
            connection
        );

        assertSame(connection, factory.getConnection());
        assertTrue("connection should be able to open", factory.canConnectionOpen());
        assertTrue("connection be open", factory.isConnectionOpen());
        assertEquals(createTimestamp, factory.getConnectionStarted());
    }

    @Test
    public void shouldClose() throws IOException {
        AmqpConnectionFactory factory = AmqpConnectionFactoryStubs.holdingConnectionCreatedAt(
            createTimestamp,
            connection
        );

        factory.close(false);

        assertFalse("connection should be closed", factory.isConnectionOpen());
        assertEquals(0, factory.getConnectionStarted());
    }

    @Test
    public void shouldThrowRuntimeExceptionOnCloseIfRequestedSo() {
        AmqpConnectionFactoryStubs factory = AmqpConnectionFactoryStubs
            .holdingConnectionCreatedAt(createTimestamp, connection)
            .onCloseThrowing(new IllegalArgumentException());

        assertThatThrownBy(() -> factory.close(false)).isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    public void shouldThrowIoExceptionOnCloseIfRequestedSo() {
        AmqpConnectionFactoryStubs factory = AmqpConnectionFactoryStubs
            .holdingConnectionCreatedAt(createTimestamp, connection)
            .onCloseThrowing(new IOException());

        assertThatThrownBy(() -> factory.close(false)).isInstanceOf(IOException.class);
    }
}
