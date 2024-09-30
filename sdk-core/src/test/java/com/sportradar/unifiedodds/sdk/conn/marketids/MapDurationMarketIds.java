/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */
package com.sportradar.unifiedodds.sdk.conn.marketids;

import com.google.common.collect.ImmutableList;
import com.sportradar.unifiedodds.sdk.conn.MarketVariant;
import java.util.List;

public final class MapDurationMarketIds {

    public static final int MAP_DURATION_MARKET_ID = 725;

    public static final String FIRST_MINUTES_OUTCOME_ID = "1831";

    public static final String REST_MINUTES_OUTCOME_UD = "1832";

    public static MarketVariant mapDurationMarket() {
        return new MapDurationMarket();
    }

    private static final class MapDurationMarket implements MarketVariant {

        @Override
        public String id() {
            return String.valueOf(MAP_DURATION_MARKET_ID);
        }

        @Override
        public List<String> outcomeIds() {
            return ImmutableList.of(FIRST_MINUTES_OUTCOME_ID, REST_MINUTES_OUTCOME_UD);
        }
    }
}
