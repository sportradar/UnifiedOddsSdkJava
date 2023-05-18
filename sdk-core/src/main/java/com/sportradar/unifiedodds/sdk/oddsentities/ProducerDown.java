/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */

package com.sportradar.unifiedodds.sdk.oddsentities;

/**
 * This message is received when a producer goes down. All the odds for all the sport events handled
 * by this producer need to be analyzed and potentially stopped.
 *
 */
public interface ProducerDown extends ProducerStatusChange {
    /**
     * Returns a {@link ProducerDownReason} indicating the reason of the event dispatch
     *
     * @return a {@link ProducerDownReason} indicating the reason of the event dispatch
     */
    ProducerDownReason getReason();
}
