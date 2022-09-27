/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */

package com.sportradar.unifiedodds.sdk.impl.rabbitconnection;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.Consumer;
import com.rabbitmq.client.TopologyRecoveryException;
import com.rabbitmq.client.impl.DefaultExceptionHandler;
import com.sportradar.unifiedodds.sdk.SDKConnectionStatusListener;
import com.sportradar.utils.SdkHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static com.google.common.base.Preconditions.checkNotNull;

class SDKExceptionHandler extends DefaultExceptionHandler {
    private static final Logger logger = LoggerFactory.getLogger(SDKExceptionHandler.class);

    private final SDKConnectionStatusListener connectionStatusListener;
    private final String orgToken;
    private final String cleanToken;

    public SDKExceptionHandler(SDKConnectionStatusListener connectionStatusListener, String token) {
        checkNotNull(connectionStatusListener, "connectionStatusListener cannot be a null reference");

        this.connectionStatusListener = connectionStatusListener;
        this.orgToken = token;
        this.cleanToken = SdkHelper.obfuscate(token);
    }

    @Override
    public void handleUnexpectedConnectionDriverException(Connection connection, Throwable throwable) {
        String connectionName = connection.toString().replace(orgToken, cleanToken);
        logger.error("Unexpected connection driver exception for connection {}", connectionName, throwable);
        super.handleUnexpectedConnectionDriverException(connection, throwable);
        dispatchException(throwable);
    }

    @Override
    public void handleReturnListenerException(Channel channel, Throwable throwable) {
        logger.error("Return listener exception for channel {}", channel, throwable);
        super.handleReturnListenerException(channel, throwable);
        dispatchException(throwable);
    }

    @Override
    public void handleFlowListenerException(Channel channel, Throwable throwable) {
        logger.error("Flow listener exception for channel {}", channel, throwable);
        super.handleFlowListenerException(channel, throwable);
        dispatchException(throwable);
    }

    @Override
    public void handleConfirmListenerException(Channel channel, Throwable throwable) {
        logger.error("Confirm listener exception for channel {}", channel, throwable);
        super.handleConfirmListenerException(channel, throwable);
        dispatchException(throwable);
    }

    @Override
    public void handleBlockedListenerException(Connection connection, Throwable throwable) {
        String connectionName = connection.toString().replace(orgToken, cleanToken);
        logger.error("Blocked listener exception for connection {}", connectionName, throwable);
        super.handleBlockedListenerException(connection, throwable);
        dispatchException(throwable);
    }

    @Override
    public void handleConsumerException(Channel channel, Throwable throwable, Consumer consumer, String s, String s1) {
        logger.error("Consumer exception for channel {}, consumer tag {}, method {}", channel, s, s1, throwable);
        super.handleConsumerException(channel, throwable, consumer, s, s1);
        dispatchException(throwable);
    }

    @Override
    public void handleConnectionRecoveryException(Connection connection, Throwable throwable) {
        String connectionName = connection.toString().replace(orgToken, cleanToken);
        logger.error("Connection recovery exception for connection {}", connectionName, throwable);
        super.handleConnectionRecoveryException(connection, throwable);
        dispatchException(throwable);
    }

    @Override
    public void handleChannelRecoveryException(Channel channel, Throwable throwable) {
        logger.error("Channel recovery exception for channel {}", channel, throwable);
        super.handleChannelRecoveryException(channel, throwable);
        dispatchException(throwable);
    }

    @Override
    public void handleTopologyRecoveryException(Connection connection, Channel channel, TopologyRecoveryException e) {
        String connectionName = connection.toString().replace(orgToken, cleanToken);
        logger.error("Topology recovery exception for connection {}, channel {}", connectionName, channel, e);
        super.handleTopologyRecoveryException(connection, channel, e);
        dispatchException(e);
    }

    private void dispatchException(Throwable throwable) {
        connectionStatusListener.onConnectionException(throwable);
    }
}
