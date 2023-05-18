/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */

package com.sportradar.unifiedodds.sdk.impl.oddsentities;

import static com.google.common.base.Preconditions.checkNotNull;

import com.sportradar.unifiedodds.sdk.oddsentities.Producer;
import com.sportradar.unifiedodds.sdk.oddsentities.ProducerDown;
import com.sportradar.unifiedodds.sdk.oddsentities.ProducerDownReason;
import com.sportradar.unifiedodds.sdk.oddsentities.ProducerStatusChange;

/**
 * A {@link ProducerStatusChange} implementation used to inform that a feed-based producer is down
 */
class ProducerDownImpl extends MessageImpl implements ProducerDown {

    /**
     * A {@link ProducerDownReason} instance indicating why the {@link ProducerDown} message was
     * dispatched
     */
    private final ProducerDownReason reason;

    /**
     * Initializes a new instance of the
     * {@link ProducerDownImpl} class
     *
     * @param producer The producer of the message
     * @param reason A {@link ProducerDownReason} instance indicating why the {@link ProducerDown}
     *        message was dispatched
     * @param timestamp A UTC based timestamp specifying when the message was generated
     */
    ProducerDownImpl(Producer producer, ProducerDownReason reason, long timestamp) {
        super(producer, new MessageTimestampImpl(timestamp));
        checkNotNull(reason, "reason cannot be a null reference");

        this.reason = reason;
    }

    /**
     * Gets a {@link ProducerDownReason} instance indicating why the {@link ProducerDown} message
     * was dispatched
     *
     * @return a {@link ProducerDownReason} instance indicating why the {@link ProducerDown}
     *         message was dispatched
     */
    @Override
    public ProducerDownReason getReason() {
        return reason;
    }
}
