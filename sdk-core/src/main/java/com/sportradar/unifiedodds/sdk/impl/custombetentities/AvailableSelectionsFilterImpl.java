/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */

package com.sportradar.unifiedodds.sdk.impl.custombetentities;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableList;
import com.sportradar.uf.custombet.datamodel.CAPIAvailableSelections;
import com.sportradar.uf.custombet.datamodel.CAPIEventType;
import com.sportradar.uf.custombet.datamodel.CAPIFilteredEventType;
import com.sportradar.uf.custombet.datamodel.CAPIMarketsType;
import com.sportradar.unifiedodds.sdk.custombetentities.AvailableSelections;
import com.sportradar.unifiedodds.sdk.custombetentities.AvailableSelectionsFilter;
import com.sportradar.unifiedodds.sdk.custombetentities.Market;
import com.sportradar.unifiedodds.sdk.custombetentities.MarketFilter;
import com.sportradar.utils.URN;
import java.util.List;

/**
 * Implements methods used to access available selections for the event
 */
public class AvailableSelectionsFilterImpl implements AvailableSelectionsFilter {

    /**
     * An {@link URN} specifying the id of the event
     */
    private final URN event;

    /**
     * An {@link List} specifying available markets for the event
     */
    private final List<MarketFilter> markets;

    private final String generatedAt;

    public AvailableSelectionsFilterImpl(CAPIFilteredEventType eventType, String generatedAt) {
        Preconditions.checkNotNull(eventType);

        this.event = URN.parse(eventType.getId());

        this.markets =
            (eventType.getMarkets() != null)
                ? eventType
                    .getMarkets()
                    .getMarkets()
                    .stream()
                    .map(MarketFilterImpl::new)
                    .collect(ImmutableList.toImmutableList())
                : ImmutableList.of();

        this.generatedAt = generatedAt;
    }

    @Override
    public URN getEvent() {
        return event;
    }

    @Override
    public List<MarketFilter> getMarkets() {
        return markets;
    }
}
