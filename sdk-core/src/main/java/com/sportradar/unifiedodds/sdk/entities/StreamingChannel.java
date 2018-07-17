/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */

package com.sportradar.unifiedodds.sdk.entities;

/**
 * An interface providing methods to access streaming channel data
 */
public interface StreamingChannel {
    /**
     * Returns a value uniquely identifying the current streaming channel
     *
     * @return - the value uniquely identifying the current streaming channel
     */
    int getId();

    /**
     * Returns the name of the channel represented by the current instance
     *
     * @return - the name of the channel represented by the current instance
     */
    String getName();
}
