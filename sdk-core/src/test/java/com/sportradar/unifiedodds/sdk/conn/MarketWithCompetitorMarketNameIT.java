/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */
package com.sportradar.unifiedodds.sdk.conn;

import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.wireMockConfig;
import static com.sportradar.unifiedodds.sdk.ExceptionHandlingStrategy.Throw;
import static com.sportradar.unifiedodds.sdk.conn.ProducerId.LIVE_ODDS;
import static com.sportradar.unifiedodds.sdk.conn.SapiMarketDescriptions.Competitor1ToWinBothHalves.competitor1ToWinBothHalvesMarketDescription;
import static com.sportradar.unifiedodds.sdk.conn.SapiMatchSummaries.Euro2024.soccerMatchGermanyScotlandEuro2024;
import static com.sportradar.unifiedodds.sdk.conn.Sport.FOOTBALL;
import static com.sportradar.unifiedodds.sdk.conn.UfMarkets.WithOdds.competitor1ToWinBothHalvesMarket;
import static com.sportradar.unifiedodds.sdk.impl.Constants.*;
import static com.sportradar.unifiedodds.sdk.impl.oddsentities.markets.ExpectationTowardsSdkErrorHandlingStrategy.WILL_CATCH_EXCEPTIONS;
import static com.sportradar.unifiedodds.sdk.impl.oddsentities.markets.ExpectationTowardsSdkErrorHandlingStrategy.WILL_THROW_EXCEPTIONS;
import static com.sportradar.unifiedodds.sdk.testutil.rabbit.integration.RabbitMqClientFactory.createRabbitMqClient;
import static com.sportradar.unifiedodds.sdk.testutil.rabbit.integration.RabbitMqProducer.connectDeclaringExchange;
import static com.sportradar.utils.domain.names.LanguageHolder.in;
import static com.sportradar.utils.domain.names.TranslationHolder.with;
import static java.util.Collections.singletonList;

import com.github.tomakehurst.wiremock.common.ConsoleNotifier;
import com.github.tomakehurst.wiremock.junit5.WireMockExtension;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.http.client.Client;
import com.sportradar.uf.sportsapi.datamodel.DescMarket;
import com.sportradar.uf.sportsapi.datamodel.SapiMatchSummaryEndpoint;
import com.sportradar.uf.sportsapi.datamodel.SapiTeamCompetitor;
import com.sportradar.unifiedodds.sdk.ExceptionHandlingStrategy;
import com.sportradar.unifiedodds.sdk.exceptions.ObjectNotFoundException;
import com.sportradar.unifiedodds.sdk.impl.Constants;
import com.sportradar.unifiedodds.sdk.impl.oddsentities.markets.ExpectationTowardsSdkErrorHandlingStrategy;
import com.sportradar.unifiedodds.sdk.impl.oddsentities.markets.MarketAssert;
import com.sportradar.unifiedodds.sdk.internal.impl.TimeUtilsImpl;
import com.sportradar.unifiedodds.sdk.oddsentities.MarketWithOdds;
import com.sportradar.unifiedodds.sdk.oddsentities.OddsChange;
import com.sportradar.unifiedodds.sdk.shared.FeedMessageBuilder;
import com.sportradar.unifiedodds.sdk.testutil.rabbit.integration.*;
import com.sportradar.utils.Urn;
import java.util.Locale;
import lombok.val;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;
import org.junit.jupiter.params.ParameterizedTest;
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
class MarketWithCompetitorMarketNameIT {

    private static final String COMPETITOR1 = "\\{\\$competitor1}";

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

    private MarketWithCompetitorMarketNameIT() throws Exception {}

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
    void processesCompetitorExpressionInMarketName() throws Exception {
        val germanyScotlandSummary = soccerMatchGermanyScotlandEuro2024();
        globalVariables.setProducer(LIVE_ODDS);
        globalVariables.setSportEventUrn(germanyScotlandSummary.getSportEvent());
        globalVariables.setSportUrn(FOOTBALL);
        FeedMessageBuilder messages = new FeedMessageBuilder(globalVariables);
        Locale aLanguage = Locale.ENGLISH;
        RoutingKeys routingKeys = new RoutingKeys(globalVariables);

        apiSimulator.defineBookmaker();
        apiSimulator.activateOnlyLiveProducer();
        apiSimulator.stubMarketListContaining(
            competitor1ToWinBothHalvesMarketDescription(aLanguage),
            aLanguage
        );
        apiSimulator.stubMatchSummary(aLanguage, germanyScotlandSummary);

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
                messages.oddsChange(competitor1ToWinBothHalvesMarket()),
                routingKeys.liveOddsChange()
            );

            val market = theOnlyMarketIn(listenerWaitingFor.theOnlyOddsChange());

            MarketAssert
                .assertThat(market)
                .hasName(
                    with(
                        nameFrom(
                            competitor1ToWinBothHalvesMarketDescription(aLanguage),
                            homeCompetitor(germanyScotlandSummary)
                        ),
                        in(aLanguage)
                    )
                );
        }
    }

    @ParameterizedTest
    @MethodSource("exceptionHandlingStrategies")
    void uponMissingMatchSummaryFetchingMarketNameRespectsExceptionHandlingStrategy(
        ExceptionHandlingStrategy exceptionHandlingStrategy,
        ExpectationTowardsSdkErrorHandlingStrategy willFailRespectingSdkStrategy
    ) throws Exception {
        val germanyScotlandSummary = soccerMatchGermanyScotlandEuro2024();
        globalVariables.setProducer(LIVE_ODDS);
        globalVariables.setSportEventUrn(germanyScotlandSummary.getSportEvent());
        globalVariables.setSportUrn(FOOTBALL);
        FeedMessageBuilder messages = new FeedMessageBuilder(globalVariables);
        Locale aLanguage = Locale.ENGLISH;
        RoutingKeys routingKeys = new RoutingKeys(globalVariables);

        apiSimulator.defineBookmaker();
        apiSimulator.activateOnlyLiveProducer();
        apiSimulator.stubMarketListContaining(
            competitor1ToWinBothHalvesMarketDescription(aLanguage),
            aLanguage
        );
        apiSimulator.stubMatchSummaryNotFound(
            aLanguage,
            Urn.parse(germanyScotlandSummary.getSportEvent().getId())
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
                .with(exceptionHandlingStrategy)
                .withDefaultLanguage(aLanguage)
                .with1Session()
                .withOpenedFeed()
        ) {
            rabbitProducer.send(
                messages.oddsChange(competitor1ToWinBothHalvesMarket()),
                routingKeys.liveOddsChange()
            );

            val market = theOnlyMarketIn(listenerWaitingFor.theOnlyOddsChange());

            getNameForGiven(market, aLanguage, willFailRespectingSdkStrategy);
            getNamesForGiven(market, aLanguage, willFailRespectingSdkStrategy);
        }
    }

    private void getNameForGiven(
        MarketWithOdds market,
        Locale aLanguage,
        ExpectationTowardsSdkErrorHandlingStrategy willFailRespectingSdkStrategy
    ) {
        if (willFailRespectingSdkStrategy == WILL_THROW_EXCEPTIONS) {
            resultsInObjectNotFoundWhen(() -> market.getName(aLanguage));
        } else {
            Assertions.assertThat(market.getName(aLanguage)).isNull();
        }
    }

    private void getNamesForGiven(
        MarketWithOdds market,
        Locale aLanguage,
        ExpectationTowardsSdkErrorHandlingStrategy willFailRespectingSdkStrategy
    ) {
        if (willFailRespectingSdkStrategy == WILL_THROW_EXCEPTIONS) {
            resultsInObjectNotFoundWhen(() -> market.getNames(singletonList(aLanguage)));
        } else {
            Assertions.assertThat(market.getNames(singletonList(aLanguage))).containsEntry(aLanguage, null);
        }
    }

    private void resultsInObjectNotFoundWhen(Runnable runnable) {
        Assertions.assertThatThrownBy(runnable::run).isInstanceOf(ObjectNotFoundException.class);
    }

    private SapiTeamCompetitor homeCompetitor(SapiMatchSummaryEndpoint summary) {
        return summary.getSportEvent().getCompetitors().getCompetitor().get(0);
    }

    private String nameFrom(DescMarket market, SapiTeamCompetitor homeCompetitor) {
        return market.getName().replaceAll(COMPETITOR1, homeCompetitor.getName());
    }

    private MarketWithOdds theOnlyMarketIn(
        OddsChange<com.sportradar.unifiedodds.sdk.entities.SportEvent> oddsChange
    ) {
        Assertions.assertThat(oddsChange.getMarkets()).hasSize(1);
        return oddsChange.getMarkets().get(0);
    }

    private static Object[] exceptionHandlingStrategies() {
        return new Object[][] {
            { Throw, WILL_THROW_EXCEPTIONS },
            { ExceptionHandlingStrategy.Catch, WILL_CATCH_EXCEPTIONS },
        };
    }
}
