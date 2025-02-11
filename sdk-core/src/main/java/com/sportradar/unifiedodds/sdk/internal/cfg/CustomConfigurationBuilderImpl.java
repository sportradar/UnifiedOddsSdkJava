/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */

package com.sportradar.unifiedodds.sdk.internal.cfg;

import com.google.common.base.Preconditions;
import com.google.common.base.Strings;
import com.sportradar.unifiedodds.sdk.cfg.CustomConfigurationBuilder;
import com.sportradar.unifiedodds.sdk.cfg.UofConfiguration;

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
     * Sets the username used to authenticate with the messaging server
     *
     * @param username the username used to authenticate with the messaging server
     * @return the {@link CustomConfigurationBuilder} instance used to set custom config values
     */
    @Override
    public CustomConfigurationBuilder setMessagingUsername(String username) {
        Preconditions.checkArgument(!Strings.isNullOrEmpty(username));
        UofRabbitConfigurationImpl rabbitConfiguration = (UofRabbitConfigurationImpl) configuration.getRabbit();
        rabbitConfiguration.setUsername(username);
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
        UofRabbitConfigurationImpl rabbitConfiguration = (UofRabbitConfigurationImpl) configuration.getRabbit();
        rabbitConfiguration.setPassword(password);
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
}
