/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */

package com.sportradar.unifiedodds.sdk.impl.oddsentities;

import com.google.common.base.Preconditions;
import com.sportradar.unifiedodds.sdk.oddsentities.Message;
import com.sportradar.unifiedodds.sdk.oddsentities.Producer;

/**
 * Created on 22/06/2017.
 * // TODO @eti: Javadoc
 */
abstract class MessageImpl implements Message {
    private final Producer producer;
    private final long timestamp;

    MessageImpl(Producer producer, long timestamp) {
        Preconditions.checkArgument(timestamp > 0);

        this.producer = producer;
        this.timestamp = timestamp;
    }

    /**
     * Returns the {@link Producer} that generated this message
     *
     * @return the {@link Producer} that generated this message
     */
    @Override
    public Producer getProducer() {
        return producer;
    }

    /**
     * Returns a timestamp indicating when was this message created in milliseconds since EPOCH UTC
     *
     * @return a timestamp indicating when was this message created in milliseconds since EPOCH UTC
     */
    @Override
    public long getTimestamp() {
        return timestamp;
    }
}
