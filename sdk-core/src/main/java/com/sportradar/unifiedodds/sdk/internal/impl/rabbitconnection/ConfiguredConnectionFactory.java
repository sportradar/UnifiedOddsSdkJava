/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */
package com.sportradar.unifiedodds.sdk.internal.impl.rabbitconnection;

import static com.google.common.base.Preconditions.checkNotNull;
import static java.lang.String.valueOf;

import com.google.common.base.Strings;
import com.google.inject.Inject;
import com.google.inject.name.Named;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.impl.CredentialsProvider;
import com.sportradar.unifiedodds.sdk.SdkConnectionStatusListener;
import com.sportradar.unifiedodds.sdk.cfg.UofConfiguration;
import com.sportradar.unifiedodds.sdk.internal.commoniam.OAuth2TokenCache;
import com.sportradar.unifiedodds.sdk.internal.impl.RuntimeConfiguration;
import com.sportradar.unifiedodds.sdk.internal.impl.SdkInternalConfiguration;
import com.sportradar.unifiedodds.sdk.internal.impl.TimeUtils;
import com.sportradar.unifiedodds.sdk.internal.impl.apireaders.WhoAmIReader;
import java.io.IOException;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeoutException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@SuppressWarnings({ "ClassFanOutComplexity" })
public class ConfiguredConnectionFactory {

    /**
     * A {@link Logger} instance used for execution logging
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(SingleInstanceAmqpConnectionFactory.class);

    /**
     * Specifies the AMQP broker virtual host prefix.
     */
    private static final String VIRTUAL_HOST_PREFIX = "/unifiedfeed/";
    private static final int MILLIS_IN_SECOND = 1000;

    private final String version;
    /**
     * A {@link SdkInternalConfiguration} instance representing odds feed configuration
     */
    private final SdkInternalConfiguration config;

    /**
     * A {@link ConnectionFactory} instance used to create the {@link Connection} instance
     */
    private final ConnectionFactory rabbitConnectionFactory;

    /**
     * A {@link ExecutorService} dedicated to the underlying RabbitMQ connection
     */
    private final ExecutorService dedicatedRabbitMqExecutor;

    /**
     * A {@link SdkConnectionStatusListener} used to notify the outside world when the connection is
     * closed
     */
    private final SdkConnectionStatusListener connectionStatusListener;
    private final UofConfiguration uofConfiguration;
    private final OAuth2TokenCache tokenCache;

    private DateTimeFormatter dateTimeFormatter = DateTimeFormatter
        .ofPattern("yyyyMMddHHmm")
        .withZone(ZoneId.of("UTC"));
    private TimeUtils timeUtils;

    @Inject
    @SuppressWarnings("ParameterNumber")
    public ConfiguredConnectionFactory(
        ConnectionFactory rabbitConnectionFactory,
        SdkInternalConfiguration config,
        UofConfiguration uofConfiguration,
        @Named("version") String version,
        SdkConnectionStatusListener connectionStatusListener,
        @Named("DedicatedRabbitMqExecutor") ExecutorService dedicatedRabbitMqExecutor,
        final TimeUtils timeUtils,
        @Named("OAuth2TokenCacheForRabbitMq") OAuth2TokenCache tokenCache
    ) {
        this.uofConfiguration = uofConfiguration;
        this.tokenCache = tokenCache;
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
    }

    /**
     * Creates and returns the {@link Connection} instance
     *
     * @return The created {@link Connection} instance
     */
    Connection createConfiguredConnection(WhoAmIReader whoAmIReader, String sslVersion)
        throws KeyManagementException, NoSuchAlgorithmException, IOException, TimeoutException {
        LOGGER.info("Creating new connection (Sportradar Unified Odds SDK " + version + ")");

        if (config.getUseMessagingSsl()) {
            rabbitConnectionFactory.useSslProtocol(sslVersion);
        }

        int bookmakerId = whoAmIReader.getBookmakerId();
        if (bookmakerId == 0) {
            // failsafe if the validation didn't execute before
            whoAmIReader.validateBookmakerDetails();
        }

        setVhostLocation(bookmakerId);
        setEnvironmentIdentifiers(whoAmIReader.getBookmakerId());
        setCredentials(bookmakerId);

        rabbitConnectionFactory.setAutomaticRecoveryEnabled(true);
        rabbitConnectionFactory.setConnectionTimeout(
            RuntimeConfiguration.getRabbitConnectionTimeout() * MILLIS_IN_SECOND
        );
        rabbitConnectionFactory.setRequestedHeartbeat(RuntimeConfiguration.getRabbitHeartbeat());
        setExceptionHandler();
        return rabbitConnectionFactory.newConnection(dedicatedRabbitMqExecutor);
    }

    private void setExceptionHandler() {
        rabbitConnectionFactory.setExceptionHandler(
            new SdkExceptionHandler(
                connectionStatusListener,
                uofConfiguration.getClientAuthentication() == null ? uofConfiguration.getAccessToken() : ""
            )
        );
    }

    private void setEnvironmentIdentifiers(int bookmakerId) {
        Map<String, Object> props = rabbitConnectionFactory.getClientProperties();
        props.put("SrUfSdkType", "java");
        props.put("SrUfSdkVersion", version);
        props.put("SrUfSdkInit", dateTimeFormatter.format(timeUtils.nowInstant()));
        props.put("SrUfSdkConnName", "RabbitMQ / Java");
        props.put("SrUfSdkBId", valueOf(bookmakerId));
        rabbitConnectionFactory.setClientProperties(props);
    }

    private void setVhostLocation(int bookmakerId) {
        rabbitConnectionFactory.setHost(config.getMessagingHost());
        rabbitConnectionFactory.setPort(config.getPort());
        if (config.getMessagingVirtualHost() != null) {
            rabbitConnectionFactory.setVirtualHost(config.getMessagingVirtualHost());
        } else {
            rabbitConnectionFactory.setVirtualHost(VIRTUAL_HOST_PREFIX + bookmakerId);
        }
    }

    private void setCredentials(int bookmakerId) {
        if (uofConfiguration.getClientAuthentication() == null) {
            setCredentialsAccessToken();
        } else {
            setCredentialsClientAuthentication(bookmakerId);
        }
    }

    private void setCredentialsAccessToken() {
        if (config.getMessagingUsername() != null) {
            rabbitConnectionFactory.setUsername(config.getMessagingUsername());
        } else {
            rabbitConnectionFactory.setUsername(uofConfiguration.getAccessToken());
        }
        String password = Strings.isNullOrEmpty(config.getMessagingPassword())
            ? ""
            : config.getMessagingPassword();
        rabbitConnectionFactory.setPassword(password);
    }

    private void setCredentialsClientAuthentication(int bookmakerId) {
        String username = config.getMessagingUsername() != null
            ? config.getMessagingUsername()
            : valueOf(bookmakerId);
        if (config.getMessagingPassword() != null) {
            rabbitConnectionFactory.setUsername(username);
            rabbitConnectionFactory.setPassword(config.getMessagingPassword());
        } else {
            rabbitConnectionFactory.setCredentialsProvider(
                new OAuth2CredentialProvider(username, tokenCache)
            );
        }
    }

    private static class OAuth2CredentialProvider implements CredentialsProvider {

        private String username;
        private OAuth2TokenCache tokenCache;

        public OAuth2CredentialProvider(String username, OAuth2TokenCache tokenCache) {
            this.username = username;
            this.tokenCache = tokenCache;
        }

        @Override
        public String getUsername() {
            return username;
        }

        @Override
        public String getPassword() {
            return tokenCache.getToken().getAccessToken();
        }
    }
}
