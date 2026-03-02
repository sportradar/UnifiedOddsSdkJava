/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */
package com.sportradar.unifiedodds.sdk.impl.oddsentities.markets;

import static com.sportradar.unifiedodds.sdk.ExceptionHandlingStrategy.Catch;
import static com.sportradar.unifiedodds.sdk.ExceptionHandlingStrategy.Throw;
import static com.sportradar.unifiedodds.sdk.caching.markets.MarketDescriptionFactory.descriptionFrom;
import static com.sportradar.unifiedodds.sdk.caching.markets.MarketDescriptionFactory.namesOf;
import static com.sportradar.unifiedodds.sdk.caching.markets.MarketDescriptorProviders.noMarketDescribingProvider;
import static com.sportradar.unifiedodds.sdk.caching.markets.MarketDescriptorProviders.providing;
import static com.sportradar.unifiedodds.sdk.conn.SapiMarketDescriptions.CorrectScoreFlex.correctScoreFlexMarketDescription;
import static com.sportradar.unifiedodds.sdk.conn.SapiMarketDescriptions.Handicap.handicapMarketDescription;
import static com.sportradar.unifiedodds.sdk.conn.SapiMarketDescriptions.OddEven.oddEvenMarketDescription;
import static com.sportradar.unifiedodds.sdk.conn.SapiMarketDescriptions.OddEven.oddOutcomeDescription;
import static com.sportradar.unifiedodds.sdk.conn.UfMarkets.Simple.*;
import static com.sportradar.unifiedodds.sdk.conn.UfMarkets.Simple.oddEvenMarket;
import static com.sportradar.unifiedodds.sdk.conn.UfMarkets.Simple.winnerCompetitorMarket;
import static com.sportradar.unifiedodds.sdk.conn.UfMarkets.WithRollbackSettlementOutcomes.oddEvenMarketWithBothOutcomes;
import static com.sportradar.unifiedodds.sdk.conn.UfSpecifiers.UfHandicapForTheRestSpecifier.handicapForTheRest;
import static com.sportradar.unifiedodds.sdk.conn.UfSpecifiers.UfHandicapSpecifier.handicap;
import static com.sportradar.unifiedodds.sdk.conn.marketids.ExactGoalsMarketIds.fivePlusVariant;
import static com.sportradar.unifiedodds.sdk.conn.marketids.FlexScoreMarketIds.FLEX_SCORE_MARKET_MAPPING_MARKET_ID;
import static com.sportradar.unifiedodds.sdk.conn.marketids.HandicapMarketIds.HANDICAP_MARKET_MARKET_ID;
import static com.sportradar.unifiedodds.sdk.conn.marketids.OddEvenMarketIds.EVEN_OUTCOME_ID;
import static com.sportradar.unifiedodds.sdk.conn.marketids.OddEvenMarketIds.ODD_EVEN_MARKET_ID;
import static com.sportradar.unifiedodds.sdk.conn.marketids.OddEvenMarketIds.ODD_OUTCOME_ID;
import static com.sportradar.unifiedodds.sdk.impl.oddsentities.markets.ExpectationTowardsSdkErrorHandlingStrategy.WILL_CATCH_EXCEPTIONS;
import static com.sportradar.unifiedodds.sdk.impl.oddsentities.markets.ExpectationTowardsSdkErrorHandlingStrategy.WILL_THROW_EXCEPTIONS;
import static com.sportradar.unifiedodds.sdk.impl.oddsentities.markets.MarketFactories.BuilderStubbingOutSportEventAndCaches.stubbingOutCaches;
import static com.sportradar.utils.domain.names.LanguageHolder.in;
import static com.sportradar.utils.domain.names.Languages.any;
import static com.sportradar.utils.domain.names.TranslationHolder.of;
import static com.sportradar.utils.domain.producers.ProducerIds.*;
import static java.util.Collections.singletonList;
import static java.util.Locale.ENGLISH;
import static java.util.Locale.FRENCH;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.entry;
import static org.mockito.Mockito.*;

import com.google.common.collect.ImmutableMap;
import com.sportradar.uf.datamodel.UfMarket;
import com.sportradar.unifiedodds.sdk.ExceptionHandlingStrategy;
import com.sportradar.unifiedodds.sdk.caching.markets.MarketDescriptionStub;
import com.sportradar.unifiedodds.sdk.entities.SportEvents;
import com.sportradar.unifiedodds.sdk.oddsentities.OutcomeRollbackSettlement;
import com.sportradar.utils.domain.names.Languages;
import java.util.stream.Stream;
import lombok.val;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

public class MarketFactoryImplWithRollbackSettlementTest {

    private static Stream<Object[]> exceptionHandlingStrategies() {
        return Stream.of(
            new Object[] { Throw, WILL_THROW_EXCEPTIONS },
            new Object[] { Catch, WILL_CATCH_EXCEPTIONS }
        );
    }

    @Test
    void marketHasId() {
        val marketFactory = stubbingOutCaches()
            .with(noMarketDescribingProvider())
            .withDefaultLanguage(any())
            .build();

        val market = marketFactory
            .buildMarketWithRollbackSettlement(SportEvents.any(), oddEvenMarket(), anyProducerId())
            .get();

        assertThat(market.getId()).isEqualTo(ODD_EVEN_MARKET_ID);
    }

    @Test
    void marketHasSpecifiers() {
        val marketFactory = stubbingOutCaches()
            .with(noMarketDescribingProvider())
            .withDefaultLanguage(any())
            .build();

        val ufMarket = exactGoalsMarket(fivePlusVariant());

        val market = marketFactory
            .buildMarketWithRollbackSettlement(SportEvents.any(), ufMarket, anyProducerId())
            .get();

        assertThat(market.getSpecifiers()).containsExactly(entry("variant", fivePlusVariant().id()));
    }

    @Test
    void marketHasExtendedSpecifiers() {
        val marketFactory = stubbingOutCaches()
            .with(noMarketDescribingProvider())
            .withDefaultLanguage(any())
            .build();

        UfMarket ufMarket = handicapMarket(handicap(2.5), handicapForTheRest(1.5));

        val market = marketFactory
            .buildMarketWithRollbackSettlement(SportEvents.any(), ufMarket, anyProducerId())
            .get();

        assertThat(market.getAdditionalMarketInfo()).containsExactly(entry("hcp_for_the_rest", "1.5"));
    }

    @Test
    void marketHasOutcomeTypeInTheMarketDefinition() {
        val aLanguage = ENGLISH;
        val description = new MarketDescriptionStub().withOutcomeType("competitor");
        val marketFactory = stubbingOutCaches()
            .with(providing(in(aLanguage), description))
            .withDefaultLanguage(aLanguage)
            .build();

        val market = marketFactory
            .buildMarketWithRollbackSettlement(SportEvents.any(), winnerCompetitorMarket(), anyProducerId())
            .get();

        assertThat(market.getMarketDefinition().getOutcomeType()).isEqualTo("competitor");
    }

    @Test
    void marketHasNameTemplateInTheMarketDefinitionForTheDefaultLanguage() {
        val aLanguage = ENGLISH;
        val marketFactory = stubbingOutCaches()
            .with(providing(in(aLanguage), namesOf(oddEvenMarketDescription(), in(aLanguage))))
            .withDefaultLanguage(aLanguage)
            .build();

        val market = marketFactory
            .buildMarketWithRollbackSettlement(SportEvents.any(), oddEvenMarket(), anyProducerId())
            .get();

        assertThat(market.getMarketDefinition().getNameTemplate())
            .isEqualTo(oddEvenMarketDescription().getName());
    }

    @Test
    void marketHasNameTemplateInTheMarketDefinitionForNonDefaultLanguage() {
        val languageA = ENGLISH;
        val languageB = FRENCH;
        val marketFactory = stubbingOutCaches()
            .with(providing(in(languageB), namesOf(oddEvenMarketDescription(languageB), in(languageB))))
            .withDefaultLanguage(languageA)
            .build();

        val market = marketFactory
            .buildMarketWithRollbackSettlement(SportEvents.any(), oddEvenMarket(), anyProducerId())
            .get();

        assertThat(market.getMarketDefinition().getNameTemplate(languageB))
            .isEqualTo(oddEvenMarketDescription(languageB).getName());
    }

    @Test
    void marketDefinitionHasGroups() {
        val aLanguage = ENGLISH;
        val marketFactory = stubbingOutCaches()
            .with(providing(in(aLanguage), descriptionFrom(correctScoreFlexMarketDescription(aLanguage))))
            .withDefaultLanguage(aLanguage)
            .build();

        val rollbackMarket = marketFactory
            .buildMarketWithRollbackSettlement(SportEvents.any(), correctScoreFlexMarket(), anyProducerId())
            .get();

        assertThat(rollbackMarket.getMarketDefinition().getGroups())
            .containsExactly("all", "score", "regular_play");
    }

    @Test
    void marketDefinitionHasAttributes() {
        val aLanguage = ENGLISH;
        val marketFactory = stubbingOutCaches()
            .with(providing(in(aLanguage), descriptionFrom(correctScoreFlexMarketDescription(aLanguage))))
            .withDefaultLanguage(aLanguage)
            .build();

        val rollbackMarket = marketFactory
            .buildMarketWithRollbackSettlement(SportEvents.any(), correctScoreFlexMarket(), anyProducerId())
            .get();

        assertThat(rollbackMarket.getMarketDefinition().getAttributes())
            .containsExactly(
                entry("is_flex_score", "Outcomes should be adjusted according to score specifier")
            );
    }

    @Test
    void marketDefinitionHasValidMappings() {
        val aLanguage = ENGLISH;
        val marketFactory = stubbingOutCaches()
            .with(providing(in(aLanguage), descriptionFrom(correctScoreFlexMarketDescription(aLanguage))))
            .withDefaultLanguage(aLanguage)
            .build();

        val market = marketFactory
            .buildMarketWithRollbackSettlement(SportEvents.any(), correctScoreFlexMarket(), LIVE_PRODUCER_ID)
            .get();

        MarketMappingDataAssert
            .assertThat(market.getMarketDefinition().getValidMappings(aLanguage))
            .hasOnlyOneMappingWithMarketId(FLEX_SCORE_MARKET_MAPPING_MARKET_ID);
    }

    @Test
    void marketDefinitionAlwaysHasEmptyMappingsForPremiumCricket() {
        val aLanguage = ENGLISH;
        val marketFactory = stubbingOutCaches()
            .with(providing(in(aLanguage), descriptionFrom(correctScoreFlexMarketDescription(aLanguage))))
            .withDefaultLanguage(aLanguage)
            .build();

        val market = marketFactory
            .buildMarketWithRollbackSettlement(
                SportEvents.any(),
                correctScoreFlexMarket(),
                PREMIUM_CRICKET_PRODUCER_ID
            )
            .get();

        assertThat(market.getMarketDefinition().getValidMappings(aLanguage)).isEmpty();
    }

    @Test
    void marketDefinitionHasValidMappingsWithAdjustment() {
        val aLanguage = ENGLISH;
        val marketFactory = stubbingOutCaches()
            .with(providing(in(aLanguage), descriptionFrom(correctScoreFlexMarketDescription(aLanguage))))
            .withDefaultLanguage(aLanguage)
            .build();

        val market = marketFactory
            .buildMarketWithRollbackSettlement(SportEvents.any(), correctScoreFlexMarket(), LIVE_PRODUCER_ID)
            .get();

        MarketMappingDataAssert
            .assertThat(market.getMarketDefinition().getValidMappings(aLanguage, false))
            .hasOnlyOneMappingWithMarketId(FLEX_SCORE_MARKET_MAPPING_MARKET_ID);
        MarketMappingDataAssert
            .assertThat(market.getMarketDefinition().getValidMappings(aLanguage, true))
            .hasOnlyOneMappingWithMarketId(FLEX_SCORE_MARKET_MAPPING_MARKET_ID);
    }

    @Test
    void marketHasNameInDefaultLanguage() {
        val aLanguage = ENGLISH;
        val marketFactory = stubbingOutCaches()
            .with(providing(in(aLanguage), namesOf(oddEvenMarketDescription(), in(aLanguage))))
            .withDefaultLanguage(aLanguage)
            .build();

        val market = marketFactory
            .buildMarketWithRollbackSettlement(SportEvents.any(), oddEvenMarket(), anyProducerId())
            .get();

        MarketAssert.assertThat(market).hasName(of(oddEvenMarketDescription().getName(), in(aLanguage)));
    }

    @Test
    void marketHasNameInNonDefaultLanguage() {
        val defaultLanguage = ENGLISH;
        val nonDefaultLanguage = FRENCH;
        val marketFactory = stubbingOutCaches()
            .with(
                providing(in(nonDefaultLanguage), namesOf(oddEvenMarketDescription(), in(nonDefaultLanguage)))
            )
            .withDefaultLanguage(defaultLanguage)
            .build();

        val market = marketFactory
            .buildMarketWithRollbackSettlement(SportEvents.any(), oddEvenMarket(), anyProducerId())
            .get();

        MarketAssert
            .assertThat(market)
            .hasName(of(oddEvenMarketDescription().getName(), in(nonDefaultLanguage)));
    }

    @ParameterizedTest
    @MethodSource("exceptionHandlingStrategies")
    void marketNameRetrievalRespectsExceptionHandlingStrategy(
        ExceptionHandlingStrategy exceptionHandlingStrategy,
        ExpectationTowardsSdkErrorHandlingStrategy willFailRespectingSdkStrategy
    ) {
        val aLanguage = Languages.any();
        val marketFactory = stubbingOutCaches()
            .with(noMarketDescribingProvider())
            .with(exceptionHandlingStrategy)
            .withDefaultLanguage(aLanguage)
            .build();

        val market = marketFactory
            .buildMarketWithRollbackSettlement(SportEvents.any(), oddEvenMarket(), anyProducerId())
            .get();

        MarketAssert.assertThat(market).getNameForDefault(aLanguage, willFailRespectingSdkStrategy);
    }

    @Test
    void outcomesHaveProperIds() {
        val aLanguage = Languages.any();
        val marketFactory = stubbingOutCaches()
            .with(noMarketDescribingProvider())
            .withDefaultLanguage(aLanguage)
            .build();

        val market = marketFactory
            .buildMarketWithRollbackSettlement(
                SportEvents.any(),
                oddEvenMarketWithBothOutcomes(),
                anyProducerId()
            )
            .get();

        OutcomeRollbackSettlementsAssert
            .assertThat(market.getOutcomeRollbackSettlements())
            .hasOutcomeWithId(ODD_OUTCOME_ID)
            .hasOutcomeWithId(EVEN_OUTCOME_ID);
    }

    @Test
    void outcomeHasNameInDefaultLanguage() {
        val aLanguage = ENGLISH;
        val marketFactory = stubbingOutCaches()
            .with(providing(in(aLanguage), namesOf(oddEvenMarketDescription(), in(aLanguage))))
            .withDefaultLanguage(aLanguage)
            .build();

        val market = marketFactory
            .buildMarketWithRollbackSettlement(
                SportEvents.any(),
                oddEvenMarketWithBothOutcomes(),
                anyProducerId()
            )
            .get();

        OutcomeRollbackSettlement oddOutcome = market.getOutcomeRollbackSettlements().get(0);
        OutcomeAssert.assertThat(oddOutcome).hasName(of(oddOutcomeDescription().getName(), in(aLanguage)));
    }

    @Test
    void outcomeHasNameFromNameTemplateFetchedFromMarketDescriptionProvider() throws Exception {
        val aLanguage = ENGLISH;
        val provider = spy(providing(in(aLanguage), namesOf(handicapMarketDescription(), in(aLanguage))));
        val marketFactory = stubbingOutCaches().with(provider).withDefaultLanguage(aLanguage).build();

        val market = marketFactory
            .buildMarketWithRollbackSettlement(
                SportEvents.any(),
                handicapMarket(handicap(1.5), handicapForTheRest(0.5)),
                LIVE_PRODUCER_ID
            )
            .get();

        val firstOutcome = market.getOutcomeRollbackSettlements().get(0);
        val expectedOutcomeName = handicapMarketDescription().getOutcomes().getOutcome().get(0).getName();

        assertThat(firstOutcome.getOutcomeDefinition().getNameTemplate()).isEqualTo(expectedOutcomeName);

        val expectedSpecifiers = ImmutableMap.of("hcp", "1.5");
        verify(provider)
            .getMarketDescription(
                HANDICAP_MARKET_MARKET_ID,
                expectedSpecifiers,
                singletonList(aLanguage),
                true
            );
    }

    @ParameterizedTest
    @MethodSource("exceptionHandlingStrategies")
    void outcomeNameRetrievalFailsRespectingExceptionStrategy(
        ExceptionHandlingStrategy exceptionHandlingStrategy,
        ExpectationTowardsSdkErrorHandlingStrategy willRespectSdkStrategy
    ) {
        val aLanguage = Languages.any();
        val marketFactory = stubbingOutCaches()
            .with(noMarketDescribingProvider())
            .withDefaultLanguage(aLanguage)
            .with(exceptionHandlingStrategy)
            .build();

        val market = marketFactory
            .buildMarketWithRollbackSettlement(
                SportEvents.any(),
                oddEvenMarketWithBothOutcomes(),
                anyProducerId()
            )
            .get();

        val outcome = market.getOutcomeRollbackSettlements().get(0);
        OutcomeAssert
            .assertThat(outcome)
            .nameIsNotBackedByMarketDescriptionForDefaultLanguage(aLanguage, willRespectSdkStrategy);
    }

    @Test
    void outcomeDefinitionHasNameTemplate() {
        val aLanguage = ENGLISH;
        val marketFactory = stubbingOutCaches()
            .with(providing(in(aLanguage), namesOf(oddEvenMarketDescription(), in(aLanguage))))
            .withDefaultLanguage(aLanguage)
            .build();

        val market = marketFactory
            .buildMarketWithRollbackSettlement(
                SportEvents.any(),
                oddEvenMarketWithBothOutcomes(),
                anyProducerId()
            )
            .get();

        val oddOutcome = market.getOutcomeRollbackSettlements().get(0);
        assertThat(oddOutcome.getOutcomeDefinition().getNameTemplate())
            .isEqualTo(oddOutcomeDescription().getName());
    }

    @ParameterizedTest
    @MethodSource("exceptionHandlingStrategies")
    void outcomeDefinitionIsMissingNameTemplateForDefaultLanguage(
        ExceptionHandlingStrategy exceptionHandlingStrategy,
        ExpectationTowardsSdkErrorHandlingStrategy willRespectSdkStrategy
    ) {
        val aLanguage = Languages.any();
        val marketFactory = stubbingOutCaches()
            .with(noMarketDescribingProvider())
            .with(exceptionHandlingStrategy)
            .withDefaultLanguage(aLanguage)
            .build();

        val market = marketFactory
            .buildMarketWithRollbackSettlement(
                SportEvents.any(),
                oddEvenMarketWithBothOutcomes(),
                anyProducerId()
            )
            .get();

        val oneOfOutcomes = market.getOutcomeRollbackSettlements().get(0);
        OutcomeAssert
            .assertThat(oneOfOutcomes)
            .getNameTemplateMethodsFailForDefaultLanguage(aLanguage, willRespectSdkStrategy);
    }

    @ParameterizedTest
    @MethodSource("exceptionHandlingStrategies")
    void outcomeDefinitionIsMissingNameTemplateForNonDefaultLanguage(
        ExceptionHandlingStrategy exceptionHandlingStrategy,
        ExpectationTowardsSdkErrorHandlingStrategy willRespectSdkStrategy
    ) {
        val langA = ENGLISH;
        val langB = FRENCH;
        val marketFactory = stubbingOutCaches()
            .with(noMarketDescribingProvider())
            .with(exceptionHandlingStrategy)
            .withDefaultLanguage(langA)
            .build();

        val market = marketFactory
            .buildMarketWithRollbackSettlement(
                SportEvents.any(),
                oddEvenMarketWithBothOutcomes(),
                anyProducerId()
            )
            .get();

        val oneOfOutcomes = market.getOutcomeRollbackSettlements().get(0);
        OutcomeAssert
            .assertThat(oneOfOutcomes)
            .getNameTemplateMethodsFailForNonDefaultLanguage(langB, willRespectSdkStrategy);
    }
}
