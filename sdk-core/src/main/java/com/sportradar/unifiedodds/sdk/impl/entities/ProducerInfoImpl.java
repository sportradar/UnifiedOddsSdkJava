/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */

package com.sportradar.unifiedodds.sdk.impl.entities;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableList;
import com.sportradar.unifiedodds.sdk.caching.exportable.ExportableProducerInfoCI;
import com.sportradar.unifiedodds.sdk.entities.ProducerInfo;
import com.sportradar.unifiedodds.sdk.entities.ProducerInfoLink;
import com.sportradar.unifiedodds.sdk.entities.StreamingChannel;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Contains information about a specific producer
 */
public class ProducerInfoImpl implements ProducerInfo {
    /**
     * A value indicating whether the instance is being auto traded
     */
    private final boolean isAutoTraded;

    /**
     * A value indicating whether the sport event associated with the current
     * instance is available in hosted statistic solutions
     */
    private final boolean isInHostedStatistics;

    /**
     * A value indicating whether the sport event associated with the current
     * instance is available in the LiveCenterSoccer solution
     */
    private final boolean isInLiveCenterSoccer;

    /**
     * A value indicating whether the sport event associated with the current
     * instance is available in the LiveScore solution
     */
    private final boolean isInLiveScore;

    /**
     * An unmodifiable {@link List} representing links to the producer represented by current instance
     * @see com.google.common.collect.ImmutableList
     */
    private final List<ProducerInfoLink> producerInfoLinks;

    /**
     * An unmodifiable {@link List} representing streaming channels associated with producer
     * @see com.google.common.collect.ImmutableList
     */
    private final List<StreamingChannel> streamingChannels;


    /**
     * Initializes a new instance of the {@link ProducerInfoImpl} class
     *
     * @param isAutoTraded - a value indicating whether the instance is being auto traded
     * @param isInHostedStatistics - a value indicating whether the sport event associated with the current
     *                               instance is available in hosted statistic solutions
     * @param isInLiveCenterSoccer - a value indicating whether the sport event associated with the current
     *                               instance is available in the LiveCenterSoccer solution
     * @param isInLiveScore - a value indicating whether the sport event associated with the current
     *                        instance is available in the LiveScore solution
     * @param producerInfoLinks - a {@link List} representing links to the producer represented by current instance
     * @param streamingChannels - a {@link List} representing streaming channels associated with producer
     */
    ProducerInfoImpl(boolean isAutoTraded, boolean isInHostedStatistics, boolean isInLiveCenterSoccer,
                            boolean isInLiveScore, List<ProducerInfoLink> producerInfoLinks, List<StreamingChannel> streamingChannels) {
        this.isAutoTraded = isAutoTraded;
        this.isInHostedStatistics = isInHostedStatistics;
        this.isInLiveCenterSoccer = isInLiveCenterSoccer;
        this.isInLiveScore = isInLiveScore;

        this.producerInfoLinks = producerInfoLinks == null ? null : ImmutableList.copyOf(producerInfoLinks);
        this.streamingChannels = streamingChannels == null ? null : ImmutableList.copyOf(streamingChannels);
    }

    ProducerInfoImpl(ExportableProducerInfoCI exportable) {
        Preconditions.checkNotNull(exportable);
        this.isAutoTraded = exportable.isAutoTraded();
        this.isInHostedStatistics = exportable.isInHostedStatistics();
        this.isInLiveCenterSoccer = exportable.isInLiveCenterSoccer();
        this.isInLiveScore = exportable.isInLiveScore();
        this.producerInfoLinks = exportable.getProducerInfoLinks() != null ? exportable.getProducerInfoLinks().stream().map(ProducerInfoLinkImpl::new).collect(ImmutableList.toImmutableList()) : null;
        this.streamingChannels = exportable.getStreamingChannels() != null ? exportable.getStreamingChannels().stream().map(StreamingChannelImpl::new).collect(ImmutableList.toImmutableList()) : null;
    }

    /**
     * Returns an indication if the current instance is being auto traded
     *
     * @return - an indication if the current instance is being auto traded
     */
    @Override
    public boolean isAutoTraded() {
        return isAutoTraded;
    }

    /**
     * Returns an indication if the sport event associated with the current
     * instance is available in the LiveCenterSoccer solution
     *
     * @return - an indication if the sport event associated with the current
     * instance is available in the LiveCenterSoccer solution
     */
    @Override
    public boolean isInHostedStatistics() {
        return isInHostedStatistics;
    }

    /**
     * Returns an indication if the sport event associated with the current
     * instance is available in the LiveCenterSoccer solution
     *
     * @return - an indication if the sport event associated with the current
     * instance is available in the LiveCenterSoccer solution
     */
    @Override
    public boolean isInLiveCenterSoccer() {
        return isInLiveCenterSoccer;
    }

    /**
     * Returns an indication if the sport event associated with the current
     * instance is available in the LiveScore solution
     *
     * @return - an indication if the sport event associated with the current
     * instance is available in the LiveScore solution
     */
    @Override
    public boolean isInLiveScore() {
        return isInLiveScore;
    }

    /**
     * Returns an unmodifiable {@link List} representing links to the producer represented by current instance
     * @see com.google.common.collect.ImmutableList
     *
     * @return - an unmodifiable {@link List} representing links to the producer represented by current instance
     */
    @Override
    public List<ProducerInfoLink> getProducerInfoLinks() {
        return producerInfoLinks;
    }

    /**
     * Returns an unmodifiable {@link List} representing streaming channels associated with current producer instance
     *
     * @return - an unmodifiable {@link List} representing streaming channels associated with current producer instance
     */
    @Override
    public List<StreamingChannel> getStreamingChannels() {
        return streamingChannels;
    }

    /**
     * Returns a {@link String} describing the current {@link ProducerInfo} instance
     *
     * @return - a {@link String} describing the current {@link ProducerInfo} instance
     */
    @Override
    public String toString() {
        return "ProducerInfoImpl{" +
                "isAutoTraded=" + isAutoTraded +
                ", isInHostedStatistics=" + isInHostedStatistics +
                ", isInLiveCenterSoccer=" + isInLiveCenterSoccer +
                ", isInLiveScore=" + isInLiveScore +
                ", producerInfoLinks=" + producerInfoLinks +
                ", streamingChannels=" + streamingChannels +
                '}';
    }

    public ExportableProducerInfoCI export() {
        return new ExportableProducerInfoCI(
                isAutoTraded,
                isInHostedStatistics,
                isInLiveCenterSoccer,
                isInLiveScore,
                producerInfoLinks != null ? producerInfoLinks.stream().map(p -> ((ProducerInfoLinkImpl)p).export()).collect(Collectors.toList()) : null,
                streamingChannels != null ? streamingChannels.stream().map(s -> ((StreamingChannelImpl)s).export()).collect(Collectors.toList()) : null
        );
    }
}
