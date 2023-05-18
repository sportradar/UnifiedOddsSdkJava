/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */

package com.sportradar.unifiedodds.sdk.caching.ci;

import com.google.common.base.Preconditions;
import com.sportradar.uf.sportsapi.datamodel.SAPIStreamingChannel;

/**
 * A streaming channel representation used by caching components
 */
@SuppressWarnings({ "AbbreviationAsWordInName" })
public class StreamingChannelCI {

    /**
     * The id of the channel
     */
    private final int id;

    /**
     * The channel name
     */
    private final String name;

    /**
     * Initializes a new instance of the {@link StreamingChannelCI} class
     *
     * @param channel - {@link SAPIStreamingChannel} containing information about the channel
     */
    public StreamingChannelCI(SAPIStreamingChannel channel) {
        Preconditions.checkNotNull(channel);

        id = channel.getId();
        name = channel.getName();
    }

    /**
     * Returns the id of the channel
     *
     * @return - the id of the channel
     */
    public int getId() {
        return id;
    }

    /**
     * Returns the name of the channel
     *
     * @return - the name of the channel
     */
    public String getName() {
        return name;
    }
}
