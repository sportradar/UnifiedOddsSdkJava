/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */

package com.sportradar.unifiedodds.sdk.impl.entities;

import com.sportradar.unifiedodds.sdk.entities.TvChannel;

import java.util.Date;

/**
 * Represents a TV channel
 */
public class TvChannelImpl implements TvChannel {
    /**
     * The name of the channel represented by the current instance
     */
    private final String name;

    /**
     * A {@link Date} specifying when the coverage on the channel represented by the
     * current {@link TvChannel} starts
     */
    private final Date time;

    /**
     * The stream url
     */
    private final String streamUrl;

    /**
     *Initializes a new instance of the {@link TvChannelImpl} class
     *
     * @param name - the name of the channel represented by the current instance
     * @param time - a {@link Date} specifying when the coverage on the channel
     *               represented by the current {@link TvChannel} starts
     */
    public TvChannelImpl(String name, Date time, String streamUrl) {
        this.name = name;
        this.time = time;
        this.streamUrl = streamUrl;
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
     * Returns a {@link Date} specifying when the coverage on the channel starts
     *
     * @return - a {@link Date} specifying when the coverage on the channel represented by the
     * current {@link TvChannel} starts, or a null reference if the time is not known
     */
    @Override
    public Date getTime() {
        return time;
    }

    /**
     * Returns the stream url of the channel represented by the current instance
     *
     * @return - the stream url of the channel represented by the current instance
     */
    @Override
    public String getStreamUrl() {
        return streamUrl;
    }

    /**
     * Returns a {@link String} describing the current {@link TvChannel} instance
     *
     * @return - a {@link String} describing the current {@link TvChannel} instance
     */
    @Override
    public String toString() {
        return "TvChannelImpl{" +
                "name='" + name + '\'' +
                ", time=" + time + '\'' +
                ", streamUrl=" + streamUrl +
                '}';
    }
}
