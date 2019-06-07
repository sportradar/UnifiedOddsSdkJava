/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */

package com.sportradar.unifiedodds.sdk.entities;

import java.util.Date;

/**
 * An interface providing methods to access {@link TvChannel} implementations properties
 */
public interface TvChannel {
    /**
     * Returns the name of the channel represented by the current instance
     *
     * @return - the name of the channel represented by the current instance
     */
    String getName();

    /**
     * Returns a {@link Date} specifying when the coverage on the channel starts
     *
     * @return - a {@link Date} specifying when the coverage on the channel represented by the
     * current {@link TvChannel} starts, or a null reference if the time is not known
     */
    Date getTime();

    /**
     * Returns the stream url
     *
     * @return the stream url; otherwise null
     */
    default String getStreamUrl(){
        throw new UnsupportedOperationException("Method not implemented. Use derived type.");
    }
}
