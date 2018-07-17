/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */

package com.sportradar.unifiedodds.sdk.oddsentities;

import com.sportradar.unifiedodds.sdk.entities.NamedValue;
import com.sportradar.unifiedodds.sdk.entities.SportEvent;

import java.util.List;

/**
 * Defines methods implemented by cash-out probability messages
 */
public interface CashOutProbabilities<T extends SportEvent> extends MarketMessage<T> {
    /**
     * Returns the betstop reason value descriptor
     *
     * @return betstop reason value descriptor
     */
    NamedValue getBetstopReasonValue();

    /**
     * Returns the reason for the most recently sent betstop if the betstop is still active
     *
     * @return the reason for the most recently sent betstop if the betstop is still active
     *         (otherwise null)
     */
    String getBetstopReason();

    /**
     * Returns the betting status value descriptor
     *
     * @return the betting status value descriptor
     */
    NamedValue getBettingStatusValue();

    /**
     * If this field is set, it reports that a previous betstop was sent but the markets have now
     * been reopened. A conservative bookmaker could keep the markets suspended. Previously, we call
     * it that the markets are current in early betstart.
     *
     * @return the betting status - if set the affected markets are in early betstart otherwise
     *         null.
     */
    String getBettingStatus();

    List<MarketWithProbabilities> getMarkets();
}
