/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */
package com.sportradar.unifiedodds.sdk.conn.marketids;

import static java.util.Arrays.asList;

import com.sportradar.unifiedodds.sdk.conn.MarketVariant;
import java.util.List;

public class ChampionshipFreeTextMarketIds {

    public static final int CHAMPIONSHIP_FREE_TEXT_MARKET_ID = 534;

    public static final int CHAMPIONSHIP_FREE_TEXT_OPEN_MARKET_ID = 906;

    public static NflAfcConferenceOutrightVariant nflAfcConferenceOutrightsVariant() {
        return new NflAfcConferenceOutrightVariant();
    }

    public static class NflAfcConferenceOutrightVariant implements MarketVariant {

        @Override
        public String id() {
            return "pre:markettext:40841";
        }

        @Override
        public List<String> outcomeIds() {
            return asList(
                "pre:outcometext:35260",
                "pre:outcometext:33133",
                "pre:outcometext:35270",
                "pre:outcometext:35278",
                "pre:outcometext:119349",
                "pre:outcometext:33135",
                "pre:outcometext:33136",
                "pre:outcometext:35274",
                "pre:outcometext:35266",
                "pre:outcometext:35254",
                "pre:outcometext:35250",
                "pre:outcometext:33138",
                "pre:outcometext:35264",
                "pre:outcometext:35249",
                "pre:outcometext:35253",
                "pre:outcometext:35263"
            );
        }
    }
}
