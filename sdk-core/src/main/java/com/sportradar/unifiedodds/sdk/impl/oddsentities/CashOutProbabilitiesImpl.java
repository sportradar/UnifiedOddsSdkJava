/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */

package com.sportradar.unifiedodds.sdk.impl.oddsentities;

import com.google.common.base.Preconditions;
import com.sportradar.uf.datamodel.UFCashout;
import com.sportradar.unifiedodds.sdk.caching.NamedValuesProvider;
import com.sportradar.unifiedodds.sdk.entities.NamedValue;
import com.sportradar.unifiedodds.sdk.entities.SportEvent;
import com.sportradar.unifiedodds.sdk.impl.oddsentities.markets.MarketFactory;
import com.sportradar.unifiedodds.sdk.oddsentities.CashOutProbabilities;
import com.sportradar.unifiedodds.sdk.oddsentities.MarketWithProbabilities;
import com.sportradar.unifiedodds.sdk.oddsentities.MessageTimestamp;
import com.sportradar.unifiedodds.sdk.oddsentities.Producer;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Implements methods used to access event CashOut probabilities
 */
public class CashOutProbabilitiesImpl<T extends SportEvent> extends EventMessageImpl<T> implements CashOutProbabilities<T> {
    private final Integer betstopReason;
    private final Integer bettingStatus;
    private final List<MarketWithProbabilities> marketList;
    private final NamedValuesProvider namedValuesProvider;

    CashOutProbabilitiesImpl(T sportEvent, UFCashout cashoutData, Producer producer, MarketFactory marketFactory, NamedValuesProvider namedValuesProvider, MessageTimestamp timestamp) {
        super(sportEvent, new byte[0], producer, timestamp, cashoutData.getRequestId());

        Preconditions.checkNotNull(marketFactory);
        Preconditions.checkNotNull(namedValuesProvider);

        this.namedValuesProvider = namedValuesProvider;

        if (cashoutData.getOdds() != null) {
            betstopReason = cashoutData.getOdds().getBetstopReason();
            bettingStatus = cashoutData.getOdds().getBettingStatus();

            if (cashoutData.getOdds().getMarket() != null) {
                marketList = cashoutData.getOdds().getMarket().stream()
                        .map(m -> marketFactory.buildMarketWithProbabilities(sportEvent, m, cashoutData.getProduct()))
                        .filter(Optional::isPresent)
                        .map(Optional::get)
                        .collect(Collectors.toList());
            } else {
                marketList = Collections.emptyList();
            }
        } else {
            betstopReason = null;
            bettingStatus = null;
            marketList = Collections.emptyList();
        }
    }

    /**
     * Returns the betstop reason value descriptor
     *
     * @return betstop reason value descriptor
     */
    @Override
    public NamedValue getBetstopReasonValue() {
        if (betstopReason == null) {
            return null;
        }

        return namedValuesProvider.getBetStopReasons().getNamedValue(betstopReason);
    }

    /**
     * Returns the reason for the most recently sent betstop if the betstop is still active
     *
     * @return the reason for the most recently sent betstop if the betstop is still active
     * (otherwise null)
     */
    @Override
    public String getBetstopReason() {
        if (betstopReason == null) {
            return null;
        }

        return namedValuesProvider.getBetStopReasons().getNamedValue(betstopReason).getDescription();
    }

    /**
     * Returns the betting status value descriptor
     *
     * @return the betting status value descriptor
     */
    @Override
    public NamedValue getBettingStatusValue() {
        if (bettingStatus == null) {
            return null;
        }

        return namedValuesProvider.getBettingStatuses().getNamedValue(bettingStatus);
    }

    /**
     * If this field is set, it reports that a previous betstop was sent but the markets have now
     * been reopened. A conservative bookmaker could keep the markets suspended. Previously, we call
     * it that the markets are current in early betstart.
     *
     * @return the betting status - if set the affected markets are in early betstart otherwise
     * null.
     */
    @Override
    public String getBettingStatus() {
        if (bettingStatus == null) {
            return null;
        }

        return namedValuesProvider.getBettingStatuses().getNamedValue(bettingStatus).getDescription();
    }

    @Override
    public List<MarketWithProbabilities> getMarkets() {
        return marketList;
    }
}
