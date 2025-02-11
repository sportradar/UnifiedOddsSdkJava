/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */

package com.sportradar.unifiedodds.sdk.cfg;

/**
 * Defines methods implemented by classes used to set recovery related configuration properties
 */
@SuppressWarnings({ "LineLength" })
public interface RecoveryConfigurationBuilder<T> extends ConfigurationBuilderBase<T> {
    /**
     * Sets the max time window between two consecutive alive messages before the associated producer is marked as down
     *
     * @param inactivitySeconds the max time window between two consecutive alive messages
     * @return a {@link RecoveryConfigurationBuilder} derived instance used to set general configuration properties
     */
    T setInactivitySeconds(int inactivitySeconds);

    /**
     * Sets the max time window between two consecutive alive messages before the prematch producer is marked as down
     *
     * @param inactivitySeconds the max time window between two consecutive alive messages
     * @return a {@link RecoveryConfigurationBuilder} derived instance used to set general configuration properties
     */
    T setInactivitySecondsPrematch(int inactivitySeconds);

    /**
     * Sets the maximum time in seconds in which recovery must be completed (minimum 600 seconds)
     *
     * @param timeInSeconds the value in seconds
     * @return a {@link RecoveryConfigurationBuilder} derived instance used to set general configuration properties
     */
    T setMaxRecoveryTime(int timeInSeconds);

    /**
     * Sets the minimal time between two successive recovery requests initiated by alive messages (minimum 20 seconds)
     *
     * @param intervalSeconds the minimal time between two successive recovery requests initiated by alive messages (default 30)
     * @return a {@link RecoveryConfigurationBuilder} derived instance used to set general configuration properties
     */
    T setMinIntervalBetweenRecoveryRequests(int intervalSeconds);

    /**
     * Sets the timeout which should be used on HTTP requests (seconds)
     *
     * @return a {@link RecoveryConfigurationBuilder} derived instance used to set general configuration properties
     */
    T setHttpClientTimeout(int httpClientTimeout);

    /**
     * Sets the timeout which should be used on HTTP requests for recovery endpoints (seconds)
     *
     * @return a {@link RecoveryConfigurationBuilder} derived instance used to set general configuration properties
     */
    T setHttpClientRecoveryTimeout(int httpClientRecoveryTimeout);

    /**
     * Sets the timeout which should be used on HTTP requests for fast failing endpoints (seconds)
     *
     * @return a {@link RecoveryConfigurationBuilder} derived instance used to set general configuration properties
     */
    T setHttpClientFastFailingTimeout(int httpClientFastFailingTimeout);

    /**
     * Sets connection pool size for http client.
     * Should be set to low value to avoid resource overuse.
     * Default: 20
     *
     * @return a {@link RecoveryConfigurationBuilder} derived instance used to set general configuration properties
     */
    T setHttpClientMaxConnTotal(int httpClientMaxConnTotal);

    /**
     * Sets maximum number of concurrent connections per route for http client.
     * Should be set to low value to avoid resource overuse.
     * Default: 15
     *
     * @return a {@link RecoveryConfigurationBuilder} derived instance used to set general configuration properties
     */
    T setHttpClientMaxConnPerRoute(int httpClientMaxConnPerRoute);

    /**
     * Sets the timeout for cache items in SportEventCache (in hours)
     *
     * @return a {@link RecoveryConfigurationBuilder} derived instance used to set general configuration properties
     */
    T setSportEventCacheTimeout(int timeoutInHours);

    /**
     * Sets the timeout for cache items in SportEventStatusCache (in minutes)
     *
     * @return a {@link RecoveryConfigurationBuilder} derived instance used to set general configuration properties
     */
    T setSportEventStatusCacheTimeout(int timeoutInMinutes);

    /**
     * Sets the timeout for cache items in ProfileCache (in hours)
     *
     * @return a {@link RecoveryConfigurationBuilder} derived instance used to set general configuration properties
     */
    T setProfileCacheTimeout(int timeoutInHours);

    /**
     * Sets the timeout for cache items in single variant market description cache (in hours)
     *
     * @return a {@link RecoveryConfigurationBuilder} derived instance used to set general configuration properties
     */
    T setVariantMarketDescriptionCacheTimeout(int timeoutInHours);

    /**
     * Sets the ignore BetPal timeline sport event status cache timeout. (in hours)
     * How long should the event id from BetPal be cached.
     * SportEventStatus from timeline endpoint for these events are ignored.
     *
     * @return a {@link RecoveryConfigurationBuilder} derived instance used to set general configuration properties
     */
    T setIgnoreBetPalTimelineSportEventStatusCacheTimeout(int timeoutInHours);

    /**
     * Sets the value indicating whether to ignore sport event status from timeline endpoint
     * for sport events on BetPal producer
     *
     * @param ignore if set to true ignore
     * @return a {@link RecoveryConfigurationBuilder} derived instance used to set general configuration properties
     */
    T setIgnoreBetPalTimelineSportEventStatus(boolean ignore);

    /**
     * Sets the rabbit timeout setting for connection attempts (in seconds)
     * @param timeoutInSeconds the rabbit connection timeout
     * @return a {@link ConfigurationBuilderBase} derived instance used to set general configuration properties
     */
    T setRabbitConnectionTimeout(int timeoutInSeconds);

    /**
     * Sets a heartbeat timeout to use when negotiating with the rabbit server (in seconds)
     * @param heartbeatInSeconds a heartbeat timeout
     * @return a {@link ConfigurationBuilderBase} derived instance used to set general configuration properties
     */
    T setRabbitHeartbeat(int heartbeatInSeconds);

    /**
     * Sets the interval for automatically collecting statistics (in minutes)
     * @param intervalInMinutes the interval for automatically collecting statistics
     * @return a {@link ConfigurationBuilderBase} derived instance used to set general configuration properties
     */
    T setStatisticsInterval(int intervalInMinutes);

    /**
     * Sets the value indicating whether to ignore market mappings when fetching market descriptions from API
     *
     * @param omit if set to true
     * @return a {@link RecoveryConfigurationBuilder} derived instance used to set general configuration properties
     */
    T omitMarketMappings(boolean omit);

    /**
     * Sets the value indicating whether sdk usage (metrics) can be exported
     *
     * @param enableUsageExport if set to true export is permitted, otherwise not
     * @return a {@link RecoveryConfigurationBuilder} derived instance used to set general configuration properties
     */
    T enableUsageExport(boolean enableUsageExport);
}
