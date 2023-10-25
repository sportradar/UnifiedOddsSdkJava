/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */

package com.sportradar.unifiedodds.sdk.cfg;

import java.time.Duration;

public interface UofApiConfiguration {
    /**
     * @return The Sportradar host used for API-access
     */
    String getHost();

    /**
     * @return The port of Sportradar host used for API-access
     */
    int getPort();

    /**
     * Gets a value indicating whether SSL should be used when requesting API endpoints
     * @return a value indicating whether SSL should be used when requesting API endpoints
     */
    boolean getUseSsl();

    /**
     * Get the timeout which should be used on HTTP requests (seconds)
     * @return the timeout which should be used when performing HTTP requests (seconds)
     */
    Duration getHttpClientTimeout();

    /**
     * Get the timeout which should be used on HTTP requests for recovery requests (seconds)
     * @return the timeout which should be used when performing HTTP requests for recovery requests (seconds)
     */
    Duration getHttpClientRecoveryTimeout();

    /**
     * Get the timeout which should be used for fast failing HTTP requests (seconds)
     * @return the timeout which should be used for fast failing HTTP requests (seconds)
     */
    Duration getHttpClientFastFailingTimeout();

    /**
     * Returns connection pool size for http client
     * @return connection pool size for http client
     */
    int getHttpClientMaxConnTotal();

    /**
     * Returns maximum number of concurrent connections per route for http client
     * @return maximum number of concurrent connections per route for http client
     */
    int getHttpClientMaxConnPerRoute();

    /**
     * @return The host used when connecting to Replay Server
     */
    String getReplayHost();
}
