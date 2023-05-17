/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */

package com.sportradar.unifiedodds.sdk.impl.entities;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableList;
import com.sportradar.uf.sportsapi.datamodel.SAPIProductInfo;
import com.sportradar.uf.sportsapi.datamodel.SAPIProductInfoLinks;
import com.sportradar.uf.sportsapi.datamodel.SAPIStreamingChannels;
import com.sportradar.unifiedodds.sdk.caching.exportable.ExportableProducerInfoCI;
import com.sportradar.unifiedodds.sdk.entities.ProducerInfo;
import com.sportradar.unifiedodds.sdk.entities.ProducerInfoLink;
import com.sportradar.unifiedodds.sdk.entities.StreamingChannel;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Contains information about a specific producer
 */
@SuppressWarnings({ "HiddenField", "UnnecessaryParentheses" })
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
     * A value indicating whether the sport event associated with the current
     * instance is available in the LiveMatchTracker solution
     */
    private final boolean isInLiveMatchTracker;

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
     * @param productInfo - a product info data
     */
    ProducerInfoImpl(SAPIProductInfo productInfo) {
        this.isAutoTraded = productInfo.getIsAutoTraded() != null;
        this.isInHostedStatistics = productInfo.getIsInHostedStatistics() != null;
        this.isInLiveCenterSoccer = productInfo.getIsInLiveCenterSoccer() != null;
        this.isInLiveMatchTracker = productInfo.getIsInLiveMatchTracker() != null;
        this.isInLiveScore = productInfo.getIsInLiveScore() != null;

        List<ProducerInfoLink> productInfoLinks = prepareProductLinks(productInfo.getLinks());
        List<StreamingChannel> streamingChannels = prepareProductStreams(productInfo.getStreaming());

        this.producerInfoLinks = productInfoLinks == null ? null : ImmutableList.copyOf(productInfoLinks);
        this.streamingChannels = streamingChannels == null ? null : ImmutableList.copyOf(streamingChannels);
    }

    ProducerInfoImpl(ExportableProducerInfoCI exportable) {
        Preconditions.checkNotNull(exportable);
        this.isAutoTraded = exportable.isAutoTraded();
        this.isInHostedStatistics = exportable.isInHostedStatistics();
        this.isInLiveCenterSoccer = exportable.isInLiveCenterSoccer();
        this.isInLiveScore = exportable.isInLiveScore();
        this.isInLiveMatchTracker = exportable.isInLiveMatchTracker();
        this.producerInfoLinks =
            exportable.getProducerInfoLinks() != null
                ? exportable
                    .getProducerInfoLinks()
                    .stream()
                    .map(ProducerInfoLinkImpl::new)
                    .collect(ImmutableList.toImmutableList())
                : null;
        this.streamingChannels =
            exportable.getStreamingChannels() != null
                ? exportable
                    .getStreamingChannels()
                    .stream()
                    .map(StreamingChannelImpl::new)
                    .collect(ImmutableList.toImmutableList())
                : null;
    }

    /**
     * Prepares the {@link SAPIProductInfoLinks} entities for further use in the {@link FixtureImpl}
     *
     * @param links - the {@link SAPIProductInfoLinks} instance that should be prepped for further use
     * @return - a {@link List} of processed {@link ProducerInfoLink} entities
     */
    private List<ProducerInfoLink> prepareProductLinks(SAPIProductInfoLinks links) {
        return links == null
            ? null
            : links
                .getLink()
                .stream()
                .map(link -> new ProducerInfoLinkImpl(link.getRef(), link.getName()))
                .collect(Collectors.toList());
    }

    /**
     * Prepares the {@link SAPIStreamingChannels} entities for further use in the {@link FixtureImpl}
     *
     * @param streamingChannels - the {@link SAPIStreamingChannels} instance that should be prepped for further use
     * @return - a {@link List} of processed {@link StreamingChannel} entities
     */
    private List<StreamingChannel> prepareProductStreams(SAPIStreamingChannels streamingChannels) {
        return streamingChannels == null
            ? null
            : streamingChannels
                .getChannel()
                .stream()
                .map(channel -> new StreamingChannelImpl(channel.getId(), channel.getName()))
                .collect(Collectors.toList());
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
     * Returns an indication if the sport event associated with the current
     * instance is available in the LiveMatchTracker solution
     *
     * @return - an indication if the sport event associated with the current
     * instance is available in the LiveMatchTracker solution
     */
    @Override
    public boolean isInLiveMatchTracker() {
        return isInLiveMatchTracker;
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
        return (
            "ProducerInfoImpl{" +
            "isAutoTraded=" +
            isAutoTraded +
            ", isInHostedStatistics=" +
            isInHostedStatistics +
            ", isInLiveCenterSoccer=" +
            isInLiveCenterSoccer +
            ", isInLiveScore=" +
            isInLiveScore +
            ", isInLiveMatchTracker=" +
            isInLiveMatchTracker +
            ", producerInfoLinks=" +
            producerInfoLinks +
            ", streamingChannels=" +
            streamingChannels +
            '}'
        );
    }

    public ExportableProducerInfoCI export() {
        return new ExportableProducerInfoCI(
            isAutoTraded,
            isInHostedStatistics,
            isInLiveCenterSoccer,
            isInLiveScore,
            isInLiveMatchTracker,
            producerInfoLinks != null
                ? producerInfoLinks
                    .stream()
                    .map(p -> ((ProducerInfoLinkImpl) p).export())
                    .collect(Collectors.toList())
                : null,
            streamingChannels != null
                ? streamingChannels
                    .stream()
                    .map(s -> ((StreamingChannelImpl) s).export())
                    .collect(Collectors.toList())
                : null
        );
    }
}
