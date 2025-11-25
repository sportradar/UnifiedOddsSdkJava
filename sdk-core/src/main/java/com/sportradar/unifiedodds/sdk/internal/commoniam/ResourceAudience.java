/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */
package com.sportradar.unifiedodds.sdk.internal.commoniam;

public enum ResourceAudience {
    UF_REST_API("UF-RestAPI"),
    UF_RABBIT_MQ("UF-RabbitMQ");

    private final String value;

    ResourceAudience(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
