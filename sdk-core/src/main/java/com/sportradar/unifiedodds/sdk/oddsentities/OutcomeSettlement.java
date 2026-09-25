/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */

package com.sportradar.unifiedodds.sdk.oddsentities;

/**
 * Describes how to handle a bet on a particular outcome for a particular market
 */
public interface OutcomeSettlement extends Outcome {
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
     * @return deadheat factor or 1 if none.
     */
    double getDeadHeatFactor();

    /**
     * Returns an indication of the outcome result state
     *
     * @return an indication of the outcome result state
     */
    OutcomeResult getOutcomeResult();

    /**
     * Returns whether the each-way outcome is settled as a win or a place
     *
     * @return the each-way result, or null if not present
     */
    default EachWayResult getEachWayResult() {
        return null;
    }

    /**
     * Returns the each-way factor (fraction of win odds used to settle the place part)
     *
     * @return the each-way factor, or null if not present
     */
    default Double getEachWayFactor() {
        return null;
    }

    /**
     * Returns the dead-heat factor for the place part of an each-way bet
     *
     * @return the dead-heat factor for place, or null if not present
     */
    default Double getDeadHeatFactorPlace() {
        return null;
    }
}
