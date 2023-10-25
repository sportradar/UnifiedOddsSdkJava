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
     * Indicates if the outcome is {@link PlayerOutcomeOdds} instance
     *
     * @return <code>true</code> if the current outcome is a player outcome, otherwise <code>false</code>
     */
    boolean isPlayerOutcome();

    /**
     * Gets the odds in specified format
     *
     * @param oddsDisplayType display type of the odds (default: @link OddsDisplayType.Decimal)
     * @return the odds for this outcome in this market in wanted format
     */
    Double getOdds(OddsDisplayType oddsDisplayType);
}
