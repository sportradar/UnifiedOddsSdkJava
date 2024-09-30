/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */
package com.sportradar.unifiedodds.sdk.conn.marketids;

import com.google.common.collect.ImmutableList;
import com.sportradar.unifiedodds.sdk.conn.MarketVariant;
import java.util.List;

public class EventMatchDayHomeTeamsTotalMarketIds {

    public static final int EVENT_MATCH_DAY_HOME_TEAMS_TOTAL_MARKET_ID = 797;

    public static final String UNDER_TOTAL_OUTCOME_ID = "13";
    public static final String OVER_TOTAL_OUTCOME_ID = "12";

    public static MarketVariant eventMatchDayHomeTeamsTotalMarket() {
        return new EventMatchDayHomeTeamsTotalMarket();
    }

    private static final class EventMatchDayHomeTeamsTotalMarket implements MarketVariant {

        @Override
        public String id() {
            return String.valueOf(EVENT_MATCH_DAY_HOME_TEAMS_TOTAL_MARKET_ID);
        }

        @Override
        public List<String> outcomeIds() {
            return ImmutableList.of(UNDER_TOTAL_OUTCOME_ID, OVER_TOTAL_OUTCOME_ID);
        }
    }
}
