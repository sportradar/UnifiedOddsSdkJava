/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */
package com.sportradar.unifiedodds.sdk.conn;

import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.wireMockConfig;
import static com.sportradar.unifiedodds.sdk.conn.SapiMatchSummaries.Euro2024.soccerMatchGermanyScotlandEuro2024;
import static com.sportradar.unifiedodds.sdk.conn.SapiTournaments.tournamentEuro2024;
import static com.sportradar.unifiedodds.sdk.conn.UfSportEventStatuses.soccerMatchFeedStatus;
import static com.sportradar.unifiedodds.sdk.conn.UfSportEventStatuses.withEveryStatistic;
import static com.sportradar.unifiedodds.sdk.impl.Constants.*;
import static com.sportradar.unifiedodds.sdk.impl.Constants.UF_VIRTUALHOST;
import static com.sportradar.unifiedodds.sdk.testutil.rabbit.integration.Credentials.with;
import static com.sportradar.unifiedodds.sdk.testutil.rabbit.integration.RabbitMqClientFactory.createRabbitMqClient;
import static com.sportradar.unifiedodds.sdk.testutil.rabbit.integration.RabbitMqProducer.connectDeclaringExchange;

import com.github.tomakehurst.wiremock.junit5.WireMockExtension;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.http.client.Client;
import com.sportradar.uf.datamodel.UfSportEventStatus;
import com.sportradar.uf.sportsapi.datamodel.SapiMatchStatistics;
import com.sportradar.unifiedodds.sdk.ExceptionHandlingStrategy;
import com.sportradar.unifiedodds.sdk.entities.SoccerEvent;
import com.sportradar.unifiedodds.sdk.impl.Constants;
import com.sportradar.unifiedodds.sdk.impl.TimeUtilsImpl;
import com.sportradar.unifiedodds.sdk.shared.FeedMessageBuilder;
import com.sportradar.unifiedodds.sdk.testutil.rabbit.integration.*;
import com.sportradar.utils.Urn;
import java.util.Locale;
import lombok.val;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.RegisterExtension;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

@SuppressWarnings({ "ClassFanOutComplexity", "ClassDataAbstractionCoupling" })
public class SportEventStatisticsIT {

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

    private SportEventStatisticsIT() throws Exception {}

    @BeforeEach
    public void setup() throws Exception {
        rabbitMqUserSetup.setupUser(sdkCredentials);
        sportsApiBaseUrl = BaseUrl.of("localhost", wireMock.getPort());
    }

    @AfterEach
    public void tearDownProxy() {
        rabbitMqUserSetup.revertChangesMade();
    }

    @ParameterizedTest
    @EnumSource(ExceptionHandlingStrategy.class)
    public void withoutReceivingFeedMessageStatisticsAreReturnedFromTheMatchSummaryEndpoint(
        ExceptionHandlingStrategy strategy
    ) throws Exception {
        globalVariables.setProducer(ProducerId.LIVE_ODDS);
        Locale aLanguage = Locale.ENGLISH;

        apiSimulator.defineBookmaker();
        apiSimulator.activateOnlyLiveProducer();
        apiSimulator.stubAllSports(aLanguage);
        apiSimulator.stubAllTournaments(aLanguage, tournamentEuro2024());
        apiSimulator.stubMatchSummary(aLanguage, soccerMatchGermanyScotlandEuro2024());

        try (
            val sdk = SdkSetup
                .with(sdkCredentials, RABBIT_BASE_URL, sportsApiBaseUrl, globalVariables.getNodeId())
                .with(ListenerCollectingMessages.to(messagesStorage))
                .with(strategy)
                .withDefaultLanguage(aLanguage)
                .withoutFeed()
        ) {
            val sportDataProvider = sdk.getSportDataProvider();
            val matchId = Urn.parse(soccerMatchGermanyScotlandEuro2024().getSportEvent().getId());
            val match = (SoccerEvent) sportDataProvider.getSportEvent(matchId);
            val statistics = match.getStatus().getStatistics();

            SapiMatchStatistics expectedStats = soccerMatchGermanyScotlandEuro2024().getStatistics();
            StatisticsAssert.assertThat(statistics).totalsEqualToThoseIn(expectedStats);
            StatisticsAssert.assertThat(statistics).forPeriodsEqualToThoseIn(expectedStats);
        }
    }

    @ParameterizedTest
    @EnumSource(ExceptionHandlingStrategy.class)
    public void feedMessageStatisticsIfPresentAreFavoredOverApiCall(ExceptionHandlingStrategy strategy)
        throws Exception {
        FeedMessageBuilder messages = new FeedMessageBuilder(globalVariables);
        val matchId = Urn.parse(soccerMatchGermanyScotlandEuro2024().getSportEvent().getId());
        globalVariables.setProducer(ProducerId.LIVE_ODDS);
        globalVariables.setSportEventUrn(matchId);
        globalVariables.setSportUrn(Sport.FOOTBALL);
        RoutingKeys routingKeys = new RoutingKeys(globalVariables);

        Locale aLanguage = Locale.ENGLISH;
        apiSimulator.defineBookmaker();
        apiSimulator.activateOnlyLiveProducer();
        apiSimulator.stubAllSports(aLanguage);
        apiSimulator.stubAllTournaments(aLanguage, tournamentEuro2024());
        apiSimulator.stubMatchSummary(aLanguage, soccerMatchGermanyScotlandEuro2024());

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
            UfSportEventStatus ufStatus = withEveryStatistic(soccerMatchFeedStatus());
            rabbitProducer.send(messages.oddsChange(ufStatus), routingKeys.liveOddsChange());

            listinerWaitingFor.theOnlyOddsChange();

            val sportDataProvider = sdk.getSportDataProvider();
            val match = (SoccerEvent) sportDataProvider.getSportEvent(matchId);
            val statistics = match.getStatus().getStatistics();

            StatisticsAssert
                .assertThat(statistics)
                .totalsEqualToThoseIn(withEveryStatistic(soccerMatchFeedStatus()));
        }
    }
}
