/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */
package com.sportradar.unifiedodds.sdk.impl.rabbitconnection;

import com.rabbitmq.client.*;
import java.io.IOException;
import java.net.InetAddress;
import java.util.Map;
import java.util.Optional;

public class ConnectionAllOperationsUnsupported implements Connection {

    @Override
    public InetAddress getAddress() {
        throw new UnsupportedOperationException();
    }

    @Override
    public int getPort() {
        throw new UnsupportedOperationException();
    }

    @Override
    public int getChannelMax() {
        throw new UnsupportedOperationException();
    }

    @Override
    public int getFrameMax() {
        throw new UnsupportedOperationException();
    }

    @Override
    public int getHeartbeat() {
        throw new UnsupportedOperationException();
    }

    @Override
    public Map<String, Object> getClientProperties() {
        throw new UnsupportedOperationException();
    }

    @Override
    public String getClientProvidedName() {
        throw new UnsupportedOperationException();
    }

    @Override
    public Map<String, Object> getServerProperties() {
        throw new UnsupportedOperationException();
    }

    @Override
    public Channel createChannel() throws IOException {
        throw new UnsupportedOperationException();
    }

    @Override
    public Channel createChannel(int i) throws IOException {
        throw new UnsupportedOperationException();
    }

    @Override
    public Optional<Channel> openChannel() throws IOException {
        throw new UnsupportedOperationException();
    }

    @Override
    public Optional<Channel> openChannel(int channelNumber) throws IOException {
        throw new UnsupportedOperationException();
    }

    @Override
    public void close() throws IOException {
        throw new UnsupportedOperationException();
    }

    @Override
    public void close(int i, String s) throws IOException {
        throw new UnsupportedOperationException();
    }

    @Override
    public void close(int i) throws IOException {
        throw new UnsupportedOperationException();
    }

    @Override
    public void close(int i, String s, int i1) throws IOException {
        throw new UnsupportedOperationException();
    }

    @Override
    public void abort() {
        throw new UnsupportedOperationException();
    }

    @Override
    public void abort(int i, String s) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void abort(int i) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void abort(int i, String s, int i1) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void addBlockedListener(BlockedListener blockedListener) {
        throw new UnsupportedOperationException();
    }

    @Override
    public BlockedListener addBlockedListener(
        BlockedCallback blockedCallback,
        UnblockedCallback unblockedCallback
    ) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean removeBlockedListener(BlockedListener blockedListener) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void clearBlockedListeners() {
        throw new UnsupportedOperationException();
    }

    @Override
    public ExceptionHandler getExceptionHandler() {
        throw new UnsupportedOperationException();
    }

    @Override
    public String getId() {
        throw new UnsupportedOperationException();
    }

    @Override
    public void setId(String s) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void addShutdownListener(ShutdownListener shutdownListener) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void removeShutdownListener(ShutdownListener shutdownListener) {
        throw new UnsupportedOperationException();
    }

    @Override
    public ShutdownSignalException getCloseReason() {
        throw new UnsupportedOperationException();
    }

    @Override
    public void notifyListeners() {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean isOpen() {
        throw new UnsupportedOperationException();
    }
}
