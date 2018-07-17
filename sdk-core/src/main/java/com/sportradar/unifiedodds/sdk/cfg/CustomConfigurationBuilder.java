package com.sportradar.unifiedodds.sdk.cfg;

/**
 * Defines methods implemented by classes used to set general and custom configuration properties
 */
public interface CustomConfigurationBuilder extends RecoveryConfigurationBuilder<CustomConfigurationBuilder> {
    /**
     * Set the host name of the Sports API server
     *
     * @param apiHost the host name of the Sports API server
     * @return the {@link CustomConfigurationBuilder} instance used to set custom config values
     */
    CustomConfigurationBuilder setApiHost(String apiHost);

    /**
     * Sets the host name of the AMQP server
     *
     * @param messagingHost the host name of the AMQP server
     * @return the {@link CustomConfigurationBuilder} instance used to set custom config values
     */
    CustomConfigurationBuilder setMessagingHost(String messagingHost);

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
     * Sets the value specifying whether SSL should be used to communicate with Sports API
     *
     * @param useApiSsl the value specifying whether SSL should be used to communicate with Sports API
     * @return the {@link CustomConfigurationBuilder} instance used to set custom config values
     */
    CustomConfigurationBuilder useApiSsl(boolean useApiSsl);

    /**
     * Sets the value specifying whether SSL should be used to communicate with the messaging server
     *
     * @param useMessagingSsl the value specifying whether SSL should be used to communicate with the messaging server
     * @return the {@link CustomConfigurationBuilder} instance used to set custom config values
     */
    CustomConfigurationBuilder useMessagingSsl(boolean useMessagingSsl);
}
