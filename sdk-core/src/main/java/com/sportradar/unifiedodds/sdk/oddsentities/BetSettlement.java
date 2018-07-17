/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */

package com.sportradar.unifiedodds.sdk.oddsentities;

import com.sportradar.unifiedodds.sdk.entities.SportEvent;

import java.util.List;

/**
 * Received when one or more markets for a particular competition have an outcome and results should
 * be cleared
 */
public interface BetSettlement<T extends SportEvent> extends MarketMessage<T> {

    /**
     *
     * @return the certainty of the settlement
     */
    BetSettlementCertainty getCertainty();

    List<MarketWithSettlement> getMarkets();
}
