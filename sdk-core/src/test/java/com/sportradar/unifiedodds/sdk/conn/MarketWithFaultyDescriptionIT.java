/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */
package com.sportradar.unifiedodds.sdk.conn;

import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.wireMockConfig;
import static com.sportradar.unifiedodds.sdk.conn.SapiMarketDescriptions.FreeTextMarketDescription.freeTextMarketDescription;
import static com.sportradar.unifiedodds.sdk.conn.SapiMarketDescriptions.NascarOutrights.*;
import static com.sportradar.unifiedodds.sdk.conn.SapiMarketDescriptions.OddEven.oddEvenMarketDescription;
import static com.sportradar.unifiedodds.sdk.conn.SapiMarketDescriptions.notFoundWithEmptyMarket;
import static com.sportradar.unifiedodds.sdk.conn.UfMarkets.WithOdds.*;
import static com.sportradar.unifiedodds.sdk.conn.marketids.FreeTextMarketIds.*;
import static com.sportradar.unifiedodds.sdk.conn.marketids.FreeTextMarketIds.NascarOutrightsOddEvenVariant.nascarEvenOutcomeOf;
import static com.sportradar.unifiedodds.sdk.conn.marketids.OddEvenMarketIds.evenOutcomeOf;
import static com.sportradar.unifiedodds.sdk.impl.Constants.*;
import static com.sportradar.unifiedodds.sdk.impl.oddsentities.markets.ExpectationTowardsSdkErrorHandlingStrategy.WILL_CATCH_EXCEPTIONS;
import static com.sportradar.unifiedodds.sdk.impl.oddsentities.markets.ExpectationTowardsSdkErrorHandlingStrategy.WILL_THROW_EXCEPTIONS;
import static com.sportradar.unifiedodds.sdk.impl.oddsentities.markets.MarketAssert.assertThat;
import static com.sportradar.unifiedodds.sdk.impl.oddsentities.markets.OutcomeAssert.assertThat;
import static com.sportradar.unifiedodds.sdk.testutil.rabbit.integration.Credentials.with;
import static com.sportradar.unifiedodds.sdk.testutil.rabbit.integration.RabbitMqClientFactory.createRabbitMqClient;
import static com.sportradar.unifiedodds.sdk.testutil.rabbit.integration.RabbitMqProducer.connectDeclaringExchange;
import static com.sportradar.utils.Urns.SportEvents.getForAnyMatch;
import static com.sportradar.utils.domain.names.LanguageHolder.in;
import static com.sportradar.utils.domain.names.TranslationHolder.of;
import static java.util.Locale.*;
import static org.apache.http.HttpStatus.*;
import static org.assertj.core.api.Assertions.assertThat;

import com.github.tomakehurst.wiremock.junit.WireMockRule;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.http.client.Client;
import com.sportradar.uf.sportsapi.datamodel.DescMarket;
import com.sportradar.unifiedodds.sdk.ExceptionHandlingStrategy;
import com.sportradar.unifiedodds.sdk.exceptions.InitException;
import com.sportradar.unifiedodds.sdk.impl.Constants;
import com.sportradar.unifiedodds.sdk.impl.TimeUtilsImpl;
import com.sportradar.unifiedodds.sdk.impl.oddsentities.markets.ExpectationTowardsSdkErrorHandlingStrategy;
import com.sportradar.unifiedodds.sdk.oddsentities.MarketWithOdds;
import com.sportradar.unifiedodds.sdk.oddsentities.OddsChange;
import com.sportradar.unifiedodds.sdk.shared.FeedMessageBuilder;
import com.sportradar.unifiedodds.sdk.testutil.rabbit.integration.*;
import com.sportradar.utils.Urns;
import java.io.IOException;
import java.util.Locale;
import java.util.concurrent.TimeoutException;
import junitparams.JUnitParamsRunner;
import junitparams.Parameters;
import lombok.val;
import org.assertj.core.api.Assertions;
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

    private final GlobalVariables globalVariables = new GlobalVariables();
    private final ApiSimulator apiSimulator = new ApiSimulator(wireMockRule);

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

    public MarketWithFaultyDescriptionIT() throws Exception {}

    @Before
    public void setup() throws Exception {
        rabbitMqUserSetup.setupUser(sdkCredentials);
        sportsApiBaseUrl = BaseUrl.of("localhost", wireMockRule.port());
    }

    @After
    public void tearDownProxy() {
        rabbitMqUserSetup.revertChangesMade();
    }

    @Test
    @Parameters(method = "exceptionHandlingStrategies")
    public void marketNameRetrievalFailsWhenMarketDescriptionIsMissing(
        ExceptionHandlingStrategy exceptionHandlingStrategy,
        ExpectationTowardsSdkErrorHandlingStrategy willFailRespectingSdkStrategy
    ) throws IOException, TimeoutException, InitException {
        globalVariables.setProducer(ProducerId.LIVE_ODDS);
        globalVariables.setSportEventUrn(SportEvent.MATCH);
        globalVariables.setSportUrn(Sport.FOOTBALL);
        FeedMessageBuilder messages = new FeedMessageBuilder(globalVariables);
        Locale aLanguage = ENGLISH;
        RoutingKeys routingKeys = new RoutingKeys(globalVariables);

        apiSimulator.defineBookmaker();
        apiSimulator.activateOnlyLiveProducer();
        apiSimulator.stubEmptyMarketList(aLanguage);

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

            assertThat(market).getNameForGiven(aLanguage, willFailRespectingSdkStrategy);
        }
    }

    @Test
    @Parameters(method = "exceptionHandlingStrategies")
    public void outcomeNameRetrievalFailsWhenMarketDescriptionIsMissing(
        ExceptionHandlingStrategy exceptionHandlingStrategy,
        ExpectationTowardsSdkErrorHandlingStrategy willRespectSdkStrategy
    ) throws IOException, TimeoutException, InitException {
        globalVariables.setProducer(ProducerId.LIVE_ODDS);
        globalVariables.setSportEventUrn(SportEvent.MATCH);
        globalVariables.setSportUrn(Sport.FOOTBALL);
        FeedMessageBuilder messages = new FeedMessageBuilder(globalVariables);
        Locale aLanguage = ENGLISH;
        RoutingKeys routingKeys = new RoutingKeys(globalVariables);

        apiSimulator.defineBookmaker();
        apiSimulator.activateOnlyLiveProducer();
        apiSimulator.stubEmptyMarketList(aLanguage);

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
        globalVariables.setSportEventUrn(SportEvent.MATCH);
        globalVariables.setSportUrn(Sport.FOOTBALL);
        val messageingSimulatorFactory = new MessagingSimulator.Factory(
            exchangeLocation,
            adminCredentials,
            factory,
            new TimeUtilsImpl(),
            globalVariables
        );

        Locale aLanguage = ENGLISH;

        apiSimulator.defineBookmaker();
        apiSimulator.activateOnlyLiveProducer();
        apiSimulator.stubMarketListContaining(oddEvenDescriptionMissingEvenOutcome(), aLanguage);

        try (
            val messagingSimulator = messageingSimulatorFactory.connectDeclaringExchange();
            val sdk = SdkSetup
                .with(sdkCredentials, RABBIT_BASE_URL, sportsApiBaseUrl, globalVariables.getNodeId())
                .with(ListenerCollectingMessages.to(messagesStorage))
                .with(exceptionHandlingStrategy)
                .withDefaultLanguage(aLanguage)
                .with1Session()
                .withOpenedFeed()
        ) {
            messagingSimulator.send(m -> m.oddsChange(oddEvenMarket()), RoutingKeys::liveOddsChange);

            val oddsChange = listinerWaitingFor.theOnlyOddsChange();
            val outcome = evenOutcomeOf(theOnlyMarketIn(oddsChange));

            assertThat(outcome)
                .nameIsNotBackedByOutcomeDescriptionForDefaultLanguage(aLanguage, willRespectSdkStrategy);
        }
    }

    @Test
    @Parameters(method = "exceptionHandlingStrategies")
    public void singleVariantMarketApiFailuresAreThrottledPerLanguage(
        ExceptionHandlingStrategy exceptionHandlingStrategy,
        ExpectationTowardsSdkErrorHandlingStrategy willRespectSdkStrategy
    ) throws IOException, TimeoutException, InitException {
        globalVariables.setProducer(ProducerId.LIVE_ODDS);
        globalVariables.setSportEventUrn(SportEvent.MATCH);
        globalVariables.setSportUrn(Sport.FOOTBALL);
        FeedMessageBuilder messages = new FeedMessageBuilder(globalVariables);
        Locale langA = ENGLISH;
        Locale langB = FRENCH;
        RoutingKeys routingKeys = new RoutingKeys(globalVariables);

        apiSimulator.defineBookmaker();
        apiSimulator.activateOnlyLiveProducer();
        apiSimulator.stubMarketListContaining(freeTextMarketDescription(), langA);
        apiSimulator.stubMarketListContaining(freeTextMarketDescription(), langB);
        apiSimulator.stubEmptyVariantList(langA);
        apiSimulator.stubEmptyVariantList(langB);
        apiSimulator.stubSingleVariantMarket(
            SC_SERVICE_UNAVAILABLE,
            FREE_TEXT_MARKET_ID,
            nascarOutrightsOddEvenVariant(),
            langA
        );
        apiSimulator.stubSingleVariantMarket(nascarOutrightsOddEvenMarketDescription(langB), langB);

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
                .with1Session()
                .withOpenedFeed()
        ) {
            rabbitProducer.send(
                messages.oddsChange(nascarOutrightsOddEvenMarket()),
                routingKeys.liveOddsChange()
            );

            val oddsChange = listinerWaitingFor.theOnlyOddsChange();
            MarketWithOdds market = theOnlyMarketIn(oddsChange);
            val outcome = nascarEvenOutcomeOf(market);

            assertThat(outcome)
                .nameIsNotBackedByOutcomeDescriptionForDefaultLanguage(langA, willRespectSdkStrategy);
            assertThat(outcome)
                .hasNameInNonDefaultLanguage(of(nascarEvenOutcomeDescription(langB).getName(), in(langB)));

            apiSimulator.stubSingleVariantMarket(nascarOutrightsOddEvenMarketDescription(langA), langA);

            assertThat(outcome)
                .nameIsNotBackedByOutcomeDescriptionForDefaultLanguage(langA, willRespectSdkStrategy);
            assertThat(outcome)
                .hasNameInNonDefaultLanguage(of(nascarEvenOutcomeDescription(langB).getName(), in(langB)));
        }
    }

    @Test
    @Parameters(method = "exceptionHandlingStrategies")
    public void singleVariantMarketDescriptionNotFoundResponsesForOneLanguageDoNotInterfereWithOtherLanguages(
        ExceptionHandlingStrategy exceptionHandlingStrategy,
        ExpectationTowardsSdkErrorHandlingStrategy willRespectSdkStrategy
    ) throws IOException, TimeoutException, InitException {
        globalVariables.setProducer(ProducerId.LIVE_ODDS);
        globalVariables.setSportEventUrn(SportEvent.MATCH);
        globalVariables.setSportUrn(Sport.FOOTBALL);
        FeedMessageBuilder messages = new FeedMessageBuilder(globalVariables);
        Locale langA = ENGLISH;
        Locale langB = FRENCH;
        RoutingKeys routingKeys = new RoutingKeys(globalVariables);

        apiSimulator.defineBookmaker();
        apiSimulator.activateOnlyLiveProducer();
        apiSimulator.stubMarketListContaining(freeTextMarketDescription(), langA);
        apiSimulator.stubMarketListContaining(freeTextMarketDescription(), langB);
        apiSimulator.stubEmptyVariantList(langA);
        apiSimulator.stubEmptyVariantList(langB);
        apiSimulator.stubSingleVariantMarket(
            SC_OK,
            FREE_TEXT_MARKET_ID,
            nascarOutrightsOddEvenVariant(),
            langA,
            notFoundWithEmptyMarket()
        );
        apiSimulator.stubSingleVariantMarket(nascarOutrightsOddEvenMarketDescription(langB), langB);

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
                .with1Session()
                .withOpenedFeed()
        ) {
            rabbitProducer.send(
                messages.oddsChange(nascarOutrightsOddEvenMarket()),
                routingKeys.liveOddsChange()
            );

            val oddsChange = listinerWaitingFor.theOnlyOddsChange();
            MarketWithOdds market = theOnlyMarketIn(oddsChange);

            Assertions.assertThat(market.getName(langA)).isEqualTo(freeTextMarketDescription().getName());

            Assertions
                .assertThat(market.getName(langB))
                .isEqualTo(nascarOutrightsOddEvenMarketDescription(langB).getName());
        }
    }

    @Test
    @Parameters(method = "exceptionHandlingStrategies")
    public void singleVariantMarketDescriptionNotFoundHttpStatusForOneLanguageDoNotInterfereWithOtherLanguages(
        ExceptionHandlingStrategy exceptionHandlingStrategy,
        ExpectationTowardsSdkErrorHandlingStrategy willRespectSdkStrategy
    ) throws IOException, TimeoutException, InitException {
        globalVariables.setProducer(ProducerId.LIVE_ODDS);
        globalVariables.setSportEventUrn(SportEvent.MATCH);
        globalVariables.setSportUrn(Sport.FOOTBALL);
        FeedMessageBuilder messages = new FeedMessageBuilder(globalVariables);
        Locale langA = ENGLISH;
        Locale langB = FRENCH;
        RoutingKeys routingKeys = new RoutingKeys(globalVariables);

        apiSimulator.defineBookmaker();
        apiSimulator.activateOnlyLiveProducer();
        apiSimulator.stubMarketListContaining(freeTextMarketDescription(), langA);
        apiSimulator.stubMarketListContaining(freeTextMarketDescription(), langB);
        apiSimulator.stubEmptyVariantList(langA);
        apiSimulator.stubEmptyVariantList(langB);
        apiSimulator.stubSingleVariantMarket(
            SC_NOT_FOUND,
            FREE_TEXT_MARKET_ID,
            nascarOutrightsOddEvenVariant(),
            langA
        );
        apiSimulator.stubSingleVariantMarket(nascarOutrightsOddEvenMarketDescription(langB), langB);

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
                .with1Session()
                .withOpenedFeed()
        ) {
            rabbitProducer.send(
                messages.oddsChange(nascarOutrightsOddEvenMarket()),
                routingKeys.liveOddsChange()
            );

            val oddsChange = listinerWaitingFor.theOnlyOddsChange();
            MarketWithOdds market = theOnlyMarketIn(oddsChange);

            Assertions.assertThat(market.getName(langA)).isEqualTo(freeTextMarketDescription().getName());

            Assertions
                .assertThat(market.getName(langB))
                .isEqualTo(nascarOutrightsOddEvenMarketDescription(langB).getName());
        }
    }

    @Test
    @Parameters(method = "exceptionHandlingStrategies")
    public void singleVariantMarketApiFailuresAreThrottledPerLanguageForTwoSubsequentMessages(
        ExceptionHandlingStrategy exceptionHandlingStrategy,
        ExpectationTowardsSdkErrorHandlingStrategy willRespectSdkStrategy
    ) throws IOException, TimeoutException, InitException {
        globalVariables.setProducer(ProducerId.LIVE_ODDS);
        globalVariables.setSportEventUrn(SportEvent.MATCH);
        globalVariables.setSportUrn(Sport.FOOTBALL);
        FeedMessageBuilder messages = new FeedMessageBuilder(globalVariables);
        Locale langA = ENGLISH;
        Locale langB = FRENCH;
        RoutingKeys routingKeys = new RoutingKeys(globalVariables);

        apiSimulator.defineBookmaker();
        apiSimulator.activateOnlyLiveProducer();
        apiSimulator.stubMarketListContaining(freeTextMarketDescription(), langA);
        apiSimulator.stubMarketListContaining(freeTextMarketDescription(), langB);
        apiSimulator.stubEmptyVariantList(langA);
        apiSimulator.stubEmptyVariantList(langB);
        apiSimulator.stubSingleVariantMarket(
            SC_SERVICE_UNAVAILABLE,
            FREE_TEXT_MARKET_ID,
            nascarOutrightsOddEvenVariant(),
            langA
        );
        apiSimulator.stubSingleVariantMarket(nascarOutrightsOddEvenMarketDescription(langB), langB);

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
                .with1Session()
                .withOpenedFeed()
        ) {
            rabbitProducer.send(
                messages.oddsChange(nascarOutrightsOddEvenMarket()),
                routingKeys.liveOddsChange()
            );

            val oddsChange = listinerWaitingFor.theOnlyOddsChange();
            MarketWithOdds market = theOnlyMarketIn(oddsChange);
            val outcome = nascarEvenOutcomeOf(market);

            assertThat(outcome)
                .nameIsNotBackedByOutcomeDescriptionForDefaultLanguage(langA, willRespectSdkStrategy);
            assertThat(outcome)
                .hasNameInNonDefaultLanguage(of(nascarEvenOutcomeDescription(langB).getName(), in(langB)));

            rabbitProducer.send(
                messages.oddsChange(nascarOutrightsOddEvenMarket()),
                routingKeys.liveOddsChange()
            );

            apiSimulator.stubSingleVariantMarket(nascarOutrightsOddEvenMarketDescription(langA), langA);
            val oddsChange1 = listinerWaitingFor.secondOddsChange();
            MarketWithOdds market1 = theOnlyMarketIn(oddsChange1);
            val outcome1 = nascarEvenOutcomeOf(market1);

            assertThat(outcome1)
                .nameIsNotBackedByOutcomeDescriptionForDefaultLanguage(langA, willRespectSdkStrategy);
            assertThat(outcome1)
                .hasNameInNonDefaultLanguage(of(nascarEvenOutcomeDescription(langB).getName(), in(langB)));
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
        DescMarket incompleteOddEvenDescription = SapiMarketDescriptions.OddEven.oddEvenMarketDescription();
        incompleteOddEvenDescription.getOutcomes().getOutcome().remove(1);
        return incompleteOddEvenDescription;
    }
}
