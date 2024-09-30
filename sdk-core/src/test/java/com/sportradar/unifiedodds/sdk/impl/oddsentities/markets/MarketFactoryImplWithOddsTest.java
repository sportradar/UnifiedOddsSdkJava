/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */
package com.sportradar.unifiedodds.sdk.impl.oddsentities.markets;

import static com.sportradar.unifiedodds.sdk.caching.markets.MarketDescriptionFactory.namesOf;
import static com.sportradar.unifiedodds.sdk.caching.markets.MarketDescriptorProviders.noMarketDescribingProvider;
import static com.sportradar.unifiedodds.sdk.caching.markets.MarketDescriptorProviders.providing;
import static com.sportradar.unifiedodds.sdk.conn.SapiMarketDescriptions.MapDuration.mapDurationMarketDescription;
import static com.sportradar.unifiedodds.sdk.conn.SapiMarketDescriptions.OddEven.oddEvenMarketDescription;
import static com.sportradar.unifiedodds.sdk.conn.SapiMarketDescriptions.WhenWillTheRunBeScoredExtraInnings.whenWillTheRunBeScoredExtraInningsMarketDescription;
import static com.sportradar.unifiedodds.sdk.conn.UfMarkets.WithOdds.*;
import static com.sportradar.unifiedodds.sdk.conn.UfSpecifiers.UfInningNrSpecifier.inningNr;
import static com.sportradar.unifiedodds.sdk.conn.UfSpecifiers.UfMapNrSpecifier.mapNr;
import static com.sportradar.unifiedodds.sdk.conn.UfSpecifiers.UfMinuteSpecifier.minute;
import static com.sportradar.unifiedodds.sdk.conn.UfSpecifiers.UfRunNrSpecifier.runNr;
import static com.sportradar.unifiedodds.sdk.entities.SportEvents.anyStage;
import static com.sportradar.unifiedodds.sdk.impl.oddsentities.markets.DescMarketFixture.market;
import static com.sportradar.unifiedodds.sdk.impl.oddsentities.markets.ExpectationTowardsSdkErrorHandlingStrategy.WILL_CATCH_EXCEPTIONS;
import static com.sportradar.unifiedodds.sdk.impl.oddsentities.markets.ExpectationTowardsSdkErrorHandlingStrategy.WILL_THROW_EXCEPTIONS;
import static com.sportradar.unifiedodds.sdk.impl.oddsentities.markets.MarketAssert.assertThat;
import static com.sportradar.unifiedodds.sdk.impl.oddsentities.markets.MarketDefinitionAssert.MethodsBackedByMarketDescriptionInScope.ALL_METHODS;
import static com.sportradar.unifiedodds.sdk.impl.oddsentities.markets.MarketDefinitionAssert.MethodsBackedByMarketDescriptionInScope.METHODS_EXCLUDING_VALID_MAPPINGS;
import static com.sportradar.unifiedodds.sdk.impl.oddsentities.markets.MarketDefinitionAssert.assertThat;
import static com.sportradar.unifiedodds.sdk.impl.oddsentities.markets.MarketFactories.BuilderStubbingOutSportEventAndCaches.stubbingOutSportEventAndCaches;
import static com.sportradar.unifiedodds.sdk.impl.oddsentities.markets.Ordinals.ordinal;
import static com.sportradar.unifiedodds.sdk.impl.oddsentities.markets.OutcomeOddsAssert.assertThat;
import static com.sportradar.unifiedodds.sdk.testutil.generic.naturallanguage.Prepositions.with;
import static com.sportradar.utils.domain.names.LanguageHolder.in;
import static com.sportradar.utils.domain.names.TranslationHolder.of;
import static com.sportradar.utils.domain.names.TranslationHolder.with;
import static com.sportradar.utils.domain.producers.ProducerIds.PREMIUM_CRICKET_PRODUCER_ID;
import static com.sportradar.utils.domain.producers.ProducerIds.anyProducerId;
import static java.util.Locale.ENGLISH;
import static java.util.Locale.FRENCH;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;

import com.sportradar.uf.datamodel.UfOddsChangeMarket;
import com.sportradar.uf.sportsapi.datamodel.DescMarket;
import com.sportradar.uf.sportsapi.datamodel.DescOutcomes;
import com.sportradar.unifiedodds.sdk.ExceptionHandlingStrategy;
import com.sportradar.unifiedodds.sdk.entities.SportEvents;
import com.sportradar.unifiedodds.sdk.exceptions.NameGenerationException;
import com.sportradar.unifiedodds.sdk.oddsentities.MarketWithOdds;
import com.sportradar.utils.domain.UniqueObjects;
import com.sportradar.utils.domain.names.Languages;
import com.sportradar.utils.domain.producers.ProducerIds;
import java.util.Arrays;
import java.util.Locale;
import java.util.function.Function;
import java.util.stream.Stream;
import lombok.val;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

@SuppressWarnings("MagicNumber")
public class MarketFactoryImplWithOddsTest {

    public static final String EXCEPTION_HANDLING_STRATEGIES =
        "com.sportradar.unifiedodds.sdk.impl.oddsentities.markets.MarketFactoryImplWithOddsTest" +
        "#exceptionHandlingStrategies";
    public static final String NON_PREMIUM_CRICKET_PRODUCER_IDS_AND_EXCEPTION_HANDLING_STRATEGIES =
        "com.sportradar.unifiedodds.sdk.impl.oddsentities.markets.MarketFactoryImplWithOddsTest" +
        "#nonPremiumCricketProducerIdsAndExceptionHandlingStrategies";
    public static final String ORDINAL_EXPRESSIONS =
        "com.sportradar.unifiedodds.sdk.impl.oddsentities.markets.Parameters#ordinals";
    public static final String CALCULATED_MAPNR_ORDINAL_EXPRESSIONS =
        "com.sportradar.unifiedodds.sdk.impl.oddsentities.markets.Parameters#calculatedMapnrOrdinals";
    public static final String CALCULATED_MAPNR_ARITHMETIC_EXPRESSIONS =
        "com.sportradar.unifiedodds.sdk.impl.oddsentities.markets.Parameters#calculatedMapnrArithmeticExpressions";
    public static final String CALCULATED_INNINGNR_ORDINAL_EXPRESSIONS =
        "com.sportradar.unifiedodds.sdk.impl.oddsentities.markets.Parameters#calculatedInningnrOrdinals";
    public static final String CALCULATED_INNINGNR_ARITHMETIC_EXPRESSIONS =
        "com.sportradar.unifiedodds.sdk.impl.oddsentities.markets.Parameters#calculatedInningnrArithmeticExpressions";

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

        @Nested
        class WithExpressionInMarketName {

            @ParameterizedTest
            @MethodSource(ORDINAL_EXPRESSIONS)
            void marketHasNameWithCalculatedOrdinalExpression(int mapNr, Locale lang) {
                val factory = stubbingOutSportEventAndCaches()
                    .with(providing(in(lang), namesOf(mapDurationMarketDescription(lang), in(lang))))
                    .withDefaultLanguage(lang)
                    .build();

                val market = factory
                    .buildMarketWithOdds(
                        SportEvents.any(),
                        mapDurationMarket(mapNr(mapNr), minute(11)),
                        anyProducerId()
                    )
                    .get();

                String ordinalMapNr = replaceMapNrInName(
                    mapDurationMarketDescription(lang),
                    with(ordinal(mapNr, in(lang)))
                )
                    .getName();
                assertThat(market).hasName(with(ordinalMapNr, in(lang)));
            }

            @ParameterizedTest
            @MethodSource(CALCULATED_MAPNR_ORDINAL_EXPRESSIONS)
            void marketHasNameWithCalculatedOrdinalExpression(
                String mapNrExpression,
                int mapNr,
                int expectedOrdinal,
                Locale lang
            ) {
                val factory = stubbingOutSportEventAndCaches()
                    .with(
                        providing(
                            in(lang),
                            namesOf(
                                replaceMapNrInName(mapDurationMarketDescription(lang), mapNrExpression),
                                in(lang)
                            )
                        )
                    )
                    .withDefaultLanguage(lang)
                    .build();

                val market = factory
                    .buildMarketWithOdds(
                        SportEvents.any(),
                        mapDurationMarket(mapNr(mapNr), minute(11)),
                        anyProducerId()
                    )
                    .get();

                String ordinalMapNr = replaceMapNrInName(
                    mapDurationMarketDescription(lang),
                    with(ordinal(expectedOrdinal, in(lang)))
                )
                    .getName();
                assertThat(market).hasName(with(ordinalMapNr, in(lang)));
            }

            @ParameterizedTest
            @MethodSource(CALCULATED_MAPNR_ARITHMETIC_EXPRESSIONS)
            void marketHasNameWithArithmeticExpression(
                String mapNrExpression,
                int mapNr,
                String expectedValue
            ) {
                val lang = ENGLISH;
                val factory = stubbingOutSportEventAndCaches()
                    .with(
                        providing(
                            in(lang),
                            namesOf(
                                replaceMapNrInName(mapDurationMarketDescription(lang), mapNrExpression),
                                in(lang)
                            )
                        )
                    )
                    .withDefaultLanguage(lang)
                    .build();

                val market = factory
                    .buildMarketWithOdds(
                        SportEvents.any(),
                        mapDurationMarket(mapNr(mapNr), minute(11)),
                        anyProducerId()
                    )
                    .get();

                String ordinalMapNr = replaceMapNrInName(
                    mapDurationMarketDescription(lang),
                    with(expectedValue)
                )
                    .getName();
                assertThat(market).hasName(with(ordinalMapNr, in(lang)));
            }

            private DescMarket replaceMapNrInName(DescMarket descMarket, String replacement) {
                descMarket.setName(descMarket.getName().replaceAll("\\{!mapnr}", replacement));
                return descMarket;
            }
        }

        @Nested
        class WithExpressionInOutcomeName {

            @Test
            void marketOutcomeNameArithmeticExpressionDoesNotSupportDoubleTypeForSpecifiers() {
                val lang = ENGLISH;
                val factory = stubbingOutSportEventAndCaches()
                    .with(providing(in(lang), namesOf(mapDurationMarketDescription(lang), in(lang))))
                    .withDefaultLanguage(lang)
                    .build();
                val market = factory
                    .buildMarketWithOdds(
                        SportEvents.any(),
                        replaceMinuteWithDouble(mapDurationMarket(mapNr(2), minute(3))),
                        anyProducerId()
                    )
                    .get();

                val firstOutcome = market.getOutcomeOdds().get(0);
                assertThatExceptionOfType(UnsupportedOperationException.class)
                    .isThrownBy(() -> firstOutcome.getName(lang));
            }

            private UfOddsChangeMarket replaceMinuteWithDouble(UfOddsChangeMarket ufOddsChangeMarket) {
                val newSpecifiers = ufOddsChangeMarket
                    .getSpecifiers()
                    .replaceAll("minute=\\d+", "minute=2.1");
                ufOddsChangeMarket.setSpecifiers(newSpecifiers);
                return ufOddsChangeMarket;
            }

            @ParameterizedTest
            @MethodSource(CALCULATED_INNINGNR_ORDINAL_EXPRESSIONS)
            void marketHasOutcomeNameWithCalculatedOrdinalExpression(
                String expression,
                int inningnr,
                int expectedOrdinal,
                Locale lang
            ) {
                val marketDescription = market(whenWillTheRunBeScoredExtraInningsMarketDescription(lang))
                    .firstOutcome(nameInninngnrReplacedWith(expression));

                val factory = stubbingOutSportEventAndCaches()
                    .with(providing(in(lang), namesOf(marketDescription, in(lang))))
                    .withDefaultLanguage(lang)
                    .build();

                val market = factory
                    .buildMarketWithOdds(
                        SportEvents.any(),
                        whenWillTheRunBeScoredExtraInningsMarket(inningNr(inningnr), runNr(3)),
                        anyProducerId()
                    )
                    .get();

                String ordinalName = replaceExpression(
                    expression,
                    marketDescription.getFirstOutcome().getName(),
                    with(ordinal(expectedOrdinal, in(lang)))
                );
                OutcomeAssert
                    .assertThat(market.getOutcomeOdds().get(0))
                    .hasNameInDefaultLanguage(with(ordinalName, in(lang)));
            }

            @ParameterizedTest
            @MethodSource(CALCULATED_INNINGNR_ARITHMETIC_EXPRESSIONS)
            void marketHasOutcomeNameWithCalculatedArithmeticExpression(
                String expression,
                int inningnr,
                String expected
            ) {
                val lang = ENGLISH;
                val marketDescription = market(whenWillTheRunBeScoredExtraInningsMarketDescription(lang))
                    .firstOutcome(nameInninngnrReplacedWith(expression));

                val factory = stubbingOutSportEventAndCaches()
                    .with(providing(in(lang), namesOf(marketDescription, in(lang))))
                    .withDefaultLanguage(lang)
                    .build();

                val market = factory
                    .buildMarketWithOdds(
                        SportEvents.any(),
                        whenWillTheRunBeScoredExtraInningsMarket(inningNr(inningnr), runNr(3)),
                        anyProducerId()
                    )
                    .get();

                String ordinalName = replaceExpression(
                    expression,
                    marketDescription.getFirstOutcome().getName(),
                    with(expected)
                );
                OutcomeAssert
                    .assertThat(market.getOutcomeOdds().get(0))
                    .hasNameInDefaultLanguage(with(ordinalName, in(lang)));
            }

            private String replaceExpression(String expression, String name, String ordinal) {
                return name.replace(expression, ordinal);
            }

            private Function<DescOutcomes.Outcome, DescOutcomes.Outcome> nameInninngnrReplacedWith(
                String replacement
            ) {
                return o -> {
                    o.setName(o.getName().replaceAll("\\{!inningnr}", replacement));
                    return o;
                };
            }
        }
    }
}

@SuppressWarnings("MagicNumber")
class Parameters {

    static Stream<Arguments> ordinals() {
        return Stream.of(
            Arguments.of(specifierValue(1), ENGLISH),
            Arguments.of(specifierValue(2), ENGLISH),
            Arguments.of(specifierValue(3), ENGLISH),
            Arguments.of(specifierValue(4), ENGLISH),
            Arguments.of(specifierValue(1), FRENCH),
            Arguments.of(specifierValue(2), FRENCH),
            Arguments.of(specifierValue(3), FRENCH),
            Arguments.of(specifierValue(4), FRENCH)
        );
    }

    static Stream<Arguments> calculatedMapnrOrdinals() {
        return Stream.of(
            Arguments.of("{!(mapnr-1)}", specifierValue(2), expected(1), ENGLISH),
            Arguments.of("{!(mapnr-0)}", specifierValue(2), expected(2), ENGLISH),
            Arguments.of("{!(mapnr+1)}", specifierValue(2), expected(3), ENGLISH),
            Arguments.of("{!(mapnr+0)}", specifierValue(4), expected(4), ENGLISH),
            Arguments.of("{!(mapnr-3)}", specifierValue(4), expected(1), FRENCH),
            Arguments.of("{!(mapnr-11)}", specifierValue(13), expected(2), FRENCH)
        );
    }

    static Stream<Arguments> calculatedInningnrOrdinals() {
        return Stream.of(
            Arguments.of("{!(inningnr-1)}", specifierValue(2), expected(1), ENGLISH),
            Arguments.of("{!(inningnr-0)}", specifierValue(2), expected(2), ENGLISH),
            Arguments.of("{!(inningnr+1)}", specifierValue(2), expected(3), ENGLISH),
            Arguments.of("{!(inningnr+0)}", specifierValue(4), expected(4), ENGLISH),
            Arguments.of("{!(inningnr-3)}", specifierValue(4), expected(1), FRENCH),
            Arguments.of("{!(inningnr-11)}", specifierValue(13), expected(2), FRENCH)
        );
    }

    static Stream<Arguments> calculatedMapnrArithmeticExpressions() {
        return Stream.of(
            Arguments.of("{(mapnr+1)}", specifierValue(2), expected("3")),
            Arguments.of("{(mapnr+0)}", specifierValue(2), expected("2")),
            Arguments.of("{(mapnr+3)}", specifierValue(2), expected("5")),
            Arguments.of("{(mapnr+11)}", specifierValue(4), expected("15")),
            Arguments.of("{(mapnr-1)}", specifierValue(0), expected("-1")),
            Arguments.of("{(mapnr-2)}", specifierValue(2), expected("0")),
            Arguments.of("{(mapnr-11)}", specifierValue(12), expected("1"))
        );
    }

    static Stream<Arguments> calculatedInningnrArithmeticExpressions() {
        return Stream.of(
            Arguments.of("{(inningnr-3)}", specifierValue(2), expected("-1")),
            Arguments.of("{(inningnr-0)}", specifierValue(2), expected("2")),
            Arguments.of("{(inningnr+4)}", specifierValue(-5), expected("-1")),
            Arguments.of("{(inningnr+0)}", specifierValue(4), expected("4")),
            Arguments.of("{(inningnr-33)}", specifierValue(33), expected("0")),
            Arguments.of("{(inningnr-4)}", specifierValue(8), expected("4"))
        );
    }

    static <T> T specifierValue(T specifier) {
        return specifier;
    }

    static <T> T expected(T value) {
        return value;
    }
}
