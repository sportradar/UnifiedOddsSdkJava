/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */
package com.sportradar.unifiedodds.sdk.conn;

import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.wireMockConfig;
import static com.sportradar.unifiedodds.sdk.ExceptionHandlingStrategy.Throw;
import static com.sportradar.unifiedodds.sdk.conn.ProducerId.LIVE_ODDS;
import static com.sportradar.unifiedodds.sdk.conn.SapiMarketDescriptions.MapDuration.mapDurationMarketDescription;
import static com.sportradar.unifiedodds.sdk.conn.SapiMarketDescriptions.SetNrBreakNr.setNrBreakNrMarketDescription;
import static com.sportradar.unifiedodds.sdk.conn.Sport.ESPORT_DOTA;
import static com.sportradar.unifiedodds.sdk.conn.SportEvent.MATCH;
import static com.sportradar.unifiedodds.sdk.conn.UfMarkets.WithOdds.mapDurationMarket;
import static com.sportradar.unifiedodds.sdk.conn.UfMarkets.WithOdds.setNrBreakNrMarket;
import static com.sportradar.unifiedodds.sdk.conn.UfSpecifiers.UfBreakNrSpecifier.breakNr;
import static com.sportradar.unifiedodds.sdk.conn.UfSpecifiers.UfMapNrSpecifier.mapNr;
import static com.sportradar.unifiedodds.sdk.conn.UfSpecifiers.UfMinuteSpecifier.minute;
import static com.sportradar.unifiedodds.sdk.conn.UfSpecifiers.UfSetNrSpecifier.setNr;
import static com.sportradar.unifiedodds.sdk.conn.marketids.SetNrBreakNrMarketIds.setNrBreakNrMarket;
import static com.sportradar.unifiedodds.sdk.impl.Constants.*;
import static com.sportradar.unifiedodds.sdk.impl.oddsentities.markets.Ordinals.ordinal;
import static com.sportradar.unifiedodds.sdk.testutil.generic.naturallanguage.Prepositions.with;
import static com.sportradar.unifiedodds.sdk.testutil.rabbit.integration.RabbitMqClientFactory.createRabbitMqClient;
import static com.sportradar.unifiedodds.sdk.testutil.rabbit.integration.RabbitMqProducer.connectDeclaringExchange;
import static com.sportradar.utils.domain.names.LanguageHolder.in;
import static com.sportradar.utils.domain.names.TranslationHolder.with;

import com.github.tomakehurst.wiremock.common.ConsoleNotifier;
import com.github.tomakehurst.wiremock.junit5.WireMockExtension;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.http.client.Client;
import com.sportradar.uf.datamodel.UfOddsChangeMarket;
import com.sportradar.unifiedodds.sdk.conn.UfSpecifiers.UfBreakNrSpecifier;
import com.sportradar.unifiedodds.sdk.conn.UfSpecifiers.UfMapNrSpecifier;
import com.sportradar.unifiedodds.sdk.conn.UfSpecifiers.UfSetNrSpecifier;
import com.sportradar.unifiedodds.sdk.exceptions.InitException;
import com.sportradar.unifiedodds.sdk.impl.Constants;
import com.sportradar.unifiedodds.sdk.impl.TimeUtilsImpl;
import com.sportradar.unifiedodds.sdk.impl.oddsentities.markets.MarketAssert;
import com.sportradar.unifiedodds.sdk.impl.oddsentities.markets.Ordinals;
import com.sportradar.unifiedodds.sdk.oddsentities.MarketWithOdds;
import com.sportradar.unifiedodds.sdk.oddsentities.OddsChange;
import com.sportradar.unifiedodds.sdk.shared.FeedMessageBuilder;
import com.sportradar.unifiedodds.sdk.testutil.generic.naturallanguage.Prepositions;
import com.sportradar.unifiedodds.sdk.testutil.rabbit.integration.*;
import java.io.IOException;
import java.util.Locale;
import java.util.concurrent.TimeoutException;
import lombok.val;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
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
class MarketWithOrdinalOperandMarketNameIT {

    private static final String MAPNR = "\\{!mapnr}";
    private static final String SETNR = "\\{!setnr}";
    private static final String BREAKNR = "\\{!breaknr}";

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

    private MarketWithOrdinalOperandMarketNameIT() throws Exception {}

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
    @MethodSource("mapNrSpecifiers")
    void processesOrdinalExpressionsInTheMarketName(UfMapNrSpecifier mapNrSpecifier)
        throws IOException, TimeoutException, InitException {
        globalVariables.setProducer(LIVE_ODDS);
        globalVariables.setSportEventUrn(MATCH);
        globalVariables.setSportUrn(ESPORT_DOTA);
        FeedMessageBuilder messages = new FeedMessageBuilder(globalVariables);
        Locale aLanguage = Locale.ENGLISH;
        RoutingKeys routingKeys = new RoutingKeys(globalVariables);

        apiSimulator.defineBookmaker();
        apiSimulator.activateOnlyLiveProducer();
        apiSimulator.stubMarketListContaining(mapDurationMarketDescription(aLanguage), aLanguage);

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
                messages.oddsChange(mapDurationMarket(mapNrSpecifier, minute(9))),
                routingKeys.liveOddsChange()
            );

            val market = theOnlyMarketIn(listenerWaitingFor.theOnlyOddsChange());

            String nameWithOrdinal = mapDurationMarketDescription(aLanguage)
                .getName()
                .replaceAll(MAPNR, with(ordinal(mapNrSpecifier.getValue(), in(aLanguage))));

            MarketAssert.assertThat(market).hasName(with(nameWithOrdinal, in(aLanguage)));
        }
    }

    @ParameterizedTest
    @MethodSource("setNrBreakNrSpecifiers")
    void processesTwoOrdinalExpressionsInTheSingleMarketName(
        UfSetNrSpecifier setNrSpecifier,
        UfBreakNrSpecifier breakNrSpecifier
    ) throws IOException, TimeoutException, InitException {
        globalVariables.setProducer(LIVE_ODDS);
        globalVariables.setSportEventUrn(MATCH);
        globalVariables.setSportUrn(ESPORT_DOTA);
        FeedMessageBuilder messages = new FeedMessageBuilder(globalVariables);
        Locale aLanguage = Locale.ENGLISH;
        RoutingKeys routingKeys = new RoutingKeys(globalVariables);

        apiSimulator.defineBookmaker();
        apiSimulator.activateOnlyLiveProducer();
        apiSimulator.stubMarketListContaining(setNrBreakNrMarketDescription(aLanguage), aLanguage);

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
                messages.oddsChange(setNrBreakNrMarket(setNrSpecifier, breakNrSpecifier)),
                routingKeys.liveOddsChange()
            );

            val market = theOnlyMarketIn(listenerWaitingFor.theOnlyOddsChange());

            String nameWithOrdinals = setNrBreakNrMarketDescription(aLanguage)
                .getName()
                .replaceAll(SETNR, with(ordinal(setNrSpecifier.getValue(), in(aLanguage))))
                .replaceAll(BREAKNR, with(ordinal(breakNrSpecifier.getValue(), in(aLanguage))));
            MarketAssert.assertThat(market).hasName(with(nameWithOrdinals, in(aLanguage)));
        }
    }

    private MarketWithOdds theOnlyMarketIn(
        OddsChange<com.sportradar.unifiedodds.sdk.entities.SportEvent> oddsChange
    ) {
        Assertions.assertThat(oddsChange.getMarkets()).hasSize(1);
        return oddsChange.getMarkets().get(0);
    }

    static Object[] mapNrSpecifiers() {
        return new Object[][] { { mapNr(1) }, { mapNr(2) }, { mapNr(4) } };
    }

    static Object[] setNrBreakNrSpecifiers() {
        return new Object[][] {
            { setNr(1), breakNr(1) },
            { setNr(2), breakNr(2) },
            { setNr(3), breakNr(1) },
        };
    }
}
