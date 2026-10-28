/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */
package com.sportradar.unifiedodds.sdk.conn;

import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.wireMockConfig;
import static com.sportradar.unifiedodds.sdk.ExceptionHandlingStrategy.Throw;
import static com.sportradar.unifiedodds.sdk.conn.ProducerId.LIVE_ODDS;
import static com.sportradar.unifiedodds.sdk.conn.SapiMarketDescriptions.WinnerCompetitor.winnerCompetitorMarketDescription;
import static com.sportradar.unifiedodds.sdk.conn.SapiStageSummaries.GrandPrix2024.grandPrix2024RaceStageEndpoint;
import static com.sportradar.unifiedodds.sdk.conn.Sport.FORMULA1;
import static com.sportradar.unifiedodds.sdk.conn.UfMarkets.WithOdds.winnerCompetitorMarket;
import static com.sportradar.unifiedodds.sdk.impl.Constants.*;
import static com.sportradar.unifiedodds.sdk.impl.oddsentities.markets.ExpectationTowardsSdkErrorHandlingStrategy.WILL_CATCH_EXCEPTIONS;
import static com.sportradar.unifiedodds.sdk.impl.oddsentities.markets.ExpectationTowardsSdkErrorHandlingStrategy.WILL_THROW_EXCEPTIONS;
import static com.sportradar.unifiedodds.sdk.testutil.rabbit.integration.RabbitMqClientFactory.createRabbitMqClient;
import static com.sportradar.unifiedodds.sdk.testutil.rabbit.integration.RabbitMqProducer.connectDeclaringExchange;
import static java.util.Collections.singletonList;
import static java.util.stream.Collectors.toList;

import com.github.tomakehurst.wiremock.common.ConsoleNotifier;
import com.github.tomakehurst.wiremock.junit5.WireMockExtension;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.http.client.Client;
import com.sportradar.uf.sportsapi.datamodel.SapiStageSummaryEndpoint;
import com.sportradar.uf.sportsapi.datamodel.SapiTeamCompetitor;
import com.sportradar.unifiedodds.sdk.ExceptionHandlingStrategy;
import com.sportradar.unifiedodds.sdk.exceptions.ObjectNotFoundException;
import com.sportradar.unifiedodds.sdk.impl.Constants;
import com.sportradar.unifiedodds.sdk.impl.oddsentities.markets.ExpectationTowardsSdkErrorHandlingStrategy;
import com.sportradar.unifiedodds.sdk.internal.impl.TimeUtilsImpl;
import com.sportradar.unifiedodds.sdk.oddsentities.MarketWithOdds;
import com.sportradar.unifiedodds.sdk.oddsentities.OddsChange;
import com.sportradar.unifiedodds.sdk.oddsentities.Outcome;
import com.sportradar.unifiedodds.sdk.shared.FeedMessageBuilder;
import com.sportradar.unifiedodds.sdk.testutil.rabbit.integration.*;
import com.sportradar.utils.Urn;
import java.util.List;
import java.util.Locale;
import lombok.val;
import org.assertj.core.api.Assertions;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

@SuppressWarnings({ "ClassDataAbstractionCoupling", "ClassFanOutComplexity" })
class MarketWithCompetitorOutcomeTypeIT {

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

    private BaseUrl sportsApiBaseUrl;

    private MarketWithCompetitorOutcomeTypeIT() throws Exception {}

    @BeforeEach
    void setup() throws Exception {
        rabbitMqUserSetup.setupUser(sdkCredentials);
        sportsApiBaseUrl = BaseUrl.of("localhost", wireMock.getPort());
    }

    @AfterEach
    void tearDownProxy() {
        rabbitMqUserSetup.revertChangesMade();
    }

    @Test
    void generatesOutcomesFromRaceCompetitors() throws Exception {
        val f1Race = grandPrix2024RaceStageEndpoint();
        globalVariables.setProducer(LIVE_ODDS);
        globalVariables.setSportEventUrn(f1Race.getSportEvent());
        globalVariables.setSportUrn(FORMULA1);
        FeedMessageBuilder messages = new FeedMessageBuilder(globalVariables);
        Locale aLanguage = Locale.ENGLISH;
        RoutingKeys routingKeys = new RoutingKeys(globalVariables);

        apiSimulator.defineBookmaker();
        apiSimulator.activateOnlyLiveProducer();
        apiSimulator.stubMarketListContaining(winnerCompetitorMarketDescription(), aLanguage);
        SapiCompetitorProfiles
            .profilesFromSapiStageSummary(f1Race)
            .forEach(c -> apiSimulator.stubCompetitorProfile(aLanguage, c));

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
                .with(Throw)
                .withDefaultLanguage(aLanguage)
                .with1Session()
                .withOpenedFeed()
        ) {
            val f1RaceCompetitors = f1Race.getSportEvent().getCompetitors().getCompetitor();
            val f1RaceCompetitorIds = getRaceCompetitorIds(f1Race);
            rabbitProducer.send(
                messages.oddsChange(winnerCompetitorMarket(f1RaceCompetitorIds)),
                routingKeys.liveOddsChange()
            );

            val market = theOnlyMarketIn(listenerWaitingFor.theOnlyOddsChange());

            val marketOutcomeNames = market.getOutcomeOdds().stream().map(Outcome::getName).collect(toList());
            val f1raceCompetitorNames = f1RaceCompetitors
                .stream()
                .map(SapiTeamCompetitor::getName)
                .collect(toList());

            Assertions.assertThat(marketOutcomeNames).isNotEmpty().isEqualTo(f1raceCompetitorNames);
        }
    }

    @ParameterizedTest
    @MethodSource("exceptionHandlingStrategies")
    void uponFailedCompetitorProfileCallFetchingFirstOutcomeNameRespectsExceptionHandlingStrategy(
        ExceptionHandlingStrategy exceptionHandlingStrategy,
        ExpectationTowardsSdkErrorHandlingStrategy willFailRespectingSdkStrategy
    ) throws Exception {
        val f1Race = grandPrix2024RaceStageEndpoint();
        globalVariables.setProducer(LIVE_ODDS);
        globalVariables.setSportEventUrn(f1Race.getSportEvent());
        globalVariables.setSportUrn(FORMULA1);
        FeedMessageBuilder messages = new FeedMessageBuilder(globalVariables);
        Locale aLanguage = Locale.ENGLISH;
        RoutingKeys routingKeys = new RoutingKeys(globalVariables);
        val firstCompetitorId = getRaceCompetitorIds(f1Race).get(0);

        apiSimulator.defineBookmaker();
        apiSimulator.activateOnlyLiveProducer();
        apiSimulator.stubMarketListContaining(winnerCompetitorMarketDescription(), aLanguage);
        apiSimulator.stubCompetitorProfileNotFound(aLanguage, Urn.parse(firstCompetitorId));

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
                .with(exceptionHandlingStrategy)
                .withDefaultLanguage(aLanguage)
                .with1Session()
                .withOpenedFeed()
        ) {
            rabbitProducer.send(
                messages.oddsChange(winnerCompetitorMarket(getRaceCompetitorIds(f1Race))),
                routingKeys.liveOddsChange()
            );

            val market = theOnlyMarketIn(listenerWaitingFor.theOnlyOddsChange());
            val firstOutcome = market.getOutcomeOdds().get(0);

            Assertions.assertThat(firstOutcome.getId()).isEqualTo(firstCompetitorId);
            getNameForGiven(firstOutcome, aLanguage, willFailRespectingSdkStrategy);
            getNamesForGiven(firstOutcome, aLanguage, willFailRespectingSdkStrategy);
        }
    }

    private void getNameForGiven(
        Outcome outcome,
        Locale aLanguage,
        ExpectationTowardsSdkErrorHandlingStrategy willFailRespectingSdkStrategy
    ) {
        if (willFailRespectingSdkStrategy == WILL_THROW_EXCEPTIONS) {
            resultsInObjectNotFoundWhen(() -> outcome.getName(aLanguage));
        } else {
            Assertions.assertThat(outcome.getName(aLanguage)).isEqualTo("null");
        }
    }

    private void getNamesForGiven(
        Outcome outcome,
        Locale aLanguage,
        ExpectationTowardsSdkErrorHandlingStrategy willFailRespectingSdkStrategy
    ) {
        if (willFailRespectingSdkStrategy == WILL_THROW_EXCEPTIONS) {
            resultsInObjectNotFoundWhen(() -> outcome.getNames(singletonList(aLanguage)));
        } else {
            Assertions
                .assertThat(outcome.getNames(singletonList(aLanguage)))
                .containsEntry(aLanguage, "null");
        }
    }

    private void resultsInObjectNotFoundWhen(Runnable runnable) {
        Assertions.assertThatThrownBy(runnable::run).isInstanceOf(ObjectNotFoundException.class);
    }

    private static Object[] exceptionHandlingStrategies() {
        return new Object[][] {
            { Throw, WILL_THROW_EXCEPTIONS },
            { ExceptionHandlingStrategy.Catch, WILL_CATCH_EXCEPTIONS },
        };
    }

    private MarketWithOdds theOnlyMarketIn(
        OddsChange<com.sportradar.unifiedodds.sdk.entities.SportEvent> oddsChange
    ) {
        Assertions.assertThat(oddsChange.getMarkets()).hasSize(1);
        return oddsChange.getMarkets().get(0);
    }

    @NotNull
    private static List<String> getRaceCompetitorIds(SapiStageSummaryEndpoint f1Race) {
        return f1Race
            .getSportEvent()
            .getCompetitors()
            .getCompetitor()
            .stream()
            .map(SapiTeamCompetitor::getId)
            .collect(toList());
    }
}
