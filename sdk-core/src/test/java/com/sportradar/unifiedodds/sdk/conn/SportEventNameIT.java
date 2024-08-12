/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */
package com.sportradar.unifiedodds.sdk.conn;

import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.wireMockConfig;
import static com.sportradar.unifiedodds.sdk.ExceptionHandlingStrategy.Throw;
import static com.sportradar.unifiedodds.sdk.SapiCategories.england;
import static com.sportradar.unifiedodds.sdk.conn.SapiMatchSummaries.Euro2024.soccerMatchGermanyScotlandEuro2024;
import static com.sportradar.unifiedodds.sdk.conn.UfMarkets.WithOdds.oddEvenMarket;
import static com.sportradar.unifiedodds.sdk.impl.Constants.*;
import static com.sportradar.unifiedodds.sdk.impl.oddsentities.markets.ExpectationTowardsSdkErrorHandlingStrategy.WILL_CATCH_EXCEPTIONS;
import static com.sportradar.unifiedodds.sdk.impl.oddsentities.markets.ExpectationTowardsSdkErrorHandlingStrategy.WILL_THROW_EXCEPTIONS;
import static com.sportradar.unifiedodds.sdk.impl.oddsentities.markets.SportEventAssert.assertThat;
import static com.sportradar.unifiedodds.sdk.testutil.rabbit.integration.Credentials.with;
import static com.sportradar.unifiedodds.sdk.testutil.rabbit.integration.RabbitMqClientFactory.createRabbitMqClient;
import static com.sportradar.unifiedodds.sdk.testutil.rabbit.integration.RabbitMqProducer.connectDeclaringExchange;
import static com.sportradar.utils.domain.names.LanguageHolder.in;
import static com.sportradar.utils.domain.names.TranslationHolder.of;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.in;

import com.github.tomakehurst.wiremock.common.ConsoleNotifier;
import com.github.tomakehurst.wiremock.junit5.WireMockExtension;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.http.client.Client;
import com.sportradar.uf.sportsapi.datamodel.DescMarket;
import com.sportradar.uf.sportsapi.datamodel.SapiMatchSummaryEndpoint;
import com.sportradar.unifiedodds.sdk.ExceptionHandlingStrategy;
import com.sportradar.unifiedodds.sdk.impl.Constants;
import com.sportradar.unifiedodds.sdk.impl.TimeUtilsImpl;
import com.sportradar.unifiedodds.sdk.impl.oddsentities.markets.ExpectationTowardsSdkErrorHandlingStrategy;
import com.sportradar.unifiedodds.sdk.impl.oddsentities.markets.SportEventAssert;
import com.sportradar.unifiedodds.sdk.oddsentities.MarketWithOdds;
import com.sportradar.unifiedodds.sdk.oddsentities.OddsChange;
import com.sportradar.unifiedodds.sdk.shared.FeedMessageBuilder;
import com.sportradar.unifiedodds.sdk.testutil.rabbit.integration.*;
import com.sportradar.utils.Urn;
import com.sportradar.utils.domain.names.LanguageHolder;
import com.sportradar.utils.domain.names.TranslationHolder;
import java.util.Locale;
import lombok.val;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
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
public class SportEventNameIT {

    public static final String EXCEPTION_HANDLING_STRATEGIES =
        "com.sportradar.unifiedodds.sdk.conn.SportEventMethodSources#exceptionHandlingStrategies";

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

    public SportEventNameIT() throws Exception {}

    @BeforeEach
    void setup() throws Exception {
        rabbitMqUserSetup.setupUser(sdkCredentials);
        sportsApiBaseUrl = BaseUrl.of("localhost", wireMock.getPort());
    }

    @AfterEach
    void tearDown() {
        rabbitMqUserSetup.revertChangesMade();
    }

    @Nested
    class Match {

        @ParameterizedTest
        @MethodSource(EXCEPTION_HANDLING_STRATEGIES)
        public void forSportDataProviderMatchNameRetrievalFailuresAreProcessedIndependently(
            ExceptionHandlingStrategy exceptionHandlingStrategy,
            ExpectationTowardsSdkErrorHandlingStrategy willRespectSdkStrategy
        ) throws Exception {
            globalVariables.setProducer(ProducerId.LIVE_ODDS);
            Locale langA = Locale.ENGLISH;
            Locale langB = Locale.GERMAN;
            val id = Urn.parse(soccerMatchGermanyScotlandEuro2024().getSportEvent().getId());

            apiSimulator.defineBookmaker();
            apiSimulator.activateOnlyLiveProducer();
            apiSimulator.stubEmptyAllTournaments(langA);
            apiSimulator.stubAllSports(langA);
            apiSimulator.stubSportCategories(langA, Sport.FOOTBALL, england());
            apiSimulator.stubMatchSummary(langA, soccerMatchGermanyScotlandEuro2024(langA));
            apiSimulator.stubMatchSummaryNotFound(langB, id);

            try (
                val sdk = SdkSetup
                    .with(sdkCredentials, RABBIT_BASE_URL, sportsApiBaseUrl, globalVariables.getNodeId())
                    .with(ListenerCollectingMessages.to(messagesStorage))
                    .with(exceptionHandlingStrategy)
                    .withDefaultLanguage(langA)
                    .withDesiredLanguages(langA, langB)
                    .withoutFeed()
            ) {
                val event = sdk.getSportDataProvider().getSportEvent(id);

                assertThat(event)
                    .hasName(of(matchName(soccerMatchGermanyScotlandEuro2024(langA)), in(langA)));
                assertThat(event).getNameFor(langB, willRespectSdkStrategy);
            }
        }

        @ParameterizedTest
        @MethodSource(EXCEPTION_HANDLING_STRATEGIES)
        public void forFeedMessageMatchNameRetrievalFailuresAreProcessedIndependently(
            ExceptionHandlingStrategy exceptionHandlingStrategy,
            ExpectationTowardsSdkErrorHandlingStrategy willRespectSdkStrategy
        ) throws Exception {
            val id = Urn.parse(soccerMatchGermanyScotlandEuro2024().getSportEvent().getId());
            globalVariables.setProducer(ProducerId.LIVE_ODDS);
            globalVariables.setSportEventUrn(id);
            globalVariables.setSportUrn(Sport.FOOTBALL);
            val messages = new FeedMessageBuilder(globalVariables);
            val routingKeys = new RoutingKeys(globalVariables);

            Locale langA = Locale.ENGLISH;
            Locale langB = Locale.GERMAN;

            apiSimulator.defineBookmaker();
            apiSimulator.activateOnlyLiveProducer();
            apiSimulator.stubEmptyAllTournaments(langA);
            apiSimulator.stubAllSports(langA);
            apiSimulator.stubSportCategories(langA, Sport.FOOTBALL, england());
            apiSimulator.stubMatchSummary(langA, soccerMatchGermanyScotlandEuro2024(langA));
            apiSimulator.stubMatchSummaryNotFound(langB, id);
            apiSimulator.stubEmptyMarketList(langA);

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
                    .withDefaultLanguage(langA)
                    .withDesiredLanguages(langA, langB)
                    .with1Session()
                    .withOpenedFeed();
            ) {
                rabbitProducer.send(messages.oddsChange(oddEvenMarket()), routingKeys.liveOddsChange());

                val oddsChange = listinerWaitingFor.theOnlyOddsChange();

                val event = oddsChange.getEvent();

                assertThat(event)
                    .hasName(of(matchName(soccerMatchGermanyScotlandEuro2024(langA)), in(langA)));
                assertThat(event).getNameFor(langB, willRespectSdkStrategy);
            }
        }

        @ParameterizedTest
        @MethodSource(EXCEPTION_HANDLING_STRATEGIES)
        public void matchNameIsLanguageAware(
            ExceptionHandlingStrategy exceptionHandlingStrategy,
            ExpectationTowardsSdkErrorHandlingStrategy unused
        ) throws Exception {
            globalVariables.setProducer(ProducerId.LIVE_ODDS);
            Locale langA = Locale.ENGLISH;
            Locale langB = Locale.GERMAN;
            val id = Urn.parse(soccerMatchGermanyScotlandEuro2024().getSportEvent().getId());
            val matchLangA = soccerMatchGermanyScotlandEuro2024(langA);
            val matchLangB = soccerMatchGermanyScotlandEuro2024(langB);

            apiSimulator.defineBookmaker();
            apiSimulator.activateOnlyLiveProducer();
            apiSimulator.stubEmptyAllTournaments(langA);
            apiSimulator.stubAllSports(langA);
            apiSimulator.stubSportCategories(langA, Sport.FOOTBALL, england());
            apiSimulator.stubMatchSummary(langA, matchLangA);
            apiSimulator.stubMatchSummary(langB, matchLangB);

            try (
                val sdk = SdkSetup
                    .with(sdkCredentials, RABBIT_BASE_URL, sportsApiBaseUrl, globalVariables.getNodeId())
                    .with(ListenerCollectingMessages.to(messagesStorage))
                    .with(exceptionHandlingStrategy)
                    .withDefaultLanguage(langA)
                    .withDesiredLanguages(langA, langB)
                    .withoutFeed()
            ) {
                val event = sdk.getSportDataProvider().getSportEvent(id);

                assertThat(event).hasName(of(matchName(matchLangA), in(langA)));
                assertThat(event).hasName(of(matchName(matchLangB), in(langB)));
            }
        }

        private String matchName(SapiMatchSummaryEndpoint match) {
            val competitors = match.getSportEvent().getCompetitors().getCompetitor();
            val home = competitors.get(0).getName();
            val away = competitors.get(1).getName();
            return home + " vs. " + away;
        }
    }
}

class SportEventMethodSources {

    static Object[] exceptionHandlingStrategies() {
        return new Object[][] {
            { Throw, WILL_THROW_EXCEPTIONS },
            { ExceptionHandlingStrategy.Catch, WILL_CATCH_EXCEPTIONS },
        };
    }
}
