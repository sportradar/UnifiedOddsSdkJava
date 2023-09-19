/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */

package com.sportradar.unifiedodds.sdk.impl.oddsentities;

import com.google.common.base.Preconditions;
import com.sportradar.uf.datamodel.UfBetSettlement;
import com.sportradar.unifiedodds.sdk.entities.SportEvent;
import com.sportradar.unifiedodds.sdk.impl.oddsentities.markets.MarketFactory;
import com.sportradar.unifiedodds.sdk.oddsentities.*;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created on 23/06/2017.
 * // TODO @eti: Javadoc
 */
@SuppressWarnings({ "ConstantName" })
class BetSettlementImpl<T extends SportEvent> extends EventMessageImpl<T> implements BetSettlement<T> {

    private static final Logger logger = LoggerFactory.getLogger(BetSettlementImpl.class);
    private final BetSettlementCertainty certainty;
    private final List<MarketWithSettlement> affectedMarkets;

    BetSettlementImpl(
        T sportEvent,
        UfBetSettlement message,
        Producer producer,
        byte[] rawMessage,
        MarketFactory marketFactory,
        MessageTimestamp timestamp
    ) {
        super(sportEvent, rawMessage, producer, timestamp, message.getRequestId());
        Preconditions.checkNotNull(marketFactory);

        if (message.getCertainty() == 1) {
            certainty = BetSettlementCertainty.LiveScouted;
        } else if (message.getCertainty() == 2) {
            certainty = BetSettlementCertainty.Confirmed;
        } else {
            certainty = BetSettlementCertainty.Unknown;
        }

        if (message.getOutcomes().getMarket() != null) {
            affectedMarkets =
                message
                    .getOutcomes()
                    .getMarket()
                    .stream()
                    .map(m -> marketFactory.buildMarketWithSettlement(sportEvent, m, message.getProduct()))
                    .filter(Optional::isPresent)
                    .map(Optional::get)
                    .collect(Collectors.toList());
        } else {
            logger.warn("Processing BetSettlement with empty market list");
            affectedMarkets = Collections.emptyList();
        }
    }

    /**
     * @return the certainty of the settlement
     */
    @Override
    public BetSettlementCertainty getCertainty() {
        return certainty;
    }

    @Override
    public List<MarketWithSettlement> getMarkets() {
        return affectedMarkets;
    }
}
