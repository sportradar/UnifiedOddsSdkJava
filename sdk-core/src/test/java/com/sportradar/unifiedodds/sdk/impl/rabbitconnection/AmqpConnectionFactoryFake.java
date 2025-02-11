/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */
package com.sportradar.unifiedodds.sdk.impl.rabbitconnection;

import static com.sportradar.unifiedodds.sdk.impl.rabbitconnection.ConnectionToBeProvided.ConnectionHealth.HEALTHY;
import static com.sportradar.unifiedodds.sdk.testutil.generic.concurrent.WaiterForEvents.WaitingStatus.EVENT_NOT_HAPPENED;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.Mockito.mock;

import com.rabbitmq.client.AlreadyClosedException;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ShutdownSignalException;
import com.sportradar.unifiedodds.sdk.impl.rabbitconnection.ConnectionToBeProvided.ConnectionAbsent;
import com.sportradar.unifiedodds.sdk.impl.rabbitconnection.ConnectionToBeProvided.ConnectionPresent;
import com.sportradar.unifiedodds.sdk.internal.impl.rabbitconnection.AmqpConnectionFactory;
import com.sportradar.unifiedodds.sdk.testutil.generic.concurrent.WaiterForEvents;
import java.io.IOException;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.util.*;
import java.util.concurrent.TimeoutException;

public class AmqpConnectionFactoryFake implements AmqpConnectionFactory {

    private Optional<RuntimeException> runtimeExceptionOnClose = Optional.empty();
    private Optional<IOException> ioExceptionOnClose = Optional.empty();
    private ConnectionToBeProvided currentConnection = new ConnectionAbsent();
    private ConnectionToBeProvided afterCurrentConnectionStopping = new ConnectionAbsent();
    private WaiterForEvents alreadyClosedConnectionIsClosed = WaiterForEvents.createWaiterForEvents();

    private AmqpConnectionFactoryFake(ConnectionToBeProvided connection) {
        afterCurrentConnectionStopping = connection;
    }

    public AmqpConnectionFactoryFake afterClosingStarts(ConnectionToBeProvided nextConnection) {
        if (afterCurrentConnectionStopping instanceof ConnectionPresent) {
            throw new IllegalArgumentException("next connection to be opened is already arranged");
        } else {
            afterCurrentConnectionStopping = nextConnection;
        }
        return this;
    }

    @Override
    public Connection getConnection()
        throws IOException, TimeoutException, NoSuchAlgorithmException, KeyManagementException {
        rotatePlannedConnectionsIfThereIsNoCurrentConnection();
        if (currentConnection instanceof ConnectionPresent) {
            return ((ConnectionPresent) currentConnection).getConnection();
        } else {
            return null;
        }
    }

    private void rotatePlannedConnectionsIfThereIsNoCurrentConnection() {
        if (currentConnection instanceof ConnectionAbsent) {
            currentConnection = afterCurrentConnectionStopping;
            afterCurrentConnectionStopping = new ConnectionAbsent();
        }
    }

    @Override
    public void close(boolean feedStopped) throws IOException {
        if (currentConnection instanceof ConnectionAbsent) {
            alreadyClosedConnectionIsClosed.markEventHappened();
            throw new AlreadyClosedException(mock(ShutdownSignalException.class));
        }
        assertConnectionIsNotClosed();
        assertSdkLetsConnectionToAutoRecover();
        if (runtimeExceptionOnClose.isPresent()) {
            throw runtimeExceptionOnClose.get();
        } else if (ioExceptionOnClose.isPresent()) {
            throw ioExceptionOnClose.get();
        }
        currentConnection = new ConnectionAbsent();
    }

    private void assertConnectionIsNotClosed() {
        assertThat(currentConnection).isInstanceOf(ConnectionPresent.class);
    }

    private void assertSdkLetsConnectionToAutoRecover() {
        assertThat(currentConnection).isInstanceOf(ConnectionPresent.class);
        assertThat(((ConnectionPresent) currentConnection).getHealth()).isEqualTo(HEALTHY);
    }

    @Override
    public boolean isConnectionHealthy() {
        boolean isConnectionHealthy =
            currentConnection instanceof ConnectionPresent &&
            ((ConnectionPresent) currentConnection).getHealth() == HEALTHY;
        return isConnectionHealthy;
    }

    public boolean hasConnection() {
        return currentConnection instanceof ConnectionPresent;
    }

    @Override
    public long getConnectionStarted() {
        if (currentConnection instanceof ConnectionPresent) {
            return ((ConnectionPresent) currentConnection).getCreatedAt();
        } else {
            return 0;
        }
    }

    @Override
    public boolean canConnectionOpen() {
        return true;
    }

    public AmqpConnectionFactoryFake onCloseThrowing(RuntimeException t) {
        runtimeExceptionOnClose = Optional.of(t);
        return this;
    }

    public AmqpConnectionFactoryFake onCloseThrowing(IOException t) {
        ioExceptionOnClose = Optional.of(t);
        return this;
    }

    public static AmqpConnectionFactoryFake initiallyProvides(ConnectionToBeProvided connection) {
        return new AmqpConnectionFactoryFake(connection);
    }

    public void assertThatAlreadyClosedConnectionWasNotClosedAgain() {
        assertThat(alreadyClosedConnectionIsClosed.getWaitingStatus()).isEqualTo(EVENT_NOT_HAPPENED);
    }
}
