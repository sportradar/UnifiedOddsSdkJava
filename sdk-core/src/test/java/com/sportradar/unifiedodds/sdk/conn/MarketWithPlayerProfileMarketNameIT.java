/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */
package com.sportradar.unifiedodds.sdk.conn;

import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.wireMockConfig;
import static com.sportradar.unifiedodds.sdk.ExceptionHandlingStrategy.Throw;
import static com.sportradar.unifiedodds.sdk.conn.ProducerId.LIVE_ODDS;
import static com.sportradar.unifiedodds.sdk.conn.SapiMarketDescriptions.PlayerToStrikeOutAppearanceTimeAtBat.playerToStrikeOutAppearanceTimeAtBatMarketDescription;
import static com.sportradar.unifiedodds.sdk.conn.SapiMatchSummaries.Mlb.MlbHoustonAstrosLosAngelesAngels2024.MLB_HOUSTON_ASTROS_LOS_ANGELES_ANGELS_2024;
import static com.sportradar.unifiedodds.sdk.conn.SapiMatchSummaries.Mlb.MlbHoustonAstrosLosAngelesAngels2024.mlbHoustonAstrosLosAngelesAngels2024;
import static com.sportradar.unifiedodds.sdk.conn.Sport.BASEBALL;
import static com.sportradar.unifiedodds.sdk.conn.UfMarkets.WithOdds.playerToStrikeOutAppearanceTimeAtBatMarket;
import static com.sportradar.unifiedodds.sdk.conn.UfSpecifiers.UfAppearanceNrSpecifier.appearanceNr;
import static com.sportradar.unifiedodds.sdk.conn.UfSpecifiers.UfPlayerSpecifier.player;
import static com.sportradar.unifiedodds.sdk.impl.Constants.*;
import static com.sportradar.unifiedodds.sdk.testutil.rabbit.integration.RabbitMqClientFactory.createRabbitMqClient;
import static com.sportradar.unifiedodds.sdk.testutil.rabbit.integration.RabbitMqProducer.connectDeclaringExchange;
import static com.sportradar.utils.domain.names.LanguageHolder.in;
import static com.sportradar.utils.domain.names.TranslationHolder.with;
import static org.junit.jupiter.params.provider.Arguments.arguments;

import com.github.tomakehurst.wiremock.common.ConsoleNotifier;
import com.github.tomakehurst.wiremock.junit5.WireMockExtension;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.http.client.Client;
import com.sportradar.uf.sportsapi.datamodel.DescMarket;
import com.sportradar.uf.sportsapi.datamodel.SapiPlayerExtended;
import com.sportradar.unifiedodds.sdk.conn.SapiPlayerProfiles.Mlb;
import com.sportradar.unifiedodds.sdk.conn.UfSpecifiers.UfAppearanceNrSpecifier;
import com.sportradar.unifiedodds.sdk.impl.Constants;
import com.sportradar.unifiedodds.sdk.impl.TimeUtilsImpl;
import com.sportradar.unifiedodds.sdk.impl.oddsentities.markets.MarketAssert;
import com.sportradar.unifiedodds.sdk.impl.oddsentities.markets.Ordinals;
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
public class MarketWithPlayerProfileMarketNameIT {

    private static final String APPEARANCENR = "\\{!appearancenr}";
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

    private MarketWithPlayerProfileMarketNameIT() throws Exception {}

    @BeforeEach
    void setup() throws Exception {
        rabbitMqUserSetup.setupUser(sdkCredentials);
        sportsApiBaseUrl = BaseUrl.of("localhost", wireMock.getPort());
    }

    @AfterEach
    public void tearDownProxy() {
        rabbitMqUserSetup.revertChangesMade();
    }

    @ParameterizedTest
    @MethodSource("appearanceAndPlayer")
    void processesProfileProfileExpressionMarketName(
        SapiPlayerExtended player,
        UfAppearanceNrSpecifier appearanceNr
    ) throws Exception {
        globalVariables.setProducer(LIVE_ODDS);
        globalVariables.setSportEventUrn(MLB_HOUSTON_ASTROS_LOS_ANGELES_ANGELS_2024);
        globalVariables.setSportUrn(BASEBALL);
        FeedMessageBuilder messages = new FeedMessageBuilder(globalVariables);
        Locale aLanguage = Locale.ENGLISH;
        RoutingKeys routingKeys = new RoutingKeys(globalVariables);

        apiSimulator.defineBookmaker();
        apiSimulator.activateOnlyLiveProducer();
        apiSimulator.stubMarketListContaining(
            playerToStrikeOutAppearanceTimeAtBatMarketDescription(aLanguage),
            aLanguage
        );
        apiSimulator.stubMatchSummary(aLanguage, mlbHoustonAstrosLosAngelesAngels2024());
        apiSimulator.stubPlayerProfile(aLanguage, player);

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
                    playerToStrikeOutAppearanceTimeAtBatMarket(appearanceNr, player(withIdFrom(player)))
                ),
                routingKeys.liveOddsChange()
            );

            val market = theOnlyMarketIn(listenerWaitingFor.theOnlyOddsChange());

            MarketAssert
                .assertThat(market)
                .hasName(
                    with(
                        nameFrom(
                            playerToStrikeOutAppearanceTimeAtBatMarketDescription(aLanguage),
                            withExpressionsReplacedWith(appearanceNr, nameFrom(player)),
                            in(aLanguage)
                        ),
                        in(aLanguage)
                    )
                );
        }
    }

    private static Urn withIdFrom(SapiPlayerExtended player) {
        return Urn.parse(player.getId());
    }

    private String nameFrom(SapiPlayerExtended player) {
        return player.getName();
    }

    private String nameFrom(DescMarket market, ExpressionInputs expressionInputs, LanguageHolder language) {
        return market
            .getName()
            .replaceAll(
                APPEARANCENR,
                Ordinals.ordinal(expressionInputs.getAppearanceNr().getValue(), language)
            )
            .replaceAll(PLAYER, expressionInputs.getPlayerName());
    }

    private static ExpressionInputs withExpressionsReplacedWith(
        UfAppearanceNrSpecifier appearanceNr,
        String competitorName
    ) {
        return new ExpressionInputs(appearanceNr, competitorName);
    }

    private MarketWithOdds theOnlyMarketIn(
        OddsChange<com.sportradar.unifiedodds.sdk.entities.SportEvent> oddsChange
    ) {
        Assertions.assertThat(oddsChange.getMarkets()).hasSize(1);
        return oddsChange.getMarkets().get(0);
    }

    static Stream<Arguments> appearanceAndPlayer() {
        return Stream.of(
            arguments(Mlb.victorCaratiniProfile(), appearanceNr(1)),
            arguments(Mlb.jakeMeyersProfile(), appearanceNr(2))
        );
    }

    @Value
    @SuppressWarnings("VisibilityModifier")
    private static class ExpressionInputs {

        UfAppearanceNrSpecifier appearanceNr;
        String playerName;
    }
}
