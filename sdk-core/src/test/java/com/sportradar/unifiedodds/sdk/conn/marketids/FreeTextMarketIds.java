/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */
package com.sportradar.unifiedodds.sdk.conn.marketids;

import static java.util.Arrays.asList;

import com.sportradar.unifiedodds.sdk.conn.MarketVariant;
import com.sportradar.unifiedodds.sdk.oddsentities.MarketWithOdds;
import com.sportradar.unifiedodds.sdk.oddsentities.OutcomeOdds;
import java.util.List;

public class FreeTextMarketIds {

    public static final int FREE_TEXT_MARKET_ID = 559;

    public static NascarOutrightsWinnerVariant nascarOutrightsVariant() {
        return new NascarOutrightsWinnerVariant();
    }

    public static NascarOutrightsOddEvenVariant nascarOutrightsOddEvenVariant() {
        return new NascarOutrightsOddEvenVariant();
    }

    public static class NascarOutrightsWinnerVariant implements MarketVariant {

        @Override
        public String id() {
            return "pre:markettext:233945";
        }

        @Override
        public List<String> outcomeIds() {
            return asList(
                "pre:outcometext:9832848",
                "pre:outcometext:11154385",
                "pre:outcometext:8191791",
                "pre:outcometext:9840952",
                "pre:outcometext:9513940",
                "pre:outcometext:6258421",
                "pre:outcometext:7084505",
                "pre:outcometext:6569371",
                "pre:outcometext:505632",
                "pre:outcometext:6621072",
                "pre:outcometext:1014491",
                "pre:outcometext:318760",
                "pre:outcometext:7125706",
                "pre:outcometext:5520588",
                "pre:outcometext:7458692",
                "pre:outcometext:12572116",
                "pre:outcometext:16714708",
                "pre:outcometext:1277357",
                "pre:outcometext:1238461",
                "pre:outcometext:7696944",
                "pre:outcometext:11787001",
                "pre:outcometext:11160813",
                "pre:outcometext:5695824",
                "pre:outcometext:6593846",
                "pre:outcometext:6944491",
                "pre:outcometext:6864419",
                "pre:outcometext:8217403",
                "pre:outcometext:8820457",
                "pre:outcometext:7401353",
                "pre:outcometext:9065140",
                "pre:outcometext:9110228",
                "pre:outcometext:7125704",
                "pre:outcometext:9832847",
                "pre:outcometext:9861894",
                "pre:outcometext:9840951",
                "pre:outcometext:318761"
            );
        }
    }

    public static class NascarOutrightsOddEvenVariant implements MarketVariant {

        public static final String NASCAR_ODD_OUTCOME_ID = "pre:outcometext:15093909";
        public static final String NASCAR_EVEN_OUTCOME_ID = "pre:outcometext:15093907";

        public static OutcomeOdds nascarEvenOutcomeOf(MarketWithOdds market) {
            return market
                .getOutcomeOdds()
                .stream()
                .filter(NascarOutrightsOddEvenVariant::isNascarEven)
                .findFirst()
                .get();
        }

        public static boolean isNascarEven(OutcomeOdds outcome) {
            return outcome.getId().equals(NASCAR_EVEN_OUTCOME_ID);
        }

        @Override
        public String id() {
            return "pre:markettext:234285";
        }

        @Override
        public List<String> outcomeIds() {
            return asList(NASCAR_ODD_OUTCOME_ID, NASCAR_EVEN_OUTCOME_ID);
        }
    }
}
