/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */
package com.sportradar.unifiedodds.sdk.conn;

import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.wireMockConfig;
import static com.sportradar.unifiedodds.sdk.conn.OddEvenMarket.evenOutcomeOf;
import static com.sportradar.unifiedodds.sdk.conn.SapiMarketDescriptions.oddEvenDescription;
import static com.sportradar.unifiedodds.sdk.conn.UfMarkets.WithOdds.oddEven;
import static com.sportradar.unifiedodds.sdk.impl.Constants.*;
import static com.sportradar.unifiedodds.sdk.impl.oddsentities.markets.ExpectationTowardsSdkErrorHandlingStrategy.WILL_CATCH_EXCEPTIONS;
import static com.sportradar.unifiedodds.sdk.impl.oddsentities.markets.ExpectationTowardsSdkErrorHandlingStrategy.WILL_THROW_EXCEPTIONS;
import static com.sportradar.unifiedodds.sdk.impl.oddsentities.markets.MarketAssert.assertThat;
import static com.sportradar.unifiedodds.sdk.impl.oddsentities.markets.OutcomeAssert.assertThat;
import static com.sportradar.unifiedodds.sdk.testutil.rabbit.integration.Credentials.with;
import static com.sportradar.unifiedodds.sdk.testutil.rabbit.integration.RabbitMqClientFactory.createRabbitMqClient;
import static com.sportradar.unifiedodds.sdk.testutil.rabbit.integration.RabbitMqProducer.connectDeclaringExchange;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.github.tomakehurst.wiremock.junit.WireMockRule;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.http.client.Client;
import com.sportradar.uf.sportsapi.datamodel.DescMarket;
import com.sportradar.unifiedodds.sdk.ExceptionHandlingStrategy;
import com.sportradar.unifiedodds.sdk.exceptions.InitException;
import com.sportradar.unifiedodds.sdk.exceptions.NameGenerationException;
import com.sportradar.unifiedodds.sdk.impl.Constants;
import com.sportradar.unifiedodds.sdk.impl.TimeUtilsImpl;
import com.sportradar.unifiedodds.sdk.impl.oddsentities.markets.ExpectationTowardsSdkErrorHandlingStrategy;
import com.sportradar.unifiedodds.sdk.impl.oddsentities.markets.MarketAssert;
import com.sportradar.unifiedodds.sdk.impl.oddsentities.markets.OutcomeAssert;
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
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
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
    }
)
public class MarketWithFaultyDescriptionIT {

    @Rule
    public WireMockRule wireMockRule = new WireMockRule(wireMockConfig().dynamicPort());

    private GlobalVariables globalVariables = new GlobalVariables();
    private ApiSimulator apiSimulator = new ApiSimulator(wireMockRule);

    private Credentials sdkCredentials = Credentials.with(Constants.SDK_USERNAME, Constants.SDK_PASSWORD);
    private VhostLocation vhostLocation = VhostLocation.at(RABBIT_BASE_URL, Constants.UF_VIRTUALHOST);
    private ExchangeLocation exchangeLocation = ExchangeLocation.at(vhostLocation, Constants.UF_EXCHANGE);
    private Credentials adminCredentials = Credentials.with(
        Constants.ADMIN_USERNAME,
        Constants.ADMIN_PASSWORD
    );
    private ConnectionFactory factory = new ConnectionFactory();
    private MessagesInMemoryStorage messagesStorage = new MessagesInMemoryStorage();

    private WaiterForSingleMessage listinerWaitingFor = new WaiterForSingleMessage(messagesStorage);
    private final Client rabbitMqClient = createRabbitMqClient(
        RABBIT_IP,
        with(ADMIN_USERNAME, ADMIN_PASSWORD),
        Client::new
    );
    private RabbitMqUserSetup rabbitMqUserSetup = RabbitMqUserSetup.create(
        VhostLocation.at(RABBIT_BASE_URL, UF_VIRTUALHOST),
        rabbitMqClient
    );

    private BaseUrl sportsApiBaseUrl;

    public MarketWithFaultyDescriptionIT() throws Exception {}

    @Before
    public void setup() throws Exception {
        rabbitMqUserSetup.setupUser(sdkCredentials);
        sportsApiBaseUrl = BaseUrl.of("localhost", wireMockRule.port());
    }

    @After
    public void tearDownProxy() throws Exception {
        rabbitMqUserSetup.revertChangesMade();
    }

    @Test
    @Parameters(method = "exceptionHandlingStrategies")
    public void marketNameRetrievalFailsWhenMarketDescriptionIsMissing(
        ExceptionHandlingStrategy exceptionHandlingStrategy,
        ExpectationTowardsSdkErrorHandlingStrategy willRespectSdkStrategy
    ) throws IOException, TimeoutException, InitException {
        globalVariables.setProducer(ProducerId.LIVE_ODDS);
        FeedMessageBuilder messages = new FeedMessageBuilder(ProducerId.LIVE_ODDS, SportEvent.MATCH);
        final int nodeId = 1;
        Locale aLanguage = Locale.ENGLISH;
        RoutingKeys routingKeys = new RoutingKeys(
            ProducerId.LIVE_ODDS,
            Sport.FOOTBALL,
            SportEvent.MATCH,
            nodeId
        );

        apiSimulator.defineBookmaker();
        apiSimulator.activateOnlyLiveProducer();
        apiSimulator.stubEmptyMarketList();

        try (
            val rabbitProducer = connectDeclaringExchange(
                exchangeLocation,
                adminCredentials,
                factory,
                new TimeUtilsImpl()
            );
            val sdk = SdkSetup
                .with(sdkCredentials, RABBIT_BASE_URL, sportsApiBaseUrl, nodeId)
                .with(ListenerCollectingMessages.to(messagesStorage))
                .with(exceptionHandlingStrategy)
                .withDefaultLanguage(aLanguage)
                .with1Session()
                .withOpenedFeed()
        ) {
            rabbitProducer.send(messages.oddsChange(oddEven()), routingKeys.liveOddsChange());

            val market = theOnlyMarketIn(listinerWaitingFor.theOnlyOddsChange());

            assertThat(market)
                .nameIsNotBackedByMarketDescriptionForDefaultLanguage(aLanguage, willRespectSdkStrategy);
        }
    }

    @Test
    @Parameters(method = "exceptionHandlingStrategies")
    public void outcomeNameRetrievalFailsWhenMarketDescriptionIsMissing(
        ExceptionHandlingStrategy exceptionHandlingStrategy,
        ExpectationTowardsSdkErrorHandlingStrategy willRespectSdkStrategy
    ) throws IOException, TimeoutException, InitException {
        globalVariables.setProducer(ProducerId.LIVE_ODDS);
        FeedMessageBuilder messages = new FeedMessageBuilder(ProducerId.LIVE_ODDS, SportEvent.MATCH);
        final int nodeId = 1;
        Locale aLanguage = Locale.ENGLISH;
        RoutingKeys routingKeys = new RoutingKeys(
            ProducerId.LIVE_ODDS,
            Sport.FOOTBALL,
            SportEvent.MATCH,
            nodeId
        );

        apiSimulator.defineBookmaker();
        apiSimulator.activateOnlyLiveProducer();
        apiSimulator.stubEmptyMarketList();

        try (
            val rabbitProducer = connectDeclaringExchange(
                exchangeLocation,
                adminCredentials,
                factory,
                new TimeUtilsImpl()
            );
            val sdk = SdkSetup
                .with(sdkCredentials, RABBIT_BASE_URL, sportsApiBaseUrl, nodeId)
                .with(ListenerCollectingMessages.to(messagesStorage))
                .with(exceptionHandlingStrategy)
                .withDefaultLanguage(aLanguage)
                .with1Session()
                .withOpenedFeed()
        ) {
            rabbitProducer.send(messages.oddsChange(oddEven()), routingKeys.liveOddsChange());

            val oddsChange = listinerWaitingFor.theOnlyOddsChange();
            val outcome = evenOutcomeOf(theOnlyMarketIn(oddsChange));

            assertThat(outcome)
                .nameIsNotBackedByMarketDescriptionForDefaultLanguage(aLanguage, willRespectSdkStrategy);
        }
    }

    @Test
    @Parameters(method = "exceptionHandlingStrategies")
    public void outcomeNameRetrievalFailsWhenOutcomeDescriptionIsMissing(
        ExceptionHandlingStrategy exceptionHandlingStrategy,
        ExpectationTowardsSdkErrorHandlingStrategy willRespectSdkStrategy
    ) throws IOException, TimeoutException, InitException {
        globalVariables.setProducer(ProducerId.LIVE_ODDS);
        FeedMessageBuilder messages = new FeedMessageBuilder(ProducerId.LIVE_ODDS, SportEvent.MATCH);
        final int nodeId = 1;
        Locale aLanguage = Locale.ENGLISH;
        RoutingKeys routingKeys = new RoutingKeys(
            ProducerId.LIVE_ODDS,
            Sport.FOOTBALL,
            SportEvent.MATCH,
            nodeId
        );

        apiSimulator.defineBookmaker();
        apiSimulator.activateOnlyLiveProducer();
        apiSimulator.stubMarketListContaining(oddEvenDescriptionMissingEvenOutcome());

        try (
            val rabbitProducer = connectDeclaringExchange(
                exchangeLocation,
                adminCredentials,
                factory,
                new TimeUtilsImpl()
            );
            val sdk = SdkSetup
                .with(sdkCredentials, RABBIT_BASE_URL, sportsApiBaseUrl, nodeId)
                .with(ListenerCollectingMessages.to(messagesStorage))
                .with(exceptionHandlingStrategy)
                .withDefaultLanguage(aLanguage)
                .with1Session()
                .withOpenedFeed()
        ) {
            rabbitProducer.send(messages.oddsChange(oddEven()), routingKeys.liveOddsChange());

            val oddsChange = listinerWaitingFor.theOnlyOddsChange();
            val outcome = evenOutcomeOf(theOnlyMarketIn(oddsChange));

            assertThat(outcome)
                .nameIsNotBackedByOutcomeDescriptionForDefaultLanguage(aLanguage, willRespectSdkStrategy);
        }
    }

    private Object[] exceptionHandlingStrategies() {
        return new Object[][] {
            { ExceptionHandlingStrategy.Throw, WILL_THROW_EXCEPTIONS },
            { ExceptionHandlingStrategy.Catch, WILL_CATCH_EXCEPTIONS },
        };
    }

    private MarketWithOdds theOnlyMarketIn(
        OddsChange<com.sportradar.unifiedodds.sdk.entities.SportEvent> oddsChange
    ) {
        assertThat(oddsChange.getMarkets().size()).isEqualTo(1);
        return oddsChange.getMarkets().get(0);
    }

    private static DescMarket oddEvenDescriptionMissingEvenOutcome() {
        DescMarket incompleteOddEvenDescription = oddEvenDescription();
        incompleteOddEvenDescription.getOutcomes().getOutcome().remove(1);
        return incompleteOddEvenDescription;
    }
}
