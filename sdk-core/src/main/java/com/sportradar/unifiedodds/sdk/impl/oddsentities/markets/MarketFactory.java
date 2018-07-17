/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */

package com.sportradar.unifiedodds.sdk.impl.oddsentities.markets;

import com.sportradar.uf.datamodel.UFBetSettlementMarket;
import com.sportradar.uf.datamodel.UFMarket;
import com.sportradar.uf.datamodel.UFOddsChangeMarket;
import com.sportradar.unifiedodds.sdk.entities.SportEvent;
import com.sportradar.unifiedodds.sdk.oddsentities.Market;
import com.sportradar.unifiedodds.sdk.oddsentities.MarketCancel;
import com.sportradar.unifiedodds.sdk.oddsentities.MarketWithOdds;
import com.sportradar.unifiedodds.sdk.oddsentities.MarketWithProbabilities;
import com.sportradar.unifiedodds.sdk.oddsentities.MarketWithSettlement;

import java.util.Optional;

/**
 * Created on 23/06/2017.
 * // TODO @eti: Javadoc
 */
public interface MarketFactory {
    Optional<Market> buildMarket(SportEvent sportEvent, UFMarket m, int producerId);

    Optional<MarketWithOdds> buildMarketWithOdds(SportEvent sportEvent, UFOddsChangeMarket market, int producerId);

    Optional<MarketWithSettlement> buildMarketWithSettlement(SportEvent sportEvent, UFBetSettlementMarket market, int producerId);

    Optional<MarketWithProbabilities> buildMarketWithProbabilities(SportEvent sportEvent, UFOddsChangeMarket market, int producerId);

    Optional<MarketCancel> buildMarketCancel(SportEvent sportEvent, UFMarket market, int producerId);
}
