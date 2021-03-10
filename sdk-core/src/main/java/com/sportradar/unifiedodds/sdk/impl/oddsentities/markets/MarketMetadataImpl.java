/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */

package com.sportradar.unifiedodds.sdk.impl.oddsentities.markets;

import com.google.common.base.Preconditions;
import com.sportradar.uf.datamodel.UFMarketMetadata;
import com.sportradar.unifiedodds.sdk.oddsentities.MarketMetadata;
import com.sportradar.utils.SdkHelper;

import java.time.Instant;
import java.util.Date;

/**
 * An implementation of the {@link MarketMetadata} instance which uses data provided by the feed to define metadata information
 */
public class MarketMetadataImpl implements MarketMetadata {
    private final Long nextBetStop;
    private final Long startTime;
    private final Long endTime;
    private final Long aamsId;

    MarketMetadataImpl(UFMarketMetadata marketMetadata) {
        Preconditions.checkNotNull(marketMetadata);

        nextBetStop = marketMetadata.getNextBetstop();
        startTime = marketMetadata.getStartTime();
        endTime = marketMetadata.getEndTime();
        aamsId = marketMetadata.getAamsId();
    }

    /**
     * Returns a timestamp in UTC when to betstop the associated market. Typically used for outrights and typically is
     * the start-time of the event the market refers to.
     *
     * @return a timestamp in UTC in which to bestop the associated market.
     */
    @Override
    public Long getNextBetstop() {
        return nextBetStop;
    }

    @Override
    public Long getStartTime() { return startTime; }

    @Override
    public Long getEndTime() { return endTime; }

    @Override
    public Long getAamsId() { return aamsId; }

    @Override
    public String toString() {
        return "MarketMetadataImpl{" +
                "nextBetStop=" + nextBetStop +
                ", startTime=" + startTime +
                ", endTime=" + endTime +
                ", aamsId=" + aamsId +
                '}';
    }
}
