/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */
package com.sportradar.unifiedodds.sdk.impl.rabbitconnection;

import static com.google.common.base.Preconditions.checkNotNull;

import com.google.common.base.Strings;
import com.google.inject.name.Named;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.sportradar.unifiedodds.sdk.OperationManager;
import com.sportradar.unifiedodds.sdk.SDKConnectionStatusListener;
import com.sportradar.unifiedodds.sdk.SDKInternalConfiguration;
import com.sportradar.unifiedodds.sdk.impl.TimeUtils;
import com.sportradar.unifiedodds.sdk.impl.apireaders.WhoAmIReader;
import java.io.IOException;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeoutException;
import javax.inject.Inject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@SuppressWarnings({ "ClassFanOutComplexity" })
public class ConfiguredConnectionFactory {

    /**
     * A {@link Logger} instance used for execution logging
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(SingleInstanceAMQPConnectionFactory.class);

    /**
     * Specifies the AMQP broker virtual host prefix.
     */
    private static final String VIRTUAL_HOST_PREFIX = "/unifiedfeed/";
    private static final int MILLIS_IN_SECOND = 1000;

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
     * A {@link ExecutorService} dedicated to the underlying RabbitMQ connection
     */
    private final ExecutorService dedicatedRabbitMqExecutor;

    /**
     * A {@link SDKConnectionStatusListener} used to notify the outside world when the connection is
     * closed
     */
    private final SDKConnectionStatusListener connectionStatusListener;

    private DateTimeFormatter dateTimeFormatter = DateTimeFormatter
        .ofPattern("yyyyMMddHHmm")
        .withZone(ZoneId.of("UTC"));
    private TimeUtils timeUtils;

    @Inject
    public ConfiguredConnectionFactory(
        ConnectionFactory rabbitConnectionFactory,
        SDKInternalConfiguration config,
        @Named("version") String version,
        SDKConnectionStatusListener connectionStatusListener,
        @Named("DedicatedRabbitMqExecutor") ExecutorService dedicatedRabbitMqExecutor,
        final TimeUtils timeUtils
    ) {
        checkNotNull(rabbitConnectionFactory, "rabbitConnectionFactory cannot be a null reference");
        checkNotNull(config, "config cannot be a null reference");
        checkNotNull(connectionStatusListener, "connectionStatusListener cannot be a null reference");
        checkNotNull(dedicatedRabbitMqExecutor, "dedicatedRabbitMqExecutor cannot be a null reference");
        checkNotNull(timeUtils, "timeUtils cannot be a null reference");

        this.rabbitConnectionFactory = rabbitConnectionFactory;
        this.config = config;
        this.version = version;
        this.connectionStatusListener = connectionStatusListener;
        this.dedicatedRabbitMqExecutor = dedicatedRabbitMqExecutor;
        this.timeUtils = timeUtils;

        this.messagingPassword =
            Strings.isNullOrEmpty(config.getMessagingPassword()) ? "" : config.getMessagingPassword();
    }

    /**
     * Creates and returns the {@link Connection} instance
     *
     * @return The created {@link Connection} instance
     */
    Connection createConfiguredConnection(WhoAmIReader whoAmIReader, String sslVersion)
        throws KeyManagementException, NoSuchAlgorithmException, IOException, TimeoutException {
        LOGGER.info("Creating new connection (Sportradar Unified Odds SDK " + version + ")");
        if (config.getAccessToken() == null) { // this is just a failsafe, the configuration gets validated on creation
            LOGGER.warn("Access token needs to be set in OddsFeedConfiguration");
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
        props.put("SrUfSdkVersion", version);
        props.put("SrUfSdkInit", dateTimeFormatter.format(timeUtils.nowInstant()));
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
        rabbitConnectionFactory.setConnectionTimeout(
            OperationManager.getRabbitConnectionTimeout() * MILLIS_IN_SECOND
        );
        rabbitConnectionFactory.setRequestedHeartbeat(OperationManager.getRabbitHeartbeat());
        rabbitConnectionFactory.setExceptionHandler(
            new SDKExceptionHandler(connectionStatusListener, config.getAccessToken())
        );
        return rabbitConnectionFactory.newConnection(dedicatedRabbitMqExecutor);
    }
}
