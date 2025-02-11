/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */
package com.sportradar.unifiedodds.sdk.caching.markets;

import static com.sportradar.unifiedodds.sdk.caching.markets.MarketDescriptionFactory.getOutcomeDescription;
import static com.sportradar.unifiedodds.sdk.caching.markets.VariantMarketDescriptionCaches.stubbingOutDataProvidersAndTime;
import static com.sportradar.unifiedodds.sdk.conn.SapiMarketDescriptions.ExactGoals.exactGoalsMarketDescription;
import static com.sportradar.unifiedodds.sdk.conn.SapiMarketDescriptions.NascarOutrights.nascarOutrightsMarketDescription;
import static com.sportradar.unifiedodds.sdk.conn.SapiMarketDescriptions.NascarOutrights.outcomeDescriptionFor;
import static com.sportradar.unifiedodds.sdk.conn.SapiMarketDescriptions.getOutcomeDescription;
import static com.sportradar.unifiedodds.sdk.conn.marketids.FreeTextMarketIds.FREE_TEXT_MARKET_ID;
import static com.sportradar.unifiedodds.sdk.conn.marketids.FreeTextMarketIds.nascarOutrightsVariant;
import static com.sportradar.unifiedodds.sdk.impl.MarketDescriptionDataProviders.providing;
import static com.sportradar.unifiedodds.sdk.testutil.generic.naturallanguage.Prepositions.from;
import static com.sportradar.unifiedodds.sdk.testutil.generic.naturallanguage.Prepositions.of;
import static com.sportradar.utils.domain.names.LanguageHolder.in;
import static com.sportradar.utils.generic.testing.RandomObjectPicker.pickOneRandomlyFrom;
import static com.sportradar.utils.time.TimeInterval.seconds;
import static java.util.Locale.ENGLISH;
import static java.util.Locale.FRENCH;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.times;

import com.sportradar.uf.sportsapi.datamodel.DescMarket;
import com.sportradar.unifiedodds.sdk.caching.markets.VariantMarketDescriptionCacheUsingOutcomesTest.TestParameterSources.OutcomeNameRemover;
import com.sportradar.unifiedodds.sdk.internal.caching.Languages;
import com.sportradar.unifiedodds.sdk.internal.caching.markets.VariantMarketDescriptionCache;
import com.sportradar.unifiedodds.sdk.testutil.generic.concurrent.AtomicActionPerformer;
import com.sportradar.utils.time.TimeInterval;
import com.sportradar.utils.time.TimeUtilsStub;
import java.time.Instant;
import java.util.Locale;
import java.util.stream.Stream;
import lombok.val;
import org.junit.jupiter.api.Named;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;

public class VariantMarketDescriptionCacheUsingOutcomesTest {

    private static final String NULLIFY_OR_EMPTY_OUTCOME_NAME =
        "com.sportradar.unifiedodds.sdk.caching.markets." +
        "VariantMarketDescriptionCacheUsingOutcomesTest$TestParameterSources#nullifyOrEmptyOutcomeName";

    private static final String WITH_MEMORY_CLEANUP_AND_WITHOUT =
        "com.sportradar.unifiedodds.sdk.caching.markets." +
        "VariantMarketDescriptionCacheUsingOutcomesTest$TestParameterSources#withMemoryCleanupAndWithout";

    @SuppressWarnings("ConstantName")
    private static final String NULLIFY_OR_EMPTY_OUTCOME_NAME__WITH_MEMORY_CLEANUP_AND_WITHOUT =
        "com.sportradar.unifiedodds.sdk.caching.markets." +
        "VariantMarketDescriptionCacheUsingOutcomesTest$TestParameterSources" +
        "#nullifyOrEmptyOutcomeNameAndWithMemoryCleanupAndWithout";

    private static final TimeInterval FAULTY_MARKET_TTL = seconds(30);

    private static final TimeInterval JUST_BELOW_FAULTY_MARKET_TTL = FAULTY_MARKET_TTL.minus(seconds(1));

    private static final TimeInterval JUST_OVER_FAULTY_MARKET_TTL = FAULTY_MARKET_TTL.plus(seconds(1));

    private static final long MIDNIGHT_TIMESTAMP_MILLIS = 1664402400000L;
    private final TimeUtilsStub time = TimeUtilsStub
        .threadSafe(new AtomicActionPerformer())
        .withCurrentTime(Instant.ofEpochMilli(MIDNIGHT_TIMESTAMP_MILLIS));

    @ParameterizedTest
    @ValueSource(strings = { "en", "fr" })
    public void translatesOutcomeName(String language) throws Exception {
        val outcomeId = pickOneRandomlyFrom(nascarOutrightsVariant().outcomeIds());

        val aLanguage = new Locale(language);
        val cache = stubbingOutDataProvidersAndTime()
            .with(providing(in(aLanguage), nascarOutrightsVariant(), of(nascarOutrightsMarketDescription())))
            .build();

        val description = cache.getMarketDescriptor(
            FREE_TEXT_MARKET_ID,
            nascarOutrightsVariant().id(),
            new Languages.BestEffort(aLanguage)
        );

        val outcomeDescription = getOutcomeDescription(outcomeId, from(description));
        assertThat(outcomeDescription.getName(aLanguage))
            .isEqualTo(outcomeDescriptionFor(outcomeId).getName());
    }

    @ParameterizedTest
    @MethodSource(WITH_MEMORY_CLEANUP_AND_WITHOUT)
    public void translatesOutcomeNameToTwoDifferentLanguagesOneByOne(
        VariantMarketDescriptionCache.Config memoryCleanupSetting
    ) throws Exception {
        val outcomeId = pickOneRandomlyFrom(nascarOutrightsVariant().outcomeIds());
        val languageA = ENGLISH;
        val languageB = FRENCH;
        val cache = stubbingOutDataProvidersAndTime()
            .with(
                providing(
                    in(languageA),
                    nascarOutrightsVariant(),
                    of(nascarOutrightsMarketDescription(languageA)),
                    in(languageB),
                    nascarOutrightsVariant(),
                    of(nascarOutrightsMarketDescription(languageB))
                )
            )
            .with(time)
            .with(memoryCleanupSetting)
            .build();

        val descriptionA = cache.getMarketDescriptor(
            FREE_TEXT_MARKET_ID,
            nascarOutrightsVariant().id(),
            new Languages.BestEffort(languageA)
        );

        val descriptionB = cache.getMarketDescriptor(
            FREE_TEXT_MARKET_ID,
            nascarOutrightsVariant().id(),
            new Languages.BestEffort(languageB)
        );

        val outcomeDescriptionA = getOutcomeDescription(outcomeId, from(descriptionA));
        assertThat(outcomeDescriptionA.getName(languageA))
            .isEqualTo(outcomeDescriptionFor(outcomeId).getName());
        val outcomeDescriptionB = getOutcomeDescription(outcomeId, from(descriptionB));
        assertThat(outcomeDescriptionB.getName(languageB))
            .isEqualTo(outcomeDescriptionFor(outcomeId).getName());
    }

    @ParameterizedTest
    @MethodSource(NULLIFY_OR_EMPTY_OUTCOME_NAME)
    public void notTranslatesMissingOutcomeNames(OutcomeNameRemover outcomeName) throws Exception {
        val outcomeId = pickOneRandomlyFrom(nascarOutrightsVariant().outcomeIds());
        val aLanguage = ENGLISH;

        DescMarket faultyMarketDescription = outcomeName.remove(
            outcomeId,
            nascarOutrightsMarketDescription(aLanguage)
        );

        val cache = stubbingOutDataProvidersAndTime()
            .with(providing(in(aLanguage), nascarOutrightsVariant(), of(faultyMarketDescription)))
            .build();

        val description = cache.getMarketDescriptor(
            FREE_TEXT_MARKET_ID,
            nascarOutrightsVariant().id(),
            new Languages.BestEffort(aLanguage)
        );

        assertThat(getOutcomeDescription(outcomeId, description).getName(aLanguage)).isEmpty();
    }

    @Test
    public void cachesOutcomeName() throws Exception {
        val outcomeId = pickOneRandomlyFrom(nascarOutrightsVariant().outcomeIds());
        val aLanguage = ENGLISH;
        val dataProvider = providing(
            in(aLanguage),
            nascarOutrightsVariant(),
            of(nascarOutrightsMarketDescription(aLanguage))
        );

        val cache = stubbingOutDataProvidersAndTime().with(dataProvider).build();

        val description1 = cache.getMarketDescriptor(
            FREE_TEXT_MARKET_ID,
            nascarOutrightsVariant().id(),
            new Languages.BestEffort(aLanguage)
        );

        val description2 = cache.getMarketDescriptor(
            FREE_TEXT_MARKET_ID,
            nascarOutrightsVariant().id(),
            new Languages.BestEffort(aLanguage)
        );

        assertThat(getOutcomeDescription(outcomeId, description1).getName(aLanguage))
            .isEqualTo(getOutcomeDescription(outcomeId, description2).getName(aLanguage));
        verify(dataProvider, times(1)).getData(any(Locale.class), any(), any());
    }

    @ParameterizedTest
    @MethodSource(NULLIFY_OR_EMPTY_OUTCOME_NAME)
    public void cachesDescriptionWithMissingOutcomeNameForHalfMinute(OutcomeNameRemover outcomeName)
        throws Exception {
        val outcomeId = pickOneRandomlyFrom(nascarOutrightsVariant().outcomeIds());
        val aLanguage = ENGLISH;
        val faultyMarket = outcomeName.remove(outcomeId, nascarOutrightsMarketDescription(aLanguage));
        val dataProvider = providing(in(aLanguage), nascarOutrightsVariant(), of(faultyMarket));
        val cache = stubbingOutDataProvidersAndTime().with(dataProvider).with(time).build();

        val description1 = cache.getMarketDescriptor(
            FREE_TEXT_MARKET_ID,
            nascarOutrightsVariant().id(),
            new Languages.BestEffort(aLanguage)
        );

        time.tick(JUST_BELOW_FAULTY_MARKET_TTL);

        val description2 = cache.getMarketDescriptor(
            FREE_TEXT_MARKET_ID,
            nascarOutrightsVariant().id(),
            new Languages.BestEffort(aLanguage)
        );

        assertThat(getOutcomeDescription(outcomeId, description1).getName(aLanguage)).isEmpty();
        assertThat(getOutcomeDescription(outcomeId, description2).getName(aLanguage)).isEmpty();
        verify(dataProvider, times(1))
            .getData(aLanguage, FREE_TEXT_MARKET_ID + "", nascarOutrightsVariant().id());
    }

    @ParameterizedTest
    @MethodSource(NULLIFY_OR_EMPTY_OUTCOME_NAME__WITH_MEMORY_CLEANUP_AND_WITHOUT)
    public void evictsDescriptionWithNoOutcomeNameAfterHalfMinute(
        OutcomeNameRemover outcomeName,
        VariantMarketDescriptionCache.Config memoryCleanupSetting
    ) throws Exception {
        val outcomeId = pickOneRandomlyFrom(nascarOutrightsVariant().outcomeIds());
        val aLanguage = ENGLISH;
        val faultyMarket = outcomeName.remove(outcomeId, nascarOutrightsMarketDescription(aLanguage));
        val dataProvider = providing(in(aLanguage), nascarOutrightsVariant(), of(faultyMarket));
        val cache = stubbingOutDataProvidersAndTime()
            .with(dataProvider)
            .with(time)
            .with(memoryCleanupSetting)
            .build();

        val description1 = cache.getMarketDescriptor(
            FREE_TEXT_MARKET_ID,
            nascarOutrightsVariant().id(),
            new Languages.BestEffort(aLanguage)
        );

        time.tick(JUST_OVER_FAULTY_MARKET_TTL);

        val description2 = cache.getMarketDescriptor(
            FREE_TEXT_MARKET_ID,
            nascarOutrightsVariant().id(),
            new Languages.BestEffort(aLanguage)
        );

        assertThat(getOutcomeDescription(outcomeId, description1).getName(aLanguage)).isEmpty();
        assertThat(getOutcomeDescription(outcomeId, description2).getName(aLanguage)).isEmpty();
        verify(dataProvider, times(2))
            .getData(aLanguage, FREE_TEXT_MARKET_ID + "", nascarOutrightsVariant().id());
    }

    @ParameterizedTest
    @MethodSource(NULLIFY_OR_EMPTY_OUTCOME_NAME__WITH_MEMORY_CLEANUP_AND_WITHOUT)
    public void descriptionWithFirstLanguagesHavingShorterAndSecondOneLongerTtls(
        OutcomeNameRemover outcomeName,
        VariantMarketDescriptionCache.Config memoryCleanupSetting
    ) throws Exception {
        val outcomeId = pickOneRandomlyFrom(nascarOutrightsVariant().outcomeIds());
        val langA = ENGLISH;
        val langB = FRENCH;
        DescMarket faultyMarket = outcomeName.remove(outcomeId, nascarOutrightsMarketDescription(langA));
        val dataProvider = providing(
            in(langA),
            nascarOutrightsVariant(),
            of(faultyMarket),
            in(langB),
            nascarOutrightsVariant(),
            of(nascarOutrightsMarketDescription(langB))
        );
        val cache = stubbingOutDataProvidersAndTime()
            .with(dataProvider)
            .with(time)
            .with(memoryCleanupSetting)
            .build();

        cache
            .getMarketDescriptor(
                FREE_TEXT_MARKET_ID,
                nascarOutrightsVariant().id(),
                new Languages.BestEffort(langA, langB)
            )
            .getName(langA);

        time.tick(JUST_OVER_FAULTY_MARKET_TTL);

        cache
            .getMarketDescriptor(
                FREE_TEXT_MARKET_ID,
                nascarOutrightsVariant().id(),
                new Languages.BestEffort(langA, langB)
            )
            .getName(langA);

        verify(dataProvider, times(2))
            .getData(langA, FREE_TEXT_MARKET_ID + "", nascarOutrightsVariant().id());
        verify(dataProvider, times(1))
            .getData(langB, FREE_TEXT_MARKET_ID + "", nascarOutrightsVariant().id());
    }

    @ParameterizedTest
    @MethodSource(NULLIFY_OR_EMPTY_OUTCOME_NAME__WITH_MEMORY_CLEANUP_AND_WITHOUT)
    public void descriptionWithFirstLanguagesHavingLongerAndSecondOneShorterTtls(
        OutcomeNameRemover outcomeName,
        VariantMarketDescriptionCache.Config memoryCleanupSetting
    ) throws Exception {
        val outcomeId = pickOneRandomlyFrom(nascarOutrightsVariant().outcomeIds());
        val langA = ENGLISH;
        val langB = FRENCH;
        DescMarket faultyMarket = outcomeName.remove(outcomeId, nascarOutrightsMarketDescription(langB));
        val dataProvider = providing(
            in(langA),
            nascarOutrightsVariant(),
            of(nascarOutrightsMarketDescription(langA)),
            in(langB),
            nascarOutrightsVariant(),
            of(faultyMarket)
        );
        val cache = stubbingOutDataProvidersAndTime()
            .with(dataProvider)
            .with(time)
            .with(memoryCleanupSetting)
            .build();

        cache
            .getMarketDescriptor(
                FREE_TEXT_MARKET_ID,
                nascarOutrightsVariant().id(),
                new Languages.BestEffort(langA, langB)
            )
            .getName(langA);

        time.tick(JUST_OVER_FAULTY_MARKET_TTL);

        cache
            .getMarketDescriptor(
                FREE_TEXT_MARKET_ID,
                nascarOutrightsVariant().id(),
                new Languages.BestEffort(langA, langB)
            )
            .getName(langA);

        verify(dataProvider, times(1))
            .getData(langA, FREE_TEXT_MARKET_ID + "", nascarOutrightsVariant().id());
        verify(dataProvider, times(2))
            .getData(langB, FREE_TEXT_MARKET_ID + "", nascarOutrightsVariant().id());
    }

    public static class TestParameterSources {

        public static Stream<Arguments> nullifyOrEmptyOutcomeNameAndWithMemoryCleanupAndWithout() {
            val withMemoryCleaningUp = mock(VariantMarketDescriptionCache.Config.class);
            when(withMemoryCleaningUp.getNumberOfVariantsToStartAggressiveCleanUpMemoryFrom()).thenReturn(0);
            val noMemoryCleaningUp = mock(VariantMarketDescriptionCache.Config.class);
            final int highEnoughNumberToNotBeExercisedByTests = 1000;
            when(noMemoryCleaningUp.getNumberOfVariantsToStartAggressiveCleanUpMemoryFrom())
                .thenReturn(highEnoughNumberToNotBeExercisedByTests);
            return Stream.of(
                Arguments.of(
                    Named.of(
                        "nullified market name",
                        (OutcomeNameRemover) TestParameterSources::nullifyOutcomeName
                    ),
                    Named.of("with memory cleanup", withMemoryCleaningUp)
                ),
                Arguments.of(
                    Named.of(
                        "empty market name",
                        (OutcomeNameRemover) TestParameterSources::emptyOutcomeName
                    ),
                    Named.of("with memory cleanup", withMemoryCleaningUp)
                ),
                Arguments.of(
                    Named.of(
                        "nullified market name",
                        (OutcomeNameRemover) TestParameterSources::nullifyOutcomeName
                    ),
                    Named.of("no memory cleanup", noMemoryCleaningUp)
                ),
                Arguments.of(
                    Named.of(
                        "empty market name",
                        (OutcomeNameRemover) TestParameterSources::emptyOutcomeName
                    ),
                    Named.of("no memory cleanup", noMemoryCleaningUp)
                )
            );
        }

        public static Stream<Arguments> withMemoryCleanupAndWithout() {
            val configForMemoryCleaningUp = mock(VariantMarketDescriptionCache.Config.class);
            when(configForMemoryCleaningUp.getNumberOfVariantsToStartAggressiveCleanUpMemoryFrom())
                .thenReturn(0);
            val configForNoMemoryCleaningUp = mock(VariantMarketDescriptionCache.Config.class);
            final int highEnoughNumberNotToBeExercisedByTests = 1000;
            when(configForMemoryCleaningUp.getNumberOfVariantsToStartAggressiveCleanUpMemoryFrom())
                .thenReturn(highEnoughNumberNotToBeExercisedByTests);
            return Stream.of(
                Arguments.of(Named.of("with memory cleanup", configForMemoryCleaningUp)),
                Arguments.of(Named.of("without memory cleanup", configForNoMemoryCleaningUp))
            );
        }

        @SuppressWarnings("unused")
        public static Stream<Arguments> nullifyOrEmptyOutcomeName() {
            return Stream.of(
                Arguments.of(
                    Named.<OutcomeNameRemover>of(
                        "nullified outcome name",
                        TestParameterSources::nullifyOutcomeName
                    )
                ),
                Arguments.of(
                    Named.<OutcomeNameRemover>of("empty outcome name", TestParameterSources::emptyOutcomeName)
                )
            );
        }

        public static DescMarket nullifyOutcomeName(String outcomeId, DescMarket market) {
            getOutcomeDescription(outcomeId, market).setName(null);
            return market;
        }

        public static DescMarket emptyOutcomeName(String outcomeId, DescMarket market) {
            getOutcomeDescription(outcomeId, market).setName("");
            return market;
        }

        public interface OutcomeNameRemover {
            DescMarket remove(String outcomeId, DescMarket market);
        }
    }
}
