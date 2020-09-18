/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */

package com.sportradar.unifiedodds.sdk.cfg;

import com.google.common.base.Preconditions;
import com.google.common.base.Strings;
import com.sportradar.unifiedodds.sdk.SDKConfigurationPropertiesReader;
import com.sportradar.unifiedodds.sdk.SDKConfigurationReader;
import com.sportradar.unifiedodds.sdk.SDKConfigurationYamlReader;
import com.sportradar.utils.SdkHelper;

import java.util.ArrayList;

/**
 * A basic implementation of the {@link CustomConfigurationBuilder}
 */
class CustomConfigurationBuilderImpl extends RecoveryConfigurationBuilderImpl<CustomConfigurationBuilder> implements CustomConfigurationBuilder {
    private final String accessToken;
    private final Environment environment;

    private String messagingHost;
    private String apiHost;
    private int messagingPort;
    private boolean useMessagingSsl;
    private boolean useApiSsl;
    private String username;
    private String password;
    private String messagingVirtualHost;

    CustomConfigurationBuilderImpl(String accessToken, String messagingHost, String apiHost, int messagingPort, boolean useMessagingSsl, boolean useApiSsl, SDKConfigurationPropertiesReader sdkConfigurationPropertiesReader, SDKConfigurationYamlReader sdkConfigurationYamlReader, Environment environment) {
        super(sdkConfigurationPropertiesReader, sdkConfigurationYamlReader);

        this.accessToken = accessToken;
        this.messagingHost = messagingHost;
        this.apiHost = apiHost;
        this.messagingPort = messagingPort;
        this.useMessagingSsl = useMessagingSsl;
        this.useApiSsl = useApiSsl;
        this.environment = environment;
    }

    @Override
    public CustomConfigurationBuilder loadConfigFromSdkProperties() {
        loadConfigFrom(sdkConfigurationPropertiesReader);
        return super.loadConfigFromSdkProperties();
    }

    @Override
    public CustomConfigurationBuilder loadConfigFromApplicationYml() {
        loadConfigFrom(sdkConfigurationYamlReader);
        return super.loadConfigFromApplicationYml();
    }

    /**
     * Set the host name of the Sports API server
     *
     * @param apiHost the host name of the Sports API server
     * @return the {@link CustomConfigurationBuilder} instance used to set custom config values
     */
    @Override
    public CustomConfigurationBuilder setApiHost(String apiHost) {
        Preconditions.checkArgument(!Strings.isNullOrEmpty(apiHost), "ApiHost can not be null/empty");

        this.apiHost = apiHost;
        return this;
    }

    /**
     * Sets the host name of the AMQP server
     *
     * @param messagingHost the host name of the AMQP server
     * @return the {@link CustomConfigurationBuilder} instance used to set custom config values
     */
    @Override
    public CustomConfigurationBuilder setMessagingHost(String messagingHost) {
        Preconditions.checkArgument(!Strings.isNullOrEmpty(messagingHost), "MessagingHost can not be null/empty");

        this.messagingHost = messagingHost;
        return this;
    }

    /**
     * Sets a custom port used to connect to AMQP broker
     *
     * @param port the port used to connect to AMQP broker
     * @return the {@link CustomConfigurationBuilder} instance used to set custom config values
     */
    @Override
    public CustomConfigurationBuilder setMessagingPort(int port) {
        Preconditions.checkArgument(port > 0);

        this.messagingPort = port;
        return this;
    }

    /**
     * Sets the username used to authenticate with the messaging server
     *
     * @param username the username used to authenticate with the messaging server
     * @return the {@link CustomConfigurationBuilder} instance used to set custom config values
     */
    @Override
    public CustomConfigurationBuilder setMessagingUsername(String username) {
        Preconditions.checkArgument(!Strings.isNullOrEmpty(username));

        this.username = username;
        return this;
    }

    /**
     * Sets the password used to authenticate with the messaging server
     *
     * @param password the password used to authenticate with the messaging server
     * @return the {@link CustomConfigurationBuilder} instance used to set custom config values
     */
    @Override
    public CustomConfigurationBuilder setMessagingPassword(String password) {
        Preconditions.checkArgument(!Strings.isNullOrEmpty(password));

        this.password = password;
        return this;
    }

    /**
     * Sets the virtual host used to connect to the messaging server
     *
     * @param vHost the virtual host used to connect to the messaging server
     * @return the {@link CustomConfigurationBuilder} instance used to set custom config values
     */
    @Override
    public CustomConfigurationBuilder setMessagingVirtualHost(String vHost) {
        Preconditions.checkArgument(!Strings.isNullOrEmpty(vHost), "messaging virtual host can be null or not empty");

        this.messagingVirtualHost = vHost;
        return this;
    }

    /**
     * Sets the value specifying whether SSL should be used to communicate with Sports API
     *
     * @param useApiSsl the value specifying whether SSL should be used to communicate with Sports API
     * @return the {@link CustomConfigurationBuilder} instance used to set custom config values
     */
    @Override
    public CustomConfigurationBuilder useApiSsl(boolean useApiSsl) {
        this.useApiSsl = useApiSsl;
        return this;
    }

    /**
     * Sets the value specifying whether SSL should be used to communicate with the messaging server
     *
     * @param useMessagingSsl the value specifying whether SSL should be used to communicate with the messaging server
     * @return the {@link CustomConfigurationBuilder} instance used to set custom config values
     */
    @Override
    public CustomConfigurationBuilder useMessagingSsl(boolean useMessagingSsl) {
        this.useMessagingSsl = useMessagingSsl;
        return this;
    }

    /**
     * Builds and returns a {@link OddsFeedConfiguration} instance
     *
     * @return the constructed {@link OddsFeedConfiguration} instance
     */
    @Override
    public OddsFeedConfiguration build() {

        defaultLocale = SdkHelper.checkConfigurationLocales(defaultLocale, getSupportedLocales());

        return new OddsFeedConfiguration(
                accessToken,
                defaultLocale,
                new ArrayList<>(getSupportedLocales()),
                messagingHost,
                apiHost,
                maxInactivitySeconds,
                maxRecoveryExecutionTimeMinutes,
                minIntervalBetweenRecoveryRequests,
                useMessagingSsl,
                useApiSsl,
                messagingPort,
                username,
                password,
                nodeId,
                environment == Environment.Integration || environment == Environment.Staging,
                new ArrayList<>(disabledProducers),
                exceptionHandlingStrategy,
                environment,
                messagingVirtualHost, httpClientTimeout, httpClientMaxConnTotal, httpClientMaxConnPerRoute, recoveryHttpClientTimeout, recoveryHttpClientMaxConnTotal, recoveryHttpClientMaxConnPerRoute);
    }

    /**
     * Loads the properties that are relevant to the builder from the provided {@link SDKConfigurationReader}
     *
     * @param sdkConfigurationReader the reader from which the properties should be red
     */
    private void loadConfigFrom(SDKConfigurationReader sdkConfigurationReader) {
        Preconditions.checkNotNull(sdkConfigurationReader);

        sdkConfigurationReader.readMessagingHost().ifPresent(this::setMessagingHost);
        sdkConfigurationReader.readApiHost().ifPresent(this::setApiHost);
        sdkConfigurationReader.readUseApiSsl().ifPresent(this::useApiSsl);
        sdkConfigurationReader.readUseMessagingSsl().ifPresent(this::useMessagingSsl);
        sdkConfigurationReader.readMessagingPort().ifPresent(this::setMessagingPort);
        sdkConfigurationReader.readMessagingUsername().ifPresent(this::setMessagingUsername);
        sdkConfigurationReader.readMessagingPassword().ifPresent(this::setMessagingPassword);
        sdkConfigurationReader.readMessagingVirtualHost().ifPresent(this::setMessagingVirtualHost);
    }
}
