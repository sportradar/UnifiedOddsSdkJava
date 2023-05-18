/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */
package com.sportradar.unifiedodds.sdk.impl.rabbitconnection;

import com.rabbitmq.client.Connection;
import java.io.IOException;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.util.Optional;
import java.util.concurrent.TimeoutException;

public interface AmqpConnectionFactoryStubs extends AMQPConnectionFactory {
    public AmqpConnectionFactoryStubs onCloseThrowing(RuntimeException t);

    public AmqpConnectionFactoryStubs onCloseThrowing(IOException t);

    public static AmqpConnectionFactoryStubs holdingConnectionCreatedAt(
        long epochMillis,
        Connection connection
    ) {
        return new AmqpConnectionFactoryStubsInstance(epochMillis, connection);
    }

    public static class AmqpConnectionFactoryStubsInstance implements AmqpConnectionFactoryStubs {

        private boolean isClosed;
        private Optional<RuntimeException> runtimeExceptionOnClose = Optional.empty();
        private Optional<IOException> ioExceptionOnClose = Optional.empty();
        private long epochMillis;
        private Connection connection;

        public AmqpConnectionFactoryStubsInstance(long epochMillis, Connection connection) {
            this.epochMillis = epochMillis;
            this.connection = connection;
        }

        @Override
        public Connection getConnection()
            throws IOException, TimeoutException, NoSuchAlgorithmException, KeyManagementException {
            return connection;
        }

        @Override
        public void close(boolean feedStopped) throws IOException {
            if (runtimeExceptionOnClose.isPresent()) {
                throw runtimeExceptionOnClose.get();
            } else if (ioExceptionOnClose.isPresent()) {
                throw ioExceptionOnClose.get();
            }
            isClosed = true;
        }

        @Override
        public boolean isConnectionOpen() {
            return !isClosed;
        }

        @Override
        public long getConnectionStarted() {
            if (isClosed) {
                return 0;
            } else {
                return epochMillis;
            }
        }

        @Override
        public boolean canConnectionOpen() {
            return true;
        }

        @Override
        public AmqpConnectionFactoryStubs onCloseThrowing(RuntimeException t) {
            runtimeExceptionOnClose = Optional.of(t);
            return this;
        }

        @Override
        public AmqpConnectionFactoryStubs onCloseThrowing(IOException t) {
            ioExceptionOnClose = Optional.of(t);
            return this;
        }
    }
}
