/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */
package com.sportradar.unifiedodds.sdk.conn;

import static com.sportradar.unifiedodds.sdk.conn.UfMarkets.WithOdds.UfOddsChangeOutcomeBuilder.activeOutcome;
import static com.sportradar.unifiedodds.sdk.conn.marketids.ExactGoalsMarketIds.EXACT_GOALS_MARKET_ID;
import static com.sportradar.unifiedodds.sdk.conn.marketids.FlexScoreMarketIds.*;
import static com.sportradar.unifiedodds.sdk.conn.marketids.FreeTextMarketIds.FREE_TEXT_MARKET_ID;
import static com.sportradar.unifiedodds.sdk.conn.marketids.OddEvenMarketIds.*;
import static com.sportradar.unifiedodds.sdk.testutil.generic.naturallanguage.Prepositions.from;
import static com.sportradar.unifiedodds.sdk.testutil.generic.naturallanguage.Prepositions.to;
import static com.sportradar.utils.generic.testing.RandomObjectPicker.pickOneRandomlyFrom;

import com.sportradar.uf.datamodel.*;
import com.sportradar.uf.sportsapi.datamodel.SapiTeamCompetitor;
import com.sportradar.unifiedodds.sdk.conn.UfSpecifiers.*;
import com.sportradar.unifiedodds.sdk.conn.marketids.*;
import com.sportradar.utils.domain.feedmessages.markets.TeamIndicators;
import java.util.List;
import lombok.val;

public class UfMarkets {

    public static class Simple {

        public static UfMarket oddEvenMarket() {
            UfMarket market = new UfMarket();
            market.setId(ODD_EVEN_MARKET_ID);
            return market;
        }
    }

    @SuppressWarnings("ClassFanOutComplexity")
    public static class WithOdds {

        public static UfOddsChangeMarket oddEvenMarket() {
            UfOddsChangeMarket market = new UfOddsChangeMarket();
            market.setId(ODD_EVEN_MARKET_ID);

            market.getOutcome().add(activeOutcome().withId(ODD_OUTCOME_ID));
            market.getOutcome().add(activeOutcome().withId(EVEN_OUTCOME_ID));

            return market;
        }

        public static UfOddsChangeMarket anytimeGoalscorerMarket() {
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

        public static UfOddsChangeMarket exactGoalsMarket(MarketVariant variant) {
            UfOddsChangeMarket market = new UfOddsChangeMarket();
            market.setId(EXACT_GOALS_MARKET_ID);
            market.setSpecifiers("variant=" + variant.id());
            populateOutcomeIds(from(variant), to(market));
            return market;
        }

        private static void populateOutcomeIds(MarketVariant fromVariant, UfOddsChangeMarket toMarket) {
            fromVariant.outcomeIds().forEach(id -> toMarket.getOutcome().add(activeOutcome().withId(id)));
        }

        public static UfOddsChangeMarket nascarOutrightsMarket() {
            UfOddsChangeMarket market = new UfOddsChangeMarket();
            market.setId(FREE_TEXT_MARKET_ID);
            market.setSpecifiers("variant=" + FreeTextMarketIds.nascarOutrightsVariant().id());
            populateOutcomeIds(from(FreeTextMarketIds.nascarOutrightsVariant()), to(market));
            return market;
        }

        public static UfOddsChangeMarket nascarOutrightsOddEvenMarket() {
            UfOddsChangeMarket market = new UfOddsChangeMarket();
            market.setId(FREE_TEXT_MARKET_ID);
            market.setSpecifiers("variant=" + FreeTextMarketIds.nascarOutrightsOddEvenVariant().id());
            populateOutcomeIds(from(FreeTextMarketIds.nascarOutrightsOddEvenVariant()), to(market));
            return market;
        }

        public static UfOddsChangeMarket correctScoreFlexMarket() {
            UfOddsChangeMarket market = new UfOddsChangeMarket();
            market.setId(CORRECT_SCORE_FLEX_SCORE_MARKET_ID);
            market.setSpecifiers(THREE_TO_ONE_SCORE_SPECIFIER);
            populateOutcomeIds(from(FlexScoreMarketIds.correctScoreFlexScoreMarket()), to(market));
            return market;
        }

        public static UfOddsChangeMarket correctScoreFlexMarket(int currentHomeScore, int currentAwayScore) {
            UfOddsChangeMarket market = new UfOddsChangeMarket();
            market.setId(CORRECT_SCORE_FLEX_SCORE_MARKET_ID);
            market.setSpecifiers(String.format(SCORE_SPECIFIER, currentHomeScore, currentAwayScore));
            populateOutcomeIds(from(FlexScoreMarketIds.correctScoreFlexScoreMarket()), to(market));
            return market;
        }

        public static UfOddsChangeMarket mapDurationMarket(UfMapNrSpecifier mapnr, UfMinuteSpecifier minute) {
            UfOddsChangeMarket market = new UfOddsChangeMarket();
            market.setId(MapDurationMarketIds.MAP_DURATION_MARKET_ID);
            market.setSpecifiers(UfSpecifiers.join(mapnr, minute));
            populateOutcomeIds(from(MapDurationMarketIds.mapDurationMarket()), to(market));
            return market;
        }

        public static UfOddsChangeMarket handicapMarket(UfHandicapSpecifier hcp) {
            UfOddsChangeMarket market = new UfOddsChangeMarket();
            market.setId(HandicapMarketIds.HANDICAP_MARKET_MARKET_ID);
            market.setSpecifiers(hcp.getKeyValue());
            populateOutcomeIds(from(HandicapMarketIds.handicapMarket()), to(market));
            return market;
        }

        public static UfOddsChangeMarket winnerCompetitorMarket(List<SapiTeamCompetitor> competitors) {
            UfOddsChangeMarket market = new UfOddsChangeMarket();
            market.setId(WinnerCompetitorMarketIds.WINNER_COMPETITOR_MARKET_ID);
            populateOutcomeIds(
                from(WinnerCompetitorMarketIds.winnerCompetitorMarket(competitors)),
                to(market)
            );
            return market;
        }

        public static UfOddsChangeMarket whenWillTheRunBeScoredExtraInningsMarket(
            UfInningNrSpecifier inningnr,
            UfRunNrSpecifier runnr
        ) {
            UfOddsChangeMarket market = new UfOddsChangeMarket();
            market.setId(
                WhenWillTheRunBeScoredExtraInningsMarketIds.WHEN_WILL_THE_RUN_BE_SCORED_EXTRA_INNINGS_MARKET_ID
            );
            market.setSpecifiers(UfSpecifiers.join(inningnr, runnr));
            populateOutcomeIds(
                from(WhenWillTheRunBeScoredExtraInningsMarketIds.whenWillTheRunBeScoredExtraInningsMarket()),
                to(market)
            );
            return market;
        }

        public static UfOddsChangeMarket setNrBreakNrMarket(
            UfSetNrSpecifier setnr,
            UfBreakNrSpecifier breaknr
        ) {
            UfOddsChangeMarket market = new UfOddsChangeMarket();
            market.setId(SetNrBreakNrMarketIds.SET_NR_BREAK_NR_MARKET_ID);
            market.setSpecifiers(UfSpecifiers.join(setnr, breaknr));
            populateOutcomeIds(from(SetNrBreakNrMarketIds.setNrBreakNrMarket()), to(market));
            return market;
        }

        public static UfOddsChangeMarket holeNrCompetitorUnderParMarket(
            UfHoleNrSpecifier holeNr,
            UfCompetitorSpecifier competitor
        ) {
            UfOddsChangeMarket market = new UfOddsChangeMarket();
            market.setId(HoleNrCompetitorUnderParMarketIds.HOLE_NR_COMPETITOR_UNDER_PAR_MARKET_ID);
            market.setSpecifiers(UfSpecifiers.join(holeNr, competitor));
            populateOutcomeIds(
                from(HoleNrCompetitorUnderParMarketIds.holeNrCompetitorUnderParMarket()),
                to(market)
            );
            return market;
        }

        public static UfOddsChangeMarket playerToStrikeOutAppearanceTimeAtBatMarket(
            UfAppearanceNrSpecifier appearanceNr,
            UfPlayerSpecifier player
        ) {
            UfOddsChangeMarket market = new UfOddsChangeMarket();
            market.setId(
                PlayerToStrikeOutAppearanceTimeAtBatMarketIds.PLAYER_TO_STRIKE_OUT_APPEARANCE_TIME_AT_BAT_MARKET_ID
            );
            market.setSpecifiers(UfSpecifiers.join(appearanceNr, player));
            populateOutcomeIds(
                from(
                    PlayerToStrikeOutAppearanceTimeAtBatMarketIds.playerToStrikeOutAppearanceTimeAtBatMarket()
                ),
                to(market)
            );
            return market;
        }

        public static UfOddsChangeMarket eventMatchDayHomeTeamsTotalMarket(UfMatchDaySpecifier matchDay) {
            UfOddsChangeMarket market = new UfOddsChangeMarket();
            market.setId(EventMatchDayHomeTeamsTotalMarketIds.EVENT_MATCH_DAY_HOME_TEAMS_TOTAL_MARKET_ID);
            market.setSpecifiers(matchDay.getKeyValue());
            populateOutcomeIds(
                from(EventMatchDayHomeTeamsTotalMarketIds.eventMatchDayHomeTeamsTotalMarket()),
                to(market)
            );
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

            private UfOddsChangeMarket.UfOutcome withAnyTeamAndId(String id) {
                val outcome = withId(id);
                outcome.setTeam(pickOneRandomlyFrom(TeamIndicators.values()).getValue());
                return outcome;
            }
        }
    }

    public static class WithSettlementOutcomes {

        public static UfBetSettlementMarket oddEvenMarketWhereWonOdd() {
            UfBetSettlementMarket market = new UfBetSettlementMarket();
            market.setId(ODD_EVEN_MARKET_ID);

            market.getOutcome().add(UfBetSettlementOutcomeBuilder.activeOutcome().wonWithId(ODD_OUTCOME_ID));
            market.getOutcome().add(UfBetSettlementOutcomeBuilder.activeOutcome().wonWithId(EVEN_OUTCOME_ID));

            return market;
        }

        public static class UfBetSettlementOutcomeBuilder {

            private UfBetSettlementOutcomeBuilder() {}

            public static UfBetSettlementOutcomeBuilder activeOutcome() {
                return new UfBetSettlementOutcomeBuilder();
            }

            private UfBetSettlementMarket.UfOutcome wonWithId(String id) {
                UfBetSettlementMarket.UfOutcome outcome = new UfBetSettlementMarket.UfOutcome();
                outcome.setId(id);
                outcome.setResult(UfResult.WON);
                return outcome;
            }
        }
    }
}
