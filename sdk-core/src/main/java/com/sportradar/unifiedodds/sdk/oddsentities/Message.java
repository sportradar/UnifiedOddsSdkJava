/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */

package com.sportradar.unifiedodds.sdk.oddsentities;

public interface Message {
    /**
     * Returns the {@link Producer} that generated this message
     *
     * @return the {@link Producer} that generated this message
     */
    Producer getProducer();

    /**
     * @return when was this message created in milliseconds since EPOCH UTC
     */
    long getTimestamp();
}
