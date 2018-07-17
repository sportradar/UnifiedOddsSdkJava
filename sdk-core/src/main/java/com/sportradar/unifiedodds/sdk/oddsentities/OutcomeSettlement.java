/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */

package com.sportradar.unifiedodds.sdk.oddsentities;

/**
 * Describes how to handle a bet on a particular outcome for a particular market
 */
public interface OutcomeSettlement extends Outcome{
    /**
     * 
     * @return true if this outcome is a win, false if it is a loss
     * @deprecated in favour of {@link #getOutcomeResult()}
     */
    @Deprecated
    boolean isWinning();

    /**
     * Under certain circumstances the whole bet is refunded or half the bet is refunded
     * 
     * @return 1 if the whole bet is refunded (regardless of win or loss), 0.5 if half the bet is
     *         refunded (the other half is payed out if it is a win otherwise lost), 0 no refund
     */
    double getVoidFactor();

    /**
     * Dead-heat Factor (A dead-heat factor may be returned for markets where a bet has be placed on
     * a particular team/player to place and this particular player has placed but the place is
     * shared with multiple players, reducing the payout)
     * 
     * @return deadheat factor or 0 if none.
     */
    double getDeadHeatFactor();

    /**
     * Returns an indication of the outcome result state
     *
     * @return an indication of the outcome result state
     */
    OutcomeResult getOutcomeResult();
}
