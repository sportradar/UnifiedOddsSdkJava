/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */

package com.sportradar.unifiedodds.sdk.impl;

import static com.google.common.base.Preconditions.checkNotNull;

import com.google.common.base.Strings;
import com.google.inject.name.Named;
import com.rabbitmq.client.*;
import com.sportradar.unifiedodds.sdk.OperationManager;
import com.sportradar.unifiedodds.sdk.SDKConnectionStatusListener;
import com.sportradar.unifiedodds.sdk.SDKInternalConfiguration;
import com.sportradar.unifiedodds.sdk.impl.apireaders.WhoAmIReader;
import java.io.IOException;
import java.net.*;
import java.nio.charset.StandardCharsets;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeoutException;
import javax.inject.Inject;
import javax.net.ssl.SSLContext;
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

    private String sslVersion;

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
     * A {@link ExecutorService} dedicated to the underlying RabbitMQ connection
     */
    private final ExecutorService dedicatedRabbitMqExecutor;

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
    public SingleInstanceAMQPConnectionFactory(ConnectionFactory rabbitConnectionFactory,
                                               SDKInternalConfiguration config,
                                               SDKConnectionStatusListener connectionStatusListener,
                                               @Named("version") String version,
                                               WhoAmIReader whoAmIReader,
                                               @Named("DedicatedRabbitMqExecutor") ExecutorService dedicatedRabbitMqExecutor) {
        checkNotNull(rabbitConnectionFactory, "rabbitConnectionFactory cannot be a null reference");
        checkNotNull(config, "config cannot be a null reference");
        checkNotNull(connectionStatusListener, "connectionStatusListener cannot be a null reference");
        checkNotNull(dedicatedRabbitMqExecutor, "dedicatedRabbitMqExecutor cannot be a null reference");

        this.rabbitConnectionFactory = rabbitConnectionFactory;
        this.config = config;
        this.connectionStatusListener = connectionStatusListener;
        this.version = version;
        this.whoAmIReader = whoAmIReader;
        this.dedicatedRabbitMqExecutor = dedicatedRabbitMqExecutor;

        this.messagingPassword = Strings.isNullOrEmpty(config.getMessagingPassword()) ? "" : config.getMessagingPassword();
        this.shutdownListener = new ShutdownListenerImpl(this);
        this.blockedListener = new BlockedListenerImpl(this);
        this.connectionStarted = 0;
        this.canFeedBeOpened = true;
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
                                             SDKInternalConfiguration config,
                                             String version2,
                                             WhoAmIReader whoAmIReader,
                                             String sslVersion)
                throws KeyManagementException,
                       NoSuchAlgorithmException,
                       IOException,
                       TimeoutException {
        logger.info("Creating new connection (Sportradar Unified Odds SDK " + version2 + ")");

        if (config.getAccessToken() == null) { // this is just a failsafe, the configuration gets validated on creation
            logger.warn("Access token needs to be set in OddsFeedConfiguration");
            throw new IllegalArgumentException("No access token set in OddsFeedConfiguration.");
        }

        rabbitConnectionFactory.setHost(config.getMessagingHost());
        rabbitConnectionFactory.setPort(config.getPort());
        if (config.getUseMessagingSsl()) {
            rabbitConnectionFactory.useSslProtocol(sslVersion);
        }

        int bookmakerId = whoAmIReader.getBookmakerId();
        if (bookmakerId == 0) {
            // failsafe if the validation didn't execute before
            whoAmIReader.validateBookmakerDetails();
        }

        Map<String, Object> props = rabbitConnectionFactory.getClientProperties();
        props.put("SrUfSdkType", "java");
        props.put("SrUfSdkVersion", version2);
        props.put("SrUfSdkInit", new SimpleDateFormat("yyyyMMddHHmm").format(new Date()));
        props.put("SrUfSdkConnName", "RabbitMQ / Java");
        props.put("SrUfSdkBId", String.valueOf(whoAmIReader.getBookmakerId()));
        rabbitConnectionFactory.setClientProperties(props);

        if (config.getMessagingUsername() != null) {
            rabbitConnectionFactory.setUsername(config.getMessagingUsername());
        } else {
            // the default behaviour
            rabbitConnectionFactory.setUsername(config.getAccessToken());
        }
        rabbitConnectionFactory.setPassword(messagingPassword);
        if (config.getMessagingVirtualHost() != null) {
            rabbitConnectionFactory.setVirtualHost(config.getMessagingVirtualHost());
        } else {
            // the default behaviour
            rabbitConnectionFactory.setVirtualHost(VIRTUAL_HOST_PREFIX + bookmakerId);
        }

        rabbitConnectionFactory.setAutomaticRecoveryEnabled(true);
        rabbitConnectionFactory.setConnectionTimeout(OperationManager.getRabbitConnectionTimeout() * 1000);
        rabbitConnectionFactory.setRequestedHeartbeat(OperationManager.getRabbitHeartbeat());

        rabbitConnectionFactory.setExceptionHandler(new SDKExceptionHandler(connectionStatusListener, config.getAccessToken()));

        Connection con = rabbitConnectionFactory.newConnection(dedicatedRabbitMqExecutor);
        connectionStarted = new TimeUtilsImpl().now();
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
        logger.info("Creating new SDKConnection for {}", config.getMessagingHost());
        Connection actualConnection = null;
        if (config.getUseMessagingSsl()) {
            if(sslVersion != null){
                actualConnection = newConnectionInternal(rabbitConnectionFactory, config, version, whoAmIReader, sslVersion);
            }
            else{
                List<String> sslProtocols =  Arrays.asList(SSLContext.getDefault().getSupportedSSLParameters().getProtocols());
                Collections.reverse(sslProtocols);
                for(String testSslVersion : sslProtocols) {
                    try{
                        actualConnection = newConnectionInternal(rabbitConnectionFactory, config, version, whoAmIReader, testSslVersion);
                        sslVersion = testSslVersion;
                        break;
                    }
                    catch(Exception ex){
                        logger.debug("Error creating connection for SSL version {}. Exception={}", testSslVersion, ex.getMessage());
                    }
                }
            }
        }
        else{
            actualConnection = newConnectionInternal(rabbitConnectionFactory, config, version, whoAmIReader, null);
        }
        if(actualConnection == null){
            return null;
        }
        SDKConnection sdkConnection = new SDKConnection(actualConnection);
        sdkConnection.addShutdownListener(shutdownListener);
        sdkConnection.addBlockedListener(blockedListener);
        return sdkConnection;
    }

  private void checkFirewall() throws IOException {
    URI uri;
    try {
      uri = new URI(config.getAPIHost());
    } catch (URISyntaxException e) {
      throw new IllegalArgumentException("Invalid API host format", e);
    }
    int port = uri.getPort() < 0 ? 443 : uri.getPort();
    try {
      Socket s = new Socket(uri.getHost(), uri.getPort() < 0 ? 443 : uri.getPort());
      s.close();
    } catch (UnknownHostException uhe) {
      logger.error("Unable to lookup " + config.getAPIHost() + ":" + port + ". Network down?");
      System.exit(3);
    } catch (SocketException e) {
      boolean fwProblem = e.getMessage().toLowerCase().contains("permission denied");
      if (!fwProblem) return;
      CloseableHttpClient httpClient =
          HttpClientBuilder.create()
              .useSystemProperties()
              .setRedirectStrategy(new LaxRedirectStrategy())
              .build();
      try {
        HttpGet httpGet = new HttpGet("http://ipecho.net/plain");
        ResponseHandler<String> handler =
            resp -> {
              if (resp.getStatusLine().getStatusCode() == HttpStatus.SC_OK)
                return EntityUtils.toString(resp.getEntity(), StandardCharsets.UTF_8);
              else return "";
            };
        String resp = httpClient.execute(httpGet, handler);
        throw new IOException(
            "Firewall problem? If you believe your firewall is ok, please contact Sportradar and check that your ip ("
                + resp
                + ") is whitelisted ",
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
     */
    @Override
    public synchronized Connection getConnection() {
        synchronized (syncLock) {
            if(!canFeedBeOpened){
                logger.warn("Connection should be closed.");
                return null;
            }
            try {
                if(this.connection == null){
                    SDKConnection sdkConnection = this.createSdkConnection();
                    this.connection = sdkConnection;
                }
            } catch (Exception exc) {
                logger.error("Error creating connection: " + exc.getMessage(), exc);
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
        logger.info("Closing underlying AMQP connection");
        if(feedStopped){
            canFeedBeOpened = false;
        }
        try {
            if (this.connection != null) {
                this.connection.close();
            }
        } catch(IOException ex){
            logger.error("Error closing connection: ", ex);
        } finally {
            this.connection = null;
            connectionStarted = 0;
        }
        logger.info("Connection closed");
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
    public long getConnectionStarted() { return connectionStarted; }

    /**
     * Check if the connection can or should be opened
     *
     * @return value indicating if the connection can or should be opened
     */
    @Override
    public boolean canConnectionOpen() { return canFeedBeOpened; }

    private synchronized void handleShutdown(ShutdownSignalException cause) {
        try {
            MDC.setContextMap(whoAmIReader.getAssociatedSdkMdcContextMap());
            try {
                this.connectionStatusListener.onConnectionDown();
            } catch (Exception re) {
                logger.warn("Problems dispatching onConnectionDown()", re);
            }

            if (cause.isInitiatedByApplication()) {
                logger.info("Existing AMQP connection has been shut-down. [initiated by application]");
            } else {
                logger.warn("Existing AMQP connection has been shut-down. Ex:", cause);
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
            } catch (Exception re) {
                logger.warn("Problems dispatching onConnectionDown()", re);
            }

            if (blocked) {
                logger.warn("Existing AMQP connection has been blocked. " + reason);
            } else {
                logger.info("Existing AMQP connection has been unblocked.");
            }
        } finally {
            MDC.clear();
        }
    }

    private final static class ShutdownListenerImpl implements ShutdownListener {

        private final SingleInstanceAMQPConnectionFactory parent;

        private ShutdownListenerImpl(final SingleInstanceAMQPConnectionFactory parent) { this.parent = parent; }

        @Override
        public void shutdownCompleted(ShutdownSignalException cause) {
            this.parent.handleShutdown(cause);
        }
    }

    private final static class BlockedListenerImpl implements BlockedListener {

        private final SingleInstanceAMQPConnectionFactory parent;

        private BlockedListenerImpl(final SingleInstanceAMQPConnectionFactory parent) { this.parent = parent; }

        @Override
        public void handleBlocked(String s) throws IOException { this.parent.handleBlocked(s, true); }

        @Override
        public void handleUnblocked() throws IOException { this.parent.handleBlocked(null, false); }
    }
}
