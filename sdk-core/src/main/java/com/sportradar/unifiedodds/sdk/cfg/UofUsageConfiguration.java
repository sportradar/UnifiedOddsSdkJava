/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */
package com.sportradar.unifiedodds.sdk.cfg;

public interface UofUsageConfiguration {
    /**
     * Indicates if the SDK usage (metrics) export is enabled
     * <p>
     * Default value is true
     */
    boolean isExportEnabled();

    /**
     * Get the interval in seconds in which the usage metrics are exported
     *
     * @return the export interval in seconds
     */
    int getExportIntervalInSec();

    /**
     * Get the timeout for exporting usage metrics
     *
     * @return the export timeout in seconds
     */
    int getExportTimeoutInSec();

    /**
     * Get the host to which the usage metrics are exported
     *
     * @return the host
     */
    String getHost();
}
