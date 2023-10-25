/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */

package com.sportradar.unifiedodds.sdk.impl.rabbitconnection;

import static com.google.common.base.Preconditions.checkNotNull;

import com.rabbitmq.client.*;
import com.sportradar.unifiedodds.sdk.SdkConnectionStatusListener;
import com.sportradar.unifiedodds.sdk.SdkInternalConfiguration;
import com.sportradar.unifiedodds.sdk.impl.TimeUtils;
import com.sportradar.unifiedodds.sdk.impl.apireaders.WhoAmIReader;
import java.io.IOException;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.util.concurrent.TimeoutException;
import javax.inject.Inject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;

/**
 * A {@link AmqpConnectionFactory} implementation which creates only one connection instance. All
 * subsequent calls to {@code newConnection()} method return instance created by the first call.
 */
@SuppressWarnings({ "ClassFanOutComplexity", "HiddenField", "IllegalCatch" })
public class SingleInstanceAmqpConnectionFactory implements AmqpConnectionFactory {

    /**
     * A {@link Logger} instance used for execution logging
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(SingleInstanceAmqpConnectionFactory.class);

    private String sslVersion;

    /**
     * A {@link SdkInternalConfiguration} instance representing odds feed configuration
     */
    private final SdkInternalConfiguration config;

    /**
     * A {@link ConnectionFactory} instance used to create the {@link Connection} instance
     */
    private final ConfiguredConnectionFactory configuredConnectionFactory;

    /**
     * A {@link SdkConnectionStatusListener} used to notify the outside world when the connection is
     * closed
     */
    private final SdkConnectionStatusListener connectionStatusListener;

    /**
     * Instance used to fetch data about the client token
     */
    private final WhoAmIReader whoAmIReader;

    /**
     * A {@link Connection} instance representing connection to the Rabbit MQ
     */
    private Connection connection;

    /**
     * A {@link ShutdownListener} used to detect when the connection gets closed
     */
    private final ShutdownListener shutdownListener;

    /**
     * A {@link BlockedListener} used to detect when the connection gets blocked
     */
    private final BlockedListener blockedListener;

    /**
     * When the connection was started
     */
    private long connectionStarted;

    /**
     * An {@link Object} used for thread safety
     */
    private final Object syncLock = new Object();

    /**
     * Can the feed connection be opened
     */
    private boolean canFeedBeOpened;
    private final FirewallChecker firewallChecker;
    private final TimeUtils timeUtils;

    private final SslProtocolsProvider sslProtocolsProvider;

    /**
     * Initializes a new instance of the {@link SingleInstanceAmqpConnectionFactory} class
     *
     * @param configuredConnectionFactory A {@link ConfiguredConnectionFactory} instance used to create the
     *        {@link Connection} instance
     * @param config A {@link SdkInternalConfiguration} instance representing odds feed configuration
     * @param connectionStatusListener A {@link SdkConnectionStatusListener} used to notify the
     *        outside world when the connection is closed
     */
    @Inject
    @SuppressWarnings("ParameterNumber")
    public SingleInstanceAmqpConnectionFactory(
        ConfiguredConnectionFactory configuredConnectionFactory,
        SdkInternalConfiguration config,
        SdkConnectionStatusListener connectionStatusListener,
        WhoAmIReader whoAmIReader,
        FirewallChecker firewallChecker,
        TimeUtils timeUtils,
        SslProtocolsProvider sslProtocolsProvider
    ) {
        checkNotNull(configuredConnectionFactory, "rabbitMqConnectionFactory cannot be a null reference");
        checkNotNull(config, "config cannot be a null reference");
        checkNotNull(connectionStatusListener, "connectionStatusListener cannot be a null reference");
        checkNotNull(firewallChecker, "firewallChecker");
        checkNotNull(timeUtils, "timeUtils");
        checkNotNull(sslProtocolsProvider, "sslProtocolsProvider");

        this.configuredConnectionFactory = configuredConnectionFactory;
        this.config = config;
        this.connectionStatusListener = connectionStatusListener;
        this.whoAmIReader = whoAmIReader;
        this.firewallChecker = firewallChecker;
        this.timeUtils = timeUtils;
        this.shutdownListener = new ShutdownListenerImpl(this);
        this.blockedListener = new BlockedListenerImpl(this);
        this.connectionStarted = 0;
        this.canFeedBeOpened = true;
        this.sslProtocolsProvider = sslProtocolsProvider;
    }

    /**
     * Creates and returns a {@link SdkConnection} instance
     *
     * @return the created {@link SdkConnection} instance
     */
    private SdkConnection createSdkConnection()
        throws IOException, TimeoutException, NoSuchAlgorithmException, KeyManagementException {
        firewallChecker.checkFirewall(config.getApiHost());
        LOGGER.info("Creating new SDKConnection for {}", config.getMessagingHost());
        Connection actualConnection = null;
        if (config.getUseMessagingSsl()) {
            if (sslVersion != null) {
                actualConnection = newConnectionInternal(whoAmIReader, sslVersion);
            } else {
                for (String testSslVersion : sslProtocolsProvider.provideSupportedPrioritised()) {
                    try {
                        actualConnection = newConnectionInternal(whoAmIReader, testSslVersion);
                        sslVersion = testSslVersion;
                        break;
                    } catch (Exception ex) {
                        LOGGER.debug(
                            "Error creating connection for SSL version {}. Exception={}",
                            testSslVersion,
                            ex.getMessage()
                        );
                    }
                }
            }
        } else {
            actualConnection = newConnectionInternal(whoAmIReader, null);
        }
        if (actualConnection == null) {
            return null;
        }
        SdkConnection sdkConnection = new SdkConnection(actualConnection);
        sdkConnection.addShutdownListener(shutdownListener);
        sdkConnection.addBlockedListener(blockedListener);
        return sdkConnection;
    }

    Connection newConnectionInternal(WhoAmIReader whoAmIReader, String sslVersion)
        throws KeyManagementException, NoSuchAlgorithmException, IOException, TimeoutException {
        final Connection con = configuredConnectionFactory.createConfiguredConnection(
            whoAmIReader,
            sslVersion
        );
        connectionStarted = timeUtils.now();
        LOGGER.info("Connection created successfully");
        return con;
    }

    /**
     * Returns a {@link Connection} instance representing connection to the AMQP broker
     *
     * @return a {@link Connection} instance representing connection to the AMQP broker
     */
    @Override
    public synchronized Connection getConnection() {
        synchronized (syncLock) {
            if (!canFeedBeOpened) {
                LOGGER.warn("Connection should be closed.");
                return null;
            }
            try {
                if (this.connection == null) {
                    SdkConnection sdkConnection = this.createSdkConnection();
                    this.connection = sdkConnection;
                }
            } catch (Exception exc) {
                LOGGER.error("Error creating connection: " + exc.getMessage(), exc);
            }
            return this.connection;
        }
    }

    /**
     * Close the AMQP connection
     * @param feedStopped indication if feed was stopped
     * @throws IOException if the connection couldn't be terminated
     */
    @Override
    public synchronized void close(boolean feedStopped) throws IOException {
        LOGGER.info("Closing underlying AMQP connection");
        if (feedStopped) {
            canFeedBeOpened = false;
        }
        try {
            if (this.connection != null) {
                this.connection.close();
            }
        } catch (IOException ex) {
            LOGGER.error("Error closing connection: ", ex);
        } finally {
            this.connection = null;
            connectionStarted = 0;
            LOGGER.info("Connection close executed");
        }
    }

    @Override
    public synchronized boolean isConnectionOpen() {
        return this.connection != null && this.connection.isOpen();
    }

    /**
     * Get the timestamp when the connection started
     *
     * @return the timestamp when the connection started
     */
    @Override
    public long getConnectionStarted() {
        return connectionStarted;
    }

    /**
     * Check if the connection can or should be opened
     *
     * @return value indicating if the connection can or should be opened
     */
    @Override
    public boolean canConnectionOpen() {
        return canFeedBeOpened;
    }

    private synchronized void handleShutdown(ShutdownSignalException cause) {
        try {
            MDC.setContextMap(whoAmIReader.getAssociatedSdkMdcContextMap());
            try {
                this.connectionStatusListener.onConnectionDown();
            } catch (Exception e) {
                LOGGER.warn("Problems dispatching onConnectionDown()", e);
            }

            if (cause.isInitiatedByApplication()) {
                LOGGER.info("Existing AMQP connection has been shut-down. [initiated by application]");
            } else {
                LOGGER.warn("Existing AMQP connection has been shut-down. Ex:", cause);
            }
        } finally {
            MDC.clear();
        }
    }

    private synchronized void handleBlocked(String reason, boolean blocked) {
        try {
            MDC.setContextMap(whoAmIReader.getAssociatedSdkMdcContextMap());
            try {
                this.connectionStatusListener.onConnectionDown();
            } catch (Exception e) {
                LOGGER.warn("Problems dispatching onConnectionDown()", e);
            }

            if (blocked) {
                LOGGER.warn("Existing AMQP connection has been blocked. " + reason);
            } else {
                LOGGER.info("Existing AMQP connection has been unblocked.");
            }
        } finally {
            MDC.clear();
        }
    }

    private static final class ShutdownListenerImpl implements ShutdownListener {

        private final SingleInstanceAmqpConnectionFactory parent;

        private ShutdownListenerImpl(final SingleInstanceAmqpConnectionFactory parent) {
            this.parent = parent;
        }

        @Override
        public void shutdownCompleted(ShutdownSignalException cause) {
            this.parent.handleShutdown(cause);
        }
    }

    private static final class BlockedListenerImpl implements BlockedListener {

        private final SingleInstanceAmqpConnectionFactory parent;

        private BlockedListenerImpl(final SingleInstanceAmqpConnectionFactory parent) {
            this.parent = parent;
        }

        @Override
        public void handleBlocked(String s) {
            this.parent.handleBlocked(s, true);
        }

        @Override
        public void handleUnblocked() throws IOException {
            this.parent.handleBlocked(null, false);
        }
    }
}
