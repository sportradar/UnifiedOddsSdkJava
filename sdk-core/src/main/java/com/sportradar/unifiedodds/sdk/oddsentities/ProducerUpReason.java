/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */

package com.sportradar.unifiedodds.sdk.oddsentities;

/**
 * Represents reasons why the Producer was marked up
 */
@SuppressWarnings({ "LineLength", "MissingSwitchDefault" })
public enum ProducerUpReason {
    /**
     * Indicates that the Producer was marked up because the first recovery request completed successfully
     */
    FirstRecoveryCompleted,

    /**
     * Indicates that the Producer was marked up because the time frame between the message generation
     * and the message processing completion was again within the limit
     */
    ProcessingQueDelayStabilized,

    /**
     * Indicates that the Producer was marked up because a producer came back online
     */
    ReturnedFromInactivity;

    public ProducerStatusReason asProducerStatusReason() {
        switch (this) {
            case FirstRecoveryCompleted:
                return ProducerStatusReason.FirstRecoveryCompleted;
            case ProcessingQueDelayStabilized:
                return ProducerStatusReason.ProcessingQueDelayStabilized;
            case ReturnedFromInactivity:
                return ProducerStatusReason.ReturnedFromInactivity;
        }

        throw new IllegalArgumentException(this + " can't be mapped to ProducerStatusReason");
    }
}
