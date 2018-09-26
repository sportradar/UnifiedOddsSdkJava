/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */

package com.sportradar.unifiedodds.sdk.impl;

import com.google.common.base.Strings;
import com.google.common.util.concurrent.ThreadFactoryBuilder;
import com.google.inject.name.Named;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.ShutdownListener;
import com.rabbitmq.client.ShutdownSignalException;
import com.sportradar.unifiedodds.sdk.SDKConnectionStatusListener;
import com.sportradar.unifiedodds.sdk.SDKInternalConfiguration;
import com.sportradar.unifiedodds.sdk.impl.apireaders.WhoAmIReader;
import org.apache.http.HttpStatus;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.client.LaxRedirectStrategy;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;

import javax.inject.Inject;
import java.io.IOException;
import java.net.*;
import java.nio.charset.StandardCharsets;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.util.Map;
import java.util.concurrent.TimeoutException;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * A {@link AMQPConnectionFactory} implementation which creates only one connection instance. All
 * subsequent calls to {@code newConnection()} method return instance created by the first call.
 */
public class SingleInstanceAMQPConnectionFactory implements AMQPConnectionFactory {

    /**
     * A {@link Logger} instance used for execution logging
     */
    private static final Logger logger = LoggerFactory.getLogger(SingleInstanceAMQPConnectionFactory.class);

    /**
     * Specifies the AMQP broker virtual host prefix.
     */
    private static final String VIRTUAL_HOST_PREFIX = "/unifiedfeed/";

    private final String version;

    /**
     * A {@link SDKInternalConfiguration} instance representing odds feed configuration
     */
    private final SDKInternalConfiguration config;

    /**
     * A password used when establishing connection to the AMQP broker
     */
    private final String messagingPassword;

    /**
     * A {@link ConnectionFactory} instance used to create the {@link Connection} instance
     */
    private final ConnectionFactory rabbitConnectionFactory;

    /**
     * A {@link SDKConnectionStatusListener} used to notify the outside world when the connection is
     * closed
     */
    private final SDKConnectionStatusListener connectionStatusListener;

    /**
     * Instance used to fetch data about the client token
     */
    private final WhoAmIReader whoAmIReader;

    /**
     * An object used for thread synchronisation.
     */
    private final Object lock = new Object();

    /**
     * A {@link Connection} instance representing connection to the Rabbit MQ
     */
    private Connection connection;

    /**
     * A {@link ShutdownListener} used to detect when the connection gets closed
     */
    private final ShutdownListener shutdownListener = new ShutdownListenerImpl();

    /**
     * Initializes a new instance of the {@link SingleInstanceAMQPConnectionFactory} class
     * 
     * @param rabbitConnectionFactory A {@link ConnectionFactory} instance used to create the
     *        {@link Connection} instance
     * @param config A {@link SDKInternalConfiguration} instance representing odds feed configuration
     * @param connectionStatusListener A {@link SDKConnectionStatusListener} used to notify the
     *        outside world when the connection is closed
     */
    @Inject
    public SingleInstanceAMQPConnectionFactory(ConnectionFactory rabbitConnectionFactory, SDKInternalConfiguration config,
                                               SDKConnectionStatusListener connectionStatusListener,
                                               @Named("version") String version, WhoAmIReader whoAmIReader) {
        checkNotNull(rabbitConnectionFactory, "rabbitConnectionFactory cannot be a null reference");
        checkNotNull(config, "config cannot be a null reference");
        checkNotNull(connectionStatusListener, "connectionStatusListener cannot be a null reference");
        this.rabbitConnectionFactory = rabbitConnectionFactory;
        this.config = config;
        this.connectionStatusListener = connectionStatusListener;
        this.version = version;
        this.whoAmIReader = whoAmIReader;

        this.messagingPassword = Strings.isNullOrEmpty(config.getMessagingPassword()) ? "" : config.getMessagingPassword();
    }

    /**
     * Creates and returns the {@link Connection} instance
     * 
     * @param rabbitConnectionFactory A {@link ConnectionFactory} instance used to create the
     *        {@link Connection} instance
     * @param config A {@link SDKInternalConfiguration} instance representing odds feed configuration
     * @return The created {@link Connection} instance
     */
    private Connection newConnectionInternal(ConnectionFactory rabbitConnectionFactory,
                                             SDKInternalConfiguration config, String version2, WhoAmIReader whoAmIReader)
                throws KeyManagementException, NoSuchAlgorithmException, IOException, TimeoutException {
        logger.info("Creating new connection (Sportradar Unified Odds SDK " + version2 + ")");

        if (config.getAccessToken() == null) { // this is just a failsafe, the configuration gets validated on creation
            logger.warn("Access token needs to be set in OddsFeedConfiguration");
            throw new IllegalArgumentException("No access token set in OddsFeedConfiguration.");
        }

        rabbitConnectionFactory.setHost(config.getMessagingHost());
        rabbitConnectionFactory.setPort(config.getPort());
        if (config.getUseMessagingSsl()) {
            rabbitConnectionFactory.useSslProtocol();
        }

        int bookmakerId = whoAmIReader.getBookmakerId();
        if (bookmakerId == 0) {
            // failsafe if the validation didn't execute before
            whoAmIReader.validateBookmakerDetails();
        }

        Map<String, Object> props = rabbitConnectionFactory.getClientProperties();
        props.put("SrSdkType", "java");
        props.put("SrSdkVersion", version2);
        rabbitConnectionFactory.setClientProperties(props);

        if (config.getMessagingUsername() != null) {
            rabbitConnectionFactory.setUsername(config.getMessagingUsername());
        } else {
            // the default behaviour
            rabbitConnectionFactory.setUsername(config.getAccessToken());
        }

        if (config.getMessagingVirtualHost() != null) {
            rabbitConnectionFactory.setVirtualHost(config.getMessagingVirtualHost());
        } else {
            // the default behaviour
            rabbitConnectionFactory.setVirtualHost(VIRTUAL_HOST_PREFIX + bookmakerId);
        }

        rabbitConnectionFactory.setPassword(messagingPassword);

        rabbitConnectionFactory.setAutomaticRecoveryEnabled(true);

        rabbitConnectionFactory.setThreadFactory(
                new ThreadFactoryBuilder().setNameFormat(whoAmIReader.getSdkContextDescription() + "-amqp-t-%d").build()
        );

        Connection con = rabbitConnectionFactory.newConnection();
        logger.info("Connection created successfully");
        return con;
    }

    /**
     * Creates and returns a {@link SDKConnection} instance
     * 
     * @return the created {@link SDKConnection} instance
     */
    private SDKConnection createSdkConnection()
                throws IOException, TimeoutException, NoSuchAlgorithmException, KeyManagementException {
        checkFirewall();
        Connection actualConnection = newConnectionInternal(rabbitConnectionFactory, config, version, whoAmIReader);
        SDKConnection sdkConnection = new SDKConnection(actualConnection);
        sdkConnection.addShutdownListener(shutdownListener);
        return sdkConnection;
    }

    private void checkFirewall() throws IOException {
        URI uri;
        try {
            uri = new URI(config.getAPIHost());
        } catch (URISyntaxException e) {
            throw new IllegalArgumentException("Invalid API host format", e);
        }
        try {
            Socket s = new Socket(uri.getHost(), uri.getPort() < 0 ? 443 : uri.getPort());
            s.close();
        } catch (UnknownHostException uhe) {
            logger.error("Unable to lookup " + config.getAPIHost() + ". Network down?");
            System.exit(3);
        } catch (SocketException e) {
            boolean fwProblem = e.getMessage().toLowerCase().contains("permission denied");
            if (!fwProblem)
                return;
            CloseableHttpClient httpClient =
                        HttpClientBuilder.create().setRedirectStrategy(new LaxRedirectStrategy()).build();
            try {
                HttpGet httpGet = new HttpGet("http://ipecho.net/plain");
                ResponseHandler<String> handler = resp -> {
                    if (resp.getStatusLine().getStatusCode() == HttpStatus.SC_OK)
                        return EntityUtils.toString(resp.getEntity(), StandardCharsets.UTF_8);
                    else
                        return "";
                };
                String resp = httpClient.execute(httpGet, handler);
                throw new IOException(
                            "Firewall problem? If you believe your firewall is ok, please contact Sportradar and check that your ip ("
                                        + resp + ") is whitelisted ",
                            e);
            } catch (Exception exc) {
                logger.warn("Error during firewall test, ex:", exc);
            }
        }
    }

    /**
     * Returns a {@link Connection} instance representing connection to the AMQP broker
     * 
     * @return a {@link Connection} instance representing connection to the AMQP broker
     * @throws IOException An error occurred while creating the connection instance
     * @throws TimeoutException An error occurred while creating the connection instance
     * @throws NoSuchAlgorithmException An error occurred while configuring the factory to use SSL
     * @throws KeyManagementException An error occurred while configuring the factory to use SSL
     */
    @Override
    public Connection newConnection()
                throws IOException, TimeoutException, NoSuchAlgorithmException, KeyManagementException {
        synchronized (lock) {
            if (this.connection == null) {
                this.connection = createSdkConnection();
                // it can happen that the connection is being closed, but the shutdown listener
                // hasn't been fired yed.
                // In this case the connection can no longer be used to create channels, and new
                // connection has to be created
            } else if (!this.connection.isOpen()) {
                logger.warn("The connection has been closed, but the factory has not yet been notified. New connection will be created.");
                this.connection.removeShutdownListener(shutdownListener);
                this.connection = createSdkConnection();
            }
        }
        return connection;
    }

    /**
     * Close the AMQP connection
     *
     * @throws IOException if the connection couldn't be terminated
     */
    @Override
    public void close() throws IOException {
        synchronized (lock) {
            if (isConnectionOpen()) {
                logger.info("Connection closed");
                this.connection.close();
            } else {
                logger.info("Connection closure requested, but the connection is not established.");
            }
        }
    }

    /**
     * Check if the connection is currently alive
     *
     * @return the status of the connection
     */
    @Override
    public boolean isConnectionOpen(){
        return this.connection != null && this.connection.isOpen();
    }

    private class ShutdownListenerImpl implements ShutdownListener {

        @Override
        public void shutdownCompleted(ShutdownSignalException cause) {
            synchronized (lock) {
                MDC.setContextMap(whoAmIReader.getAssociatedSdkMdcContextMap());
                // make sure the reference points to closed connection
                if (connection.isOpen()) {
                    logger.warn("Existing AMQP connection shutdown detected, but the connection instance is opened. Message will be ignored. Ex:", cause);
                    return;
                }
                SingleInstanceAMQPConnectionFactory.this.connection = null;

                try {
                    SingleInstanceAMQPConnectionFactory.this.connectionStatusListener.onConnectionDown();
                } catch (Exception re) {
                    logger.warn("Problems dispatching onConnectionDown()", re);
                }

                if (cause.isInitiatedByApplication()) {
                    logger.info("Existing AMQP connection has been shut-down. Clearing the reference to it. [initiated by application]");
                } else {
                    logger.warn("Existing AMQP connection has been shut-down. Clearing the reference to it. Ex:", cause);
                }
                MDC.clear();
            }
        }
    }
}
