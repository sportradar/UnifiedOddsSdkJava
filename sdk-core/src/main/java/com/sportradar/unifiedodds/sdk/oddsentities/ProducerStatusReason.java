/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */

package com.sportradar.unifiedodds.sdk.oddsentities;

/**
 * Describes reasons why the {@link ProducerStatusReason} message was dispatched
 */
@SuppressWarnings({ "LineLength", "NoEnumTrailingComma" })
public enum ProducerStatusReason {
    /**
     * Indicates that the {@link ProducerStatusReason} messages was dispatched because the first recovery request completed successfully
     */
    FirstRecoveryCompleted,

    /**
     * Indicates that the {@link ProducerStatusReason}  message was dispatched because the time frame between the message generation
     * and the message processing completion was again within the limit
     */
    ProcessingQueDelayStabilized,

    /**
     * Indicates that the {@link ProducerStatusReason} message was dispatched because a producer came back online
     */
    ReturnedFromInactivity,

    /**
     * Indicates that the {@link ProducerStatusReason} messages was dispatched because the interval
     * between alive messages was greater than allowed
     */
    AliveIntervalViolation,

    /**
     * Indicates that the {@link ProducerStatusReason} message was dispatched because the time frame between the message generation
     * and the message processing completion was exceeded
     */
    ProcessingQueueDelayViolation,

    /**
     * Indicates that the {@link ProducerStatusReason} message was dispatched due to information received
     * from the feed
     */
    Other,

    /**
     * Indicates that the {@link ProducerStatusReason} message was dispatched due to the loss of connection
     * to the server.
     */
    ConnectionDown,
}
