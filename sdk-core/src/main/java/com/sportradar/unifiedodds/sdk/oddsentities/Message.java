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
     * @deprecated check getTimestamps for all available message timestamps
     */
    @Deprecated
    long getTimestamp();

    /**
     * Gets the timestamps when the message was generated, sent, received and dispatched by the sdk
     * @return gets the timestamps when the message was generated, sent, received and dispatched by the sdk
     */
    default MessageTimestamp getTimestamps() {
        throw new UnsupportedOperationException("Method not implemented. Use derived type.");
    }
}
