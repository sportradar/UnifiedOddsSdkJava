/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */
package com.sportradar.unifiedodds.sdk.conn;

import static com.sportradar.unifiedodds.sdk.conn.OddEvenMarket.EVEN_OUTCOME_ID;
import static com.sportradar.unifiedodds.sdk.conn.OddEvenMarket.ODD_OUTCOME_ID;
import static com.sportradar.unifiedodds.sdk.conn.UfMarkets.WithOdds.UfOddsChangeOutcomeBuilder.activeOutcome;
import static com.sportradar.utils.generic.testing.RandomObjectPicker.pickOneRandomlyFrom;

import com.sportradar.uf.datamodel.*;
import com.sportradar.utils.domain.feedmessages.markets.TeamIndicators;
import lombok.val;

public class UfMarkets {

    public static class Simple {

        public static UfMarket oddEven() {
            UfMarket market = new UfMarket();
            market.setId(OddEvenMarket.ID);
            return market;
        }
    }

    public static class WithOdds {

        public static UfOddsChangeMarket oddEven() {
            UfOddsChangeMarket market = new UfOddsChangeMarket();
            market.setId(OddEvenMarket.ID);

            market.getOutcome().add(activeOutcome().withId(ODD_OUTCOME_ID));
            market.getOutcome().add(activeOutcome().withId(EVEN_OUTCOME_ID));

            return market;
        }

        public static UfOddsChangeMarket anytimeGoalscorer() {
            final int anytimeGoalscorerMarketId = 40;
            UfOddsChangeMarket market = new UfOddsChangeMarket();
            market.setId(anytimeGoalscorerMarketId);

            String anytimeScorer1 = "whenFoundExampleOfSuchPleaseReplaceThis1";
            String anytimeScorer2 = "whenFoundExampleOfSuchPleaseReplaceThis2";
            market
                .getOutcome()
                .add(UfOddsChangeOutcomeBuilder.activeOutcome().withAnyTeamAndId(anytimeScorer1));
            market
                .getOutcome()
                .add(UfOddsChangeOutcomeBuilder.activeOutcome().withAnyTeamAndId(anytimeScorer2));

            return market;
        }

        public static class UfOddsChangeOutcomeBuilder {

            private UfOddsChangeOutcomeBuilder() {}

            public static UfOddsChangeOutcomeBuilder activeOutcome() {
                return new UfOddsChangeOutcomeBuilder();
            }

            public UfOddsChangeMarket.UfOutcome withId(String id) {
                UfOddsChangeMarket.UfOutcome outcome = new UfOddsChangeMarket.UfOutcome();
                outcome.setId(id);
                outcome.setOdds(DecimalOdds.any());
                outcome.setActive(UfOutcomeActive.ACTIVE);
                return outcome;
            }

            public UfOddsChangeMarket.UfOutcome withAnyTeamAndId(String id) {
                val outcome = withId(id);
                outcome.setTeam(pickOneRandomlyFrom(TeamIndicators.values()).getValue());
                return outcome;
            }
        }
    }

    public static class WithSettlementOutcomes {

        public static UfBetSettlementMarket oddEvenWhereWonOdd() {
            UfBetSettlementMarket market = new UfBetSettlementMarket();
            market.setId(OddEvenMarket.ID);

            market.getOutcome().add(UfBetSettlementOutcomeBuilder.activeOutcome().wonWithId(ODD_OUTCOME_ID));
            market.getOutcome().add(UfBetSettlementOutcomeBuilder.activeOutcome().wonWithId(EVEN_OUTCOME_ID));

            return market;
        }

        public static class UfBetSettlementOutcomeBuilder {

            private UfBetSettlementOutcomeBuilder() {}

            public static UfBetSettlementOutcomeBuilder activeOutcome() {
                return new UfBetSettlementOutcomeBuilder();
            }

            public UfBetSettlementMarket.UfOutcome wonWithId(String id) {
                UfBetSettlementMarket.UfOutcome outcome = new UfBetSettlementMarket.UfOutcome();
                outcome.setId(id);
                outcome.setResult(UfResult.WON);
                return outcome;
            }

            public UfBetSettlementMarket.UfOutcome lostWithId(String id) {
                UfBetSettlementMarket.UfOutcome outcome = new UfBetSettlementMarket.UfOutcome();
                outcome.setId(id);
                outcome.setResult(UfResult.LOST);
                return outcome;
            }
        }
    }
}
