/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */
package com.sportradar.unifiedodds.sdk.impl.oddsentities.markets;

import static com.sportradar.unifiedodds.sdk.ExceptionHandlingStrategy.Catch;
import static com.sportradar.unifiedodds.sdk.ExceptionHandlingStrategy.Throw;
import static com.sportradar.unifiedodds.sdk.caching.markets.MarketDescriptionFactory.namesOf;
import static com.sportradar.unifiedodds.sdk.caching.markets.MarketDescriptorProviders.noMarketDescribingProvider;
import static com.sportradar.unifiedodds.sdk.caching.markets.MarketDescriptorProviders.providing;
import static com.sportradar.unifiedodds.sdk.conn.SapiMarketDescriptions.OddEven.oddEvenMarketDescription;
import static com.sportradar.unifiedodds.sdk.conn.UfMarkets.WithSettlementOutcomes.*;
import static com.sportradar.unifiedodds.sdk.conn.marketids.OddEvenMarketIds.ODD_OUTCOME_ID;
import static com.sportradar.unifiedodds.sdk.conn.marketids.OneXtwoMarketIds.*;
import static com.sportradar.unifiedodds.sdk.impl.oddsentities.markets.ExpectationTowardsSdkErrorHandlingStrategy.WILL_CATCH_EXCEPTIONS;
import static com.sportradar.unifiedodds.sdk.impl.oddsentities.markets.ExpectationTowardsSdkErrorHandlingStrategy.WILL_THROW_EXCEPTIONS;
import static com.sportradar.unifiedodds.sdk.impl.oddsentities.markets.MarketAssert.assertThat;
import static com.sportradar.unifiedodds.sdk.impl.oddsentities.markets.MarketDefinitionAssert.MethodsBackedByMarketDescriptionInScope.ALL_METHODS;
import static com.sportradar.unifiedodds.sdk.impl.oddsentities.markets.MarketDefinitionAssert.MethodsBackedByMarketDescriptionInScope.METHODS_EXCLUDING_VALID_MAPPINGS;
import static com.sportradar.unifiedodds.sdk.impl.oddsentities.markets.MarketDefinitionAssert.assertThat;
import static com.sportradar.unifiedodds.sdk.impl.oddsentities.markets.MarketFactories.BuilderStubbingOutSportEventAndCaches.stubbingOutCaches;
import static com.sportradar.unifiedodds.sdk.impl.oddsentities.markets.OutcomeAssert.assertThat;
import static com.sportradar.unifiedodds.sdk.impl.oddsentities.markets.OutcomeSettlementsAssert.assertThat;
import static com.sportradar.utils.domain.names.LanguageHolder.in;
import static com.sportradar.utils.domain.names.TranslationHolder.of;
import static com.sportradar.utils.domain.producers.ProducerIds.PREMIUM_CRICKET_PRODUCER_ID;
import static com.sportradar.utils.domain.producers.ProducerIds.anyProducerId;
import static org.assertj.core.api.Assertions.assertThat;

import com.sportradar.unifiedodds.sdk.ExceptionHandlingStrategy;
import com.sportradar.unifiedodds.sdk.entities.SportEvents;
import com.sportradar.utils.domain.UniqueObjects;
import com.sportradar.utils.domain.names.Languages;
import com.sportradar.utils.domain.producers.ProducerIds;
import java.util.Arrays;
import java.util.Locale;
import java.util.stream.Stream;
import lombok.val;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

public class MarketFactoryImplWithSettlementTest {

    public static final String EXCEPTION_HANDLING_STRATEGIES =
        "com.sportradar.unifiedodds.sdk.impl.oddsentities.markets.MarketFactoryImplWithSettlementTest" +
        "#exceptionHandlingStrategies";
    public static final String NON_PREMIUM_CRICKET_PRODUCER_IDS_AND_EXCEPTION_HANDLING_STRATEGIES =
        "com.sportradar.unifiedodds.sdk.impl.oddsentities.markets.MarketFactoryImplWithSettlementTest" +
        "#nonPremiumCricketProducerIdsAndExceptionHandlingStrategies";

    private static Object[] nonPremiumCricketProducerIdsAndExceptionHandlingStrategies() {
        return Arrays
            .stream(ProducerIds.nonPremiumCricketProducerIds())
            .flatMap(id ->
                Stream.of(
                    new Object[] { id, Throw, WILL_THROW_EXCEPTIONS },
                    new Object[] { id, Catch, WILL_CATCH_EXCEPTIONS }
                )
            )
            .toArray(Object[]::new);
    }

    private static Object[] exceptionHandlingStrategies() {
        return new Object[][] { { Throw, WILL_THROW_EXCEPTIONS }, { Catch, WILL_CATCH_EXCEPTIONS } };
    }

    @Nested
    public class WhenMarketIsNotBackedWithMarketDescription {

        private final UniqueObjects<Locale> uniqueLanguages = new UniqueObjects<>(() -> Languages.any());

        @ParameterizedTest
        @MethodSource(EXCEPTION_HANDLING_STRATEGIES)
        public void marketNameRetrievalFailsInDefaultLanguage(
            ExceptionHandlingStrategy exceptionHandlingStrategy,
            ExpectationTowardsSdkErrorHandlingStrategy willFailRespectingSdkStrategy
        ) {
            val aLanguage = Languages.any();
            val marketFactory = stubbingOutCaches()
                .with(noMarketDescribingProvider())
                .withDefaultLanguage(aLanguage)
                .with(exceptionHandlingStrategy)
                .build();

            val market = marketFactory
                .buildMarketWithSettlement(SportEvents.any(), oddEvenMarketWhereWonOdd(), anyProducerId())
                .get();

            assertThat(market).getNameForDefault(aLanguage, willFailRespectingSdkStrategy);
        }

        @ParameterizedTest
        @MethodSource(EXCEPTION_HANDLING_STRATEGIES)
        public void marketNameRetrievalFailsInNonDefaultLanguage(
            ExceptionHandlingStrategy exceptionHandlingStrategy,
            ExpectationTowardsSdkErrorHandlingStrategy willRespectSdkStrategy
        ) {
            val langA = uniqueLanguages.getOne();
            val langB = uniqueLanguages.getOne();
            val marketFactory = stubbingOutCaches()
                .with(noMarketDescribingProvider())
                .with(exceptionHandlingStrategy)
                .withDefaultLanguage(langA)
                .build();

            val market = marketFactory
                .buildMarketWithSettlement(SportEvents.any(), oddEvenMarketWhereWonOdd(), anyProducerId())
                .get();

            assertThat(market).getNameForGiven(langB, willRespectSdkStrategy);
        }

        @ParameterizedTest
        @MethodSource(NON_PREMIUM_CRICKET_PRODUCER_IDS_AND_EXCEPTION_HANDLING_STRATEGIES)
        public void marketDefinitionIsNotDescribedForDefaultLanguage(
            int nonPremiumCricketProducerId,
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
                .buildMarketWithSettlement(
                    SportEvents.any(),
                    oddEvenMarketWhereWonOdd(),
                    nonPremiumCricketProducerId
                )
                .get();

            val definition = market.getMarketDefinition();
            assertThat(definition)
                .methodsBackedByMarketDescriptionFailForDefaultLanguage(
                    aLanguage,
                    ALL_METHODS,
                    willRespectSdkStrategy
                );
        }

        @ParameterizedTest
        @MethodSource(EXCEPTION_HANDLING_STRATEGIES)
        public void marketDefinitionIsNotDescribedForDefaultLanguageForPremiumCricketProducer(
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
                .buildMarketWithSettlement(
                    SportEvents.any(),
                    oddEvenMarketWhereWonOdd(),
                    PREMIUM_CRICKET_PRODUCER_ID
                )
                .get();

            val definition = market.getMarketDefinition();

            assertThat(definition)
                .methodsBackedByMarketDescriptionFailForDefaultLanguage(
                    aLanguage,
                    METHODS_EXCLUDING_VALID_MAPPINGS,
                    willRespectSdkStrategy
                );
            assertThat(definition.getValidMappings(aLanguage)).isEmpty();
        }

        @ParameterizedTest
        @MethodSource(NON_PREMIUM_CRICKET_PRODUCER_IDS_AND_EXCEPTION_HANDLING_STRATEGIES)
        public void marketDefinitionIsNotDescribedForNonDefaultLanguage(
            int nonPremiumCricketProducerId,
            ExceptionHandlingStrategy exceptionHandlingStrategy,
            ExpectationTowardsSdkErrorHandlingStrategy willRespectSdkStrategy
        ) {
            val defaultLanguage = Languages.any();
            val anotherLanguage = Languages.any();
            val marketFactory = stubbingOutCaches()
                .with(noMarketDescribingProvider())
                .with(exceptionHandlingStrategy)
                .withDefaultLanguage(defaultLanguage)
                .build();

            val market = marketFactory
                .buildMarketWithSettlement(
                    SportEvents.any(),
                    oddEvenMarketWhereWonOdd(),
                    nonPremiumCricketProducerId
                )
                .get();

            val definition = market.getMarketDefinition();
            assertThat(definition)
                .methodsBackedByMarketDescriptionFailForNonDefaultLanguage(
                    anotherLanguage,
                    ALL_METHODS,
                    willRespectSdkStrategy
                );
        }

        @ParameterizedTest
        @MethodSource(EXCEPTION_HANDLING_STRATEGIES)
        public void marketDefinitionIsNotDescribedForPremiumCricketProducerForNonDefaultLanguage(
            ExceptionHandlingStrategy exceptionHandlingStrategy,
            ExpectationTowardsSdkErrorHandlingStrategy willRespectSdkStrategy
        ) {
            val defaultLanguage = Languages.any();
            val anotherLanguage = Languages.any();
            val marketFactory = stubbingOutCaches()
                .with(noMarketDescribingProvider())
                .with(exceptionHandlingStrategy)
                .withDefaultLanguage(defaultLanguage)
                .build();

            val market = marketFactory
                .buildMarketWithSettlement(
                    SportEvents.any(),
                    oddEvenMarketWhereWonOdd(),
                    PREMIUM_CRICKET_PRODUCER_ID
                )
                .get();

            val definition = market.getMarketDefinition();
            assertThat(definition)
                .methodsBackedByMarketDescriptionFailForNonDefaultLanguage(
                    anotherLanguage,
                    METHODS_EXCLUDING_VALID_MAPPINGS,
                    willRespectSdkStrategy
                );
            assertThat(definition.getValidMappings(anotherLanguage)).isEmpty();
        }

        @ParameterizedTest
        @MethodSource(EXCEPTION_HANDLING_STRATEGIES)
        public void outcomeDefinitionIsNotDescribedForDefaultLanguage(
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
                .buildMarketWithSettlement(SportEvents.any(), oddEvenMarketWhereWonOdd(), anyProducerId())
                .get();

            val oneOfOutcomes = market.getOutcomeSettlements().get(0);
            assertThat(oneOfOutcomes)
                .getNameTemplateMethodsFailForDefaultLanguage(aLanguage, willRespectSdkStrategy);
        }

        @ParameterizedTest
        @MethodSource(EXCEPTION_HANDLING_STRATEGIES)
        public void outcomeDefinitionIsNotDescribedForNonDefaultLanguage(
            ExceptionHandlingStrategy exceptionHandlingStrategy,
            ExpectationTowardsSdkErrorHandlingStrategy willRespectSdkStrategy
        ) {
            val defaultLanguage = Languages.any();
            val anotherLanguage = Languages.any();
            val marketFactory = stubbingOutCaches()
                .with(noMarketDescribingProvider())
                .with(exceptionHandlingStrategy)
                .withDefaultLanguage(defaultLanguage)
                .build();

            val market = marketFactory
                .buildMarketWithSettlement(SportEvents.any(), oddEvenMarketWhereWonOdd(), anyProducerId())
                .get();

            val oneOfOutcomes = market.getOutcomeSettlements().get(0);
            assertThat(oneOfOutcomes)
                .getNameTemplateMethodsFailForNonDefaultLanguage(anotherLanguage, willRespectSdkStrategy);
        }

        @ParameterizedTest
        @MethodSource(EXCEPTION_HANDLING_STRATEGIES)
        public void outcomeNameRetrievalFailsInDefaultLanguage(
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
                .buildMarketWithSettlement(SportEvents.any(), oddEvenMarketWhereWonOdd(), anyProducerId())
                .get();
            val outcome = market.getOutcomeSettlements().get(0);

            assertThat(outcome)
                .nameIsNotBackedByMarketDescriptionForDefaultLanguage(aLanguage, willRespectSdkStrategy);
        }

        @ParameterizedTest
        @MethodSource(EXCEPTION_HANDLING_STRATEGIES)
        public void outcomeNameRetrievalFailsInNonDefaultLanguage(
            ExceptionHandlingStrategy exceptionHandlingStrategy,
            ExpectationTowardsSdkErrorHandlingStrategy willRespectSdkStrategy
        ) {
            val langA = uniqueLanguages.getOne();
            val langB = uniqueLanguages.getOne();
            val marketFactory = stubbingOutCaches()
                .with(noMarketDescribingProvider())
                .with(exceptionHandlingStrategy)
                .withDefaultLanguage(langA)
                .build();

            val market = marketFactory
                .buildMarketWithSettlement(SportEvents.any(), oddEvenMarketWhereWonOdd(), anyProducerId())
                .get();
            val outcome = market.getOutcomeSettlements().get(0);

            assertThat(outcome)
                .nameIsNotBackedByMarketDescriptionForNonDefaultLanguage(langB, willRespectSdkStrategy);
        }
    }

    @Nested
    public class WhenMarketIsBackedWithMarketDescription {

        private final UniqueObjects<Locale> uniqueLanguages = new UniqueObjects<>(() -> Languages.any());

        @Test
        public void marketHasNameForDefaultLanguage() {
            val langA = uniqueLanguages.getOne();
            val langB = uniqueLanguages.getOne();

            val marketFactory = stubbingOutCaches()
                .with(providing(in(langB), namesOf(oddEvenMarketDescription(), in(langB))))
                .withDefaultLanguage(langA)
                .build();

            val market = marketFactory
                .buildMarketWithSettlement(SportEvents.any(), oddEvenMarketWhereWonOdd(), anyProducerId())
                .get();

            assertThat(market).hasName(of(oddEvenMarketDescription().getName(), in(langB)));
        }

        @Test
        public void definitionHasNameTemplateForNonDefaultLanguage() {
            val langA = uniqueLanguages.getOne();
            val langB = uniqueLanguages.getOne();

            val marketFactory = stubbingOutCaches()
                .with(providing(in(langB), namesOf(oddEvenMarketDescription(), in(langB))))
                .withDefaultLanguage(langA)
                .build();

            val market = marketFactory
                .buildMarketWithSettlement(SportEvents.any(), oddEvenMarketWhereWonOdd(), anyProducerId())
                .get();

            assertThat(market.getMarketDefinition().getNameTemplate(langB))
                .isEqualTo(oddEvenMarketDescription().getName());
        }
    }

    @Nested
    class WhenMarketIsCreatedWithSettledOutcomes {

        @Test
        void wonLostAndUndecidedOutcomeResultsAreAvailable() {
            val aLanguage = Languages.any();
            val marketFactory = stubbingOutCaches()
                .with(noMarketDescribingProvider())
                .withDefaultLanguage(aLanguage)
                .with(Throw)
                .build();

            val market = marketFactory
                .buildMarketWithSettlement(
                    SportEvents.any(),
                    oneXtwoMarketCompetitor1WonDrawUndecidedCompetitor2Lost(),
                    anyProducerId()
                )
                .get();

            val outcomes = market.getOutcomeSettlements();
            assertThat(outcomes).hasWinningOutcome().withId(COMPETITOR_1_OUTCOME_ID);
            assertThat(outcomes).hasUndecidedOutcome().withId(DRAW_OUTCOME_ID);
            assertThat(outcomes).hasLostOutcome().withId(COMPETITOR_2_OUTCOME_ID);
        }

        @Test
        void unknownOutcomeResultIsMappedToUnsupportedBySdk() {
            val aLanguage = Languages.any();
            val marketFactory = stubbingOutCaches()
                .with(noMarketDescribingProvider())
                .withDefaultLanguage(aLanguage)
                .with(Throw)
                .build();

            val market = marketFactory
                .buildMarketWithSettlement(
                    SportEvents.any(),
                    oddEvenMarketWhereOddHasResultUnsupportedBySdk(),
                    anyProducerId()
                )
                .get();

            assertThat(market.getOutcomeSettlements()).hasUnsupportedBySdkOutcome().withId(ODD_OUTCOME_ID);
        }
    }
}
