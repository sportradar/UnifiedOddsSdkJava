/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */

package com.sportradar.unifiedodds.sdk.cfg;

import java.util.List;
import java.util.Locale;

/**
 * All of the fields that can be set trough the {@link OddsFeedConfigurationBuilder} are optional
 */
public interface OddsFeedConfigurationBuilder {

    /**
     * Sets the default locale. This is the locale that will be used for any getter that returns
     * localized Strings (i.e. Sport name, Tournament name, Team name, Player name etc). The default
     * locale is English if not specified.
     *
     * @param defaultLocale the locale to use as default.
     * @return the current instance {@link OddsFeedConfigurationBuilder}
     */
    OddsFeedConfigurationBuilder setDefaultLocale(Locale defaultLocale);

    /**
     * Which locales should be auto-fetched - beside the default {@link Locale}.
     *
     * @param locales a list of locales that should be auto-fetched
     * @return the current instance {@link OddsFeedConfigurationBuilder}
     */
    OddsFeedConfigurationBuilder addDesiredLocales(List<Locale> locales);

    /**
     * Sets a value indicating whether SSL should be used when connecting to the AMQP broker
     *
     * @param useSsl value indicating whether the SDK should use SSL when connecting to the AMQP broker
     * @return the current instance {@link OddsFeedConfigurationBuilder}
     */
    OddsFeedConfigurationBuilder setMessagingUseSsl(boolean useSsl);

    /**
     * Sets a value indicating whether SSL should be used when requesting API endpoints
     *
     * @param useSsl value indicating whether the SDK should use SSL when requesting API endpoints
     * @return the current instance {@link OddsFeedConfigurationBuilder}
     */
    OddsFeedConfigurationBuilder setApiUseSsl(boolean useSsl);

    /**
     * Specify the AMQP host to receive messages from (if not specified this defaults to
     * mq.betradar.com)
     *
     * @param host - the AMQP host to receive messages from
     * @return the current instance {@link OddsFeedConfigurationBuilder}
     */
    OddsFeedConfigurationBuilder setMessagingHost(String host);

    /**
     * Specify the host used for API access (if not specified this defaults to
     * api.betradar.com)
     *
     * @param apiHost the host used for API access
     * @return the current instance {@link OddsFeedConfigurationBuilder}
     */
    OddsFeedConfigurationBuilder setApiHost(String apiHost);

    /**
     * Sets the port used to connect to AMQP broker
     *
     * @param port the port used to connect to AMQP broker
     * @return the current instance {@link OddsFeedConfigurationBuilder}
     */
    OddsFeedConfigurationBuilder setMessagingPort(int port);

    /**
     * Specify how many seconds is the maximum length of inactivity (default and lowest 20 seconds, highest 180s)
     *
     * @param inactivitySeconds the number of seconds of inactivity before flagging a producer as down
     * @return the current instance {@link OddsFeedConfigurationBuilder}
     */
    OddsFeedConfigurationBuilder setInactivitySeconds(int inactivitySeconds);

    /**
     * Specify the maximum execution time of a recovery request. (default and highest 6 hours, lowest 15m)
     *
     * @param executionMinutes the number of minutes before the recovery request is repeated
     * @return the current instance {@link OddsFeedConfigurationBuilder}
     */
    OddsFeedConfigurationBuilder setMaxRecoveryExecutionMinutes(int executionMinutes);

    /**
     * Set the password of the broker to which you are connecting - this is not required for the connection to the
     * default Sportradar AMQP servers
     *
     * @param password - the password used to connect to the AMQP broker(ex: your testing replay server)
     * @return the current instance {@link OddsFeedConfigurationBuilder}
     */
    OddsFeedConfigurationBuilder setMessagingPassword(String password);

    /**
     * Set the node identifier of the currently running SDK instance. If you run multiple SDK instances on different systems,
     * the node id should be different on each one of them.
     *
     * @param id the node identifier
     * @return the current instance {@link OddsFeedConfigurationBuilder}
     */
    OddsFeedConfigurationBuilder setSdkNodeId(int id);

    /**
     * Set the setting property which will ensure the SDK connects to the integration environment
     * (please note that the integration environment requires different access tokens than the production ones)
     *
     * @deprecated in favour of {{@link #setUseIntegrationEnvironment(boolean)}} from v2.0.18
     *
     * @param useStagingEnvironment an indication if the integration environment should be used
     * @return the current instance {@link OddsFeedConfigurationBuilder}
     */
    @Deprecated
    default OddsFeedConfigurationBuilder setUseStagingEnvironment(boolean useStagingEnvironment) {
        return setUseIntegrationEnvironment(useStagingEnvironment);
    }

    /**
     * Set the setting property which will ensure the SDK connects to the integration environment
     * (please note that the integration environment requires different access tokens than the production ones)
     *
     * @param useIntegrationEnvironment an indication if the integration environment should be used
     * @return the current instance {@link OddsFeedConfigurationBuilder}
     */
    OddsFeedConfigurationBuilder setUseIntegrationEnvironment(boolean useIntegrationEnvironment);

    /**
     * Set a list of producer identifiers which should be disabled automatically when the SDK starts
     *
     * @param disabledProducers a {@link List} of producer identifiers which should be disabled
     * @return the current instance {@link OddsFeedConfigurationBuilder}
     */
    OddsFeedConfigurationBuilder setDisabledProducers(List<Integer> disabledProducers);

    /**
     * Reads the SDK properties file and sets the available properties
     *
     * @return the current instance {@link OddsFeedConfigurationBuilder}
     */
    OddsFeedConfigurationBuilder loadConfigFromSdkProperties();

    /**
     * Builds an {@link OddsFeedConfiguration} instance with the provided data
     *
     * @return - a new  {@link OddsFeedConfiguration} instance built with the provided data
     */
    OddsFeedConfiguration build();
}
