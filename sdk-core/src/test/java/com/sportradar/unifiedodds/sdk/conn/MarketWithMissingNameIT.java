/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */
package com.sportradar.unifiedodds.sdk.conn;

import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.wireMockConfig;
import static com.google.common.collect.Iterables.getOnlyElement;
import static com.sportradar.unifiedodds.sdk.ExceptionHandlingStrategy.Throw;
import static com.sportradar.unifiedodds.sdk.conn.AcceptanceTestDsl.Setup.context;
import static com.sportradar.unifiedodds.sdk.conn.ProducerId.LIVE_ODDS;
import static com.sportradar.unifiedodds.sdk.conn.SapiMarketDescriptions.ExactGoals.exactGoalsMarketDescription;
import static com.sportradar.unifiedodds.sdk.conn.SapiMarketDescriptions.FreeTextMarketDescription.freeTextMarketDescription;
import static com.sportradar.unifiedodds.sdk.conn.SapiMarketDescriptions.NascarOutrights.nascarOutrightsMarketDescription;
import static com.sportradar.unifiedodds.sdk.conn.SapiMarketDescriptions.OddEven.oddEvenMarketDescription;
import static com.sportradar.unifiedodds.sdk.conn.SapiMarketDescriptions.OneXtwo.oneXtwoMarketDescription;
import static com.sportradar.unifiedodds.sdk.conn.SapiVariantDescriptions.ExactGoals.fivePlusVariantDescription;
import static com.sportradar.unifiedodds.sdk.conn.Sport.FOOTBALL;
import static com.sportradar.unifiedodds.sdk.conn.SportEvent.MATCH;
import static com.sportradar.unifiedodds.sdk.conn.UfMarkets.WithOdds.*;
import static com.sportradar.unifiedodds.sdk.conn.marketids.ExactGoalsMarketIds.fivePlusVariant;
import static com.sportradar.unifiedodds.sdk.impl.Constants.*;
import static com.sportradar.unifiedodds.sdk.impl.oddsentities.markets.ExpectationTowardsSdkErrorHandlingStrategy.WILL_CATCH_EXCEPTIONS;
import static com.sportradar.unifiedodds.sdk.impl.oddsentities.markets.ExpectationTowardsSdkErrorHandlingStrategy.WILL_THROW_EXCEPTIONS;
import static com.sportradar.unifiedodds.sdk.testutil.rabbit.integration.Credentials.with;
import static com.sportradar.unifiedodds.sdk.testutil.rabbit.integration.RabbitMqClientFactory.createRabbitMqClient;
import static com.sportradar.unifiedodds.sdk.testutil.rabbit.integration.RabbitMqProducer.connectDeclaringExchange;
import static org.assertj.core.api.Assertions.*;

import com.github.tomakehurst.wiremock.junit.WireMockRule;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.http.client.Client;
import com.sportradar.uf.sportsapi.datamodel.DescMarket;
import com.sportradar.unifiedodds.sdk.ExceptionHandlingStrategy;
import com.sportradar.unifiedodds.sdk.exceptions.InitException;
import com.sportradar.unifiedodds.sdk.impl.Constants;
import com.sportradar.unifiedodds.sdk.impl.TimeUtilsImpl;
import com.sportradar.unifiedodds.sdk.impl.oddsentities.markets.ExpectationTowardsSdkErrorHandlingStrategy;
import com.sportradar.unifiedodds.sdk.impl.oddsentities.markets.MarketAssert;
import com.sportradar.unifiedodds.sdk.oddsentities.MarketDefinition;
import com.sportradar.unifiedodds.sdk.oddsentities.MarketWithOdds;
import com.sportradar.unifiedodds.sdk.oddsentities.OddsChange;
import com.sportradar.unifiedodds.sdk.shared.FeedMessageBuilder;
import com.sportradar.unifiedodds.sdk.testutil.rabbit.integration.*;
import java.io.IOException;
import java.util.Locale;
import java.util.concurrent.TimeoutException;
import junitparams.JUnitParamsRunner;
import junitparams.Parameters;
import lombok.val;
import org.assertj.core.api.Assertions;
import org.junit.*;
import org.junit.runner.RunWith;

@RunWith(JUnitParamsRunner.class)
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
        "LambdaBodyLength",
    }
)
public class MarketWithMissingNameIT {

    @Rule
    public WireMockRule wireMock = new WireMockRule(wireMockConfig().dynamicPort());

    private final GlobalVariables globalVariables = new GlobalVariables();
    private final ApiSimulator apiSimulator = new ApiSimulator(wireMock);
    private final Locale aLanguage = Locale.ENGLISH;
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

    public MarketWithMissingNameIT() throws Exception {}

    @Before
    public void setup() throws Exception {
        rabbitMqUserSetup.setupUser(sdkCredentials);
        sportsApiBaseUrl = BaseUrl.of("localhost", wireMock.port());
    }

    @After
    public void tearDownProxy() {
        rabbitMqUserSetup.revertChangesMade();
    }

    @Test
    @Parameters(method = "exceptionHandlingStrategies")
    public void marketDescriptionHasNullName(
        ExceptionHandlingStrategy exceptionHandlingStrategy,
        ExpectationTowardsSdkErrorHandlingStrategy unused
    ) throws Exception {
        globalVariables.setProducer(LIVE_ODDS);
        Locale aLanguage = Locale.ENGLISH;

        apiSimulator.defineBookmaker();
        apiSimulator.activateOnlyLiveProducer();
        apiSimulator.stubMarketListContaining(nullifyName(oneXtwoMarketDescription()), aLanguage);

        try (
            val sdk = SdkSetup
                .with(sdkCredentials, RABBIT_BASE_URL, sportsApiBaseUrl, globalVariables.getNodeId())
                .with(ListenerCollectingMessages.to(messagesStorage))
                .with(exceptionHandlingStrategy)
                .withDefaultLanguage(aLanguage)
                .withoutFeed()
        ) {
            val marketDescriptions = sdk.getMarketDescriptionManager().getMarketDescriptions();
            val namelessDescription = getOnlyElement(marketDescriptions);

            Assertions.assertThat(namelessDescription.getName(aLanguage)).isNull();
        }
    }

    @Test
    @Parameters(method = "exceptionHandlingStrategies")
    public void marketDefinitionWithNullNameIsHandedOverToCustomerCodeAndFailsUponNameRetrieval(
        ExceptionHandlingStrategy exceptionHandlingStrategy,
        ExpectationTowardsSdkErrorHandlingStrategy notUsed
    ) {
        context(c -> c.setProducer(LIVE_ODDS).setSportEventUrn(MATCH).setSportUrn(FOOTBALL), wireMock)
            .stubApiBookmakerAndProducersAnd(api ->
                api.stubMarketListContaining(nullifyName(oddEvenMarketDescription()), aLanguage)
            )
            .sdkWithFeed(sdk -> sdk.with(exceptionHandlingStrategy).withDefaultLanguage(aLanguage))
            .runScenario((sdk, dsl) -> {
                dsl.rabbitProducer.send(
                    dsl.messages.oddsChange(oddEvenMarket()),
                    dsl.routingKeys.liveOddsChange()
                );

                val market = theOnlyMarketIn(dsl.listinerWaitingFor.theOnlyOddsChange());
                MarketDefinition definition = market.getMarketDefinition();

                assertThat(definition.getNameTemplate()).isNull();
                assertThat(definition.getNameTemplate(aLanguage)).isNull();
            });
    }

    @Test
    @Parameters(method = "exceptionHandlingStrategies")
    public void marketWithNullNameIsHandedOverToCustomerCodeAndFailsUponNameRetrieval(
        ExceptionHandlingStrategy exceptionHandlingStrategy,
        ExpectationTowardsSdkErrorHandlingStrategy willFailRespectingSdkStrategy
    ) throws IOException, TimeoutException, InitException {
        globalVariables.setProducer(LIVE_ODDS);
        globalVariables.setSportEventUrn(MATCH);
        globalVariables.setSportUrn(FOOTBALL);
        FeedMessageBuilder messages = new FeedMessageBuilder(globalVariables);
        Locale aLanguage = Locale.ENGLISH;
        RoutingKeys routingKeys = new RoutingKeys(globalVariables);

        apiSimulator.defineBookmaker();
        apiSimulator.activateOnlyLiveProducer();
        apiSimulator.stubMarketListContaining(nullifyName(oddEvenMarketDescription()), aLanguage);

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
            rabbitProducer.send(messages.oddsChange(oddEvenMarket()), routingKeys.liveOddsChange());

            val market = theOnlyMarketIn(listinerWaitingFor.theOnlyOddsChange());

            MarketAssert.assertThat(market).getNameForDefault(aLanguage, willFailRespectingSdkStrategy);
        }
    }

    @Test
    @Parameters(method = "exceptionHandlingStrategies")
    public void structuredVariantMarketWithNullNameIsHandedOverToCustomerCodeAndFailsUponNameRetrieval(
        ExceptionHandlingStrategy exceptionHandlingStrategy,
        ExpectationTowardsSdkErrorHandlingStrategy willFailRespectingSdkStrategy
    ) throws IOException, TimeoutException, InitException {
        globalVariables.setProducer(LIVE_ODDS);
        globalVariables.setSportEventUrn(MATCH);
        globalVariables.setSportUrn(FOOTBALL);
        FeedMessageBuilder messages = new FeedMessageBuilder(globalVariables);
        Locale aLanguage = Locale.ENGLISH;
        RoutingKeys routingKeys = new RoutingKeys(globalVariables);

        apiSimulator.defineBookmaker();
        apiSimulator.activateOnlyLiveProducer();
        apiSimulator.stubMarketListContaining(nullifyName(exactGoalsMarketDescription()), aLanguage);
        apiSimulator.stubVariantListContaining(fivePlusVariantDescription(), aLanguage);

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
                messages.oddsChange(exactGoalsMarket(fivePlusVariant())),
                routingKeys.liveOddsChange()
            );

            val market = theOnlyMarketIn(listinerWaitingFor.theOnlyOddsChange());

            MarketAssert.assertThat(market).getNameForDefault(aLanguage, willFailRespectingSdkStrategy);
        }
    }

    @Test
    @Parameters(method = "exceptionHandlingStrategies")
    public void unstructuredVariantMarketWithNullNameIsHandedOverToCustomerCodeAndFailsUponNameRetrieval(
        ExceptionHandlingStrategy exceptionHandlingStrategy,
        ExpectationTowardsSdkErrorHandlingStrategy willFailRespectingSdkStrategy
    ) throws IOException, TimeoutException, InitException {
        globalVariables.setProducer(LIVE_ODDS);
        globalVariables.setSportEventUrn(MATCH);
        globalVariables.setSportUrn(FOOTBALL);
        FeedMessageBuilder messages = new FeedMessageBuilder(globalVariables);
        Locale aLanguage = Locale.ENGLISH;
        RoutingKeys routingKeys = new RoutingKeys(globalVariables);

        apiSimulator.defineBookmaker();
        apiSimulator.activateOnlyLiveProducer();
        apiSimulator.stubMarketListContaining(freeTextMarketDescription(), aLanguage);
        apiSimulator.stubSingleVariantMarket(nullifyName(nascarOutrightsMarketDescription()), aLanguage);

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
            rabbitProducer.send(messages.oddsChange(nascarOutrightsMarket()), routingKeys.liveOddsChange());

            val market = theOnlyMarketIn(listinerWaitingFor.theOnlyOddsChange());

            MarketAssert.assertThat(market).getNameForDefault(aLanguage, willFailRespectingSdkStrategy);
        }
    }

    private Object[] exceptionHandlingStrategies() {
        return new Object[][] {
            { Throw, WILL_THROW_EXCEPTIONS },
            { ExceptionHandlingStrategy.Catch, WILL_CATCH_EXCEPTIONS },
        };
    }

    private MarketWithOdds theOnlyMarketIn(
        OddsChange<com.sportradar.unifiedodds.sdk.entities.SportEvent> oddsChange
    ) {
        Assertions.assertThat(oddsChange.getMarkets().size()).isEqualTo(1);
        return oddsChange.getMarkets().get(0);
    }

    private static DescMarket nullifyName(DescMarket market) {
        market.setName(null);
        return market;
    }
}
