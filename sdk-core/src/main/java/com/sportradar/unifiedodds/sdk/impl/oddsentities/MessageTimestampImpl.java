/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */

package com.sportradar.unifiedodds.sdk.impl.oddsentities;


import com.google.common.base.Preconditions;
import com.sportradar.unifiedodds.sdk.oddsentities.MessageTimestamp;

public class MessageTimestampImpl implements MessageTimestamp {
    private final long created;
    private final long sent;
    private final long received;
    private final long dispatched;

    public MessageTimestampImpl(long created, long sent, long received, long dispatched)
    {
//        Preconditions.checkArgument(created > 0);
        Preconditions.checkArgument(sent > 0);
        Preconditions.checkArgument(received > 0);
//        Preconditions.checkArgument(dispatched > 0);

        this.created = created;
        this.sent = sent;
        this.received = received;
        this.dispatched = dispatched;
    }

    public MessageTimestampImpl(long timestamp)
    {
        Preconditions.checkArgument(timestamp > 0);

        this.created = timestamp;
        this.sent = timestamp;
        this.received = timestamp;
        this.dispatched = timestamp;
    }

    /**
     * Gets the value specifying when the message was generated and put in queue on rabbit server (milliseconds since EPOCH UTC)
     *
     * @return the value specifying when the message was generated
     */
    @Override
    public long getCreated() {
        return created;
    }

    /**
     * Gets the value specifying when the message was sent from the rabbit server (milliseconds since EPOCH UTC)
     *
     * @return the value specifying when the message was sent from the rabbit server
     */
    @Override
    public long getSent() {
        return sent;
    }

    /**
     * Gets the value specifying when the message was received for processing by the sdk (milliseconds since EPOCH UTC)
     *
     * @return the value specifying when the message was received for processing by the sdk
     */
    @Override
    public long getReceived() {
        return received;
    }

    /**
     * Gets the value specifying when the message was dispatched to the user from the sdk (milliseconds since EPOCH UTC)
     *
     * @return the value specifying when the message was dispatched to the user from the sdk
     */
    @Override
    public long getDispatched() {
        return dispatched;
    }
}
