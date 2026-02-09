/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */
package com.sportradar.unifiedodds.sdk.conn;

import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.wireMockConfig;
import static com.sportradar.unifiedodds.sdk.ExceptionHandlingStrategy.Throw;
import static com.sportradar.unifiedodds.sdk.conn.ApiSimulator.ApiStubDelay.toBeDelayedBy;
import static com.sportradar.unifiedodds.sdk.conn.ApiSimulator.HeaderEquality.requiringAuthorizationHeader;
import static com.sportradar.unifiedodds.sdk.conn.CommonIamTokens.*;
import static com.sportradar.unifiedodds.sdk.conn.SapiFixtureChanges.fixtureChanges;
import static com.sportradar.unifiedodds.sdk.conn.SapiMatchSummaries.Euro2024.*;
import static com.sportradar.unifiedodds.sdk.conn.SapiPlayerProfiles.kaiHavertzProfile;
import static com.sportradar.unifiedodds.sdk.conn.SapiSimpleTeams.EnderunTitansCollegeBasketballTeam.sapiEnderunTitansTeam;
import static com.sportradar.unifiedodds.sdk.conn.SapiSports.allSports;
import static com.sportradar.unifiedodds.sdk.conn.SapiTournaments.tournamentEuro2024;
import static com.sportradar.unifiedodds.sdk.conn.UfMarkets.WithOdds.oddEvenMarket;
import static com.sportradar.unifiedodds.sdk.impl.Constants.*;
import static com.sportradar.unifiedodds.sdk.impl.oddsentities.markets.SportEventAssert.assertThat;
import static com.sportradar.unifiedodds.sdk.testutil.rabbit.integration.Credentials.with;
import static com.sportradar.unifiedodds.sdk.testutil.rabbit.integration.RabbitMqClientFactory.createRabbitMqClient;
import static com.sportradar.unifiedodds.sdk.testutil.rabbit.integration.RabbitMqProducer.connectDeclaringExchange;
import static com.sportradar.utils.domain.names.LanguageHolder.in;
import static com.sportradar.utils.domain.names.TranslationHolder.of;
import static java.util.Collections.singletonList;
import static java.util.Locale.ENGLISH;
import static org.assertj.core.api.Assertions.assertThatException;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.awaitility.Awaitility.await;

import com.github.tomakehurst.wiremock.common.ConsoleNotifier;
import com.github.tomakehurst.wiremock.junit5.WireMockExtension;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.http.client.Client;
import com.sportradar.uf.sportsapi.datamodel.SapiMatchSummaryEndpoint;
import com.sportradar.unifiedodds.sdk.CapiCustomBet;
import com.sportradar.unifiedodds.sdk.ExceptionHandlingStrategy;
import com.sportradar.unifiedodds.sdk.MessageInterest;
import com.sportradar.unifiedodds.sdk.UofSdk;
import com.sportradar.unifiedodds.sdk.cfg.UofConfiguration;
import com.sportradar.unifiedodds.sdk.conn.marketids.OddEvenMarketIds;
import com.sportradar.unifiedodds.sdk.entities.custombet.Selection;
import com.sportradar.unifiedodds.sdk.exceptions.ObjectNotFoundException;
import com.sportradar.unifiedodds.sdk.impl.Constants;
import com.sportradar.unifiedodds.sdk.impl.assertions.SportListAssert;
import com.sportradar.unifiedodds.sdk.internal.commoniam.OAuth2TokenCache.OAuth2TokenCacheException;
import com.sportradar.unifiedodds.sdk.internal.impl.TimeUtilsImpl;
import com.sportradar.unifiedodds.sdk.managers.CustomBetManager;
import com.sportradar.unifiedodds.sdk.managers.SportDataProvider;
import com.sportradar.unifiedodds.sdk.oddsentities.Producer;
import com.sportradar.unifiedodds.sdk.shared.FeedMessageBuilder;
import com.sportradar.unifiedodds.sdk.testutil.rabbit.integration.*;
import com.sportradar.utils.Urn;
import com.sportradar.utils.Urns;
import java.time.temporal.ChronoUnit;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicReference;
import lombok.val;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;

@SuppressWarnings({ "ClassFanOutComplexity", "ClassDataAbstractionCoupling", "MagicNumber" })
class CommonIamTokenCacheIT {

    @RegisterExtension
    private static WireMockExtension wireMock = WireMockExtension
        .newInstance()
        .options(wireMockConfig().dynamicPort().notifier(new ConsoleNotifier(true)))
        .build();

    @RegisterExtension
    private static WireMockExtension commonIamWireMock = WireMockExtension
        .newInstance()
        .options(wireMockConfig().dynamicPort().notifier(new ConsoleNotifier(true)))
        .build();

    private static final Urn ANY_MATCH = Urn.parse("sr:match:12345");
    private final ScheduledExecutorService executor = Executors.newScheduledThreadPool(1);
    private final int oneForRabbitAndOneForApi = 2;
    private final String bookmakerIdOf1 = "1";
    private final GlobalVariables globalVariables = new GlobalVariables();
    private final ApiSimulator apiSimulator = new ApiSimulator(wireMock.getRuntimeInfo().getWireMock());
    private final MessagesInMemoryStorage messagesStorage = new MessagesInMemoryStorage();

    private final CommonIamData commonIamData = CommonIamData.with(
        Constants.COMMON_IAM_CLIENT_ID,
        Constants.COMMON_IAM_KEY_ID,
        Constants.COMMON_IAM_PRIVATE_KEY
    );

    private final Client rabbitMqClient = createRabbitMqClient(
        RABBIT_IP,
        with(ADMIN_USERNAME, ADMIN_PASSWORD),
        Client::new
    );
    private final RabbitMqUserSetup rabbitMqUserSetup = RabbitMqUserSetup.create(
        VhostLocation.at(RABBIT_BASE_URL, UF_VIRTUALHOST),
        rabbitMqClient
    );
    private final RabbitConnections connections = new RabbitConnections(rabbitMqClient);

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

    private BaseUrl sportsApiBaseUrl;
    private BaseUrl commonIamApiBaseUrl;
    private CommonIamSimulator commonIamSimulator;

    CommonIamTokenCacheIT() throws Exception {}

    @BeforeEach
    void setup() throws Exception {
        sportsApiBaseUrl = BaseUrl.of("localhost", wireMock.getPort());
        commonIamApiBaseUrl = BaseUrl.of("localhost", commonIamWireMock.getPort());
        commonIamSimulator = new CommonIamSimulator(commonIamWireMock.getRuntimeInfo().getWireMock());
    }

    @AfterEach
    void tearDown() {
        rabbitMqUserSetup.revertChangesMade();
        executor.shutdownNow();
    }

    @Test
    void dueToDesignFlawBuildingConfigurationRequests2TokensMeanwhileSdkStartsWithCleanCacheToo()
        throws Exception {
        rabbitMqUserSetup.setupUser(
            Credentials.with(bookmakerIdOf1, anotherValidCommonIamToken().getAccessToken())
        );

        globalVariables.setProducer(ProducerId.LIVE_ODDS);

        apiSimulator.defineBookmakerWithIdOf1(
            requiringAuthorizationHeader(validCommonIamToken().getHeaderValue())
        );
        apiSimulator.activateOnlyLiveProducer();
        apiSimulator.stubEmptyAllTournaments(ENGLISH);
        apiSimulator.stubAllSports(
            ENGLISH,
            requiringAuthorizationHeader(validCommonIamToken().getHeaderValue())
        );

        commonIamSimulator.stubTokenEndpointForApi(validCommonIamToken());
        commonIamSimulator.stubTokenEndpointForRabbit(anotherValidCommonIamToken());

        AtomicReference<UofConfiguration> config = new AtomicReference<>();
        commonIamSimulator.verifyTokenEndpointCalled2TimesDuringConfiguration(() ->
            config.set(
                SdkSetup
                    .withCommonIam(RABBIT_BASE_URL, sportsApiBaseUrl, globalVariables.getNodeId())
                    .with(ListenerCollectingMessages.to(messagesStorage))
                    .withCommonIamCredentials(commonIamData)
                    .withCommonIamApiBaseUrl(commonIamApiBaseUrl)
                    .with(Throw)
                    .withDefaultLanguage(ENGLISH)
                    .buildConfigurationWithoutSdk()
            )
        );

        try (
            val rabbitProducer = connectDeclaringExchange(
                exchangeLocation,
                adminCredentials,
                factory,
                new TimeUtilsImpl()
            );
            val sdk = new UofSdk(new NoOpUofGlobalEventsListener(), config.get());
        ) {
            sdk
                .getSessionBuilder()
                .setListener(new NoOpUofListener())
                .setMessageInterest(MessageInterest.AllMessages)
                .build();
            sdk.open();
            sdk.getSportDataProvider().getSports(ENGLISH);

            commonIamSimulator.verifyAfterSdkStartupTokenEndpointCalledTimes(oneForRabbitAndOneForApi);
        }
    }

    @Test
    void uponReceivingFeedMessageRetrievesSportEvenSummaryWithCommonIamToken() throws Exception {
        rabbitMqUserSetup.setupUser(
            Credentials.with(bookmakerIdOf1, anotherValidCommonIamToken().getAccessToken())
        );
        val id = Urn.parse(soccerMatchGermanyScotlandEuro2024().getSportEvent().getId());
        globalVariables.setProducer(ProducerId.LIVE_ODDS);
        globalVariables.setSportEventUrn(id);
        globalVariables.setSportUrn(Sport.FOOTBALL);
        val messages = new FeedMessageBuilder(globalVariables);
        val routingKeys = new RoutingKeys(globalVariables);

        apiSimulator.defineBookmakerWithIdOf1(
            requiringAuthorizationHeader(validCommonIamToken().getHeaderValue())
        );
        apiSimulator.activateOnlyLiveProducer();
        apiSimulator.stubEmptyAllTournaments(ENGLISH);
        apiSimulator.stubMatchSummary(
            ENGLISH,
            soccerMatchGermanyScotlandEuro2024(ENGLISH),
            requiringAuthorizationHeader(validCommonIamToken().getHeaderValue())
        );

        commonIamSimulator.stubTokenEndpointForApi(validCommonIamToken());

        commonIamSimulator.stubTokenEndpointForRabbit(anotherValidCommonIamToken());

        try (
            val rabbitProducer = connectDeclaringExchange(
                exchangeLocation,
                adminCredentials,
                factory,
                new TimeUtilsImpl()
            );
            val sdk = SdkSetup
                .withCommonIam(RABBIT_BASE_URL, sportsApiBaseUrl, globalVariables.getNodeId())
                .with(ListenerCollectingMessages.to(messagesStorage))
                .withCommonIamCredentials(commonIamData)
                .withCommonIamApiBaseUrl(commonIamApiBaseUrl)
                .with(Throw)
                .withDefaultLanguage(ENGLISH)
                .with1Session()
                .withOpenedFeed();
        ) {
            rabbitProducer.send(messages.oddsChange(oddEvenMarket()), routingKeys.liveOddsChange());

            val oddsChange = listenerWaitingFor.theOnlyOddsChange();

            val event = oddsChange.getEvent();

            assertThat(event)
                .hasName(of(matchName(soccerMatchGermanyScotlandEuro2024(ENGLISH)), in(ENGLISH)));

            commonIamSimulator.verifyAfterSdkStartupTokenEndpointCalledTimes(oneForRabbitAndOneForApi);
        }
    }

    @Test
    void rabbitTokenIsCachedAndReusedForSubsequentReconnections() throws Exception {
        rabbitMqUserSetup.setupUser(
            Credentials.with(bookmakerIdOf1, anotherValidCommonIamToken().getAccessToken())
        );
        val id = Urn.parse(soccerMatchGermanyScotlandEuro2024().getSportEvent().getId());
        globalVariables.setProducer(ProducerId.LIVE_ODDS);
        globalVariables.setSportEventUrn(id);
        globalVariables.setSportUrn(Sport.FOOTBALL);
        val messages = new FeedMessageBuilder(globalVariables);
        val routingKeys = new RoutingKeys(globalVariables);

        apiSimulator.defineBookmakerWithIdOf1(
            requiringAuthorizationHeader(validCommonIamToken().getHeaderValue())
        );
        apiSimulator.activateOnlyLiveProducer();
        apiSimulator.stubEmptyAllTournaments(ENGLISH);
        apiSimulator.stubMatchSummary(
            ENGLISH,
            soccerMatchGermanyScotlandEuro2024(ENGLISH),
            requiringAuthorizationHeader(validCommonIamToken().getHeaderValue())
        );

        commonIamSimulator.stubTokenEndpointForApi(validCommonIamToken());

        commonIamSimulator.stubTokenEndpointForRabbit(anotherValidCommonIamToken());

        try (
            val rabbitProducer = connectDeclaringExchange(
                exchangeLocation,
                adminCredentials,
                factory,
                new TimeUtilsImpl()
            );
            val sdk = SdkSetup
                .withCommonIam(RABBIT_BASE_URL, sportsApiBaseUrl, globalVariables.getNodeId())
                .with(ListenerCollectingMessages.to(messagesStorage))
                .withCommonIamCredentials(commonIamData)
                .withCommonIamApiBaseUrl(commonIamApiBaseUrl)
                .with(Throw)
                .withDefaultLanguage(ENGLISH)
                .with1Session()
                .withOpenedFeed();
        ) {
            rabbitProducer.send(messages.oddsChange(oddEvenMarket()), routingKeys.liveOddsChange());
            listenerWaitingFor.theOnlyOddsChange();

            connections.killExistingConnectionForUser(bookmakerIdOf1);

            executor.scheduleAtFixedRate(
                () -> rabbitProducer.send(messages.oddsChange(oddEvenMarket()), routingKeys.liveOddsChange()),
                1,
                500,
                TimeUnit.MILLISECONDS
            );

            listenerWaitingFor.secondOddsChange();

            commonIamSimulator.verifyAfterSdkStartupTokenEndpointCalledTimes(oneForRabbitAndOneForApi);
        }
    }

    @Test
    void rabbitTokenIsRotatedAfterExpiryForSubsequentReconnections() throws Exception {
        rabbitMqUserSetup.setupUser(
            Credentials.with(bookmakerIdOf1, immediatelyExpiredCommonIamToken().getAccessToken())
        );
        val id = Urn.parse(soccerMatchGermanyScotlandEuro2024().getSportEvent().getId());
        globalVariables.setProducer(ProducerId.LIVE_ODDS);
        globalVariables.setSportEventUrn(id);
        globalVariables.setSportUrn(Sport.FOOTBALL);
        val messages = new FeedMessageBuilder(globalVariables);
        val routingKeys = new RoutingKeys(globalVariables);

        apiSimulator.defineBookmakerWithIdOf1(
            requiringAuthorizationHeader(validCommonIamToken().getHeaderValue())
        );
        apiSimulator.activateOnlyLiveProducer();
        apiSimulator.stubEmptyAllTournaments(ENGLISH);
        apiSimulator.stubMatchSummary(
            ENGLISH,
            soccerMatchGermanyScotlandEuro2024(ENGLISH),
            requiringAuthorizationHeader(validCommonIamToken().getHeaderValue())
        );

        commonIamSimulator.stubTokenEndpointForApi(validCommonIamToken());

        commonIamSimulator.stubTokenEndpointForRabbit(immediatelyExpiredCommonIamToken());

        try (
            val rabbitProducer = connectDeclaringExchange(
                exchangeLocation,
                adminCredentials,
                factory,
                new TimeUtilsImpl()
            );
            val sdk = SdkSetup
                .withCommonIam(RABBIT_BASE_URL, sportsApiBaseUrl, globalVariables.getNodeId())
                .with(ListenerCollectingMessages.to(messagesStorage))
                .withCommonIamCredentials(commonIamData)
                .withCommonIamApiBaseUrl(commonIamApiBaseUrl)
                .with(Throw)
                .withDefaultLanguage(ENGLISH)
                .with1Session()
                .withOpenedFeed();
        ) {
            rabbitProducer.send(messages.oddsChange(oddEvenMarket()), routingKeys.liveOddsChange());
            listenerWaitingFor.theOnlyOddsChange();

            commonIamSimulator.stubTokenEndpointForRabbit(anotherValidCommonIamToken());
            rabbitMqUserSetup.updateUser(
                Credentials.with(bookmakerIdOf1, anotherValidCommonIamToken().getAccessToken())
            );

            connections.killExistingConnectionForUser(bookmakerIdOf1);

            executor.scheduleAtFixedRate(
                () -> rabbitProducer.send(messages.oddsChange(oddEvenMarket()), routingKeys.liveOddsChange()),
                1,
                500,
                TimeUnit.MILLISECONDS
            );

            listenerWaitingFor.secondOddsChange();

            val twoForRabbitOneForApi = 3;
            commonIamSimulator.verifyAfterSdkStartupTokenEndpointCalledTimes(twoForRabbitOneForApi);
        }
    }

    @Test
    void retrievesDataFromSportDataProviderWithCommonIamToken() throws Exception {
        globalVariables.setProducer(ProducerId.LIVE_ODDS);

        commonIamSimulator.stubTokenEndpointForApi(validCommonIamToken());
        apiSimulator.defineBookmaker(requiringAuthorizationHeader(validCommonIamToken().getHeaderValue()));
        apiSimulator.activateOnlyLiveProducer();
        apiSimulator.stubEmptyAllTournaments(ENGLISH);
        apiSimulator.stubAllSports(
            ENGLISH,
            requiringAuthorizationHeader(validCommonIamToken().getHeaderValue())
        );

        try (
            val sdk = SdkSetup
                .withCommonIam(RABBIT_BASE_URL, sportsApiBaseUrl, globalVariables.getNodeId())
                .with(ListenerCollectingMessages.to(messagesStorage))
                .withCommonIamCredentials(commonIamData)
                .withCommonIamApiBaseUrl(commonIamApiBaseUrl)
                .with(Throw)
                .withDefaultLanguage(ENGLISH)
                .withoutFeed()
        ) {
            val sports = sdk.getSportDataProvider().getSports(ENGLISH);

            SportListAssert
                .assertThat(sports, in(ENGLISH))
                .containsExactlyAllElementsInAnyOrderComparingIdAndName(allSports());
        }
    }

    @Test
    void apiTokenIsCachedAndReusedForSubsequentRecoveryApiCalls() throws Exception {
        rabbitMqUserSetup.setupUser(
            Credentials.with(bookmakerIdOf1, anotherValidCommonIamToken().getAccessToken())
        );

        val messages = new FeedMessageBuilder(globalVariables);
        val routingKeys = new RoutingKeys(globalVariables);

        commonIamSimulator.stubTokenEndpointForApi(validCommonIamToken());
        commonIamSimulator.stubTokenEndpointForRabbit(anotherValidCommonIamToken());
        apiSimulator.defineBookmakerWithIdOf1(
            requiringAuthorizationHeader(validCommonIamToken().getHeaderValue())
        );
        List<ProducerId> tenProducerIds = tenProducers();
        apiSimulator.activateProducers(tenProducerIds);
        tenProducerIds.forEach(apiSimulator::stubRecovery);

        try (
            val rabbitProducer = connectDeclaringExchange(
                exchangeLocation,
                adminCredentials,
                factory,
                new TimeUtilsImpl()
            );
            val sdk = SdkSetup
                .withCommonIam(RABBIT_BASE_URL, sportsApiBaseUrl, globalVariables.getNodeId())
                .withCommonIamCredentials(commonIamData)
                .withCommonIamApiBaseUrl(commonIamApiBaseUrl)
                .with(Throw)
                .withDefaultLanguage(ENGLISH)
                .with1Session()
                .withOpenedFeed();
        ) {
            tenProducerIds.forEach(producerId -> {
                globalVariables.setProducer(producerId);
                rabbitProducer.send(messages.alive(), routingKeys.alive());
            });
            tenProducerIds.forEach(producerId ->
                awaitUntil(() -> apiSimulator.isRecoveryEverRequestedFor(producerId))
            );

            int expectedApiCallCount = 10;
            apiSimulator.verifyTotalCallsAtLeast(expectedApiCallCount);

            commonIamSimulator.verifyAfterSdkStartupTokenEndpointCalledTimes(oneForRabbitAndOneForApi);
        }
    }

    private static List<ProducerId> tenProducers() {
        return Arrays.asList(
            ProducerId.LIVE_ODDS,
            ProducerId.BETRADAR_CTRL,
            ProducerId.BETPAL,
            ProducerId.PREMIUM_CRICKET,
            ProducerId.VIRTUAL_FOOTBALL,
            ProducerId.NUMBERS_BETTING,
            ProducerId.VIRTUAL_BASKETBALL,
            ProducerId.VIRTUAL_TENNIS_OPEN,
            ProducerId.VIRTUAL_DOG_RACING,
            ProducerId.VIRTUAL_HORSE_RACING
        );
    }

    private void awaitUntil(Callable<Boolean> predicate) {
        int testSecondsForSlowMachines = 10;
        await().atMost(testSecondsForSlowMachines, TimeUnit.SECONDS).until(predicate::call);
    }

    @Test
    void apiTokenIsCachedAndReusedForSubsequentSportsApiCalls() throws Exception {
        globalVariables.setProducer(ProducerId.LIVE_ODDS);

        commonIamSimulator.stubTokenEndpointForApi(validCommonIamToken());
        apiSimulator.defineBookmaker(requiringAuthorizationHeader(validCommonIamToken().getHeaderValue()));
        apiSimulator.activateOnlyLiveProducer();
        apiSimulator.stubEmptyAllTournaments(ENGLISH);
        apiSimulator.stubAllSports(ENGLISH);
        apiSimulator.stubMatchSummary(
            ENGLISH,
            soccerMatchGermanyScotlandEuro2024(ENGLISH),
            requiringAuthorizationHeader(validCommonIamToken().getHeaderValue())
        );

        try (
            val sdk = SdkSetup
                .withCommonIam(RABBIT_BASE_URL, sportsApiBaseUrl, globalVariables.getNodeId())
                .with(ListenerCollectingMessages.to(messagesStorage))
                .withCommonIamCredentials(commonIamData)
                .withCommonIamApiBaseUrl(commonIamApiBaseUrl)
                .with(Throw)
                .withDefaultLanguage(ENGLISH)
                .withoutFeed()
        ) {
            val sportsProvider = sdk.getSportDataProvider();

            int expectedApiCallCount = 20;
            repeatTimes(expectedApiCallCount, getSportEventAndImmediatelyRemoveFromCache(sportsProvider));

            commonIamSimulator.verifyAfterSdkStartupTokenEndpointCalledOnce();
        }
    }

    @Test
    @SuppressWarnings("ExecutableStatementCount")
    void apiTokenIsCachedAndReusedForDifferentApiCalls() throws Exception {
        rabbitMqUserSetup.setupUser(
            Credentials.with(bookmakerIdOf1, anotherValidCommonIamToken().getAccessToken())
        );

        Urn germanyVsScotlandMatchUrn = Urn.parse(GERMANY_SCOTLAND_MATCH_URN);
        globalVariables.setProducer(ProducerId.LIVE_ODDS);
        globalVariables.setSportEventUrn(germanyVsScotlandMatchUrn);
        globalVariables.setSportUrn(Sport.FOOTBALL);

        commonIamSimulator.stubTokenEndpointForApi(validCommonIamToken());
        commonIamSimulator.stubTokenEndpointForRabbit(anotherValidCommonIamToken());
        apiSimulator.defineBookmakerWithIdOf1(
            requiringAuthorizationHeader(validCommonIamToken().getHeaderValue())
        );
        apiSimulator.activateOnlyLiveProducer();
        apiSimulator.stubAllTournaments(ENGLISH, tournamentEuro2024());
        apiSimulator.stubAllSports(ENGLISH);
        val matchSummary = soccerMatchGermanyScotlandEuro2024(ENGLISH);
        apiSimulator.stubMatchSummary(
            ENGLISH,
            matchSummary,
            requiringAuthorizationHeader(validCommonIamToken().getHeaderValue())
        );
        apiSimulator.stubCompetitorProfile(ENGLISH, sapiEnderunTitansTeam());
        apiSimulator.stubCustomBetCalculate(CapiCustomBet.getCalculationWithHarmonization(true));
        apiSimulator.stubCustomBetCalculateFilter(CapiCustomBet.getCalculationFilterWithHarmonization(true));
        apiSimulator.stubPlayerProfile(ENGLISH, kaiHavertzProfile());
        apiSimulator.stubEventOddsRecovery(matchSummary.getSportEvent().getId());
        apiSimulator.stubEventStatefulRecovery(matchSummary.getSportEvent().getId());
        apiSimulator.stubFixtureChanges(ENGLISH, fixtureChanges());

        try (
            val rabbitProducer = connectDeclaringExchange(
                exchangeLocation,
                adminCredentials,
                factory,
                new TimeUtilsImpl()
            );
            val sdk = SdkSetup
                .withCommonIam(RABBIT_BASE_URL, sportsApiBaseUrl, globalVariables.getNodeId())
                .with(ListenerCollectingMessages.to(messagesStorage))
                .withCommonIamCredentials(commonIamData)
                .withCommonIamApiBaseUrl(commonIamApiBaseUrl)
                .with(Throw)
                .withDefaultLanguage(ENGLISH)
                .with1Session()
                .withOpenedFeed()
        ) {
            val sportsProvider = sdk.getSportDataProvider();
            val customBetManager = sdk.getCustomBetManager();
            Selection selection = anySelection(customBetManager);

            customBetManager.calculateProbability(singletonList(selection)).getOdds();
            customBetManager.calculateProbabilityFilter(singletonList(selection)).getOdds();
            sportsProvider.getSportEvent(germanyVsScotlandMatchUrn, ENGLISH).getName(ENGLISH);
            sportsProvider
                .getCompetitor(Urn.parse(sapiEnderunTitansTeam().getCompetitor().getId()))
                .getName(ENGLISH);
            sportsProvider.getPlayerProfile(Urn.parse(kaiHavertzProfile().getId())).getName(ENGLISH);

            sdk
                .getEventRecoveryRequestIssuer()
                .initiateEventOddsMessagesRecovery(anyProducer(sdk), germanyVsScotlandMatchUrn);
            sdk
                .getEventRecoveryRequestIssuer()
                .initiateEventOddsMessagesRecovery(anyProducer(sdk), germanyVsScotlandMatchUrn);
            sportsProvider.getFixtureChanges().get(0).getSportEventId();

            int expectedApiCallCount = 12;
            apiSimulator.verifyTotalCallsAtLeast(expectedApiCallCount);

            commonIamSimulator.verifyAfterSdkStartupTokenEndpointCalledTimes(oneForRabbitAndOneForApi);
        }
    }

    private static Producer anyProducer(UofSdk sdk) {
        val producer = sdk.getProducerManager().getAvailableProducers().values().stream().findFirst().get();
        return producer;
    }

    private static Selection anySelection(CustomBetManager customBetManager) {
        Selection selection = customBetManager
            .getCustomBetSelectionBuilder()
            .setEventId(Urns.SportEvents.getForAnyMatch())
            .setMarketId(OddEvenMarketIds.ODD_EVEN_MARKET_ID)
            .setOutcomeId(OddEvenMarketIds.EVEN_OUTCOME_ID)
            .build();
        return selection;
    }

    @Test
    void onAttemptToFetchTokenItFailsAndThrowsObjectNotFoundException() throws Exception {
        globalVariables.setProducer(ProducerId.LIVE_ODDS);

        commonIamSimulator.stubTokenEndpoint(immediatelyExpiredCommonIamToken());
        apiSimulator.defineBookmaker(
            requiringAuthorizationHeader(immediatelyExpiredCommonIamToken().getHeaderValue())
        );
        apiSimulator.activateOnlyLiveProducer();
        apiSimulator.stubEmptyAllTournaments(ENGLISH);
        apiSimulator.stubAllSports(ENGLISH);

        try (
            val sdk = SdkSetup
                .withCommonIam(RABBIT_BASE_URL, sportsApiBaseUrl, globalVariables.getNodeId())
                .with(ListenerCollectingMessages.to(messagesStorage))
                .withCommonIamCredentials(commonIamData)
                .withCommonIamApiBaseUrl(commonIamApiBaseUrl)
                .with(Throw)
                .withDefaultLanguage(ENGLISH)
                .withoutFeed()
        ) {
            val sportsProvider = sdk.getSportDataProvider();

            commonIamSimulator.stubTokenEndpointWithInternalServerErrorResponse();

            assertThatExceptionOfType(ObjectNotFoundException.class)
                .isThrownBy(() -> sportsProvider.getSports(ENGLISH))
                .withRootCauseInstanceOf(OAuth2TokenCacheException.class);
        }
    }

    @Test
    void sportProviderAllSportsHttpCallFailsIfNoResponseIn1Second() throws Exception {
        val fastFailingTimeout = 1;
        apiSimulator.defineBookmaker();
        apiSimulator.activateOnlyLiveProducer();
        apiSimulator.stubAllSports(ENGLISH);
        apiSimulator.stubEmptyAllTournaments(ENGLISH);
        commonIamSimulator.stubTokenEndpoint(immediatelyExpiredCommonIamToken());

        try (
            val sdk = SdkSetup
                .withCommonIam(RABBIT_BASE_URL, sportsApiBaseUrl, globalVariables.getNodeId())
                .with(ListenerCollectingMessages.to(messagesStorage))
                .with(ExceptionHandlingStrategy.Throw)
                .withCommonIamCredentials(commonIamData)
                .withCommonIamApiBaseUrl(commonIamApiBaseUrl)
                .withDefaultLanguage(ENGLISH)
                .withClientFastFailingTimeout(fastFailingTimeout)
                .withoutFeed()
        ) {
            val sportDataProvider = sdk.getSportDataProvider();

            commonIamSimulator.stubTokenEndpoint(
                validCommonIamToken(),
                toBeDelayedBy(fastFailingTimeout + 1, ChronoUnit.SECONDS)
            );

            assertThatException()
                .isThrownBy(() -> sportDataProvider.getSportEvent(ANY_MATCH, ENGLISH))
                .withRootCauseInstanceOf(TimeoutException.class)
                .isInstanceOf(ObjectNotFoundException.class);
        }
    }

    private static Runnable getSportEventAndImmediatelyRemoveFromCache(SportDataProvider sportsProvider) {
        return () -> {
            Urn id = Urn.parse(soccerMatchGermanyScotlandEuro2024().getSportEvent().getId());
            sportsProvider.getSportEvent(id, ENGLISH);
            sportsProvider.purgeSportEventCacheData(id);
        };
    }

    private void repeatTimes(int times, Runnable action) {
        for (int i = 0; i < times; i++) {
            action.run();
        }
    }

    private String matchName(SapiMatchSummaryEndpoint match) {
        val competitors = match.getSportEvent().getCompetitors().getCompetitor();
        val home = competitors.get(0).getName();
        val away = competitors.get(1).getName();
        return home + " vs. " + away;
    }
}
