/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */

package com.sportradar.unifiedodds.sdk.impl.oddsentities.markets;

import com.sportradar.uf.datamodel.UFOutcomeActive;
import com.sportradar.unifiedodds.sdk.impl.markets.NameProvider;
import com.sportradar.unifiedodds.sdk.oddsentities.OutcomeDefinition;
import com.sportradar.unifiedodds.sdk.oddsentities.OutcomeOdds;

import java.util.Locale;

/**
 * Created on 26/06/2017.
 * // TODO @eti: Javadoc
 */
class OutcomeOddsImpl extends OutcomeProbabilitiesImpl implements OutcomeOdds {
    private final double odds;


    OutcomeOddsImpl(String id, NameProvider nameProvider, OutcomeDefinition outcomeDefinition, Locale defaultLocale,
                    UFOutcomeActive active, Double odds, Double probability) {
        super(id, nameProvider, outcomeDefinition, defaultLocale, active, probability);

        this.odds = odds == null ? Double.NaN : odds;
    }


    /**
     * The odds for this outcome in this market
     *
     * @return the odds for this outcome in this market in decimal
     */
    @Override
    public double getOdds() {
        return odds;
    }

    /**
     * Indicates if the outcome is {@link com.sportradar.unifiedodds.sdk.oddsentities.PlayerOutcomeOdds} instance
     *
     * @return <code>true</code> if the current outcome is a player outcome, otherwise <code>false</code>
     */
    @Override
    public boolean isPlayerOutcome() {
        return false;
    }
}
