/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */

package com.sportradar.unifiedodds.sdk.oddsentities;

import com.sportradar.unifiedodds.sdk.entities.NamedValue;
import com.sportradar.unifiedodds.sdk.entities.SportEvent;

import java.util.List;


/**
 * Describes a set of odds changes for a particular competition
 *
 */
public interface OddsChange<T extends SportEvent> extends MarketMessage<T> {

    /**
     * Get the reason why the odds changed
     * 
     * @return if RiskAdjustment this means the user changed some configuration forcing an odds
     *         change, otherwise it is a normal update based on changed conditions (i.e. something
     *         happened in the game or enough time has passed)
     */
    OddsChangeReason getChangeReason();

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

    List<MarketWithOdds> getMarkets();
}
