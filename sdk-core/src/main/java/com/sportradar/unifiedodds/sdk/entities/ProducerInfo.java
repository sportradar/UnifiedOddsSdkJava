/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */

package com.sportradar.unifiedodds.sdk.entities;

import java.util.List;

/**
 * An interface providing methods for accessing producer information
 */
public interface ProducerInfo {
    /**
     * Returns an indication if the current instance is being auto traded
     *
     * @return - an indication if the current instance is being auto traded
     */
    boolean isAutoTraded();

    /**
     * Returns an indication if the sport event associated with the current
     * instance is available in the LiveCenterSoccer solution
     *
     * @return - an indication if the sport event associated with the current
     * instance is available in the LiveCenterSoccer solution
     */
    boolean isInHostedStatistics();

    /**
     * Returns an indication if the sport event associated with the current
     * instance is available in the LiveCenterSoccer solution
     *
     * @return - an indication if the sport event associated with the current
     * instance is available in the LiveCenterSoccer solution
     */
    boolean isInLiveCenterSoccer();

    /**
     * Returns an indication if the sport event associated with the current
     * instance is available in the LiveScore solution
     *
     * @return - an indication if the sport event associated with the current
     * instance is available in the LiveScore solution
     */
    boolean isInLiveScore();

    /**
     * Returns an unmodifiable {@link List} representing links to the producer represented by current instance
     *
     * @return - an unmodifiable {@link List} representing links to the producer represented by current instance
     */
    List<ProducerInfoLink> getProducerInfoLinks();

    /**
     * Returns an unmodifiable {@link List} representing streaming channels associated with current producer instance
     *
     * @return - an unmodifiable {@link List} representing streaming channels associated with current producer instance
     */
    List<StreamingChannel> getStreamingChannels();
}
