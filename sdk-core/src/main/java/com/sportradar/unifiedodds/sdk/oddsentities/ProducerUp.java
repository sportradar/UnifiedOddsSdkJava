/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */

package com.sportradar.unifiedodds.sdk.oddsentities;

/**
 * The event that gets released when the producer comes up after a specific time or for the 1st time at all
 */
public interface ProducerUp extends ProducerStatusChange {
    /**
     * Returns the {@link ProducerUpReason} indicating the reason of the event dispatch
     *
     * @return a {@link ProducerUpReason} indicating the reason of the event dispatch
     */
    ProducerUpReason getReason();
}
