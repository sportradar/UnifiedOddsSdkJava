/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */
package com.sportradar.unifiedodds.sdk.impl.oddsentities.markets;

import static com.sportradar.unifiedodds.sdk.caching.markets.MarketDescriptions.namesOf;
import static com.sportradar.unifiedodds.sdk.caching.markets.MarketDescriptorProviders.noMarketDescribingProvider;
import static com.sportradar.unifiedodds.sdk.caching.markets.MarketDescriptorProviders.providing;
import static com.sportradar.unifiedodds.sdk.conn.SapiMarketDescriptions.oddEvenDescription;
import static com.sportradar.unifiedodds.sdk.conn.UfMarkets.Simple.oddEven;
import static com.sportradar.unifiedodds.sdk.impl.oddsentities.markets.ExpectationTowardsSdkErrorHandlingStrategy.WILL_CATCH_EXCEPTIONS;
import static com.sportradar.unifiedodds.sdk.impl.oddsentities.markets.ExpectationTowardsSdkErrorHandlingStrategy.WILL_THROW_EXCEPTIONS;
import static com.sportradar.unifiedodds.sdk.impl.oddsentities.markets.MarketAssert.assertThat;
import static com.sportradar.unifiedodds.sdk.impl.oddsentities.markets.MarketDefinitionAssert.MethodsBackedByMarketDescriptionInScope.ALL_METHODS;
import static com.sportradar.unifiedodds.sdk.impl.oddsentities.markets.MarketDefinitionAssert.MethodsBackedByMarketDescriptionInScope.METHODS_EXCLUDING_VALID_MAPPINGS;
import static com.sportradar.unifiedodds.sdk.impl.oddsentities.markets.MarketDefinitionAssert.assertThat;
import static com.sportradar.unifiedodds.sdk.impl.oddsentities.markets.MarketFactories.BuilderStubbingOutSportEventAndCaches.stubbingOutSportEventAndCaches;
import static com.sportradar.utils.domain.names.LanguageHolder.in;
import static com.sportradar.utils.domain.names.TranslationHolder.of;
import static com.sportradar.utils.domain.names.TranslationHolder.with;
import static com.sportradar.utils.domain.producers.ProducerIds.PREMIUM_CRICKET_PRODUCER_ID;
import static com.sportradar.utils.domain.producers.ProducerIds.anyProducerId;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.sportradar.unifiedodds.sdk.ExceptionHandlingStrategy;
import com.sportradar.unifiedodds.sdk.caching.markets.MarketDescriptions;
import com.sportradar.unifiedodds.sdk.entities.SportEvents;
import com.sportradar.utils.domain.UniqueObjects;
import com.sportradar.utils.domain.names.Languages;
import com.sportradar.utils.domain.producers.ProducerIds;
import java.util.Arrays;
import java.util.Locale;
import java.util.stream.Stream;
import junitparams.JUnitParamsRunner;
import junitparams.Parameters;
import lombok.val;
import org.junit.Test;
import org.junit.experimental.runners.Enclosed;
import org.junit.runner.RunWith;

@RunWith(Enclosed.class)
public class MarketFactoryImplCancelTest {

    @RunWith(JUnitParamsRunner.class)
    public static class WhenMarketIsNotBackedWithMarketDescription {

        private final UniqueObjects<Locale> uniqueLanguages = new UniqueObjects<>(() -> Languages.any());

        @Test
        @Parameters(method = "exceptionHandlingStrategies")
        public void marketNameRetrievalFailsInDefaultLanguage(
            ExceptionHandlingStrategy exceptionHandlingStrategy,
            ExpectationTowardsSdkErrorHandlingStrategy willRespectSdkStrategy
        ) {
            val aLanguage = Languages.any();
            val marketFactory = stubbingOutSportEventAndCaches()
                .with(noMarketDescribingProvider())
                .withDefaultLanguage(aLanguage)
                .with(exceptionHandlingStrategy)
                .build();

            val market = marketFactory.buildMarketCancel(SportEvents.any(), oddEven(), anyProducerId()).get();

            assertThat(market)
                .nameIsNotBackedByMarketDescriptionForDefaultLanguage(aLanguage, willRespectSdkStrategy);
        }

        @Test
        @Parameters(method = "exceptionHandlingStrategies")
        public void marketNameRetrievalFailsInNonDefaultLanguage(
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

            val market = marketFactory.buildMarketCancel(SportEvents.any(), oddEven(), anyProducerId()).get();

            assertThat(market)
                .nameIsNotBackedByMarketDescriptionForNonDefaultLanguage(langB, willRespectSdkStrategy);
        }

        @Test
        @Parameters(method = "nonPremiumCricketProducerIdsAndExceptionHandlingStrategies")
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
                .buildMarketCancel(SportEvents.any(), oddEven(), nonPremiumCricketProducerId)
                .get();

            val definition = market.getMarketDefinition();
            assertThat(definition)
                .methodsBackedByMarketDescriptionFailForDefaultLanguage(
                    aLanguage,
                    ALL_METHODS,
                    willRespectSdkStrategy
                );
        }

        @Test
        @Parameters(method = "exceptionHandlingStrategies")
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
                .buildMarketCancel(SportEvents.any(), oddEven(), PREMIUM_CRICKET_PRODUCER_ID)
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

        @Test
        @Parameters(method = "nonPremiumCricketProducerIdsAndExceptionHandlingStrategies")
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
                .buildMarketCancel(SportEvents.any(), oddEven(), nonPremiumCricketProducerId)
                .get();

            val definition = market.getMarketDefinition();
            assertThat(definition)
                .methodsBackedByMarketDescriptionFailForNonDefaultLanguage(
                    anotherLanguage,
                    ALL_METHODS,
                    willRespectSdkStrategy
                );
        }

        @Test
        @Parameters(method = "exceptionHandlingStrategies")
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
                .buildMarketCancel(SportEvents.any(), oddEven(), PREMIUM_CRICKET_PRODUCER_ID)
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

        private Object[] nonPremiumCricketProducerIdsAndExceptionHandlingStrategies() {
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

        private Object[] exceptionHandlingStrategies() {
            return new Object[][] {
                { ExceptionHandlingStrategy.Throw, WILL_THROW_EXCEPTIONS },
                { ExceptionHandlingStrategy.Catch, WILL_CATCH_EXCEPTIONS },
            };
        }
    }

    public static class WhenMarketIsBackedWithMarketDescription {

        private final UniqueObjects<Locale> uniqueLanguages = new UniqueObjects<>(() -> Languages.any());

        @Test
        public void marketHasNameForDefaultLanguage() {
            val langA = uniqueLanguages.getOne();
            val langB = uniqueLanguages.getOne();

            val marketFactory = stubbingOutSportEventAndCaches()
                .with(providing(in(langB), namesOf(oddEvenDescription(), in(langB))))
                .withDefaultLanguage(langA)
                .build();

            val market = marketFactory.buildMarketCancel(SportEvents.any(), oddEven(), anyProducerId()).get();

            assertThat(market).hasName(of(oddEvenDescription().getName(), in(langB)));
        }

        @Test
        public void definitionHasNameTemplateForNonDefaultLanguage() {
            val langA = uniqueLanguages.getOne();
            val langB = uniqueLanguages.getOne();

            val marketFactory = stubbingOutSportEventAndCaches()
                .with(providing(in(langB), namesOf(oddEvenDescription(), in(langB))))
                .withDefaultLanguage(langA)
                .build();

            val market = marketFactory.buildMarketCancel(SportEvents.any(), oddEven(), anyProducerId()).get();

            assertThat(market.getMarketDefinition().getNameTemplate(langB))
                .isEqualTo(oddEvenDescription().getName());
        }
    }
}
