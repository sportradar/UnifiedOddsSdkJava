package com.sportradar.unifiedodds.sdk.impl.rabbitconnection;

import com.rabbitmq.client.Connection;
import org.junit.Test;

import java.io.IOException;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.util.concurrent.TimeoutException;

import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;

public class AMQPConnectionFactoryStubTest {
    @Test
    public void shouldBeAbleToProvideConnection() throws IOException, NoSuchAlgorithmException, KeyManagementException, TimeoutException {
        Connection connection = mock(Connection.class);
        long createTimestamp = 1664402403L;

        AMQPConnectionFactory factory = AMQPConnectionFactoryStub.holdingConnectionCreatedAt(createTimestamp, connection);

        assertSame(connection, factory.getConnection());
        assertTrue("connection should be able to open", factory.canConnectionOpen());
        assertEquals(createTimestamp, factory.getConnectionStarted());
    }
}
