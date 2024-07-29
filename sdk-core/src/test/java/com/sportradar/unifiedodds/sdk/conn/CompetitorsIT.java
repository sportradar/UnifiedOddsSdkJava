/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */
package com.sportradar.unifiedodds.sdk.conn;

import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.wireMockConfig;
import static com.google.common.base.Predicates.not;
import static com.sportradar.unifiedodds.sdk.conn.CompetitorsIT.SapiCompetitorsWrapper.fromGroups;
import static com.sportradar.unifiedodds.sdk.conn.CompetitorsIT.SapiCompetitorsWrapper.fromTournament;
import static com.sportradar.unifiedodds.sdk.conn.SapiCompetitorProfiles.BuffaloSabres.buffaloSabres;
import static com.sportradar.unifiedodds.sdk.conn.SapiMatchSummaries.Euro2024.soccerMatchGermanyVsVirtual2024;
import static com.sportradar.unifiedodds.sdk.conn.SapiTournaments.Euro2024.euro2024TournamentInfo;
import static com.sportradar.unifiedodds.sdk.conn.SapiTournaments.Nascar2024.nascarCup2024TournamentInfo;
import static com.sportradar.unifiedodds.sdk.conn.SapiTournaments.Nascar2024.replaceFirstCompetitorWithVirtual;
import static com.sportradar.unifiedodds.sdk.conn.SapiTournaments.tournamentEuro2024;
import static com.sportradar.unifiedodds.sdk.conn.UfMarkets.WithOdds.oddEvenMarket;
import static com.sportradar.unifiedodds.sdk.impl.Constants.*;
import static com.sportradar.unifiedodds.sdk.testutil.rabbit.integration.Credentials.with;
import static com.sportradar.unifiedodds.sdk.testutil.rabbit.integration.RabbitMqClientFactory.createRabbitMqClient;
import static com.sportradar.unifiedodds.sdk.testutil.rabbit.integration.RabbitMqProducer.connectDeclaringExchange;
import static com.sportradar.utils.domain.names.LanguageHolder.in;
import static org.assertj.core.api.Assertions.assertThat;

import com.github.tomakehurst.wiremock.junit5.WireMockExtension;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.http.client.Client;
import com.sportradar.uf.sportsapi.datamodel.SapiTeam;
import com.sportradar.uf.sportsapi.datamodel.SapiTournamentInfoEndpoint;
import com.sportradar.unifiedodds.sdk.ExceptionHandlingStrategy;
import com.sportradar.unifiedodds.sdk.entities.*;
import com.sportradar.unifiedodds.sdk.impl.Constants;
import com.sportradar.unifiedodds.sdk.impl.TimeUtilsImpl;
import com.sportradar.unifiedodds.sdk.shared.FeedMessageBuilder;
import com.sportradar.unifiedodds.sdk.testutil.rabbit.integration.*;
import com.sportradar.utils.Urn;
import java.util.Locale;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import lombok.val;
import org.apache.commons.lang3.BooleanUtils;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.extension.RegisterExtension;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

@SuppressWarnings({ "ClassFanOutComplexity", "VariableDeclarationUsageDistance", "MultipleStringLiterals" })
class CompetitorsIT {

    @RegisterExtension
    private static WireMockExtension wireMock = WireMockExtension
        .newInstance()
        .options(wireMockConfig().dynamicPort().dynamicPort())
        .build();

    private final GlobalVariables globalVariables = new GlobalVariables();
    private final ApiSimulator apiSimulator = new ApiSimulator(wireMock.getRuntimeInfo().getWireMock());

    private final Credentials sdkCredentials = Credentials.with(
        Constants.SDK_USERNAME,
        Constants.SDK_PASSWORD
    );
    private final VhostLocation vhostLocation = VhostLocation.at(RABBIT_BASE_URL, Constants.UF_VIRTUALHOST);
    private final ExchangeLocation exchangeLocation = ExchangeLocation.at(
        vhostLocation,
        Constants.UF_EXCHANGE
    );
    private final Credentials adminCredentials = Credentials.with(
        Constants.ADMIN_USERNAME,
        Constants.ADMIN_PASSWORD
    );
    private final ConnectionFactory factory = new ConnectionFactory();
    private final MessagesInMemoryStorage messagesStorage = new MessagesInMemoryStorage();

    private final WaiterForSingleMessage listinerWaitingFor = new WaiterForSingleMessage(messagesStorage);
    private final Client rabbitMqClient = createRabbitMqClient(
        RABBIT_IP,
        with(ADMIN_USERNAME, ADMIN_PASSWORD),
        Client::new
    );
    private final RabbitMqUserSetup rabbitMqUserSetup = RabbitMqUserSetup.create(
        VhostLocation.at(RABBIT_BASE_URL, UF_VIRTUALHOST),
        rabbitMqClient
    );

    private BaseUrl sportsApiBaseUrl;

    private CompetitorsIT() throws Exception {}

    @BeforeEach
    void setup() throws Exception {
        rabbitMqUserSetup.setupUser(sdkCredentials);
        sportsApiBaseUrl = BaseUrl.of("localhost", wireMock.getPort());
    }

    @AfterEach
    void tearDown() {
        rabbitMqUserSetup.revertChangesMade();
    }

    @Nested
    class SeasonCompetitors {

        @Disabled
        @ParameterizedTest
        @EnumSource(ExceptionHandlingStrategy.class)
        void fromSportDataProviderProperlyProvideVirtualInfo(ExceptionHandlingStrategy strategy)
            throws Exception {
            val seasonId = Urn.parse(euro2024TournamentInfo().getSeason().getId());
            globalVariables.setProducer(ProducerId.LIVE_ODDS);
            globalVariables.setSportEventUrn(seasonId);
            globalVariables.setSportUrn(Sport.FOOTBALL);

            Locale aLanguage = Locale.ENGLISH;
            apiSimulator.defineBookmaker();
            apiSimulator.activateOnlyLiveProducer();
            apiSimulator.stubAllSports(aLanguage);
            apiSimulator.stubAllTournaments(aLanguage, tournamentEuro2024());
            apiSimulator.stubSeasonSummary(aLanguage, euro2024TournamentInfo());

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
                    .allMatch(Competitor::isVirtual, "are virtual");
                assertThat(competitors)
                    .filteredOn(sapiCompetitors::isNotVirtual)
                    .allMatch(not(Competitor::isVirtual), "are not virtual");
            }
        }

        @ParameterizedTest
        @EnumSource(ExceptionHandlingStrategy.class)
        void currentlyAllAreMarkedAsVirtualEvenThoughTheyShouldNot(ExceptionHandlingStrategy strategy)
            throws Exception {
            val seasonId = Urn.parse(euro2024TournamentInfo().getSeason().getId());
            globalVariables.setProducer(ProducerId.LIVE_ODDS);
            globalVariables.setSportEventUrn(seasonId);
            globalVariables.setSportUrn(Sport.FOOTBALL);

            Locale aLanguage = Locale.ENGLISH;
            apiSimulator.defineBookmaker();
            apiSimulator.activateOnlyLiveProducer();
            apiSimulator.stubAllSports(aLanguage);
            apiSimulator.stubAllTournaments(aLanguage, tournamentEuro2024());
            apiSimulator.stubSeasonSummary(aLanguage, euro2024TournamentInfo());

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

                assertThat(competitors).allMatch(not(Competitor::isVirtual), "are not virtual");
            }
        }
    }

    @Nested
    class TournamentCompetitors {

        @Disabled
        @ParameterizedTest
        @EnumSource(ExceptionHandlingStrategy.class)
        void fromSportDataProviderProperlyProvideVirtualInfo(ExceptionHandlingStrategy strategy)
            throws Exception {
            val tournamentId = Urn.parse(euro2024TournamentInfo().getTournament().getId());
            globalVariables.setProducer(ProducerId.LIVE_ODDS);
            globalVariables.setSportEventUrn(tournamentId);
            globalVariables.setSportUrn(Sport.FOOTBALL);

            Locale aLanguage = Locale.ENGLISH;
            apiSimulator.defineBookmaker();
            apiSimulator.activateOnlyLiveProducer();
            apiSimulator.stubAllSports(aLanguage);
            apiSimulator.stubAllTournaments(aLanguage, tournamentEuro2024());
            apiSimulator.stubTournamentSummary(aLanguage, euro2024TournamentInfo());

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
                    .allMatch(Competitor::isVirtual, "are virtual");
                assertThat(competitors)
                    .filteredOn(sapiCompetitors::isNotVirtual)
                    .allMatch(not(Competitor::isVirtual), "are not virtual");
            }
        }

        @Disabled
        @ParameterizedTest
        @EnumSource(ExceptionHandlingStrategy.class)
        void fromFeedMessageSportEventProperlyProvideVirtualInfo(ExceptionHandlingStrategy strategy)
            throws Exception {
            val tournamentId = Urn.parse(euro2024TournamentInfo().getTournament().getId());
            val messages = new FeedMessageBuilder(globalVariables);
            val aLanguage = Locale.ENGLISH;
            val routingKeys = new RoutingKeys(globalVariables);
            globalVariables.setProducer(ProducerId.LIVE_ODDS);
            globalVariables.setSportEventUrn(tournamentId);
            globalVariables.setSportUrn(Sport.FOOTBALL);

            apiSimulator.defineBookmaker();
            apiSimulator.activateOnlyLiveProducer();
            apiSimulator.stubAllSports(aLanguage);
            apiSimulator.stubAllTournaments(aLanguage, tournamentEuro2024());
            apiSimulator.stubTournamentSummary(aLanguage, euro2024TournamentInfo());

            try (
                val rabbitProducer = connectDeclaringExchange(
                    exchangeLocation,
                    adminCredentials,
                    factory,
                    new TimeUtilsImpl()
                );
                val sdk = SdkSetup
                    .with(sdkCredentials, RABBIT_BASE_URL, sportsApiBaseUrl, globalVariables.getNodeId())
                    .with(ListenerCollectingMessages.to(messagesStorage))
                    .with(strategy)
                    .withDefaultLanguage(aLanguage)
                    .with1Session()
                    .withOpenedFeed()
            ) {
                rabbitProducer.send(messages.oddsChange(oddEvenMarket()), routingKeys.liveOddsChange());

                val oddsChange = listinerWaitingFor.theOnlyOddsChange();
                val tournament = (Tournament) oddsChange.getEvent();
                val competitors = tournament.getCurrentSeason().getCompetitors();
                val sapiCompetitors = fromGroups(euro2024TournamentInfo());

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
        void currentlyAllAreMarkedAsVirtualEvenThoughTheyShouldNot(ExceptionHandlingStrategy strategy)
            throws Exception {
            val tournamentId = Urn.parse(euro2024TournamentInfo().getTournament().getId());
            globalVariables.setProducer(ProducerId.LIVE_ODDS);
            globalVariables.setSportEventUrn(tournamentId);
            globalVariables.setSportUrn(Sport.FOOTBALL);

            Locale aLanguage = Locale.ENGLISH;
            apiSimulator.defineBookmaker();
            apiSimulator.activateOnlyLiveProducer();
            apiSimulator.stubAllSports(aLanguage);
            apiSimulator.stubAllTournaments(aLanguage, tournamentEuro2024());
            apiSimulator.stubTournamentSummary(aLanguage, euro2024TournamentInfo());

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

                assertThat(competitors).allMatch(not(Competitor::isVirtual), "are not virtual");
            }
        }
    }

    @Nested
    class StageCompetitors {

        @Disabled
        @ParameterizedTest
        @EnumSource(ExceptionHandlingStrategy.class)
        void fromSportDataProviderProperlyProvideVirtualInfo(ExceptionHandlingStrategy strategy)
            throws Exception {
            val stageId = Urn.parse(nascarCup2024TournamentInfo().getTournament().getId());
            val nascarCupWithVirtual = replaceFirstCompetitorWithVirtual(nascarCup2024TournamentInfo());
            globalVariables.setProducer(ProducerId.LIVE_ODDS);
            globalVariables.setSportEventUrn(stageId);
            globalVariables.setSportUrn(Sport.FOOTBALL);

            Locale aLanguage = Locale.ENGLISH;
            apiSimulator.defineBookmaker();
            apiSimulator.activateOnlyLiveProducer();
            apiSimulator.stubAllSports(aLanguage);
            apiSimulator.stubEmptyAllTournaments(aLanguage);
            apiSimulator.stubStageSummary(aLanguage, nascarCupWithVirtual);

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

        @Disabled
        @ParameterizedTest
        @EnumSource(ExceptionHandlingStrategy.class)
        void fromFeedMessageSportEventProperlyProvideVirtualInfo(ExceptionHandlingStrategy strategy)
            throws Exception {
            val messages = new FeedMessageBuilder(globalVariables);
            val routingKeys = new RoutingKeys(globalVariables);
            val stageId = Urn.parse(nascarCup2024TournamentInfo().getTournament().getId());
            val nascarCupWithVirtual = replaceFirstCompetitorWithVirtual(nascarCup2024TournamentInfo());
            globalVariables.setProducer(ProducerId.LIVE_ODDS);
            globalVariables.setSportEventUrn(stageId);
            globalVariables.setSportUrn(Sport.FOOTBALL);

            Locale aLanguage = Locale.ENGLISH;
            apiSimulator.defineBookmaker();
            apiSimulator.activateOnlyLiveProducer();
            apiSimulator.stubAllSports(aLanguage);
            apiSimulator.stubEmptyAllTournaments(aLanguage);
            apiSimulator.stubStageSummary(aLanguage, nascarCupWithVirtual);

            try (
                val rabbitProducer = connectDeclaringExchange(
                    exchangeLocation,
                    adminCredentials,
                    factory,
                    new TimeUtilsImpl()
                );
                val sdk = SdkSetup
                    .with(sdkCredentials, RABBIT_BASE_URL, sportsApiBaseUrl, globalVariables.getNodeId())
                    .with(ListenerCollectingMessages.to(messagesStorage))
                    .with(strategy)
                    .withDefaultLanguage(aLanguage)
                    .with1Session()
                    .withOpenedFeed()
            ) {
                rabbitProducer.send(messages.oddsChange(oddEvenMarket()), routingKeys.liveOddsChange());

                val oddsChange = listinerWaitingFor.theOnlyOddsChange();
                val stage = (Stage) oddsChange.getEvent();
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
        void currentlyAllAreMarkedAsVirtualEvenThoughTheyShouldNot(ExceptionHandlingStrategy strategy)
            throws Exception {
            val stageId = Urn.parse(nascarCup2024TournamentInfo().getTournament().getId());
            val nascarCupWithVirtual = replaceFirstCompetitorWithVirtual(nascarCup2024TournamentInfo());
            globalVariables.setProducer(ProducerId.LIVE_ODDS);
            globalVariables.setSportEventUrn(stageId);
            globalVariables.setSportUrn(Sport.FOOTBALL);

            Locale aLanguage = Locale.ENGLISH;
            apiSimulator.defineBookmaker();
            apiSimulator.activateOnlyLiveProducer();
            apiSimulator.stubAllSports(aLanguage);
            apiSimulator.stubEmptyAllTournaments(aLanguage);
            apiSimulator.stubStageSummary(aLanguage, nascarCupWithVirtual);

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

                assertThat(competitors).allMatch(not(Competitor::isVirtual), "are not virtual");
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

            Locale aLanguage = Locale.ENGLISH;
            apiSimulator.defineBookmaker();
            apiSimulator.activateOnlyLiveProducer();
            apiSimulator.stubAllSports(aLanguage);
            apiSimulator.stubAllTournaments(aLanguage, tournamentEuro2024());
            apiSimulator.stubMatchSummary(aLanguage, soccerMatchGermanyVsVirtual2024());

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
        void fromFeedMessageSportEventProperlyReturnVirtualFlag(ExceptionHandlingStrategy strategy)
            throws Exception {
            val messages = new FeedMessageBuilder(globalVariables);
            val routingKeys = new RoutingKeys(globalVariables);
            val matchId = Urn.parse(soccerMatchGermanyVsVirtual2024().getSportEvent().getId());
            globalVariables.setProducer(ProducerId.LIVE_ODDS);
            globalVariables.setSportEventUrn(matchId);
            globalVariables.setSportUrn(Sport.FOOTBALL);

            Locale aLanguage = Locale.ENGLISH;
            apiSimulator.defineBookmaker();
            apiSimulator.activateOnlyLiveProducer();
            apiSimulator.stubAllSports(aLanguage);
            apiSimulator.stubAllTournaments(aLanguage, tournamentEuro2024());
            apiSimulator.stubMatchSummary(aLanguage, soccerMatchGermanyVsVirtual2024());

            try (
                val rabbitProducer = connectDeclaringExchange(
                    exchangeLocation,
                    adminCredentials,
                    factory,
                    new TimeUtilsImpl()
                );
                val sdk = SdkSetup
                    .with(sdkCredentials, RABBIT_BASE_URL, sportsApiBaseUrl, globalVariables.getNodeId())
                    .with(ListenerCollectingMessages.to(messagesStorage))
                    .with(strategy)
                    .withDefaultLanguage(aLanguage)
                    .with1Session()
                    .withOpenedFeed()
            ) {
                rabbitProducer.send(messages.oddsChange(oddEvenMarket()), routingKeys.liveOddsChange());

                val oddsChange = listinerWaitingFor.theOnlyOddsChange();
                val match = (Match) oddsChange.getEvent();
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
    }

    @Nested
    class ProfileEndpointCompetitors {

        @ParameterizedTest
        @EnumSource(ExceptionHandlingStrategy.class)
        void competitorDataIsProperlyPopulated(ExceptionHandlingStrategy strategy) throws Exception {
            globalVariables.setProducer(ProducerId.LIVE_ODDS);
            globalVariables.setSportEventUrn(SportEvent.MATCH);
            globalVariables.setSportUrn(Sport.FOOTBALL);

            Locale aLanguage = Locale.ENGLISH;
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

                CompetitorAssert.assertThat(competitor, in(aLanguage)).isEqualTo(sapiCompetitorProfile);
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
