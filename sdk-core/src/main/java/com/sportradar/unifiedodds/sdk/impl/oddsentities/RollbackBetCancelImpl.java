/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */

package com.sportradar.unifiedodds.sdk.impl.oddsentities;

import com.google.common.base.Preconditions;
import com.sportradar.uf.datamodel.UFRollbackBetCancel;
import com.sportradar.unifiedodds.sdk.entities.SportEvent;
import com.sportradar.unifiedodds.sdk.impl.oddsentities.markets.MarketFactory;
import com.sportradar.unifiedodds.sdk.oddsentities.Market;
import com.sportradar.unifiedodds.sdk.oddsentities.MessageTimestamp;
import com.sportradar.unifiedodds.sdk.oddsentities.Producer;
import com.sportradar.unifiedodds.sdk.oddsentities.RollbackBetCancel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Created on 23/06/2017.
 * // TODO @eti: Javadoc
 */
class RollbackBetCancelImpl<T extends SportEvent>  extends EventMessageImpl<T> implements RollbackBetCancel<T> {
    private final static Logger logger = LoggerFactory.getLogger(RollbackBetCancelImpl.class);
    private final Date startTime;
    private final Date endTime;
    private final List<Market> affectedMarkets;

    RollbackBetCancelImpl(T sportEvent, UFRollbackBetCancel message, Producer producer, byte[] rawMessage, MarketFactory factory, MessageTimestamp timestamp) {
        super(sportEvent, rawMessage, producer, timestamp, message.getRequestId());
        Preconditions.checkNotNull(factory);

        startTime = message.getStartTime() == null ? null : new Date(message.getStartTime());
        endTime = message.getEndTime() == null ? null : new Date(message.getEndTime());

        if (message.getMarket() != null) {
            affectedMarkets = message.getMarket().stream()
                    .map(m -> factory.buildMarket(sportEvent, m, message.getProduct()))
                    .filter(Optional::isPresent)
                    .map(Optional::get)
                    .collect(Collectors.toList());
        } else {
            logger.warn("Processing RollbackBetCancel with empty market list. [sportEvent:{}, producer:{}]", sportEvent.getId(), producer);
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

    /**
     * @return the timestamp from when bets placed should be rejected (if 0 this means all bets).
     * This should correspond to a startime in a previous betcancel
     */
    @Override
    public Date getStartTime() {
        return startTime;
    }

    /**
     * @return the end of the period for which bets placed should be rejected (if 0 this means all
     * bets). This should correspond to the endtime in a previous betcancel
     */
    @Override
    public Date getEndTime() {
        return endTime;
    }
}
