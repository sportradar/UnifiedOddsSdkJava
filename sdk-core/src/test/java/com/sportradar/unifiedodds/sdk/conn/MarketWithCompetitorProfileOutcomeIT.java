/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */
package com.sportradar.unifiedodds.sdk.conn;

import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.wireMockConfig;
import static com.sportradar.unifiedodds.sdk.ExceptionHandlingStrategy.Throw;
import static com.sportradar.unifiedodds.sdk.conn.ProducerId.LIVE_ODDS;
import static com.sportradar.unifiedodds.sdk.conn.SapiMarketDescriptions.HoleNrCompetitorUnderPar.holeNrCompetitorUnderParMarketDescription;
import static com.sportradar.unifiedodds.sdk.conn.SapiStageSummaries.ThePlayersGolfChampionship.Round2.THE_PLAYERS_GOLF_ROUND_2_COMPETITION_GROUP_ID;
import static com.sportradar.unifiedodds.sdk.conn.SapiStageSummaries.ThePlayersGolfChampionship.Round2.thePlayersGolfChampionshipRound2;
import static com.sportradar.unifiedodds.sdk.conn.Sport.GOLF;
import static com.sportradar.unifiedodds.sdk.conn.UfMarkets.WithOdds.holeNrCompetitorUnderParMarket;
import static com.sportradar.unifiedodds.sdk.conn.UfSpecifiers.UfCompetitorSpecifier.competitor;
import static com.sportradar.unifiedodds.sdk.conn.UfSpecifiers.UfHoleNrSpecifier.holeNr;
import static com.sportradar.unifiedodds.sdk.impl.Constants.*;
import static com.sportradar.unifiedodds.sdk.testutil.rabbit.integration.RabbitMqClientFactory.createRabbitMqClient;
import static com.sportradar.unifiedodds.sdk.testutil.rabbit.integration.RabbitMqProducer.connectDeclaringExchange;
import static com.sportradar.utils.domain.names.TranslationHolder.with;
import static org.junit.jupiter.params.provider.Arguments.arguments;

import com.github.tomakehurst.wiremock.common.ConsoleNotifier;
import com.github.tomakehurst.wiremock.junit5.WireMockExtension;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.http.client.Client;
import com.sportradar.uf.sportsapi.datamodel.DescMarket;
import com.sportradar.uf.sportsapi.datamodel.SapiCompetitorProfileEndpoint;
import com.sportradar.unifiedodds.sdk.conn.UfSpecifiers.UfHoleNrSpecifier;
import com.sportradar.unifiedodds.sdk.impl.Constants;
import com.sportradar.unifiedodds.sdk.impl.TimeUtilsImpl;
import com.sportradar.unifiedodds.sdk.impl.oddsentities.markets.MarketAssert;
import com.sportradar.unifiedodds.sdk.oddsentities.MarketWithOdds;
import com.sportradar.unifiedodds.sdk.oddsentities.OddsChange;
import com.sportradar.unifiedodds.sdk.shared.FeedMessageBuilder;
import com.sportradar.unifiedodds.sdk.testutil.rabbit.integration.*;
import com.sportradar.utils.Urn;
import com.sportradar.utils.domain.names.LanguageHolder;
import java.util.Locale;
import java.util.stream.Stream;
import lombok.Value;
import lombok.val;
import org.assertj.core.api.Assertions;
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
class MarketWithCompetitorProfileOutcomeIT {

    private static final String HOLENR = "\\{holenr}";
    private static final String COMPETITOR = "\\{%competitor}";

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

    private MarketWithCompetitorProfileOutcomeIT() throws Exception {}

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
    @MethodSource("competitorAndHole")
    void processesCompetitorProfileExpressionInOutcomeNames(
        SapiCompetitorProfileEndpoint competitor,
        UfHoleNrSpecifier holeNr
    ) throws Exception {
        globalVariables.setProducer(LIVE_ODDS);
        globalVariables.setSportEventUrn(Urn.parse(THE_PLAYERS_GOLF_ROUND_2_COMPETITION_GROUP_ID));
        globalVariables.setSportUrn(GOLF);
        FeedMessageBuilder messages = new FeedMessageBuilder(globalVariables);
        Locale aLanguage = Locale.ENGLISH;
        RoutingKeys routingKeys = new RoutingKeys(globalVariables);

        val holeNrCompetitorUnderParMarketDescription = holeNrCompetitorUnderParMarketDescription(aLanguage);
        apiSimulator.defineBookmaker();
        apiSimulator.activateOnlyLiveProducer();
        apiSimulator.stubMarketListContaining(holeNrCompetitorUnderParMarketDescription, aLanguage);
        apiSimulator.stubCompetitorProfile(aLanguage, competitor);

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
                messages.oddsChange(
                    holeNrCompetitorUnderParMarket(holeNr, competitor(withIdFrom(competitor)))
                ),
                routingKeys.liveOddsChange()
            );

            val market = theOnlyMarketIn(listenerWaitingFor.theOnlyOddsChange());

            MarketAssert
                .assertThat(market)
                .hasName(
                    with(
                        nameFrom(
                            holeNrCompetitorUnderParMarketDescription,
                            withExpressionsReplacedWith(holeNr, nameFrom(competitor))
                        ),
                        LanguageHolder.in(aLanguage)
                    )
                );
        }
    }

    private static Urn withIdFrom(SapiCompetitorProfileEndpoint competitor) {
        return Urn.parse(competitor.getCompetitor().getId());
    }

    private String nameFrom(SapiCompetitorProfileEndpoint competitorProfile) {
        return competitorProfile.getCompetitor().getName();
    }

    private String nameFrom(DescMarket market, ExpressionInputs expressionInputs) {
        return market
            .getName()
            .replaceAll(HOLENR, expressionInputs.getHoleNr().getValue() + "")
            .replaceAll(COMPETITOR, expressionInputs.getCompetitorName());
    }

    private static ExpressionInputs withExpressionsReplacedWith(
        UfHoleNrSpecifier holenr,
        String competitorName
    ) {
        return new ExpressionInputs(holenr, competitorName);
    }

    private MarketWithOdds theOnlyMarketIn(
        OddsChange<com.sportradar.unifiedodds.sdk.entities.SportEvent> oddsChange
    ) {
        Assertions.assertThat(oddsChange.getMarkets()).hasSize(1);
        return oddsChange.getMarkets().get(0);
    }

    static Stream<Arguments> competitorAndHole() {
        val golfRaceStage = thePlayersGolfChampionshipRound2();
        return SapiCompetitorProfiles
            .profilesFromSapiStageSummary(golfRaceStage)
            .stream()
            .flatMap(c -> Stream.of(arguments(c, holeNr(1)), arguments(c, holeNr(2))));
    }

    @Value
    @SuppressWarnings("VisibilityModifier")
    private static class ExpressionInputs {

        UfHoleNrSpecifier holeNr;
        String competitorName;
    }
}
