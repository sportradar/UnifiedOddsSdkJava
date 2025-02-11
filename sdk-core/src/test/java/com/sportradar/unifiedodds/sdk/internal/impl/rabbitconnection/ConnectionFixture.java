/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */
package com.sportradar.unifiedodds.sdk.internal.impl.rabbitconnection;

import static org.mockito.Mockito.*;

import com.rabbitmq.client.BlockedListener;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ShutdownListener;
import com.rabbitmq.client.ShutdownSignalException;
import java.io.IOException;
import lombok.val;

public interface ConnectionFixture extends Connection {
    public ControlApi getControlApi();

    public static class Holder {

        private ConnectionFixture connectionFixture;

        private ShutdownListener shutdownListener = mock(ShutdownListener.class);
        private BlockedListener blockedListener = mock(BlockedListener.class);
        private boolean isOpened = true;

        public Holder() throws IOException {
            connectionFixture = mock(ConnectionFixture.class);
            recordListenersOnRegistering();
            callListenerOnClose();
            indicateConnectionIsOpenInitially();
            defineControlApi();
        }

        private void indicateConnectionIsOpenInitially() {
            when(connectionFixture.isOpen()).thenAnswer(i -> isOpened);
        }

        private void callListenerOnClose() throws IOException {
            final val shutdownSignal = mock(ShutdownSignalException.class);
            when(shutdownSignal.isInitiatedByApplication()).thenReturn(true);
            doAnswer(invocation -> {
                    shutdownListener.shutdownCompleted(shutdownSignal);
                    isOpened = false;
                    return null;
                })
                .when(connectionFixture)
                .close();
        }

        public ConnectionFixture get() {
            return connectionFixture;
        }

        private void recordListenersOnRegistering() {
            doAnswer(invocation -> shutdownListener = invocation.getArgument(0, ShutdownListener.class))
                .when(connectionFixture)
                .addShutdownListener(any());
            doAnswer(invocation -> blockedListener = invocation.getArgument(0, BlockedListener.class))
                .when(connectionFixture)
                .addBlockedListener(any());
        }

        private void defineControlApi() {
            when(connectionFixture.getControlApi()).thenReturn(new ControlApi(Holder.this));
        }
    }

    public class ControlApi {

        private Holder holder;

        private ControlApi(Holder holder) {
            this.holder = holder;
        }

        public void closeInitiatedByRabbitMq() {
            holder.shutdownListener.shutdownCompleted(mock(ShutdownSignalException.class));
            holder.isOpened = false;
        }

        public void blockDueTo(final String reason) throws IOException {
            holder.blockedListener.handleBlocked(reason);
        }

        public void unblock() throws IOException {
            holder.blockedListener.handleUnblocked();
        }
    }
}
