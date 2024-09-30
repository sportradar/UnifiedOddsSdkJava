/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */
package com.sportradar.unifiedodds.sdk.conn;

import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.wireMockConfig;
import static com.sportradar.unifiedodds.sdk.ExceptionHandlingStrategy.Throw;
import static com.sportradar.unifiedodds.sdk.conn.ProducerId.LIVE_ODDS;
import static com.sportradar.unifiedodds.sdk.conn.SapiMarketDescriptions.Handicap.handicapMarketDescription;
import static com.sportradar.unifiedodds.sdk.conn.SapiMatchSummaries.Euro2024.soccerMatchGermanyScotlandEuro2024;
import static com.sportradar.unifiedodds.sdk.conn.Sport.FOOTBALL;
import static com.sportradar.unifiedodds.sdk.conn.UfMarkets.WithOdds.*;
import static com.sportradar.unifiedodds.sdk.conn.UfSpecifiers.UfHandicapSpecifier.handicap;
import static com.sportradar.unifiedodds.sdk.conn.marketids.HandicapMarketIds.COMPETITOR1_PLUS_HANDICAP_OUTCOME_ID;
import static com.sportradar.unifiedodds.sdk.conn.marketids.HandicapMarketIds.COMPETITOR2_MINUS_HANDICAP_OUTCOME_ID;
import static com.sportradar.unifiedodds.sdk.impl.Constants.*;
import static com.sportradar.unifiedodds.sdk.testutil.generic.naturallanguage.Prepositions.from;
import static com.sportradar.unifiedodds.sdk.testutil.generic.naturallanguage.Prepositions.with;
import static com.sportradar.unifiedodds.sdk.testutil.rabbit.integration.RabbitMqClientFactory.createRabbitMqClient;
import static com.sportradar.unifiedodds.sdk.testutil.rabbit.integration.RabbitMqProducer.connectDeclaringExchange;
import static com.sportradar.utils.domain.names.LanguageHolder.in;
import static com.sportradar.utils.domain.names.TranslationHolder.of;

import com.github.tomakehurst.wiremock.common.ConsoleNotifier;
import com.github.tomakehurst.wiremock.junit5.WireMockExtension;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.http.client.Client;
import com.sportradar.uf.sportsapi.datamodel.*;
import com.sportradar.unifiedodds.sdk.conn.UfSpecifiers.UfHandicapSpecifier;
import com.sportradar.unifiedodds.sdk.impl.Constants;
import com.sportradar.unifiedodds.sdk.impl.TimeUtilsImpl;
import com.sportradar.unifiedodds.sdk.impl.oddsentities.markets.OutcomeAssert;
import com.sportradar.unifiedodds.sdk.oddsentities.MarketWithOdds;
import com.sportradar.unifiedodds.sdk.oddsentities.OddsChange;
import com.sportradar.unifiedodds.sdk.oddsentities.OutcomeOdds;
import com.sportradar.unifiedodds.sdk.shared.FeedMessageBuilder;
import com.sportradar.unifiedodds.sdk.testutil.rabbit.integration.*;
import java.util.Locale;
import lombok.Value;
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
class MarketWithCompetitorOutcomeNameIT {

    private static final String HCP_PLUS = "\\{\\+hcp}";
    private static final String HCP_MINUS = "\\{-hcp}";
    private static final String COMPETITOR1 = "\\{\\$competitor1}";
    private static final String COMPETITOR2 = "\\{\\$competitor2}";

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

    private MarketWithCompetitorOutcomeNameIT() throws Exception {}

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
    @MethodSource("handicapSpecifiers")
    void processesUnaryAndCompetitorExpressionInOutcomeNames(UfHandicapSpecifier handicap) throws Exception {
        val germanyScotlandSummary = soccerMatchGermanyScotlandEuro2024();
        globalVariables.setProducer(LIVE_ODDS);
        globalVariables.setSportEventUrn(germanyScotlandSummary.getSportEvent());
        globalVariables.setSportUrn(FOOTBALL);
        FeedMessageBuilder messages = new FeedMessageBuilder(globalVariables);
        Locale aLanguage = Locale.ENGLISH;
        RoutingKeys routingKeys = new RoutingKeys(globalVariables);

        val handicapMarketDescription = handicapMarketDescription();
        apiSimulator.defineBookmaker();
        apiSimulator.activateOnlyLiveProducer();
        apiSimulator.stubMarketListContaining(handicapMarketDescription, aLanguage);
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
            rabbitProducer.send(messages.oddsChange(handicapMarket(handicap)), routingKeys.liveOddsChange());

            val market = theOnlyMarketIn(listenerWaitingFor.theOnlyOddsChange());

            OutcomeAssert
                .assertThat(outcome(COMPETITOR1_PLUS_HANDICAP_OUTCOME_ID, from(market)))
                .hasName(
                    of(
                        outcome(
                            COMPETITOR1_PLUS_HANDICAP_OUTCOME_ID,
                            from(handicapMarketDescription),
                            withExpressionsReplaced(
                                with(handicap),
                                with(firstCompetitor(from(germanyScotlandSummary)))
                            )
                        ),
                        in(Locale.ENGLISH)
                    )
                );

            OutcomeAssert
                .assertThat(outcome(COMPETITOR2_MINUS_HANDICAP_OUTCOME_ID, from(market)))
                .hasName(
                    of(
                        outcome(
                            COMPETITOR2_MINUS_HANDICAP_OUTCOME_ID,
                            from(handicapMarketDescription),
                            withExpressionsReplaced(
                                with(handicap),
                                with(secondCompetitor(from(germanyScotlandSummary)))
                            )
                        ),
                        in(Locale.ENGLISH)
                    )
                );
        }
    }

    private SapiTeamCompetitor firstCompetitor(SapiMatchSummaryEndpoint summary) {
        return summary.getSportEvent().getCompetitors().getCompetitor().get(0);
    }

    private SapiTeamCompetitor secondCompetitor(SapiMatchSummaryEndpoint summary) {
        return summary.getSportEvent().getCompetitors().getCompetitor().get(1);
    }

    private static ExpressionInputs withExpressionsReplaced(
        UfHandicapSpecifier handicap,
        SapiTeamCompetitor competitor
    ) {
        return new ExpressionInputs(handicap, competitor);
    }

    private static OutcomeOdds outcome(String id, MarketWithOdds market) {
        return market.getOutcomeOdds().stream().filter(o -> o.getId().equals(id)).findFirst().get();
    }

    private static String outcome(String id, DescMarket market, ExpressionInputs expressionInputs) {
        val outcome = market
            .getOutcomes()
            .getOutcome()
            .stream()
            .filter(o -> o.getId().equals(id))
            .findFirst()
            .get();
        val handicap = expressionInputs.getHandicap();
        return outcome
            .getName()
            .replaceAll(COMPETITOR1, expressionInputs.competitor.getName())
            .replaceAll(COMPETITOR2, expressionInputs.competitor.getName())
            .replaceAll(HCP_PLUS, formattedPlus(handicap))
            .replaceAll(HCP_MINUS, formattedMinus(handicap));
    }

    private static String formattedPlus(UfHandicapSpecifier handicap) {
        if (handicap.getValue() == 0) {
            return "0";
        }
        return handicap.getValue() > 0 ? "+" + handicap.getValue() : "-" + Math.abs(handicap.getValue());
    }

    private static String formattedMinus(UfHandicapSpecifier handicap) {
        if (handicap.getValue() == 0) {
            return "0";
        }
        return handicap.getValue() > 0 ? "-" + handicap.getValue() : "+" + Math.abs(handicap.getValue());
    }

    private MarketWithOdds theOnlyMarketIn(
        OddsChange<com.sportradar.unifiedodds.sdk.entities.SportEvent> oddsChange
    ) {
        Assertions.assertThat(oddsChange.getMarkets()).hasSize(1);
        return oddsChange.getMarkets().get(0);
    }

    static Object[] handicapSpecifiers() {
        return new Object[][] { { handicap(1) }, { handicap(2.0) }, { handicap(-2) }, { handicap(0) } };
    }

    @Value
    @SuppressWarnings("VisibilityModifier")
    private static class ExpressionInputs {

        UfHandicapSpecifier handicap;
        SapiTeamCompetitor competitor;
    }
}
