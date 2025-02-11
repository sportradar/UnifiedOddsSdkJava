/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */
package com.sportradar.unifiedodds.sdk.internal.common.telemetry;

public enum LongSdkHistogram implements MeterDescriptor {
    DATA_ROUTER_MANAGER("uofsdk-dataroutermanager", "The time it takes to execute Api call");

    private final String id;
    private final String description;

    LongSdkHistogram(String id, String description) {
        this.id = id;
        this.description = description;
    }

    public String getId() {
        return id;
    }

    public String getDescription() {
        return description;
    }
}
