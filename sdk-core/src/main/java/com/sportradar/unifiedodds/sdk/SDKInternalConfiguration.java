/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */

package com.sportradar.unifiedodds.sdk;

import com.google.common.base.Preconditions;
import com.google.common.base.Strings;
import com.sportradar.unifiedodds.sdk.cfg.Environment;
import com.sportradar.unifiedodds.sdk.cfg.OddsFeedConfiguration;

import java.util.*;

/**
 * The internal SDK configuration
 */
public class SDKInternalConfiguration {
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
    private final Environment selectedEnvironment;

    SDKInternalConfiguration(OddsFeedConfiguration cfg,
                             SDKConfigurationPropertiesReader sdkConfigurationPropertiesReader,
                             SDKConfigurationYamlReader sdkConfigurationYamlReader) {
        this(cfg, false, sdkConfigurationPropertiesReader, sdkConfigurationYamlReader);
    }

    SDKInternalConfiguration(OddsFeedConfiguration cfg,
                             boolean setReplaySession,
                             SDKConfigurationPropertiesReader sdkConfigurationPropertiesReader,
                             SDKConfigurationYamlReader sdkConfigurationYamlReader) {
        Preconditions.checkNotNull(cfg);
        Preconditions.checkNotNull(sdkConfigurationPropertiesReader);
        Preconditions.checkNotNull(sdkConfigurationYamlReader);

        host = cfg.getMessagingHost();
        apiHost = cfg.getAPIHost();
        useApiSsl = cfg.getUseApiSsl();
        useMessagingSsl = cfg.getUseMessagingSsl();
        port = cfg.getPort();

        accessToken = cfg.getAccessToken();
        defaultLocale = cfg.getDefaultLocale();
        desiredLocales = cfg.getDesiredLocales();
        inactivitySeconds = cfg.getLongestInactivityInterval();
        maxRecoveryExecutionMinutes = cfg.getMaxRecoveryExecutionMinutes();
        minIntervalBetweenRecoveryRequests = cfg.getMinIntervalBetweenRecoveryRequests();
        messagingUsername = cfg.getMessagingUsername();
        messagingPassword = cfg.getMessagingPassword();
        messagingVirtualHost = cfg.getMessagingVirtualHost();
        isReplaySession = setReplaySession;
        sdkNodeId = cfg.getSdkNodeId();
        disabledProducers = cfg.getDisabledProducers();
        exceptionHandlingStrategy = cfg.getExceptionHandlingStrategy();
        selectedEnvironment = cfg.getEnvironment();

        cleanTrafficLogEntries = sdkConfigurationPropertiesReader.readCleanTrafficLogEntries()
                .orElse(sdkConfigurationYamlReader.readCleanTrafficLogEntries()
                        .orElse(false));
        httpClientTimeout = sdkConfigurationPropertiesReader.readHttpClientTimeout()
                .orElse(sdkConfigurationYamlReader.readHttpClientTimeout()
                        .orElse(30));
        recoveryHttpClientTimeout = sdkConfigurationPropertiesReader.readRecoveryHttpClientTimeout()
                .orElse(sdkConfigurationYamlReader.readRecoveryHttpClientTimeout()
                        .orElse(httpClientTimeout));
        simpleVariantCaching = sdkConfigurationPropertiesReader.readSimpleVariantCaching()
                .orElse(sdkConfigurationYamlReader.readSimpleVariantCaching()
                        .orElse(false));

        schedulerTasksToSkip = new HashSet<>();
        schedulerTasksToSkip.addAll(sdkConfigurationPropertiesReader.readSchedulerTasksToSkip());
        schedulerTasksToSkip.addAll(sdkConfigurationYamlReader.readSchedulerTasksToSkip());
    }

    /**
     * @return Host / IP for connection as provided by Sportradar
     */
    public String getMessagingHost() {
        if (isReplaySession && host.equals("mq.betradar.com"))
            return "replaymq.betradar.com";
        return host;
    }

    /**
     * @return The Sportradar host used for API-access
     */
    public String getAPIHost() { return apiHost; }

    /**
     * @return The selected environment used for API-access
     */
    public Environment getEnvironment() { return selectedEnvironment; }

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
        if (!desiredLocales.contains(defaultLocale))
            desiredLocales.add(0, defaultLocale);
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
     * Updates the API host - this method can be used only while in replay mode, no other SDK modes support this
     *
     * @param newApiHost the new API host
     */
    public void updateApiHost(String newApiHost) {
        Preconditions.checkArgument(!Strings.isNullOrEmpty(newApiHost));

        if (isReplaySession) {
            apiHost = newApiHost;
        }
    }

    @Override
    public String toString() {
        String obfuscatedToken = String.format("%s***%s", accessToken.substring(0, 3), accessToken.substring(accessToken.length()-3));

        return new StringJoiner(", ", SDKInternalConfiguration.class.getSimpleName() + "[", "]")
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
                .add("recoveryHttpClientTimeout=" + recoveryHttpClientTimeout)
                .add("disabledProducers=" + disabledProducers)
                .add("simpleVariantCaching=" + simpleVariantCaching)
                .add("schedulerTasksToSkip=" + schedulerTasksToSkip)
                .add("messagingVirtualHost='" + messagingVirtualHost + "'")
                .add("apiHost='" + apiHost + "'")
                .add("selectedEnvironment=" + selectedEnvironment)
                .toString();
    }
}
