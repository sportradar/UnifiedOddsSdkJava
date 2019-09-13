/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */

package com.sportradar.unifiedodds.sdk.impl.entities;

import com.google.common.base.Preconditions;
import com.sportradar.unifiedodds.sdk.caching.exportable.ExportableStreamingChannelCI;
import com.sportradar.unifiedodds.sdk.entities.StreamingChannel;

/**
 * Represents a streaming channel
 */
public class StreamingChannelImpl implements StreamingChannel {
    /**
     * The identifier of the chanel
     */
    private final int id;

    /**
     * The name of the channel
     */
    private final String name;


    /**
     * Initializes a new {@link StreamingChannelImpl} instance
     *
     * @param id - a unique identifier representing the current streaming channel
     * @param name - the name of the streaming channel represented by the current instance
     */
    StreamingChannelImpl(int id, String name) {
        this.id = id;
        this.name = name;
    }

    StreamingChannelImpl(ExportableStreamingChannelCI exportable) {
        Preconditions.checkNotNull(exportable);
        this.id = exportable.getId();
        this.name = exportable.getName();
    }

    /**
     * Returns a value uniquely identifying the current streaming channel
     *
     * @return - the value uniquely identifying the current streaming channel
     */
    @Override
    public int getId() {
        return id;
    }

    /**
     * Returns the name of the channel represented by the current instance
     *
     * @return - the name of the channel represented by the current instance
     */
    @Override
    public String getName() {
        return name;
    }

    /**
     * Returns a {@link String} describing the current {@link StreamingChannel} instance
     *
     * @return - a {@link String} describing the current {@link StreamingChannel} instance
     */
    @Override
    public String toString() {
        return "StreamingChannelImpl{" +
                "id=" + id +
                ", name='" + name + '\'' +
                '}';
    }

    public ExportableStreamingChannelCI export() {
        return new ExportableStreamingChannelCI(
                id,
                name
        );
    }
}
