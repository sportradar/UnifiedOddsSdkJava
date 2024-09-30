/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */
package com.sportradar.unifiedodds.sdk.conn.marketids;

import com.google.common.collect.ImmutableList;
import com.sportradar.unifiedodds.sdk.conn.MarketVariant;
import java.util.List;

public class HoleNrCompetitorUnderParMarketIds {

    public static final int HOLE_NR_COMPETITOR_UNDER_PAR_MARKET_ID = 1026;
    public static final String YES_OUTCOME_ID = "74";
    public static final String NO_OUTCOME_ID = "76";

    public static HoleNrCompetitorUnderParMarket holeNrCompetitorUnderParMarket() {
        return new HoleNrCompetitorUnderParMarket();
    }

    public static class HoleNrCompetitorUnderParMarket implements MarketVariant {

        @Override
        public String id() {
            return String.valueOf(HOLE_NR_COMPETITOR_UNDER_PAR_MARKET_ID);
        }

        @Override
        public List<String> outcomeIds() {
            return ImmutableList.of(YES_OUTCOME_ID, NO_OUTCOME_ID);
        }
    }
}
