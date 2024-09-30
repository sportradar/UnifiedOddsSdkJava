/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */
package com.sportradar.unifiedodds.sdk.conn;

import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.wireMockConfig;
import static com.sportradar.unifiedodds.sdk.ExceptionHandlingStrategy.Throw;
import static com.sportradar.unifiedodds.sdk.conn.ProducerId.VIRTUAL_FOOTBALL;
import static com.sportradar.unifiedodds.sdk.conn.SapiMarketDescriptions.EventMatchDayHomeTeamsTotal.eventMatchDayHomeTeamsTotal;
import static com.sportradar.unifiedodds.sdk.conn.SapiTournaments.VirtualFootballLeague.VirtualFootballLeagueSeason.VIRTUAL_FOOTBALL_LEAGUE_SEASON_ID;
import static com.sportradar.unifiedodds.sdk.conn.SapiTournaments.VirtualFootballLeague.VirtualFootballLeagueSeason.virtualFootballLeagueSeasonInfo;
import static com.sportradar.unifiedodds.sdk.conn.Sport.FOOTBALL;
import static com.sportradar.unifiedodds.sdk.conn.UfMarkets.WithOdds.eventMatchDayHomeTeamsTotalMarket;
import static com.sportradar.unifiedodds.sdk.conn.UfSpecifiers.UfMatchDaySpecifier.matchDay;
import static com.sportradar.unifiedodds.sdk.impl.Constants.*;
import static com.sportradar.unifiedodds.sdk.testutil.rabbit.integration.RabbitMqClientFactory.createRabbitMqClient;
import static com.sportradar.unifiedodds.sdk.testutil.rabbit.integration.RabbitMqProducer.connectDeclaringExchange;
import static com.sportradar.utils.domain.names.LanguageHolder.in;
import static com.sportradar.utils.domain.names.TranslationHolder.with;

import com.github.tomakehurst.wiremock.common.ConsoleNotifier;
import com.github.tomakehurst.wiremock.junit5.WireMockExtension;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.http.client.Client;
import com.sportradar.uf.sportsapi.datamodel.DescMarket;
import com.sportradar.uf.sportsapi.datamodel.SapiTournamentInfoEndpoint;
import com.sportradar.unifiedodds.sdk.conn.UfSpecifiers.UfMatchDaySpecifier;
import com.sportradar.unifiedodds.sdk.impl.Constants;
import com.sportradar.unifiedodds.sdk.impl.TimeUtilsImpl;
import com.sportradar.unifiedodds.sdk.impl.oddsentities.markets.MarketAssert;
import com.sportradar.unifiedodds.sdk.oddsentities.MarketWithOdds;
import com.sportradar.unifiedodds.sdk.oddsentities.OddsChange;
import com.sportradar.unifiedodds.sdk.shared.FeedMessageBuilder;
import com.sportradar.unifiedodds.sdk.testutil.rabbit.integration.*;
import java.util.Locale;
import lombok.Value;
import lombok.val;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;

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
public class MarketWithEventInMarketNameIT {

    private static final String MATCHDAY = "\\{matchday}";
    private static final String EVENT = "\\{\\$event}";

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

    private MarketWithEventInMarketNameIT() throws Exception {}

    @BeforeEach
    void setup() throws Exception {
        rabbitMqUserSetup.setupUser(sdkCredentials);
        sportsApiBaseUrl = BaseUrl.of("localhost", wireMock.getPort());
    }

    @AfterEach
    public void tearDownProxy() {
        rabbitMqUserSetup.revertChangesMade();
    }

    @Test
    void processesEventExpressionMarketName() throws Exception {
        globalVariables.setProducer(VIRTUAL_FOOTBALL);
        globalVariables.setSportEventUrn(VIRTUAL_FOOTBALL_LEAGUE_SEASON_ID);
        globalVariables.setSportUrn(FOOTBALL);
        FeedMessageBuilder messages = new FeedMessageBuilder(globalVariables);
        Locale aLanguage = Locale.ENGLISH;
        RoutingKeys routingKeys = new RoutingKeys(globalVariables);

        apiSimulator.defineBookmaker();
        apiSimulator.activateProducer(VIRTUAL_FOOTBALL);
        apiSimulator.stubMarketListContaining(eventMatchDayHomeTeamsTotal(aLanguage), aLanguage);
        apiSimulator.stubSeasonSummary(aLanguage, virtualFootballLeagueSeasonInfo());

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
                messages.oddsChange(eventMatchDayHomeTeamsTotalMarket(matchDay(3))),
                routingKeys.liveOddsChange()
            );

            val market = theOnlyMarketIn(listenerWaitingFor.theOnlyOddsChange());

            MarketAssert
                .assertThat(market)
                .hasName(
                    with(
                        nameFrom(
                            eventMatchDayHomeTeamsTotal(aLanguage),
                            withExpressionsReplacedWith(
                                seasonNameFrom(virtualFootballLeagueSeasonInfo()),
                                matchDay(3)
                            )
                        ),
                        in(aLanguage)
                    )
                );
        }
    }

    private String seasonNameFrom(SapiTournamentInfoEndpoint player) {
        return player.getSeason().getName();
    }

    private String nameFrom(DescMarket market, ExpressionInputs expressionInputs) {
        return market
            .getName()
            .replaceAll(MATCHDAY, expressionInputs.matchDay.getValue().toString())
            .replaceAll(EVENT, expressionInputs.seasonName);
    }

    private static ExpressionInputs withExpressionsReplacedWith(
        String seasonName,
        UfMatchDaySpecifier matchDay
    ) {
        return new ExpressionInputs(seasonName, matchDay);
    }

    private MarketWithOdds theOnlyMarketIn(
        OddsChange<com.sportradar.unifiedodds.sdk.entities.SportEvent> oddsChange
    ) {
        Assertions.assertThat(oddsChange.getMarkets()).hasSize(1);
        return oddsChange.getMarkets().get(0);
    }

    @Value
    @SuppressWarnings("VisibilityModifier")
    private static class ExpressionInputs {

        String seasonName;
        UfMatchDaySpecifier matchDay;
    }
}
