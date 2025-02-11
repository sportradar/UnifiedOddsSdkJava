/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */
package com.sportradar.unifiedodds.sdk.internal.common.telemetry;

public enum UsageGauge implements MeterDescriptor {
    PRODUCER_STATUS(
        "uofsdk-producer-status",
        "Current producer status with optional information about the reason"
    );

    private final String meterName;
    private final String description;

    UsageGauge(String meterName, String description) {
        this.meterName = meterName;
        this.description = description;
    }

    public String getId() {
        return meterName;
    }

    public String getDescription() {
        return description;
    }
}
