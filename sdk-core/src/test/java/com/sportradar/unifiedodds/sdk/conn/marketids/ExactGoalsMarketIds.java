/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */
package com.sportradar.unifiedodds.sdk.conn.marketids;

import static java.util.Arrays.asList;

import com.sportradar.unifiedodds.sdk.conn.MarketVariant;
import java.util.List;

public final class ExactGoalsMarketIds {

    public static final int EXACT_GOALS_MARKET_ID = 21;

    private ExactGoalsMarketIds() {}

    public static FivePlusVariant fivePlusVariant() {
        return new FivePlusVariant();
    }

    public static class FivePlusVariant implements MarketVariant {

        @Override
        public String id() {
            return "sr:exact_goals:5+";
        }

        @Override
        public List<String> outcomeIds() {
            return asList(
                "sr:exact_goals:5+:1336",
                "sr:exact_goals:5+:1337",
                "sr:exact_goals:5+:1338",
                "sr:exact_goals:5+:1339",
                "sr:exact_goals:5+:1340",
                "sr:exact_goals:5+:1341"
            );
        }
    }
}
