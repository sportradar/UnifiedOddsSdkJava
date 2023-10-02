/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */

package com.sportradar.unifiedodds.sdk.caching.ci;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableList;
import com.sportradar.uf.sportsapi.datamodel.SapiProductInfo;
import java.util.List;
import java.util.stream.Collectors;

/**
 * A producer info representation used by caching components
 */
public class ProducerInfoCi {

    /**
     * The isInLiveScore property backing field
     */
    private final boolean isInLiveScore;

    /**
     * The isInHostedStatistics property backing field
     */
    private final boolean isInHostedStatistics;

    /**
     * The isInLiveCenterSoccer property backing field
     */
    private final boolean isInLiveCenterSoccer;

    /**
     * The isAutoTraded property backing field
     */
    private final boolean isAutoTraded;

    /**
     * The isInLiveMatchTracker property backing field
     */
    private final boolean isInLiveMatchTracker;

    /**
     * A {@link List< ProducerInfoLinkCi >} containing the associated {@link ProducerInfoLinkCi}
     */
    private final List<ProducerInfoLinkCi> producerInfoLinks;

    /**
     * A {@link List< StreamingChannelCi >} containing the associated {@link StreamingChannelCi}
     */
    private final List<StreamingChannelCi> streamingChannels;

    /**
     * Initializes a new instance of the {@link ProducerInfoCi} class
     *
     * @param pInfo - {@link SapiProductInfo} containing information about the producer
     */
    public ProducerInfoCi(SapiProductInfo pInfo) {
        Preconditions.checkNotNull(pInfo);

        isInLiveScore = pInfo.getIsInLiveScore() != null;
        isInHostedStatistics = pInfo.getIsInHostedStatistics() != null;
        isInLiveCenterSoccer = pInfo.getIsInLiveCenterSoccer() != null;
        isAutoTraded = pInfo.getIsAutoTraded() != null;
        isInLiveMatchTracker = pInfo.getIsInLiveMatchTracker() != null;

        if (
            pInfo.getLinks() != null &&
            pInfo.getLinks().getLink() != null &&
            !pInfo.getLinks().getLink().isEmpty()
        ) {
            producerInfoLinks =
                pInfo.getLinks().getLink().stream().map(ProducerInfoLinkCi::new).collect(Collectors.toList());
        } else {
            producerInfoLinks = null;
        }

        if (
            pInfo.getStreaming() != null &&
            pInfo.getStreaming().getChannel() != null &&
            !pInfo.getStreaming().getChannel().isEmpty()
        ) {
            streamingChannels =
                pInfo
                    .getStreaming()
                    .getChannel()
                    .stream()
                    .map(StreamingChannelCi::new)
                    .collect(Collectors.toList());
        } else {
            streamingChannels = null;
        }
    }

    /**
     * Returns <code>true</code> if the producer is in the live score
     *
     * @return - <code>true</code> if the producer is in the live score
     */
    public boolean isInLiveScore() {
        return isInLiveScore;
    }

    /**
     * Returns <code>true</code> if the producer is in the hosted statistics
     *
     * @return - <code>true</code> if the producer is in the hosted statistics
     */
    public boolean isInHostedStatistics() {
        return isInHostedStatistics;
    }

    /**
     * Returns <code>true</code> if the producer is in the live center soccer
     *
     * @return - <code>true</code> if the producer is in the live center soccer
     */
    public boolean isInLiveCenterSoccer() {
        return isInLiveCenterSoccer;
    }

    /**
     * Returns <code>true</code> if the producer is auto traded
     *
     * @return - <code>true</code> if the producer is auto traded
     */
    public boolean isAutoTraded() {
        return isAutoTraded;
    }

    /**
     * Returns <code>true</code> if the producer is in the live match tracker
     *
     * @return - <code>true</code> if the producer is in the live match tracker
     */
    public boolean isInLiveMatchTracker() {
        return isInLiveMatchTracker;
    }

    /**
     * Returns a unmodifiable {@link List} containing the associated {@link StreamingChannelCi}
     *
     * @return - a unmodifiable {@link List} containing the associated {@link StreamingChannelCi}
     */
    public List<ProducerInfoLinkCi> getProducerInfoLinks() {
        return producerInfoLinks == null ? null : ImmutableList.copyOf(producerInfoLinks);
    }

    /**
     * Returns a unmodifiable {@link List} containing the associated {@link StreamingChannelCi}
     *
     * @return - a unmodifiable {@link List} containing the associated {@link StreamingChannelCi}
     */
    public List<StreamingChannelCi> getStreamingChannels() {
        return streamingChannels == null ? null : ImmutableList.copyOf(streamingChannels);
    }
}
