/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */
package com.sportradar.unifiedodds.sdk.conn;

import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.wireMockConfig;
import static com.sportradar.unifiedodds.sdk.ExceptionHandlingStrategy.Throw;
import static com.sportradar.unifiedodds.sdk.conn.ProducerId.LIVE_ODDS;
import static com.sportradar.unifiedodds.sdk.conn.SapiMarketDescriptions.AnytimeGoalscorer.anytimeGoalscorerMarketDescription;
import static com.sportradar.unifiedodds.sdk.conn.SapiMatchSummaries.Euro2024.GERMANY_SCOTLAND_MATCH_URN;
import static com.sportradar.unifiedodds.sdk.conn.SapiMatchSummaries.Euro2024.soccerMatchGermanyScotlandEuro2024;
import static com.sportradar.unifiedodds.sdk.conn.SapiPlayerProfiles.*;
import static com.sportradar.unifiedodds.sdk.conn.SapiTeams.Germany2024Uefa.germanyCompetitorProfile;
import static com.sportradar.unifiedodds.sdk.conn.Sport.FOOTBALL;
import static com.sportradar.unifiedodds.sdk.conn.UfMarkets.WithOdds.anytimeGoalscorerMarket;
import static com.sportradar.unifiedodds.sdk.conn.UfSpecifiers.UfPlayerSpecifier.player;
import static com.sportradar.unifiedodds.sdk.impl.Constants.*;
import static com.sportradar.unifiedodds.sdk.testutil.rabbit.integration.RabbitMqClientFactory.createRabbitMqClient;
import static com.sportradar.unifiedodds.sdk.testutil.rabbit.integration.RabbitMqProducer.connectDeclaringExchange;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.params.provider.Arguments.arguments;

import com.github.tomakehurst.wiremock.common.ConsoleNotifier;
import com.github.tomakehurst.wiremock.junit5.WireMockExtension;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.http.client.Client;
import com.sportradar.uf.sportsapi.datamodel.SapiPlayerExtended;
import com.sportradar.unifiedodds.sdk.conn.UfSpecifiers.UfPlayerSpecifier;
import com.sportradar.unifiedodds.sdk.entities.SportEvent;
import com.sportradar.unifiedodds.sdk.impl.Constants;
import com.sportradar.unifiedodds.sdk.impl.oddsentities.markets.OutcomesAssert;
import com.sportradar.unifiedodds.sdk.internal.impl.TimeUtilsImpl;
import com.sportradar.unifiedodds.sdk.oddsentities.MarketWithOdds;
import com.sportradar.unifiedodds.sdk.oddsentities.OddsChange;
import com.sportradar.unifiedodds.sdk.shared.FeedMessageBuilder;
import com.sportradar.unifiedodds.sdk.testutil.rabbit.integration.*;
import com.sportradar.utils.Urn;
import java.util.Locale;
import java.util.stream.Stream;
import lombok.val;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.RegisterExtension;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

@SuppressWarnings(
    {
        "ClassDataAbstractionCoupling",
        "ClassFanOutComplexity",
        "CyclomaticComplexity",
        "ExecutableStatementCount",
        "HiddenField",
        "IllegalCatch",
        "JavaNCSS",
        "LineLength",
        "MagicNumber",
        "MethodLength",
        "MultipleStringLiterals",
        "OverloadMethodsDeclarationOrder",
        "ParameterAssignment",
    }
)
class MarketWithPlayerProfileOutcomeIT {

    private static final String PLAYER = "\\{%player}";

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

    private MarketWithPlayerProfileOutcomeIT() throws Exception {}

    @BeforeEach
    void setup() throws Exception {
        rabbitMqUserSetup.setupUser(sdkCredentials);
        sportsApiBaseUrl = BaseUrl.of("localhost", wireMock.getPort());
    }

    @AfterEach
    void tearDownProxy() {
        rabbitMqUserSetup.revertChangesMade();
    }

    @ParameterizedTest
    @MethodSource("kaiHavertzWithIdsFromDifferentFeedProviders")
    void processesPlayerProfileExpressionInOutcomeNames(
        SapiPlayerExtended kaiHavertzPlayerProfile,
        UfPlayerSpecifier ufPlayerSpecifier
    ) throws Exception {
        globalVariables.setProducer(LIVE_ODDS);
        globalVariables.setSportEventUrn(Urn.parse(GERMANY_SCOTLAND_MATCH_URN));
        globalVariables.setSportUrn(FOOTBALL);
        FeedMessageBuilder messages = new FeedMessageBuilder(globalVariables);
        Locale aLanguage = Locale.ENGLISH;
        RoutingKeys routingKeys = new RoutingKeys(globalVariables);

        val anytimeGoalscorerMarketDescription = anytimeGoalscorerMarketDescription(aLanguage);
        val soccerMatchGermanyScotlandEuro2024 = soccerMatchGermanyScotlandEuro2024();

        apiSimulator.defineBookmaker();
        apiSimulator.activateOnlyLiveProducer();
        apiSimulator.stubMarketListContaining(anytimeGoalscorerMarketDescription, aLanguage);
        apiSimulator.stubPlayerProfile(aLanguage, kaiHavertzPlayerProfile);
        apiSimulator.stubMatchSummary(aLanguage, soccerMatchGermanyScotlandEuro2024);

        val germanyCompetitorProfile = germanyCompetitorProfile();
        germanyCompetitorProfile.getPlayers().getPlayer().add(kaiHavertzPlayerProfile);
        apiSimulator.stubCompetitorProfile(aLanguage, germanyCompetitorProfile);

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
            rabbitProducer.send(
                messages.oddsChange(anytimeGoalscorerMarket(ufPlayerSpecifier)),
                routingKeys.liveOddsChange()
            );

            val market = theOnlyMarketIn(listenerWaitingFor.theOnlyOddsChange());
            val outcomes = market.getOutcomeOdds();

            OutcomesAssert
                .assertThat(outcomes)
                .hasOutcomeWithId(kaiHavertzPlayerProfile.getId())
                .which()
                .hasNameForDefaultLanguage(aLanguage, kaiHavertzPlayerProfile.getName());
        }
    }

    private MarketWithOdds theOnlyMarketIn(OddsChange<SportEvent> oddsChange) {
        assertThat(oddsChange.getMarkets()).hasSize(1);
        return oddsChange.getMarkets().get(0);
    }

    static Stream<Arguments> kaiHavertzWithIdsFromDifferentFeedProviders() {
        return Stream.of(
            arguments(kaiHavertzProfileFromBetGenius(), player(Urn.parse(KAI_HAVERTZ_BETGENIUS_PLAYER_ID))),
            arguments(kaiHavertzProfileFromOddin(), player(Urn.parse(KAI_HAVERTZ_ODDIN_PLAYER_ID)))
        );
    }
}
