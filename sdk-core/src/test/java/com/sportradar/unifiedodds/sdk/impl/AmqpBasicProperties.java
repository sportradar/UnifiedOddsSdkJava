/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */
package com.sportradar.unifiedodds.sdk.impl;

import com.rabbitmq.client.AMQP;
import java.util.HashMap;
import java.util.Map;

public class AmqpBasicProperties {

    public static final long ANY_TIMESTAMP = 1487254396715L;

    public static AMQP.BasicProperties withAnyTimestamp() {
        return withTimestamp(ANY_TIMESTAMP);
    }

    public static AMQP.BasicProperties withTimestamp(long timestamp) {
        Map<String, Object> rawHeaders = new HashMap<>();
        rawHeaders.put("timestamp_in_ms", timestamp);
        return new AMQP.BasicProperties.Builder().headers(rawHeaders).build();
    }
}
