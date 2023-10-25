/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */

package com.sportradar.unifiedodds.sdk.oddsentities;

/**
 * Describes reasons why the Producer was marked down
 */
@SuppressWarnings({ "LineLength", "MissingSwitchDefault" })
public enum ProducerDownReason {
    /**
     * Indicates that the Producer was marked down because the interval
     * between alive messages was greater than allowed.
     */
    AliveIntervalViolation,

    /**
     * Indicates that the Producer was marked down because the time frame between the message generation
     * and the message processing completion was exceeded.
     */
    ProcessingQueueDelayViolation,

    /**
     * Indicates that the Producer was marked down due to information received
     * from the feed.
     */
    Other,

    /**
     * Indicates that the Producer was marked down dispatched due to the loss of connection
     * to the server.
     */
    ConnectionDown;

    public ProducerStatusReason asProducerStatusReason() {
        switch (this) {
            case AliveIntervalViolation:
                return ProducerStatusReason.AliveIntervalViolation;
            case ProcessingQueueDelayViolation:
                return ProducerStatusReason.ProcessingQueueDelayViolation;
            case ConnectionDown:
                return ProducerStatusReason.ConnectionDown;
            case Other:
                return ProducerStatusReason.Other;
        }

        throw new IllegalArgumentException(this + " can't be mapped to ProducerStatusReason");
    }
}
