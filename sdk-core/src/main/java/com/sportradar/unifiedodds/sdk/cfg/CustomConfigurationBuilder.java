/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */

package com.sportradar.unifiedodds.sdk.cfg;

/**
 * Defines methods implemented by classes used to set general and custom configuration properties
 */
public interface CustomConfigurationBuilder extends RecoveryConfigurationBuilder<CustomConfigurationBuilder> {
    /**
     * Set the host name of the Sports API server
     *
     * @param host the host name of the Sports API server
     * @return the {@link CustomConfigurationBuilder} instance used to set custom config values
     */
    CustomConfigurationBuilder setApiHost(String host);

    /**
     * Set the port of the Sports API server
     *
     * @param port the port of the Sports API server
     * @return the {@link CustomConfigurationBuilder} instance used to set custom config values
     */
    CustomConfigurationBuilder setApiPort(int port);

    /**
     * Sets the value specifying whether SSL should be used to communicate with Sports API
     *
     * @param useSsl the value specifying whether SSL should be used to communicate with Sports API
     * @return the {@link CustomConfigurationBuilder} instance used to set custom config values
     */
    CustomConfigurationBuilder setApiUseSsl(boolean useSsl);

    /**
     * Sets the host name of the AMQP server
     *
     * @param host the host name of the AMQP server
     * @return the {@link CustomConfigurationBuilder} instance used to set custom config values
     */
    CustomConfigurationBuilder setMessagingHost(String host);

    /**
     * Sets a custom port used to connect to AMQP broker
     *
     * @param port the port used to connect to AMQP broker
     * @return the {@link CustomConfigurationBuilder} instance used to set custom config values
     */
    CustomConfigurationBuilder setMessagingPort(int port);

    /**
     * Sets the username used to authenticate with the messaging server
     *
     * @param username the username used to authenticate with the messaging server
     * @return the {@link CustomConfigurationBuilder} instance used to set custom config values
     */
    CustomConfigurationBuilder setMessagingUsername(String username);

    /**
     * Sets the virtual host used to connect to the messaging server
     *
     * @param vHost the virtual host used to connect to the messaging server
     * @return the {@link CustomConfigurationBuilder} instance used to set custom config values
     */
    CustomConfigurationBuilder setMessagingVirtualHost(String vHost);

    /**
     * Sets the password used to authenticate with the messaging server
     *
     * @param password the password used to authenticate with the messaging server
     * @return the {@link CustomConfigurationBuilder} instance used to set custom config values
     */
    CustomConfigurationBuilder setMessagingPassword(String password);

    /**
     * Sets the value specifying whether SSL should be used to communicate with the messaging server
     *
     * @param useSsl the value specifying whether SSL should be used to communicate with the messaging server
     * @return the {@link CustomConfigurationBuilder} instance used to set custom config values
     */
    CustomConfigurationBuilder setMessagingUseSsl(boolean useSsl);
}
