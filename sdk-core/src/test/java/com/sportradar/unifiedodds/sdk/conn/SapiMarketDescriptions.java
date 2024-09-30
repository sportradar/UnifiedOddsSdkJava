/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */
package com.sportradar.unifiedodds.sdk.conn;

import static com.sportradar.unifiedodds.sdk.conn.marketids.ChampionshipFreeTextMarketIds.*;
import static com.sportradar.unifiedodds.sdk.conn.marketids.ExactGoalsMarketIds.EXACT_GOALS_MARKET_ID;
import static com.sportradar.unifiedodds.sdk.conn.marketids.FlexScoreMarketIds.CORRECT_SCORE_FLEX_SCORE_MARKET_ID;
import static com.sportradar.unifiedodds.sdk.conn.marketids.FreeTextMarketIds.*;
import static com.sportradar.unifiedodds.sdk.conn.marketids.FreeTextMarketIds.FREE_TEXT_MARKET_ID;
import static com.sportradar.unifiedodds.sdk.conn.marketids.FreeTextMarketIds.NascarOutrightsOddEvenVariant.NASCAR_EVEN_OUTCOME_ID;
import static com.sportradar.unifiedodds.sdk.conn.marketids.FreeTextMarketIds.NascarOutrightsOddEvenVariant.NASCAR_ODD_OUTCOME_ID;
import static com.sportradar.unifiedodds.sdk.conn.marketids.FreeTextMarketIds.nascarOutrightsVariant;
import static com.sportradar.unifiedodds.sdk.conn.marketids.HoleNrCompetitorUnderParMarketIds.HOLE_NR_COMPETITOR_UNDER_PAR_MARKET_ID;
import static com.sportradar.unifiedodds.sdk.conn.marketids.OddEvenMarketIds.*;
import static com.sportradar.unifiedodds.sdk.conn.marketids.OneXtwoMarketIds.*;
import static com.sportradar.unifiedodds.sdk.conn.marketids.PlayerToStrikeOutAppearanceTimeAtBatMarketIds.PLAYER_TO_STRIKE_OUT_APPEARANCE_TIME_AT_BAT_MARKET_ID;
import static com.sportradar.unifiedodds.sdk.testutil.generic.naturallanguage.Prepositions.*;
import static java.util.Arrays.stream;
import static java.util.stream.Collectors.toMap;
import static org.assertj.core.api.Assertions.assertThat;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.sportradar.uf.sportsapi.datamodel.*;
import com.sportradar.unifiedodds.sdk.conn.MarketVariant.OutcomeMapping;
import com.sportradar.unifiedodds.sdk.conn.marketids.*;
import com.sportradar.unifiedodds.sdk.impl.UnifiedFeedConstants;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.stream.Collectors;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.val;

@SuppressWarnings({ "ClassDataAbstractionCoupling", "MultipleStringLiterals" })
public final class SapiMarketDescriptions {

    public static final String LANGUAGE_NOT_SUPPORTED_BY_TEST_FIXTURE =
        "Language not supported by test fixture";

    public static DescOutcomes.Outcome getOutcomeDescription(String outcomeId, DescMarket marketDescription) {
        val outcomes = marketDescription
            .getOutcomes()
            .getOutcome()
            .stream()
            .filter(o -> outcomeId.equals(o.getId()))
            .collect(Collectors.toList());
        assertThat(outcomes.size()).isOne();
        return outcomes.get(0);
    }

    public static DescMarket removeAllOutcomesFrom(DescMarket marketDescription) {
        marketDescription.getOutcomes().getOutcome().clear();
        return marketDescription;
    }

    public static MarketDescriptions notFoundWithEmptyMarket() {
        DescMarket emptyMarketWithIdZero = new DescMarket();
        MarketDescriptions responseForPreMatchWithFaultyMarketAndNotFoundStatus = new MarketDescriptions();
        responseForPreMatchWithFaultyMarketAndNotFoundStatus.setResponseCode(ResponseCode.NOT_FOUND);
        responseForPreMatchWithFaultyMarketAndNotFoundStatus.getMarket().add(emptyMarketWithIdZero);
        return responseForPreMatchWithFaultyMarketAndNotFoundStatus;
    }

    public static class ExactGoals {

        public static DescMarket exactGoalsMarketDescription(Locale language) {
            return exactGoalsMarketDescription(MarketTranslation.getFor(language));
        }

        public static DescMarket exactGoalsMarketDescription() {
            return exactGoalsMarketDescription(MarketTranslation.EN);
        }

        private static DescMarket exactGoalsMarketDescription(MarketTranslation translation) {
            DescMarket market = new DescMarket();
            market.setId(EXACT_GOALS_MARKET_ID);
            market.setName(translation.marketName);
            market.setGroups("all|score|regular_play");
            market.setSpecifiers(SpecifierDescriptions.variant());
            return market;
        }

        @Getter
        @RequiredArgsConstructor
        private enum MarketTranslation {
            EN(Locale.ENGLISH, "Exact goal"),
            FR(Locale.FRENCH, "Nombre de buts exact");

            private final Locale language;
            private final String marketName;

            public static ExactGoals.MarketTranslation getFor(Locale language) {
                return stream(ExactGoals.MarketTranslation.values())
                    .filter(translation -> translation.language.equals(language))
                    .findFirst()
                    .orElseThrow(() -> new RuntimeException(LANGUAGE_NOT_SUPPORTED_BY_TEST_FIXTURE));
            }
        }
    }

    public static class ChampionshipFreeTextMarketDescription {

        public static DescMarket championshipFreeTextMarketDescription() {
            DescMarket market = new DescMarket();
            market.setId(CHAMPIONSHIP_FREE_TEXT_MARKET_ID);
            market.setName("Championship free text market");
            market.setOutcomeType(UnifiedFeedConstants.FREETEXT_VARIANT_VALUE);
            market.setIncludesOutcomesOfType(UnifiedFeedConstants.OUTCOMETEXT_VARIANT_VALUE);
            market.setGroups("all");
            market.setSpecifiers(SpecifierDescriptions.variant());
            return market;
        }
    }

    public static class ChampionshipFreeTextOpenMarketDescription {

        public static DescMarket championshipFreeTextMarketDescription() {
            DescMarket market = new DescMarket();
            market.setId(CHAMPIONSHIP_FREE_TEXT_OPEN_MARKET_ID);
            market.setName("Championship free text market");
            market.setOutcomeType(UnifiedFeedConstants.FREETEXT_VARIANT_VALUE);
            market.setIncludesOutcomesOfType(UnifiedFeedConstants.OUTCOMETEXT_VARIANT_VALUE);
            market.setGroups("all");
            market.setSpecifiers(SpecifierDescriptions.variantAndVersion());
            return market;
        }
    }

    public static class FreeTextMarketDescription {

        public static DescMarket freeTextMarketDescription() {
            DescMarket market = new DescMarket();
            market.setId(FREE_TEXT_MARKET_ID);
            market.setName("Free text market");
            market.setOutcomeType(UnifiedFeedConstants.FREETEXT_VARIANT_VALUE);
            market.setIncludesOutcomesOfType(UnifiedFeedConstants.OUTCOMETEXT_VARIANT_VALUE);
            market.setGroups("all");
            market.setSpecifiers(SpecifierDescriptions.variant());
            return market;
        }
    }

    public static class NascarOutrights {

        private static final Map<String, String> WINNER_ENGLISH_TRANSLATIONS = ImmutableMap
            .<String, String>builder()
            .put("pre:outcometext:9832848", "Gilliland, Todd")
            .put("pre:outcometext:11154385", "Smith, Zane")
            .put("pre:outcometext:8191791", "Preece, Ryan")
            .put("pre:outcometext:9840952", "Haley, Justin")
            .put("pre:outcometext:9513940", "Gragson, Noah")
            .put("pre:outcometext:6258421", "Blaney, Ryan")
            .put("pre:outcometext:7084505", "Buescher, Chris")
            .put("pre:outcometext:6569371", "Wallace Jr., Darrell")
            .put("pre:outcometext:505632", "Hamlin, Denny")
            .put("pre:outcometext:6621072", "Elliott, Chase")
            .put("pre:outcometext:1014491", "Keselowski, Brad")
            .put("pre:outcometext:318760", "Busch, Kyle")
            .put("pre:outcometext:7125706", "Reddick, Tyler")
            .put("pre:outcometext:5520588", "Dillon, Austin")
            .put("pre:outcometext:7458692", "Lajoie, Corey")
            .put("pre:outcometext:12572116", "Hocevar, Carson")
            .put("pre:outcometext:16714708", "Gibbs, Ty")
            .put("pre:outcometext:1277357", "Logano, Joey")
            .put("pre:outcometext:1238461", "McDowell, Michael")
            .put("pre:outcometext:7696944", "Suarez, Daniel")
            .put("pre:outcometext:11787001", "Kraus, Derek")
            .put("pre:outcometext:11160813", "Berry, Josh")
            .put("pre:outcometext:5695824", "Stenhouse Jr, Ricky")
            .put("pre:outcometext:6593846", "Larson, Kyle")
            .put("pre:outcometext:6944491", "Bowman, Alex")
            .put("pre:outcometext:6864419", "Chastain, Ross")
            .put("pre:outcometext:8217403", "Bell, Christopher")
            .put("pre:outcometext:8820457", "Byron, William")
            .put("pre:outcometext:7401353", "Nemechek, John Hunter")
            .put("pre:outcometext:9065140", "Cindric, Austin")
            .put("pre:outcometext:9110228", "Hemric, Daniel")
            .put("pre:outcometext:7125704", "Jones, Erik")
            .put("pre:outcometext:9832847", "Briscoe, Chase")
            .put("pre:outcometext:9861894", "Burton, Harrison")
            .put("pre:outcometext:9840951", "Grala, Kaz")
            .put("pre:outcometext:318761", "Truex Jr, Martin")
            .build();

        private static final Map<String, String> ODD_EVEN_ENGLISH_TRANSLATIONS = ImmutableMap
            .<String, String>builder()
            .put(NASCAR_EVEN_OUTCOME_ID, "Even")
            .put(NASCAR_ODD_OUTCOME_ID, "Odd")
            .build();

        public static DescOutcomes.Outcome nascarEvenOutcomeDescription() {
            return nascarEvenOutcomeDescription(Locale.ENGLISH);
        }

        public static DescOutcomes.Outcome nascarEvenOutcomeDescription(Locale language) {
            return nascarEvenOutcomeDescription(OddEvenMarketTranslation.getFor(language));
        }

        private static DescOutcomes.Outcome nascarEvenOutcomeDescription(
            OddEvenMarketTranslation translation
        ) {
            DescOutcomes.Outcome evenOutcome = new DescOutcomes.Outcome();
            evenOutcome.setId(NASCAR_EVEN_OUTCOME_ID);
            evenOutcome.setName(translation.outcomeTranslations.get(NASCAR_EVEN_OUTCOME_ID));
            return evenOutcome;
        }

        public static DescMarket nascarOutrightsOddEvenMarketDescription(Locale language) {
            return nascarOutrightsOddEvenMarketDescription(OddEvenMarketTranslation.getFor(language));
        }

        public static DescMarket nascarOutrightsOddEvenMarketDescription() {
            return nascarOutrightsOddEvenMarketDescription(OddEvenMarketTranslation.EN);
        }

        private static DescMarket nascarOutrightsOddEvenMarketDescription(
            OddEvenMarketTranslation oddEvenMarketTranslation
        ) {
            DescMarket market = new DescMarket();
            market.setId(FREE_TEXT_MARKET_ID);
            market.setName(oddEvenMarketTranslation.getMarketName());
            market.setVariant(nascarOutrightsOddEvenVariant().id());
            populateOutcomes(
                from(FreeTextMarketIds.nascarOutrightsOddEvenVariant()),
                to(market),
                with(oddEvenMarketTranslation)
            );
            return market;
        }

        public static DescMarket nascarOutrightsMarketDescription(Locale language) {
            return nascarOutrightsMarketDescription(WinnerMarketTranslation.getFor(language));
        }

        public static DescMarket nascarOutrightsMarketDescription() {
            return nascarOutrightsMarketDescription(WinnerMarketTranslation.EN);
        }

        private static DescMarket nascarOutrightsMarketDescription(
            WinnerMarketTranslation winnerMarketTranslation
        ) {
            DescMarket market = new DescMarket();
            market.setId(FREE_TEXT_MARKET_ID);
            market.setName(winnerMarketTranslation.getMarketName());
            market.setVariant(nascarOutrightsVariant().id());
            populateOutcomes(
                from(FreeTextMarketIds.nascarOutrightsVariant()),
                to(market),
                in(winnerMarketTranslation)
            );
            return market;
        }

        public static DescOutcomes.Outcome outcomeDescriptionFor(String outcomeId) {
            return outcomeDescriptionFor(outcomeId, Locale.ENGLISH);
        }

        public static DescOutcomes.Outcome outcomeDescriptionFor(String outcomeId, Locale language) {
            return outcomeDescriptionFor(outcomeId, WinnerMarketTranslation.getFor(language));
        }

        private static DescOutcomes.Outcome outcomeDescriptionFor(
            String outcomeId,
            MarketTranslation translation
        ) {
            return outcome(outcomeId, translation.getOutcomeTranslations().get(outcomeId));
        }

        private static void populateOutcomes(
            MarketVariant fromVariant,
            DescMarket toMarket,
            MarketTranslation translation
        ) {
            val outcomes = new DescOutcomes();
            populateOutcomes(fromVariant, to(outcomes), with(translation));
            toMarket.setOutcomes(outcomes);
        }

        private static void populateOutcomes(
            MarketVariant fromVariant,
            DescOutcomes toOutcomes,
            MarketTranslation translation
        ) {
            fromVariant
                .outcomeIds()
                .forEach(id ->
                    toOutcomes.getOutcome().add(outcome(id, translation.getOutcomeTranslations().get(id)))
                );
        }

        private static DescOutcomes.Outcome outcome(String id, String name) {
            val outcome = new DescOutcomes.Outcome();
            outcome.setId(id);
            outcome.setName(name);
            return outcome;
        }

        public interface MarketTranslation {
            Locale getLanguage();
            String getMarketName();
            Map<String, String> getOutcomeTranslations();
        }

        @RequiredArgsConstructor
        @Getter
        private enum WinnerMarketTranslation implements MarketTranslation {
            EN(
                Locale.ENGLISH,
                "NASCAR Cup Series - Shriners Children’s 500 - Winner",
                WINNER_ENGLISH_TRANSLATIONS
            ),
            FR_SAME_AS_ENGLISH_IN_PROD(
                Locale.FRENCH,
                "NASCAR Cup Series - Shriners Children’s 500 - Winner",
                WINNER_ENGLISH_TRANSLATIONS
            );

            private final Locale language;
            private final String marketName;
            private final Map<String, String> outcomeTranslations;

            public static WinnerMarketTranslation getFor(Locale language) {
                return stream(WinnerMarketTranslation.values())
                    .filter(translation -> translation.language.equals(language))
                    .findFirst()
                    .orElseThrow(() -> new RuntimeException(LANGUAGE_NOT_SUPPORTED_BY_TEST_FIXTURE));
            }
        }

        @RequiredArgsConstructor
        @Getter
        private enum OddEvenMarketTranslation implements MarketTranslation {
            EN(
                Locale.ENGLISH,
                "NASCAR Cup Series - Shriners Children’s 500 - Car Number of Race Winner",
                ODD_EVEN_ENGLISH_TRANSLATIONS
            ),
            FR_SAME_AS_ENGLISH_IN_PROD(
                Locale.FRENCH,
                "NASCAR Cup Series - Shriners Children’s 500 - Car Number of Race Winner",
                ODD_EVEN_ENGLISH_TRANSLATIONS
            );

            private final Locale language;
            private final String marketName;
            private final Map<String, String> outcomeTranslations;

            public static OddEvenMarketTranslation getFor(Locale language) {
                return stream(OddEvenMarketTranslation.values())
                    .filter(translation -> translation.language.equals(language))
                    .findFirst()
                    .orElseThrow(() -> new RuntimeException(LANGUAGE_NOT_SUPPORTED_BY_TEST_FIXTURE));
            }
        }
    }

    public static class NflAfcConferenceOutrights {

        private static final Map<String, String> ENGLISH_TRANSLATIONS = ImmutableMap
            .<String, String>builder()
            .put("pre:outcometext:35260", "Jacksonville Jaguars")
            .put("pre:outcometext:33133", "Pittsburgh Steelers")
            .put("pre:outcometext:35270", "New England Patriots")
            .put("pre:outcometext:35278", "Cleveland Browns")
            .put("pre:outcometext:119349", "Houston Texans")
            .put("pre:outcometext:33135", "Cincinnati Bengals")
            .put("pre:outcometext:33136", "Las Vegas Raiders")
            .put("pre:outcometext:35274", "Los Angeles Chargers")
            .put("pre:outcometext:35266", "Kansas City Chiefs")
            .put("pre:outcometext:35254", "Indianapolis Colts")
            .put("pre:outcometext:35250", "Baltimore Ravens")
            .put("pre:outcometext:33138", "Miami Dolphins")
            .put("pre:outcometext:35264", "Buffalo Bills")
            .put("pre:outcometext:35249", "Tennessee Titans")
            .put("pre:outcometext:35253", "Denver Broncos")
            .put("pre:outcometext:35263", "New York Jets")
            .build();

        public static DescMarket nflAfcConferenceOutrightsMarketDescription(Locale language) {
            return nflAfcConferenceOutrightsMarketDescription(WinnerMarketTranslation.getFor(language));
        }

        public static DescMarket nflAfcConferenceOutrightsMarketDescription() {
            return nflAfcConferenceOutrightsMarketDescription(WinnerMarketTranslation.EN);
        }

        private static DescMarket nflAfcConferenceOutrightsMarketDescription(
            WinnerMarketTranslation marketTranslation
        ) {
            DescMarket market = new DescMarket();
            market.setId(CHAMPIONSHIP_FREE_TEXT_MARKET_ID);
            market.setName(marketTranslation.getMarketName());
            market.setVariant(nflAfcConferenceOutrightsVariant().id());
            populateOutcomes(from(nflAfcConferenceOutrightsVariant()), to(market), with(marketTranslation));
            return market;
        }

        public static DescMarket openMarket(DescMarket market) {
            if (market.getId() != CHAMPIONSHIP_FREE_TEXT_MARKET_ID) {
                throw new RuntimeException("Only championship free text market is supported");
            }
            market.setId(CHAMPIONSHIP_FREE_TEXT_OPEN_MARKET_ID);
            return market;
        }

        private static void populateOutcomes(
            MarketVariant fromVariant,
            DescMarket toMarket,
            MarketTranslation translation
        ) {
            val outcomes = new DescOutcomes();
            populateOutcomes(fromVariant, to(outcomes), with(translation));
            toMarket.setOutcomes(outcomes);
        }

        private static void populateOutcomes(
            MarketVariant fromVariant,
            DescOutcomes toOutcomes,
            MarketTranslation translation
        ) {
            fromVariant
                .outcomeIds()
                .forEach(id ->
                    toOutcomes.getOutcome().add(outcome(id, translation.getOutcomeTranslations().get(id)))
                );
        }

        private static DescOutcomes.Outcome outcome(String id, String name) {
            val outcome = new DescOutcomes.Outcome();
            outcome.setId(id);
            outcome.setName(name);
            return outcome;
        }

        public interface MarketTranslation {
            Locale getLanguage();
            String getMarketName();
            Map<String, String> getOutcomeTranslations();
        }

        @RequiredArgsConstructor
        @Getter
        private enum WinnerMarketTranslation implements MarketTranslation {
            EN(Locale.ENGLISH, "NFL 2017/18 AFC Conference - Winner", ENGLISH_TRANSLATIONS),
            FR_SAME_AS_ENGLISH_IN_PROD(
                Locale.FRENCH,
                "NFL 2017/18 AFC Conference - Winner",
                ENGLISH_TRANSLATIONS
            );

            private final Locale language;
            private final String marketName;
            private final Map<String, String> outcomeTranslations;

            public static WinnerMarketTranslation getFor(Locale language) {
                return stream(WinnerMarketTranslation.values())
                    .filter(translation -> translation.language.equals(language))
                    .findFirst()
                    .orElseThrow(() -> new RuntimeException(LANGUAGE_NOT_SUPPORTED_BY_TEST_FIXTURE));
            }
        }
    }

    public static class OddEven {

        public static DescMarket oddEvenMarketDescription() {
            return oddEvenMarketDescription(Locale.ENGLISH);
        }

        public static DescMarket oddEvenMarketDescription(Locale language) {
            return oddEvenMarketDescription(MarketTranslation.getFor(language));
        }

        private static DescMarket oddEvenMarketDescription(MarketTranslation translation) {
            DescMarket market = new DescMarket();
            market.setId(ODD_EVEN_MARKET_ID);
            market.setName(translation.marketName);
            market.setOutcomes(new DescOutcomes());
            market.getOutcomes().getOutcome().add(oddOutcomeDescription());
            market.getOutcomes().getOutcome().add(evenOutcomeDescription());
            return market;
        }

        public static DescOutcomes.Outcome oddOutcomeDescription() {
            return oddOutcomeDescription(Locale.ENGLISH);
        }

        public static DescOutcomes.Outcome oddOutcomeDescription(Locale language) {
            return oddOutcomeDescription(MarketTranslation.getFor(language));
        }

        private static DescOutcomes.Outcome oddOutcomeDescription(MarketTranslation translation) {
            DescOutcomes.Outcome evenOutcome = new DescOutcomes.Outcome();
            evenOutcome.setId(ODD_OUTCOME_ID);
            evenOutcome.setName(translation.getOddOutcomeName());
            return evenOutcome;
        }

        public static DescOutcomes.Outcome evenOutcomeDescription() {
            return evenOutcomeDescription(Locale.ENGLISH);
        }

        public static DescOutcomes.Outcome evenOutcomeDescription(Locale language) {
            return evenOutcomeDescription(MarketTranslation.getFor(language));
        }

        private static DescOutcomes.Outcome evenOutcomeDescription(MarketTranslation translation) {
            DescOutcomes.Outcome evenOutcome = new DescOutcomes.Outcome();
            evenOutcome.setId(EVEN_OUTCOME_ID);
            evenOutcome.setName(translation.getEvenOutcomeName());
            return evenOutcome;
        }

        @RequiredArgsConstructor
        @Getter
        private enum MarketTranslation {
            EN(Locale.ENGLISH, "Odd/Even", "Odd", "Even"),
            FR(Locale.FRENCH, "Pair/Impair", "impair", "pair");

            private final Locale language;
            private final String marketName;
            private final String oddOutcomeName;
            private final String evenOutcomeName;

            public static MarketTranslation getFor(Locale language) {
                return stream(MarketTranslation.values())
                    .filter(translation -> translation.language.equals(language))
                    .findFirst()
                    .orElseThrow(() -> new RuntimeException(LANGUAGE_NOT_SUPPORTED_BY_TEST_FIXTURE));
            }
        }
    }

    public static class OneXtwo {

        public static DescMarket oneXtwoMarketDescription() {
            return oneXtwoMarketDescription(Locale.ENGLISH);
        }

        public static DescMarket oneXtwoMarketDescription(Locale language) {
            return oneXtwoMarketDescription(MarketTranslation.getFor(language));
        }

        private static DescMarket oneXtwoMarketDescription(MarketTranslation translation) {
            DescMarket market = new DescMarket();
            market.setId(ONE_X_TWO_MARKET_ID);
            market.setName(translation.marketName);
            market.setGroups("all|score|regular_play");
            market.setOutcomes(new DescOutcomes());
            market.getOutcomes().getOutcome().add(competitor1outcomeDescription());
            market.getOutcomes().getOutcome().add(competitor2outcomeDescription());
            return market;
        }

        public static DescOutcomes.Outcome competitor1outcomeDescription() {
            return competitor1outcomeDescription(Locale.ENGLISH);
        }

        public static DescOutcomes.Outcome competitor1outcomeDescription(Locale language) {
            return competitor1outcomeDescription(MarketTranslation.getFor(language));
        }

        private static DescOutcomes.Outcome competitor1outcomeDescription(MarketTranslation translation) {
            DescOutcomes.Outcome evenOutcome = new DescOutcomes.Outcome();
            evenOutcome.setId(COMPETITOR_1_OUTCOME_ID);
            evenOutcome.setName(translation.getCompetitor1OutcomeName());
            return evenOutcome;
        }

        public static DescOutcomes.Outcome drawOutcomeDescription() {
            return drawOutcomeDescription(Locale.ENGLISH);
        }

        public static DescOutcomes.Outcome drawOutcomeDescription(Locale language) {
            return drawOutcomeDescription(MarketTranslation.getFor(language));
        }

        private static DescOutcomes.Outcome drawOutcomeDescription(MarketTranslation translation) {
            DescOutcomes.Outcome evenOutcome = new DescOutcomes.Outcome();
            evenOutcome.setId(DRAW_OUTCOME_ID);
            evenOutcome.setName(translation.getDrawOutcomeName());
            return evenOutcome;
        }

        public static DescOutcomes.Outcome competitor2outcomeDescription() {
            return competitor2outcomeDescription(Locale.ENGLISH);
        }

        public static DescOutcomes.Outcome competitor2outcomeDescription(Locale language) {
            return competitor2outcomeDescription(MarketTranslation.getFor(language));
        }

        private static DescOutcomes.Outcome competitor2outcomeDescription(MarketTranslation translation) {
            DescOutcomes.Outcome evenOutcome = new DescOutcomes.Outcome();
            evenOutcome.setId(COMPETITOR_2_OUTCOME_ID);
            evenOutcome.setName(translation.getCompetitor2OutcomeName());
            return evenOutcome;
        }

        @RequiredArgsConstructor
        @Getter
        private enum MarketTranslation {
            EN(Locale.ENGLISH, "1x2", "{$competitor1}", "Match nul", "{$competitor2}");

            private final Locale language;
            private final String marketName;
            private final String competitor1OutcomeName;
            private final String drawOutcomeName;
            private final String competitor2OutcomeName;

            public static MarketTranslation getFor(Locale language) {
                return stream(MarketTranslation.values())
                    .filter(translation -> translation.language.equals(language))
                    .findFirst()
                    .orElseThrow(() -> new RuntimeException(LANGUAGE_NOT_SUPPORTED_BY_TEST_FIXTURE));
            }
        }
    }

    public static class CorrectScoreFlex {

        //<editor-fold desc="English translations for outcomes">
        private static final Map<String, String> ENGLISH_TRANSLATIONS = ImmutableMap
            .<String, String>builder()
            .put("1058", "0:0")
            .put("1059", "0:1")
            .put("1060", "0:2")
            .put("1061", "0:3")
            .put("1062", "0:4")
            .put("1063", "0:5")
            .put("1064", "0:6")
            .put("1065", "0:7")
            .put("1066", "0:8")
            .put("1067", "0:9")
            .put("1068", "0:10")
            .put("1069", "0:11")
            .put("1070", "0:12")
            .put("1071", "0:13")
            .put("1072", "0:14")
            .put("1073", "0:15")
            .put("1074", "0:16")
            .put("1075", "0:17")
            .put("1076", "0:18")
            .put("1077", "0:19")
            .put("1078", "1:0")
            .put("1079", "1:1")
            .put("1080", "1:2")
            .put("1081", "1:3")
            .put("1082", "1:4")
            .put("1083", "1:5")
            .put("1084", "1:6")
            .put("1085", "1:7")
            .put("1086", "1:8")
            .put("1087", "1:9")
            .put("1088", "1:10")
            .put("1089", "1:11")
            .put("1090", "1:12")
            .put("1091", "1:13")
            .put("1092", "1:14")
            .put("1093", "1:15")
            .put("1094", "1:16")
            .put("1095", "1:17")
            .put("1096", "1:18")
            .put("1097", "1:19")
            .put("1098", "2:0")
            .put("1099", "2:1")
            .put("1100", "2:2")
            .put("1101", "2:3")
            .put("1102", "2:4")
            .put("1103", "2:5")
            .put("1104", "2:6")
            .put("1105", "2:7")
            .put("1106", "2:8")
            .put("1107", "2:9")
            .put("1108", "2:10")
            .put("1109", "2:11")
            .put("1110", "2:12")
            .put("1111", "2:13")
            .put("1112", "2:14")
            .put("1113", "2:15")
            .put("1114", "2:16")
            .put("1115", "2:17")
            .put("1116", "2:18")
            .put("1117", "2:19")
            .put("1118", "3:0")
            .put("1119", "3:1")
            .put("1120", "3:2")
            .put("1121", "3:3")
            .put("1122", "3:4")
            .put("1123", "3:5")
            .put("1124", "3:6")
            .put("1125", "3:7")
            .put("1126", "3:8")
            .put("1127", "3:9")
            .put("1128", "3:10")
            .put("1129", "3:11")
            .put("1130", "3:12")
            .put("1131", "3:13")
            .put("1132", "3:14")
            .put("1133", "3:15")
            .put("1134", "3:16")
            .put("1135", "3:17")
            .put("1136", "3:18")
            .put("1137", "3:19")
            .put("1138", "4:0")
            .put("1139", "4:1")
            .put("1140", "4:2")
            .put("1141", "4:3")
            .put("1142", "4:4")
            .put("1143", "4:5")
            .put("1144", "4:6")
            .put("1145", "4:7")
            .put("1146", "4:8")
            .put("1147", "4:9")
            .put("1148", "4:10")
            .put("1149", "4:11")
            .put("1150", "4:12")
            .put("1151", "4:13")
            .put("1152", "4:14")
            .put("1153", "4:15")
            .put("1154", "4:16")
            .put("1155", "4:17")
            .put("1156", "4:18")
            .put("1157", "4:19")
            .put("1158", "5:0")
            .put("1159", "5:1")
            .put("1160", "5:2")
            .put("1161", "5:3")
            .put("1162", "5:4")
            .put("1163", "5:5")
            .put("1164", "5:6")
            .put("1165", "5:7")
            .put("1166", "5:8")
            .put("1167", "5:9")
            .put("1168", "5:10")
            .put("1169", "5:11")
            .put("1170", "5:12")
            .put("1171", "5:13")
            .put("1172", "5:14")
            .put("1173", "5:15")
            .put("1174", "5:16")
            .put("1175", "5:17")
            .put("1176", "5:18")
            .put("1177", "5:19")
            .put("1178", "6:0")
            .put("1179", "6:1")
            .put("1180", "6:2")
            .put("1181", "6:3")
            .put("1182", "6:4")
            .put("1183", "6:5")
            .put("1184", "6:6")
            .put("1185", "6:7")
            .put("1186", "6:8")
            .put("1187", "6:9")
            .put("1188", "6:10")
            .put("1189", "6:11")
            .put("1190", "6:12")
            .put("1191", "6:13")
            .put("1192", "6:14")
            .put("1193", "6:15")
            .put("1194", "6:16")
            .put("1195", "6:17")
            .put("1196", "6:18")
            .put("1197", "6:19")
            .put("1198", "7:0")
            .put("1199", "7:1")
            .put("1200", "7:2")
            .put("1201", "7:3")
            .put("1202", "7:4")
            .put("1203", "7:5")
            .put("1204", "7:6")
            .put("1205", "7:7")
            .put("1206", "7:8")
            .put("1207", "7:9")
            .put("1208", "7:10")
            .put("1209", "7:11")
            .put("1210", "7:12")
            .put("1211", "7:13")
            .put("1212", "7:14")
            .put("1213", "7:15")
            .put("1214", "7:16")
            .put("1215", "7:17")
            .put("1216", "7:18")
            .put("1217", "7:19")
            .put("1218", "8:0")
            .put("1219", "8:1")
            .put("1220", "8:2")
            .put("1221", "8:3")
            .put("1222", "8:4")
            .put("1223", "8:5")
            .put("1224", "8:6")
            .put("1225", "8:7")
            .put("1226", "8:8")
            .put("1227", "8:9")
            .put("1228", "8:10")
            .put("1229", "8:11")
            .put("1230", "8:12")
            .put("1231", "8:13")
            .put("1232", "8:14")
            .put("1233", "8:15")
            .put("1234", "8:16")
            .put("1235", "8:17")
            .put("1236", "8:18")
            .put("1237", "8:19")
            .put("1238", "9:0")
            .put("1239", "9:1")
            .put("1240", "9:2")
            .put("1241", "9:3")
            .put("1242", "9:4")
            .put("1243", "9:5")
            .put("1244", "9:6")
            .put("1245", "9:7")
            .put("1246", "9:8")
            .put("1247", "9:9")
            .put("1248", "9:10")
            .put("1249", "9:11")
            .put("1250", "9:12")
            .put("1251", "9:13")
            .put("1252", "9:14")
            .put("1253", "9:15")
            .put("1254", "9:16")
            .put("1255", "9:17")
            .put("1256", "9:18")
            .put("1257", "9:19")
            .put("1258", "10:0")
            .put("1259", "10:1")
            .put("1260", "10:2")
            .put("1261", "10:3")
            .put("1262", "10:4")
            .put("1263", "10:5")
            .put("1264", "10:6")
            .put("1265", "10:7")
            .put("1266", "10:8")
            .put("1267", "10:9")
            .put("1268", "10:10")
            .put("1269", "10:11")
            .put("1270", "10:12")
            .put("1271", "10:13")
            .put("1272", "10:14")
            .put("1273", "10:15")
            .put("1274", "10:16")
            .put("1275", "10:17")
            .put("1276", "10:18")
            .put("1277", "10:19")
            .put("1278", "11:0")
            .put("1279", "11:1")
            .put("1280", "11:2")
            .put("1281", "11:3")
            .put("1282", "11:4")
            .put("1283", "11:5")
            .put("1284", "11:6")
            .put("1285", "11:7")
            .put("1286", "11:8")
            .put("1287", "11:9")
            .put("1288", "11:10")
            .put("1289", "11:11")
            .put("1290", "11:12")
            .put("1291", "11:13")
            .put("1292", "11:14")
            .put("1293", "11:15")
            .put("1294", "11:16")
            .put("1295", "11:17")
            .put("1296", "11:18")
            .put("1297", "11:19")
            .put("1298", "12:0")
            .put("1299", "12:1")
            .put("1300", "12:2")
            .put("1301", "12:3")
            .put("1302", "12:4")
            .put("1303", "12:5")
            .put("1304", "12:6")
            .put("1305", "12:7")
            .put("1306", "12:8")
            .put("1307", "12:9")
            .put("1308", "12:10")
            .put("1309", "12:11")
            .put("1310", "12:12")
            .put("1311", "12:13")
            .put("1312", "12:14")
            .put("1313", "12:15")
            .put("1314", "12:16")
            .put("1315", "12:17")
            .put("1316", "12:18")
            .put("1317", "12:19")
            .put("1318", "13:0")
            .put("1319", "13:1")
            .put("1320", "13:2")
            .put("1321", "13:3")
            .put("1322", "13:4")
            .put("1323", "13:5")
            .put("1324", "13:6")
            .put("1325", "13:7")
            .put("1326", "13:8")
            .put("1327", "13:9")
            .put("1328", "13:10")
            .put("1329", "13:11")
            .put("1330", "13:12")
            .put("1331", "13:13")
            .put("1332", "13:14")
            .put("1333", "13:15")
            .put("1334", "13:16")
            .put("1335", "13:17")
            .put("1336", "13:18")
            .put("1337", "13:19")
            .put("1338", "14:0")
            .put("1339", "14:1")
            .put("1340", "14:2")
            .put("1341", "14:3")
            .put("1342", "14:4")
            .put("1343", "14:5")
            .put("1344", "14:6")
            .put("1345", "14:7")
            .put("1346", "14:8")
            .put("1347", "14:9")
            .put("1348", "14:10")
            .put("1349", "14:11")
            .put("1350", "14:12")
            .put("1351", "14:13")
            .put("1352", "14:14")
            .put("1353", "14:15")
            .put("1354", "14:16")
            .put("1355", "14:17")
            .put("1356", "14:18")
            .put("1357", "14:19")
            .put("1358", "15:0")
            .put("1359", "15:1")
            .put("1360", "15:2")
            .put("1361", "15:3")
            .put("1362", "15:4")
            .put("1363", "15:5")
            .put("1364", "15:6")
            .put("1365", "15:7")
            .put("1366", "15:8")
            .put("1367", "15:9")
            .put("1368", "15:10")
            .put("1369", "15:11")
            .put("1370", "15:12")
            .put("1371", "15:13")
            .put("1372", "15:14")
            .put("1373", "15:15")
            .put("1374", "15:16")
            .put("1375", "15:17")
            .put("1376", "15:18")
            .put("1377", "15:19")
            .put("1378", "16:0")
            .put("1379", "16:1")
            .put("1380", "16:2")
            .put("1381", "16:3")
            .put("1382", "16:4")
            .put("1383", "16:5")
            .put("1384", "16:6")
            .put("1385", "16:7")
            .put("1386", "16:8")
            .put("1387", "16:9")
            .put("1388", "16:10")
            .put("1389", "16:11")
            .put("1390", "16:12")
            .put("1391", "16:13")
            .put("1392", "16:14")
            .put("1393", "16:15")
            .put("1394", "16:16")
            .put("1395", "16:17")
            .put("1396", "16:18")
            .put("1397", "16:19")
            .put("1398", "17:0")
            .put("1399", "17:1")
            .put("1400", "17:2")
            .put("1401", "17:3")
            .put("1402", "17:4")
            .put("1403", "17:5")
            .put("1404", "17:6")
            .put("1405", "17:7")
            .put("1406", "17:8")
            .put("1407", "17:9")
            .put("1408", "17:10")
            .put("1409", "17:11")
            .put("1410", "17:12")
            .put("1411", "17:13")
            .put("1412", "17:14")
            .put("1413", "17:15")
            .put("1414", "17:16")
            .put("1415", "17:17")
            .put("1416", "17:18")
            .put("1417", "17:19")
            .put("1418", "18:0")
            .put("1419", "18:1")
            .put("1420", "18:2")
            .put("1421", "18:3")
            .put("1422", "18:4")
            .put("1423", "18:5")
            .put("1424", "18:6")
            .put("1425", "18:7")
            .put("1426", "18:8")
            .put("1427", "18:9")
            .put("1428", "18:10")
            .put("1429", "18:11")
            .put("1430", "18:12")
            .put("1431", "18:13")
            .put("1432", "18:14")
            .put("1433", "18:15")
            .put("1434", "18:16")
            .put("1435", "18:17")
            .put("1436", "18:18")
            .put("1437", "18:19")
            .put("1438", "19:0")
            .put("1439", "19:1")
            .put("1440", "19:2")
            .put("1441", "19:3")
            .put("1442", "19:4")
            .put("1443", "19:5")
            .put("1444", "19:6")
            .put("1445", "19:7")
            .put("1446", "19:8")
            .put("1447", "19:9")
            .put("1448", "19:10")
            .put("1449", "19:11")
            .put("1450", "19:12")
            .put("1451", "19:13")
            .put("1452", "19:14")
            .put("1453", "19:15")
            .put("1454", "19:16")
            .put("1455", "19:17")
            .put("1456", "19:18")
            .put("1457", "19:19")
            .build();

        //</editor-fold>

        //<editor-fold desc="English translations for mapping outcomes">
        private static final Map<String, OutcomeMapping> OUTCOME_MAPPING_ENGLISH_TRANSLATIONS = ImmutableList
            .<OutcomeMapping>builder()
            .add(new OutcomeMapping("1058", "700", "0:0"))
            .add(new OutcomeMapping("1059", "702", "0:1"))
            .add(new OutcomeMapping("1060", "704", "0:2"))
            .add(new OutcomeMapping("1061", "706", "0:3"))
            .add(new OutcomeMapping("1062", "708", "0:4"))
            .add(new OutcomeMapping("1063", "710", "0:5"))
            .add(new OutcomeMapping("1064", "712", "0:6"))
            .add(new OutcomeMapping("1065", "714", "0:7"))
            .add(new OutcomeMapping("1066", "716", "0:8"))
            .add(new OutcomeMapping("1067", "718", "0:9"))
            .add(new OutcomeMapping("1068", "720", "0:10"))
            .add(new OutcomeMapping("1069", "722", "0:11"))
            .add(new OutcomeMapping("1070", "724", "0:12"))
            .add(new OutcomeMapping("1071", "726", "0:13"))
            .add(new OutcomeMapping("1072", "728", "0:14"))
            .add(new OutcomeMapping("1073", "730", "0:15"))
            .add(new OutcomeMapping("1074", "732", "0:16"))
            .add(new OutcomeMapping("1075", "734", "0:17"))
            .add(new OutcomeMapping("1076", "736", "0:18"))
            .add(new OutcomeMapping("1077", "738", "0:19"))
            .add(new OutcomeMapping("1078", "740", "1:0"))
            .add(new OutcomeMapping("1079", "742", "1:1"))
            .add(new OutcomeMapping("1080", "744", "1:2"))
            .add(new OutcomeMapping("1081", "746", "1:3"))
            .add(new OutcomeMapping("1082", "748", "1:4"))
            .add(new OutcomeMapping("1083", "750", "1:5"))
            .add(new OutcomeMapping("1084", "752", "1:6"))
            .add(new OutcomeMapping("1085", "754", "1:7"))
            .add(new OutcomeMapping("1086", "756", "1:8"))
            .add(new OutcomeMapping("1087", "758", "1:9"))
            .add(new OutcomeMapping("1088", "760", "1:10"))
            .add(new OutcomeMapping("1089", "762", "1:11"))
            .add(new OutcomeMapping("1090", "764", "1:12"))
            .add(new OutcomeMapping("1091", "766", "1:13"))
            .add(new OutcomeMapping("1092", "768", "1:14"))
            .add(new OutcomeMapping("1093", "770", "1:15"))
            .add(new OutcomeMapping("1094", "772", "1:16"))
            .add(new OutcomeMapping("1095", "774", "1:17"))
            .add(new OutcomeMapping("1096", "776", "1:18"))
            .add(new OutcomeMapping("1097", "778", "1:19"))
            .add(new OutcomeMapping("1098", "780", "2:0"))
            .add(new OutcomeMapping("1099", "782", "2:1"))
            .add(new OutcomeMapping("1100", "784", "2:2"))
            .add(new OutcomeMapping("1101", "786", "2:3"))
            .add(new OutcomeMapping("1102", "788", "2:4"))
            .add(new OutcomeMapping("1103", "790", "2:5"))
            .add(new OutcomeMapping("1104", "792", "2:6"))
            .add(new OutcomeMapping("1105", "794", "2:7"))
            .add(new OutcomeMapping("1106", "796", "2:8"))
            .add(new OutcomeMapping("1107", "798", "2:9"))
            .add(new OutcomeMapping("1108", "800", "2:10"))
            .add(new OutcomeMapping("1109", "802", "2:11"))
            .add(new OutcomeMapping("1110", "804", "2:12"))
            .add(new OutcomeMapping("1111", "806", "2:13"))
            .add(new OutcomeMapping("1112", "808", "2:14"))
            .add(new OutcomeMapping("1113", "810", "2:15"))
            .add(new OutcomeMapping("1114", "812", "2:16"))
            .add(new OutcomeMapping("1115", "814", "2:17"))
            .add(new OutcomeMapping("1116", "816", "2:18"))
            .add(new OutcomeMapping("1117", "818", "2:19"))
            .add(new OutcomeMapping("1118", "820", "3:0"))
            .add(new OutcomeMapping("1119", "822", "3:1"))
            .add(new OutcomeMapping("1120", "824", "3:2"))
            .add(new OutcomeMapping("1121", "826", "3:3"))
            .add(new OutcomeMapping("1122", "828", "3:4"))
            .add(new OutcomeMapping("1123", "830", "3:5"))
            .add(new OutcomeMapping("1124", "832", "3:6"))
            .add(new OutcomeMapping("1125", "834", "3:7"))
            .add(new OutcomeMapping("1126", "836", "3:8"))
            .add(new OutcomeMapping("1127", "838", "3:9"))
            .add(new OutcomeMapping("1128", "840", "3:10"))
            .add(new OutcomeMapping("1129", "842", "3:11"))
            .add(new OutcomeMapping("1130", "844", "3:12"))
            .add(new OutcomeMapping("1131", "846", "3:13"))
            .add(new OutcomeMapping("1132", "848", "3:14"))
            .add(new OutcomeMapping("1133", "850", "3:15"))
            .add(new OutcomeMapping("1134", "852", "3:16"))
            .add(new OutcomeMapping("1135", "854", "3:17"))
            .add(new OutcomeMapping("1136", "856", "3:18"))
            .add(new OutcomeMapping("1137", "858", "3:19"))
            .add(new OutcomeMapping("1138", "860", "4:0"))
            .add(new OutcomeMapping("1139", "862", "4:1"))
            .add(new OutcomeMapping("1140", "864", "4:2"))
            .add(new OutcomeMapping("1141", "866", "4:3"))
            .add(new OutcomeMapping("1142", "868", "4:4"))
            .add(new OutcomeMapping("1143", "870", "4:5"))
            .add(new OutcomeMapping("1144", "872", "4:6"))
            .add(new OutcomeMapping("1145", "874", "4:7"))
            .add(new OutcomeMapping("1146", "876", "4:8"))
            .add(new OutcomeMapping("1147", "878", "4:9"))
            .add(new OutcomeMapping("1148", "880", "4:10"))
            .add(new OutcomeMapping("1149", "882", "4:11"))
            .add(new OutcomeMapping("1150", "884", "4:12"))
            .add(new OutcomeMapping("1151", "886", "4:13"))
            .add(new OutcomeMapping("1152", "888", "4:14"))
            .add(new OutcomeMapping("1153", "890", "4:15"))
            .add(new OutcomeMapping("1154", "892", "4:16"))
            .add(new OutcomeMapping("1155", "894", "4:17"))
            .add(new OutcomeMapping("1156", "896", "4:18"))
            .add(new OutcomeMapping("1157", "898", "4:19"))
            .add(new OutcomeMapping("1158", "900", "5:0"))
            .add(new OutcomeMapping("1159", "902", "5:1"))
            .add(new OutcomeMapping("1160", "904", "5:2"))
            .add(new OutcomeMapping("1161", "906", "5:3"))
            .add(new OutcomeMapping("1162", "908", "5:4"))
            .add(new OutcomeMapping("1163", "910", "5:5"))
            .add(new OutcomeMapping("1164", "912", "5:6"))
            .add(new OutcomeMapping("1165", "914", "5:7"))
            .add(new OutcomeMapping("1166", "916", "5:8"))
            .add(new OutcomeMapping("1167", "918", "5:9"))
            .add(new OutcomeMapping("1168", "920", "5:10"))
            .add(new OutcomeMapping("1169", "922", "5:11"))
            .add(new OutcomeMapping("1170", "924", "5:12"))
            .add(new OutcomeMapping("1171", "926", "5:13"))
            .add(new OutcomeMapping("1172", "928", "5:14"))
            .add(new OutcomeMapping("1173", "930", "5:15"))
            .add(new OutcomeMapping("1174", "932", "5:16"))
            .add(new OutcomeMapping("1175", "934", "5:17"))
            .add(new OutcomeMapping("1176", "936", "5:18"))
            .add(new OutcomeMapping("1177", "938", "5:19"))
            .add(new OutcomeMapping("1178", "940", "6:0"))
            .add(new OutcomeMapping("1179", "942", "6:1"))
            .add(new OutcomeMapping("1180", "944", "6:2"))
            .add(new OutcomeMapping("1181", "946", "6:3"))
            .add(new OutcomeMapping("1182", "948", "6:4"))
            .add(new OutcomeMapping("1183", "950", "6:5"))
            .add(new OutcomeMapping("1184", "952", "6:6"))
            .add(new OutcomeMapping("1185", "954", "6:7"))
            .add(new OutcomeMapping("1186", "956", "6:8"))
            .add(new OutcomeMapping("1187", "958", "6:9"))
            .add(new OutcomeMapping("1188", "960", "6:10"))
            .add(new OutcomeMapping("1189", "962", "6:11"))
            .add(new OutcomeMapping("1190", "964", "6:12"))
            .add(new OutcomeMapping("1191", "966", "6:13"))
            .add(new OutcomeMapping("1192", "968", "6:14"))
            .add(new OutcomeMapping("1193", "970", "6:15"))
            .add(new OutcomeMapping("1194", "972", "6:16"))
            .add(new OutcomeMapping("1195", "974", "6:17"))
            .add(new OutcomeMapping("1196", "976", "6:18"))
            .add(new OutcomeMapping("1197", "978", "6:19"))
            .add(new OutcomeMapping("1198", "980", "7:0"))
            .add(new OutcomeMapping("1199", "982", "7:1"))
            .add(new OutcomeMapping("1200", "984", "7:2"))
            .add(new OutcomeMapping("1201", "986", "7:3"))
            .add(new OutcomeMapping("1202", "988", "7:4"))
            .add(new OutcomeMapping("1203", "990", "7:5"))
            .add(new OutcomeMapping("1204", "992", "7:6"))
            .add(new OutcomeMapping("1205", "994", "7:7"))
            .add(new OutcomeMapping("1206", "996", "7:8"))
            .add(new OutcomeMapping("1207", "998", "7:9"))
            .add(new OutcomeMapping("1208", "1000", "7:10"))
            .add(new OutcomeMapping("1209", "1002", "7:11"))
            .add(new OutcomeMapping("1210", "1004", "7:12"))
            .add(new OutcomeMapping("1211", "1006", "7:13"))
            .add(new OutcomeMapping("1212", "1008", "7:14"))
            .add(new OutcomeMapping("1213", "1010", "7:15"))
            .add(new OutcomeMapping("1214", "1012", "7:16"))
            .add(new OutcomeMapping("1215", "1014", "7:17"))
            .add(new OutcomeMapping("1216", "1016", "7:18"))
            .add(new OutcomeMapping("1217", "1018", "7:19"))
            .add(new OutcomeMapping("1218", "1020", "8:0"))
            .add(new OutcomeMapping("1219", "1022", "8:1"))
            .add(new OutcomeMapping("1220", "1024", "8:2"))
            .add(new OutcomeMapping("1221", "1026", "8:3"))
            .add(new OutcomeMapping("1222", "1028", "8:4"))
            .add(new OutcomeMapping("1223", "1030", "8:5"))
            .add(new OutcomeMapping("1224", "1032", "8:6"))
            .add(new OutcomeMapping("1225", "1034", "8:7"))
            .add(new OutcomeMapping("1226", "1036", "8:8"))
            .add(new OutcomeMapping("1227", "1038", "8:9"))
            .add(new OutcomeMapping("1228", "1040", "8:10"))
            .add(new OutcomeMapping("1229", "1042", "8:11"))
            .add(new OutcomeMapping("1230", "1044", "8:12"))
            .add(new OutcomeMapping("1231", "1046", "8:13"))
            .add(new OutcomeMapping("1232", "1048", "8:14"))
            .add(new OutcomeMapping("1233", "1050", "8:15"))
            .add(new OutcomeMapping("1234", "1052", "8:16"))
            .add(new OutcomeMapping("1235", "1054", "8:17"))
            .add(new OutcomeMapping("1236", "1056", "8:18"))
            .add(new OutcomeMapping("1237", "1058", "8:19"))
            .add(new OutcomeMapping("1238", "1060", "9:0"))
            .add(new OutcomeMapping("1239", "1062", "9:1"))
            .add(new OutcomeMapping("1240", "1064", "9:2"))
            .add(new OutcomeMapping("1241", "1066", "9:3"))
            .add(new OutcomeMapping("1242", "1068", "9:4"))
            .add(new OutcomeMapping("1243", "1070", "9:5"))
            .add(new OutcomeMapping("1244", "1072", "9:6"))
            .add(new OutcomeMapping("1245", "1074", "9:7"))
            .add(new OutcomeMapping("1246", "1076", "9:8"))
            .add(new OutcomeMapping("1247", "1078", "9:9"))
            .add(new OutcomeMapping("1248", "1080", "9:10"))
            .add(new OutcomeMapping("1249", "1082", "9:11"))
            .add(new OutcomeMapping("1250", "1084", "9:12"))
            .add(new OutcomeMapping("1251", "1086", "9:13"))
            .add(new OutcomeMapping("1252", "1088", "9:14"))
            .add(new OutcomeMapping("1253", "1090", "9:15"))
            .add(new OutcomeMapping("1254", "1092", "9:16"))
            .add(new OutcomeMapping("1255", "1094", "9:17"))
            .add(new OutcomeMapping("1256", "1096", "9:18"))
            .add(new OutcomeMapping("1257", "1098", "9:19"))
            .add(new OutcomeMapping("1258", "1100", "10:0"))
            .add(new OutcomeMapping("1259", "1102", "10:1"))
            .add(new OutcomeMapping("1260", "1104", "10:2"))
            .add(new OutcomeMapping("1261", "1106", "10:3"))
            .add(new OutcomeMapping("1262", "1108", "10:4"))
            .add(new OutcomeMapping("1263", "1110", "10:5"))
            .add(new OutcomeMapping("1264", "1112", "10:6"))
            .add(new OutcomeMapping("1265", "1114", "10:7"))
            .add(new OutcomeMapping("1266", "1116", "10:8"))
            .add(new OutcomeMapping("1267", "1118", "10:9"))
            .add(new OutcomeMapping("1268", "1120", "10:10"))
            .add(new OutcomeMapping("1269", "1122", "10:11"))
            .add(new OutcomeMapping("1270", "1124", "10:12"))
            .add(new OutcomeMapping("1271", "1126", "10:13"))
            .add(new OutcomeMapping("1272", "1128", "10:14"))
            .add(new OutcomeMapping("1273", "1130", "10:15"))
            .add(new OutcomeMapping("1274", "1132", "10:16"))
            .add(new OutcomeMapping("1275", "1134", "10:17"))
            .add(new OutcomeMapping("1276", "1136", "10:18"))
            .add(new OutcomeMapping("1277", "1138", "10:19"))
            .add(new OutcomeMapping("1278", "1140", "11:0"))
            .add(new OutcomeMapping("1279", "1142", "11:1"))
            .add(new OutcomeMapping("1280", "1144", "11:2"))
            .add(new OutcomeMapping("1281", "1146", "11:3"))
            .add(new OutcomeMapping("1282", "1148", "11:4"))
            .add(new OutcomeMapping("1283", "1150", "11:5"))
            .add(new OutcomeMapping("1284", "1152", "11:6"))
            .add(new OutcomeMapping("1285", "1154", "11:7"))
            .add(new OutcomeMapping("1286", "1156", "11:8"))
            .add(new OutcomeMapping("1287", "1158", "11:9"))
            .add(new OutcomeMapping("1288", "1160", "11:10"))
            .add(new OutcomeMapping("1289", "1162", "11:11"))
            .add(new OutcomeMapping("1290", "1164", "11:12"))
            .add(new OutcomeMapping("1291", "1166", "11:13"))
            .add(new OutcomeMapping("1292", "1168", "11:14"))
            .add(new OutcomeMapping("1293", "1170", "11:15"))
            .add(new OutcomeMapping("1294", "1172", "11:16"))
            .add(new OutcomeMapping("1295", "1174", "11:17"))
            .add(new OutcomeMapping("1296", "1176", "11:18"))
            .add(new OutcomeMapping("1297", "1178", "11:19"))
            .add(new OutcomeMapping("1298", "1180", "12:0"))
            .add(new OutcomeMapping("1299", "1182", "12:1"))
            .add(new OutcomeMapping("1300", "1184", "12:2"))
            .add(new OutcomeMapping("1301", "1186", "12:3"))
            .add(new OutcomeMapping("1302", "1188", "12:4"))
            .add(new OutcomeMapping("1303", "1190", "12:5"))
            .add(new OutcomeMapping("1304", "1192", "12:6"))
            .add(new OutcomeMapping("1305", "1194", "12:7"))
            .add(new OutcomeMapping("1306", "1196", "12:8"))
            .add(new OutcomeMapping("1307", "1198", "12:9"))
            .add(new OutcomeMapping("1308", "1200", "12:10"))
            .add(new OutcomeMapping("1309", "1202", "12:11"))
            .add(new OutcomeMapping("1310", "1204", "12:12"))
            .add(new OutcomeMapping("1311", "1206", "12:13"))
            .add(new OutcomeMapping("1312", "1208", "12:14"))
            .add(new OutcomeMapping("1313", "1210", "12:15"))
            .add(new OutcomeMapping("1314", "1212", "12:16"))
            .add(new OutcomeMapping("1315", "1214", "12:17"))
            .add(new OutcomeMapping("1316", "1216", "12:18"))
            .add(new OutcomeMapping("1317", "1218", "12:19"))
            .add(new OutcomeMapping("1318", "1220", "13:0"))
            .add(new OutcomeMapping("1319", "1222", "13:1"))
            .add(new OutcomeMapping("1320", "1224", "13:2"))
            .add(new OutcomeMapping("1321", "1226", "13:3"))
            .add(new OutcomeMapping("1322", "1228", "13:4"))
            .add(new OutcomeMapping("1323", "1230", "13:5"))
            .add(new OutcomeMapping("1324", "1232", "13:6"))
            .add(new OutcomeMapping("1325", "1234", "13:7"))
            .add(new OutcomeMapping("1326", "1236", "13:8"))
            .add(new OutcomeMapping("1327", "1238", "13:9"))
            .add(new OutcomeMapping("1328", "1240", "13:10"))
            .add(new OutcomeMapping("1329", "1242", "13:11"))
            .add(new OutcomeMapping("1330", "1244", "13:12"))
            .add(new OutcomeMapping("1331", "1246", "13:13"))
            .add(new OutcomeMapping("1332", "1248", "13:14"))
            .add(new OutcomeMapping("1333", "1250", "13:15"))
            .add(new OutcomeMapping("1334", "1252", "13:16"))
            .add(new OutcomeMapping("1335", "1254", "13:17"))
            .add(new OutcomeMapping("1336", "1256", "13:18"))
            .add(new OutcomeMapping("1337", "1258", "13:19"))
            .add(new OutcomeMapping("1338", "1260", "14:0"))
            .add(new OutcomeMapping("1339", "1262", "14:1"))
            .add(new OutcomeMapping("1340", "1264", "14:2"))
            .add(new OutcomeMapping("1341", "1266", "14:3"))
            .add(new OutcomeMapping("1342", "1268", "14:4"))
            .add(new OutcomeMapping("1343", "1270", "14:5"))
            .add(new OutcomeMapping("1344", "1272", "14:6"))
            .add(new OutcomeMapping("1345", "1274", "14:7"))
            .add(new OutcomeMapping("1346", "1276", "14:8"))
            .add(new OutcomeMapping("1347", "1278", "14:9"))
            .add(new OutcomeMapping("1348", "1280", "14:10"))
            .add(new OutcomeMapping("1349", "1282", "14:11"))
            .add(new OutcomeMapping("1350", "1284", "14:12"))
            .add(new OutcomeMapping("1351", "1286", "14:13"))
            .add(new OutcomeMapping("1352", "1288", "14:14"))
            .add(new OutcomeMapping("1353", "1290", "14:15"))
            .add(new OutcomeMapping("1354", "1292", "14:16"))
            .add(new OutcomeMapping("1355", "1294", "14:17"))
            .add(new OutcomeMapping("1356", "1296", "14:18"))
            .add(new OutcomeMapping("1357", "1298", "14:19"))
            .add(new OutcomeMapping("1358", "1300", "15:0"))
            .add(new OutcomeMapping("1359", "1302", "15:1"))
            .add(new OutcomeMapping("1360", "1304", "15:2"))
            .add(new OutcomeMapping("1361", "1306", "15:3"))
            .add(new OutcomeMapping("1362", "1308", "15:4"))
            .add(new OutcomeMapping("1363", "1310", "15:5"))
            .add(new OutcomeMapping("1364", "1312", "15:6"))
            .add(new OutcomeMapping("1365", "1314", "15:7"))
            .add(new OutcomeMapping("1366", "1316", "15:8"))
            .add(new OutcomeMapping("1367", "1318", "15:9"))
            .add(new OutcomeMapping("1368", "1320", "15:10"))
            .add(new OutcomeMapping("1369", "1322", "15:11"))
            .add(new OutcomeMapping("1370", "1324", "15:12"))
            .add(new OutcomeMapping("1371", "1326", "15:13"))
            .add(new OutcomeMapping("1372", "1328", "15:14"))
            .add(new OutcomeMapping("1373", "1330", "15:15"))
            .add(new OutcomeMapping("1374", "1332", "15:16"))
            .add(new OutcomeMapping("1375", "1334", "15:17"))
            .add(new OutcomeMapping("1376", "1336", "15:18"))
            .add(new OutcomeMapping("1377", "1338", "15:19"))
            .add(new OutcomeMapping("1378", "1340", "16:0"))
            .add(new OutcomeMapping("1379", "1342", "16:1"))
            .add(new OutcomeMapping("1380", "1344", "16:2"))
            .add(new OutcomeMapping("1381", "1346", "16:3"))
            .add(new OutcomeMapping("1382", "1348", "16:4"))
            .add(new OutcomeMapping("1383", "1350", "16:5"))
            .add(new OutcomeMapping("1384", "1352", "16:6"))
            .add(new OutcomeMapping("1385", "1354", "16:7"))
            .add(new OutcomeMapping("1386", "1356", "16:8"))
            .add(new OutcomeMapping("1387", "1358", "16:9"))
            .add(new OutcomeMapping("1388", "1360", "16:10"))
            .add(new OutcomeMapping("1389", "1362", "16:11"))
            .add(new OutcomeMapping("1390", "1364", "16:12"))
            .add(new OutcomeMapping("1391", "1366", "16:13"))
            .add(new OutcomeMapping("1392", "1368", "16:14"))
            .add(new OutcomeMapping("1393", "1370", "16:15"))
            .add(new OutcomeMapping("1394", "1372", "16:16"))
            .add(new OutcomeMapping("1395", "1374", "16:17"))
            .add(new OutcomeMapping("1396", "1376", "16:18"))
            .add(new OutcomeMapping("1397", "1378", "16:19"))
            .add(new OutcomeMapping("1398", "1380", "17:0"))
            .add(new OutcomeMapping("1399", "1382", "17:1"))
            .add(new OutcomeMapping("1400", "1384", "17:2"))
            .add(new OutcomeMapping("1401", "1386", "17:3"))
            .add(new OutcomeMapping("1402", "1388", "17:4"))
            .add(new OutcomeMapping("1403", "1390", "17:5"))
            .add(new OutcomeMapping("1404", "1392", "17:6"))
            .add(new OutcomeMapping("1405", "1394", "17:7"))
            .add(new OutcomeMapping("1406", "1396", "17:8"))
            .add(new OutcomeMapping("1407", "1398", "17:9"))
            .add(new OutcomeMapping("1408", "1400", "17:10"))
            .add(new OutcomeMapping("1409", "1402", "17:11"))
            .add(new OutcomeMapping("1410", "1404", "17:12"))
            .add(new OutcomeMapping("1411", "1406", "17:13"))
            .add(new OutcomeMapping("1412", "1408", "17:14"))
            .add(new OutcomeMapping("1413", "1410", "17:15"))
            .add(new OutcomeMapping("1414", "1412", "17:16"))
            .add(new OutcomeMapping("1415", "1414", "17:17"))
            .add(new OutcomeMapping("1416", "1416", "17:18"))
            .add(new OutcomeMapping("1417", "1418", "17:19"))
            .add(new OutcomeMapping("1418", "1420", "18:0"))
            .add(new OutcomeMapping("1419", "1422", "18:1"))
            .add(new OutcomeMapping("1420", "1424", "18:2"))
            .add(new OutcomeMapping("1421", "1426", "18:3"))
            .add(new OutcomeMapping("1422", "1428", "18:4"))
            .add(new OutcomeMapping("1423", "1430", "18:5"))
            .add(new OutcomeMapping("1424", "1432", "18:6"))
            .add(new OutcomeMapping("1425", "1434", "18:7"))
            .add(new OutcomeMapping("1426", "1436", "18:8"))
            .add(new OutcomeMapping("1427", "1438", "18:9"))
            .add(new OutcomeMapping("1428", "1440", "18:10"))
            .add(new OutcomeMapping("1429", "1442", "18:11"))
            .add(new OutcomeMapping("1430", "1444", "18:12"))
            .add(new OutcomeMapping("1431", "1446", "18:13"))
            .add(new OutcomeMapping("1432", "1448", "18:14"))
            .add(new OutcomeMapping("1433", "1450", "18:15"))
            .add(new OutcomeMapping("1434", "1452", "18:16"))
            .add(new OutcomeMapping("1435", "1454", "18:17"))
            .add(new OutcomeMapping("1436", "1456", "18:18"))
            .add(new OutcomeMapping("1437", "1458", "18:19"))
            .add(new OutcomeMapping("1438", "1460", "19:0"))
            .add(new OutcomeMapping("1439", "1462", "19:1"))
            .add(new OutcomeMapping("1440", "1464", "19:2"))
            .add(new OutcomeMapping("1441", "1466", "19:3"))
            .add(new OutcomeMapping("1442", "1468", "19:4"))
            .add(new OutcomeMapping("1443", "1470", "19:5"))
            .add(new OutcomeMapping("1444", "1472", "19:6"))
            .add(new OutcomeMapping("1445", "1474", "19:7"))
            .add(new OutcomeMapping("1446", "1476", "19:8"))
            .add(new OutcomeMapping("1447", "1478", "19:9"))
            .add(new OutcomeMapping("1448", "1480", "19:10"))
            .add(new OutcomeMapping("1449", "1482", "19:11"))
            .add(new OutcomeMapping("1450", "1484", "19:12"))
            .add(new OutcomeMapping("1451", "1486", "19:13"))
            .add(new OutcomeMapping("1452", "1488", "19:14"))
            .add(new OutcomeMapping("1453", "1490", "19:15"))
            .add(new OutcomeMapping("1454", "1492", "19:16"))
            .add(new OutcomeMapping("1455", "1494", "19:17"))
            .add(new OutcomeMapping("1456", "1496", "19:18"))
            .add(new OutcomeMapping("1457", "1498", "19:19"))
            .build()
            .stream()
            .collect(toMap(OutcomeMapping::getOutcomeId, om -> om));

        // </editor-fold>

        public static DescMarket correctScoreFlexMarketDescription(Locale language) {
            return correctScoreFlexMarketDescription(MarketTranslation.getFor(language));
        }

        private static DescMarket correctScoreFlexMarketDescription(
            CorrectScoreFlex.MarketTranslation translation
        ) {
            DescMarket market = new DescMarket();
            market.setId(CORRECT_SCORE_FLEX_SCORE_MARKET_ID);
            market.setName(translation.marketName);
            market.setGroups("all|score|regular_play");
            market.setSpecifiers(scoreSpecifier());
            market.setAttributes(flexScoreAttributes());
            populateOutcomes(from(FlexScoreMarketIds.correctScoreFlexScoreMarket()), to(market));
            market.setMappings(mappings());
            return market;
        }

        private static DescSpecifiers scoreSpecifier() {
            DescSpecifiers.Specifier specifier = new DescSpecifiers.Specifier();
            specifier.setName("score");
            specifier.setType("string");
            specifier.setDescription("current score in match");
            DescSpecifiers descSpecifiers = new DescSpecifiers();
            descSpecifiers.getSpecifier().add(specifier);
            return descSpecifiers;
        }

        private static Attributes flexScoreAttributes() {
            Attributes attributes = new Attributes();
            Attributes.Attribute flexScoreAttribute = new Attributes.Attribute();
            flexScoreAttribute.setName("is_flex_score");
            flexScoreAttribute.setDescription("Outcomes should be adjusted according to score specifier");
            attributes.getAttribute().add(flexScoreAttribute);
            return attributes;
        }

        private static Mappings mappings() {
            val mappings = new Mappings();
            populateOutcomeMappings(from(FlexScoreMarketIds.correctScoreFlexScoreMarket()), to(mappings));
            return mappings;
        }

        private static void populateOutcomeMappings(MarketVariant market, Mappings mappings) {
            market
                .mappings()
                .forEach((mapping, outcomes) -> {
                    List<Mappings.Mapping.MappingOutcome> mappingOutcomes = outcomes
                        .stream()
                        .map(o ->
                            MarketTranslation.EN.outcomeMappingTranslations.get(o).toSapiOutcomeMapping()
                        )
                        .collect(Collectors.toList());
                    Mappings.Mapping m = mapping.toSapiMapping(mappingOutcomes);
                    mappings.getMapping().add(m);
                });
        }

        private static void populateOutcomes(MarketVariant fromVariant, DescMarket toMarket) {
            val outcomes = new DescOutcomes();
            populateOutcomes(fromVariant, to(outcomes));
            toMarket.setOutcomes(outcomes);
        }

        private static void populateOutcomes(MarketVariant fromVariant, DescOutcomes toOutcomes) {
            fromVariant
                .outcomeIds()
                .forEach(id ->
                    toOutcomes
                        .getOutcome()
                        .add(outcome(id, CorrectScoreFlex.MarketTranslation.EN.outcomeTranslations.get(id)))
                );
        }

        private static DescOutcomes.Outcome outcome(String id, String name) {
            val outcome = new DescOutcomes.Outcome();
            outcome.setId(id);
            outcome.setName(name);
            return outcome;
        }

        @Getter
        @RequiredArgsConstructor
        private enum MarketTranslation {
            EN(
                Locale.ENGLISH,
                "Correct score [{score}]",
                ENGLISH_TRANSLATIONS,
                OUTCOME_MAPPING_ENGLISH_TRANSLATIONS
            );

            private final Locale language;
            private final String marketName;
            private final Map<String, String> outcomeTranslations;
            private final Map<String, OutcomeMapping> outcomeMappingTranslations;

            public static CorrectScoreFlex.MarketTranslation getFor(Locale language) {
                return stream(CorrectScoreFlex.MarketTranslation.values())
                    .filter(translation -> translation.language.equals(language))
                    .findFirst()
                    .orElseThrow(() -> new RuntimeException(LANGUAGE_NOT_SUPPORTED_BY_TEST_FIXTURE));
            }
        }
    }

    public static class MapDuration {

        public static DescMarket mapDurationMarketDescription(Locale locale) {
            return mapDurationMarketDescription(MarketTranslation.getFor(locale));
        }

        private static DescMarket mapDurationMarketDescription(MarketTranslation translation) {
            DescMarket market = new DescMarket();
            market.setId(MapDurationMarketIds.MAP_DURATION_MARKET_ID);
            market.setName(translation.marketName);
            market.setGroups("all|map|regular_play");
            market.setSpecifiers(specifiers());
            market.setOutcomes(outcomes(translation));
            return market;
        }

        private static DescOutcomes outcomes(MarketTranslation translation) {
            DescOutcomes descOutcomes = new DescOutcomes();
            DescOutcomes.Outcome firstMinutesOutcome = firstMapMinutesOutcome(translation);
            DescOutcomes.Outcome restMinutesOutcome = restMapMinutesOutcome(translation);
            descOutcomes.getOutcome().add(firstMinutesOutcome);
            descOutcomes.getOutcome().add(restMinutesOutcome);
            return descOutcomes;
        }

        private static DescOutcomes.Outcome firstMapMinutesOutcome(MarketTranslation translation) {
            DescOutcomes.Outcome firstMinutesOutcome = new DescOutcomes.Outcome();
            firstMinutesOutcome.setId(MapDurationMarketIds.FIRST_MINUTES_OUTCOME_ID);
            firstMinutesOutcome.setName(translation.firstMinutesOutcomeName);
            return firstMinutesOutcome;
        }

        private static DescOutcomes.Outcome restMapMinutesOutcome(MarketTranslation translation) {
            DescOutcomes.Outcome restMinutesOutcome = new DescOutcomes.Outcome();
            restMinutesOutcome.setId(MapDurationMarketIds.REST_MINUTES_OUTCOME_UD);
            restMinutesOutcome.setName(translation.restMinutesOutcomeName);
            return restMinutesOutcome;
        }

        private static DescSpecifiers specifiers() {
            DescSpecifiers descSpecifiers = new DescSpecifiers();
            DescSpecifiers.Specifier mapNrSpecifier = new DescSpecifiers.Specifier();
            mapNrSpecifier.setName("mapnr");
            mapNrSpecifier.setType("integer");
            descSpecifiers.getSpecifier().add(mapNrSpecifier);
            DescSpecifiers.Specifier minuteSpecifier = new DescSpecifiers.Specifier();
            minuteSpecifier.setName("minute");
            minuteSpecifier.setType("integer");
            descSpecifiers.getSpecifier().add(minuteSpecifier);
            return descSpecifiers;
        }

        @RequiredArgsConstructor
        @Getter
        private enum MarketTranslation {
            EN(Locale.ENGLISH, "{!mapnr} map - duration", "00:00 - {(minute-1)}:59", "{minute}:00+}"),
            FR(Locale.FRENCH, "{!mapnr} map - duré", "00:00 - {(minute-1)}:59", "{minute}:00+}");

            private final Locale language;
            private final String marketName;
            private final String firstMinutesOutcomeName;
            private final String restMinutesOutcomeName;

            public static MarketTranslation getFor(Locale language) {
                return stream(MarketTranslation.values())
                    .filter(translation -> translation.language.equals(language))
                    .findFirst()
                    .orElseThrow(() -> new RuntimeException(LANGUAGE_NOT_SUPPORTED_BY_TEST_FIXTURE));
            }
        }
    }

    public static class WhenWillTheRunBeScoredExtraInnings {

        public static DescMarket whenWillTheRunBeScoredExtraInningsMarketDescription(Locale locale) {
            return whenWillTheRunBeScoredExtraInningsMarketDescription(MarketTranslation.getFor(locale));
        }

        private static DescMarket whenWillTheRunBeScoredExtraInningsMarketDescription(
            MarketTranslation translation
        ) {
            DescMarket market = new DescMarket();
            market.setId(
                WhenWillTheRunBeScoredExtraInningsMarketIds.WHEN_WILL_THE_RUN_BE_SCORED_EXTRA_INNINGS_MARKET_ID
            );
            market.setName(translation.marketName);
            market.setGroups("all|score|incl_ei");
            market.setSpecifiers(specifiers());
            market.setOutcomes(outcomes(translation));
            return market;
        }

        private static DescSpecifiers specifiers() {
            DescSpecifiers descSpecifiers = new DescSpecifiers();
            DescSpecifiers.Specifier inningNrSpecifier = new DescSpecifiers.Specifier();
            inningNrSpecifier.setName("inningnr");
            inningNrSpecifier.setType("integer");
            descSpecifiers.getSpecifier().add(inningNrSpecifier);
            DescSpecifiers.Specifier runNrSpecifier = new DescSpecifiers.Specifier();
            runNrSpecifier.setName("runnr");
            runNrSpecifier.setType("integer");
            descSpecifiers.getSpecifier().add(runNrSpecifier);
            return descSpecifiers;
        }

        private static DescOutcomes outcomes(MarketTranslation translation) {
            DescOutcomes descOutcomes = new DescOutcomes();
            descOutcomes.getOutcome().add(currentInningOutcome(translation));
            descOutcomes.getOutcome().add(oneInningIntoTheFutureOutcome(translation));
            descOutcomes.getOutcome().add(twoInningsIntoTheFutureOutcome(translation));
            return descOutcomes;
        }

        private static DescOutcomes.Outcome currentInningOutcome(MarketTranslation translation) {
            DescOutcomes.Outcome currentInningOutcome = new DescOutcomes.Outcome();
            currentInningOutcome.setId(WhenWillTheRunBeScoredExtraInningsMarketIds.CURRENT_INNING_OUTCOME_ID);
            currentInningOutcome.setName(translation.currentInning);
            return currentInningOutcome;
        }

        private static DescOutcomes.Outcome oneInningIntoTheFutureOutcome(MarketTranslation translation) {
            DescOutcomes.Outcome oneInningIntoTheFutureOutcome = new DescOutcomes.Outcome();
            oneInningIntoTheFutureOutcome.setId(
                WhenWillTheRunBeScoredExtraInningsMarketIds.ONE_INNING_INTO_THE_FUTURE_OUTCOME_ID
            );
            oneInningIntoTheFutureOutcome.setName(translation.oneInningIntoTheFuture);
            return oneInningIntoTheFutureOutcome;
        }

        private static DescOutcomes.Outcome twoInningsIntoTheFutureOutcome(MarketTranslation translation) {
            DescOutcomes.Outcome twoInningsIntoTheFutureOutcome = new DescOutcomes.Outcome();
            twoInningsIntoTheFutureOutcome.setId(
                WhenWillTheRunBeScoredExtraInningsMarketIds.TWO_INNINGS_INTO_THE_FUTURE_OUTCOME_ID
            );
            twoInningsIntoTheFutureOutcome.setName(translation.twoInningsIntoTheFuture);
            return twoInningsIntoTheFutureOutcome;
        }

        @RequiredArgsConstructor
        @Getter
        private enum MarketTranslation {
            EN(
                Locale.ENGLISH,
                "When will the {!runnr} run be scored (incl. extra innings)",
                "{!inningnr} inning",
                "{!(inningnr+1)} inning",
                "{!(inningnr+2)} inning",
                "other inning or no run"
            ),
            FR(
                Locale.FRENCH,
                "Quand sera marqué le {!runnr} run (inclus manches supplémentaires)",
                "{!inningnr} manche",
                "{!(inningnr+1)} manche",
                "{!(inningnr+2)} manche",
                "autre manche ou aucun run"
            );

            private final Locale language;
            private final String marketName;
            private final String currentInning;
            private final String oneInningIntoTheFuture;
            private final String twoInningsIntoTheFuture;
            private final String otherInning;

            public static MarketTranslation getFor(Locale language) {
                return stream(MarketTranslation.values())
                    .filter(translation -> translation.language.equals(language))
                    .findFirst()
                    .orElseThrow(() -> new RuntimeException(LANGUAGE_NOT_SUPPORTED_BY_TEST_FIXTURE));
            }
        }
    }

    public static class SetNrBreakNr {

        public static DescMarket setNrBreakNrMarketDescription(Locale locale) {
            return setNrBreakNrMarketDescription(SetNrBreakNr.MarketTranslation.getFor(locale));
        }

        private static DescMarket setNrBreakNrMarketDescription(SetNrBreakNr.MarketTranslation translation) {
            DescMarket market = new DescMarket();
            market.setId(SetNrBreakNrMarketIds.SET_NR_BREAK_NR_MARKET_ID);
            market.setName(translation.marketName);
            market.setGroups("all|map|regular_play");
            market.setSpecifiers(specifiers());
            market.setOutcomes(outcomes(translation));
            return market;
        }

        private static DescOutcomes outcomes(MarketTranslation translation) {
            DescOutcomes.Outcome competitor1Outcome = new DescOutcomes.Outcome();
            competitor1Outcome.setId("6");
            competitor1Outcome.setName("{$competitor1}");
            DescOutcomes.Outcome noneOutcome = new DescOutcomes.Outcome();
            noneOutcome.setId("7");
            noneOutcome.setName(translation.noneOutcome);
            DescOutcomes.Outcome competitor2Outcome = new DescOutcomes.Outcome();
            competitor2Outcome.setId("8");
            DescOutcomes outcomes = new DescOutcomes();
            outcomes.getOutcome().add(competitor1Outcome);
            outcomes.getOutcome().add(noneOutcome);
            outcomes.getOutcome().add(competitor2Outcome);
            return outcomes;
        }

        private static DescSpecifiers specifiers() {
            DescSpecifiers descSpecifiers = new DescSpecifiers();
            DescSpecifiers.Specifier setNrSpecifier = new DescSpecifiers.Specifier();
            setNrSpecifier.setName("setnr");
            setNrSpecifier.setType("integer");
            descSpecifiers.getSpecifier().add(setNrSpecifier);
            DescSpecifiers.Specifier breakNrSpecifier = new DescSpecifiers.Specifier();
            breakNrSpecifier.setName("breaknr");
            breakNrSpecifier.setType("integer");
            descSpecifiers.getSpecifier().add(breakNrSpecifier);
            return descSpecifiers;
        }

        @RequiredArgsConstructor
        @Getter
        private enum MarketTranslation {
            EN(Locale.ENGLISH, "{!setnr} set - {!breaknr} break", "none"),
            FR(Locale.FRENCH, "{!setnr} set - {!breaknr} break", "aucun");

            private final Locale language;
            private final String marketName;
            private final String noneOutcome;

            public static SetNrBreakNr.MarketTranslation getFor(Locale language) {
                return stream(SetNrBreakNr.MarketTranslation.values())
                    .filter(translation -> translation.language.equals(language))
                    .findFirst()
                    .orElseThrow(() -> new RuntimeException(LANGUAGE_NOT_SUPPORTED_BY_TEST_FIXTURE));
            }
        }
    }

    public static class Handicap {

        public static DescMarket handicapMarketDescription() {
            DescMarket market = new DescMarket();
            market.setId(HandicapMarketIds.HANDICAP_MARKET_MARKET_ID);
            market.setName("Handicap");
            market.setGroups("all|score|regular_play");
            market.setSpecifiers(specifiers());
            market.setOutcomes(outcomes());
            return market;
        }

        private static DescSpecifiers specifiers() {
            DescSpecifiers descSpecifiers = new DescSpecifiers();
            DescSpecifiers.Specifier hcpSpecifier = new DescSpecifiers.Specifier();
            hcpSpecifier.setName("hcp");
            hcpSpecifier.setType("decimal");
            descSpecifiers.getSpecifier().add(hcpSpecifier);
            return descSpecifiers;
        }

        private static DescOutcomes outcomes() {
            DescOutcomes.Outcome competitor1Outcome = new DescOutcomes.Outcome();
            competitor1Outcome.setId(HandicapMarketIds.COMPETITOR1_PLUS_HANDICAP_OUTCOME_ID);
            competitor1Outcome.setName("{$competitor1} ({+hcp})");
            DescOutcomes.Outcome competitor2Outcome = new DescOutcomes.Outcome();
            competitor2Outcome.setId(HandicapMarketIds.COMPETITOR2_MINUS_HANDICAP_OUTCOME_ID);
            competitor2Outcome.setName("{$competitor2} ({-hcp})");
            DescOutcomes descOutcomes = new DescOutcomes();
            descOutcomes.getOutcome().add(competitor1Outcome);
            descOutcomes.getOutcome().add(competitor2Outcome);
            return descOutcomes;
        }
    }

    public static class WinnerCompetitor {

        public static DescMarket winnerCompetitorMarketDescription() {
            DescMarket market = new DescMarket();
            market.setId(WinnerCompetitorMarketIds.WINNER_COMPETITOR_MARKET_ID);
            market.setName("Winner");
            market.setGroups("all");
            market.setIncludesOutcomesOfType("sr:competitor");
            market.setOutcomeType("competitor");
            return market;
        }
    }

    public static class HoleNrCompetitorUnderPar {

        public static DescMarket holeNrCompetitorUnderParMarketDescription(Locale language) {
            MarketTranslation translation = MarketTranslation.getFor(language);
            DescMarket market = new DescMarket();
            market.setId(HOLE_NR_COMPETITOR_UNDER_PAR_MARKET_ID);
            market.setName(translation.getMarketName());
            market.setGroups("all");
            market.setSpecifiers(specifiers());
            market.setAttributes(attributes());
            market.setOutcomes(outcomes(translation));
            return market;
        }

        private static DescOutcomes outcomes(MarketTranslation translation) {
            DescOutcomes.Outcome yesOutcome = new DescOutcomes.Outcome();
            yesOutcome.setId(HoleNrCompetitorUnderParMarketIds.YES_OUTCOME_ID);
            yesOutcome.setName(translation.yesOutcome);
            DescOutcomes.Outcome noOutcome = new DescOutcomes.Outcome();
            noOutcome.setId(HoleNrCompetitorUnderParMarketIds.NO_OUTCOME_ID);
            noOutcome.setName(translation.noOutcome);
            DescOutcomes descOutcomes = new DescOutcomes();
            descOutcomes.getOutcome().add(yesOutcome);
            descOutcomes.getOutcome().add(noOutcome);
            return descOutcomes;
        }

        private static DescSpecifiers specifiers() {
            DescSpecifiers descSpecifiers = new DescSpecifiers();
            DescSpecifiers.Specifier holenrSpecifier = new DescSpecifiers.Specifier();
            holenrSpecifier.setName("holenr");
            holenrSpecifier.setType("integer");
            descSpecifiers.getSpecifier().add(holenrSpecifier);
            DescSpecifiers.Specifier competitorSpecifier = new DescSpecifiers.Specifier();
            competitorSpecifier.setName("competitor");
            competitorSpecifier.setType("string");
            descSpecifiers.getSpecifier().add(competitorSpecifier);
            return descSpecifiers;
        }

        private static Attributes attributes() {
            Attributes attributes = new Attributes();
            Attributes.Attribute isGolfStrokePlayMarketAttribute = new Attributes.Attribute();
            isGolfStrokePlayMarketAttribute.setName("is_golf_stroke_play_market");
            isGolfStrokePlayMarketAttribute.setDescription("This market is applicable to Golf stroke play");
            attributes.getAttribute().add(isGolfStrokePlayMarketAttribute);
            return attributes;
        }

        @RequiredArgsConstructor
        @Getter
        private enum MarketTranslation {
            EN(Locale.ENGLISH, "Hole {holenr} - {%competitor} under par", "yes", "no");

            private final Locale language;
            private final String marketName;
            private final String yesOutcome;
            private final String noOutcome;

            public static MarketTranslation getFor(Locale language) {
                return stream(MarketTranslation.values())
                    .filter(translation -> translation.language.equals(language))
                    .findFirst()
                    .orElseThrow(() -> new RuntimeException(LANGUAGE_NOT_SUPPORTED_BY_TEST_FIXTURE));
            }
        }
    }

    public static class PlayerToStrikeOutAppearanceTimeAtBat {

        public static DescMarket playerToStrikeOutAppearanceTimeAtBatMarketDescription(Locale language) {
            return playerToStrikeOutAppearanceTimeAtBatMarketDescription(MarketTranslation.getFor(language));
        }

        public static DescMarket playerToStrikeOutAppearanceTimeAtBatMarketDescription(
            MarketTranslation translation
        ) {
            DescMarket market = new DescMarket();
            market.setId(PLAYER_TO_STRIKE_OUT_APPEARANCE_TIME_AT_BAT_MARKET_ID);
            market.setName(translation.marketName);
            market.setGroups("all|player|rapid_market");
            market.setOutcomes(outcomes(translation));
            market.setSpecifiers(specifiers());
            return market;
        }

        private static DescSpecifiers specifiers() {
            DescSpecifiers.Specifier appearancenrSpecifier = new DescSpecifiers.Specifier();
            appearancenrSpecifier.setName("appearancenr");
            appearancenrSpecifier.setType("integer");
            DescSpecifiers.Specifier playerSpecifier = new DescSpecifiers.Specifier();
            playerSpecifier.setName("player");
            playerSpecifier.setType("string");
            DescSpecifiers descSpecifiers = new DescSpecifiers();
            descSpecifiers.getSpecifier().add(appearancenrSpecifier);
            descSpecifiers.getSpecifier().add(playerSpecifier);
            return descSpecifiers;
        }

        private static DescOutcomes outcomes(MarketTranslation translation) {
            DescOutcomes.Outcome yesOutcome = new DescOutcomes.Outcome();
            yesOutcome.setId(PlayerToStrikeOutAppearanceTimeAtBatMarketIds.YES_OUTCOME_ID);
            yesOutcome.setName(translation.yesOutcome);
            DescOutcomes.Outcome noOutcome = new DescOutcomes.Outcome();
            noOutcome.setId(PlayerToStrikeOutAppearanceTimeAtBatMarketIds.NO_OUTCOME_ID);
            noOutcome.setName(translation.noOutcome);
            DescOutcomes descOutcomes = new DescOutcomes();
            descOutcomes.getOutcome().add(yesOutcome);
            descOutcomes.getOutcome().add(noOutcome);
            return descOutcomes;
        }

        @RequiredArgsConstructor
        @Getter
        private enum MarketTranslation {
            EN(Locale.ENGLISH, "{%player} to strike out {!appearancenr} time at bat", "yes", "no");

            private final Locale language;
            private final String marketName;
            private final String yesOutcome;
            private final String noOutcome;

            public static MarketTranslation getFor(Locale language) {
                return stream(MarketTranslation.values())
                    .filter(translation -> translation.language.equals(language))
                    .findFirst()
                    .orElseThrow(() -> new RuntimeException(LANGUAGE_NOT_SUPPORTED_BY_TEST_FIXTURE));
            }
        }
    }

    public static final class EventMatchDayHomeTeamsTotal {

        public static DescMarket eventMatchDayHomeTeamsTotal(Locale language) {
            return eventMatchDayHomeTeamsTotal(MarketTranslation.getFor(language));
        }

        private static DescMarket eventMatchDayHomeTeamsTotal(MarketTranslation translation) {
            DescMarket market = new DescMarket();
            market.setId(EventMatchDayHomeTeamsTotalMarketIds.EVENT_MATCH_DAY_HOME_TEAMS_TOTAL_MARKET_ID);
            market.setName(translation.marketName);
            market.setGroups("all|matchday");
            market.setOutcomes(outcomes(translation));
            market.setSpecifiers(specifiers());
            return market;
        }

        private static DescOutcomes outcomes(MarketTranslation translation) {
            DescOutcomes.Outcome underOutcome = new DescOutcomes.Outcome();
            underOutcome.setId(EventMatchDayHomeTeamsTotalMarketIds.UNDER_TOTAL_OUTCOME_ID);
            underOutcome.setName(translation.underTotalOutcome);
            DescOutcomes.Outcome overOutcome = new DescOutcomes.Outcome();
            overOutcome.setId(EventMatchDayHomeTeamsTotalMarketIds.OVER_TOTAL_OUTCOME_ID);
            overOutcome.setName(translation.overTotalOutcome);
            DescOutcomes descOutcomes = new DescOutcomes();
            descOutcomes.getOutcome().add(underOutcome);
            descOutcomes.getOutcome().add(overOutcome);
            return descOutcomes;
        }

        private static DescSpecifiers specifiers() {
            DescSpecifiers.Specifier totalSpecifier = new DescSpecifiers.Specifier();
            totalSpecifier.setName("total");
            totalSpecifier.setType("decimal");
            DescSpecifiers.Specifier matchDaySpecifier = new DescSpecifiers.Specifier();
            matchDaySpecifier.setName("matchday");
            matchDaySpecifier.setType("integer");
            DescSpecifiers descSpecifiers = new DescSpecifiers();
            descSpecifiers.getSpecifier().add(totalSpecifier);
            descSpecifiers.getSpecifier().add(matchDaySpecifier);
            return descSpecifiers;
        }

        @RequiredArgsConstructor
        @Getter
        private enum MarketTranslation {
            EN(
                Locale.ENGLISH,
                "{$event} matchday {matchday} - home teams total",
                "under {total}",
                "over {total}"
            );

            private final Locale language;
            private final String marketName;
            private final String underTotalOutcome;
            private final String overTotalOutcome;

            public static MarketTranslation getFor(Locale language) {
                return stream(MarketTranslation.values())
                    .filter(translation -> translation.language.equals(language))
                    .findFirst()
                    .orElseThrow(() -> new RuntimeException(LANGUAGE_NOT_SUPPORTED_BY_TEST_FIXTURE));
            }
        }
    }
}
