/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */
package com.sportradar.unifiedodds.sdk.impl;

import com.sportradar.unifiedodds.sdk.LoggerDefinitions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MessageTrafficLogger {

    private static final String TRAFFIC_LOG_DELIMITER = "<~>";

    private static final Logger TRAFFIC_LOGGER = LoggerFactory.getLogger(
        LoggerDefinitions.UfSdkTrafficLog.class
    );

    private MessageTrafficLogger() {}

    public static void logReceivedOnClosedChannel(
        String consumerDescription,
        String routingKey,
        byte[] body
    ) {
        TRAFFIC_LOGGER.debug(
            "{} {} {} {} {}",
            consumerDescription,
            TRAFFIC_LOG_DELIMITER,
            routingKey,
            TRAFFIC_LOG_DELIMITER,
            "received on closed channel: " + new String(body)
        );
    }
}
