/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */

package com.sportradar.unifiedodds.sdk.cfg;

import com.sportradar.unifiedodds.sdk.ExceptionHandlingStrategy;
import com.sportradar.utils.SdkHelper;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Locale;

/**
 * This class is used to specify various configuration parameters for a session to the Sportradar
 * system(s)
 *
 */
public class OddsFeedConfiguration {
    private static final int HTTP_CLIENT_TIMEOUT = 30;
    private static final int HTTP_CLIENT_MAX_CONN_TOTAL = 20;
    private static final int HTTP_CLIENT_MAX_CONN_PER_ROUTE = 15;
    private static final int RECOVERY_HTTP_CLIENT_TIMEOUT = 30;
    private static final int RECOVERY_HTTP_CLIENT_MAX_CONN_TOTAL = 20;
    private static final int RECOVERY_HTTP_CLIENT_MAX_CONN_PER_ROUTE = 15;

    private final String accessToken;
    private final Locale defaultLocale;
    private final List<Locale> desiredLocales;
    private final String host;
    private final String apiHost;
    private final int inactivitySeconds;
    private final int maxRecoveryExecutionMinutes;
    private final int minIntervalBetweenRecoveryRequests;
    private final boolean useMessagingSsl;
    private final boolean useApiSsl;
    private final int port;
    private final String messagingUsername;
    private final String messagingPassword;
    private final Integer sdkNodeId;
    private final boolean useIntegrationEnvironment;
    private final List<Integer> disabledProducers;
    private final ExceptionHandlingStrategy exceptionHandlingStrategy;
    private final Environment selectedEnvironment;
    private final String messagingVirtualHost;
    private final int httpClientTimeout;
    private final int httpClientMaxConnTotal;
    private final int httpClientMaxConnPerRoute;
    private final int recoveryHttpClientTimeout;
    private final int recoveryHttpClientMaxConnTotal;
    private final int recoveryHttpClientMaxConnPerRoute;

    OddsFeedConfiguration(String accessToken,
                          Locale defaultLocale,
                          List<Locale> desiredLocales,
                          String host,
                          String apiHost,
                          int inactivitySeconds,
                          int maxRecoveryExecutionMinutes,
                          int minIntervalBetweenRecoveryRequests,
                          boolean useMessagingSsl,
                          boolean useApiSsl,
                          int port,
                          String messagingUsername,
                          String messagingPassword,
                          Integer sdkNodeId,
                          boolean useIntegrationEnvironment,
                          List<Integer> disabledProducers,
                          ExceptionHandlingStrategy exceptionHandlingStrategy,
                          Environment selectedEnvironment,
                          String messagingVirtualHost,
                          Integer httpClientTimeout,
                          Integer httpClientMaxConnTotal,
                          Integer httpClientMaxConnPerRoute,
                          Integer recoveryHttpClientTimeout,
                          Integer recoveryHttpClientMaxConnTotal,
                          Integer recoveryHttpClientMaxConnPerRoute) {
        // ctor parameters are validated in the cfg builder instance
        this.accessToken = accessToken;
        this.defaultLocale = defaultLocale;
        this.desiredLocales = desiredLocales;
        this.host = host;
        this.apiHost = apiHost;
        this.inactivitySeconds = inactivitySeconds;
        this.maxRecoveryExecutionMinutes = maxRecoveryExecutionMinutes;
        this.minIntervalBetweenRecoveryRequests = minIntervalBetweenRecoveryRequests;
        this.useMessagingSsl = useMessagingSsl;
        this.useApiSsl = useApiSsl;
        this.port = port;
        this.messagingUsername = messagingUsername;
        this.messagingPassword = messagingPassword;
        this.sdkNodeId = sdkNodeId;
        this.httpClientTimeout = httpClientTimeout != null ? httpClientTimeout : HTTP_CLIENT_TIMEOUT;
        this.httpClientMaxConnTotal = httpClientMaxConnTotal != null ? httpClientMaxConnTotal : HTTP_CLIENT_MAX_CONN_TOTAL;
        this.httpClientMaxConnPerRoute = httpClientMaxConnPerRoute != null ? httpClientMaxConnPerRoute : HTTP_CLIENT_MAX_CONN_PER_ROUTE;
        this.recoveryHttpClientTimeout = recoveryHttpClientTimeout != null ? recoveryHttpClientTimeout : RECOVERY_HTTP_CLIENT_TIMEOUT;
        this.recoveryHttpClientMaxConnTotal = recoveryHttpClientMaxConnTotal != null ? recoveryHttpClientMaxConnTotal : RECOVERY_HTTP_CLIENT_MAX_CONN_TOTAL;
        this.recoveryHttpClientMaxConnPerRoute = recoveryHttpClientMaxConnPerRoute != null ? recoveryHttpClientMaxConnPerRoute : RECOVERY_HTTP_CLIENT_MAX_CONN_PER_ROUTE;
        if (sdkNodeId != null && sdkNodeId < 0)
        {
            LoggerFactory.getLogger(OddsFeedConfiguration.class).warn(String.format("Setting nodeId to %s. Use only positive numbers; negative are reserved for internal use.", sdkNodeId));
        }
        this.useIntegrationEnvironment = useIntegrationEnvironment;
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
     * @return The selected environment used for API-access
     */
    public Environment getEnvironment() { return selectedEnvironment; }

    /**
     * @return The longest inactivity interval between producer alive messages(seconds)
     */
    public int getLongestInactivityInterval() { return inactivitySeconds; }

    /**
     * @return The max recovery execution time, after which the recovery request is repeated(minutes)
     */
    public int getMaxRecoveryExecutionMinutes() {
        return maxRecoveryExecutionMinutes;
    }

    /**
     * @return The minimal interval between recovery requests initiated by alive messages(seconds)
     */
    public int getMinIntervalBetweenRecoveryRequests() { return minIntervalBetweenRecoveryRequests; }

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
     * MTS customer must set this value! Use only positive numbers; negative are reserved for internal use.
     *
     * @return the assigned SDK node identifier
     */
    public Integer getSdkNodeId() {
        return sdkNodeId;
    }

    /**
     * Returns an indication if the SDK should connect to the integration environment
     *
     * @deprecated in favour of {{@link #getUseIntegrationEnvironment()}} from v2.0.18
     *
     * @return <code>true</code> if the SDK should connect to the integration environment, otherwise <code>false</code>
     */
    @Deprecated
    public boolean getUseStagingEnvironment() {
        return useIntegrationEnvironment;
    }

    /**
     * Returns an indication if the SDK should connect to the integration environment
     *
     * @return <code>true</code> if the SDK should connect to the integration environment, otherwise <code>false</code>
     */
    public boolean getUseIntegrationEnvironment() {
        return useIntegrationEnvironment;
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

    /**
     * Indicates the timeout which should be used on HTTP requests(seconds)
     *
     * @return the timeout which should be used when performing HTTP requests(seconds)
     */
    public int getHttpClientTimeout() {
        return httpClientTimeout;
    }

    /**
     * Returns connection pool size for http client
     *
     * @return connection pool size for http client
     */
    public int getHttpClientMaxConnTotal() {
        return httpClientMaxConnTotal;
    }

    /**
     * Returns maximum number of concurrent connections per route for http client
     *
     * @return maximum number of concurrent connections per route for http client
     */
    public int getHttpClientMaxConnPerRoute() {
        return httpClientMaxConnPerRoute;
    }

    /**
     * Indicates the timeout which should be used on HTTP requests for recovery endpoints(seconds)
     *
     * @return the timeout which should be used when performing HTTP requests for recovery endpoints(seconds)
     */
    public int getRecoveryHttpClientTimeout() {
        return recoveryHttpClientTimeout;
    }

    /**
     * Returns connection pool size for recovery http client
     *
     * @return connection pool size for recovery http client
     */
    public int getRecoveryHttpClientMaxConnTotal() {
        return recoveryHttpClientMaxConnTotal;
    }

    /**
     * Returns maximum number of concurrent connections per route for recovery http client
     *
     * @return maximum number of concurrent connections per route for recovery http client
     */
    public int getRecoveryHttpClientMaxConnPerRoute() {
        return recoveryHttpClientMaxConnPerRoute;
    }

    @Override
    public String toString() {

        String obfuscatedToken = SdkHelper.obfuscate(accessToken);

        return "OddsFeedConfiguration{" +
                " accessToken='" + obfuscatedToken + '\'' +
                ", defaultLocale=" + defaultLocale +
                ", desiredLocales=" + desiredLocales +
                ", host='" + host + '\'' +
                ", apiHost='" + apiHost + '\'' +
                ", inactivitySeconds=" + inactivitySeconds +
                ", maxRecoveryExecutionMinutes=" + maxRecoveryExecutionMinutes +
                ", minIntervalBetweenRecoveryRequests=" + minIntervalBetweenRecoveryRequests +
                ", useMessagingSsl=" + useMessagingSsl +
                ", useApiSsl=" + useApiSsl +
                ", port=" + port +
                ", messagingUsername='" + messagingUsername + '\'' +
                ", messagingPassword='" + messagingPassword + '\'' +
                ", sdkNodeId=" + sdkNodeId +
                ", useIntegrationEnvironment=" + useIntegrationEnvironment +
                ", disabledProducers=" + disabledProducers +
                ", exceptionHandlingStrategy=" + exceptionHandlingStrategy +
                ", selectedEnvironment=" + selectedEnvironment +
                ", messagingVirtualHost=" + messagingVirtualHost +
                ", httpClientTimeout=" + httpClientTimeout +
                ", httpClientMaxConnTotal=" + httpClientMaxConnTotal +
                ", httpClientMaxConnPerRoute=" + httpClientMaxConnPerRoute +
                ", recoveryHttpClientTimeout=" + recoveryHttpClientTimeout +
                ", recoveryHttpClientMaxConnTotal=" + recoveryHttpClientMaxConnTotal +
                ", recoveryHttpClientMaxConnPerRoute=" + recoveryHttpClientMaxConnPerRoute +
                "}";
    }

}
