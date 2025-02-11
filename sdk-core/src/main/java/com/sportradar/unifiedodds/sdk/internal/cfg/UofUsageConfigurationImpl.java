/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */
package com.sportradar.unifiedodds.sdk.internal.cfg;

import com.sportradar.unifiedodds.sdk.cfg.UofUsageConfiguration;
import java.util.StringJoiner;

public class UofUsageConfigurationImpl implements UofUsageConfiguration {

    private boolean exportEnabled;
    private final int exportIntervalInSec;
    private final int exportTimeoutInSec;
    private String host;

    UofUsageConfigurationImpl() {
        exportEnabled = true;
        exportIntervalInSec = ConfigLimit.USAGE_EXPORT_INTERVAL_SEC;
        exportTimeoutInSec = ConfigLimit.USAGE_EXPORT_TIMEOUT_SEC;
        host = null;
    }

    public void setExportEnabled(boolean exportEnabled) {
        this.exportEnabled = exportEnabled;
    }

    public void setHost(String host) {
        this.host = host;
    }

    @Override
    public boolean isExportEnabled() {
        return exportEnabled;
    }

    @Override
    public int getExportIntervalInSec() {
        return exportIntervalInSec;
    }

    @Override
    public int getExportTimeoutInSec() {
        return exportTimeoutInSec;
    }

    @Override
    public String getHost() {
        return host;
    }

    @Override
    public String toString() {
        return new StringJoiner(", ", "UsageConfiguration{", "}")
            .add("isExportEnabled=" + exportEnabled)
            .add("exportIntervalInSec=" + exportIntervalInSec)
            .add("exportTimeoutInSec=" + exportTimeoutInSec)
            .add("host='" + host + "'")
            .toString();
    }
}
