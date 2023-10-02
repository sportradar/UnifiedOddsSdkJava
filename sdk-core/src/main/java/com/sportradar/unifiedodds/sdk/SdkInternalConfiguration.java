/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */

package com.sportradar.unifiedodds.sdk;

import com.google.common.base.Preconditions;
import com.google.common.base.Strings;
import com.sportradar.unifiedodds.sdk.cfg.Environment;
import com.sportradar.unifiedodds.sdk.cfg.SdkConfigurationPropertiesReader;
import com.sportradar.unifiedodds.sdk.cfg.SdkConfigurationYamlReader;
import com.sportradar.unifiedodds.sdk.cfg.UofConfiguration;
import com.sportradar.utils.SdkHelper;
import java.util.*;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * The internal SDK configuration
 */
@SuppressWarnings(
    { "ExecutableStatementCount", "HiddenField", "MagicNumber", "MultipleStringLiterals", "NeedBraces" }
)
public class SdkInternalConfiguration {

    private final String accessToken;
    private final Locale defaultLocale;
    private final List<Locale> desiredLocales;
    private final String host;
    private final int inactivitySeconds;
    private final int maxRecoveryExecutionMinutes;
    private final int minIntervalBetweenRecoveryRequests;
    private final boolean useMessagingSsl;
    private final boolean useApiSsl;
    private final int port;
    private final boolean isReplaySession;
    private final String messagingUsername;
    private final String messagingPassword;
    private final ExceptionHandlingStrategy exceptionHandlingStrategy;
    private final Integer sdkNodeId;
    private final boolean cleanTrafficLogEntries;
    private final int httpClientTimeout;
    private final int recoveryHttpClientTimeout;
    private final List<Integer> disabledProducers;
    private final boolean simpleVariantCaching;
    private final Set<String> schedulerTasksToSkip;
    private final String messagingVirtualHost;
    private String apiHost;
    private final int apiPort;
    private String apiHostAndPort;
    private final Supplier<Environment> selectedEnvironment;
    private final int httpClientMaxConnTotal;
    private final int httpClientMaxConnPerRoute;
    private final int recoveryHttpClientMaxConnTotal;
    private final int recoveryHttpClientMaxConnPerRoute;

    SdkInternalConfiguration(
        UofConfiguration cfg,
        SdkConfigurationPropertiesReader sdkConfigurationPropertiesReader,
        SdkConfigurationYamlReader sdkConfigurationYamlReader
    ) {
        this(cfg, false, sdkConfigurationPropertiesReader, sdkConfigurationYamlReader);
    }

    SdkInternalConfiguration(
        UofConfiguration cfg,
        boolean setReplaySession,
        SdkConfigurationPropertiesReader sdkConfigurationPropertiesReader,
        SdkConfigurationYamlReader sdkConfigurationYamlReader
    ) {
        Preconditions.checkNotNull(cfg, "cfg");
        Preconditions.checkNotNull(sdkConfigurationPropertiesReader, "sdkConfigurationPropertiesReader");
        Preconditions.checkNotNull(sdkConfigurationYamlReader, "sdkConfigurationYamlReader");

        host = cfg.getRabbit().getHost();
        apiHost = cfg.getApi().getHost();
        apiPort = cfg.getApi().getPort();
        apiHostAndPort = hostAndPort(apiHost, apiPort);
        useApiSsl = cfg.getApi().getUseSsl();
        useMessagingSsl = cfg.getRabbit().getUseSsl();
        port = cfg.getRabbit().getPort();

        accessToken = cfg.getAccessToken();
        defaultLocale = cfg.getDefaultLanguage();
        desiredLocales = cfg.getLanguages();
        inactivitySeconds = (int) (cfg.getProducer().getInactivitySeconds().toMillis() / 1000);
        maxRecoveryExecutionMinutes = (int) (cfg.getProducer().getMaxRecoveryTime().toMillis() / 1000);
        minIntervalBetweenRecoveryRequests =
            (int) (cfg.getProducer().getMinIntervalBetweenRecoveryRequests().toMillis() / 1000);
        messagingUsername = cfg.getRabbit().getUsername();
        messagingPassword = cfg.getRabbit().getPassword();
        messagingVirtualHost = cfg.getRabbit().getVirtualHost();
        isReplaySession = setReplaySession;
        sdkNodeId = cfg.getNodeId();
        disabledProducers = cfg.getProducer().getDisabledProducers();
        exceptionHandlingStrategy = cfg.getExceptionHandlingStrategy();
        selectedEnvironment = () -> cfg.getEnvironment();
        httpClientTimeout = (int) (cfg.getApi().getHttpClientTimeout().toMillis() / 1000);
        httpClientMaxConnTotal = cfg.getApi().getHttpClientMaxConnTotal();
        httpClientMaxConnPerRoute = cfg.getApi().getHttpClientMaxConnPerRoute();
        recoveryHttpClientTimeout = (int) (cfg.getApi().getHttpClientRecoveryTimeout().toMillis() / 1000);
        recoveryHttpClientMaxConnTotal = cfg.getApi().getHttpClientMaxConnTotal();
        recoveryHttpClientMaxConnPerRoute = cfg.getApi().getHttpClientMaxConnPerRoute();

        cleanTrafficLogEntries = false;
        simpleVariantCaching = false;

        schedulerTasksToSkip = new HashSet<>();
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
    public String getApiHost() {
        return apiHost;
    }

    public int getApiPort() {
        return apiPort;
    }

    public String getApiHostAndPort() {
        return apiHostAndPort;
    }

    /**
     * @return The selected environment used for API-access
     */
    public Environment getEnvironment() {
        return selectedEnvironment.get();
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
    public int getMaxRecoveryExecutionSeconds() {
        return maxRecoveryExecutionMinutes;
    }

    /**
     * @return The minimal interval between recovery requests initiated by alive messages(seconds)
     */
    public int getMinIntervalBetweenRecoveryRequests() {
        return minIntervalBetweenRecoveryRequests;
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
     * @return - a {@link List} of locales in which the data should be prefetched
     */
    public List<Locale> getDesiredLocales() {
        if (!desiredLocales.contains(defaultLocale)) desiredLocales.add(0, defaultLocale);
        return desiredLocales;
    }

    /**
     * An indication if the current configuration is setup for use in a replay session
     *
     * @return - an indication if the current configuration is setup for use in a replay session
     */
    public boolean isReplaySession() {
        return isReplaySession;
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
     * connecting to the default Sportradar AMQP servers
     *
     * @return - the password of the broker to which you are connecting
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
     * Returns an indication on how should be the SDK exceptions handled
     *
     * @return an indication on how should be the SDK exceptions handled
     */
    public ExceptionHandlingStrategy getExceptionHandlingStrategy() {
        return exceptionHandlingStrategy;
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
     * Indicates if the received xml payloads should be cleaned from special characters such as new lines or not
     *
     * @return <code>true</code> if the message entries should be cleaned; otherwise <code>false</code>
     */
    public boolean isCleanTrafficLogEntriesEnabled() {
        return cleanTrafficLogEntries;
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
     * Indicates the timeout which should be used on HTTP requests for recovery endpoints(seconds)
     *
     * @return the timeout which should be used when performing HTTP requests for recovery endpoints(seconds)
     */
    public int getRecoveryHttpClientTimeout() {
        return recoveryHttpClientTimeout;
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
     * Returns an indication if the variants should be cached in a more simpler manner
     *
     * @return <code>true</code>
     */
    public boolean getSimpleVariantCaching() {
        return simpleVariantCaching;
    }

    /**
     * Returns a set of task names that should be skipped
     *
     * @return a set of task names that should be skipped
     */
    public Set<String> getSchedulerTasksToSkip() {
        return schedulerTasksToSkip;
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

    /**
     * Updates the API host - this method can be used only while in replay mode, no other SDK modes support this
     *
     * @param newApiHost the new API host
     */
    public void updateApiHost(String newApiHost) {
        Preconditions.checkArgument(!Strings.isNullOrEmpty(newApiHost));

        if (isReplaySession) {
            apiHost = newApiHost;
            apiHostAndPort = hostAndPort(apiHost, apiPort);
        }
    }

    @Override
    public String toString() {
        String obfuscatedToken = SdkHelper.obfuscate(accessToken);

        return new StringJoiner(", ", SdkInternalConfiguration.class.getSimpleName() + "[", "]")
            .add("accessToken='" + obfuscatedToken + "'")
            .add("defaultLocale=" + defaultLocale)
            .add("desiredLocales=" + desiredLocales)
            .add("host='" + host + "'")
            .add("inactivitySeconds=" + inactivitySeconds)
            .add("maxRecoveryExecutionMinutes=" + maxRecoveryExecutionMinutes)
            .add("minIntervalBetweenRecoveryRequests=" + minIntervalBetweenRecoveryRequests)
            .add("useMessagingSsl=" + useMessagingSsl)
            .add("useApiSsl=" + useApiSsl)
            .add("port=" + port)
            .add("isReplaySession=" + isReplaySession)
            .add("messagingUsername='" + messagingUsername + "'")
            .add("messagingPassword='" + messagingPassword + "'")
            .add("exceptionHandlingStrategy=" + exceptionHandlingStrategy)
            .add("sdkNodeId=" + sdkNodeId)
            .add("cleanTrafficLogEntries=" + cleanTrafficLogEntries)
            .add("httpClientTimeout=" + httpClientTimeout)
            .add("httpClientMaxConnTotal=" + httpClientMaxConnTotal)
            .add("httpClientMaxConnPerRoute=" + httpClientMaxConnPerRoute)
            .add("recoveryHttpClientTimeout=" + recoveryHttpClientTimeout)
            .add("recoveryHttpClientMaxConnTotal=" + recoveryHttpClientMaxConnTotal)
            .add("recoveryHttpClientMaxConnPerRoute=" + recoveryHttpClientMaxConnPerRoute)
            .add("disabledProducers=" + disabledProducers)
            .add("simpleVariantCaching=" + simpleVariantCaching)
            .add("schedulerTasksToSkip=" + schedulerTasksToSkip)
            .add("messagingVirtualHost='" + messagingVirtualHost + "'")
            .add("apiHost='" + apiHost + "'")
            .add("apiPort=" + apiPort)
            .add("selectedEnvironment=" + selectedEnvironment)
            .toString();
    }

    private String hostAndPort(String apiHost, int apiPort) {
        return apiHost + (apiPort == 80 || apiPort == 0 ? "" : ":" + apiPort);
    }
}
