/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */
package com.sportradar.unifiedodds.sdk.conn;

import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.wireMockConfig;
import static com.sportradar.unifiedodds.sdk.ExceptionHandlingStrategy.Throw;
import static com.sportradar.unifiedodds.sdk.conn.ProducerId.LIVE_ODDS;
import static com.sportradar.unifiedodds.sdk.conn.SapiMarketDescriptions.ExactGoals.exactGoalsMarketDescription;
import static com.sportradar.unifiedodds.sdk.conn.SapiMarketDescriptions.OddEven.oddEvenMarketDescription;
import static com.sportradar.unifiedodds.sdk.conn.SapiMarketDescriptions.OneXtwo.oneXtwoMarketDescription;
import static com.sportradar.unifiedodds.sdk.conn.SapiVariantDescriptions.ExactGoals.fivePlusVariantDescription;
import static com.sportradar.unifiedodds.sdk.conn.Sport.FOOTBALL;
import static com.sportradar.unifiedodds.sdk.conn.SportEvent.MATCH;
import static com.sportradar.unifiedodds.sdk.conn.UfMarkets.WithOdds.exactGoalsMarket;
import static com.sportradar.unifiedodds.sdk.conn.marketids.ExactGoalsMarketIds.fivePlusVariant;
import static com.sportradar.unifiedodds.sdk.conn.marketids.OddEvenMarketIds.ODD_EVEN_MARKET_ID;
import static com.sportradar.unifiedodds.sdk.impl.Constants.*;
import static com.sportradar.unifiedodds.sdk.testutil.generic.naturallanguage.Prepositions.from;
import static com.sportradar.unifiedodds.sdk.testutil.rabbit.integration.Credentials.with;
import static com.sportradar.unifiedodds.sdk.testutil.rabbit.integration.RabbitMqClientFactory.createRabbitMqClient;
import static com.sportradar.unifiedodds.sdk.testutil.rabbit.integration.RabbitMqProducer.connectDeclaringExchange;
import static com.sportradar.utils.domain.names.LanguageHolder.in;
import static com.sportradar.utils.domain.names.TranslationHolder.of;
import static java.util.Locale.ENGLISH;
import static java.util.Locale.FRENCH;
import static java.util.stream.Collectors.toSet;

import com.github.tomakehurst.wiremock.common.ConsoleNotifier;
import com.github.tomakehurst.wiremock.junit5.WireMockExtension;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.http.client.Client;
import com.sportradar.uf.sportsapi.datamodel.DescVariant;
import com.sportradar.unifiedodds.sdk.entities.markets.MarketDescription;
import com.sportradar.unifiedodds.sdk.impl.Constants;
import com.sportradar.unifiedodds.sdk.impl.oddsentities.markets.MarketDescriptionAssert;
import com.sportradar.unifiedodds.sdk.impl.oddsentities.markets.OutcomeDescriptionsAssert;
import com.sportradar.unifiedodds.sdk.internal.impl.TimeUtilsImpl;
import com.sportradar.unifiedodds.sdk.oddsentities.MarketWithOdds;
import com.sportradar.unifiedodds.sdk.oddsentities.OddsChange;
import com.sportradar.unifiedodds.sdk.oddsentities.OutcomeOdds;
import com.sportradar.unifiedodds.sdk.shared.FeedMessageBuilder;
import com.sportradar.unifiedodds.sdk.testutil.rabbit.integration.*;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import lombok.val;
import org.assertj.core.api.Assertions;
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
        "LambdaBodyLength",
    }
)
class MarketsIT {

    @RegisterExtension
    private static WireMockExtension wireMock = WireMockExtension
        .newInstance()
        .options(wireMockConfig().dynamicPort().notifier(new ConsoleNotifier(true)))
        .build();

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

    private final GlobalVariables globalVariables = new GlobalVariables();
    private final ApiSimulator apiSimulator = new ApiSimulator(wireMock.getRuntimeInfo().getWireMock());
    private final Credentials sdkCredentials = Credentials.with(
        Constants.SDK_USERNAME,
        Constants.SDK_PASSWORD
    );
    private final MessagesInMemoryStorage messagesStorage = new MessagesInMemoryStorage();
    private final WaiterForSingleMessage listenerWaitingFor = new WaiterForSingleMessage(messagesStorage);
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

    MarketsIT() throws Exception {}

    @BeforeEach
    void setup() throws Exception {
        rabbitMqUserSetup.setupUser(sdkCredentials);
        sportsApiBaseUrl = BaseUrl.of("localhost", wireMock.getPort());
    }

    @Test
    void invariantMarketDescriptionsAreLoadedOnSdkStartup() throws Exception {
        globalVariables.setProducer(LIVE_ODDS);

        apiSimulator.defineBookmaker();
        apiSimulator.activateOnlyLiveProducer();
        apiSimulator.stubMarketList(
            ENGLISH,
            oddEvenMarketDescription(ENGLISH),
            oneXtwoMarketDescription(ENGLISH)
        );
        apiSimulator.stubMarketList(
            FRENCH,
            oddEvenMarketDescription(FRENCH),
            oneXtwoMarketDescription(FRENCH)
        );

        try (
            val sdk = SdkSetup
                .with(sdkCredentials, RABBIT_BASE_URL, sportsApiBaseUrl, globalVariables.getNodeId())
                .with(ListenerCollectingMessages.to(messagesStorage))
                .with(Throw)
                .withDefaultLanguage(ENGLISH)
                .withDesiredLanguages(ENGLISH, FRENCH)
                .withoutFeed()
        ) {
            val descriptionManager = sdk.getMarketDescriptionManager();

            val englishOddEvenMarketDescription = getMarket(
                ODD_EVEN_MARKET_ID,
                from(descriptionManager.getMarketDescriptions(ENGLISH))
            );
            MarketDescriptionAssert
                .assertThat(englishOddEvenMarketDescription)
                .hasName(of(oddEvenMarketDescription(ENGLISH).getName(), in(ENGLISH)));
            OutcomeDescriptionsAssert
                .assertThat(englishOddEvenMarketDescription.getOutcomes())
                .haveNamesEqualTo(oddEvenMarketDescription(ENGLISH).getOutcomes(), in(ENGLISH));

            val frenchOddEvenMarketDescription = getMarket(
                ODD_EVEN_MARKET_ID,
                from(descriptionManager.getMarketDescriptions(FRENCH))
            );
            MarketDescriptionAssert
                .assertThat(frenchOddEvenMarketDescription)
                .hasName(of(oddEvenMarketDescription(FRENCH).getName(), in(FRENCH)));
            OutcomeDescriptionsAssert
                .assertThat(frenchOddEvenMarketDescription.getOutcomes())
                .haveNamesEqualTo(oddEvenMarketDescription(FRENCH).getOutcomes(), in(FRENCH));
        }
    }

    @Test
    void retrievesNameOfVariantMarketFromVariantListUponReceivingFeedMessage() throws Exception {
        globalVariables.setProducer(LIVE_ODDS);
        globalVariables.setSportEventUrn(MATCH);
        globalVariables.setSportUrn(FOOTBALL);
        FeedMessageBuilder messages = new FeedMessageBuilder(globalVariables);
        Locale aLanguage = Locale.ENGLISH;
        RoutingKeys routingKeys = new RoutingKeys(globalVariables);

        apiSimulator.defineBookmaker();
        apiSimulator.activateOnlyLiveProducer();
        apiSimulator.stubMarketListContaining(exactGoalsMarketDescription(), aLanguage);
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
                .with(Throw)
                .withDefaultLanguage(aLanguage)
                .with1Session()
                .withOpenedFeed()
        ) {
            rabbitProducer.send(
                messages.oddsChange(exactGoalsMarket(fivePlusVariant())),
                routingKeys.liveOddsChange()
            );

            val market = theOnlyMarketIn(listenerWaitingFor.theOnlyOddsChange());

            Assertions.assertThat(market.getName()).isEqualTo(exactGoalsMarketDescription(ENGLISH).getName());
            Assertions
                .assertThat(market.getName(ENGLISH))
                .isEqualTo(exactGoalsMarketDescription(ENGLISH).getName());
        }
    }

    @Test
    void retrievesOutcomeNameOfVariantMarketFromVariantListUponReceivingFeedMessage() throws Exception {
        globalVariables.setProducer(LIVE_ODDS);
        globalVariables.setSportEventUrn(MATCH);
        globalVariables.setSportUrn(FOOTBALL);
        FeedMessageBuilder messages = new FeedMessageBuilder(globalVariables);
        Locale aLanguage = Locale.ENGLISH;
        RoutingKeys routingKeys = new RoutingKeys(globalVariables);

        apiSimulator.defineBookmaker();
        apiSimulator.activateOnlyLiveProducer();
        apiSimulator.stubMarketListContaining(exactGoalsMarketDescription(), aLanguage);
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
                .with(Throw)
                .withDefaultLanguage(aLanguage)
                .with1Session()
                .withOpenedFeed()
        ) {
            rabbitProducer.send(
                messages.oddsChange(exactGoalsMarket(fivePlusVariant())),
                routingKeys.liveOddsChange()
            );

            val market = theOnlyMarketIn(listenerWaitingFor.theOnlyOddsChange());

            val outcomes = market.getOutcomeOdds();

            Assertions.assertThat(outcomes).hasSize(6);
            Assertions
                .assertThat(setOfIdAndNamesFrom(outcomes))
                .isEqualTo(setOfIdAndNamesFrom(fivePlusVariantDescription()));
        }
    }

    private Set<String> setOfIdAndNamesFrom(DescVariant variant) {
        val outcomes = variant.getOutcomes().getOutcome();
        return outcomes.stream().map(o -> o.getId() + ":" + o.getName()).collect(toSet());
    }

    private Set<String> setOfIdAndNamesFrom(List<OutcomeOdds> outcomes) {
        return outcomes.stream().map(o -> o.getId() + ":" + o.getName()).collect(toSet());
    }

    private MarketDescription getMarket(int marketId, List<MarketDescription> marketDescriptions) {
        return marketDescriptions.stream().filter(md -> md.getId() == marketId).findFirst().get();
    }

    private MarketWithOdds theOnlyMarketIn(
        OddsChange<com.sportradar.unifiedodds.sdk.entities.SportEvent> oddsChange
    ) {
        Assertions.assertThat(oddsChange.getMarkets().size()).isEqualTo(1);
        return oddsChange.getMarkets().get(0);
    }
}
