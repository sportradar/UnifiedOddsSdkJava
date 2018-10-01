/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */

package com.sportradar.unifiedodds.sdk.cfg;

import com.sportradar.unifiedodds.sdk.ExceptionHandlingStrategy;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Locale;

/**
 * This class is used to specify various configuration parameters for a session to the Sportradar
 * system(s)
 *
 */
public class OddsFeedConfiguration {
    private final String accessToken;
    private final Locale defaultLocale;
    private final List<Locale> desiredLocales;
    private final String host;
    private final String apiHost;
    private final int inactivitySeconds;
    private final int maxRecoveryExecutionMinutes;
    private final boolean useMessagingSsl;
    private final boolean useApiSsl;
    private final int port;
    private final String messagingUsername;
    private final String messagingPassword;
    private final Integer sdkNodeId;
    private final boolean useStagingEnvironment;
    private final List<Integer> disabledProducers;
    private final ExceptionHandlingStrategy exceptionHandlingStrategy;
    private final Environment selectedEnvironment;
    private final String messagingVirtualHost;

    OddsFeedConfiguration(String accessToken, Locale defaultLocale, List<Locale> desiredLocales,
                          String host, String apiHost, int inactivitySeconds, int maxRecoveryExecutionMinutes,
                          boolean useMessagingSsl, boolean useApiSsl, int port, String messagingUsername, String messagingPassword, Integer sdkNodeId,
                          boolean useStagingEnvironment, List<Integer> disabledProducers, ExceptionHandlingStrategy exceptionHandlingStrategy, Environment selectedEnvironment,
                          String messagingVirtualHost) {
        // ctor parameters are validated in the cfg builder instance
        this.accessToken = accessToken;
        this.defaultLocale = defaultLocale;
        this.desiredLocales = desiredLocales;
        this.host = host;
        this.apiHost = apiHost;
        this.inactivitySeconds = inactivitySeconds;
        this.maxRecoveryExecutionMinutes = maxRecoveryExecutionMinutes;
        this.useMessagingSsl = useMessagingSsl;
        this.useApiSsl = useApiSsl;
        this.port = port;
        this.messagingUsername = messagingUsername;
        this.messagingPassword = messagingPassword;
        this.sdkNodeId = sdkNodeId;
        if (sdkNodeId < 0)
        {
            LoggerFactory.getLogger(OddsFeedConfiguration.class).warn(String.format("Setting nodeId to %s. Use only positive numbers; negative are reserved for internal use.", sdkNodeId));
        }
        this.useStagingEnvironment = useStagingEnvironment;
        this.disabledProducers = disabledProducers;
        this.exceptionHandlingStrategy = exceptionHandlingStrategy;
        this.selectedEnvironment = selectedEnvironment;
        this.messagingVirtualHost = messagingVirtualHost;
    }

    /**
     * @return Host / IP for connection as provided by Sportradar
     */
    public String getMessagingHost() {
        return host;
    }

    /**
     * @return The Sportradar host used for API-access
     */
    public String getAPIHost() {
        return apiHost;
    }

    /**
     * @return The longest inactivity interval between producer alive messages(seconds)
     */
    public int getLongestInactivityInterval() {
        return inactivitySeconds;
    }

    /**
     * @return The max recovery execution time, after which the recovery request is repeated(minutes)
     */
    public int getMaxRecoveryExecutionMinutes() {
        return maxRecoveryExecutionMinutes;
    }

    /**
     * 
     * @return your access token that is used to identify and verify your identity
     */
    public String getAccessToken() {
        return accessToken;
    }

    /**
     * Gets a value indicating whether SSL should be used when connecting to AMQP broker
     *
     * @return a value indicating whether SSL should be used when connecting to AMQP broker
     */
    public boolean getUseMessagingSsl() {
        return useMessagingSsl;
    }

    /**
     * Gets a value indicating whether SSL should be used when requesting API endpoints
     *
     * @return a value indicating whether SSL should be used when requesting API endpoints
     */
    public boolean getUseApiSsl() {
        return useApiSsl;
    }

    /**
     * Gets the port used to connect to AMQP broker
     * 
     * @return the port used to connect to AMQP broker
     */
    public int getPort() {
        return port;
    }

    /**
     * The default locale used for any getter that returns localized Strings. (i.e. Sport name,
     * Tournament name, Team name, Player name etc). The default locale is English if not specified.
     * 
     * @return the default locale
     */
    public Locale getDefaultLocale() {
        return defaultLocale;
    }

    /**
     * Returns a {@link List} of locales in which the data should be prefetched
     *
     * @return a {@link List} of locales in which the data should be prefetched
     */
    public List<Locale> getDesiredLocales() {
        return desiredLocales;
    }

    /**
     * Returns the username of the broker to which you are currently connecting - this field should be null/blank if
     * you are connecting to the default Sportradar AMQP servers
     *
     * @return the username of the broker to which you are connecting
     */
    public String getMessagingUsername() {
        return messagingUsername;
    }

    /**
     * Returns the password of the broker to which you are connecting - this field should be null/blank if
     * you are connecting to the default Sportradar AMQP servers
     *
     * @return the password of the broker to which you are connecting
     */
    public String getMessagingPassword() {
        return messagingPassword;
    }

    /**
     * Returns the custom set messaging virtual host
     *
     * @return the custom messaging virtual host
     */
    public String getMessagingVirtualHost() {
        return messagingVirtualHost;
    }

    /**
     * Returns the assigned SDK node identifier
     *
     * @return the assigned SDK node identifier
     */
    public Integer getSdkNodeId() {
        return sdkNodeId;
    }

    /**
     * Returns an indication if the SDK should connect to the staging environment
     *
     * @return <code>true</code> if the SDK should connect to the staging environment, otherwise <code>false</code>
     */
    public boolean getUseStagingEnvironment() {
        return useStagingEnvironment;
    }

    /**
     * Returns a list of producer identifiers which should be disabled automatically when the SDK starts up
     *
     * @return a list of producer identifiers which should be disabled automatically when the sdk starts up
     */
    public List<Integer> getDisabledProducers() {
        return disabledProducers;
    }

    /**
     * Returns the {@link ExceptionHandlingStrategy} which will be used trough the SDK
     *
     * @return the {@link ExceptionHandlingStrategy} which will be used trough the SDK
     */
    public ExceptionHandlingStrategy getExceptionHandlingStrategy() {
        return exceptionHandlingStrategy;
    }

    @Override
    public String toString() {

        String obfuscatedToken = String.format("%s***%s", accessToken.substring(0, 3), accessToken.substring(accessToken.length()-3));

        return "OddsFeedConfiguration{" +
                "\n\taccessToken='" + obfuscatedToken + '\'' +
                ",\n\tdefaultLocale=" + defaultLocale +
                ",\n\tdesiredLocales=" + desiredLocales +
                ",\n\thost='" + host + '\'' +
                ",\n\tapiHost='" + apiHost + '\'' +
                ",\n\tinactivitySeconds=" + inactivitySeconds +
                ",\n\tmaxRecoveryExecutionMinutes=" + maxRecoveryExecutionMinutes +
                ",\n\tuseMessagingSsl=" + useMessagingSsl +
                ",\n\tuseApiSsl=" + useApiSsl +
                ",\n\tport=" + port +
                ",\n\tmessagingUsername='" + messagingUsername + '\'' +
                ",\n\tmessagingPassword='" + messagingPassword + '\'' +
                ",\n\tsdkNodeId=" + sdkNodeId +
                ",\n\tuseStagingEnvironment=" + useStagingEnvironment +
                ",\n\tdisabledProducers=" + disabledProducers +
                ",\n\texceptionHandlingStrategy=" + exceptionHandlingStrategy +
                ",\n\tselectedEnvironment=" + selectedEnvironment +
                ",\n\tmessagingVirtualHost=" + messagingVirtualHost +
                "\n}";
    }
}
