/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */
package com.sportradar.unifiedodds.sdk.conn.marketids;

import com.google.common.collect.ImmutableList;
import com.sportradar.unifiedodds.sdk.conn.MarketVariant;
import java.util.List;

public class SetNrBreakNrMarketIds {

    public static final int SET_NR_BREAK_NR_MARKET_ID = 1281;
    public static final String COMPETITOR1_OUTCOME_ID = "6";
    public static final String NONE_OUTCOME_ID = "7";
    public static final String COMPETITOR2_OUTCOME_ID = "8";

    public static MarketVariant setNrBreakNrMarket() {
        return new SetNrBreakNrMarket();
    }

    private static final class SetNrBreakNrMarket implements MarketVariant {

        @Override
        public String id() {
            return String.valueOf(SET_NR_BREAK_NR_MARKET_ID);
        }

        @Override
        public List<String> outcomeIds() {
            return ImmutableList.of(COMPETITOR1_OUTCOME_ID, NONE_OUTCOME_ID, COMPETITOR2_OUTCOME_ID);
        }
    }
}
