/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */

package com.sportradar.unifiedodds.sdk.oddsentities;

/**
 * Describes the odds for a particular outcome (e.g. what the current odds for Above - Total Goals
 * 2.5) This is included in the {@link MarketWithOdds} which in turn is included in the
 * {@link OddsChange} message.
 */
@SuppressWarnings({ "OverloadMethodsDeclarationOrder" })
public interface OutcomeOdds extends OutcomeProbabilities {
    /**
     * The odds for this outcome in this market
     *
     * @return the odds for this outcome in this market in decimal
     * @deprecated from v2.0.13 in favour of {@link #getOdds(OddsDisplayType)}
     */
    double getOdds();

    /**
     * Indicates if the outcome is {@link PlayerOutcomeOdds} instance
     *
     * @return <code>true</code> if the current outcome is a player outcome, otherwise <code>false</code>
     */
    boolean isPlayerOutcome();

    /**
     * Gets the odds in specified format
     * Note: default method will be merged in next major version scheduled for January 2019
     * @param oddsDisplayType display type of the odds (default: @link OddsDisplayType.Decimal)
     * @return the odds for this outcome in this market in wanted format
     */
    default Double getOdds(OddsDisplayType oddsDisplayType) {
        throw new UnsupportedOperationException("Method not implemented. Use derived type.");
    }
}
