/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */
package com.sportradar.unifiedodds.sdk.impl.oddsentities.markets;

import static com.sportradar.unifiedodds.sdk.caching.markets.MarketDescriptionFactory.namesOf;
import static com.sportradar.unifiedodds.sdk.caching.markets.MarketDescriptorProviders.*;
import static com.sportradar.unifiedodds.sdk.conn.SapiMarketDescriptions.OddEven.evenOutcomeDescription;
import static com.sportradar.unifiedodds.sdk.conn.SapiMarketDescriptions.OddEven.oddEvenMarketDescription;
import static com.sportradar.unifiedodds.sdk.conn.SapiMarketDescriptions.removeAllOutcomesFrom;
import static com.sportradar.unifiedodds.sdk.conn.UfMarkets.WithOdds.oddEvenMarket;
import static com.sportradar.unifiedodds.sdk.conn.marketids.OddEvenMarketIds.EVEN_OUTCOME_ID;
import static com.sportradar.unifiedodds.sdk.impl.oddsentities.markets.ExpectationTowardsSdkErrorHandlingStrategy.WILL_CATCH_EXCEPTIONS;
import static com.sportradar.unifiedodds.sdk.impl.oddsentities.markets.ExpectationTowardsSdkErrorHandlingStrategy.WILL_THROW_EXCEPTIONS;
import static com.sportradar.unifiedodds.sdk.impl.oddsentities.markets.MarketAssert.assertThat;
import static com.sportradar.unifiedodds.sdk.impl.oddsentities.markets.MarketDefinitionAssert.MethodsBackedByMarketDescriptionInScope.ALL_METHODS;
import static com.sportradar.unifiedodds.sdk.impl.oddsentities.markets.MarketDefinitionAssert.MethodsBackedByMarketDescriptionInScope.METHODS_EXCLUDING_VALID_MAPPINGS;
import static com.sportradar.unifiedodds.sdk.impl.oddsentities.markets.MarketDefinitionAssert.assertThat;
import static com.sportradar.unifiedodds.sdk.impl.oddsentities.markets.MarketFactories.BuilderStubbingOutSportEventAndCaches.stubbingOutSportEventAndCaches;
import static com.sportradar.unifiedodds.sdk.impl.oddsentities.markets.OutcomeAssert.assertThat;
import static com.sportradar.utils.domain.names.LanguageHolder.in;
import static com.sportradar.utils.domain.names.TranslationHolder.of;
import static com.sportradar.utils.domain.producers.ProducerIds.PREMIUM_CRICKET_PRODUCER_ID;
import static com.sportradar.utils.domain.producers.ProducerIds.anyProducerId;
import static com.sportradar.utils.generic.testing.Exceptions.ignoringExceptions;
import static com.sportradar.utils.time.TimeInterval.seconds;
import static java.util.Locale.ENGLISH;
import static java.util.Locale.FRENCH;
import static org.assertj.core.api.Assertions.assertThat;

import com.sportradar.uf.sportsapi.datamodel.DescMarket;
import com.sportradar.uf.sportsapi.datamodel.DescOutcomes;
import com.sportradar.unifiedodds.sdk.ExceptionHandlingStrategy;
import com.sportradar.unifiedodds.sdk.conn.SapiMarketDescriptions;
import com.sportradar.unifiedodds.sdk.conn.marketids.OddEvenMarketIds;
import com.sportradar.unifiedodds.sdk.entities.SportEvents;
import com.sportradar.unifiedodds.sdk.oddsentities.MarketWithProbabilities;
import com.sportradar.unifiedodds.sdk.oddsentities.OutcomeProbabilities;
import com.sportradar.unifiedodds.sdk.testutil.generic.concurrent.AtomicActionPerformer;
import com.sportradar.utils.domain.UniqueObjects;
import com.sportradar.utils.domain.names.Languages;
import com.sportradar.utils.domain.producers.ProducerIds;
import com.sportradar.utils.time.TimeInterval;
import com.sportradar.utils.time.TimeUtilsStub;
import java.time.Instant;
import java.util.Arrays;
import java.util.Locale;
import java.util.stream.Stream;
import lombok.val;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

public class MarketFactoryImplWithProbabilitiesTest {

    public static final String EXCEPTION_HANDLING_STRATEGIES =
        "com.sportradar.unifiedodds.sdk.impl.oddsentities.markets.MarketFactoryImplWithProbabilitiesTest" +
        "#exceptionHandlingStrategies";
    public static final String NON_PREMIUM_CRICKET_PRODUCER_IDS_AND_EXCEPTION_HANDLING_STRATEGIES =
        "com.sportradar.unifiedodds.sdk.impl.oddsentities.markets.MarketFactoryImplWithProbabilitiesTest" +
        "#nonPremiumCricketProducerIdsAndExceptionHandlingStrategies";

    public static final TimeInterval CIRCUIT_BREAKER_OPEN_PERIOD = seconds(30);

    private MarketFactoryImplWithProbabilitiesTest() {}

    private static OutcomeProbabilities getEvenOutcomeFrom(MarketWithProbabilities market) {
        return market.getOutcomeProbabilities().stream().filter(OddEvenMarketIds::isEven).findFirst().get();
    }

    private static DescMarket removeEvenOutcomeFrom(DescMarket oddEvenDescription) {
        oddEvenDescription.getOutcomes().getOutcome().removeIf(outcome -> isEven(outcome));
        return oddEvenDescription;
    }

    private static boolean isEven(DescOutcomes.Outcome outcome) {
        return outcome.getId().equals(EVEN_OUTCOME_ID);
    }

    private static void getName(OutcomeProbabilities outcome) {
        ignoringExceptions(() -> outcome.getName());
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
                .buildMarketWithProbabilities(SportEvents.any(), oddEvenMarket(), anyProducerId())
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
                .buildMarketWithProbabilities(SportEvents.any(), oddEvenMarket(), anyProducerId())
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
                .buildMarketWithProbabilities(SportEvents.any(), oddEvenMarket(), nonPremiumCricketProducerId)
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
                .buildMarketWithProbabilities(SportEvents.any(), oddEvenMarket(), PREMIUM_CRICKET_PRODUCER_ID)
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
                .buildMarketWithProbabilities(SportEvents.any(), oddEvenMarket(), nonPremiumCricketProducerId)
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
                .buildMarketWithProbabilities(SportEvents.any(), oddEvenMarket(), PREMIUM_CRICKET_PRODUCER_ID)
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
            val marketFactory = stubbingOutSportEventAndCaches()
                .with(noMarketDescribingProvider())
                .with(exceptionHandlingStrategy)
                .withDefaultLanguage(aLanguage)
                .build();

            val market = marketFactory
                .buildMarketWithProbabilities(SportEvents.any(), oddEvenMarket(), anyProducerId())
                .get();

            val oneOfOutcomes = market.getOutcomeProbabilities().get(0);
            assertThat(oneOfOutcomes)
                .methodsBackedByMarketDescriptionFailForDefaultLanguage(aLanguage, willRespectSdkStrategy);
        }

        @ParameterizedTest
        @MethodSource(EXCEPTION_HANDLING_STRATEGIES)
        public void outcomeDefinitionIsNotDescribedForNonDefaultLanguage(
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
                .buildMarketWithProbabilities(SportEvents.any(), oddEvenMarket(), anyProducerId())
                .get();

            val oneOfOutcomes = market.getOutcomeProbabilities().get(0);
            assertThat(oneOfOutcomes)
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
                .buildMarketWithProbabilities(SportEvents.any(), oddEvenMarket(), anyProducerId())
                .get();
            val outcome = market.getOutcomeProbabilities().get(0);

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
            val marketFactory = stubbingOutSportEventAndCaches()
                .with(noMarketDescribingProvider())
                .with(exceptionHandlingStrategy)
                .withDefaultLanguage(langA)
                .build();

            val market = marketFactory
                .buildMarketWithProbabilities(SportEvents.any(), oddEvenMarket(), anyProducerId())
                .get();
            val outcome = market.getOutcomeProbabilities().get(0);

            assertThat(outcome)
                .nameIsNotBackedByMarketDescriptionForNonDefaultLanguage(langB, willRespectSdkStrategy);
        }
    }

    @Nested
    public class WhenMarketDescriptionHasNoOutcomes {

        private static final long MIDNIGHT_TIMESTAMP_MILLIS = 1664402400000L;
        private final TimeUtilsStub time = TimeUtilsStub
            .threadSafe(new AtomicActionPerformer())
            .withCurrentTime(Instant.ofEpochMilli(MIDNIGHT_TIMESTAMP_MILLIS));

        private final UniqueObjects<Locale> uniqueLanguages = new UniqueObjects<>(() -> Languages.any());

        @ParameterizedTest
        @MethodSource(EXCEPTION_HANDLING_STRATEGIES)
        public void outcomeNameRetrievalFails(
            ExceptionHandlingStrategy exceptionHandlingStrategy,
            ExpectationTowardsSdkErrorHandlingStrategy willRespectSdkStrategy
        ) {
            val aLanguage = Languages.any();
            val faultyDescription = removeAllOutcomesFrom(oddEvenMarketDescription());
            val marketFactory = stubbingOutSportEventAndCaches()
                .with(providing(in(aLanguage), namesOf(faultyDescription, in(aLanguage))))
                .withDefaultLanguage(aLanguage)
                .with(exceptionHandlingStrategy)
                .build();

            val market = marketFactory
                .buildMarketWithProbabilities(SportEvents.any(), oddEvenMarket(), anyProducerId())
                .get();
            val outcome = getEvenOutcomeFrom(market);

            assertThat(outcome)
                .nameIsNotBackedByOutcomeDescriptionForDefaultLanguage(aLanguage, willRespectSdkStrategy);
        }

        @ParameterizedTest
        @MethodSource(EXCEPTION_HANDLING_STRATEGIES)
        public void outcomeNameRetrievalFailsForNonDefaultLanguage(
            ExceptionHandlingStrategy exceptionHandlingStrategy,
            ExpectationTowardsSdkErrorHandlingStrategy willRespectSdkStrategy
        ) {
            val langA = uniqueLanguages.getOne();
            val langB = uniqueLanguages.getOne();
            val faultyDescription = removeAllOutcomesFrom(oddEvenMarketDescription());
            val marketFactory = stubbingOutSportEventAndCaches()
                .with(providing(in(langB), namesOf(faultyDescription, in(langB))))
                .withDefaultLanguage(langA)
                .with(exceptionHandlingStrategy)
                .build();

            val market = marketFactory
                .buildMarketWithProbabilities(SportEvents.any(), oddEvenMarket(), anyProducerId())
                .get();
            val outcome = getEvenOutcomeFrom(market);

            assertThat(outcome)
                .nameIsNotBackedByOutcomeDescriptionForNonDefaultLanguage(langB, willRespectSdkStrategy);
        }

        @ParameterizedTest
        @MethodSource(EXCEPTION_HANDLING_STRATEGIES)
        public void outcomeNameRetrievalFailsDueToMissingOutcomeInSpiteOfReloadOfMarkets(
            ExceptionHandlingStrategy exceptionHandlingStrategy,
            ExpectationTowardsSdkErrorHandlingStrategy willRespectSdkStrategy
        ) {
            val language = Languages.any();
            val faultyDescription = namesOf(removeAllOutcomesFrom(oddEvenMarketDescription()), in(language));
            val descriptionProvider = providing(in(language), faultyDescription);
            val marketFactory = stubbingOutSportEventAndCaches()
                .with(descriptionProvider)
                .withDefaultLanguage(language)
                .with(exceptionHandlingStrategy)
                .with(time)
                .build();

            val market = marketFactory
                .buildMarketWithProbabilities(SportEvents.any(), oddEvenMarket(), anyProducerId())
                .get();

            val outcome = getEvenOutcomeFrom(market);
            assertThat(outcome)
                .nameIsNotBackedByOutcomeDescriptionForDefaultLanguage(language, willRespectSdkStrategy);
            descriptionProvider.verifyDescriptionWasReloaded();
        }

        @Test
        public void reloadingMarketsRepairsMissingOutcome() {
            val language = ENGLISH;
            val faultyDescription = namesOf(removeAllOutcomesFrom(oddEvenMarketDescription()), in(language));
            val repairedDescription = namesOf(oddEvenMarketDescription(), in(language));
            val descriptionProvider = providing(
                in(language),
                faultyDescription,
                andAfterReloading(in(language), repairedDescription)
            );
            val marketFactory = stubbingOutSportEventAndCaches()
                .with(descriptionProvider)
                .withDefaultLanguage(language)
                .with(ExceptionHandlingStrategy.Throw)
                .with(time)
                .build();

            val market = marketFactory
                .buildMarketWithProbabilities(SportEvents.any(), oddEvenMarket(), anyProducerId())
                .get();

            val outcome = getEvenOutcomeFrom(market);
            assertThat(outcome)
                .hasNameInDefaultLanguage(of(evenOutcomeDescription(language).getName(), in(language)));
        }

        @ParameterizedTest
        @MethodSource(EXCEPTION_HANDLING_STRATEGIES)
        public void reloadingOfMarketsAreThrottledTo1CallPer30secondsTimeframe(
            ExceptionHandlingStrategy exceptionHandlingStrategy,
            ExpectationTowardsSdkErrorHandlingStrategy notUsed
        ) {
            val language = Languages.any();
            val faultyDescription = namesOf(removeAllOutcomesFrom(oddEvenMarketDescription()), in(language));
            val descriptionProvider = providing(in(language), faultyDescription);
            val marketFactory = stubbingOutSportEventAndCaches()
                .with(descriptionProvider)
                .withDefaultLanguage(language)
                .with(exceptionHandlingStrategy)
                .with(time)
                .build();

            val market = marketFactory
                .buildMarketWithProbabilities(SportEvents.any(), oddEvenMarket(), anyProducerId())
                .get();
            val outcome = getEvenOutcomeFrom(market);
            getName(outcome);
            time.tick(CIRCUIT_BREAKER_OPEN_PERIOD.minus(seconds(1)));
            getName(outcome);

            descriptionProvider.verifyDescriptionWasReloadedTimes(1);
        }

        @ParameterizedTest
        @MethodSource(EXCEPTION_HANDLING_STRATEGIES)
        public void throttlingAllowsToReloadMarketsAgainAfter30secondsTimeframePasses(
            ExceptionHandlingStrategy exceptionHandlingStrategy,
            ExpectationTowardsSdkErrorHandlingStrategy notUsed
        ) {
            val language = Languages.any();
            val faultyDescription = namesOf(removeAllOutcomesFrom(oddEvenMarketDescription()), in(language));
            val descriptionProvider = providing(in(language), faultyDescription);
            val marketFactory = stubbingOutSportEventAndCaches()
                .with(descriptionProvider)
                .withDefaultLanguage(language)
                .with(exceptionHandlingStrategy)
                .with(time)
                .build();

            val market = marketFactory
                .buildMarketWithProbabilities(SportEvents.any(), oddEvenMarket(), anyProducerId())
                .get();
            val outcome = getEvenOutcomeFrom(market);
            getName(outcome);
            time.tick(CIRCUIT_BREAKER_OPEN_PERIOD.plus(seconds(1)));
            getName(outcome);

            descriptionProvider.verifyDescriptionWasReloadedTimes(2);
        }

        private OutcomeProbabilities getEvenOutcomeFrom(MarketWithProbabilities market) {
            return market
                .getOutcomeProbabilities()
                .stream()
                .filter(OddEvenMarketIds::isEven)
                .findFirst()
                .get();
        }
    }

    @Nested
    public class WhenMarketDescriptionIsMissingOutcome {

        private static final long MIDNIGHT_TIMESTAMP_MILLIS = 1664402400000L;
        private final TimeUtilsStub time = TimeUtilsStub
            .threadSafe(new AtomicActionPerformer())
            .withCurrentTime(Instant.ofEpochMilli(MIDNIGHT_TIMESTAMP_MILLIS));

        private final UniqueObjects<Locale> uniqueLanguages = new UniqueObjects<>(() -> Languages.any());

        @ParameterizedTest
        @MethodSource(EXCEPTION_HANDLING_STRATEGIES)
        public void outcomeNameRetrievalFails(
            ExceptionHandlingStrategy exceptionHandlingStrategy,
            ExpectationTowardsSdkErrorHandlingStrategy willRespectSdkStrategy
        ) {
            val aLanguage = Languages.any();
            val faultyDescription = removeEvenOutcomeFrom(oddEvenMarketDescription());
            val marketFactory = stubbingOutSportEventAndCaches()
                .with(providing(in(aLanguage), namesOf(faultyDescription, in(aLanguage))))
                .withDefaultLanguage(aLanguage)
                .with(exceptionHandlingStrategy)
                .build();

            val market = marketFactory
                .buildMarketWithProbabilities(SportEvents.any(), oddEvenMarket(), anyProducerId())
                .get();
            val outcome = getEvenOutcomeFrom(market);

            assertThat(outcome)
                .nameIsNotBackedByOutcomeDescriptionForDefaultLanguage(aLanguage, willRespectSdkStrategy);
        }

        @ParameterizedTest
        @MethodSource(EXCEPTION_HANDLING_STRATEGIES)
        public void outcomeNameRetrievalFailsForNonDefaultLanguage(
            ExceptionHandlingStrategy exceptionHandlingStrategy,
            ExpectationTowardsSdkErrorHandlingStrategy willRespectSdkStrategy
        ) {
            val langA = uniqueLanguages.getOne();
            val langB = uniqueLanguages.getOne();
            val faultyDescription = removeEvenOutcomeFrom(oddEvenMarketDescription());
            val marketFactory = stubbingOutSportEventAndCaches()
                .with(providing(in(langB), namesOf(faultyDescription, in(langB))))
                .withDefaultLanguage(langA)
                .with(exceptionHandlingStrategy)
                .build();

            val market = marketFactory
                .buildMarketWithProbabilities(SportEvents.any(), oddEvenMarket(), anyProducerId())
                .get();
            val outcome = getEvenOutcomeFrom(market);

            assertThat(outcome)
                .nameIsNotBackedByOutcomeDescriptionForNonDefaultLanguage(langB, willRespectSdkStrategy);
        }

        @ParameterizedTest
        @MethodSource(EXCEPTION_HANDLING_STRATEGIES)
        public void outcomeNameRetrievalFailsDueToMissingOutcomeInSpiteOfReloadOfMarkets(
            ExceptionHandlingStrategy exceptionHandlingStrategy,
            ExpectationTowardsSdkErrorHandlingStrategy willRespectSdkStrategy
        ) {
            val language = Languages.any();
            val faultyDescription = namesOf(removeEvenOutcomeFrom(oddEvenMarketDescription()), in(language));
            val descriptionProvider = providing(in(language), faultyDescription);
            val marketFactory = stubbingOutSportEventAndCaches()
                .with(descriptionProvider)
                .withDefaultLanguage(language)
                .with(exceptionHandlingStrategy)
                .with(time)
                .build();

            val market = marketFactory
                .buildMarketWithProbabilities(SportEvents.any(), oddEvenMarket(), anyProducerId())
                .get();

            val outcome = getEvenOutcomeFrom(market);
            assertThat(outcome)
                .nameIsNotBackedByOutcomeDescriptionForDefaultLanguage(language, willRespectSdkStrategy);
            descriptionProvider.verifyDescriptionWasReloaded();
        }

        @Test
        public void reloadingMarketsRepairsMissingOutcome() {
            val language = ENGLISH;
            val faultyDescription = namesOf(removeEvenOutcomeFrom(oddEvenMarketDescription()), in(language));
            val repairedDescription = namesOf(oddEvenMarketDescription(), in(language));
            val descriptionProvider = providing(
                in(language),
                faultyDescription,
                andAfterReloading(in(language), repairedDescription)
            );
            val marketFactory = stubbingOutSportEventAndCaches()
                .with(descriptionProvider)
                .withDefaultLanguage(language)
                .with(ExceptionHandlingStrategy.Throw)
                .with(time)
                .build();

            val market = marketFactory
                .buildMarketWithProbabilities(SportEvents.any(), oddEvenMarket(), anyProducerId())
                .get();

            val outcome = getEvenOutcomeFrom(market);
            assertThat(outcome)
                .hasNameInDefaultLanguage(of(evenOutcomeDescription(language).getName(), in(language)));
        }

        @ParameterizedTest
        @MethodSource(EXCEPTION_HANDLING_STRATEGIES)
        public void reloadingOfMarketsAreThrottledTo1CallPer30secondsTimeframe(
            ExceptionHandlingStrategy exceptionHandlingStrategy,
            ExpectationTowardsSdkErrorHandlingStrategy notUsed
        ) {
            val language = Languages.any();
            val faultyDescription = namesOf(removeEvenOutcomeFrom(oddEvenMarketDescription()), in(language));
            val descriptionProvider = providing(in(language), faultyDescription);
            val marketFactory = stubbingOutSportEventAndCaches()
                .with(descriptionProvider)
                .withDefaultLanguage(language)
                .with(exceptionHandlingStrategy)
                .with(time)
                .build();

            val market = marketFactory
                .buildMarketWithProbabilities(SportEvents.any(), oddEvenMarket(), anyProducerId())
                .get();
            val outcome = getEvenOutcomeFrom(market);
            getName(outcome);
            time.tick(CIRCUIT_BREAKER_OPEN_PERIOD.minus(seconds(1)));
            getName(outcome);

            descriptionProvider.verifyDescriptionWasReloadedTimes(1);
        }

        @ParameterizedTest
        @MethodSource(EXCEPTION_HANDLING_STRATEGIES)
        public void throttlingAllowsToReloadMarketsAgainAfter30secondsTimeframePasses(
            ExceptionHandlingStrategy exceptionHandlingStrategy,
            ExpectationTowardsSdkErrorHandlingStrategy notUsed
        ) {
            val language = Languages.any();
            val faultyDescription = namesOf(removeEvenOutcomeFrom(oddEvenMarketDescription()), in(language));
            val descriptionProvider = providing(in(language), faultyDescription);
            val marketFactory = stubbingOutSportEventAndCaches()
                .with(descriptionProvider)
                .withDefaultLanguage(language)
                .with(exceptionHandlingStrategy)
                .with(time)
                .build();

            val market = marketFactory
                .buildMarketWithProbabilities(SportEvents.any(), oddEvenMarket(), anyProducerId())
                .get();
            val outcome = getEvenOutcomeFrom(market);
            getName(outcome);
            time.tick(CIRCUIT_BREAKER_OPEN_PERIOD.plus(seconds(1)));
            getName(outcome);

            descriptionProvider.verifyDescriptionWasReloadedTimes(2);
        }

        private OutcomeProbabilities getEvenOutcomeFrom(MarketWithProbabilities market) {
            return market.getOutcomeProbabilities().get(1);
        }
    }

    @Nested
    public class WhenMarketDescriptionIsMissingOutcomeTranslation {

        private static final long MIDNIGHT_TIMESTAMP_MILLIS = 1664402400000L;
        private final TimeUtilsStub time = TimeUtilsStub
            .threadSafe(new AtomicActionPerformer())
            .withCurrentTime(Instant.ofEpochMilli(MIDNIGHT_TIMESTAMP_MILLIS));

        private final UniqueObjects<Locale> uniqueLanguages = new UniqueObjects<>(() -> Languages.any());

        @ParameterizedTest
        @MethodSource(EXCEPTION_HANDLING_STRATEGIES)
        public void outcomeNameRetrievalFails(
            ExceptionHandlingStrategy exceptionHandlingStrategy,
            ExpectationTowardsSdkErrorHandlingStrategy willRespectSdkStrategy
        ) {
            val langA = FRENCH;
            val langB = ENGLISH;

            val marketDescription = namesOf(
                oddEvenMarketDescription(langA),
                in(langA),
                removeEvenOutcomeFrom(oddEvenMarketDescription(langB)),
                in(langB)
            );
            val marketFactory = stubbingOutSportEventAndCaches()
                .with(providing(in(langA), in(langB), marketDescription))
                .withDefaultLanguage(langB)
                .with(exceptionHandlingStrategy)
                .build();

            val market = marketFactory
                .buildMarketWithProbabilities(SportEvents.any(), oddEvenMarket(), anyProducerId())
                .get();
            val outcome = getEvenOutcomeFrom(market);

            assertThat(outcome)
                .nameIsNotBackedByOutcomeDescriptionForDefaultLanguage(langB, willRespectSdkStrategy);
        }

        @ParameterizedTest
        @MethodSource(EXCEPTION_HANDLING_STRATEGIES)
        public void outcomeNameRetrievalFailsForNonDefaultLanguage(
            ExceptionHandlingStrategy exceptionHandlingStrategy,
            ExpectationTowardsSdkErrorHandlingStrategy willRespectSdkStrategy
        ) {
            val langA = uniqueLanguages.getOne();
            val langB = uniqueLanguages.getOne();
            val faultyDescription = removeEvenOutcomeFrom(oddEvenMarketDescription());
            val marketFactory = stubbingOutSportEventAndCaches()
                .with(providing(in(langB), namesOf(faultyDescription, in(langB))))
                .withDefaultLanguage(langA)
                .with(exceptionHandlingStrategy)
                .build();

            val market = marketFactory
                .buildMarketWithProbabilities(SportEvents.any(), oddEvenMarket(), anyProducerId())
                .get();
            val outcome = getEvenOutcomeFrom(market);

            assertThat(outcome)
                .nameIsNotBackedByOutcomeDescriptionForNonDefaultLanguage(langB, willRespectSdkStrategy);
        }

        @ParameterizedTest
        @MethodSource(EXCEPTION_HANDLING_STRATEGIES)
        public void marketsAreNotReloaded(
            ExceptionHandlingStrategy exceptionHandlingStrategy,
            ExpectationTowardsSdkErrorHandlingStrategy notUsed
        ) {
            val langA = FRENCH;
            val langB = ENGLISH;

            val marketDescription = namesOf(
                oddEvenMarketDescription(langA),
                in(langA),
                removeEvenOutcomeFrom(oddEvenMarketDescription(langB)),
                in(langB)
            );
            val descriptionProvider = providing(in(langA), in(langB), marketDescription);
            val marketFactory = stubbingOutSportEventAndCaches()
                .with(descriptionProvider)
                .withDefaultLanguage(langB)
                .with(exceptionHandlingStrategy)
                .with(time)
                .build();

            val market = marketFactory
                .buildMarketWithProbabilities(SportEvents.any(), oddEvenMarket(), anyProducerId())
                .get();
            val outcome = getEvenOutcomeFrom(market);
            getName(outcome);
            time.tick(CIRCUIT_BREAKER_OPEN_PERIOD.minus(seconds(1)));
            getName(outcome);

            descriptionProvider.verifyDescriptionWasReloadedTimes(0);
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
                .buildMarketWithProbabilities(SportEvents.any(), oddEvenMarket(), anyProducerId())
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
                .buildMarketWithProbabilities(SportEvents.any(), oddEvenMarket(), anyProducerId())
                .get();

            assertThat(market.getMarketDefinition().getNameTemplate(langB))
                .isEqualTo(oddEvenMarketDescription().getName());
        }

        @Test
        public void outcomeHasNameForDefaultLanguage() {
            val aLanguage = Languages.any();
            val marketFactory = stubbingOutSportEventAndCaches()
                .with(providing(in(aLanguage), namesOf(oddEvenMarketDescription(), in(aLanguage))))
                .withDefaultLanguage(aLanguage)
                .build();

            val market = marketFactory
                .buildMarketWithProbabilities(SportEvents.any(), oddEvenMarket(), anyProducerId())
                .get();
            val outcome = getEvenOutcomeFrom(market);

            assertThat(outcome)
                .hasNameInDefaultLanguage(
                    of(SapiMarketDescriptions.OddEven.evenOutcomeDescription().getName(), in(aLanguage))
                );
        }
    }
}
