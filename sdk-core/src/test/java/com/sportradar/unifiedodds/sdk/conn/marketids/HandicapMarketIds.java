/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */
package com.sportradar.unifiedodds.sdk.conn.marketids;

import com.google.common.collect.ImmutableList;
import com.sportradar.unifiedodds.sdk.conn.MarketVariant;
import java.util.List;

public class HandicapMarketIds {

    public static final int HANDICAP_MARKET_MARKET_ID = 16;
    public static final String COMPETITOR1_PLUS_HANDICAP_OUTCOME_ID = "1714";
    public static final String COMPETITOR2_MINUS_HANDICAP_OUTCOME_ID = "1715";

    public static MarketVariant handicapMarket() {
        return new HandicapMarket();
    }

    private static final class HandicapMarket implements MarketVariant {

        @Override
        public String id() {
            return String.valueOf(HANDICAP_MARKET_MARKET_ID);
        }

        @Override
        public List<String> outcomeIds() {
            return ImmutableList.of(
                COMPETITOR1_PLUS_HANDICAP_OUTCOME_ID,
                COMPETITOR2_MINUS_HANDICAP_OUTCOME_ID
            );
        }
    }
}
