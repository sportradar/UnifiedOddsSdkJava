/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */

package com.sportradar.unifiedodds.sdk.impl.oddsentities.markets;

import com.google.common.base.Preconditions;
import com.sportradar.uf.datamodel.UFMarketMetadata;
import com.sportradar.unifiedodds.sdk.oddsentities.MarketMetadata;

/**
 * An implementation of the {@link MarketMetadata} instance which uses data provided by the feed to define metadata information
 */
public class MarketMetadataImpl implements MarketMetadata {
    private final Long nextBetStop;

    MarketMetadataImpl(UFMarketMetadata marketMetadata) {
        Preconditions.checkNotNull(marketMetadata);

        nextBetStop = marketMetadata.getNextBetstop();
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
    public String toString() {
        return "MarketMetadataImpl{" +
                "nextBetStop=" + nextBetStop +
                '}';
    }
}
