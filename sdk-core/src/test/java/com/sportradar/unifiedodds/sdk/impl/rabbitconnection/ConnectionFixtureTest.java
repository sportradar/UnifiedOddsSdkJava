/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */
package com.sportradar.unifiedodds.sdk.impl.rabbitconnection;

import static org.assertj.core.api.Assertions.assertThatNoException;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

import com.rabbitmq.client.BlockedListener;
import com.rabbitmq.client.ShutdownListener;
import com.rabbitmq.client.ShutdownSignalException;
import java.io.IOException;
import lombok.val;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

public class ConnectionFixtureTest {

    private final ConnectionFixture connection = new ConnectionFixture.Holder().get();

    public ConnectionFixtureTest() throws IOException {}

    @Test
    public void connectionShouldBeOpenInitially() {
        assertTrue(connection.isOpen());
    }

    @Test
    public void rabbitInitiatingConnectionClosureShouldIndicateThatClosureIsNotInitiatedByTheApplication() {
        final val shutdownListener = mock(ShutdownListener.class);
        connection.addShutdownListener(shutdownListener);

        connection.getControlApi().closeInitiatedByRabbitMq();

        final val captor = ArgumentCaptor.forClass(ShutdownSignalException.class);
        verify(shutdownListener).shutdownCompleted(captor.capture());
        assertFalse(captor.getValue().isInitiatedByApplication());
    }

    @Test
    public void connectionShouldNotBeOpenIfItWasClosedByRabbitMq() {
        connection.getControlApi().closeInitiatedByRabbitMq();

        assertFalse(connection.isOpen());
    }

    @Test
    public void applicationInitiatingConnectionClosureShouldIndicateThatClosureIsInitiatedByTheApplication()
        throws IOException {
        final val shutdownListener = mock(ShutdownListener.class);
        connection.addShutdownListener(shutdownListener);

        connection.close();

        final val captor = ArgumentCaptor.forClass(ShutdownSignalException.class);
        verify(shutdownListener).shutdownCompleted(captor.capture());
        assertTrue(captor.getValue().isInitiatedByApplication());
    }

    @Test
    public void connectionShouldNotBeOpenIfItWasClosedByApplication() throws IOException {
        connection.close();

        assertFalse(connection.isOpen());
    }

    @Test
    public void closingConnectionWithoutRegisteredShutdownListenerShouldSucceed() {
        connection.getControlApi().closeInitiatedByRabbitMq();

        assertFalse(connection.isOpen());
    }

    @Test
    public void blockingConnectionShouldResultInCallingRegisteredListener() throws IOException {
        final val blockingListener = mock(BlockedListener.class);
        connection.addBlockedListener(blockingListener);
        final val reason = "congestion";

        connection.getControlApi().blockDueTo(reason);

        verify(blockingListener).handleBlocked(reason);
    }

    @Test
    public void blockingConnectionWithoutRegisteredListenerShouldSucceed() throws IOException {
        assertThatNoException()
            .isThrownBy(() -> {
                connection.getControlApi().blockDueTo("reason");
            });
    }

    @Test
    public void unblockingConnectionShouldResultInCallingRegisteredListener() throws IOException {
        final val blockingListener = mock(BlockedListener.class);
        connection.addBlockedListener(blockingListener);

        connection.getControlApi().unblock();

        verify(blockingListener).handleUnblocked();
    }
}
