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
     * Gets the timestamps when the message was generated, sent, received and dispatched by the sdk
     * @return gets the timestamps when the message was generated, sent, received and dispatched by the sdk
     */
    MessageTimestamp getTimestamps();
}
