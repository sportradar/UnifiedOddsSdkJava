/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */

package com.sportradar.unifiedodds.sdk.oddsentities;

import com.sportradar.unifiedodds.sdk.entities.SportEvent;
import com.sportradar.utils.URN;

import java.util.Date;
import java.util.List;

/**
 * Sent to describe that all bets on the particular market (line) for the particular competition
 * should be cancelled and refunded in full. If a time range is specified it only relates to bets
 * for that market within that time period. This could for example happen if a goal is first
 * incorrectly reported and then readjusted. During the period from the incorrect report to the
 * adjustment all bets on related markets would be canceled in retrospect.
 * 
 * Cancel bet is sent when a particular bets made on a particular market needs to be cancelled and
 * refunded due to some kind of error. This is different than a bet-settlement / refund in that this
 * message is sent due to some kind of error.
 */
public interface BetCancel<T extends SportEvent> extends MarketMessage<T> {
    /**
     * If the BetCancel has a time interval, this method will return an indication of when the Bet Cancel interval started
     *
     * @return a {@link Date} indicating the start time of the Bet Cancel interval
     */
    Date getStartTime();

    /**
     * If the BetCancel has a time interval, this method will return an indication of when the Bet Cancel interval finished
     *
     * @return a {@link Date} indicating the end time of the Bet Cancel interval
     */
    Date getEndTime();

    /**
     * If the market was cancelled because of a migration from a different sport event, it gets a {@link URN} specifying the sport event from which the market has migrated.
     *
     * @return the {@link URN} identifier of the superceded event
     */
    String getSupercededBy();

    List<MarketCancel> getMarkets();
}
