/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */
package com.sportradar.unifiedodds.sdk.conn.marketids;

import static java.util.stream.Collectors.toList;

import com.sportradar.uf.sportsapi.datamodel.SapiTeamCompetitor;
import com.sportradar.unifiedodds.sdk.conn.MarketVariant;
import java.util.List;

public class WinnerCompetitorMarketIds {

    public static final int WINNER_COMPETITOR_MARKET_ID = 1110;

    public static MarketVariant winnerCompetitorMarket(List<SapiTeamCompetitor> competitors) {
        return new WinnerCompetitorMarket(competitors);
    }

    private static final class WinnerCompetitorMarket implements MarketVariant {

        private final List<SapiTeamCompetitor> competitors;

        public WinnerCompetitorMarket(List<SapiTeamCompetitor> competitors) {
            this.competitors = competitors;
        }

        @Override
        public String id() {
            return String.valueOf(WINNER_COMPETITOR_MARKET_ID);
        }

        @Override
        public List<String> outcomeIds() {
            return competitors.stream().map(SapiTeamCompetitor::getId).collect(toList());
        }
    }
}
