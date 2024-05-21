/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */
package com.sportradar.unifiedodds.sdk.caching.markets;

import static com.sportradar.unifiedodds.sdk.caching.markets.DataProviderAnswers.withGetDataThrowingByDefault;
import static com.sportradar.unifiedodds.sdk.caching.markets.VariantMarketDescriptionCaches.stubbingOutDataProvidersAndTime;
import static com.sportradar.unifiedodds.sdk.caching.markets.VariantMarketSources.nullifyMarketName;
import static com.sportradar.unifiedodds.sdk.conn.SapiMarketDescriptions.ExactGoals.exactGoalsMarketDescription;
import static com.sportradar.unifiedodds.sdk.conn.SapiMarketDescriptions.NascarOutrights.*;
import static com.sportradar.unifiedodds.sdk.conn.SapiMarketDescriptions.NflAfcConferenceOutrights.nflAfcConferenceOutrightsMarketDescription;
import static com.sportradar.unifiedodds.sdk.conn.SapiMarketDescriptions.NflAfcConferenceOutrights.openMarket;
import static com.sportradar.unifiedodds.sdk.conn.SapiMarketDescriptions.notFoundWithEmptyMarket;
import static com.sportradar.unifiedodds.sdk.conn.marketids.ChampionshipFreeTextMarketIds.*;
import static com.sportradar.unifiedodds.sdk.conn.marketids.ExactGoalsMarketIds.EXACT_GOALS_MARKET_ID;
import static com.sportradar.unifiedodds.sdk.conn.marketids.ExactGoalsMarketIds.fivePlusVariant;
import static com.sportradar.unifiedodds.sdk.conn.marketids.FreeTextMarketIds.*;
import static com.sportradar.unifiedodds.sdk.conn.marketids.FreeTextMarketIds.nascarOutrightsVariant;
import static com.sportradar.unifiedodds.sdk.impl.MarketDescriptionDataProviders.providing;
import static com.sportradar.unifiedodds.sdk.impl.MarketDescriptionDataProviders.providingList;
import static com.sportradar.unifiedodds.sdk.testutil.generic.naturallanguage.Conjunctions.and;
import static com.sportradar.unifiedodds.sdk.testutil.generic.naturallanguage.Determiners.every;
import static com.sportradar.unifiedodds.sdk.testutil.generic.naturallanguage.Prepositions.of;
import static com.sportradar.utils.domain.names.LanguageHolder.in;
import static com.sportradar.utils.time.TimeInterval.seconds;
import static java.util.Locale.ENGLISH;
import static java.util.Locale.FRENCH;
import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.google.common.collect.ImmutableSet;
import com.sportradar.uf.sportsapi.datamodel.DescMarket;
import com.sportradar.uf.sportsapi.datamodel.DescOutcomes;
import com.sportradar.uf.sportsapi.datamodel.MarketDescriptions;
import com.sportradar.unifiedodds.sdk.caching.ci.markets.MarketDescriptionCi;
import com.sportradar.unifiedodds.sdk.caching.markets.VariantMarketDescriptionCache.Config;
import com.sportradar.unifiedodds.sdk.caching.markets.VariantMarketSources.AttributeRemover;
import com.sportradar.unifiedodds.sdk.conn.MarketVariant;
import com.sportradar.unifiedodds.sdk.domain.language.Languages;
import com.sportradar.unifiedodds.sdk.entities.markets.MarketDescription;
import com.sportradar.unifiedodds.sdk.exceptions.internal.CacheItemNotFoundException;
import com.sportradar.unifiedodds.sdk.impl.DataProvider;
import com.sportradar.unifiedodds.sdk.impl.TimeUtils;
import com.sportradar.unifiedodds.sdk.impl.markets.MappingValidatorFactory;
import com.sportradar.unifiedodds.sdk.testutil.generic.concurrent.AtomicActionPerformer;
import com.sportradar.unifiedodds.sdk.testutil.generic.concurrent.VoidCallables;
import com.sportradar.utils.time.TimeInterval;
import com.sportradar.utils.time.TimeUtilsStub;
import java.time.Instant;
import java.util.Locale;
import java.util.Set;
import java.util.concurrent.*;
import java.util.function.Function;
import java.util.stream.Stream;
import lombok.SneakyThrows;
import lombok.val;
import org.junit.jupiter.api.Named;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;

class VariantMarketDescriptionCacheTest {

    private static final int SECONDS_IN_HOUR = 60 * 60;

    private static final String NULLIFY_OR_EMPTY_MARKET_NAME =
        "com.sportradar.unifiedodds.sdk.caching.markets.VariantMarketSources#nullifyOrEmptyMarketName";

    private static final String NULLIFY_OR_EMPTY_OUTCOMES =
        "com.sportradar.unifiedodds.sdk.caching.markets.VariantMarketSources#nullifyOrEmptyOutcomes";

    private static final String WITH_MEMORY_CLEANUP_AND_WITHOUT =
        "com.sportradar.unifiedodds.sdk.caching.markets.VariantMarketSources#withMemoryCleanupAndWithout";

    @SuppressWarnings("ConstantName")
    private static final String NULLIFY_OR_EMPTY_MARKET_NAME__WITH_MEMORY_CLEANUP_AND_WITHOUT =
        "com.sportradar.unifiedodds.sdk.caching.markets.VariantMarketSources" +
        "#nullifyOrEmptyMarketNameAndWithMemoryCleanupAndWithout";

    private static final String LANGUAGES =
        "com.sportradar.unifiedodds.sdk.caching.markets.LanguageSources#languages";

    private static final TimeInterval FAULTY_MARKET_TTL = seconds(30);

    private static final TimeInterval LESS_THAN_FAULTY_MARKET_TTL = FAULTY_MARKET_TTL.minus(seconds(1));

    private static final TimeInterval MORE_THAN_FAULTY_MARKET_TTL = FAULTY_MARKET_TTL.plus(seconds(1));

    private static final long MIDNIGHT_TIMESTAMP_MILLIS = 1664402400000L;
    private static final int MILLIS_IN_SECOND = 1000;

    private final TimeUtilsStub time = TimeUtilsStub
        .threadSafe(new AtomicActionPerformer())
        .withCurrentTime(Instant.ofEpochMilli(MIDNIGHT_TIMESTAMP_MILLIS));

    @Test
    public void rejectsCreationWithNullArguments() {
        Cache<String, MarketDescriptionCi> c = CacheBuilder.newBuilder().build();
        DataProvider<MarketDescriptions> p = mock(DataProvider.class);
        MappingValidatorFactory v = mock(MappingValidatorFactory.class);
        TimeUtils t = mock(TimeUtils.class);
        Config f = mock(Config.class);

        assertThatThrownBy(() -> new VariantMarketDescriptionCache(null, p, v, t, f))
            .isInstanceOf(NullPointerException.class);
        assertThatThrownBy(() -> new VariantMarketDescriptionCache(c, null, v, t, f))
            .isInstanceOf(NullPointerException.class);
        assertThatThrownBy(() -> new VariantMarketDescriptionCache(c, p, null, t, f))
            .isInstanceOf(NullPointerException.class);
        assertThatThrownBy(() -> new VariantMarketDescriptionCache(c, p, v, null, f))
            .isInstanceOf(NullPointerException.class);
        assertThatThrownBy(() -> new VariantMarketDescriptionCache(c, p, v, t, null))
            .isInstanceOf(NullPointerException.class);
    }

    @Nested
    @SuppressWarnings({ "ClassFanOutComplexity", "MagicNumber", "LambdaBodylength" })
    class GetMarketDescriptor {

        @ParameterizedTest
        @ValueSource(strings = { "en", "fr" })
        public void translatesName(String language) throws Exception {
            val aLanguage = new Locale(language);
            val cache = stubbingOutDataProvidersAndTime()
                .with(
                    providing(in(aLanguage), nascarOutrightsVariant(), of(nascarOutrightsMarketDescription()))
                )
                .build();

            val description = cache.getMarketDescriptor(
                FREE_TEXT_MARKET_ID,
                nascarOutrightsVariant().id(),
                new Languages.BestEffort(aLanguage)
            );

            assertThat(description.getName(aLanguage))
                .isEqualTo(nascarOutrightsMarketDescription().getName());
        }

        @ParameterizedTest
        @MethodSource(WITH_MEMORY_CLEANUP_AND_WITHOUT)
        public void translatesNamesToTwoDifferentLanguagesOneByOne(Config memoryCleanupSetting)
            throws Exception {
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

            assertThat(descriptionA.getName(languageA))
                .isEqualTo(nascarOutrightsMarketDescription(languageA).getName());
            assertThat(descriptionB.getName(languageB))
                .isEqualTo(nascarOutrightsMarketDescription(languageB).getName());
        }

        @ParameterizedTest
        @MethodSource(WITH_MEMORY_CLEANUP_AND_WITHOUT)
        @SuppressWarnings({ "MagicNumber" })
        public void assertsCorrectCacheBehaviourOverLongerPeriodOfTimeForFaultyAndNonFaultyMarkets(
            Config memoryCleanupSetting
        ) throws Exception {
            val aLanguage = ENGLISH;
            DescMarket faultyOutrightMarket = nullifyMarketName(
                nflAfcConferenceOutrightsMarketDescription(aLanguage)
            );
            DescMarket outrightOpenMarket = openMarket(nflAfcConferenceOutrightsMarketDescription(aLanguage));
            val dataProvider = providing(
                in(aLanguage),
                nflAfcConferenceOutrightsVariant(),
                of(faultyOutrightMarket),
                in(aLanguage),
                nflAfcConferenceOutrightsVariant(),
                of(outrightOpenMarket)
            );
            val cache = stubbingOutDataProvidersAndTime()
                .with(time)
                .with(dataProvider)
                .with(memoryCleanupSetting)
                .build();

            final int wellWithinFaultyMarketTtl = 10;
            final int wellOutsideFaultyMarketTtl = 100;
            repeatFor1Hour(
                doing(
                    () ->
                        cache.getMarketDescriptor(
                            CHAMPIONSHIP_FREE_TEXT_MARKET_ID,
                            nflAfcConferenceOutrightsVariant().id(),
                            new Languages.BestEffort(aLanguage)
                        ),
                    every(seconds(wellWithinFaultyMarketTtl))
                ),
                and(
                    doing(
                        () ->
                            cache.getMarketDescriptor(
                                CHAMPIONSHIP_FREE_TEXT_OPEN_MARKET_ID,
                                nflAfcConferenceOutrightsVariant().id(),
                                new Languages.BestEffort(aLanguage)
                            ),
                        every(seconds(wellOutsideFaultyMarketTtl))
                    )
                )
            );

            verify(dataProvider, times(SECONDS_IN_HOUR / wellWithinFaultyMarketTtl / 4))
                .getData(
                    aLanguage,
                    CHAMPIONSHIP_FREE_TEXT_MARKET_ID + "",
                    nflAfcConferenceOutrightsVariant().id()
                );
            verify(dataProvider, times(1))
                .getData(
                    aLanguage,
                    CHAMPIONSHIP_FREE_TEXT_OPEN_MARKET_ID + "",
                    nflAfcConferenceOutrightsVariant().id()
                );
        }

        private void repeatFor1Hour(TimeAwareScheduledRunnable... scheduled) throws Exception {
            for (int i = 0; i < SECONDS_IN_HOUR; i++) {
                for (TimeAwareScheduledRunnable runnable : scheduled) {
                    runnable.runIfScheduled();
                }
                time.tick(seconds(1));
            }
        }

        @ParameterizedTest
        @MethodSource(NULLIFY_OR_EMPTY_MARKET_NAME)
        public void notTranslatesMissingNames(AttributeRemover marketName) throws Exception {
            val aLanguage = ENGLISH;

            DescMarket marketDescription = marketName.remove(exactGoalsMarketDescription(aLanguage));

            val cache = stubbingOutDataProvidersAndTime()
                .with(providing(in(aLanguage), fivePlusVariant(), of(marketDescription)))
                .build();

            val description = cache.getMarketDescriptor(
                EXACT_GOALS_MARKET_ID,
                fivePlusVariant().id(),
                new Languages.BestEffort(aLanguage)
            );

            assertThat(description.getName(aLanguage)).isNull();
        }

        @Test
        public void cachesName() throws Exception {
            val aLanguage = ENGLISH;
            val dataProvider = providing(
                in(aLanguage),
                nascarOutrightsVariant(),
                of(nascarOutrightsOddEvenMarketDescription())
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

            assertThat(description1.getName(aLanguage)).isEqualTo(description2.getName(aLanguage));
            verify(dataProvider, times(1)).getData(any(Locale.class), any(), any());
        }

        @ParameterizedTest
        @MethodSource(NULLIFY_OR_EMPTY_OUTCOMES)
        public void cachesNameForMarketWithNullOutcomes(AttributeRemover outcomes) throws Exception {
            val aLanguage = ENGLISH;
            val marketWithoutOutcomes = outcomes.remove(nascarOutrightsOddEvenMarketDescription());
            val dataProvider = providing(in(aLanguage), nascarOutrightsVariant(), of(marketWithoutOutcomes));

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

            assertThat(description1.getName(aLanguage)).isEqualTo(description2.getName(aLanguage));
            verify(dataProvider, times(1)).getData(any(Locale.class), any(), any());
        }

        @ParameterizedTest
        @MethodSource(NULLIFY_OR_EMPTY_MARKET_NAME)
        public void cachesDescriptionWithMissingNameForHalfMinute(AttributeRemover marketName)
            throws Exception {
            val aLanguage = ENGLISH;
            val dataProvider = providing(
                in(aLanguage),
                fivePlusVariant(),
                of(marketName.remove(exactGoalsMarketDescription(aLanguage)))
            );
            val cache = stubbingOutDataProvidersAndTime().with(dataProvider).with(time).build();

            val description1 = cache.getMarketDescriptor(
                EXACT_GOALS_MARKET_ID,
                fivePlusVariant().id(),
                new Languages.BestEffort(aLanguage)
            );

            time.tick(LESS_THAN_FAULTY_MARKET_TTL);

            val description2 = cache.getMarketDescriptor(
                EXACT_GOALS_MARKET_ID,
                fivePlusVariant().id(),
                new Languages.BestEffort(aLanguage)
            );

            assertThat(description1.getName(aLanguage)).isNull();
            assertThat(description2.getName(aLanguage)).isNull();
            verify(dataProvider, times(1))
                .getData(aLanguage, EXACT_GOALS_MARKET_ID + "", fivePlusVariant().id());
        }

        @ParameterizedTest
        @MethodSource(NULLIFY_OR_EMPTY_MARKET_NAME__WITH_MEMORY_CLEANUP_AND_WITHOUT)
        public void evictsDescriptionWithNoNameAfterHalfMinute(
            AttributeRemover marketName,
            Config memoryCleanupSetting
        ) throws Exception {
            val aLanguage = ENGLISH;
            val dataProvider = providing(
                in(aLanguage),
                fivePlusVariant(),
                of(marketName.remove(exactGoalsMarketDescription(aLanguage)))
            );
            val cache = stubbingOutDataProvidersAndTime()
                .with(dataProvider)
                .with(time)
                .with(memoryCleanupSetting)
                .build();

            val description1 = cache.getMarketDescriptor(
                EXACT_GOALS_MARKET_ID,
                fivePlusVariant().id(),
                new Languages.BestEffort(aLanguage)
            );

            time.tick(MORE_THAN_FAULTY_MARKET_TTL);

            val description2 = cache.getMarketDescriptor(
                EXACT_GOALS_MARKET_ID,
                fivePlusVariant().id(),
                new Languages.BestEffort(aLanguage)
            );

            assertThat(description1.getName(aLanguage)).isNull();
            assertThat(description2.getName(aLanguage)).isNull();
            verify(dataProvider, times(2))
                .getData(aLanguage, EXACT_GOALS_MARKET_ID + "", fivePlusVariant().id());
        }

        @ParameterizedTest
        @MethodSource(WITH_MEMORY_CLEANUP_AND_WITHOUT)
        public void descriptionsWithNameAreCachedForLongPeriodOfTime(Config memoryCleanupSetting)
            throws Exception {
            val aLanguage = ENGLISH;
            val dataProvider = providing(
                in(aLanguage),
                nascarOutrightsVariant(),
                of(nascarOutrightsMarketDescription())
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
                    new Languages.BestEffort(aLanguage)
                )
                .getName(aLanguage);

            time.tick(MORE_THAN_FAULTY_MARKET_TTL);

            cache
                .getMarketDescriptor(
                    FREE_TEXT_MARKET_ID,
                    nascarOutrightsVariant().id(),
                    new Languages.BestEffort(aLanguage)
                )
                .getName(aLanguage);

            verify(dataProvider, times(1))
                .getData(aLanguage, FREE_TEXT_MARKET_ID + "", nascarOutrightsVariant().id());
        }

        @ParameterizedTest
        @MethodSource(NULLIFY_OR_EMPTY_OUTCOMES)
        public void marketDescriptionWithoutOutcomesAtAllAreNotConsideredFaulty(AttributeRemover outcomes)
            throws Exception {
            val aLanguage = ENGLISH;
            val dataProvider = providing(
                in(aLanguage),
                nascarOutrightsVariant(),
                of(outcomes.remove(nascarOutrightsMarketDescription(aLanguage)))
            );
            val cache = stubbingOutDataProvidersAndTime().with(dataProvider).with(time).build();

            cache.getMarketDescriptor(
                FREE_TEXT_MARKET_ID,
                nascarOutrightsVariant().id(),
                new Languages.BestEffort(aLanguage)
            );

            time.tick(MORE_THAN_FAULTY_MARKET_TTL);

            cache.getMarketDescriptor(
                FREE_TEXT_MARKET_ID,
                nascarOutrightsVariant().id(),
                new Languages.BestEffort(aLanguage)
            );

            verify(dataProvider, times(1))
                .getData(aLanguage, FREE_TEXT_MARKET_ID + "", nascarOutrightsVariant().id());
        }

        @ParameterizedTest
        @MethodSource(NULLIFY_OR_EMPTY_MARKET_NAME__WITH_MEMORY_CLEANUP_AND_WITHOUT)
        public void descriptionWithFirstLanguagesHavingShorterAndSecondOneLongerTtls(
            AttributeRemover marketName,
            Config memoryCleanupSetting
        ) throws Exception {
            val langA = ENGLISH;
            val langB = FRENCH;
            DescMarket faultyMarket = marketName.remove(nascarOutrightsMarketDescription(langA));
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

            time.tick(MORE_THAN_FAULTY_MARKET_TTL);

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
        @MethodSource(NULLIFY_OR_EMPTY_MARKET_NAME__WITH_MEMORY_CLEANUP_AND_WITHOUT)
        public void descriptionWithFirstLanguagesHavingLongerAndSecondOneShorterTtls(
            AttributeRemover marketName,
            Config memoryCleanupSetting
        ) throws Exception {
            val langA = ENGLISH;
            val langB = FRENCH;
            DescMarket faultyMarket = marketName.remove(nascarOutrightsMarketDescription(langB));
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

            time.tick(MORE_THAN_FAULTY_MARKET_TTL);

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

        @ParameterizedTest
        @MethodSource(WITH_MEMORY_CLEANUP_AND_WITHOUT)
        public void faultyMarketTtlDoesNotImpedeSecondLanguageToBeFetchedForNonFaultyMarket(
            Config memoryCleanupSetting
        ) throws Exception {
            val langA = ENGLISH;
            val langB = FRENCH;
            val dataProvider = providing(
                in(langA),
                nascarOutrightsVariant(),
                of(nascarOutrightsMarketDescription(langA)),
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
                    new Languages.BestEffort(langA)
                )
                .getName(langA);

            time.tick(MORE_THAN_FAULTY_MARKET_TTL);

            cache
                .getMarketDescriptor(
                    FREE_TEXT_MARKET_ID,
                    nascarOutrightsVariant().id(),
                    new Languages.BestEffort(langB)
                )
                .getName(langA);

            verify(dataProvider, times(1))
                .getData(langA, FREE_TEXT_MARKET_ID + "", nascarOutrightsVariant().id());
            verify(dataProvider, times(1))
                .getData(langB, FREE_TEXT_MARKET_ID + "", nascarOutrightsVariant().id());
        }

        @ParameterizedTest
        @MethodSource(NULLIFY_OR_EMPTY_MARKET_NAME__WITH_MEMORY_CLEANUP_AND_WITHOUT)
        public void faultyMarketTtlForDifferentLanguagesFetchedAtDifferentTimesShouldNotBeConflated(
            AttributeRemover marketName,
            Config memoryCleanupSetting
        ) throws Exception {
            val langA = ENGLISH;
            val langB = FRENCH;
            DescMarket faultyMarketLangA = marketName.remove(nascarOutrightsMarketDescription(langA));
            DescMarket faultyMarketLangB = marketName.remove(nascarOutrightsMarketDescription(langB));
            val dataProvider = providing(
                in(langA),
                nascarOutrightsVariant(),
                of(faultyMarketLangA),
                in(langB),
                nascarOutrightsVariant(),
                of(faultyMarketLangB)
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
                    new Languages.BestEffort(langA)
                )
                .getName(langA);

            time.tick(LESS_THAN_FAULTY_MARKET_TTL);

            cache
                .getMarketDescriptor(
                    FREE_TEXT_MARKET_ID,
                    nascarOutrightsVariant().id(),
                    new Languages.BestEffort(langB)
                )
                .getName(langA);

            time.tick(LESS_THAN_FAULTY_MARKET_TTL);

            cache.getMarketDescriptor(
                FREE_TEXT_MARKET_ID,
                nascarOutrightsVariant().id(),
                new Languages.BestEffort(langA)
            );
            cache.getMarketDescriptor(
                FREE_TEXT_MARKET_ID,
                nascarOutrightsVariant().id(),
                new Languages.BestEffort(langB)
            );

            verify(dataProvider, times(2))
                .getData(langA, FREE_TEXT_MARKET_ID + "", nascarOutrightsVariant().id());
            verify(dataProvider, times(1))
                .getData(langB, FREE_TEXT_MARKET_ID + "", nascarOutrightsVariant().id());
        }

        @ParameterizedTest
        @MethodSource(NULLIFY_OR_EMPTY_MARKET_NAME__WITH_MEMORY_CLEANUP_AND_WITHOUT)
        public void faultyMarketTtlForRestOfLanguagesInBatchesAreIgnoredIfAtLeastOneLanguageNeedsReFetching(
            AttributeRemover marketName,
            Config memoryCleanupSetting
        ) throws Exception {
            val langA = ENGLISH;
            val langB = FRENCH;
            DescMarket faultyMarketLangA = marketName.remove(nascarOutrightsMarketDescription(langA));
            DescMarket faultyMarketLangB = marketName.remove(nascarOutrightsMarketDescription(langB));
            val dataProvider = providing(
                in(langA),
                nascarOutrightsVariant(),
                of(faultyMarketLangA),
                in(langB),
                nascarOutrightsVariant(),
                of(faultyMarketLangB)
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
                    new Languages.BestEffort(langA)
                )
                .getName(langA);

            time.tick(LESS_THAN_FAULTY_MARKET_TTL);

            cache
                .getMarketDescriptor(
                    FREE_TEXT_MARKET_ID,
                    nascarOutrightsVariant().id(),
                    new Languages.BestEffort(langB)
                )
                .getName(langA);

            time.tick(LESS_THAN_FAULTY_MARKET_TTL);

            cache.getMarketDescriptor(
                FREE_TEXT_MARKET_ID,
                nascarOutrightsVariant().id(),
                new Languages.BestEffort(langA, langB)
            );

            verify(dataProvider, times(2))
                .getData(langA, FREE_TEXT_MARKET_ID + "", nascarOutrightsVariant().id());
            verify(dataProvider, times(2))
                .getData(langB, FREE_TEXT_MARKET_ID + "", nascarOutrightsVariant().id());
        }

        @ParameterizedTest
        @MethodSource(NULLIFY_OR_EMPTY_MARKET_NAME__WITH_MEMORY_CLEANUP_AND_WITHOUT)
        @SuppressWarnings("MagicNumber")
        public void faultyMarketTtlIsAppliedSinceLastWriteNotLastRead(
            AttributeRemover marketName,
            Config memoryCleanupSetting
        ) throws Exception {
            val aLanguage = ENGLISH;
            DescMarket faultyMarket = marketName.remove(nascarOutrightsMarketDescription(aLanguage));
            val dataProvider = providing(in(aLanguage), nascarOutrightsVariant(), of(faultyMarket));
            val cache = stubbingOutDataProvidersAndTime()
                .with(dataProvider)
                .with(time)
                .with(memoryCleanupSetting)
                .build();

            repeatTimes(
                5,
                () -> {
                    cache.getMarketDescriptor(
                        FREE_TEXT_MARKET_ID,
                        nascarOutrightsVariant().id(),
                        new Languages.BestEffort(aLanguage)
                    );

                    time.tick(LESS_THAN_FAULTY_MARKET_TTL);
                }
            );

            verify(dataProvider, times(3))
                .getData(aLanguage, FREE_TEXT_MARKET_ID + "", nascarOutrightsVariant().id());
        }

        @ParameterizedTest
        @MethodSource(NULLIFY_OR_EMPTY_MARKET_NAME__WITH_MEMORY_CLEANUP_AND_WITHOUT)
        @SuppressWarnings({ "MagicNumber", "LambdaBodyLength" })
        public void faultyMarketTtlForDifferentVariantsInSingleLanguageAreNotConflated(
            AttributeRemover marketName,
            Config memoryCleanupSetting
        ) throws Exception {
            val aLanguage = ENGLISH;
            DescMarket faultyWinnerMarket = marketName.remove(nascarOutrightsMarketDescription(aLanguage));
            DescMarket faultyOddEvenMarket = marketName.remove(
                nascarOutrightsOddEvenMarketDescription(aLanguage)
            );
            val dataProvider = providing(
                in(aLanguage),
                nascarOutrightsVariant(),
                of(faultyWinnerMarket),
                in(aLanguage),
                nascarOutrightsOddEvenVariant(),
                of(faultyOddEvenMarket)
            );
            val cache = stubbingOutDataProvidersAndTime()
                .with(dataProvider)
                .with(time)
                .with(memoryCleanupSetting)
                .build();

            cache.getMarketDescriptor(
                FREE_TEXT_MARKET_ID,
                nascarOutrightsVariant().id(),
                new Languages.BestEffort(aLanguage)
            );

            repeatTimes(
                4,
                () -> {
                    time.tick(LESS_THAN_FAULTY_MARKET_TTL);

                    cache.getMarketDescriptor(
                        FREE_TEXT_MARKET_ID,
                        nascarOutrightsVariant().id(),
                        new Languages.BestEffort(aLanguage)
                    );
                    cache.getMarketDescriptor(
                        FREE_TEXT_MARKET_ID,
                        nascarOutrightsOddEvenVariant().id(),
                        new Languages.BestEffort(aLanguage)
                    );
                }
            );

            verify(dataProvider, times(3))
                .getData(aLanguage, FREE_TEXT_MARKET_ID + "", nascarOutrightsVariant().id());
            verify(dataProvider, times(2))
                .getData(aLanguage, FREE_TEXT_MARKET_ID + "", nascarOutrightsOddEvenVariant().id());
        }

        @ParameterizedTest
        @MethodSource(NULLIFY_OR_EMPTY_MARKET_NAME__WITH_MEMORY_CLEANUP_AND_WITHOUT)
        @SuppressWarnings({ "MagicNumber", "LambdaBodyLength" })
        public void applicationOfFaultyMarketTtlForSameVariantOfDifferentMarketsInSingleLanguageAreNotConflated(
            AttributeRemover marketName,
            Config memoryCleanupSetting
        ) throws Exception {
            val aLanguage = ENGLISH;
            DescMarket faultyOutrightMarket = marketName.remove(
                nflAfcConferenceOutrightsMarketDescription(aLanguage)
            );
            DescMarket faultyOutrightOpenMarket = marketName.remove(
                openMarket(nflAfcConferenceOutrightsMarketDescription(aLanguage))
            );
            val dataProvider = providing(
                in(aLanguage),
                nflAfcConferenceOutrightsVariant(),
                of(faultyOutrightMarket),
                in(aLanguage),
                nflAfcConferenceOutrightsVariant(),
                of(faultyOutrightOpenMarket)
            );
            val cache = stubbingOutDataProvidersAndTime()
                .with(dataProvider)
                .with(time)
                .with(memoryCleanupSetting)
                .build();

            cache.getMarketDescriptor(
                CHAMPIONSHIP_FREE_TEXT_MARKET_ID,
                nflAfcConferenceOutrightsVariant().id(),
                new Languages.BestEffort(aLanguage)
            );

            repeatTimes(
                4,
                () -> {
                    time.tick(LESS_THAN_FAULTY_MARKET_TTL);

                    cache.getMarketDescriptor(
                        CHAMPIONSHIP_FREE_TEXT_MARKET_ID,
                        nflAfcConferenceOutrightsVariant().id(),
                        new Languages.BestEffort(aLanguage)
                    );
                    cache.getMarketDescriptor(
                        CHAMPIONSHIP_FREE_TEXT_OPEN_MARKET_ID,
                        nflAfcConferenceOutrightsVariant().id(),
                        new Languages.BestEffort(aLanguage)
                    );
                }
            );

            verify(dataProvider, times(3))
                .getData(
                    aLanguage,
                    CHAMPIONSHIP_FREE_TEXT_MARKET_ID + "",
                    nflAfcConferenceOutrightsVariant().id()
                );
            verify(dataProvider, times(2))
                .getData(
                    aLanguage,
                    CHAMPIONSHIP_FREE_TEXT_OPEN_MARKET_ID + "",
                    nflAfcConferenceOutrightsVariant().id()
                );
        }

        @ParameterizedTest
        @MethodSource(WITH_MEMORY_CLEANUP_AND_WITHOUT)
        public void translatesNameToMultipleLanguagesAtOnce(Config memoryCleanupSetting) throws Exception {
            val languageA = ENGLISH;
            val languageB = FRENCH;
            val dataProvider = providing(
                in(languageA),
                nascarOutrightsVariant(),
                of(nascarOutrightsMarketDescription(languageA)),
                in(languageB),
                nascarOutrightsVariant(),
                of(nascarOutrightsMarketDescription(languageB))
            );

            val cache = stubbingOutDataProvidersAndTime()
                .with(dataProvider)
                .with(memoryCleanupSetting)
                .build();

            val description = cache.getMarketDescriptor(
                FREE_TEXT_MARKET_ID,
                nascarOutrightsVariant().id(),
                new Languages.BestEffort(languageA, languageB)
            );

            assertThat(description.getName(languageA))
                .isEqualTo(nascarOutrightsMarketDescription(languageA).getName());
            assertThat(description.getName(languageB))
                .isEqualTo(nascarOutrightsMarketDescription(languageB).getName());
        }

        @ParameterizedTest
        @MethodSource(WITH_MEMORY_CLEANUP_AND_WITHOUT)
        public void cachePreventsInvokingDataSourceRightAfterFailureToRetrieveData(
            Config memoryCleanupSetting
        ) throws Exception {
            val aLanguage = ENGLISH;
            val dataProvider = mock(DataProvider.class, withGetDataThrowingByDefault());

            val cache = stubbingOutDataProvidersAndTime()
                .with(time)
                .with(dataProvider)
                .with(memoryCleanupSetting)
                .build();

            assertThatThrownBy(() ->
                    cache.getMarketDescriptor(
                        EXACT_GOALS_MARKET_ID,
                        fivePlusVariant().id(),
                        new Languages.BestEffort(aLanguage)
                    )
                )
                .isInstanceOf(CacheItemNotFoundException.class);

            assertThatThrownBy(() ->
                    cache.getMarketDescriptor(
                        EXACT_GOALS_MARKET_ID,
                        fivePlusVariant().id(),
                        new Languages.BestEffort(aLanguage)
                    )
                )
                .isInstanceOf(CacheItemNotFoundException.class);
            verify(dataProvider, times(1)).getData(any(Locale.class), any(), any());
        }

        @ParameterizedTest
        @MethodSource(WITH_MEMORY_CLEANUP_AND_WITHOUT)
        public void cacheProtectsDataSourceAfterFailureForSecondLanguageAfterSuccessfullyFetchingFirstOne(
            Config memoryCleanupSetting
        ) throws Exception {
            val langA = ENGLISH;
            val langB = FRENCH;
            val dataProvider = providing(
                in(langA),
                nascarOutrightsVariant(),
                of(nascarOutrightsMarketDescription())
            );

            val cache = stubbingOutDataProvidersAndTime()
                .with(time)
                .with(dataProvider)
                .with(memoryCleanupSetting)
                .build();

            cache.getMarketDescriptor(
                FREE_TEXT_MARKET_ID,
                nascarOutrightsVariant().id(),
                new Languages.BestEffort(langA)
            );

            cache.getMarketDescriptor(
                FREE_TEXT_MARKET_ID,
                nascarOutrightsVariant().id(),
                new Languages.BestEffort(langB)
            );

            cache.getMarketDescriptor(
                FREE_TEXT_MARKET_ID,
                nascarOutrightsVariant().id(),
                new Languages.BestEffort(langB)
            );

            verify(dataProvider, times(1))
                .getData(langA, FREE_TEXT_MARKET_ID + "", nascarOutrightsVariant().id());
            verify(dataProvider, times(1))
                .getData(langB, FREE_TEXT_MARKET_ID + "", nascarOutrightsVariant().id());
        }

        @ParameterizedTest
        @MethodSource(WITH_MEMORY_CLEANUP_AND_WITHOUT)
        public void marketDescriptorWithCurrentlyAvailableLanguagesIsProvidedOnMissingLanguageFailureToFetch(
            Config memoryCleanupSetting
        ) throws Exception {
            val langA = ENGLISH;
            val langB = FRENCH;
            val dataProvider = providing(
                in(langA),
                nascarOutrightsVariant(),
                of(nascarOutrightsMarketDescription(langA))
            );

            val cache = stubbingOutDataProvidersAndTime()
                .with(time)
                .with(dataProvider)
                .with(memoryCleanupSetting)
                .build();

            cache.getMarketDescriptor(
                FREE_TEXT_MARKET_ID,
                nascarOutrightsVariant().id(),
                new Languages.BestEffort(langA)
            );

            val descriptor = cache.getMarketDescriptor(
                FREE_TEXT_MARKET_ID,
                nascarOutrightsVariant().id(),
                new Languages.BestEffort(langA, langB)
            );

            assertThat(descriptor.getName(langA))
                .isEqualTo(nascarOutrightsMarketDescription(langA).getName());
            assertThat(descriptor.getName(langB)).isNull();
        }

        @ParameterizedTest
        @MethodSource(WITH_MEMORY_CLEANUP_AND_WITHOUT)
        public void cacheThrottlesSubsequentInvocationAfterTheFirstCallFailed(Config memoryCleanupSetting)
            throws Exception {
            val aLanguage = ENGLISH;
            val dataProvider = mock(DataProvider.class, withGetDataThrowingByDefault());

            val cache = stubbingOutDataProvidersAndTime()
                .with(time)
                .with(dataProvider)
                .with(memoryCleanupSetting)
                .build();

            assertThatThrownBy(() ->
                    cache.getMarketDescriptor(
                        EXACT_GOALS_MARKET_ID,
                        fivePlusVariant().id(),
                        new Languages.BestEffort(aLanguage)
                    )
                )
                .isInstanceOf(CacheItemNotFoundException.class);

            time.tick(MORE_THAN_FAULTY_MARKET_TTL);

            assertThatThrownBy(() ->
                    cache.getMarketDescriptor(
                        EXACT_GOALS_MARKET_ID,
                        fivePlusVariant().id(),
                        new Languages.BestEffort(aLanguage)
                    )
                )
                .isInstanceOf(CacheItemNotFoundException.class);

            verify(dataProvider, times(2)).getData(any(Locale.class), any(), any());
        }

        @ParameterizedTest
        @MethodSource(WITH_MEMORY_CLEANUP_AND_WITHOUT)
        public void cacheTreatsNotFoundResponsesWithFaultyMarketsAsFaulty(Config memoryCleanupSetting)
            throws Exception {
            val aLanguage = ENGLISH;
            val dataProvider = providing(
                in(aLanguage),
                FREE_TEXT_MARKET_ID,
                nascarOutrightsVariant(),
                of(notFoundWithEmptyMarket())
            );

            val cache = stubbingOutDataProvidersAndTime()
                .with(time)
                .with(dataProvider)
                .with(memoryCleanupSetting)
                .build();

            assertThatThrownBy(() ->
                    cache.getMarketDescriptor(
                        FREE_TEXT_MARKET_ID,
                        nascarOutrightsVariant().id(),
                        new Languages.BestEffort(aLanguage)
                    )
                )
                .isInstanceOf(CacheItemNotFoundException.class);
        }

        @ParameterizedTest
        @MethodSource(NULLIFY_OR_EMPTY_MARKET_NAME__WITH_MEMORY_CLEANUP_AND_WITHOUT)
        public void cachesNameForLanguagesItIsAvailableInStartingWithAvailableOne(
            AttributeRemover marketName,
            Config memoryCleanupSetting
        ) throws Exception {
            val languageA = ENGLISH;
            val languageB = FRENCH;

            DescMarket faultyDescription = marketName.remove(nascarOutrightsMarketDescription(languageB));
            val dataProvider = providing(
                in(languageA),
                nascarOutrightsVariant(),
                of(nascarOutrightsMarketDescription(languageA)),
                in(languageB),
                nascarOutrightsOddEvenVariant(),
                of(faultyDescription)
            );

            val cache = stubbingOutDataProvidersAndTime()
                .with(dataProvider)
                .with(memoryCleanupSetting)
                .build();

            val nameInLangA = cache
                .getMarketDescriptor(
                    FREE_TEXT_MARKET_ID,
                    nascarOutrightsVariant().id(),
                    new Languages.BestEffort(languageA)
                )
                .getName(languageA);
            val noNameInLangeB = cache
                .getMarketDescriptor(
                    FREE_TEXT_MARKET_ID,
                    nascarOutrightsVariant().id(),
                    new Languages.BestEffort(languageB)
                )
                .getName(languageB);

            assertThat(nameInLangA).isEqualTo(nascarOutrightsMarketDescription(languageA).getName());
            assertThat(noNameInLangeB).isNull();
        }

        @ParameterizedTest
        @MethodSource(WITH_MEMORY_CLEANUP_AND_WITHOUT)
        public void cachesNameForLanguagesItIsAvailableInStartingWithMissingOne(Config memoryCleanupSetting)
            throws Exception {
            val languageA = ENGLISH;
            val languageB = FRENCH;

            DescMarket faultyDescription = nullifyMarketName(nascarOutrightsMarketDescription(languageB));

            val dataProvider = providing(
                in(languageA),
                nascarOutrightsVariant(),
                of(nascarOutrightsMarketDescription(languageA)),
                in(languageB),
                nascarOutrightsVariant(),
                of(faultyDescription)
            );

            val cache = stubbingOutDataProvidersAndTime()
                .with(time)
                .with(dataProvider)
                .with(memoryCleanupSetting)
                .build();

            val noNameInLangeB = cache
                .getMarketDescriptor(
                    FREE_TEXT_MARKET_ID,
                    nascarOutrightsVariant().id(),
                    new Languages.BestEffort(languageB)
                )
                .getName(languageB);
            val nameInLangA = cache
                .getMarketDescriptor(
                    FREE_TEXT_MARKET_ID,
                    nascarOutrightsVariant().id(),
                    new Languages.BestEffort(languageA)
                )
                .getName(languageA);

            assertThat(noNameInLangeB).isNull();
            assertThat(nameInLangA).isEqualTo(nascarOutrightsMarketDescription(languageA).getName());
        }

        @Test
        void concurrentNameRetrievalOfFaultyMarketsDoNotConflict() throws Exception {
            val aLanguage = ENGLISH;
            val executorService = Executors.newFixedThreadPool(15);
            val cache = stubbingOutDataProvidersAndTime()
                .with(
                    providing(
                        in(aLanguage),
                        nascarOutrightsVariant(),
                        of(nullifyMarketName(nascarOutrightsMarketDescription(aLanguage))),
                        in(aLanguage),
                        nascarOutrightsOddEvenVariant(),
                        of(nullifyMarketName(nascarOutrightsOddEvenMarketDescription(aLanguage)))
                    )
                )
                .with(aggresiveVariantCleaningConfig())
                .with(time)
                .build();

            Runnable tickClock = () -> time.tick(MORE_THAN_FAULTY_MARKET_TTL);
            val fetchOutrightsMarket = createGetMarketDescriptorTask(
                cache,
                nascarOutrightsVariant(),
                aLanguage
            );
            val fetchOddEvenOutrightsMarket = createGetMarketDescriptorTask(
                cache,
                nascarOutrightsOddEvenVariant(),
                aLanguage
            );

            repeatTimes(
                100_000,
                i -> {
                    int marketId = FREE_TEXT_MARKET_ID + i;
                    executorService.submit(tickClock);
                    executorService.submit(fetchOutrightsMarket.apply(marketId));
                    executorService.submit(fetchOddEvenOutrightsMarket.apply(marketId));
                }
            );

            repeatTimes(
                10_000,
                () -> {
                    assertThatNoException()
                        .isThrownBy(() -> {
                            cache.getMarketDescriptor(
                                FREE_TEXT_MARKET_ID,
                                nascarOutrightsVariant().id(),
                                new Languages.BestEffort(aLanguage)
                            );
                        });
                }
            );

            executorService.shutdown();
        }

        @Nested
        class LanguagesInBatch {

            @ParameterizedTest
            @MethodSource(WITH_MEMORY_CLEANUP_AND_WITHOUT)
            public void cachesNameForLanguagesItIsAvailableInStartingWithAvailableOne(
                Config memoryCleanupSetting
            ) throws Exception {
                val languageA = ENGLISH;
                val languageB = FRENCH;

                DescMarket faultyDescription = nullifyMarketName(nascarOutrightsMarketDescription(languageB));

                val dataProvider = providing(
                    in(languageA),
                    nascarOutrightsVariant(),
                    of(nascarOutrightsMarketDescription(languageA)),
                    in(languageB),
                    nascarOutrightsVariant(),
                    of(faultyDescription)
                );

                val cache = stubbingOutDataProvidersAndTime()
                    .with(dataProvider)
                    .with(memoryCleanupSetting)
                    .build();

                MarketDescription desc = cache.getMarketDescriptor(
                    FREE_TEXT_MARKET_ID,
                    nascarOutrightsVariant().id(),
                    new Languages.BestEffort(languageA, languageB)
                );

                assertThat(desc.getName(languageA))
                    .isEqualTo(nascarOutrightsMarketDescription(languageA).getName());
                assertThat(desc.getName(languageB)).isNull();
            }

            @ParameterizedTest
            @MethodSource(WITH_MEMORY_CLEANUP_AND_WITHOUT)
            public void cachesNameForLanguagesItIsAvailableInStartingWithMissingOne(
                Config memoryCleanupSetting
            ) throws Exception {
                val languageA = ENGLISH;
                val languageB = FRENCH;

                DescMarket faultyDescription = nullifyMarketName(nascarOutrightsMarketDescription(languageB));

                val dataProvider = providing(
                    in(languageA),
                    nascarOutrightsVariant(),
                    of(nascarOutrightsMarketDescription(languageA)),
                    in(languageB),
                    nascarOutrightsVariant(),
                    of(faultyDescription)
                );

                val cache = stubbingOutDataProvidersAndTime()
                    .with(dataProvider)
                    .with(memoryCleanupSetting)
                    .build();

                MarketDescription desc = cache.getMarketDescriptor(
                    FREE_TEXT_MARKET_ID,
                    nascarOutrightsVariant().id(),
                    new Languages.BestEffort(languageB, languageA)
                );

                assertThat(desc.getName(languageA))
                    .isEqualTo(nascarOutrightsMarketDescription(languageA).getName());
                assertThat(desc.getName(languageB)).isNull();
            }
        }

        private Function<Integer, Runnable> createGetMarketDescriptorTask(
            VariantMarketDescriptionCache cache,
            MarketVariant variant,
            Locale aLanguage
        ) {
            return marketId ->
                () -> {
                    try {
                        cache.getMarketDescriptor(
                            marketId,
                            variant.id(),
                            new Languages.BestEffort(aLanguage)
                        );
                    } catch (CacheItemNotFoundException e) {
                        // expected for markets with IDs which are not stubbed
                    }
                };
        }

        private Config aggresiveVariantCleaningConfig() {
            return new Config() {
                @Override
                public int getNumberOfVariantsToStartAggressiveCleanUpMemoryFrom() {
                    return 0;
                }
            };
        }

        @SneakyThrows
        private void repeatTimes(int times, VoidCallables.ThrowingRunnable r) {
            for (int i = 0; i < times; i++) {
                r.run();
            }
        }

        @SneakyThrows
        private void repeatTimes(int times, ThrowingConsumer<Integer> r) {
            for (int i = 0; i < times; i++) {
                r.accept(i);
            }
        }

        @SneakyThrows
        private TimeAwareScheduledRunnable doing(VoidCallables.ThrowingRunnable r, TimeInterval cadence) {
            val cadenceInSeconds = toSeconds(cadence.getInMillis());
            return () -> {
                long currentSeconds = toSeconds(time.now());
                if (currentSeconds % cadenceInSeconds == 0) {
                    r.run();
                }
            };
        }
    }

    @Nested
    class DeleteCacheItem {

        @Test
        void deletingNonexistentCacheItemHasNoSideEffect() throws Exception {
            val aLanguage = ENGLISH;
            val cache = stubbingOutDataProvidersAndTime()
                .with(
                    providing(
                        in(aLanguage),
                        nascarOutrightsVariant(),
                        of(nascarOutrightsMarketDescription(aLanguage))
                    )
                )
                .build();

            cache.deleteCacheItem(FREE_TEXT_MARKET_ID, nascarOutrightsVariant().id());

            val description = cache.getMarketDescriptor(
                FREE_TEXT_MARKET_ID,
                nascarOutrightsVariant().id(),
                new Languages.BestEffort(aLanguage)
            );

            assertThat(description.getName(aLanguage))
                .isEqualTo(nascarOutrightsMarketDescription(aLanguage).getName());
        }

        @ParameterizedTest
        @MethodSource(LANGUAGES)
        void removingCacheItemCausesReFetching(Locale aLanguage) throws Exception {
            val dataProvider = providing(
                in(aLanguage),
                nascarOutrightsVariant(),
                of(nascarOutrightsMarketDescription(aLanguage))
            );
            val cache = stubbingOutDataProvidersAndTime().with(dataProvider).build();
            cache.getMarketDescriptor(
                FREE_TEXT_MARKET_ID,
                nascarOutrightsVariant().id(),
                new Languages.BestEffort(aLanguage)
            );

            cache.deleteCacheItem(FREE_TEXT_MARKET_ID, nascarOutrightsVariant().id());

            cache.getMarketDescriptor(
                FREE_TEXT_MARKET_ID,
                nascarOutrightsVariant().id(),
                new Languages.BestEffort(aLanguage)
            );

            verify(dataProvider, times(2))
                .getData(aLanguage, FREE_TEXT_MARKET_ID + "", nascarOutrightsVariant().id());
        }

        @Test
        void removesOnlyRequestedItemLeavingOthersIntact() throws Exception {
            val aLanguage = ENGLISH;
            val dataProvider = providing(
                in(aLanguage),
                nascarOutrightsVariant(),
                of(nascarOutrightsMarketDescription(aLanguage)),
                in(aLanguage),
                nascarOutrightsOddEvenVariant(),
                of(nascarOutrightsOddEvenMarketDescription(aLanguage))
            );
            val cache = stubbingOutDataProvidersAndTime().with(dataProvider).build();
            cache.getMarketDescriptor(
                FREE_TEXT_MARKET_ID,
                nascarOutrightsVariant().id(),
                new Languages.BestEffort(aLanguage)
            );
            cache.getMarketDescriptor(
                FREE_TEXT_MARKET_ID,
                nascarOutrightsOddEvenVariant().id(),
                new Languages.BestEffort(aLanguage)
            );

            cache.deleteCacheItem(FREE_TEXT_MARKET_ID, nascarOutrightsVariant().id());

            cache.getMarketDescriptor(
                FREE_TEXT_MARKET_ID,
                nascarOutrightsOddEvenVariant().id(),
                new Languages.BestEffort(aLanguage)
            );

            verify(dataProvider, times(1))
                .getData(aLanguage, FREE_TEXT_MARKET_ID + "", nascarOutrightsOddEvenVariant().id());
        }

        @Test
        void removingCacheItemWithVariantIdBeingPrefixOfOtherCacheItemVariantIdDoesNotAffectThePrefixedItem()
            throws Exception {
            val aLanguage = ENGLISH;
            String prefixVariant = "test:variant:234";
            String prefixedVariant = prefixVariant + "5";
            val dataProvider = providing(
                in(aLanguage),
                () -> prefixVariant,
                of(testVariantMarketDescription(prefixVariant)),
                in(aLanguage),
                () -> prefixedVariant,
                of(testVariantMarketDescription(prefixedVariant))
            );
            val cache = stubbingOutDataProvidersAndTime().with(dataProvider).build();
            cache.getMarketDescriptor(
                FREE_TEXT_MARKET_ID,
                prefixVariant,
                new Languages.BestEffort(aLanguage)
            );
            cache.getMarketDescriptor(
                FREE_TEXT_MARKET_ID,
                prefixedVariant,
                new Languages.BestEffort(aLanguage)
            );

            cache.deleteCacheItem(FREE_TEXT_MARKET_ID, prefixVariant);

            cache.getMarketDescriptor(
                FREE_TEXT_MARKET_ID,
                prefixedVariant,
                new Languages.BestEffort(aLanguage)
            );

            verify(dataProvider, times(1)).getData(aLanguage, FREE_TEXT_MARKET_ID + "", prefixedVariant);
        }

        private DescMarket testVariantMarketDescription(String variantId) {
            DescMarket market = new DescMarket();
            market.setId(FREE_TEXT_MARKET_ID);
            market.setVariant(variantId);
            return market;
        }
    }

    private static long toSeconds(long millis) {
        return millis / MILLIS_IN_SECOND;
    }
}

interface TimeAwareScheduledRunnable {
    void runIfScheduled() throws Exception;
}

class VariantMarketSources {

    public static Stream<Arguments> nullifyOrEmptyMarketNameAndWithMemoryCleanupAndWithout() {
        val withMemoryCleaningUp = mock(Config.class);
        when(withMemoryCleaningUp.getNumberOfVariantsToStartAggressiveCleanUpMemoryFrom()).thenReturn(0);
        val noMemoryCleaningUp = mock(Config.class);
        final int highEnoughNumberToNotBeExercisedByTests = 1000;
        when(noMemoryCleaningUp.getNumberOfVariantsToStartAggressiveCleanUpMemoryFrom())
            .thenReturn(highEnoughNumberToNotBeExercisedByTests);
        return Stream.of(
            Arguments.of(
                Named.of("nullified market name", (AttributeRemover) VariantMarketSources::nullifyMarketName),
                Named.of("with memory cleanup", withMemoryCleaningUp)
            ),
            Arguments.of(
                Named.of("empty market name", (AttributeRemover) VariantMarketSources::emptyMarketName),
                Named.of("with memory cleanup", withMemoryCleaningUp)
            ),
            Arguments.of(
                Named.of("nullified market name", (AttributeRemover) VariantMarketSources::nullifyMarketName),
                Named.of("no memory cleanup", noMemoryCleaningUp)
            ),
            Arguments.of(
                Named.of("empty market name", (AttributeRemover) VariantMarketSources::emptyMarketName),
                Named.of("no memory cleanup", noMemoryCleaningUp)
            )
        );
    }

    public static Stream<Arguments> withMemoryCleanupAndWithout() {
        val configForMemoryCleaningUp = mock(Config.class);
        when(configForMemoryCleaningUp.getNumberOfVariantsToStartAggressiveCleanUpMemoryFrom()).thenReturn(0);
        val configForNoMemoryCleaningUp = mock(Config.class);
        final int highEnoughNumberNotToBeExercisedByTests = 1000;
        when(configForMemoryCleaningUp.getNumberOfVariantsToStartAggressiveCleanUpMemoryFrom())
            .thenReturn(highEnoughNumberNotToBeExercisedByTests);
        return Stream.of(
            Arguments.of(Named.of("with memory cleanup", configForMemoryCleaningUp)),
            Arguments.of(Named.of("without memory cleanup", configForNoMemoryCleaningUp))
        );
    }

    @SuppressWarnings("unused")
    public static Stream<Arguments> nullifyOrEmptyMarketName() {
        return Stream.of(
            Arguments.of(
                Named.<AttributeRemover>of("nullified market name", VariantMarketSources::nullifyMarketName)
            ),
            Arguments.of(
                Named.<AttributeRemover>of("empty market name", VariantMarketSources::emptyMarketName)
            )
        );
    }

    public static DescMarket nullifyMarketName(DescMarket market) {
        market.setName(null);
        return market;
    }

    public static DescMarket emptyMarketName(DescMarket market) {
        market.setName("");
        return market;
    }

    public static Stream<Arguments> nullifyOrEmptyOutcomes() {
        return Stream.of(
            Arguments.of(
                Named.<AttributeRemover>of("nullified outcomes", VariantMarketSources::nullifyOutcomes)
            ),
            Arguments.of(Named.<AttributeRemover>of("zero outcomes", VariantMarketSources::emptyOutcomes))
        );
    }

    public static DescMarket nullifyOutcomes(DescMarket market) {
        market.setOutcomes(null);
        assertThat(market.getOutcomes()).isNull();
        return market;
    }

    public static DescMarket emptyOutcomes(DescMarket market) {
        market.setOutcomes(new DescOutcomes());
        assertThat(market.getOutcomes().getOutcome()).isEmpty();
        return market;
    }

    public interface AttributeRemover {
        DescMarket remove(DescMarket market);
    }
}

class LanguageSources {

    static Set<Locale> languages() {
        return ImmutableSet.of(ENGLISH, FRENCH);
    }
}

interface ThrowingConsumer<T> {
    void accept(T input) throws Exception;
}
