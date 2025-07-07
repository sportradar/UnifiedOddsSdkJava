/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */
package com.sportradar.unifiedodds.sdk.conn;

import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.wireMockConfig;
import static com.sportradar.unifiedodds.sdk.conn.ApiSimulator.ApiStubDelay.toBeDelayedBy;
import static com.sportradar.unifiedodds.sdk.conn.ProducerId.LIVE_ODDS;
import static com.sportradar.unifiedodds.sdk.conn.SapiMarketDescriptions.OddEven.oddEvenMarketDescription;
import static com.sportradar.unifiedodds.sdk.conn.SapiMatchSummaries.Euro2024.soccerMatchGermanyScotlandEuro2024;
import static com.sportradar.unifiedodds.sdk.conn.SapiStageSummaries.Formula1.BahrainGrandPrix2025FormulaOne.Race.bahrainGrandPrix2025RaceStage;
import static com.sportradar.unifiedodds.sdk.conn.SapiTournaments.Euro2024.euro2024TournamentInfo;
import static com.sportradar.unifiedodds.sdk.conn.SapiTournaments.FormulaOne2025.formulaOne2025TournamentExtended;
import static com.sportradar.unifiedodds.sdk.conn.SapiTournaments.Nascar2024.nascarCup2024TournamentInfo;
import static com.sportradar.unifiedodds.sdk.conn.SapiTournaments.SimpleTournaments.ClubFriendlyGames.clubFriendlyGames;
import static com.sportradar.unifiedodds.sdk.conn.SapiTournaments.SimpleTournaments.TenerifeWomensOpen2025Golf.tenerifeWomensOpen2025Golf;
import static com.sportradar.unifiedodds.sdk.conn.SapiTournaments.tournamentEuro2024;
import static com.sportradar.unifiedodds.sdk.conn.Sport.FOOTBALL;
import static com.sportradar.unifiedodds.sdk.conn.UfMarkets.WithOdds.oddEvenMarket;
import static com.sportradar.unifiedodds.sdk.impl.Constants.*;
import static com.sportradar.unifiedodds.sdk.testutil.rabbit.integration.RabbitMqClientFactory.createRabbitMqClient;
import static com.sportradar.unifiedodds.sdk.testutil.rabbit.integration.RabbitMqProducer.connectDeclaringExchange;
import static java.util.Locale.ENGLISH;
import static java.util.Locale.FRENCH;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;

import com.github.tomakehurst.wiremock.common.ConsoleNotifier;
import com.github.tomakehurst.wiremock.junit5.WireMockExtension;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.http.client.Client;
import com.sportradar.unifiedodds.sdk.ExceptionHandlingStrategy;
import com.sportradar.unifiedodds.sdk.SapiCategories;
import com.sportradar.unifiedodds.sdk.entities.*;
import com.sportradar.unifiedodds.sdk.exceptions.ObjectNotFoundException;
import com.sportradar.unifiedodds.sdk.impl.Constants;
import com.sportradar.unifiedodds.sdk.internal.impl.TimeUtilsImpl;
import com.sportradar.unifiedodds.sdk.managers.SportDataProvider;
import com.sportradar.unifiedodds.sdk.shared.FeedMessageBuilder;
import com.sportradar.unifiedodds.sdk.testutil.rabbit.integration.*;
import com.sportradar.utils.Urn;
import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.util.Locale;
import java.util.stream.Stream;
import lombok.val;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.RegisterExtension;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

@SuppressWarnings("ClassFanOutComplexity")
class NonCriticalPathTimeoutIT {

    private static final Duration FAST_FAILING_TIMEOUT = Duration.ofSeconds(1);
    private static final Duration NORMAL_TIMEOUT = Duration.ofSeconds(3);
    private static final Duration MORE_THAN_FAST_FAILING_BUT_LESS_THAN_NORMAL = FAST_FAILING_TIMEOUT.plus(
        1,
        ChronoUnit.SECONDS
    );
    private static final Duration MORE_THAN_NORMAL_TIMEOUT = NORMAL_TIMEOUT.plus(1, ChronoUnit.SECONDS);
    private static final String MATCHES_FROM_PROVIDER =
        "com.sportradar.unifiedodds.sdk.conn.Parameters#matchGetters";
    private static final String TOURNAMENTS_FROM_PROVIDER =
        "com.sportradar.unifiedodds.sdk.conn.Parameters#tournamentGetters";
    private static final String STAGES_FROM_PROVIDER =
        "com.sportradar.unifiedodds.sdk.conn.Parameters#stageGetters";
    private static final String SEASONS_FROM_PROVIDER =
        "com.sportradar.unifiedodds.sdk.conn.Parameters#seasonGetters";

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

    private final WaiterForSingleMessage listenerWaitingFor = new WaiterForSingleMessage(messagesStorage);
    private final Client rabbitMqClient = createRabbitMqClient(
        RABBIT_IP,
        Credentials.with(ADMIN_USERNAME, ADMIN_PASSWORD),
        Client::new
    );
    private final RabbitMqUserSetup rabbitMqUserSetup = RabbitMqUserSetup.create(
        VhostLocation.at(RABBIT_BASE_URL, UF_VIRTUALHOST),
        rabbitMqClient
    );

    NonCriticalPathTimeoutIT() throws Exception {}

    @BeforeEach
    void setup() {
        sportsApiBaseUrl = BaseUrl.of("localhost", wireMock.getPort());
        rabbitMqUserSetup.setupUser(sdkCredentials);
    }

    @AfterEach
    void tearDown() {
        rabbitMqUserSetup.revertChangesMade();
    }

    @Nested
    class GetSportEventFromSportDataProvider {

        @ParameterizedTest
        @MethodSource(MATCHES_FROM_PROVIDER)
        void onAttemptToGetMatchFailsDuringSportFetchingAfterReachingFastTimeout(
            SportEventSupplier<Match> sportEventSupplier,
            Locale language
        ) throws Exception {
            val matchId = Urn.parse(soccerMatchGermanyScotlandEuro2024().getSportEvent().getId());

            apiSimulator.defineBookmaker();
            apiSimulator.activateOnlyLiveProducer();
            apiSimulator.stubAllSports(language);
            apiSimulator.stubSportCategories(
                language,
                tournamentEuro2024().getSport(),
                SapiCategories.international()
            );
            apiSimulator.stubAllTournaments(language, tournamentEuro2024());
            apiSimulator.stubMatchSummary(
                language,
                soccerMatchGermanyScotlandEuro2024(),
                toBeDelayedBy(MORE_THAN_FAST_FAILING_BUT_LESS_THAN_NORMAL)
            );

            try (
                val sdk = SdkSetup
                    .with(sdkCredentials, RABBIT_BASE_URL, sportsApiBaseUrl, globalVariables.getNodeId())
                    .with(ListenerCollectingMessages.to(messagesStorage))
                    .with(ExceptionHandlingStrategy.Throw)
                    .withDefaultLanguage(language)
                    .withClientFastFailingTimeout((int) FAST_FAILING_TIMEOUT.getSeconds())
                    .withClientTimeout((int) NORMAL_TIMEOUT.getSeconds())
                    .withoutFeed()
            ) {
                val sportDataProvider = sdk.getSportDataProvider();
                assertThatExceptionOfType(ObjectNotFoundException.class)
                    .isThrownBy(() -> sportEventSupplier.getFrom(sportDataProvider, matchId));
            }
        }

        @ParameterizedTest
        @MethodSource(STAGES_FROM_PROVIDER)
        void onAttemptToGetRaceStageFailsAfterReachingFastTimeout(
            SportEventSupplier<Stage> sportEventSupplier,
            Locale language
        ) throws Exception {
            val stageId = Urn.parse(bahrainGrandPrix2025RaceStage().getSportEvent().getId());

            apiSimulator.defineBookmaker();
            apiSimulator.activateOnlyLiveProducer();
            apiSimulator.stubAllSports(language);
            apiSimulator.stubSportCategories(
                language,
                bahrainGrandPrix2025RaceStage().getSportEvent().getTournament().getSport(),
                SapiCategories.international()
            );
            apiSimulator.stubAllTournaments(language, formulaOne2025TournamentExtended());
            apiSimulator.stubRaceSummary(
                language,
                bahrainGrandPrix2025RaceStage(),
                toBeDelayedBy(MORE_THAN_FAST_FAILING_BUT_LESS_THAN_NORMAL)
            );

            try (
                val sdk = SdkSetup
                    .with(sdkCredentials, RABBIT_BASE_URL, sportsApiBaseUrl, globalVariables.getNodeId())
                    .with(ListenerCollectingMessages.to(messagesStorage))
                    .with(ExceptionHandlingStrategy.Throw)
                    .withDefaultLanguage(language)
                    .withClientFastFailingTimeout((int) FAST_FAILING_TIMEOUT.getSeconds())
                    .withClientTimeout((int) NORMAL_TIMEOUT.getSeconds())
                    .withoutFeed()
            ) {
                val sportDataProvider = sdk.getSportDataProvider();
                val stage = sportEventSupplier.getFrom(sportDataProvider, stageId);

                assertThatExceptionOfType(ObjectNotFoundException.class)
                    .isThrownBy(() -> stage.getName(language));
            }
        }

        @ParameterizedTest
        @MethodSource(STAGES_FROM_PROVIDER)
        void onAttemptToGetTournamentStageFailsAfterReachingFastTimeout(
            SportEventSupplier<Stage> sportEventSupplier,
            Locale language
        ) throws Exception {
            val stageId = Urn.parse(nascarCup2024TournamentInfo().getTournament().getId());

            apiSimulator.defineBookmaker();
            apiSimulator.activateOnlyLiveProducer();
            apiSimulator.stubAllSports(language);
            apiSimulator.stubSportCategories(
                language,
                bahrainGrandPrix2025RaceStage().getSportEvent().getTournament().getSport(),
                SapiCategories.international()
            );
            apiSimulator.stubAllTournaments(language, nascarCup2024TournamentInfo().getTournament());
            apiSimulator.stubTournamentSummary(
                language,
                nascarCup2024TournamentInfo(),
                toBeDelayedBy(MORE_THAN_NORMAL_TIMEOUT)
            );

            try (
                val sdk = SdkSetup
                    .with(sdkCredentials, RABBIT_BASE_URL, sportsApiBaseUrl, globalVariables.getNodeId())
                    .with(ListenerCollectingMessages.to(messagesStorage))
                    .with(ExceptionHandlingStrategy.Throw)
                    .withDefaultLanguage(language)
                    .withClientFastFailingTimeout((int) FAST_FAILING_TIMEOUT.getSeconds())
                    .withClientTimeout((int) NORMAL_TIMEOUT.getSeconds())
                    .withoutFeed()
            ) {
                val sportDataProvider = sdk.getSportDataProvider();
                val stage = sportEventSupplier.getFrom(sportDataProvider, stageId);

                assertThatExceptionOfType(ObjectNotFoundException.class)
                    .isThrownBy(() -> stage.getName(language))
                    .isNotNull();
            }
        }

        @ParameterizedTest
        @MethodSource(TOURNAMENTS_FROM_PROVIDER)
        void onAttemptToGetTournamentAfterReachingFastTimeout(
            SportEventSupplier<Tournament> sportEventSupplier,
            Locale language
        ) throws Exception {
            val tournamentId = Urn.parse(tournamentEuro2024().getId());

            apiSimulator.defineBookmaker();
            apiSimulator.activateOnlyLiveProducer();
            apiSimulator.stubAllSports(language);
            apiSimulator.stubSportCategories(
                language,
                tournamentEuro2024().getSport(),
                SapiCategories.international()
            );
            apiSimulator.stubAllTournaments(language, tournamentEuro2024());
            apiSimulator.stubTournamentSummary(
                language,
                euro2024TournamentInfo(),
                toBeDelayedBy(MORE_THAN_NORMAL_TIMEOUT)
            );

            try (
                val sdk = SdkSetup
                    .with(sdkCredentials, RABBIT_BASE_URL, sportsApiBaseUrl, globalVariables.getNodeId())
                    .with(ListenerCollectingMessages.to(messagesStorage))
                    .with(ExceptionHandlingStrategy.Throw)
                    .withDefaultLanguage(language)
                    .withClientFastFailingTimeout((int) FAST_FAILING_TIMEOUT.getSeconds())
                    .withClientTimeout((int) NORMAL_TIMEOUT.getSeconds())
                    .withoutFeed()
            ) {
                val sportDataProvider = sdk.getSportDataProvider();
                val tournament = sportEventSupplier.getFrom(sportDataProvider, tournamentId);

                assertThatExceptionOfType(ObjectNotFoundException.class)
                    .isThrownBy(() -> tournament.getName(language));
            }
        }

        @ParameterizedTest
        @MethodSource(TOURNAMENTS_FROM_PROVIDER)
        void getsClubFriendySimpleTournamentWithNonCriticalPathTimeout(
            SportEventSupplier<BasicTournament> sportEventSupplier,
            Locale language
        ) throws Exception {
            val tournamentId = Urn.parse(clubFriendlyGames(ENGLISH).getTournament().getId());

            apiSimulator.defineBookmaker();
            apiSimulator.activateOnlyLiveProducer();
            apiSimulator.stubAllSports(language);
            apiSimulator.stubSportCategories(
                language,
                clubFriendlyGames(ENGLISH).getTournament().getSport(),
                SapiCategories.international()
            );
            apiSimulator.stubAllTournaments(language, clubFriendlyGames(ENGLISH).getTournament());
            apiSimulator.stubTournamentSummary(
                language,
                clubFriendlyGames(ENGLISH),
                toBeDelayedBy(MORE_THAN_FAST_FAILING_BUT_LESS_THAN_NORMAL)
            );

            try (
                val sdk = SdkSetup
                    .with(sdkCredentials, RABBIT_BASE_URL, sportsApiBaseUrl, globalVariables.getNodeId())
                    .with(ListenerCollectingMessages.to(messagesStorage))
                    .with(ExceptionHandlingStrategy.Throw)
                    .withDefaultLanguage(language)
                    .withClientFastFailingTimeout((int) FAST_FAILING_TIMEOUT.getSeconds())
                    .withClientTimeout((int) NORMAL_TIMEOUT.getSeconds())
                    .withoutFeed()
            ) {
                val sportDataProvider = sdk.getSportDataProvider();
                val basicTournament = sportEventSupplier.getFrom(sportDataProvider, tournamentId);

                assertThat(basicTournament.getName(language)).isNotNull();
            }
        }

        @ParameterizedTest
        @MethodSource(TOURNAMENTS_FROM_PROVIDER)
        void onAttemptToGetClubFriendySimpleTournamentFailsAfterReachingNormalTimeoutDuringSummaryPreloading(
            SportEventSupplier<BasicTournament> sportEventSupplier,
            Locale language
        ) throws Exception {
            val tournamentId = Urn.parse(clubFriendlyGames(ENGLISH).getTournament().getId());

            apiSimulator.defineBookmaker();
            apiSimulator.activateOnlyLiveProducer();
            apiSimulator.stubAllSports(language);
            apiSimulator.stubSportCategories(
                language,
                clubFriendlyGames(ENGLISH).getTournament().getSport(),
                SapiCategories.international()
            );
            apiSimulator.stubAllTournaments(language, clubFriendlyGames(ENGLISH).getTournament());
            apiSimulator.stubTournamentSummary(
                language,
                clubFriendlyGames(ENGLISH),
                toBeDelayedBy(MORE_THAN_NORMAL_TIMEOUT)
            );

            try (
                val sdk = SdkSetup
                    .with(sdkCredentials, RABBIT_BASE_URL, sportsApiBaseUrl, globalVariables.getNodeId())
                    .with(ListenerCollectingMessages.to(messagesStorage))
                    .with(ExceptionHandlingStrategy.Throw)
                    .withDefaultLanguage(language)
                    .withClientFastFailingTimeout((int) FAST_FAILING_TIMEOUT.getSeconds())
                    .withClientTimeout((int) NORMAL_TIMEOUT.getSeconds())
                    .withoutFeed()
            ) {
                val sportDataProvider = sdk.getSportDataProvider();
                assertThatExceptionOfType(ObjectNotFoundException.class)
                    .isThrownBy(() -> sportEventSupplier.getFrom(sportDataProvider, tournamentId));
            }
        }

        @ParameterizedTest
        @MethodSource(TOURNAMENTS_FROM_PROVIDER)
        void onAttemptToGetNotClubFriendySimpleTournamentFailsAfterReachingFastTimeout(
            SportEventSupplier<BasicTournament> sportEventSupplier,
            Locale language
        ) throws Exception {
            val tournament = tenerifeWomensOpen2025Golf(ENGLISH);
            val tournamentId = Urn.parse(tournament.getTournament().getId());

            apiSimulator.defineBookmaker();
            apiSimulator.activateOnlyLiveProducer();
            apiSimulator.stubAllSports(language);
            apiSimulator.stubSportCategories(
                language,
                tournament.getTournament().getSport(),
                SapiCategories.international()
            );
            apiSimulator.stubAllTournaments(language, tournament.getTournament());
            apiSimulator.stubTournamentSummary(
                language,
                tournament,
                toBeDelayedBy(MORE_THAN_FAST_FAILING_BUT_LESS_THAN_NORMAL)
            );

            try (
                val sdk = SdkSetup
                    .with(sdkCredentials, RABBIT_BASE_URL, sportsApiBaseUrl, globalVariables.getNodeId())
                    .with(ListenerCollectingMessages.to(messagesStorage))
                    .with(ExceptionHandlingStrategy.Throw)
                    .withDefaultLanguage(language)
                    .withClientFastFailingTimeout((int) FAST_FAILING_TIMEOUT.getSeconds())
                    .withClientTimeout((int) NORMAL_TIMEOUT.getSeconds())
                    .withoutFeed()
            ) {
                val sportDataProvider = sdk.getSportDataProvider();
                val basicTournament = sportEventSupplier.getFrom(sportDataProvider, tournamentId);

                assertThatExceptionOfType(ObjectNotFoundException.class)
                    .isThrownBy(() -> basicTournament.getName(language));
            }
        }

        @ParameterizedTest
        @MethodSource(SEASONS_FROM_PROVIDER)
        void onAttemptToGetSeasonFailsAfterReachingFastTimeout(
            SportEventSupplier<Season> sportEventSupplier,
            Locale language
        ) throws Exception {
            val seasonId = Urn.parse(euro2024TournamentInfo().getSeason().getId());

            apiSimulator.defineBookmaker();
            apiSimulator.activateOnlyLiveProducer();
            apiSimulator.stubAllSports(language);
            apiSimulator.stubSportCategories(
                language,
                tournamentEuro2024().getSport(),
                SapiCategories.international()
            );
            apiSimulator.stubAllTournaments(language, tournamentEuro2024());
            apiSimulator.stubSeasonSummary(
                language,
                euro2024TournamentInfo(),
                toBeDelayedBy(MORE_THAN_NORMAL_TIMEOUT)
            );

            try (
                val sdk = SdkSetup
                    .with(sdkCredentials, RABBIT_BASE_URL, sportsApiBaseUrl, globalVariables.getNodeId())
                    .with(ListenerCollectingMessages.to(messagesStorage))
                    .with(ExceptionHandlingStrategy.Throw)
                    .withDefaultLanguage(language)
                    .withClientFastFailingTimeout((int) FAST_FAILING_TIMEOUT.getSeconds())
                    .withClientTimeout((int) NORMAL_TIMEOUT.getSeconds())
                    .withoutFeed()
            ) {
                val sportDataProvider = sdk.getSportDataProvider();
                val season = sportEventSupplier.getFrom(sportDataProvider, seasonId);

                assertThatExceptionOfType(ObjectNotFoundException.class)
                    .isThrownBy(() -> season.getName(language));
            }
        }
    }

    @Nested
    class FeedMessage {

        @Test
        void inFeedMessageContextEvenForClubFriendlyGamesTimesOutAfterReachingFastTimeout() throws Exception {
            globalVariables.setProducer(LIVE_ODDS);
            globalVariables.setSportEventUrn(Urn.parse(clubFriendlyGames(ENGLISH).getTournament().getId()));
            globalVariables.setSportUrn(FOOTBALL);
            val messages = new FeedMessageBuilder(globalVariables);
            val routingKeys = new RoutingKeys(globalVariables);
            apiSimulator.defineBookmaker();
            apiSimulator.activateOnlyLiveProducer();
            apiSimulator.stubAllSports(ENGLISH);
            apiSimulator.stubMarketListContaining(oddEvenMarketDescription(ENGLISH), ENGLISH);
            apiSimulator.stubSportCategories(
                ENGLISH,
                clubFriendlyGames(ENGLISH).getTournament().getSport(),
                SapiCategories.international()
            );
            apiSimulator.stubAllTournaments(ENGLISH, clubFriendlyGames(ENGLISH).getTournament());
            apiSimulator.stubTournamentSummary(
                ENGLISH,
                clubFriendlyGames(ENGLISH),
                toBeDelayedBy(MORE_THAN_FAST_FAILING_BUT_LESS_THAN_NORMAL)
            );

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
                    .with(ExceptionHandlingStrategy.Throw)
                    .withDefaultLanguage(ENGLISH)
                    .withClientFastFailingTimeout((int) FAST_FAILING_TIMEOUT.getSeconds())
                    .withClientTimeout((int) NORMAL_TIMEOUT.getSeconds())
                    .with1Session()
                    .withOpenedFeed()
            ) {
                rabbitProducer.send(messages.oddsChange(oddEvenMarket()), routingKeys.liveOddsChange());

                val oddsChange = listenerWaitingFor.theOnlyOddsChange();

                assertThatExceptionOfType(ObjectNotFoundException.class)
                    .isThrownBy(() -> oddsChange.getEvent().getName(ENGLISH));
            }
        }
    }
}

class Parameters {

    static Stream<Arguments> sportEventGetters() {
        return Stream.of(
            arguments("getSportEventWithoutLocale", ENGLISH, SportDataProvider::getSportEvent),
            arguments(
                "getSportEventWithEnglishLocale",
                ENGLISH,
                (provider, id) -> provider.getSportEvent(id, ENGLISH)
            ),
            arguments(
                "getSportEventWithFrenchLocale",
                FRENCH,
                (provider, id) -> provider.getSportEvent(id, FRENCH)
            )
        );
    }

    static Stream<Arguments> competitionGetters() {
        return Stream.of(
            arguments("getCompetitionWithoutLocale", ENGLISH, SportDataProvider::getCompetition),
            arguments(
                "getCompetitionWithEnglishLocale",
                ENGLISH,
                (provider, id) -> provider.getCompetition(id, ENGLISH)
            ),
            arguments(
                "getCompetitionWithFrenchLocale",
                FRENCH,
                (provider, id) -> provider.getCompetition(id, FRENCH)
            )
        );
    }

    static Stream<Arguments> longTermEventGetters() {
        return Stream.of(
            arguments("getLongTermEventWithoutLocale", ENGLISH, SportDataProvider::getLongTermEvent),
            arguments(
                "getLongTermEventWithEnglishLocale",
                ENGLISH,
                (provider, id) -> provider.getLongTermEvent(id, ENGLISH)
            ),
            arguments(
                "getLongTermEventWithFrenchLocale",
                FRENCH,
                (provider, id) -> provider.getLongTermEvent(id, FRENCH)
            )
        );
    }

    @SuppressWarnings("unused")
    static Stream<Arguments> matchGetters() {
        return Stream.concat(sportEventGetters(), competitionGetters());
    }

    @SuppressWarnings("unused")
    static Stream<Arguments> tournamentGetters() {
        return Stream.concat(sportEventGetters(), longTermEventGetters());
    }

    static Stream<Arguments> stageGetters() {
        return Stream.concat(sportEventGetters(), competitionGetters());
    }

    static Stream<Arguments> seasonGetters() {
        return Stream.concat(sportEventGetters(), longTermEventGetters());
    }

    static <T> Arguments arguments(String name, Locale language, SportEventSupplier<T> supplier) {
        return Arguments.of(Named.named(name, supplier), language);
    }
}

interface SportEventSupplier<T> {
    T getFrom(SportDataProvider sportDataProvider, Urn id);
}
