/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */
package com.sportradar.unifiedodds.sdk.impl.rabbitconnection;

import static com.sportradar.unifiedodds.sdk.impl.rabbitconnection.AmqpConnectionFactoryFake.initiallyProvides;
import static com.sportradar.unifiedodds.sdk.impl.rabbitconnection.ConnectionToBeProvided.ChannelsToBeCreated.creating;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.rabbitmq.client.Channel;
import com.sportradar.unifiedodds.sdk.impl.TimeUtils;
import com.sportradar.utils.time.EpochMillis;
import java.io.IOException;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.util.concurrent.TimeoutException;
import org.junit.jupiter.api.Test;

public class AmqpConnectionFactoryFakeTest {

    private final Channel channel = mock(Channel.class);
    private final EpochMillis createTimestamp = new EpochMillis(1664402403L);

    private final TimeUtils time = mock(TimeUtils.class);
    private final ConnectionToBeProvided.Factory provides = new ConnectionToBeProvided.Factory(time);

    @Test
    public void shouldBeAbleToProvideConnection()
        throws IOException, NoSuchAlgorithmException, KeyManagementException, TimeoutException {
        when(time.now()).thenReturn(createTimestamp.get());
        AmqpConnectionFactory factory = initiallyProvides(provides.whichIs(creating(channel)));

        assertNotNull(factory.getConnection());
        assertTrue("connection should be able to open", factory.canConnectionOpen());
        assertTrue("connection be open", factory.isConnectionHealthy());
        assertEquals(createTimestamp.get(), factory.getConnectionStarted());
    }

    @Test
    public void connectionCreatesProvidedChannel()
        throws IOException, NoSuchAlgorithmException, KeyManagementException, TimeoutException {
        AmqpConnectionFactory factory = initiallyProvides(provides.whichIs(creating(channel)));

        assertSame(channel, factory.getConnection().createChannel());
    }

    @Test
    public void shouldClose()
        throws IOException, NoSuchAlgorithmException, KeyManagementException, TimeoutException {
        AmqpConnectionFactory factory = initiallyProvides(provides.whichIs(creating(channel)));
        factory.getConnection();

        factory.close(false);

        assertFalse("connection should be closed", factory.isConnectionHealthy());
        assertEquals(0, factory.getConnectionStarted());
    }

    @Test
    public void shouldThrowRuntimeExceptionOnCloseIfRequestedSo()
        throws IOException, NoSuchAlgorithmException, KeyManagementException, TimeoutException {
        AmqpConnectionFactoryFake factory = initiallyProvides(provides.whichIs(creating(channel)))
            .onCloseThrowing(new IllegalArgumentException());
        factory.getConnection();

        assertThatThrownBy(() -> factory.close(false)).isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    public void shouldThrowIoExceptionOnCloseIfRequestedSo()
        throws IOException, NoSuchAlgorithmException, KeyManagementException, TimeoutException {
        AmqpConnectionFactoryFake factory = initiallyProvides(provides.whichIs(creating(channel)))
            .onCloseThrowing(new IOException());
        factory.getConnection();

        assertThatThrownBy(() -> factory.close(false)).isInstanceOf(IOException.class);
    }
}
