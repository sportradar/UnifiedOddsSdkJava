/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */

package com.sportradar.unifiedodds.sdk.impl;

import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.wireMockConfig;
import static com.sportradar.unifiedodds.sdk.conn.ApiSimulator.ApiStubDelay.toBeDelayedBy;
import static com.sportradar.unifiedodds.sdk.conn.SapiMatchSummaries.Euro2024.*;
import static com.sportradar.unifiedodds.sdk.conn.UfMarkets.WithOdds.*;
import static com.sportradar.unifiedodds.sdk.conn.marketids.ExactGoalsMarketIds.fivePlusVariant;
import static com.sportradar.unifiedodds.sdk.impl.Constants.*;
import static com.sportradar.unifiedodds.sdk.testutil.rabbit.integration.Credentials.with;
import static com.sportradar.unifiedodds.sdk.testutil.rabbit.integration.RabbitMqClientFactory.createRabbitMqClient;
import static com.sportradar.unifiedodds.sdk.testutil.rabbit.integration.RabbitMqProducer.connectDeclaringExchange;
import static java.time.temporal.ChronoUnit.SECONDS;
import static org.assertj.core.api.Assertions.assertThatException;

import com.github.tomakehurst.wiremock.common.ConsoleNotifier;
import com.github.tomakehurst.wiremock.junit5.WireMockExtension;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.http.client.Client;
import com.sportradar.uf.sportsapi.datamodel.SapiFixturesEndpoint;
import com.sportradar.unifiedodds.sdk.ExceptionHandlingStrategy;
import com.sportradar.unifiedodds.sdk.conn.*;
import com.sportradar.unifiedodds.sdk.conn.RoutingKeys;
import com.sportradar.unifiedodds.sdk.entities.Match;
import com.sportradar.unifiedodds.sdk.exceptions.InitException;
import com.sportradar.unifiedodds.sdk.exceptions.ObjectNotFoundException;
import com.sportradar.unifiedodds.sdk.internal.impl.TimeUtilsImpl;
import com.sportradar.unifiedodds.sdk.shared.FeedMessageBuilder;
import com.sportradar.unifiedodds.sdk.testutil.rabbit.integration.*;
import com.sportradar.utils.Urn;
import java.io.IOException;
import java.util.Locale;
import java.util.concurrent.TimeoutException;
import lombok.val;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;

@SuppressWarnings({ "ClassDataAbstractionCoupling", "ClassFanOutComplexity" })
public class FastFailingTimeoutIT {

    public static final int HTTP_TIMEOUT = 5;

    @RegisterExtension
    private static final WireMockExtension WIRE_MOCK = WireMockExtension
        .newInstance()
        .options(wireMockConfig().dynamicPort().notifier(new ConsoleNotifier(true)))
        .build();

    private final GlobalVariables globalVariables = new GlobalVariables();
    private final ApiSimulator apiSimulator = new ApiSimulator(WIRE_MOCK.getRuntimeInfo().getWireMock());

    private final Credentials sdkCredentials = Credentials.with(
        Constants.SDK_USERNAME,
        Constants.SDK_PASSWORD
    );
    private final MessagesInMemoryStorage messagesStorage = new MessagesInMemoryStorage();
    private final Locale enLanguage = Locale.ENGLISH;

    private final VhostLocation vhostLocation = VhostLocation.at(RABBIT_BASE_URL, Constants.UF_VIRTUALHOST);
    private final ExchangeLocation exchangeLocation = ExchangeLocation.at(
        vhostLocation,
        Constants.UF_EXCHANGE
    );
    private final Credentials adminCredentials = Credentials.with(
        Constants.ADMIN_USERNAME,
        Constants.ADMIN_PASSWORD
    );
    private final Client rabbitMqClient = createRabbitMqClient(
        RABBIT_IP,
        with(ADMIN_USERNAME, ADMIN_PASSWORD),
        Client::new
    );

    private final ConnectionFactory factory = new ConnectionFactory();

    private final WaiterForSingleMessage listenerWaitingFor = new WaiterForSingleMessage(messagesStorage);
    private final RabbitMqUserSetup rabbitMqUserSetup = RabbitMqUserSetup.create(
        VhostLocation.at(RABBIT_BASE_URL, UF_VIRTUALHOST),
        rabbitMqClient
    );

    private BaseUrl sportsApiBaseUrl;

    private FastFailingTimeoutIT() throws Exception {}

    @BeforeEach
    void setup() throws Exception {
        rabbitMqUserSetup.setupUser(sdkCredentials);
        sportsApiBaseUrl = BaseUrl.of("localhost", WIRE_MOCK.getPort());
        apiSimulator.defineBookmaker();
        apiSimulator.activateOnlyLiveProducer();
    }

    @AfterEach
    void tearDownProxy() {
        rabbitMqUserSetup.revertChangesMade();
    }

    @Test
    void sportProviderAllSportsHttpCallFailsIfNoResponseIn5Seconds() throws Exception, InitException {
        apiSimulator.stubAllSports(enLanguage, toBeDelayedBy(HTTP_TIMEOUT + 1, SECONDS));
        apiSimulator.stubEmptyAllTournaments(enLanguage);

        try (
            val sdk = SdkSetup
                .with(sdkCredentials, RABBIT_BASE_URL, sportsApiBaseUrl, globalVariables.getNodeId())
                .with(ListenerCollectingMessages.to(messagesStorage))
                .with(ExceptionHandlingStrategy.Throw)
                .withDefaultLanguage(enLanguage)
                .withClientTimeout(HTTP_TIMEOUT)
                .withoutFeed()
        ) {
            val sportDataProvider = sdk.getSportDataProvider();

            assertThatException()
                .isThrownBy(() -> sportDataProvider.getSports(enLanguage))
                .withRootCauseInstanceOf(TimeoutException.class)
                .isInstanceOf(ObjectNotFoundException.class);
        }
    }

    @Test
    void sportProviderMatchSummaryHttpCallFailsIfNoResponseIn5Seconds() throws Exception {
        val germanyVsScotlandMatchId = Urn.parse(GERMANY_SCOTLAND_MATCH_URN);
        apiSimulator.stubMatchSummary(
            enLanguage,
            soccerMatchGermanyScotlandEuro2024(),
            toBeDelayedBy(HTTP_TIMEOUT + 1, SECONDS)
        );

        try (
            val sdk = SdkSetup
                .with(sdkCredentials, RABBIT_BASE_URL, sportsApiBaseUrl, globalVariables.getNodeId())
                .with(ListenerCollectingMessages.to(messagesStorage))
                .with(ExceptionHandlingStrategy.Throw)
                .withDefaultLanguage(enLanguage)
                .withClientFastFailingTimeout(HTTP_TIMEOUT)
                .withoutFeed()
        ) {
            val sportDataProvider = sdk.getSportDataProvider();

            assertThatException()
                .isThrownBy(() -> sportDataProvider.getSportEvent(germanyVsScotlandMatchId))
                .withRootCauseInstanceOf(TimeoutException.class)
                .isInstanceOf(ObjectNotFoundException.class);
        }
    }

    @Test
    void matchSummaryHttpCallFailsIfNoResponseIn5Seconds()
        throws IOException, TimeoutException, InitException {
        val matchId = Urn.parse(soccerMatchGermanyScotlandEuro2024().getSportEvent().getId());
        globalVariables.setProducer(ProducerId.LIVE_ODDS);
        globalVariables.setSportEventUrn(matchId);
        globalVariables.setSportUrn(Sport.FOOTBALL);

        apiSimulator.activateOnlyLiveProducer();
        stubSdkInternallyTriggeredApiCallsToAvoidFailedRequests();
        apiSimulator.stubMatchSummary(
            enLanguage,
            soccerMatchGermanyScotlandEuro2024(),
            toBeDelayedBy(HTTP_TIMEOUT + 1, SECONDS)
        );
        FeedMessageBuilder messages = new FeedMessageBuilder(globalVariables);
        RoutingKeys routingKeys = new RoutingKeys(globalVariables);

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
                .withDefaultLanguage(enLanguage)
                .withClientFastFailingTimeout(HTTP_TIMEOUT)
                .with1Session()
                .withOpenedFeed()
        ) {
            rabbitProducer.send(
                messages.oddsChange(exactGoalsMarket(fivePlusVariant())),
                routingKeys.liveOddsChange()
            );

            val oddsChangeMessage = listenerWaitingFor.theOnlyOddsChange();
            val match = (Match) oddsChangeMessage.getEvent();

            assertThatException()
                .isThrownBy(() -> match.getName(enLanguage))
                .withRootCauseInstanceOf(TimeoutException.class)
                .isInstanceOf(ObjectNotFoundException.class);
        }
    }

    @Test
    void matchSummaryFixtureHttpCallFailsIfResponseIn5Seconds()
        throws IOException, TimeoutException, InitException {
        val matchId = Urn.parse(soccerMatchGermanyScotlandEuro2024().getSportEvent().getId());
        globalVariables.setProducer(ProducerId.LIVE_ODDS);
        globalVariables.setSportEventUrn(matchId);
        globalVariables.setSportUrn(Sport.FOOTBALL);

        apiSimulator.activateOnlyLiveProducer();
        stubSdkInternallyTriggeredApiCallsToAvoidFailedRequests();
        apiSimulator.stubSportEventFixtures(
            matchId.toString(),
            enLanguage,
            new SapiFixturesEndpoint(),
            toBeDelayedBy(HTTP_TIMEOUT + 1, SECONDS)
        );

        FeedMessageBuilder messages = new FeedMessageBuilder(globalVariables);
        RoutingKeys routingKeys = new RoutingKeys(globalVariables);

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
                .withDefaultLanguage(enLanguage)
                .withClientTimeout(HTTP_TIMEOUT)
                .with1Session()
                .withOpenedFeed()
        ) {
            rabbitProducer.send(
                messages.oddsChange(exactGoalsMarket(fivePlusVariant())),
                routingKeys.liveOddsChange()
            );

            val oddsChangeMessage = listenerWaitingFor.theOnlyOddsChange();
            val match = (Match) oddsChangeMessage.getEvent();

            assertThatException()
                .isThrownBy(match::getFixture)
                .withRootCauseInstanceOf(TimeoutException.class)
                .isInstanceOf(ObjectNotFoundException.class);
        }
    }

    private void stubSdkInternallyTriggeredApiCallsToAvoidFailedRequests() {
        apiSimulator.stubEmptyMarketList(enLanguage);
        apiSimulator.stubEmptyVariantList(enLanguage);
        apiSimulator.stubEmptyScheduleForNext3Days(enLanguage);
    }
}
