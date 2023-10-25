/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */

package com.sportradar.unifiedodds.sdk.cfg;

import java.time.Duration;

/**
 * This class is used to specify additional configuration
 */
public interface UofAdditionalConfiguration {
    /**
     * Indicates if the market mapping should be included when requesting market descriptions from API
     * @return false if market mappings are included (default)
     */
    boolean omitMarketMappings();

    /**
     * Gets the timeout for automatically collecting statistics (in minutes)
     * Setting to 0 indicates it is disabled
     * @return the timeout for automatically collecting statistics (in minutes)
     */
    Duration getStatisticsInterval();
}
