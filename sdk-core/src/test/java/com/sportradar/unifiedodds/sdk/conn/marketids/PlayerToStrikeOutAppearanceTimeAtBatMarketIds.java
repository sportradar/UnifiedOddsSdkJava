/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */
package com.sportradar.unifiedodds.sdk.conn.marketids;

import com.google.common.collect.ImmutableList;
import com.sportradar.unifiedodds.sdk.conn.MarketVariant;
import java.util.List;

public class PlayerToStrikeOutAppearanceTimeAtBatMarketIds {

    public static final int PLAYER_TO_STRIKE_OUT_APPEARANCE_TIME_AT_BAT_MARKET_ID = 1054;
    public static final String YES_OUTCOME_ID = "74";
    public static final String NO_OUTCOME_ID = "76";

    public static MarketVariant playerToStrikeOutAppearanceTimeAtBatMarket() {
        return new PlayerToStrikeOutAppearanceTimeAtBatMarket();
    }

    private static final class PlayerToStrikeOutAppearanceTimeAtBatMarket implements MarketVariant {

        @Override
        public String id() {
            return String.valueOf(PLAYER_TO_STRIKE_OUT_APPEARANCE_TIME_AT_BAT_MARKET_ID);
        }

        @Override
        public List<String> outcomeIds() {
            return ImmutableList.of(YES_OUTCOME_ID, NO_OUTCOME_ID);
        }
    }
}
