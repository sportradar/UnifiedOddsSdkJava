/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */

package com.sportradar.unifiedodds.sdk.internal.cfg;

import com.google.common.base.Preconditions;
import com.google.common.base.Strings;
import com.sportradar.unifiedodds.sdk.cfg.CustomConfigurationBuilder;
import com.sportradar.unifiedodds.sdk.cfg.UofConfiguration;
import com.sportradar.unifiedodds.sdk.internal.cfg.UofClientAuthenticationImpl.PrivateKeyJwtImpl;

/**
 * A basic implementation of the {@link CustomConfigurationBuilder}
 */
@SuppressWarnings({ "HiddenField", "MagicNumber", "ParameterNumber" })
class CustomConfigurationBuilderImpl
    extends RecoveryConfigurationBuilderImpl<CustomConfigurationBuilder>
    implements CustomConfigurationBuilder {

    CustomConfigurationBuilderImpl(
        UofConfigurationImpl config,
        SdkConfigurationPropertiesReader sdkConfigurationPropertiesReader,
        SdkConfigurationYamlReader sdkConfigurationYamlReader
    ) {
        super(config, sdkConfigurationPropertiesReader, sdkConfigurationYamlReader);
        setMessagingUseSsl(true);
        setApiUseSsl(true);
    }

    @Override
    public CustomConfigurationBuilder loadConfigFromSdkProperties() {
        updateFieldsFromConfig(sdkConfigurationPropertiesReader);
        updateCustomFieldsFromConfig(sdkConfigurationPropertiesReader);
        return super.loadConfigFromSdkProperties();
    }

    @Override
    public CustomConfigurationBuilder loadConfigFromApplicationYml() {
        updateFieldsFromConfig(sdkConfigurationYamlReader);
        updateCustomFieldsFromConfig(sdkConfigurationYamlReader);
        return super.loadConfigFromApplicationYml();
    }

    /**
     * Set the host name of the Sports API server
     *
     * @param host the host name of the Sports API server
     * @return the {@link CustomConfigurationBuilder} instance used to set custom config values
     */
    @Override
    public CustomConfigurationBuilder setApiHost(String host) {
        Preconditions.checkArgument(!Strings.isNullOrEmpty(host), "ApiHost can not be null/empty");
        UofApiConfigurationImpl apiConfiguration = (UofApiConfigurationImpl) configuration.getApi();
        apiConfiguration.setHost(host);
        return this;
    }

    /**
     * Set the port of the Sports API server
     *
     * @param port the port of the Sports API server
     * @return the {@link CustomConfigurationBuilder} instance used to set custom config values
     */
    @Override
    public CustomConfigurationBuilder setApiPort(int port) {
        Preconditions.checkArgument(port > 0, "API Port must be greater than 0!");
        UofApiConfigurationImpl apiConfiguration = (UofApiConfigurationImpl) configuration.getApi();
        apiConfiguration.setPort(port);
        return this;
    }

    /**
     * Sets the value specifying whether SSL should be used to communicate with Sports API
     *
     * @param useSsl the value specifying whether SSL should be used to communicate with Sports API
     * @return the {@link CustomConfigurationBuilder} instance used to set custom config values
     */
    @Override
    public CustomConfigurationBuilder setApiUseSsl(boolean useSsl) {
        UofApiConfigurationImpl apiConfiguration = (UofApiConfigurationImpl) configuration.getApi();
        apiConfiguration.useSsl(useSsl);
        return this;
    }

    @Override
    public CustomConfigurationBuilder setClientAuthenticationHost(String host) {
        Preconditions.checkArgument(
            !Strings.isNullOrEmpty(host),
            "Client Authentication Host can not be null/empty"
        );
        Preconditions.checkArgument(
            configuration.getClientAuthentication() != null,
            "Client authentication must be set up in order to set authentication host"
        );
        PrivateKeyJwtImpl clientAuthentication = (PrivateKeyJwtImpl) configuration.getClientAuthentication();
        clientAuthentication.setHost(host);
        return this;
    }

    @Override
    public CustomConfigurationBuilder setClientAuthenticationTenant(String tenant) {
        Preconditions.checkArgument(
            tenant != null && !Strings.isNullOrEmpty(tenant.trim()),
            "Client Authentication tenant can not be null/empty"
        );
        Preconditions.checkArgument(
            configuration.getClientAuthentication() != null,
            "Client authentication must be set up in order to set authentication tenant"
        );

        PrivateKeyJwtImpl clientAuthentication = (PrivateKeyJwtImpl) configuration.getClientAuthentication();
        clientAuthentication.setTenant(tenant);
        return this;
    }

    @Override
    public CustomConfigurationBuilder setClientAuthenticationPort(int port) {
        Preconditions.checkArgument(port > 0, "Client Authentication Port must be greater than 0");
        Preconditions.checkArgument(
            configuration.getClientAuthentication() != null,
            "Client authentication must be set up in order to set authentication port"
        );
        PrivateKeyJwtImpl clientAuthentication = (PrivateKeyJwtImpl) configuration.getClientAuthentication();
        clientAuthentication.setPort(port);
        return this;
    }

    @Override
    public CustomConfigurationBuilder setClientAuthenticationUseSsl(boolean useSsl) {
        Preconditions.checkArgument(
            configuration.getClientAuthentication() != null,
            "Client authentication must be set up in order to set authentication ssl usage setting"
        );
        PrivateKeyJwtImpl clientAuthentication = (PrivateKeyJwtImpl) configuration.getClientAuthentication();
        clientAuthentication.setUseSsl(useSsl);
        return this;
    }

    /**
     * Sets the host name of the AMQP server
     *
     * @param host the host name of the AMQP server
     * @return the {@link CustomConfigurationBuilder} instance used to set custom config values
     */
    @Override
    public CustomConfigurationBuilder setMessagingHost(String host) {
        Preconditions.checkArgument(!Strings.isNullOrEmpty(host), "MessagingHost can not be null/empty");
        UofRabbitConfigurationImpl rabbitConfiguration = (UofRabbitConfigurationImpl) configuration.getRabbit();
        rabbitConfiguration.setHost(host);
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
        UofRabbitConfigurationImpl rabbitConfiguration = (UofRabbitConfigurationImpl) configuration.getRabbit();
        rabbitConfiguration.setPort(port);
        return this;
    }

    /**
     * Sets the username and password used to authenticate with the messaging server
     *
     * @param username the username used to authenticate with the messaging server
     * @param password the password used to authenticate with the messaging server
     * @return the {@link CustomConfigurationBuilder} instance used to set custom config values
     */
    @Override
    public CustomConfigurationBuilder setMessagingCredentials(String username, String password) {
        setMessagingUsername(username);
        setMessagingPassword(password);
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
        Preconditions.checkArgument(
            !Strings.isNullOrEmpty(vHost),
            "Virtual host can not be null or not empty"
        );
        UofRabbitConfigurationImpl rabbitConfiguration = (UofRabbitConfigurationImpl) configuration.getRabbit();
        rabbitConfiguration.setVirtualHost(vHost);
        return this;
    }

    /**
     * Sets the value specifying whether SSL should be used to communicate with the messaging server
     *
     * @param useSsl the value specifying whether SSL should be used to communicate with the messaging server
     * @return the {@link CustomConfigurationBuilder} instance used to set custom config values
     */
    @Override
    public CustomConfigurationBuilder setMessagingUseSsl(boolean useSsl) {
        UofRabbitConfigurationImpl rabbitConfiguration = (UofRabbitConfigurationImpl) configuration.getRabbit();
        rabbitConfiguration.useSsl(useSsl);
        return this;
    }

    /**
     * Builds and returns a {@link UofConfigurationImpl} instance
     *
     * @return the constructed {@link UofConfigurationImpl} instance
     */
    @Override
    public UofConfiguration build() {
        configuration.validateMinimumSettings();
        configuration.acquireBookmakerDetailsAndProducerData();

        return configuration;
    }

    private void setMessagingUsername(String username) {
        Preconditions.checkArgument(
            !Strings.isNullOrEmpty(username),
            "messaging username cannot be null or not empty"
        );
        UofRabbitConfigurationImpl rabbitConfiguration = (UofRabbitConfigurationImpl) configuration.getRabbit();
        rabbitConfiguration.setUsername(username);
    }

    private void setMessagingPassword(String password) {
        Preconditions.checkArgument(
            !Strings.isNullOrEmpty(password),
            "messaging password cannot be null or not empty"
        );
        UofRabbitConfigurationImpl rabbitConfiguration = (UofRabbitConfigurationImpl) configuration.getRabbit();
        rabbitConfiguration.setPassword(password);
    }
}
