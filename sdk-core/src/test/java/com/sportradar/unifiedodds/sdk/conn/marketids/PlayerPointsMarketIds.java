/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */
package com.sportradar.unifiedodds.sdk.conn.marketids;

import static java.util.Arrays.asList;

import com.sportradar.unifiedodds.sdk.conn.MarketVariant;
import java.util.List;

public class PlayerPointsMarketIds {

    public static final int PLAYER_POINTS_MARKET_ID = 768;

    public static PlayerPointsVariant playerPointsVariant() {
        return new PlayerPointsVariant();
    }

    public static class PlayerPointsVariant implements MarketVariant {

        @Override
        public String id() {
            return "pre:playerprops:43067097:1876136";
        }

        @Override
        public List<String> outcomeIds() {
            return asList(
                "pre:playerprops:43067097:1876136:31",
                "pre:playerprops:43067097:1876136:33",
                "pre:playerprops:43067097:1876136:35",
                "pre:playerprops:43067097:1876136:37",
                "pre:playerprops:43067097:1876136:39"
            );
        }
    }
}
