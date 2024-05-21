/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */
package com.sportradar.unifiedodds.sdk.conn.marketids;

import static com.sportradar.utils.generic.testing.RandomObjectPicker.pickOneRandomlyFrom;

import com.sportradar.unifiedodds.sdk.oddsentities.MarketWithOdds;
import com.sportradar.unifiedodds.sdk.oddsentities.MarketWithProbabilities;
import com.sportradar.unifiedodds.sdk.oddsentities.OutcomeOdds;
import com.sportradar.unifiedodds.sdk.oddsentities.OutcomeProbabilities;

public final class OddEvenMarketIds {

    public static final int ODD_EVEN_MARKET_ID = 26;

    public static final String ODD_OUTCOME_ID = "70";

    public static final String EVEN_OUTCOME_ID = "72";

    public static OutcomeOdds evenOutcomeOf(MarketWithOdds market) {
        return market.getOutcomeOdds().stream().filter(OddEvenMarketIds::isEven).findFirst().get();
    }

    public static OutcomeProbabilities evenOutcomeOf(MarketWithProbabilities market) {
        return market.getOutcomeProbabilities().stream().filter(OddEvenMarketIds::isEven).findFirst().get();
    }

    public static boolean isEven(OutcomeOdds outcome) {
        return outcome.getId().equals(EVEN_OUTCOME_ID);
    }

    public static boolean isEven(OutcomeProbabilities outcome) {
        return outcome.getId().equals(EVEN_OUTCOME_ID);
    }

    public static String anyOddEvenOutcomeId() {
        return pickOneRandomlyFrom(ODD_OUTCOME_ID, EVEN_OUTCOME_ID);
    }
}
