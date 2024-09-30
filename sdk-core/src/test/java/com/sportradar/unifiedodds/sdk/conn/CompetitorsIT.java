/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */
package com.sportradar.unifiedodds.sdk.conn;

import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.wireMockConfig;
import static com.google.common.base.Predicates.not;
import static com.sportradar.unifiedodds.sdk.conn.CompetitorAssert.assertThat;
import static com.sportradar.unifiedodds.sdk.conn.CompetitorsIT.SapiCompetitorsWrapper.fromGroups;
import static com.sportradar.unifiedodds.sdk.conn.CompetitorsIT.SapiCompetitorsWrapper.fromTournament;
import static com.sportradar.unifiedodds.sdk.conn.SapiMatchSummaries.AtpHangzhouDoubles.atpHangzhouDoubleMatch;
import static com.sportradar.unifiedodds.sdk.conn.SapiMatchSummaries.Euro2024.soccerMatchGermanyVsVirtual2024;
import static com.sportradar.unifiedodds.sdk.conn.SapiPlayerProfiles.MARTIN_ODEGAARD_PLAYER_ID;
import static com.sportradar.unifiedodds.sdk.conn.SapiSimpleTeams.EnderunTitansCollegeBasketballTeam.sapiEnderunTitansTeam;
import static com.sportradar.unifiedodds.sdk.conn.SapiStageSummaries.GrandPrix2024.*;
import static com.sportradar.unifiedodds.sdk.conn.SapiTeams.ArsenalFc.arsenalProfile;
import static com.sportradar.unifiedodds.sdk.conn.SapiTeams.BuffaloSabres.buffaloSabres;
import static com.sportradar.unifiedodds.sdk.conn.SapiTeams.GrandPrix2024.ALONSO_COMPETITOR_URN;
import static com.sportradar.unifiedodds.sdk.conn.SapiTeams.NorwayNationalSoccerTeam.norwayNationalSoccerTeamProfile;
import static com.sportradar.unifiedodds.sdk.conn.SapiTournaments.Euro2024.euro2024TournamentInfo;
import static com.sportradar.unifiedodds.sdk.conn.SapiTournaments.Nascar2024.nascarCup2024TournamentInfo;
import static com.sportradar.unifiedodds.sdk.conn.SapiTournaments.Nascar2024.replaceFirstCompetitorWithVirtual;
import static com.sportradar.unifiedodds.sdk.conn.SapiTournaments.tournamentEuro2024;
import static com.sportradar.unifiedodds.sdk.impl.Constants.RABBIT_BASE_URL;
import static com.sportradar.utils.domain.names.LanguageHolder.in;
import static java.util.Locale.ENGLISH;
import static org.assertj.core.api.Assertions.assertThat;

import com.github.tomakehurst.wiremock.common.ConsoleNotifier;
import com.github.tomakehurst.wiremock.junit5.WireMockExtension;
import com.sportradar.uf.sportsapi.datamodel.SapiCompetitorProfileEndpoint;
import com.sportradar.uf.sportsapi.datamodel.SapiTeam;
import com.sportradar.uf.sportsapi.datamodel.SapiTournamentInfoEndpoint;
import com.sportradar.unifiedodds.sdk.ExceptionHandlingStrategy;
import com.sportradar.unifiedodds.sdk.conn.SapiMatchSummaries.AtpHangzhouDoubles;
import com.sportradar.unifiedodds.sdk.conn.SapiStageSummaries.GrandPrix2024;
import com.sportradar.unifiedodds.sdk.entities.*;
import com.sportradar.unifiedodds.sdk.impl.Constants;
import com.sportradar.unifiedodds.sdk.testutil.rabbit.integration.BaseUrl;
import com.sportradar.unifiedodds.sdk.testutil.rabbit.integration.Credentials;
import com.sportradar.utils.Urn;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import lombok.val;
import org.apache.commons.lang3.BooleanUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.extension.RegisterExtension;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

@SuppressWarnings({ "ClassFanOutComplexity", "VariableDeclarationUsageDistance", "MultipleStringLiterals" })
class CompetitorsIT {

    @RegisterExtension
    private static WireMockExtension wireMock = WireMockExtension
        .newInstance()
        .options(wireMockConfig().dynamicPort().notifier(new ConsoleNotifier(true)))
        .build();

    private final GlobalVariables globalVariables = new GlobalVariables();
    private final ApiSimulator apiSimulator = new ApiSimulator(wireMock.getRuntimeInfo().getWireMock());

    private final Credentials sdkCredentials = Credentials.with(
        Constants.SDK_USERNAME,
        Constants.SDK_PASSWORD
    );
    private final MessagesInMemoryStorage messagesStorage = new MessagesInMemoryStorage();

    private BaseUrl sportsApiBaseUrl;

    private CompetitorsIT() throws Exception {}

    @BeforeEach
    void setup() throws Exception {
        sportsApiBaseUrl = BaseUrl.of("localhost", wireMock.getPort());
    }

    @Nested
    class SeasonCompetitors {

        @ParameterizedTest
        @EnumSource(ExceptionHandlingStrategy.class)
        void fromSportDataProviderProperlyProvideVirtualInfo(ExceptionHandlingStrategy strategy)
            throws Exception {
            val seasonId = Urn.parse(euro2024TournamentInfo().getSeason().getId());
            globalVariables.setProducer(ProducerId.LIVE_ODDS);
            globalVariables.setSportEventUrn(seasonId);
            globalVariables.setSportUrn(Sport.FOOTBALL);

            Locale aLanguage = ENGLISH;
            apiSimulator.defineBookmaker();
            apiSimulator.activateOnlyLiveProducer();
            apiSimulator.stubAllSports(aLanguage);
            apiSimulator.stubAllTournaments(aLanguage, tournamentEuro2024());
            apiSimulator.stubSeasonSummary(aLanguage, euro2024TournamentInfo());
            SapiCompetitorProfiles
                .profilesFromGroupCompetitors(euro2024TournamentInfo())
                .forEach(p -> apiSimulator.stubCompetitorProfile(aLanguage, p));

            try (
                val sdk = SdkSetup
                    .with(sdkCredentials, RABBIT_BASE_URL, sportsApiBaseUrl, globalVariables.getNodeId())
                    .with(ListenerCollectingMessages.to(messagesStorage))
                    .with(strategy)
                    .withDefaultLanguage(aLanguage)
                    .withoutFeed()
            ) {
                val sportDataProvider = sdk.getSportDataProvider();
                val season = (Season) sportDataProvider.getSportEvent(seasonId);
                val competitors = season.getCompetitors();
                val sapiCompetitors = fromGroups(euro2024TournamentInfo());

                assertThat(competitors)
                    .filteredOn(sapiCompetitors::isVirtual)
                    .isNotEmpty()
                    .allMatch(Competitor::isVirtual, "are virtual");
                assertThat(competitors)
                    .filteredOn(sapiCompetitors::isNotVirtual)
                    .allMatch(not(Competitor::isVirtual), "are not virtual");
            }
        }
    }

    @Nested
    class TournamentCompetitors {

        @ParameterizedTest
        @EnumSource(ExceptionHandlingStrategy.class)
        void fromSportDataProviderProperlyProvideVirtualInfo(ExceptionHandlingStrategy strategy)
            throws Exception {
            val tournamentId = Urn.parse(euro2024TournamentInfo().getTournament().getId());
            globalVariables.setProducer(ProducerId.LIVE_ODDS);
            globalVariables.setSportEventUrn(tournamentId);
            globalVariables.setSportUrn(Sport.FOOTBALL);

            Locale aLanguage = ENGLISH;
            apiSimulator.defineBookmaker();
            apiSimulator.activateOnlyLiveProducer();
            apiSimulator.stubAllSports(aLanguage);
            apiSimulator.stubAllTournaments(aLanguage, tournamentEuro2024());
            apiSimulator.stubTournamentSummary(aLanguage, euro2024TournamentInfo());
            SapiCompetitorProfiles
                .profilesFromGroupCompetitors(euro2024TournamentInfo())
                .forEach(p -> apiSimulator.stubCompetitorProfile(aLanguage, p));

            try (
                val sdk = SdkSetup
                    .with(sdkCredentials, RABBIT_BASE_URL, sportsApiBaseUrl, globalVariables.getNodeId())
                    .with(ListenerCollectingMessages.to(messagesStorage))
                    .with(strategy)
                    .withDefaultLanguage(aLanguage)
                    .withoutFeed()
            ) {
                val sportDataProvider = sdk.getSportDataProvider();
                val season = (Tournament) sportDataProvider.getSportEvent(tournamentId);
                val competitors = season.getCurrentSeason().getCompetitors();
                val sapiCompetitors = fromGroups(euro2024TournamentInfo());

                assertThat(competitors)
                    .filteredOn(sapiCompetitors::isVirtual)
                    .isNotEmpty()
                    .allMatch(Competitor::isVirtual, "are virtual");
                assertThat(competitors)
                    .filteredOn(sapiCompetitors::isNotVirtual)
                    .allMatch(not(Competitor::isVirtual), "are not virtual");
            }
        }

        @ParameterizedTest
        @EnumSource(ExceptionHandlingStrategy.class)
        void competitorVirtualFlagRemainsAfterEvictingTournamentItWasSourcedFrom(
            ExceptionHandlingStrategy strategy
        ) throws Exception {
            val tournamentId = Urn.parse(euro2024TournamentInfo().getTournament().getId());
            globalVariables.setProducer(ProducerId.LIVE_ODDS);
            globalVariables.setSportEventUrn(tournamentId);
            globalVariables.setSportUrn(Sport.FOOTBALL);

            Locale aLanguage = ENGLISH;
            apiSimulator.defineBookmaker();
            apiSimulator.activateOnlyLiveProducer();
            apiSimulator.stubAllSports(aLanguage);
            apiSimulator.stubAllTournaments(aLanguage, tournamentEuro2024());
            apiSimulator.stubTournamentSummary(aLanguage, euro2024TournamentInfo());
            SapiCompetitorProfiles
                .profilesFromGroupCompetitors(euro2024TournamentInfo())
                .forEach(p -> apiSimulator.stubCompetitorProfile(aLanguage, p));

            try (
                val sdk = SdkSetup
                    .with(sdkCredentials, RABBIT_BASE_URL, sportsApiBaseUrl, globalVariables.getNodeId())
                    .with(ListenerCollectingMessages.to(messagesStorage))
                    .with(strategy)
                    .withDefaultLanguage(aLanguage)
                    .withoutFeed()
            ) {
                val sportDataProvider = sdk.getSportDataProvider();
                val season = (Tournament) sportDataProvider.getSportEvent(tournamentId);
                val competitors = season.getCurrentSeason().getCompetitors();
                val sapiCompetitors = fromGroups(euro2024TournamentInfo());

                sportDataProvider.purgeSportEventCacheData(tournamentId, true);

                assertThat(competitors)
                    .filteredOn(sapiCompetitors::isVirtual)
                    .isNotEmpty()
                    .allMatch(Competitor::isVirtual, "are virtual");
                assertThat(competitors)
                    .filteredOn(sapiCompetitors::isNotVirtual)
                    .allMatch(not(Competitor::isVirtual), "are not virtual");
            }
        }
    }

    @Nested
    class StageCompetitors {

        @ParameterizedTest
        @EnumSource(ExceptionHandlingStrategy.class)
        void acquiringVirtualCompetitorsOfTournamentStageViaSportDataProviderSportEvent(
            ExceptionHandlingStrategy strategy
        ) throws Exception {
            val stageId = Urn.parse(nascarCup2024TournamentInfo().getTournament().getId());
            val nascarCupWithVirtual = replaceFirstCompetitorWithVirtual(nascarCup2024TournamentInfo());
            globalVariables.setProducer(ProducerId.LIVE_ODDS);
            globalVariables.setSportEventUrn(stageId);
            globalVariables.setSportUrn(Sport.FOOTBALL);

            Locale aLanguage = ENGLISH;
            apiSimulator.defineBookmaker();
            apiSimulator.activateOnlyLiveProducer();
            apiSimulator.stubAllSports(aLanguage);
            apiSimulator.stubEmptyAllTournaments(aLanguage);
            apiSimulator.stubTournamentSummary(aLanguage, nascarCupWithVirtual);
            SapiCompetitorProfiles
                .profilesFromNestedTournamentCompetitors(nascarCupWithVirtual)
                .forEach(p -> apiSimulator.stubCompetitorProfile(aLanguage, p));

            try (
                val sdk = SdkSetup
                    .with(sdkCredentials, RABBIT_BASE_URL, sportsApiBaseUrl, globalVariables.getNodeId())
                    .with(ListenerCollectingMessages.to(messagesStorage))
                    .with(strategy)
                    .withDefaultLanguage(aLanguage)
                    .withoutFeed()
            ) {
                val sportDataProvider = sdk.getSportDataProvider();
                val stage = (Stage) sportDataProvider.getSportEvent(stageId);
                val competitors = stage.getCompetitors();
                val sapiCompetitors = fromTournament(nascarCupWithVirtual);

                assertThat(competitors)
                    .filteredOn(sapiCompetitors::isVirtual)
                    .allMatch(Competitor::isVirtual, "are virtual");
                assertThat(competitors)
                    .filteredOn(sapiCompetitors::isNotVirtual)
                    .allMatch(not(Competitor::isVirtual), "are not virtual");
            }
        }

        @ParameterizedTest
        @EnumSource(ExceptionHandlingStrategy.class)
        void acquiringVirtualCompetitorOfRaceViaSportDataProvider(ExceptionHandlingStrategy strategy)
            throws Exception {
            val raceUrn = Urn.parse(GrandPrix2024.RACE_STAGE_URN);
            val grandPrixWithVirtual = replaceHamiltonWithVirtualCompetitor(grandPrix2024RaceStageEndpoint());

            Locale aLanguage = ENGLISH;
            apiSimulator.defineBookmaker();
            apiSimulator.activateOnlyLiveProducer();
            apiSimulator.stubRaceSummary(aLanguage, grandPrixWithVirtual);
            SapiCompetitorProfiles
                .profilesFromSapiStageSummary(grandPrixWithVirtual)
                .forEach(p -> apiSimulator.stubCompetitorProfile(aLanguage, p));

            try (
                val sdk = SdkSetup
                    .with(sdkCredentials, RABBIT_BASE_URL, sportsApiBaseUrl, globalVariables.getNodeId())
                    .with(ListenerCollectingMessages.to(messagesStorage))
                    .with(strategy)
                    .withDefaultLanguage(aLanguage)
                    .withoutFeed()
            ) {
                val sportDataProvider = sdk.getSportDataProvider();
                val stage = (Stage) sportDataProvider.getSportEvent(raceUrn);
                val virtualCompetitor = stage
                    .getCompetitors()
                    .stream()
                    .filter(c -> Objects.equals(c.getId().toString(), SapiTeams.VirtualCompetitor.ID))
                    .findFirst()
                    .get();
                val fernandoAlonso = stage
                    .getCompetitors()
                    .stream()
                    .filter(c -> Objects.equals(c.getId().toString(), ALONSO_COMPETITOR_URN))
                    .findFirst()
                    .get();

                assertThat(virtualCompetitor).isVirtual();
                assertThat(fernandoAlonso).isNotVirtual();
            }
        }

        @ParameterizedTest
        @EnumSource(ExceptionHandlingStrategy.class)
        void competitorVirtualFlagRemainsAfterEvictingStageItWasSourcedFrom(
            ExceptionHandlingStrategy strategy
        ) throws Exception {
            val stageId = Urn.parse(nascarCup2024TournamentInfo().getTournament().getId());
            val nascarCupWithVirtual = replaceFirstCompetitorWithVirtual(nascarCup2024TournamentInfo());
            globalVariables.setProducer(ProducerId.LIVE_ODDS);
            globalVariables.setSportEventUrn(stageId);
            globalVariables.setSportUrn(Sport.FOOTBALL);

            Locale aLanguage = ENGLISH;
            apiSimulator.defineBookmaker();
            apiSimulator.activateOnlyLiveProducer();
            apiSimulator.stubAllSports(aLanguage);
            apiSimulator.stubEmptyAllTournaments(aLanguage);
            apiSimulator.stubTournamentSummary(aLanguage, nascarCupWithVirtual);
            SapiCompetitorProfiles
                .profilesFromNestedTournamentCompetitors(nascarCupWithVirtual)
                .forEach(p -> apiSimulator.stubCompetitorProfile(aLanguage, p));

            try (
                val sdk = SdkSetup
                    .with(sdkCredentials, RABBIT_BASE_URL, sportsApiBaseUrl, globalVariables.getNodeId())
                    .with(ListenerCollectingMessages.to(messagesStorage))
                    .with(strategy)
                    .withDefaultLanguage(aLanguage)
                    .withoutFeed()
            ) {
                val sportDataProvider = sdk.getSportDataProvider();
                val stage = (Stage) sportDataProvider.getSportEvent(stageId);
                val competitors = stage.getCompetitors();
                val sapiCompetitors = fromTournament(nascarCupWithVirtual);

                sportDataProvider.purgeSportEventCacheData(stageId, true);

                assertThat(competitors)
                    .filteredOn(sapiCompetitors::isVirtual)
                    .isNotEmpty()
                    .allMatch(Competitor::isVirtual, "are virtual");
                assertThat(competitors)
                    .filteredOn(sapiCompetitors::isNotVirtual)
                    .allMatch(not(Competitor::isVirtual), "are not virtual");
            }
        }

        @ParameterizedTest
        @EnumSource(ExceptionHandlingStrategy.class)
        void competitorRemainsVirtualAfterPurgingRaceStageTheCompetitorIsAssociatedWith(
            ExceptionHandlingStrategy strategy
        ) throws Exception {
            val raceUrn = Urn.parse(GrandPrix2024.RACE_STAGE_URN);
            val grandPrixWithVirtual = replaceHamiltonWithVirtualCompetitor(grandPrix2024RaceStageEndpoint());

            Locale aLanguage = ENGLISH;
            apiSimulator.defineBookmaker();
            apiSimulator.activateOnlyLiveProducer();
            apiSimulator.stubRaceSummary(aLanguage, grandPrixWithVirtual);
            SapiCompetitorProfiles
                .profilesFromSapiStageSummary(grandPrixWithVirtual)
                .forEach(p -> apiSimulator.stubCompetitorProfile(aLanguage, p));

            try (
                val sdk = SdkSetup
                    .with(sdkCredentials, RABBIT_BASE_URL, sportsApiBaseUrl, globalVariables.getNodeId())
                    .with(ListenerCollectingMessages.to(messagesStorage))
                    .with(strategy)
                    .withDefaultLanguage(aLanguage)
                    .withoutFeed()
            ) {
                val sportDataProvider = sdk.getSportDataProvider();
                val stage = (Stage) sportDataProvider.getSportEvent(raceUrn);
                val virtualCompetitor = stage
                    .getCompetitors()
                    .stream()
                    .filter(c -> Objects.equals(c.getId().toString(), SapiTeams.VirtualCompetitor.ID))
                    .findFirst()
                    .get();
                val fernandoAlonso = stage
                    .getCompetitors()
                    .stream()
                    .filter(c -> Objects.equals(c.getId().toString(), ALONSO_COMPETITOR_URN))
                    .findFirst()
                    .get();

                sportDataProvider.purgeSportEventCacheData(Urn.parse(RACE_STAGE_URN), true);

                CompetitorAssert.assertThat(virtualCompetitor).isVirtual();
                CompetitorAssert.assertThat(fernandoAlonso).isNotVirtual();
            }
        }
    }

    @Nested
    class MatchCompetitors {

        @ParameterizedTest
        @EnumSource(ExceptionHandlingStrategy.class)
        void fromSportDataProviderProperlyReturnVirtualFlag(ExceptionHandlingStrategy strategy)
            throws Exception {
            val matchId = Urn.parse(soccerMatchGermanyVsVirtual2024().getSportEvent().getId());
            globalVariables.setProducer(ProducerId.LIVE_ODDS);
            globalVariables.setSportEventUrn(matchId);
            globalVariables.setSportUrn(Sport.FOOTBALL);

            Locale aLanguage = ENGLISH;
            apiSimulator.defineBookmaker();
            apiSimulator.activateOnlyLiveProducer();
            apiSimulator.stubAllSports(aLanguage);
            apiSimulator.stubAllTournaments(aLanguage, tournamentEuro2024());
            apiSimulator.stubMatchSummary(aLanguage, soccerMatchGermanyVsVirtual2024());
            SapiCompetitorProfiles
                .profilesFromSapiMatchSummary(soccerMatchGermanyVsVirtual2024())
                .forEach(p -> apiSimulator.stubCompetitorProfile(aLanguage, p));

            try (
                val sdk = SdkSetup
                    .with(sdkCredentials, RABBIT_BASE_URL, sportsApiBaseUrl, globalVariables.getNodeId())
                    .with(ListenerCollectingMessages.to(messagesStorage))
                    .with(strategy)
                    .withDefaultLanguage(aLanguage)
                    .withoutFeed()
            ) {
                val sportDataProvider = sdk.getSportDataProvider();
                val match = (Match) sportDataProvider.getSportEvent(matchId);
                val competitors = match
                    .getCompetitors()
                    .stream()
                    .collect(Collectors.toMap(c -> c.getId().toString(), c -> c));

                val sapiCompetitors = soccerMatchGermanyVsVirtual2024()
                    .getSportEvent()
                    .getCompetitors()
                    .getCompetitor();
                val sapiGermanyCompetitor = sapiCompetitors.get(0);
                val sapiVirtualCompetitor = sapiCompetitors.get(1);

                assertThat(competitors.get(sapiGermanyCompetitor.getId()).isVirtual()).isFalse();
                assertThat(competitors.get(sapiVirtualCompetitor.getId()).isVirtual()).isTrue();
            }
        }

        @ParameterizedTest
        @EnumSource(ExceptionHandlingStrategy.class)
        void sportDataProviderReturnsSamePlayerWithDifferentJerseyNumbersForDifferentCompetitors(
            ExceptionHandlingStrategy strategy
        ) throws Exception {
            val martinOdegaardId = Urn.parse(MARTIN_ODEGAARD_PLAYER_ID);
            val martinOdegaardArsenalJerseyNumber = getMartinOdegaardJerseyNumber(arsenalProfile());
            val martinOdegaardNorwayTeamJerseyNumber = getMartinOdegaardJerseyNumber(
                norwayNationalSoccerTeamProfile()
            );
            val norwayNationalTeamId = Urn.parse(norwayNationalSoccerTeamProfile().getCompetitor().getId());
            val arsenalFcId = Urn.parse(arsenalProfile().getCompetitor().getId());

            Locale aLanguage = ENGLISH;
            apiSimulator.defineBookmaker();
            apiSimulator.activateOnlyLiveProducer();
            apiSimulator.stubAllSports(aLanguage);
            apiSimulator.stubAllTournaments(aLanguage, tournamentEuro2024());
            apiSimulator.stubCompetitorProfile(
                aLanguage,
                norwayNationalSoccerTeamProfile(),
                arsenalProfile()
            );

            try (
                val sdk = SdkSetup
                    .with(sdkCredentials, RABBIT_BASE_URL, sportsApiBaseUrl, globalVariables.getNodeId())
                    .with(ListenerCollectingMessages.to(messagesStorage))
                    .with(strategy)
                    .withDefaultLanguage(aLanguage)
                    .withoutFeed()
            ) {
                val sportDataProvider = sdk.getSportDataProvider();
                val norwayNationalTeam = sportDataProvider.getCompetitor(norwayNationalTeamId, ENGLISH);
                val arsenalFc = sportDataProvider.getCompetitor(arsenalFcId, ENGLISH);

                val martinOdegaardNorwayTeamPlayer = getCompetitorPlayerProfileById(
                    norwayNationalTeam,
                    martinOdegaardId
                );
                val martinOdegaardArsenalPlayer = getCompetitorPlayerProfileById(arsenalFc, martinOdegaardId);

                assertThat(((CompetitorPlayer) martinOdegaardNorwayTeamPlayer).getJerseyNumber())
                    .isEqualTo(martinOdegaardNorwayTeamJerseyNumber);
                assertThat(((CompetitorPlayer) martinOdegaardArsenalPlayer).getJerseyNumber())
                    .isEqualTo(martinOdegaardArsenalJerseyNumber);
            }
        }

        @ParameterizedTest
        @EnumSource(ExceptionHandlingStrategy.class)
        void competitorVirtualFlagRemainsAfterEvictingMatchItWasSourcedFrom(
            ExceptionHandlingStrategy strategy
        ) throws Exception {
            val matchId = Urn.parse(soccerMatchGermanyVsVirtual2024().getSportEvent().getId());
            globalVariables.setProducer(ProducerId.LIVE_ODDS);
            globalVariables.setSportEventUrn(matchId);
            globalVariables.setSportUrn(Sport.FOOTBALL);

            Locale aLanguage = ENGLISH;
            apiSimulator.defineBookmaker();
            apiSimulator.activateOnlyLiveProducer();
            apiSimulator.stubAllSports(aLanguage);
            apiSimulator.stubAllTournaments(aLanguage, tournamentEuro2024());
            apiSimulator.stubMatchSummary(aLanguage, soccerMatchGermanyVsVirtual2024());
            SapiCompetitorProfiles
                .profilesFromSapiMatchSummary(soccerMatchGermanyVsVirtual2024())
                .forEach(p -> apiSimulator.stubCompetitorProfile(aLanguage, p));

            try (
                val sdk = SdkSetup
                    .with(sdkCredentials, RABBIT_BASE_URL, sportsApiBaseUrl, globalVariables.getNodeId())
                    .with(ListenerCollectingMessages.to(messagesStorage))
                    .with(strategy)
                    .withDefaultLanguage(aLanguage)
                    .withoutFeed()
            ) {
                val sportDataProvider = sdk.getSportDataProvider();
                val match = (Match) sportDataProvider.getSportEvent(matchId);
                val competitors = match
                    .getCompetitors()
                    .stream()
                    .collect(Collectors.toMap(c -> c.getId().toString(), c -> c));

                sportDataProvider.purgeSportEventCacheData(matchId, true);

                val sapiCompetitors = soccerMatchGermanyVsVirtual2024()
                    .getSportEvent()
                    .getCompetitors()
                    .getCompetitor();
                val sapiGermanyCompetitor = sapiCompetitors.get(0);
                val sapiVirtualCompetitor = sapiCompetitors.get(1);

                assertThat(competitors.get(sapiGermanyCompetitor.getId()).isVirtual()).isFalse();
                assertThat(competitors.get(sapiVirtualCompetitor.getId()).isVirtual()).isTrue();
            }
        }

        @ParameterizedTest
        @EnumSource(ExceptionHandlingStrategy.class)
        void providesCompetitorAssociatedPlayersForTennisDoublesOnlyWhenSummaryPopulatesCacheBeforehand(
            ExceptionHandlingStrategy strategy
        ) throws Exception {
            val matchId = Urn.parse(atpHangzhouDoubleMatch().getSportEvent().getId());
            globalVariables.setProducer(ProducerId.LIVE_ODDS);
            globalVariables.setSportEventUrn(matchId);
            globalVariables.setSportUrn(Sport.TENNIS);

            Locale aLanguage = ENGLISH;
            apiSimulator.defineBookmaker();
            apiSimulator.activateOnlyLiveProducer();
            apiSimulator.stubAllSports(aLanguage);
            apiSimulator.stubEmptyAllTournaments(aLanguage);
            apiSimulator.stubMatchSummary(aLanguage, atpHangzhouDoubleMatch());
            SapiCompetitorProfiles
                .profilesFromSapiMatchSummary(atpHangzhouDoubleMatch())
                .forEach(p -> apiSimulator.stubCompetitorProfile(aLanguage, p));

            try (
                val sdk = SdkSetup
                    .with(sdkCredentials, RABBIT_BASE_URL, sportsApiBaseUrl, globalVariables.getNodeId())
                    .with(ListenerCollectingMessages.to(messagesStorage))
                    .with(strategy)
                    .withDefaultLanguage(aLanguage)
                    .withoutFeed()
            ) {
                val sportDataProvider = sdk.getSportDataProvider();

                sportDataProvider.getSportEvent(matchId);

                val homeCompetitor = sportDataProvider.getCompetitor(AtpHangzhouDoubles.HOME_COMPETITOR);
                val awayCompetitor = sportDataProvider.getCompetitor(AtpHangzhouDoubles.AWAY_COMPETITOR);

                assertThat(homeCompetitor.getPlayers()).hasSize(2);
                assertThat(awayCompetitor.getPlayers()).hasSize(2);
            }
        }
    }

    private static Integer getMartinOdegaardJerseyNumber(
        SapiCompetitorProfileEndpoint sapiCompetitorProfileEndpoint
    ) {
        return sapiCompetitorProfileEndpoint
            .getPlayers()
            .getPlayer()
            .stream()
            .filter(p -> p.getId().equals(MARTIN_ODEGAARD_PLAYER_ID))
            .findFirst()
            .get()
            .getJerseyNumber();
    }

    private static Player getCompetitorPlayerProfileById(Competitor competitor, Urn playerId) {
        return competitor.getPlayers().stream().filter(p -> p.getId().equals(playerId)).findFirst().get();
    }

    @Nested
    class ProfileEndpointCompetitors {

        @ParameterizedTest
        @EnumSource(ExceptionHandlingStrategy.class)
        void competitorDataIsProperlyPopulated(ExceptionHandlingStrategy strategy) throws Exception {
            globalVariables.setProducer(ProducerId.LIVE_ODDS);
            globalVariables.setSportEventUrn(SportEvent.MATCH);
            globalVariables.setSportUrn(Sport.FOOTBALL);

            Locale aLanguage = ENGLISH;
            apiSimulator.defineBookmaker();
            apiSimulator.activateOnlyLiveProducer();
            apiSimulator.stubCompetitorProfile(aLanguage, buffaloSabres());

            try (
                val sdk = SdkSetup
                    .with(sdkCredentials, RABBIT_BASE_URL, sportsApiBaseUrl, globalVariables.getNodeId())
                    .with(ListenerCollectingMessages.to(messagesStorage))
                    .with(strategy)
                    .withDefaultLanguage(aLanguage)
                    .withoutFeed()
            ) {
                val sportDataProvider = sdk.getSportDataProvider();
                val sapiCompetitorProfile = buffaloSabres();
                val competitor = (Competitor) sportDataProvider.getCompetitor(
                    Urn.parse(sapiCompetitorProfile.getCompetitor().getId())
                );

                assertThat(competitor, in(aLanguage))
                    .hasSameUrnAndNameAs(sapiCompetitorProfile.getCompetitor())
                    .hasPlayersWithSameIdsAndNamesAs(sapiCompetitorProfile.getPlayers())
                    .hasPlayersWithSameJerseyNumbersAs(sapiCompetitorProfile.getPlayers())
                    .hasSameJerseysAs(sapiCompetitorProfile.getJerseys());
            }
        }
    }

    @Nested
    class SimpleTeamCompetitors {

        @ParameterizedTest
        @EnumSource(ExceptionHandlingStrategy.class)
        void competitorDataIsProperlyPopulated(ExceptionHandlingStrategy strategy) throws Exception {
            globalVariables.setProducer(ProducerId.LIVE_ODDS);
            globalVariables.setSportEventUrn(SportEvent.MATCH);
            globalVariables.setSportUrn(Sport.FOOTBALL);

            Locale aLanguage = ENGLISH;
            apiSimulator.defineBookmaker();
            apiSimulator.activateOnlyLiveProducer();
            apiSimulator.stubCompetitorProfile(aLanguage, sapiEnderunTitansTeam());

            try (
                val sdk = SdkSetup
                    .with(sdkCredentials, RABBIT_BASE_URL, sportsApiBaseUrl, globalVariables.getNodeId())
                    .with(ListenerCollectingMessages.to(messagesStorage))
                    .with(strategy)
                    .withDefaultLanguage(aLanguage)
                    .withoutFeed()
            ) {
                val sportDataProvider = sdk.getSportDataProvider();
                val sapiSimpleTeamProfile = sapiEnderunTitansTeam();
                val competitor = sportDataProvider.getCompetitor(
                    Urn.parse(sapiSimpleTeamProfile.getCompetitor().getId())
                );

                assertThat(competitor, in(aLanguage))
                    .hasSameUrnAndNameAs(sapiSimpleTeamProfile.getCompetitor());
            }
        }
    }

    static final class SapiCompetitorsWrapper {

        private final Map<Urn, Boolean> competitorsVirtualState;

        SapiCompetitorsWrapper(SapiTournamentInfoEndpoint info, Mode mode) {
            Stream<SapiTeam> competitors = mode == Mode.TOURNAMENT_COMPETITORS
                ? info.getTournament().getCompetitors().getCompetitor().stream()
                : info.getGroups().getGroup().stream().flatMap(g -> g.getCompetitor().stream());

            competitorsVirtualState =
                competitors.collect(
                    Collectors.toMap(
                        c -> Urn.parse(c.getId()),
                        c -> BooleanUtils.isTrue(c.isVirtual()),
                        (a, b) -> a
                    )
                );
        }

        boolean isVirtual(Competitor competitor) {
            return competitorsVirtualState.get(competitor.getId());
        }

        boolean isNotVirtual(Competitor competitor) {
            return !isVirtual(competitor);
        }

        enum Mode {
            TOURNAMENT_COMPETITORS,
            GROUP_COMPETITORS,
        }

        static SapiCompetitorsWrapper fromTournament(SapiTournamentInfoEndpoint info) {
            return new SapiCompetitorsWrapper(info, Mode.TOURNAMENT_COMPETITORS);
        }

        static SapiCompetitorsWrapper fromGroups(SapiTournamentInfoEndpoint info) {
            return new SapiCompetitorsWrapper(info, Mode.GROUP_COMPETITORS);
        }
    }
}
