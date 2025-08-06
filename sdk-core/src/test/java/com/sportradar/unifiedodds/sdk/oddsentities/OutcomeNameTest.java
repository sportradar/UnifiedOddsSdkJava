/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */
package com.sportradar.unifiedodds.sdk.oddsentities;

import static com.sportradar.unifiedodds.sdk.caching.impl.ci.CompetitorCis.competitorCi;
import static com.sportradar.unifiedodds.sdk.caching.impl.ci.PlayerProfileCis.playerProfileCi;
import static com.sportradar.unifiedodds.sdk.caching.markets.MarketDescriptionProviders.providing;
import static com.sportradar.unifiedodds.sdk.conn.SapiMarketDescriptions.AnytimeGoalscorer.anytimeGoalscorerMarketDescription;
import static com.sportradar.unifiedodds.sdk.conn.SapiMarketDescriptions.BatterHead2Head.batterHead2HeadMarketDescription;
import static com.sportradar.unifiedodds.sdk.conn.SapiMarketDescriptions.GoldHead2Head.golfHead2HeadMarketDescription;
import static com.sportradar.unifiedodds.sdk.conn.SapiMarketDescriptions.WinnerCompetitor.winnerCompetitorMarketDescription;
import static com.sportradar.unifiedodds.sdk.conn.SapiPlayerProfiles.Cricket.EnglandNationalTeam2025.joeRootProfile;
import static com.sportradar.unifiedodds.sdk.conn.SapiPlayerProfiles.Cricket.IndiaNationalTeam2025.viratKohliProfile;
import static com.sportradar.unifiedodds.sdk.conn.SapiTeams.FormulaOnePilots.fernandoAlonsoTeamCompetitor;
import static com.sportradar.unifiedodds.sdk.conn.SapiTeams.FormulaOnePilots.lewisHamiltonTeamCompetitor;
import static com.sportradar.unifiedodds.sdk.conn.SapiTeams.Germany2024Uefa.*;
import static com.sportradar.unifiedodds.sdk.conn.SapiTeams.Germany2024Uefa.getNeuerManuel;
import static com.sportradar.unifiedodds.sdk.conn.SapiTeams.TenerifeWomensOpen2025Golf.carmenAlonsoFuentes;
import static com.sportradar.unifiedodds.sdk.conn.SapiTeams.TenerifeWomensOpen2025Golf.casandraAlexander;
import static com.sportradar.unifiedodds.sdk.conn.UfMarkets.WithOdds.*;
import static com.sportradar.unifiedodds.sdk.conn.UfMarkets.WithOdds.UfOddsChangeOutcomeBuilder.activeOutcome;
import static com.sportradar.unifiedodds.sdk.conn.UfSpecifiers.UfCompetitor1Specifier.competitor1;
import static com.sportradar.unifiedodds.sdk.conn.UfSpecifiers.UfCompetitor2Specifier.competitor2;
import static com.sportradar.unifiedodds.sdk.conn.UfSpecifiers.UfMaxoversSpecifier.maxovers;
import static com.sportradar.unifiedodds.sdk.conn.UfSpecifiers.UfPlayer1Specifier.player1;
import static com.sportradar.unifiedodds.sdk.conn.UfSpecifiers.UfPlayer2Specifier.player2;
import static com.sportradar.unifiedodds.sdk.conn.UfSpecifiers.UfTypeSpecifier.type;
import static com.sportradar.unifiedodds.sdk.conn.marketids.AnytimeGoalscorerMarketIds.NO_GOAL_OUTCOME_ID;
import static com.sportradar.unifiedodds.sdk.entities.Matches.matchWithHomeAndAwayCompetitors;
import static com.sportradar.unifiedodds.sdk.entities.SportEvents.anyMatch;
import static com.sportradar.unifiedodds.sdk.impl.oddsentities.markets.MarketFactories.BuilderStubbingOutSportEventAndCaches.stubbingOutCaches;
import static com.sportradar.unifiedodds.sdk.impl.oddsentities.markets.OutcomeOddsAssert.assertThat;
import static com.sportradar.unifiedodds.sdk.oddsentities.TeamCompetitors.teamCompetitor;
import static com.sportradar.unifiedodds.sdk.testutil.generic.naturallanguage.Prepositions.from;
import static com.sportradar.unifiedodds.sdk.testutil.generic.naturallanguage.Prepositions.with;
import static com.sportradar.utils.Urn.parse;
import static com.sportradar.utils.domain.names.LanguageHolder.in;
import static org.codehaus.groovy.runtime.InvokerHelper.asList;

import com.google.common.collect.ImmutableMap;
import com.sportradar.uf.datamodel.UfOddsChangeMarket;
import com.sportradar.uf.sportsapi.datamodel.DescMarket;
import com.sportradar.uf.sportsapi.datamodel.DescOutcomes;
import com.sportradar.unifiedodds.sdk.ExceptionHandlingStrategy;
import com.sportradar.unifiedodds.sdk.caching.impl.ProfileCaches;
import com.sportradar.unifiedodds.sdk.caching.markets.MarketDescriptionStub;
import com.sportradar.unifiedodds.sdk.caching.markets.NameFocusedOutcomeDescriptionStub;
import com.sportradar.unifiedodds.sdk.entities.markets.OutcomeDescription;
import com.sportradar.unifiedodds.sdk.impl.oddsentities.markets.OutcomesAssert;
import com.sportradar.unifiedodds.sdk.internal.caching.Translations;
import com.sportradar.utils.domain.names.LanguageHolder;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.stream.Collectors;
import lombok.val;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

@SuppressWarnings("MagicNumber")
public class OutcomeNameTest {

    private static final int PRODUCER_ID = 1;
    private static final int ANY_INT = 10;

    private static final Map<String, String> NO_SPECIFIERS = ImmutableMap.of();

    @Nested
    class PlayerExpressions {

        private Locale language = Locale.ENGLISH;

        @Test
        void generatesPlayerNamesAsOutcomeNamesFromPlayerIdsInSpecifiers() throws Exception {
            val sapiMarketDescription = batterHead2HeadMarketDescription(language);
            val batterHead2HeadMarketDescription = stubWithMarketAndOutcomeIdsAndNamesFrom(
                sapiMarketDescription,
                language
            );

            val specifiers = ImmutableMap.of(
                "player1",
                joeRootProfile().getId(),
                "player2",
                viratKohliProfile().getId(),
                "maxovers",
                ANY_INT + ""
            );
            val marketDescriptionProvider = providing(
                in(language),
                batterHead2HeadMarketDescription,
                with(specifiers)
            );

            val joeRoot = playerProfileCi()
                .withId(parse(joeRootProfile().getId()))
                .withName(in(language), joeRootProfile().getName())
                .build();
            val viratKohli = playerProfileCi()
                .withId(parse(viratKohliProfile().getId()))
                .withName(in(language), viratKohliProfile().getName())
                .build();
            val profileCache = ProfileCaches.providing(in(language), joeRoot, viratKohli);

            val factory = stubbingOutCaches()
                .with(ExceptionHandlingStrategy.Throw)
                .with(marketDescriptionProvider)
                .with(profileCache)
                .withDefaultLanguage(language)
                .build();

            val market = factory
                .buildMarketWithOdds(
                    anyMatch(),
                    batterHead2HeadMarket(
                        player1(parse(joeRootProfile().getId())),
                        player2(parse(viratKohliProfile().getId())),
                        maxovers(ANY_INT)
                    ),
                    PRODUCER_ID
                )
                .get();
            val outcomeOdds = market.getOutcomeOdds();

            assertThat(outcomeOdds.get(0)).hasNameForDefaultLanguage(language, joeRootProfile().getName());
            String draw = batterHead2HeadMarketDescription.getOutcomes().get(1).getName(language);
            assertThat(outcomeOdds.get(1)).hasNameForDefaultLanguage(language, draw);
            assertThat(outcomeOdds.get(2)).hasNameForDefaultLanguage(language, viratKohliProfile().getName());
        }
    }

    @Nested
    class CompetitorExpressions {

        private Locale language = Locale.ENGLISH;

        @Test
        void generatesCompetitorNamesAsOutcomeNamesFromCompetitorIdsInSpecifiers() throws Exception {
            val sapiMarketDescription = golfHead2HeadMarketDescription(language);
            val golfHead2HeadMarketDescription = stubWithMarketAndOutcomeIdsAndNamesFrom(
                sapiMarketDescription,
                language
            );

            val specifiers = ImmutableMap.of(
                "competitor1",
                carmenAlonsoFuentes().getId(),
                "competitor2",
                casandraAlexander().getId()
            );
            val marketDescriptionProvider = providing(
                in(language),
                golfHead2HeadMarketDescription,
                with(specifiers)
            );

            val carmenAlonsoFuentes = competitorCi()
                .withId(parse(carmenAlonsoFuentes().getId()))
                .withName(in(language), carmenAlonsoFuentes().getName())
                .build();
            val casandraAlexander = competitorCi()
                .withId(parse(casandraAlexander().getId()))
                .withName(in(language), casandraAlexander().getName())
                .build();
            val profileCache = ProfileCaches.providing(in(language), carmenAlonsoFuentes, casandraAlexander);

            val factory = stubbingOutCaches() //SPORT EVENT
                .with(ExceptionHandlingStrategy.Throw)
                .with(marketDescriptionProvider)
                .with(profileCache)
                .withDefaultLanguage(language)
                .build();

            val home = teamCompetitor().withName(in(language), carmenAlonsoFuentes().getName()).build();
            val away = teamCompetitor().withName(in(language), casandraAlexander().getName()).build();
            val market = factory
                .buildMarketWithOdds(
                    matchWithHomeAndAwayCompetitors(home, away),
                    golfHead2HeadMarket(
                        competitor1(parse(carmenAlonsoFuentes().getId())),
                        competitor2(parse(casandraAlexander().getId()))
                    ),
                    PRODUCER_ID
                )
                .get();
            val outcomeOdds = market.getOutcomeOdds();

            assertThat(outcomeOdds.get(0))
                .hasNameForDefaultLanguage(language, carmenAlonsoFuentes().getName());
            String draw = golfHead2HeadMarketDescription.getOutcomes().get(1).getName(language);
            assertThat(outcomeOdds.get(1)).hasNameForDefaultLanguage(language, draw);
            assertThat(outcomeOdds.get(2)).hasNameForDefaultLanguage(language, casandraAlexander().getName());
        }
    }

    @Nested
    @SuppressWarnings("MultipleStringLiterals")
    class PlayerOutcomes {

        public static final String ANY_TOURNAMENT = "sr:tournament:1";

        private Locale language = Locale.ENGLISH;

        @Test
        void marketAggregatesOutcomesFromStaticMarketDescriptionAndDynamicOutcomeFromMessage()
            throws Exception {
            val sapiMarketDescription = anytimeGoalscorerMarketDescription(language);
            val anytimeGoalscorerMarketDescription = stubWithMarketAndOutcomeIdsAndNamesFrom(
                sapiMarketDescription,
                language
            );

            val specifiers = ImmutableMap.of("type", "live");
            val marketDescriptionProvider = providing(
                in(language),
                anytimeGoalscorerMarketDescription,
                with(specifiers)
            );

            val manuelNeuer = playerProfileCi()
                .withId(parse(getNeuerManuel().getId()))
                .withName(in(language), getNeuerManuel().getName())
                .build();
            val profileCache = ProfileCaches.providing(in(language), manuelNeuer);

            val factory = stubbingOutCaches()
                .with(ExceptionHandlingStrategy.Throw)
                .with(marketDescriptionProvider)
                .with(profileCache)
                .withDefaultLanguage(language)
                .build();

            UfOddsChangeMarket ufMarket = uefa2024ScotlandVsGermanyAnytimeGoalscorerMarket(type("live"));
            val market = factory.buildMarketWithOdds(anyMatch(), ufMarket, PRODUCER_ID).get();
            val outcomeOdds = market.getOutcomeOdds();

            OutcomesAssert
                .assertThat(outcomeOdds)
                .hasOutcomeWithId(getNeuerManuel().getId())
                .which()
                .hasNameForDefaultLanguage(language, getNeuerManuel().getName());
            OutcomesAssert
                .assertThat(outcomeOdds)
                .hasOutcomeWithId(NO_GOAL_OUTCOME_ID)
                .which()
                .hasNameForDefaultLanguage(language, noGoalOutcome().getName());
        }

        @ParameterizedTest
        @ValueSource(strings = { "sr:player:8959", "bg:player:45", "od:player:9111" })
        void outcomeCanRepresentSamePlayerFromDifferentProviders(String manuelNeuerIdFromOneOfProviders)
            throws Exception {
            val sapiMarketDescription = anytimeGoalscorerMarketDescription(language);
            val anytimeGoalscorerMarketDescription = stubWithMarketAndOutcomeIdsAndNamesFrom(
                sapiMarketDescription,
                language
            );

            val specifiers = ImmutableMap.of("type", "live");
            val marketDescriptionProvider = providing(
                in(language),
                anytimeGoalscorerMarketDescription,
                with(specifiers)
            );

            val manuelNeuer = playerProfileCi()
                .withId(parse(manuelNeuerIdFromOneOfProviders))
                .withName(in(language), getNeuerManuel().getName())
                .build();
            val profileCache = ProfileCaches.providing(in(language), manuelNeuer);

            val factory = stubbingOutCaches()
                .with(ExceptionHandlingStrategy.Throw)
                .with(marketDescriptionProvider)
                .with(profileCache)
                .withDefaultLanguage(language)
                .build();

            UfOddsChangeMarket ufMarket = uefa2024ScotlandVsGermanyAnytimeGoalscorerMarket(type("live"));
            replaceManuelNeuerIdWith(manuelNeuerIdFromOneOfProviders, ufMarket);
            val market = factory.buildMarketWithOdds(anyMatch(), ufMarket, PRODUCER_ID).get();
            val outcomeOdds = market.getOutcomeOdds();

            OutcomesAssert
                .assertThat(outcomeOdds)
                .hasOutcomeWithId(manuelNeuerIdFromOneOfProviders)
                .which()
                .hasNameForDefaultLanguage(language, getNeuerManuel().getName());
        }

        @Test
        void singleOutcomeIdCanRepresentMultiplePlayers() throws Exception {
            String multiPlayerOutcomeId = getNeuerManuel().getId() + "," + getRudigerAntonio().getId();
            String multiPlayerOutcomeName = getNeuerManuel().getName() + "," + getRudigerAntonio().getName();
            DescMarket multiPlayerOutcomeIdMarketDescription = inventedMarketAsWeCouldNotFindSuchMarketInProduction(
                "Invented Market - two chosen players collectively to score 5 goals",
                multiPlayerOutcomeId,
                multiPlayerOutcomeName
            );
            val twoChosenPlayersToScore5GoalsMarketDescription = stubWithMarketAndOutcomeIdsAndNamesFrom(
                multiPlayerOutcomeIdMarketDescription,
                language
            );

            val marketDescriptionProvider = providing(
                in(language),
                twoChosenPlayersToScore5GoalsMarketDescription,
                NO_SPECIFIERS
            );

            val manuelNeuerCi = playerProfileCi()
                .withId(parse(getNeuerManuel().getId()))
                .withName(in(language), getNeuerManuel().getName())
                .build();
            val rudigerAntonioCi = playerProfileCi()
                .withId(parse(getRudigerAntonio().getId()))
                .withName(in(language), getRudigerAntonio().getName())
                .build();

            val profileCache = ProfileCaches.providing(in(language), manuelNeuerCi, rudigerAntonioCi);

            val factory = stubbingOutCaches()
                .with(ExceptionHandlingStrategy.Throw)
                .with(marketDescriptionProvider)
                .with(profileCache)
                .withDefaultLanguage(language)
                .build();

            UfOddsChangeMarket ufMarket = new UfOddsChangeMarket();
            ufMarket.setId(multiPlayerOutcomeIdMarketDescription.getId());
            ufMarket.getOutcome().add(activeOutcome().withId(multiPlayerOutcomeId));

            val market = factory.buildMarketWithOdds(anyMatch(), ufMarket, PRODUCER_ID).get();
            val outcomeOdds = market.getOutcomeOdds();

            OutcomesAssert
                .assertThat(outcomeOdds)
                .hasOutcomeWithId(multiPlayerOutcomeId)
                .which()
                .hasNameForDefaultLanguage(language, multiPlayerOutcomeName);
        }

        @Test
        void singleOutcomeIdCanRepresentPlayerAndCompetitor() throws Exception {
            String multiOutcomeId =
                getRudigerAntonio().getId() + "," + germanyCompetitorProfile().getCompetitor().getId();
            String multiOutcomeName =
                getRudigerAntonio().getName() + "," + germanyCompetitorProfile().getCompetitor().getName();
            DescMarket multiOutcomeIdMarketDescription = inventedMarketAsWeCouldNotFindSuchMarketInProduction(
                "Invented Market - player scores and the team wins",
                multiOutcomeId,
                multiOutcomeName
            );
            val playersToScoreAndTheTeamToWinMarketDescription = stubWithMarketAndOutcomeIdsAndNamesFrom(
                multiOutcomeIdMarketDescription,
                language
            );

            val marketDescriptionProvider = providing(
                in(language),
                playersToScoreAndTheTeamToWinMarketDescription,
                NO_SPECIFIERS
            );

            val rudigerAntonioCi = playerProfileCi()
                .withId(parse(getRudigerAntonio().getId()))
                .withName(in(language), getRudigerAntonio().getName())
                .build();

            val germanyCi = competitorCi()
                .withId(parse(germanyCompetitorProfile().getCompetitor().getId()))
                .withName(in(language), germanyCompetitorProfile().getCompetitor().getName())
                .build();

            val profileCache = ProfileCaches.providing(in(language), germanyCi, rudigerAntonioCi);

            val factory = stubbingOutCaches()
                .with(ExceptionHandlingStrategy.Throw)
                .with(marketDescriptionProvider)
                .with(profileCache)
                .withDefaultLanguage(language)
                .build();

            UfOddsChangeMarket ufMarket = new UfOddsChangeMarket();
            ufMarket.setId(multiOutcomeIdMarketDescription.getId());
            ufMarket.getOutcome().add(activeOutcome().withId(multiOutcomeId));

            val market = factory.buildMarketWithOdds(anyMatch(), ufMarket, PRODUCER_ID).get();
            val outcomeOdds = market.getOutcomeOdds();

            OutcomesAssert
                .assertThat(outcomeOdds)
                .hasOutcomeWithId(multiOutcomeId)
                .which()
                .hasNameForDefaultLanguage(language, multiOutcomeName);
        }

        @Test
        void entitiesOtherThanPlayerOrCompetitorShouldBeIgnoredInOutcomeName() throws Exception {
            String multiOutcomeId = getRudigerAntonio().getId() + "," + ANY_TOURNAMENT;
            String outcomeName = getRudigerAntonio().getName();
            DescMarket multiOutcomeIdMarketDescription = inventedMarketAsWeCouldNotFindSuchMarketInProduction(
                "Invented Market - player scores and a tournament is mentioned",
                multiOutcomeId,
                outcomeName
            );
            val playersToScoreMarketDescription = stubWithMarketAndOutcomeIdsAndNamesFrom(
                multiOutcomeIdMarketDescription,
                language
            );

            val marketDescriptionProvider = providing(
                in(language),
                playersToScoreMarketDescription,
                NO_SPECIFIERS
            );

            val rudigerAntonioCi = playerProfileCi()
                .withId(parse(getRudigerAntonio().getId()))
                .withName(in(language), getRudigerAntonio().getName())
                .build();

            val profileCache = ProfileCaches.providing(in(language), rudigerAntonioCi);

            val factory = stubbingOutCaches()
                .with(ExceptionHandlingStrategy.Throw)
                .with(marketDescriptionProvider)
                .with(profileCache)
                .withDefaultLanguage(language)
                .build();

            UfOddsChangeMarket ufMarket = new UfOddsChangeMarket();
            ufMarket.setId(multiOutcomeIdMarketDescription.getId());
            ufMarket.getOutcome().add(activeOutcome().withId(multiOutcomeId));

            val market = factory.buildMarketWithOdds(anyMatch(), ufMarket, PRODUCER_ID).get();
            val outcomeOdds = market.getOutcomeOdds();

            OutcomesAssert
                .assertThat(outcomeOdds)
                .hasOutcomeWithId(multiOutcomeId)
                .which()
                .hasNameForDefaultLanguage(language, outcomeName);
        }

        @Test
        void marketDescriptionWithNullOutcomesAndFeedMessageWithEmptyOutcomesProduceEmptyOutcomesList()
            throws Exception {
            val marketDescription = winnerCompetitorMarketDescription();
            nullifyMarketDescriptionOutcomesFrom(marketDescription);

            val marketDescriptionStubWithNoOutcomes = stubWithMarketAndOutcomeIdsAndNamesFrom(
                marketDescription,
                language
            );

            val marketDescriptionProvider = providing(
                in(language),
                marketDescriptionStubWithNoOutcomes,
                NO_SPECIFIERS
            );

            val factory = stubbingOutCaches()
                .with(ExceptionHandlingStrategy.Throw)
                .with(marketDescriptionProvider)
                .withDefaultLanguage(language)
                .build();

            UfOddsChangeMarket ufMarket = getUfOddsChangeMarketWithoutAnyOutcomesFor(marketDescription);

            val market = factory.buildMarketWithOdds(anyMatch(), ufMarket, PRODUCER_ID).get();
            val outcomeOdds = market.getOutcomeOdds();

            OutcomesAssert.assertThat(outcomeOdds).isEmpty();
        }

        @NotNull
        private UfOddsChangeMarket getUfOddsChangeMarketWithoutAnyOutcomesFor(DescMarket marketDescription) {
            UfOddsChangeMarket ufMarket = new UfOddsChangeMarket();
            ufMarket.getOutcome().clear();
            ufMarket.setId(marketDescription.getId());
            return ufMarket;
        }

        private void nullifyMarketDescriptionOutcomesFrom(DescMarket marketDescription) {
            marketDescription.setOutcomes(null);
        }

        private void replaceManuelNeuerIdWith(String manuelNeuerId, UfOddsChangeMarket ufMarket) {
            ufMarket
                .getOutcome()
                .stream()
                .filter(o -> o.getId().equals(getNeuerManuel().getId()))
                .forEach(o -> {
                    o.setId(manuelNeuerId);
                });
        }

        private DescOutcomes.Outcome noGoalOutcome() {
            return anytimeGoalscorerMarketDescription(language)
                .getOutcomes()
                .getOutcome()
                .stream()
                .filter(o -> o.getId().equals(NO_GOAL_OUTCOME_ID))
                .findFirst()
                .orElseThrow(IllegalStateException::new);
        }
    }

    @Nested
    @SuppressWarnings("MultipleStringLiterals")
    class CompetitorOutcomes {

        private Locale language = Locale.ENGLISH;

        @ParameterizedTest
        @ValueSource(strings = { "sr:competitor:4521", "bg:competitor:123", "od:competitor:789" })
        void outcomesCanRepresentPlayersFromDifferentFeedProviders(String fernandoAlonsoId) throws Exception {
            val sapiMarketDescription = winnerCompetitorMarketDescription();
            val winnerCompetitorMarketDescription = stubWithMarketAndOutcomeIdsAndNamesFrom(
                sapiMarketDescription,
                language
            );

            val marketDescriptionProvider = providing(
                in(language),
                winnerCompetitorMarketDescription,
                NO_SPECIFIERS
            );

            val fernandoAlonso = fernandoAlonsoTeamCompetitor();
            val fernandoAlonsoCi = competitorCi()
                .withId(parse(fernandoAlonsoId))
                .withName(in(language), fernandoAlonso.getName())
                .build();
            val profileCache = ProfileCaches.providing(in(language), fernandoAlonsoCi);

            val factory = stubbingOutCaches()
                .with(ExceptionHandlingStrategy.Throw)
                .with(marketDescriptionProvider)
                .with(profileCache)
                .withDefaultLanguage(language)
                .build();

            UfOddsChangeMarket ufMarket = winnerCompetitorMarket(asList(fernandoAlonsoId));
            val market = factory.buildMarketWithOdds(anyMatch(), ufMarket, PRODUCER_ID).get();
            val outcomeOdds = market.getOutcomeOdds();

            OutcomesAssert
                .assertThat(outcomeOdds)
                .hasOutcomeWithId(fernandoAlonsoId)
                .which()
                .hasNameForDefaultLanguage(language, fernandoAlonso.getName());
        }

        @Test
        void singleOutcomeIdCanRepresentMultipleCompetitors() throws Exception {
            val fernandoAlonso = fernandoAlonsoTeamCompetitor();
            val lewisHamilton = lewisHamiltonTeamCompetitor();
            String multiCompetitorOutcomeId = fernandoAlonso.getId() + "," + lewisHamilton.getId();
            String multiCompetitorOutcomeName = fernandoAlonso.getName() + "," + lewisHamilton.getName();
            DescMarket multiPlayerOutcomeIdMarketDescription = inventedMarketAsWeCouldNotFindSuchMarketInProduction(
                "Invented Market - two chosen competitors collectively make less than 6 pit stops",
                multiCompetitorOutcomeId,
                multiCompetitorOutcomeName
            );
            val twoChosenPlayersToScore5GoalsMarketDescription = stubWithMarketAndOutcomeIdsAndNamesFrom(
                multiPlayerOutcomeIdMarketDescription,
                language
            );

            val marketDescriptionProvider = providing(
                in(language),
                twoChosenPlayersToScore5GoalsMarketDescription,
                NO_SPECIFIERS
            );

            val fernandAlonsoCi = competitorCi()
                .withId(parse(fernandoAlonso.getId()))
                .withName(in(language), fernandoAlonso.getName())
                .build();
            val lewisHamiltonCi = competitorCi()
                .withId(parse(lewisHamilton.getId()))
                .withName(in(language), lewisHamilton.getName())
                .build();

            val profileCache = ProfileCaches.providing(in(language), fernandAlonsoCi, lewisHamiltonCi);

            val factory = stubbingOutCaches()
                .with(ExceptionHandlingStrategy.Throw)
                .with(marketDescriptionProvider)
                .with(profileCache)
                .withDefaultLanguage(language)
                .build();

            UfOddsChangeMarket ufMarket = new UfOddsChangeMarket();
            ufMarket.setId(multiPlayerOutcomeIdMarketDescription.getId());
            ufMarket.getOutcome().add(activeOutcome().withId(multiCompetitorOutcomeId));

            val market = factory.buildMarketWithOdds(anyMatch(), ufMarket, PRODUCER_ID).get();
            val outcomeOdds = market.getOutcomeOdds();

            OutcomesAssert
                .assertThat(outcomeOdds)
                .hasOutcomeWithId(multiCompetitorOutcomeId)
                .which()
                .hasNameForDefaultLanguage(language, multiCompetitorOutcomeName);
        }
    }

    private DescMarket inventedMarketAsWeCouldNotFindSuchMarketInProduction(
        String marketName,
        String outcomeId,
        String outcomeName
    ) {
        DescMarket sapiMarketDescription = new DescMarket();
        int marketId = 99999;
        sapiMarketDescription.setId(marketId);
        sapiMarketDescription.setName(marketName);
        sapiMarketDescription.setOutcomes(createSingleOutcome(outcomeId, outcomeName));
        return sapiMarketDescription;
    }

    private DescOutcomes createSingleOutcome(String outcomeId, String outcomeName) {
        DescOutcomes outcomes = new DescOutcomes();
        DescOutcomes.Outcome multiPlayerOutcome = new DescOutcomes.Outcome();
        multiPlayerOutcome.setId(outcomeId);
        multiPlayerOutcome.setName(outcomeName);
        outcomes.getOutcome().add(multiPlayerOutcome);
        return outcomes;
    }

    public MarketDescriptionStub stubWithMarketAndOutcomeIdsAndNamesFrom(
        DescMarket sapiDescMarket,
        Locale language
    ) {
        return new MarketDescriptionStub()
            .with(new Translations(language, sapiDescMarket.getName()))
            .with(outcomeIdAndName(in(language), from(sapiDescMarket)))
            .withId(sapiDescMarket.getId());
    }

    private List<OutcomeDescription> outcomeIdAndName(LanguageHolder lang, DescMarket sapiMarketDescription) {
        DescOutcomes outcomes = sapiMarketDescription.getOutcomes();
        if (outcomes != null) {
            return outcomes
                .getOutcome()
                .stream()
                .map(o ->
                    new NameFocusedOutcomeDescriptionStub(
                        o.getId(),
                        new Translations(lang.get(), o.getName())
                    )
                )
                .collect(Collectors.toList());
        } else {
            return Collections.EMPTY_LIST;
        }
    }
}
