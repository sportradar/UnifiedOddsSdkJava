/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */
package com.sportradar.unifiedodds.sdk.impl.oddsentities.markets;

import static com.sportradar.unifiedodds.sdk.caching.markets.MarketDescriptionFactory.namesOf;
import static com.sportradar.unifiedodds.sdk.caching.markets.MarketDescriptorProviders.noMarketDescribingProvider;
import static com.sportradar.unifiedodds.sdk.caching.markets.MarketDescriptorProviders.providing;
import static com.sportradar.unifiedodds.sdk.conn.SapiMarketDescriptions.OddEven.oddEvenMarketDescription;
import static com.sportradar.unifiedodds.sdk.conn.UfMarkets.WithOdds.anytimeGoalscorerMarket;
import static com.sportradar.unifiedodds.sdk.conn.UfMarkets.WithOdds.oddEvenMarket;
import static com.sportradar.unifiedodds.sdk.entities.SportEvents.anyStage;
import static com.sportradar.unifiedodds.sdk.impl.oddsentities.markets.ExpectationTowardsSdkErrorHandlingStrategy.WILL_CATCH_EXCEPTIONS;
import static com.sportradar.unifiedodds.sdk.impl.oddsentities.markets.ExpectationTowardsSdkErrorHandlingStrategy.WILL_THROW_EXCEPTIONS;
import static com.sportradar.unifiedodds.sdk.impl.oddsentities.markets.MarketAssert.assertThat;
import static com.sportradar.unifiedodds.sdk.impl.oddsentities.markets.MarketDefinitionAssert.MethodsBackedByMarketDescriptionInScope.ALL_METHODS;
import static com.sportradar.unifiedodds.sdk.impl.oddsentities.markets.MarketDefinitionAssert.MethodsBackedByMarketDescriptionInScope.METHODS_EXCLUDING_VALID_MAPPINGS;
import static com.sportradar.unifiedodds.sdk.impl.oddsentities.markets.MarketDefinitionAssert.assertThat;
import static com.sportradar.unifiedodds.sdk.impl.oddsentities.markets.MarketFactories.BuilderStubbingOutSportEventAndCaches.stubbingOutSportEventAndCaches;
import static com.sportradar.unifiedodds.sdk.impl.oddsentities.markets.OutcomeOddsAssert.assertThat;
import static com.sportradar.utils.domain.names.LanguageHolder.in;
import static com.sportradar.utils.domain.names.TranslationHolder.of;
import static com.sportradar.utils.domain.producers.ProducerIds.*;
import static org.assertj.core.api.Assertions.assertThat;

import com.sportradar.unifiedodds.sdk.ExceptionHandlingStrategy;
import com.sportradar.unifiedodds.sdk.entities.SportEvents;
import com.sportradar.unifiedodds.sdk.oddsentities.MarketWithOdds;
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

public class MarketFactoryImplWithOddsTest {

    public static final String EXCEPTION_HANDLING_STRATEGIES =
        "com.sportradar.unifiedodds.sdk.impl.oddsentities.markets.MarketFactoryImplWithOddsTest" +
        "#exceptionHandlingStrategies";
    public static final String NON_PREMIUM_CRICKET_PRODUCER_IDS_AND_EXCEPTION_HANDLING_STRATEGIES =
        "com.sportradar.unifiedodds.sdk.impl.oddsentities.markets.MarketFactoryImplWithOddsTest" +
        "#nonPremiumCricketProducerIdsAndExceptionHandlingStrategies";

    private static MarketWithOdds buildAnytimeGoalscorerMarketVia(MarketFactory marketFactory) {
        return marketFactory
            .buildMarketWithOdds(SportEvents.anyMatch(), anytimeGoalscorerMarket(), anyProducerId())
            .get();
    }

    private static Object[] nonPremiumCricketProducerIdsAndExceptionHandlingStrategies() {
        return Arrays
            .stream(ProducerIds.nonPremiumCricketProducerIds())
            .flatMap(id ->
                Stream.of(
                    new Object[] { id, ExceptionHandlingStrategy.Throw, WILL_THROW_EXCEPTIONS },
                    new Object[] { id, ExceptionHandlingStrategy.Catch, WILL_CATCH_EXCEPTIONS }
                )
            )
            .toArray(Object[]::new);
    }

    private static Object[] exceptionHandlingStrategies() {
        return new Object[][] {
            { ExceptionHandlingStrategy.Throw, WILL_THROW_EXCEPTIONS },
            { ExceptionHandlingStrategy.Catch, WILL_CATCH_EXCEPTIONS },
        };
    }

    @Nested
    public class WhenMarketIsNotBackedWithMarketDescription {

        private final UniqueObjects<Locale> uniqueLanguages = new UniqueObjects<>(() -> Languages.any());

        @ParameterizedTest
        @MethodSource(EXCEPTION_HANDLING_STRATEGIES)
        public void marketNameRetrievalFailsForDefaultLanguage(
            ExceptionHandlingStrategy exceptionHandlingStrategy,
            ExpectationTowardsSdkErrorHandlingStrategy willFailRespectingSdkStrategy
        ) {
            val aLanguage = Languages.any();
            val marketFactory = stubbingOutSportEventAndCaches()
                .with(noMarketDescribingProvider())
                .withDefaultLanguage(aLanguage)
                .with(exceptionHandlingStrategy)
                .build();

            val market = marketFactory
                .buildMarketWithOdds(SportEvents.any(), oddEvenMarket(), anyProducerId())
                .get();

            assertThat(market).getNameForDefault(aLanguage, willFailRespectingSdkStrategy);
        }

        @ParameterizedTest
        @MethodSource(EXCEPTION_HANDLING_STRATEGIES)
        public void marketNameRetrievalFailsForNonDefaultLanguage(
            ExceptionHandlingStrategy exceptionHandlingStrategy,
            ExpectationTowardsSdkErrorHandlingStrategy willFailRespectingSdkStrategy
        ) {
            val langA = uniqueLanguages.getOne();
            val langB = uniqueLanguages.getOne();
            val marketFactory = stubbingOutSportEventAndCaches()
                .with(noMarketDescribingProvider())
                .with(exceptionHandlingStrategy)
                .withDefaultLanguage(langA)
                .build();

            val market = marketFactory
                .buildMarketWithOdds(SportEvents.any(), oddEvenMarket(), anyProducerId())
                .get();

            assertThat(market).getNameForGiven(langB, willFailRespectingSdkStrategy);
        }

        @ParameterizedTest
        @MethodSource(NON_PREMIUM_CRICKET_PRODUCER_IDS_AND_EXCEPTION_HANDLING_STRATEGIES)
        public void marketDefinitionIsNotDescribedForDefaultLanguage(
            int nonPremiumCricketProducerId,
            ExceptionHandlingStrategy exceptionHandlingStrategy,
            ExpectationTowardsSdkErrorHandlingStrategy willRespectSdkStrategy
        ) {
            val aLanguage = Languages.any();
            val marketFactory = stubbingOutSportEventAndCaches()
                .with(noMarketDescribingProvider())
                .with(exceptionHandlingStrategy)
                .withDefaultLanguage(aLanguage)
                .build();

            val market = marketFactory
                .buildMarketWithOdds(SportEvents.any(), oddEvenMarket(), nonPremiumCricketProducerId)
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
            val marketFactory = stubbingOutSportEventAndCaches()
                .with(noMarketDescribingProvider())
                .with(exceptionHandlingStrategy)
                .withDefaultLanguage(aLanguage)
                .build();

            val market = marketFactory
                .buildMarketWithOdds(SportEvents.any(), oddEvenMarket(), PREMIUM_CRICKET_PRODUCER_ID)
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
            val marketFactory = stubbingOutSportEventAndCaches()
                .with(noMarketDescribingProvider())
                .with(exceptionHandlingStrategy)
                .withDefaultLanguage(defaultLanguage)
                .build();

            val market = marketFactory
                .buildMarketWithOdds(SportEvents.any(), oddEvenMarket(), nonPremiumCricketProducerId)
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
            val marketFactory = stubbingOutSportEventAndCaches()
                .with(noMarketDescribingProvider())
                .with(exceptionHandlingStrategy)
                .withDefaultLanguage(defaultLanguage)
                .build();

            val market = marketFactory
                .buildMarketWithOdds(SportEvents.any(), oddEvenMarket(), PREMIUM_CRICKET_PRODUCER_ID)
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
        public void nonPlayerOutcomeDefinitionIsNotDescribedForDefaultLanguage(
            ExceptionHandlingStrategy exceptionHandlingStrategy,
            ExpectationTowardsSdkErrorHandlingStrategy willRespectSdkStrategy
        ) {
            val aLanguage = Languages.any();
            val marketFactory = stubbingOutSportEventAndCaches()
                .with(noMarketDescribingProvider())
                .with(exceptionHandlingStrategy)
                .withDefaultLanguage(aLanguage)
                .build();

            val market = marketFactory
                .buildMarketWithOdds(SportEvents.any(), oddEvenMarket(), anyProducerId())
                .get();

            val oneOfOutcomes = market.getOutcomeOdds().get(0);
            assertThat(oneOfOutcomes)
                .isNonPlayerOutcome()
                .methodsBackedByMarketDescriptionFailForDefaultLanguage(aLanguage, willRespectSdkStrategy);
        }

        @ParameterizedTest
        @MethodSource(EXCEPTION_HANDLING_STRATEGIES)
        public void nonPlayerOutcomeDefinitionIsNotDescribedForNonDefaultLanguage(
            ExceptionHandlingStrategy exceptionHandlingStrategy,
            ExpectationTowardsSdkErrorHandlingStrategy willRespectSdkStrategy
        ) {
            val defaultLanguage = Languages.any();
            val anotherLanguage = Languages.any();
            val marketFactory = stubbingOutSportEventAndCaches()
                .with(noMarketDescribingProvider())
                .with(exceptionHandlingStrategy)
                .withDefaultLanguage(defaultLanguage)
                .build();

            val market = marketFactory
                .buildMarketWithOdds(SportEvents.any(), oddEvenMarket(), anyProducerId())
                .get();

            val oneOfOutcomes = market.getOutcomeOdds().get(0);
            assertThat(oneOfOutcomes)
                .isNonPlayerOutcome()
                .methodsBackedByMarketDescriptionFailForNonDefaultLanguage(
                    anotherLanguage,
                    willRespectSdkStrategy
                );
        }

        @Test
        public void nonPlayerOutcomeIsCreatedForTeamOutcomeForNonMatchSportEvent() {
            val aLanguage = Languages.any();
            val marketFactory = stubbingOutSportEventAndCaches()
                .with(noMarketDescribingProvider())
                .withDefaultLanguage(aLanguage)
                .build();

            val market = marketFactory
                .buildMarketWithOdds(anyStage(), anytimeGoalscorerMarket(), anyProducerId())
                .get();

            val oneOfOutcomes = market.getOutcomeOdds().get(0);
            assertThat(oneOfOutcomes).isNonPlayerOutcome();
        }

        @ParameterizedTest
        @MethodSource(EXCEPTION_HANDLING_STRATEGIES)
        public void playerOutcomeDefinitionIsNotDescribedForDefaultLanguage(
            ExceptionHandlingStrategy exceptionHandlingStrategy,
            ExpectationTowardsSdkErrorHandlingStrategy willRespectSdkStrategy
        ) {
            val aLanguage = Languages.any();
            val marketFactory = stubbingOutSportEventAndCaches()
                .with(noMarketDescribingProvider())
                .with(exceptionHandlingStrategy)
                .withDefaultLanguage(aLanguage)
                .build();

            val market = buildAnytimeGoalscorerMarketVia(marketFactory);

            val oneOfOutcomes = market.getOutcomeOdds().get(0);
            assertThat(oneOfOutcomes)
                .isPlayerOutcome()
                .methodsBackedByMarketDescriptionFailForDefaultLanguage(aLanguage, willRespectSdkStrategy);
        }

        @ParameterizedTest
        @MethodSource(EXCEPTION_HANDLING_STRATEGIES)
        public void playerOutcomeDefinitionIsNotDescribedForNonDefaultLanguage(
            ExceptionHandlingStrategy exceptionHandlingStrategy,
            ExpectationTowardsSdkErrorHandlingStrategy willRespectSdkStrategy
        ) {
            val defaultLanguage = Languages.any();
            val anotherLanguage = Languages.any();
            val marketFactory = stubbingOutSportEventAndCaches()
                .with(noMarketDescribingProvider())
                .with(exceptionHandlingStrategy)
                .withDefaultLanguage(defaultLanguage)
                .build();

            val market = buildAnytimeGoalscorerMarketVia(marketFactory);

            val oneOfOutcomes = market.getOutcomeOdds().get(0);
            assertThat(oneOfOutcomes)
                .isPlayerOutcome()
                .methodsBackedByMarketDescriptionFailForNonDefaultLanguage(
                    anotherLanguage,
                    willRespectSdkStrategy
                );
        }

        @ParameterizedTest
        @MethodSource(EXCEPTION_HANDLING_STRATEGIES)
        public void outcomeNameRetrievalFailsInDefaultLanguage(
            ExceptionHandlingStrategy exceptionHandlingStrategy,
            ExpectationTowardsSdkErrorHandlingStrategy willRespectSdkStrategy
        ) {
            val aLanguage = Languages.any();
            val marketFactory = stubbingOutSportEventAndCaches()
                .with(noMarketDescribingProvider())
                .withDefaultLanguage(aLanguage)
                .with(exceptionHandlingStrategy)
                .build();

            val market = marketFactory
                .buildMarketWithOdds(SportEvents.any(), oddEvenMarket(), anyProducerId())
                .get();
            val outcome = market.getOutcomeOdds().get(0);

            OutcomeAssert
                .assertThat(outcome)
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
            val marketFactory = stubbingOutSportEventAndCaches()
                .with(noMarketDescribingProvider())
                .with(exceptionHandlingStrategy)
                .withDefaultLanguage(langA)
                .build();

            val market = marketFactory
                .buildMarketWithOdds(SportEvents.any(), oddEvenMarket(), anyProducerId())
                .get();
            val outcome = market.getOutcomeOdds().get(0);

            OutcomeAssert
                .assertThat(outcome)
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

            val marketFactory = stubbingOutSportEventAndCaches()
                .with(providing(in(langB), namesOf(oddEvenMarketDescription(), in(langB))))
                .withDefaultLanguage(langA)
                .build();

            val market = marketFactory
                .buildMarketWithOdds(SportEvents.any(), oddEvenMarket(), anyProducerId())
                .get();

            assertThat(market).hasName(of(oddEvenMarketDescription().getName(), in(langB)));
        }

        @Test
        public void definitionHasNameTemplateForNonDefaultLanguage() {
            val langA = uniqueLanguages.getOne();
            val langB = uniqueLanguages.getOne();

            val marketFactory = stubbingOutSportEventAndCaches()
                .with(providing(in(langB), namesOf(oddEvenMarketDescription(), in(langB))))
                .withDefaultLanguage(langA)
                .build();

            val market = marketFactory
                .buildMarketWithOdds(SportEvents.any(), oddEvenMarket(), anyProducerId())
                .get();

            assertThat(market.getMarketDefinition().getNameTemplate(langB))
                .isEqualTo(oddEvenMarketDescription().getName());
        }
    }
}
