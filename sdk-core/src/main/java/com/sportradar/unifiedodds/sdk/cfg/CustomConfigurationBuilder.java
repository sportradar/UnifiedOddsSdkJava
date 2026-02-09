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
     * Sets the virtual host used to connect to the messaging server
     *
     * @param vHost the virtual host used to connect to the messaging server
     * @return the {@link CustomConfigurationBuilder} instance used to set custom config values
     */
    CustomConfigurationBuilder setMessagingVirtualHost(String vHost);

    /**
     * Sets the username and password used to authenticate with the messaging server
     *
     * @param username the username used to authenticate with the messaging server
     * @param password the password used to authenticate with the messaging server
     * @return the {@link CustomConfigurationBuilder} instance used to set custom config values
     */
    CustomConfigurationBuilder setMessagingCredentials(String username, String password);

    /**
     * Sets the value specifying whether SSL should be used to communicate with the messaging server
     *
     * @param useSsl the value specifying whether SSL should be used to communicate with the messaging server
     * @return the {@link CustomConfigurationBuilder} instance used to set custom config values
     */
    CustomConfigurationBuilder setMessagingUseSsl(boolean useSsl);

    /**
     * Sets the host name of the client authentication server
     *
     * @param host the host name of the client authentication server
     * @return the {@link CustomConfigurationBuilder} instance used to set custom config values
     */
    CustomConfigurationBuilder setClientAuthenticationHost(String host);

    /**
     * Sets the tenant of the client authentication server
     *
     * @param tenant the tenant of the client authentication server
     * @return the {@link CustomConfigurationBuilder} instance used to set custom config values
     */
    CustomConfigurationBuilder setClientAuthenticationTenant(String tenant);

    /**
     * Sets the port of the client authentication server
     *
     * @param port the port of the client authentication server
     * @return the {@link CustomConfigurationBuilder} instance used to set custom config values
     */
    CustomConfigurationBuilder setClientAuthenticationPort(int port);

    /**
     * Sets the value specifying whether SSL should be used to communicate with the client authentication server
     * <p>
     * This setting determines whether HTTPS (SSL/TLS) or HTTP will be used for client authentication requests.
     * When set to {@code true}, the SDK will use HTTPS for secure communication with the authentication server.
     * When set to {@code false}, the SDK will use HTTP for communication.
     * </p>
     * <p>
     * <strong>Security Note:</strong> It is strongly recommended to use SSL ({@code true}) in production
     * environments to ensure secure transmission of authentication credentials and tokens.
     * </p>
     *
     * @param useSsl {@code true} if SSL should be used for client authentication communication,
     *               {@code false} if HTTP should be used instead
     * @return the {@link CustomConfigurationBuilder} instance used to set custom config values
     */
    CustomConfigurationBuilder setClientAuthenticationUseSsl(boolean useSsl);
}
