/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */

package com.sportradar.unifiedodds.example.player;

import com.google.common.base.Preconditions;

import java.util.Date;

/**
 * Created on 05/01/2018.
 * // TODO @eti: Javadoc
 */
class ParsedLine {
    private final Date timestamp;
    private final String routingKey;
    private final String messagePayload;

    ParsedLine(Date timestamp, String routingKey, String messagePayload) {
        Preconditions.checkNotNull(timestamp);
        Preconditions.checkNotNull(routingKey);
        Preconditions.checkNotNull(messagePayload);

        this.timestamp = timestamp;
        this.routingKey = routingKey;
        this.messagePayload = messagePayload;
    }

    public Date getTimestamp() {
        return timestamp;
    }

    public String getRoutingKey() {
        return routingKey;
    }

    public String getMessagePayload() {
        return messagePayload;
    }

    @Override
    public String toString() {
        return "ParsedLine{" +
                "timestamp=" + timestamp +
                ", routingKey='" + routingKey + '\'' +
                '}';
    }
}
