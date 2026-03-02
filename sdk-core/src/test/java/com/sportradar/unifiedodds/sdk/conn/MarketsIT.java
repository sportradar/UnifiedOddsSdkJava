/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */
package com.sportradar.unifiedodds.sdk.conn;

import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.wireMockConfig;
import static com.sportradar.unifiedodds.sdk.ExceptionHandlingStrategy.Throw;
import static com.sportradar.unifiedodds.sdk.conn.ProducerId.LIVE_ODDS;
import static com.sportradar.unifiedodds.sdk.conn.SapiMarketDescriptions.OddEven.oddEvenMarketDescription;
import static com.sportradar.unifiedodds.sdk.conn.SapiMarketDescriptions.OneXtwo.oneXtwoMarketDescription;
import static com.sportradar.unifiedodds.sdk.conn.marketids.OddEvenMarketIds.ODD_EVEN_MARKET_ID;
import static com.sportradar.unifiedodds.sdk.impl.Constants.RABBIT_BASE_URL;
import static com.sportradar.unifiedodds.sdk.testutil.generic.naturallanguage.Prepositions.from;
import static com.sportradar.utils.domain.names.LanguageHolder.in;
import static com.sportradar.utils.domain.names.TranslationHolder.of;
import static java.util.Locale.ENGLISH;
import static java.util.Locale.FRENCH;

import com.github.tomakehurst.wiremock.common.ConsoleNotifier;
import com.github.tomakehurst.wiremock.junit5.WireMockExtension;
import com.sportradar.unifiedodds.sdk.entities.markets.MarketDescription;
import com.sportradar.unifiedodds.sdk.impl.Constants;
import com.sportradar.unifiedodds.sdk.impl.oddsentities.markets.MarketDescriptionAssert;
import com.sportradar.unifiedodds.sdk.impl.oddsentities.markets.OutcomeDescriptionsAssert;
import com.sportradar.unifiedodds.sdk.testutil.rabbit.integration.BaseUrl;
import com.sportradar.unifiedodds.sdk.testutil.rabbit.integration.Credentials;
import java.util.List;
import lombok.val;
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

    private final GlobalVariables globalVariables = new GlobalVariables();
    private final ApiSimulator apiSimulator = new ApiSimulator(wireMock.getRuntimeInfo().getWireMock());
    private final Credentials sdkCredentials = Credentials.with(
        Constants.SDK_USERNAME,
        Constants.SDK_PASSWORD
    );
    private final MessagesInMemoryStorage messagesStorage = new MessagesInMemoryStorage();

    private BaseUrl sportsApiBaseUrl;

    @BeforeEach
    void setup() throws Exception {
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

    private MarketDescription getMarket(int marketId, List<MarketDescription> marketDescriptions) {
        return marketDescriptions.stream().filter(md -> md.getId() == marketId).findFirst().get();
    }
}
