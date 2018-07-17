/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */

package com.sportradar.unifiedodds.sdk.oddsentities;

/**
 * Describes reasons why the {@link ProducerDown} message was dispatched
 */
public enum ProducerDownReason {

    /**
     * Indicates that the {@link ProducerDown} messages was dispatched because the interval
     * between alive messages was greater than allowed.
     */
    AliveIntervalViolation,

    /**
     * Indicates that the {@link ProducerDown} message was dispatched because the time frame between the message generation
     * and the message processing completion was exceeded.
     */
    ProcessingQueueDelayViolation,

    /**
     * Indicates that the {@link ProducerDown} message was dispatched due to information received
     * from the feed.
     */
    Other;

    public ProducerStatusReason asProducerStatusReason() {
        switch (this) {
            case AliveIntervalViolation:
                return ProducerStatusReason.AliveIntervalViolation;
            case ProcessingQueueDelayViolation:
                return ProducerStatusReason.ProcessingQueueDelayViolation;
            case Other:
                return ProducerStatusReason.Other;
        }

        throw new IllegalArgumentException(this + " can't be mapped to ProducerStatusReason");
    }
}
