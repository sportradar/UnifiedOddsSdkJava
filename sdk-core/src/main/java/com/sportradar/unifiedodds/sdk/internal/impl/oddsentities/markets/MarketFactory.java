/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */

package com.sportradar.unifiedodds.sdk.internal.impl.oddsentities.markets;

import com.sportradar.uf.datamodel.UfBetSettlementMarket;
import com.sportradar.uf.datamodel.UfMarket;
import com.sportradar.uf.datamodel.UfOddsChangeMarket;
import com.sportradar.unifiedodds.sdk.entities.SportEvent;
import com.sportradar.unifiedodds.sdk.oddsentities.*;
import java.util.Optional;

/**
 * Created on 23/06/2017.
 * // TODO @eti: Javadoc
 */
public interface MarketFactory {
    Optional<Market> buildMarket(SportEvent sportEvent, UfMarket m, int producerId);

    Optional<MarketWithOdds> buildMarketWithOdds(
        SportEvent sportEvent,
        UfOddsChangeMarket market,
        int producerId
    );

    Optional<MarketWithSettlement> buildMarketWithSettlement(
        SportEvent sportEvent,
        UfBetSettlementMarket market,
        int producerId
    );

    Optional<MarketWithProbabilities> buildMarketWithProbabilities(
        SportEvent sportEvent,
        UfOddsChangeMarket market,
        int producerId
    );

    Optional<MarketCancel> buildMarketCancel(SportEvent sportEvent, UfMarket market, int producerId);
}
