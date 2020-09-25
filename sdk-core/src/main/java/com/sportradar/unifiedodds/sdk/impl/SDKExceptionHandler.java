/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */

package com.sportradar.unifiedodds.sdk.impl;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.Consumer;
import com.rabbitmq.client.TopologyRecoveryException;
import com.rabbitmq.client.impl.DefaultExceptionHandler;
import com.sportradar.unifiedodds.sdk.SDKConnectionStatusListener;

import static com.google.common.base.Preconditions.checkNotNull;

class SDKExceptionHandler extends DefaultExceptionHandler {
    private final SDKConnectionStatusListener connectionStatusListener;

    public SDKExceptionHandler(SDKConnectionStatusListener connectionStatusListener) {
        checkNotNull(connectionStatusListener, "connectionStatusListener cannot be a null reference");

        this.connectionStatusListener = connectionStatusListener;
    }

    @Override
    public void handleUnexpectedConnectionDriverException(Connection connection, Throwable throwable) {
        super.handleUnexpectedConnectionDriverException(connection, throwable);
        dispatchException(throwable);
    }

    @Override
    public void handleReturnListenerException(Channel channel, Throwable throwable) {
        super.handleReturnListenerException(channel, throwable);
        dispatchException(throwable);
    }

    @Override
    public void handleFlowListenerException(Channel channel, Throwable throwable) {
        super.handleFlowListenerException(channel, throwable);
        dispatchException(throwable);
    }

    @Override
    public void handleConfirmListenerException(Channel channel, Throwable throwable) {
        super.handleConfirmListenerException(channel, throwable);
        dispatchException(throwable);
    }

    @Override
    public void handleBlockedListenerException(Connection connection, Throwable throwable) {
        super.handleBlockedListenerException(connection, throwable);
        dispatchException(throwable);
    }

    @Override
    public void handleConsumerException(Channel channel, Throwable throwable, Consumer consumer, String s, String s1) {
        super.handleConsumerException(channel, throwable, consumer, s, s1);
        dispatchException(throwable);
    }

    @Override
    public void handleConnectionRecoveryException(Connection connection, Throwable throwable) {
        super.handleConnectionRecoveryException(connection, throwable);
        dispatchException(throwable);
    }

    @Override
    public void handleChannelRecoveryException(Channel channel, Throwable throwable) {
        super.handleChannelRecoveryException(channel, throwable);
        dispatchException(throwable);
    }

    @Override
    public void handleTopologyRecoveryException(Connection connection, Channel channel, TopologyRecoveryException e) {
        super.handleTopologyRecoveryException(connection, channel, e);
        dispatchException(e);
    }

    private void dispatchException(Throwable throwable) {
        connectionStatusListener.onConnectionException(throwable);
    }
}
