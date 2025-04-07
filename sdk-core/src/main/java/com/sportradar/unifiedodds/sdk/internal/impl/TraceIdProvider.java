/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */

package com.sportradar.unifiedodds.sdk.internal.impl;

import java.util.UUID;

public class TraceIdProvider {

    public String generateTraceId() {
        return UUID.randomUUID().toString();
    }
}
