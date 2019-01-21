/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */

package com.sportradar.unifiedodds.sdk.impl.oddsentities;

import com.google.common.base.Preconditions;
import com.sportradar.uf.datamodel.UFRollbackBetSettlement;
import com.sportradar.unifiedodds.sdk.entities.SportEvent;
import com.sportradar.unifiedodds.sdk.impl.oddsentities.markets.MarketFactory;
import com.sportradar.unifiedodds.sdk.oddsentities.Market;
import com.sportradar.unifiedodds.sdk.oddsentities.MessageTimestamp;
import com.sportradar.unifiedodds.sdk.oddsentities.Producer;
import com.sportradar.unifiedodds.sdk.oddsentities.RollbackBetSettlement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Created on 23/06/2017.
 * // TODO @eti: Javadoc
 */
class RollbackBetSettlementImpl<T extends SportEvent> extends EventMessageImpl<T> implements RollbackBetSettlement<T> {
    private final static Logger logger = LoggerFactory.getLogger(RollbackBetSettlementImpl.class);
    private final List<Market> affectedMarkets;

    RollbackBetSettlementImpl(T sportEvent, UFRollbackBetSettlement message, Producer producer, byte[] rawMessage, MarketFactory factory, MessageTimestamp timestamp) {
        super(sportEvent, rawMessage, producer, timestamp, message.getRequestId());
        Preconditions.checkNotNull(factory);

        if (message.getMarket() != null) {
            affectedMarkets = message.getMarket().stream()
                    .map(m -> factory.buildMarket(sportEvent, m, message.getProduct()))
                    .filter(Optional::isPresent)
                    .map(Optional::get)
                    .collect(Collectors.toList());
        } else {
            logger.warn("Processing RollbackBetSettlement with empty market list. [sportEvent:{}, producer:{}]", sportEvent.getId(), producer);
            affectedMarkets = Collections.emptyList();
        }
    }

    /**
     * @return the list of markets that are affected
     */
    @Override
    public List<Market> getMarkets() {
        return affectedMarkets;
    }
}
