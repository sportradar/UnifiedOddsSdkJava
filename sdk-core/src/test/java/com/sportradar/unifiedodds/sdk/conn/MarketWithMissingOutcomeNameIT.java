/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */
package com.sportradar.unifiedodds.sdk.conn;

import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.wireMockConfig;
import static com.google.common.collect.Iterables.getOnlyElement;
import static com.sportradar.unifiedodds.sdk.ExceptionHandlingStrategy.Throw;
import static com.sportradar.unifiedodds.sdk.conn.SapiMarketDescriptions.CorrectScoreFlex.correctScoreFlexMarketDescription;
import static com.sportradar.unifiedodds.sdk.conn.SapiMarketDescriptions.ExactGoals.exactGoalsMarketDescription;
import static com.sportradar.unifiedodds.sdk.conn.SapiMarketDescriptions.FreeTextMarketDescription.freeTextMarketDescription;
import static com.sportradar.unifiedodds.sdk.conn.SapiMarketDescriptions.NascarOutrights.nascarOutrightsMarketDescription;
import static com.sportradar.unifiedodds.sdk.conn.SapiMarketDescriptions.OddEven.oddEvenMarketDescription;
import static com.sportradar.unifiedodds.sdk.conn.SapiMarketDescriptions.OneXtwo.oneXtwoMarketDescription;
import static com.sportradar.unifiedodds.sdk.conn.SapiVariantDescriptions.ExactGoals.fivePlusVariantDescription;
import static com.sportradar.unifiedodds.sdk.conn.UfMarkets.WithOdds.*;
import static com.sportradar.unifiedodds.sdk.conn.marketids.ExactGoalsMarketIds.fivePlusVariant;
import static com.sportradar.unifiedodds.sdk.impl.Constants.*;
import static com.sportradar.unifiedodds.sdk.impl.oddsentities.markets.ExpectationTowardsSdkErrorHandlingStrategy.WILL_CATCH_EXCEPTIONS;
import static com.sportradar.unifiedodds.sdk.impl.oddsentities.markets.ExpectationTowardsSdkErrorHandlingStrategy.WILL_THROW_EXCEPTIONS;
import static com.sportradar.unifiedodds.sdk.testutil.generic.naturallanguage.Prepositions.*;
import static com.sportradar.unifiedodds.sdk.testutil.rabbit.integration.Credentials.with;
import static com.sportradar.unifiedodds.sdk.testutil.rabbit.integration.RabbitMqClientFactory.createRabbitMqClient;
import static com.sportradar.unifiedodds.sdk.testutil.rabbit.integration.RabbitMqProducer.connectDeclaringExchange;
import static java.util.stream.Collectors.toList;

import com.github.tomakehurst.wiremock.junit.WireMockRule;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.http.client.Client;
import com.sportradar.uf.sportsapi.datamodel.DescMarket;
import com.sportradar.uf.sportsapi.datamodel.DescVariant;
import com.sportradar.uf.sportsapi.datamodel.Mappings;
import com.sportradar.unifiedodds.sdk.ExceptionHandlingStrategy;
import com.sportradar.unifiedodds.sdk.conn.marketids.FlexScoreMarketIds;
import com.sportradar.unifiedodds.sdk.conn.marketids.OddEvenMarketIds;
import com.sportradar.unifiedodds.sdk.entities.markets.MarketDescription;
import com.sportradar.unifiedodds.sdk.entities.markets.MarketMappingData;
import com.sportradar.unifiedodds.sdk.entities.markets.OutcomeDescription;
import com.sportradar.unifiedodds.sdk.entities.markets.OutcomeMappingData;
import com.sportradar.unifiedodds.sdk.exceptions.InitException;
import com.sportradar.unifiedodds.sdk.impl.Constants;
import com.sportradar.unifiedodds.sdk.impl.TimeUtilsImpl;
import com.sportradar.unifiedodds.sdk.impl.oddsentities.markets.ExpectationTowardsSdkErrorHandlingStrategy;
import com.sportradar.unifiedodds.sdk.impl.oddsentities.markets.OutcomeAssert;
import com.sportradar.unifiedodds.sdk.oddsentities.MarketWithOdds;
import com.sportradar.unifiedodds.sdk.oddsentities.OddsChange;
import com.sportradar.unifiedodds.sdk.oddsentities.OutcomeDefinition;
import com.sportradar.unifiedodds.sdk.oddsentities.OutcomeOdds;
import com.sportradar.unifiedodds.sdk.shared.FeedMessageBuilder;
import com.sportradar.unifiedodds.sdk.testutil.rabbit.integration.*;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.TimeoutException;
import java.util.function.Function;
import java.util.function.Predicate;
import junitparams.JUnitParamsRunner;
import junitparams.Parameters;
import lombok.val;
import org.assertj.core.api.Assertions;
import org.junit.*;
import org.junit.runner.RunWith;

@RunWith(JUnitParamsRunner.class)
@Ignore
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
public class MarketWithMissingOutcomeNameIT {

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

    public MarketWithMissingOutcomeNameIT() throws Exception {}

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
    public void marketOutcomeHasEmptyName(
        ExceptionHandlingStrategy exceptionHandlingStrategy,
        ExpectationTowardsSdkErrorHandlingStrategy unused
    ) throws Exception {
        globalVariables.setProducer(ProducerId.LIVE_ODDS);
        Locale aLanguage = Locale.ENGLISH;

        apiSimulator.defineBookmaker();
        apiSimulator.activateOnlyLiveProducer();
        apiSimulator.stubMarketListContaining(nullifyFirstOutcomeName(oneXtwoMarketDescription()), aLanguage);

        try (
            val sdk = SdkSetup
                .with(sdkCredentials, RABBIT_BASE_URL, sportsApiBaseUrl, globalVariables.getNodeId())
                .with(ListenerCollectingMessages.to(messagesStorage))
                .with(exceptionHandlingStrategy)
                .withDefaultLanguage(aLanguage)
                .withoutFeed()
        ) {
            val marketDescriptions = sdk.getMarketDescriptionManager().getMarketDescriptions();
            val description = getOnlyElement(marketDescriptions);
            val faultyOutcome = firstOutcomeOf(description);

            Assertions.assertThat(faultyOutcome.getName(aLanguage)).isEmpty();
        }
    }

    @Test
    @Parameters(method = "exceptionHandlingStrategies")
    public void marketWithNullOutcomeNameIsHandedOverToCustomerCodeAndFailsUponNameRetrieval(
        ExceptionHandlingStrategy exceptionHandlingStrategy,
        ExpectationTowardsSdkErrorHandlingStrategy willFailRespectingSdkStrategy
    ) throws IOException, TimeoutException, InitException {
        globalVariables.setProducer(ProducerId.LIVE_ODDS);
        globalVariables.setSportEventUrn(SportEvent.MATCH);
        globalVariables.setSportUrn(Sport.FOOTBALL);
        FeedMessageBuilder messages = new FeedMessageBuilder(globalVariables);
        Locale aLanguage = Locale.ENGLISH;
        RoutingKeys routingKeys = new RoutingKeys(globalVariables);

        apiSimulator.defineBookmaker();
        apiSimulator.activateOnlyLiveProducer();
        apiSimulator.stubMarketListContaining(nullifyFirstOutcomeName(oddEvenMarketDescription()), aLanguage);

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
            val faultyOutcome = firstOutcomeOf(market);

            OutcomeAssert.assertThat(faultyOutcome).getNameForGiven(aLanguage, willFailRespectingSdkStrategy);
        }
    }

    @Test
    @Parameters(method = "exceptionHandlingStrategies")
    public void marketWithNullOutcomeNameTemplateIsHandedOverToCustomerCodeAsNull(
        ExceptionHandlingStrategy exceptionHandlingStrategy,
        ExpectationTowardsSdkErrorHandlingStrategy notImportant
    ) throws IOException, TimeoutException, InitException {
        globalVariables.setProducer(ProducerId.LIVE_ODDS);
        globalVariables.setSportEventUrn(SportEvent.MATCH);
        globalVariables.setSportUrn(Sport.FOOTBALL);
        FeedMessageBuilder messages = new FeedMessageBuilder(globalVariables);
        Locale aLanguage = Locale.ENGLISH;
        RoutingKeys routingKeys = new RoutingKeys(globalVariables);

        val outcomeId = OddEvenMarketIds.anyOddEvenOutcomeId();

        apiSimulator.defineBookmaker();
        apiSimulator.activateOnlyLiveProducer();
        apiSimulator.stubMarketListContaining(
            nullifyOutcomeName(oddEvenMarketDescription(), outcomeId),
            aLanguage
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
            rabbitProducer.send(messages.oddsChange(oddEvenMarket()), routingKeys.liveOddsChange());

            val market = theOnlyMarketIn(listinerWaitingFor.theOnlyOddsChange());
            val nameTemplate = getOutcomeDefinition(market, outcomeId).getNameTemplate();

            Assertions.assertThat(nameTemplate).isEmpty();
        }
    }

    @Test
    @Parameters(method = "exceptionHandlingStrategies")
    public void invariantFlexScoreMarketWithNullOutcomeNameIsHandedOverToCustomerCodeAndFailsUponNameRetrieval(
        ExceptionHandlingStrategy exceptionHandlingStrategy,
        ExpectationTowardsSdkErrorHandlingStrategy willFailRespectingSdkStrategy
    ) throws IOException, TimeoutException, InitException {
        globalVariables.setProducer(ProducerId.LIVE_ODDS);
        globalVariables.setSportEventUrn(SportEvent.MATCH);
        globalVariables.setSportUrn(Sport.FOOTBALL);
        FeedMessageBuilder messages = new FeedMessageBuilder(globalVariables);
        Locale aLanguage = Locale.ENGLISH;
        RoutingKeys routingKeys = new RoutingKeys(globalVariables);

        apiSimulator.defineBookmaker();
        apiSimulator.activateOnlyLiveProducer();
        apiSimulator.stubMarketListContaining(
            nullifyFirstOutcomeName(correctScoreFlexMarketDescription(aLanguage)),
            aLanguage
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
            rabbitProducer.send(messages.oddsChange(correctScoreFlexMarket()), routingKeys.liveOddsChange());

            val market = theOnlyMarketIn(listinerWaitingFor.theOnlyOddsChange());
            val faultyOutcome = firstOutcomeOf(market);

            OutcomeAssert.assertThat(faultyOutcome).getNameForGiven(aLanguage, willFailRespectingSdkStrategy);
        }
    }

    @Test
    @Parameters(method = "exceptionHandlingStrategies")
    public void invariantFlexScoreFutsalMarketWithNullMappingOutcomeNameIsHandedOverToCustomerCodeAndReturnsNull(
        ExceptionHandlingStrategy exceptionHandlingStrategy,
        ExpectationTowardsSdkErrorHandlingStrategy notImportant
    ) throws IOException, TimeoutException, InitException {
        globalVariables.setProducer(ProducerId.LIVE_ODDS);
        globalVariables.setSportEventUrn(SportEvent.MATCH);
        globalVariables.setSportUrn(Sport.FOOTBALL);
        FeedMessageBuilder messages = new FeedMessageBuilder(globalVariables);
        Locale aLanguage = Locale.ENGLISH;
        RoutingKeys routingKeys = new RoutingKeys(globalVariables);

        apiSimulator.defineBookmaker();
        apiSimulator.activateOnlyLiveProducer();
        apiSimulator.stubMarketListContaining(
            nullifyMappingOutcomeNameForZeroToZeroOutcome(correctScoreFlexMarketDescription(aLanguage)),
            aLanguage
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
            rabbitProducer.send(messages.oddsChange(correctScoreFlexMarket()), routingKeys.liveOddsChange());

            val market = theOnlyMarketIn(listinerWaitingFor.theOnlyOddsChange());

            val validMapping = getValidMapping(market, aLanguage);
            val producerOutcomeNamesForZeroToZeroOutcome = getProducerOutcomeNameForZeroToZeroOutcome(
                validMapping,
                aLanguage
            );

            Assertions.assertThat(producerOutcomeNamesForZeroToZeroOutcome).isNull();
        }
    }

    @Test
    @Parameters(method = "exceptionHandlingStrategies")
    public void structuredVariantMarketWithNullNameIsHandedOverToCustomerCodeAndFailsUponNameRetrieval(
        ExceptionHandlingStrategy exceptionHandlingStrategy,
        ExpectationTowardsSdkErrorHandlingStrategy willFailRespectingSdkStrategy
    ) throws IOException, TimeoutException, InitException {
        globalVariables.setProducer(ProducerId.LIVE_ODDS);
        globalVariables.setSportEventUrn(SportEvent.MATCH);
        globalVariables.setSportUrn(Sport.FOOTBALL);
        FeedMessageBuilder messages = new FeedMessageBuilder(globalVariables);
        Locale aLanguage = Locale.ENGLISH;
        RoutingKeys routingKeys = new RoutingKeys(globalVariables);

        apiSimulator.defineBookmaker();
        apiSimulator.activateOnlyLiveProducer();
        apiSimulator.stubMarketListContaining(exactGoalsMarketDescription(), aLanguage);
        apiSimulator.stubVariantListContaining(
            nullifyFirstOutcomeName(fivePlusVariantDescription()),
            aLanguage
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
                messages.oddsChange(exactGoalsMarket(fivePlusVariant())),
                routingKeys.liveOddsChange()
            );

            val market = theOnlyMarketIn(listinerWaitingFor.theOnlyOddsChange());
            val faultyOutcome = firstOutcomeOf(market);

            OutcomeAssert
                .assertThat(faultyOutcome)
                .getNameForDefault(aLanguage, willFailRespectingSdkStrategy);
        }
    }

    @Test
    @Parameters(method = "exceptionHandlingStrategies")
    public void unstructuredVariantMarketWithNullNameIsHandedOverToCustomerCodeAndFailsUponNameRetrieval(
        ExceptionHandlingStrategy exceptionHandlingStrategy,
        ExpectationTowardsSdkErrorHandlingStrategy willFailRespectingSdkStrategy
    ) throws IOException, TimeoutException, InitException {
        globalVariables.setProducer(ProducerId.LIVE_ODDS);
        globalVariables.setSportEventUrn(SportEvent.MATCH);
        globalVariables.setSportUrn(Sport.FOOTBALL);
        FeedMessageBuilder messages = new FeedMessageBuilder(globalVariables);
        Locale aLanguage = Locale.ENGLISH;
        RoutingKeys routingKeys = new RoutingKeys(globalVariables);

        apiSimulator.defineBookmaker();
        apiSimulator.activateOnlyLiveProducer();
        apiSimulator.stubMarketListContaining(freeTextMarketDescription(), aLanguage);
        apiSimulator.stubSingleVariantMarket(
            nullifyFirstOutcomeName(nascarOutrightsMarketDescription()),
            aLanguage
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
            rabbitProducer.send(messages.oddsChange(nascarOutrightsMarket()), routingKeys.liveOddsChange());

            val market = theOnlyMarketIn(listinerWaitingFor.theOnlyOddsChange());
            val faultyOutcome = firstOutcomeOf(market);

            OutcomeAssert
                .assertThat(faultyOutcome)
                .getNameForDefault(aLanguage, willFailRespectingSdkStrategy);
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
        Assertions.assertThat(oddsChange.getMarkets()).hasSize(1);
        return oddsChange.getMarkets().get(0);
    }

    private static DescMarket nullifyFirstOutcomeName(DescMarket market) {
        market.getOutcomes().getOutcome().get(0).setName(null);
        return market;
    }

    private static DescMarket nullifyOutcomeName(DescMarket market, String outcomeId) {
        val outcome = market
            .getOutcomes()
            .getOutcome()
            .stream()
            .filter(o -> o.getId().equals(outcomeId))
            .findFirst()
            .get();
        outcome.setName(null);
        return market;
    }

    private static DescVariant nullifyFirstOutcomeName(DescVariant variant) {
        variant.getOutcomes().getOutcome().get(0).setName(null);
        return variant;
    }

    private DescMarket nullifyMappingOutcomeNameForZeroToZeroOutcome(DescMarket market) {
        val onlyOneMappingElement = getOnlyElement(market.getMappings().getMapping());
        val mappingOutcome = findOutcomeMapping(
            in(onlyOneMappingElement),
            by(FlexScoreMarketIds.ZERO_TO_ZERO_OUTCOME_ID)
        );
        mappingOutcome.setProductOutcomeName(null);
        return market;
    }

    private static Mappings.Mapping.MappingOutcome findOutcomeMapping(
        Mappings.Mapping onlyOneMappingElement,
        String outcomeId
    ) {
        return onlyOneMappingElement
            .getMappingOutcome()
            .stream()
            .filter(mo -> mo.getOutcomeId().equals(outcomeId))
            .findFirst()
            .get();
    }

    private static OutcomeOdds firstOutcomeOf(MarketWithOdds market) {
        return market.getOutcomeOdds().get(0);
    }

    private OutcomeDescription firstOutcomeOf(MarketDescription market) {
        return market.getOutcomes().get(0);
    }

    private static String getProducerOutcomeNameForZeroToZeroOutcome(
        MarketMappingData validMappings,
        Locale aLanguage
    ) {
        List<String> producerOutcomeNames = validMappings
            .getOutcomeMappings()
            .entrySet()
            .stream()
            .filter(isZeroToZeroOutcome())
            .map(getProducerOutcomeName(aLanguage))
            .collect(toList());
        return getOnlyElement(producerOutcomeNames);
    }

    private static Function<Map.Entry<String, OutcomeMappingData>, String> getProducerOutcomeName(
        Locale aLanguage
    ) {
        return e -> e.getValue().getProducerOutcomeName(aLanguage);
    }

    private static Predicate<Map.Entry<String, OutcomeMappingData>> isZeroToZeroOutcome() {
        return om -> om.getValue().getOutcomeId().equals(FlexScoreMarketIds.ZERO_TO_ZERO_OUTCOME_ID);
    }

    private static MarketMappingData getValidMapping(MarketWithOdds market, Locale aLanguage) {
        return getOnlyElement(market.getMarketDefinition().getValidMappings(aLanguage, true));
    }

    private static OutcomeDefinition getOutcomeDefinition(MarketWithOdds market, String anyOutcomeId) {
        val outcome = market
            .getOutcomeOdds()
            .stream()
            .filter(o -> o.getId().equals(anyOutcomeId))
            .findFirst()
            .get();
        return outcome.getOutcomeDefinition();
    }
}
