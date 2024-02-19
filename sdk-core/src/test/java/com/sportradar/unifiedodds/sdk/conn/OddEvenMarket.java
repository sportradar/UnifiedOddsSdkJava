/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */
package com.sportradar.unifiedodds.sdk.conn;

import com.sportradar.unifiedodds.sdk.oddsentities.MarketWithOdds;
import com.sportradar.unifiedodds.sdk.oddsentities.MarketWithProbabilities;
import com.sportradar.unifiedodds.sdk.oddsentities.OutcomeOdds;
import com.sportradar.unifiedodds.sdk.oddsentities.OutcomeProbabilities;

public final class OddEvenMarket {

    public static final int ID = 26;

    public static final String ODD_OUTCOME_ID = "70";

    public static final String EVEN_OUTCOME_ID = "72";

    private OddEvenMarket() {}

    public static OutcomeOdds evenOutcomeOf(MarketWithOdds market) {
        return market.getOutcomeOdds().stream().filter(OddEvenMarket::isEven).findFirst().get();
    }

    public static OutcomeProbabilities evenOutcomeOf(MarketWithProbabilities market) {
        return market.getOutcomeProbabilities().stream().filter(OddEvenMarket::isEven).findFirst().get();
    }

    public static boolean isEven(OutcomeOdds outcome) {
        return outcome.getId().equals(EVEN_OUTCOME_ID);
    }

    public static boolean isEven(OutcomeProbabilities outcome) {
        return outcome.getId().equals(EVEN_OUTCOME_ID);
    }
}
