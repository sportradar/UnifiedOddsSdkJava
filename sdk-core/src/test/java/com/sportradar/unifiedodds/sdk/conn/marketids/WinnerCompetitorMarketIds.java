/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */
package com.sportradar.unifiedodds.sdk.conn.marketids;

import com.sportradar.unifiedodds.sdk.conn.MarketVariant;
import java.util.List;

public class WinnerCompetitorMarketIds {

    public static final int WINNER_COMPETITOR_MARKET_ID = 1110;

    public static MarketVariant winnerCompetitorMarket(List<String> competitorIds) {
        return new WinnerCompetitorMarket(competitorIds);
    }

    private static final class WinnerCompetitorMarket implements MarketVariant {

        private final List<String> competitorIds;

        public WinnerCompetitorMarket(List<String> competitorIds) {
            this.competitorIds = competitorIds;
        }

        @Override
        public String id() {
            return String.valueOf(WINNER_COMPETITOR_MARKET_ID);
        }

        @Override
        public List<String> outcomeIds() {
            return competitorIds;
        }
    }
}
