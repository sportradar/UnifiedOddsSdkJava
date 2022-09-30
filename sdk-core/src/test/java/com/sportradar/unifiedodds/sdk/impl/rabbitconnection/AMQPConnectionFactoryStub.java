package com.sportradar.unifiedodds.sdk.impl.rabbitconnection;

import com.rabbitmq.client.Connection;

import java.io.IOException;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.util.concurrent.TimeoutException;

public class AMQPConnectionFactoryStub {
    public static AMQPConnectionFactory holdingConnectionCreatedAt(long epochMillis, Connection connection) {
        return new AMQPConnectionFactory() {
            @Override
            public Connection getConnection() throws IOException, TimeoutException, NoSuchAlgorithmException, KeyManagementException {
                return connection;
            }

            @Override
            public void close(boolean feedStopped) throws IOException {

            }

            @Override
            public boolean isConnectionOpen() {
                return false;
            }

            @Override
            public long getConnectionStarted() {
                return epochMillis;
            }

            @Override
            public boolean canConnectionOpen() {
                return true;
            }
        };
    }
}
