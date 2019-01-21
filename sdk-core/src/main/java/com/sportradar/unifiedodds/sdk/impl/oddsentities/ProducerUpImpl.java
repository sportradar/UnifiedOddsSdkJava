/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */

package com.sportradar.unifiedodds.sdk.impl.oddsentities;

import com.sportradar.unifiedodds.sdk.oddsentities.Producer;
import com.sportradar.unifiedodds.sdk.oddsentities.ProducerStatusChange;
import com.sportradar.unifiedodds.sdk.oddsentities.ProducerUp;
import com.sportradar.unifiedodds.sdk.oddsentities.ProducerUpReason;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * A {@link ProducerStatusChange} implementation used to inform that a feed-based producer came online
 */
class ProducerUpImpl extends MessageImpl implements ProducerUp {
    /**
     * A {@link ProducerUpReason} instance indicating why the {@link ProducerUp} message was
     * dispatched
     */
    private final ProducerUpReason reason;

    /**
     * Initializes a new instance of the
     * {@link ProducerUpImpl} class
     *
     * @param producer The producer of the message
     * @param reason    A {@link ProducerUpReason} instance indicating why the {@link ProducerUp}
     *                  message was dispatched
     * @param timestamp A UTC based timestamp specifying when the message was generated
     */
    ProducerUpImpl(Producer producer, ProducerUpReason reason, long timestamp) {
        super(producer, new MessageTimestampImpl(timestamp));
        checkNotNull(reason, "reason cannot be a null reference");

        this.reason = reason;
    }

    /**
     * Gets a {@link ProducerUpReason} instance indicating why the {@link ProducerUp} message
     * was dispatched
     *
     * @return a {@link ProducerUpReason} instance indicating why the {@link ProducerUp}
     * message was dispatched
     */
    @Override
    public ProducerUpReason getReason() {
        return reason;
    }
}
