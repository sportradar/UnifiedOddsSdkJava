/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */

package com.sportradar.unifiedodds.sdk.impl.oddsentities.markets;

import com.sportradar.uf.datamodel.UFMarketStatus;
import com.sportradar.unifiedodds.sdk.impl.markets.NameProvider;
import com.sportradar.unifiedodds.sdk.oddsentities.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * Created on 16/10/2017.
 * // TODO @eti: Javadoc
 */
public class MarketWithProbabilitiesImpl extends MarketImpl implements MarketWithProbabilities {
    private final static Logger logger = LoggerFactory.getLogger(MarketWithProbabilitiesImpl.class);
    private final MarketStatus status;
    private final List<OutcomeProbabilities> outcomeProbabilities;
    private final CashOutStatus cashOutStatus;

    MarketWithProbabilitiesImpl(int id, NameProvider nameProvider, Map<String, String> specifiersMap, Map<String, String> extendedSpecifiers, MarketDefinition marketDefinition, Locale defaultLocale, UFMarketStatus status, List<OutcomeProbabilities> outcomeProbabilities, Integer cashoutStatus) {
        super(id, nameProvider, specifiersMap, extendedSpecifiers, marketDefinition, defaultLocale);

        MarketStatus stat = MarketStatus.fromFeedValue(status);
        if (stat == null) {
            logger.warn("Defaulting market status to deactivated, id:{}", id);
            stat = MarketStatus.Deactivated;
        }
        this.status = stat;

        this.outcomeProbabilities = outcomeProbabilities;
        this.cashOutStatus = CashOutStatus.fromFeedValue(cashoutStatus);
    }

    /**
     * Are odds for this market active
     *
     * @return true if odds for this market are active
     */
    @Override
    public MarketStatus getStatus() {
        return status;
    }

    /**
     * Returns a list of probabilities for the different available market outcomes
     *
     * @return a list of probabilities for the different outcomes for this market
     */
    @Override
    public List<OutcomeProbabilities> getOutcomeProbabilities() {
        return outcomeProbabilities;
    }

    /**
     * Returns a {@link CashOutStatus} enum which indicates the availability of cashout
     *
     * @return if available, a {@link CashOutStatus} enum which indicates the availability of cashout; otherwise null
     */
    @Override
    public CashOutStatus getCashOutStatus() {
        return cashOutStatus;
    }
}
