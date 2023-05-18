/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */

package com.sportradar.unifiedodds.sdk.impl.rabbitconnection;

import static com.google.common.base.Preconditions.checkNotNull;

import com.rabbitmq.client.*;
import java.io.IOException;
import java.net.InetAddress;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A wrapper around RabbitMQ {@link Connection} which automatically closes the connection when there
 * are no more opened channels channels
 */
@SuppressWarnings({ "AbbreviationAsWordInName", "ConstantName" })
public class SDKConnection implements Connection {

    /**
     * A {@link Logger} instance used for logging
     */
    private static final Logger logger = LoggerFactory.getLogger(SDKConnection.class);

    /**
     * A {@link Connection} instance wrapped with current {@link SDKConnection} instance
     */
    private final Connection actualConnection;

    /**
     * Specifies the number of opened {@link Channel} instances
     */
    private int channelCount;

    /**
     * An {@link Object} used for thread safety
     */
    private final Object syncLock = new Object();

    /**
     * Initializes a new instance of the {@link SDKConnection} class
     *
     * @param actualConnection A {@link Connection} instance wrapped with current
     *        {@link SDKConnection} instance
     */
    public SDKConnection(Connection actualConnection) {
        checkNotNull(actualConnection, "actualConnection cannot be a null reference");

        this.actualConnection = actualConnection;
    }

    /**
     * Attaches the listener to the provided {@link Channel} and increases the active channel count
     *
     * @param channel A {@link Channel} instance to which to attach the listener
     */
    private void postChannelCreated(Channel channel) {
        channelCount = channelCount + 1;
        logger.debug("Channel created. New channel count is {}", channelCount);
    }

    @Override
    public InetAddress getAddress() {
        return actualConnection.getAddress();
    }

    @Override
    public int getPort() {
        return actualConnection.getPort();
    }

    @Override
    public int getChannelMax() {
        return actualConnection.getChannelMax();
    }

    @Override
    public int getFrameMax() {
        return actualConnection.getFrameMax();
    }

    @Override
    public int getHeartbeat() {
        return actualConnection.getHeartbeat();
    }

    @Override
    public Map<String, Object> getClientProperties() {
        return actualConnection.getClientProperties();
    }

    @Override
    public Map<String, Object> getServerProperties() {
        return actualConnection.getServerProperties();
    }

    @Override
    public Channel createChannel() throws IOException {
        synchronized (syncLock) {
            Channel channel = actualConnection.createChannel();
            postChannelCreated(channel);
            return channel;
        }
    }

    @Override
    public Channel createChannel(int i) throws IOException {
        synchronized (syncLock) {
            Channel channel = actualConnection.createChannel(i);
            postChannelCreated(channel);
            return channel;
        }
    }

    @Override
    public void close() throws IOException {
        actualConnection.close();
    }

    @Override
    public void close(int i, String s) throws IOException {
        throw new UnsupportedOperationException("Only SDKConnection.close() is supported");
    }

    @Override
    public void close(int i) throws IOException {
        throw new UnsupportedOperationException("Only SDKConnection.close() is supported");
    }

    @Override
    public void close(int i, String s, int i2) throws IOException {
        throw new UnsupportedOperationException("Only SDKConnection.close() is supported");
    }

    @Override
    public void abort() {
        actualConnection.abort();
    }

    @Override
    public void abort(int i, String s) {
        actualConnection.abort(i, s);
    }

    @Override
    public void abort(int i) {
        actualConnection.abort(i);
    }

    @Override
    public void abort(int i, String s, int i2) {
        actualConnection.abort(i, s, i2);
    }

    @Override
    public void addBlockedListener(BlockedListener blockedListener) {
        actualConnection.addBlockedListener(blockedListener);
    }

    @Override
    public boolean removeBlockedListener(BlockedListener blockedListener) {
        return false; // To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void clearBlockedListeners() {
        actualConnection.clearBlockedListeners();
    }

    @Override
    public ExceptionHandler getExceptionHandler() {
        return actualConnection.getExceptionHandler();
    }

    @Override
    public void addShutdownListener(ShutdownListener shutdownListener) {
        actualConnection.addShutdownListener(shutdownListener);
    }

    @Override
    public void removeShutdownListener(ShutdownListener shutdownListener) {
        actualConnection.removeShutdownListener(shutdownListener);
    }

    @Override
    public ShutdownSignalException getCloseReason() {
        return actualConnection.getCloseReason();
    }

    @Override
    public void notifyListeners() {
        actualConnection.notifyListeners();
    }

    @Override
    public boolean isOpen() {
        return actualConnection.isOpen();
    }
}
