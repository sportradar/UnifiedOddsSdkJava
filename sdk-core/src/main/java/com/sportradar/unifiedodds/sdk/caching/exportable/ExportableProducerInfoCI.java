package com.sportradar.unifiedodds.sdk.caching.exportable;

import java.io.Serializable;
import java.util.List;

@SuppressWarnings({ "HiddenField", "ParameterNumber" })
public class ExportableProducerInfoCi implements Serializable {

    private boolean isAutoTraded;
    private boolean isInHostedStatistics;
    private boolean isInLiveCenterSoccer;
    private boolean isInLiveScore;
    private boolean isInLiveMatchTracker;
    private List<ExportableProducerInfoLinkCi> producerInfoLinks;
    private List<ExportableStreamingChannelCi> streamingChannels;

    public ExportableProducerInfoCi(
        boolean isAutoTraded,
        boolean isInHostedStatistics,
        boolean isInLiveCenterSoccer,
        boolean isInLiveScore,
        boolean isInLiveMatchTracker,
        List<ExportableProducerInfoLinkCi> producerInfoLinks,
        List<ExportableStreamingChannelCi> streamingChannels
    ) {
        this.isAutoTraded = isAutoTraded;
        this.isInHostedStatistics = isInHostedStatistics;
        this.isInLiveCenterSoccer = isInLiveCenterSoccer;
        this.isInLiveScore = isInLiveScore;
        this.isInLiveMatchTracker = isInLiveMatchTracker;
        this.producerInfoLinks = producerInfoLinks;
        this.streamingChannels = streamingChannels;
    }

    public boolean isAutoTraded() {
        return isAutoTraded;
    }

    public void setAutoTraded(boolean autoTraded) {
        isAutoTraded = autoTraded;
    }

    public boolean isInHostedStatistics() {
        return isInHostedStatistics;
    }

    public void setInHostedStatistics(boolean inHostedStatistics) {
        isInHostedStatistics = inHostedStatistics;
    }

    public boolean isInLiveCenterSoccer() {
        return isInLiveCenterSoccer;
    }

    public void setInLiveCenterSoccer(boolean inLiveCenterSoccer) {
        isInLiveCenterSoccer = inLiveCenterSoccer;
    }

    public boolean isInLiveScore() {
        return isInLiveScore;
    }

    public void setInLiveScore(boolean inLiveScore) {
        isInLiveScore = inLiveScore;
    }

    public boolean isInLiveMatchTracker() {
        return isInLiveMatchTracker;
    }

    public void setInLiveMatchTracker(boolean inLiveMatchTracker) {
        isInLiveMatchTracker = inLiveMatchTracker;
    }

    public List<ExportableProducerInfoLinkCi> getProducerInfoLinks() {
        return producerInfoLinks;
    }

    public void setProducerInfoLinks(List<ExportableProducerInfoLinkCi> producerInfoLinks) {
        this.producerInfoLinks = producerInfoLinks;
    }

    public List<ExportableStreamingChannelCi> getStreamingChannels() {
        return streamingChannels;
    }

    public void setStreamingChannels(List<ExportableStreamingChannelCi> streamingChannels) {
        this.streamingChannels = streamingChannels;
    }
}
