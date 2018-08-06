/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */

package com.sportradar.unifiedodds.sdk.oddsentities;

/**
 * The event that gets released when the status of the producer gets updated
 */
public interface ProducerStatus extends Message {
    /**
     * An indication if the associated {@link Producer} is down
     *
     * @return <code>true</code> if the {@link Producer} is down, otherwise <code>false</code>
     */
    boolean isDown();

    /**
     * An indication if the associated {@link Producer} is delayed(processing queue is building up)
     *
     * @return <code>true</code> if the {@link Producer} is delayed, otherwise <code>false</code>
     */
    boolean isDelayed();

    /**
     * Returns the reason of the {@link Producer} status change
     *
     * @return the reason of the status change
     */
    ProducerStatusReason getProducerStatusReason();
}
