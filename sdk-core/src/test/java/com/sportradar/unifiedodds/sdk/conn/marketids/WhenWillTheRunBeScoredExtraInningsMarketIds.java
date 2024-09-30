/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */
package com.sportradar.unifiedodds.sdk.conn.marketids;

import com.google.common.collect.ImmutableList;
import com.sportradar.unifiedodds.sdk.conn.MarketVariant;
import java.util.List;

public class WhenWillTheRunBeScoredExtraInningsMarketIds {

    public static final int WHEN_WILL_THE_RUN_BE_SCORED_EXTRA_INNINGS_MARKET_ID = 739;
    public static final String CURRENT_INNING_OUTCOME_ID = "1826";
    public static final String ONE_INNING_INTO_THE_FUTURE_OUTCOME_ID = "1828";
    public static final String TWO_INNINGS_INTO_THE_FUTURE_OUTCOME_ID = "1829";

    public static MarketVariant whenWillTheRunBeScoredExtraInningsMarket() {
        return new WhenWillTheRunBeScoredExtraInningsMarket();
    }

    private static final class WhenWillTheRunBeScoredExtraInningsMarket implements MarketVariant {

        @Override
        public String id() {
            return String.valueOf(WHEN_WILL_THE_RUN_BE_SCORED_EXTRA_INNINGS_MARKET_ID);
        }

        @Override
        public List<String> outcomeIds() {
            return ImmutableList.of(
                CURRENT_INNING_OUTCOME_ID,
                ONE_INNING_INTO_THE_FUTURE_OUTCOME_ID,
                TWO_INNINGS_INTO_THE_FUTURE_OUTCOME_ID
            );
        }
    }
}
