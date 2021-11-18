/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */

package com.sportradar.unifiedodds.sdk.impl;

import com.rabbitmq.client.Connection;

import java.io.IOException;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.util.concurrent.TimeoutException;

/**
 * Represents a factory used to create {@link Connection} instances
 */
public interface AMQPConnectionFactory {
    /**
     * Returns a {@link Connection} instance representing connection to the AMQP broker
     * 
     * @return a {@link Connection} instance representing connection to the AMQP broker
     * @throws IOException An error occurred while creating the connection instance
     * @throws TimeoutException An error occurred while creating the connection instance
     * @throws NoSuchAlgorithmException An error occurred while configuring the factory to use SSL
     * @throws KeyManagementException An error occurred while configuring the factory to use SSL
     */
    Connection getConnection() throws IOException, TimeoutException, NoSuchAlgorithmException, KeyManagementException;

    /**
     * Close the AMQP connection
     *
     * @throws IOException if the connection couldn't be terminated
     */
    void close() throws IOException;

    /**
     * Check if the connection is currently alive
     *
     * @return the status of the connection
     */
    boolean isConnectionOpen();

    /**
     * Get the timestamp when the connection started
     * @return the timestamp when the connection started
     */
    long getConnectionStarted();
}
