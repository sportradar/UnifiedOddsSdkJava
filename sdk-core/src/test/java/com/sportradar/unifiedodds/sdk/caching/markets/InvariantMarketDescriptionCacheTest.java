/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */
package com.sportradar.unifiedodds.sdk.caching.markets;

import static com.sportradar.unifiedodds.sdk.caching.markets.InvariantMarketDescriptionCaches.stubbingOutDataProvidersAndScheduler;
import static com.sportradar.unifiedodds.sdk.caching.markets.Sources.*;
import static com.sportradar.unifiedodds.sdk.conn.SapiMarketDescriptions.OddEven.oddEvenMarketDescription;
import static com.sportradar.unifiedodds.sdk.conn.marketids.OddEvenMarketIds.ODD_EVEN_MARKET_ID;
import static com.sportradar.unifiedodds.sdk.impl.MarketDescriptionDataProviders.providingList;
import static com.sportradar.utils.domain.names.LanguageHolder.in;
import static java.util.Arrays.asList;
import static java.util.Locale.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

import com.sportradar.uf.sportsapi.datamodel.DescMarket;
import com.sportradar.unifiedodds.sdk.entities.markets.MarketDescription;
import com.sportradar.unifiedodds.sdk.internal.caching.Languages;
import com.sportradar.unifiedodds.sdk.internal.exceptions.CacheItemNotFoundException;
import com.sportradar.unifiedodds.sdk.testutil.generic.concurrent.VoidCallables;
import java.util.Locale;
import java.util.stream.Stream;
import lombok.val;
import org.junit.jupiter.api.Named;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;

public class InvariantMarketDescriptionCacheTest {

    private static final String IRRELEVANT = null;
    private static final String NULLIFY_OR_EMPTY_MARKET_NAME =
        "com.sportradar.unifiedodds.sdk.caching.markets.Sources#nullifyOrEmptyMarketName";

    @Nested
    class GetMarketDescriptor {

        @ParameterizedTest
        @ValueSource(strings = { "en", "fr" })
        public void translatesName(String language) throws Exception {
            val aLanguage = new Locale(language);
            val cache = stubbingOutDataProvidersAndScheduler()
                .with(providingList(in(aLanguage), oddEvenMarketDescription(aLanguage)))
                .build();

            val description = cache.getMarketDescriptor(
                ODD_EVEN_MARKET_ID,
                IRRELEVANT,
                new Languages.BestEffort(aLanguage)
            );

            assertThat(description.getName(aLanguage))
                .isEqualTo(oddEvenMarketDescription(aLanguage).getName());
        }

        @ParameterizedTest
        @MethodSource(NULLIFY_OR_EMPTY_MARKET_NAME)
        public void notTranslatesMissingNames(NameRemover marketName) throws Exception {
            val aLanguage = ENGLISH;

            DescMarket marketDescription = marketName.remove(oddEvenMarketDescription(aLanguage));

            val cache = stubbingOutDataProvidersAndScheduler()
                .with(providingList(in(aLanguage), marketDescription))
                .build();

            val description = cache.getMarketDescriptor(
                ODD_EVEN_MARKET_ID,
                IRRELEVANT,
                new Languages.BestEffort(aLanguage)
            );

            assertThat(description.getName(aLanguage)).isNull();
        }

        @Test
        public void cachesName() throws Exception {
            val aLanguage = ENGLISH;
            val dataProvider = providingList(in(aLanguage), oddEvenMarketDescription(aLanguage));

            val cache = stubbingOutDataProvidersAndScheduler().with(dataProvider).build();

            val description1 = cache.getMarketDescriptor(
                ODD_EVEN_MARKET_ID,
                IRRELEVANT,
                new Languages.BestEffort(aLanguage)
            );

            val description2 = cache.getMarketDescriptor(
                ODD_EVEN_MARKET_ID,
                IRRELEVANT,
                new Languages.BestEffort(aLanguage)
            );

            assertThat(description1.getName(aLanguage)).isEqualTo(description2.getName(aLanguage));
            verify(dataProvider).getData(aLanguage);
        }

        @ParameterizedTest
        @MethodSource(NULLIFY_OR_EMPTY_MARKET_NAME)
        public void cachesMissingName(NameRemover marketName) throws Exception {
            val aLanguage = ENGLISH;
            val dataProvider = providingList(
                in(aLanguage),
                marketName.remove(oddEvenMarketDescription(aLanguage))
            );

            val cache = stubbingOutDataProvidersAndScheduler().with(dataProvider).build();

            val description1 = cache.getMarketDescriptor(
                ODD_EVEN_MARKET_ID,
                IRRELEVANT,
                new Languages.BestEffort(aLanguage)
            );

            val description2 = cache.getMarketDescriptor(
                ODD_EVEN_MARKET_ID,
                IRRELEVANT,
                new Languages.BestEffort(aLanguage)
            );

            assertThat(description1.getName(aLanguage)).isNull();
            assertThat(description2.getName(aLanguage)).isNull();
            verify(dataProvider).getData(aLanguage);
        }

        @Test
        public void unknownMarketsNotReFetchesToProtectDataSourceAfterInitialFetchingOfMarkets()
            throws Exception {
            val aLanguage = ENGLISH;
            final int unknownMarketId = 123456;
            val dataProvider = providingList(in(aLanguage), oddEvenMarketDescription());

            val cache = stubbingOutDataProvidersAndScheduler().with(dataProvider).build();

            cache.getMarketDescriptor(ODD_EVEN_MARKET_ID, IRRELEVANT, new Languages.BestEffort(aLanguage));
            expectNotToFind(() ->
                cache.getMarketDescriptor(unknownMarketId, IRRELEVANT, new Languages.BestEffort(aLanguage))
            );
            expectNotToFind(() ->
                cache.getMarketDescriptor(unknownMarketId, IRRELEVANT, new Languages.BestEffort(aLanguage))
            );

            verify(dataProvider, times(1)).getData(aLanguage);
        }

        @Test
        public void unknownMarketsNotReFetchesToProtectDataSource() throws Exception {
            val aLanguage = ENGLISH;
            final int unknownMarketId = 123456;
            val dataProvider = providingList(in(aLanguage), oddEvenMarketDescription());

            val cache = stubbingOutDataProvidersAndScheduler().with(dataProvider).build();

            expectNotToFind(() ->
                cache.getMarketDescriptor(unknownMarketId, IRRELEVANT, new Languages.BestEffort(aLanguage))
            );
            expectNotToFind(() ->
                cache.getMarketDescriptor(unknownMarketId, IRRELEVANT, new Languages.BestEffort(aLanguage))
            );

            verify(dataProvider, times(1)).getData(aLanguage);
        }

        @Test
        public void validMarketsAreCachedDuringInitialFetchCausedByQueryingUnknownMarket() throws Exception {
            val aLanguage = ENGLISH;
            final int unknownMarketId = 123456;
            val dataProvider = providingList(in(aLanguage), oddEvenMarketDescription());

            val cache = stubbingOutDataProvidersAndScheduler().with(dataProvider).build();

            expectNotToFind(() ->
                cache.getMarketDescriptor(unknownMarketId, IRRELEVANT, new Languages.BestEffort(aLanguage))
            );
            val description = cache.getMarketDescriptor(
                ODD_EVEN_MARKET_ID,
                IRRELEVANT,
                new Languages.BestEffort(aLanguage)
            );

            assertThat(description.getName(aLanguage)).isEqualTo(oddEvenMarketDescription().getName());
            verify(dataProvider, times(1)).getData(aLanguage);
        }

        @Test
        public void unknownMarketsNotCauseReFetchingToProtectDataSourceForLanguagesNotSeenYet()
            throws Exception {
            val langA = ENGLISH;
            val langB = FRENCH;
            final int unknownMarketId = 123456;
            val dataProvider = providingList(
                in(langA),
                oddEvenMarketDescription(langA),
                in(langB),
                oddEvenMarketDescription(langB)
            );

            val cache = stubbingOutDataProvidersAndScheduler().with(dataProvider).build();

            cache.getMarketDescriptor(ODD_EVEN_MARKET_ID, IRRELEVANT, new Languages.BestEffort(langA));
            expectNotToFind(() ->
                cache.getMarketDescriptor(unknownMarketId, IRRELEVANT, new Languages.BestEffort(langB))
            );
            expectNotToFind(() ->
                cache.getMarketDescriptor(unknownMarketId, IRRELEVANT, new Languages.BestEffort(langB))
            );

            verify(dataProvider, times(1)).getData(langA);
            verify(dataProvider, times(1)).getData(langB);
        }

        @Test
        public void translatesNameToMultipleLanguagesAtOnce() throws Exception {
            val languageA = ENGLISH;
            val languageB = FRENCH;
            val dataProvider = providingList(
                in(languageA),
                oddEvenMarketDescription(languageA),
                in(languageB),
                oddEvenMarketDescription(languageB)
            );

            val cache = stubbingOutDataProvidersAndScheduler().with(dataProvider).build();

            val description = cache.getMarketDescriptor(
                ODD_EVEN_MARKET_ID,
                IRRELEVANT,
                new Languages.BestEffort(languageA, languageB)
            );

            assertThat(description.getName(languageA))
                .isEqualTo(oddEvenMarketDescription(languageA).getName());
            assertThat(description.getName(languageB))
                .isEqualTo(oddEvenMarketDescription(languageB).getName());
        }

        @ParameterizedTest
        @MethodSource(NULLIFY_OR_EMPTY_MARKET_NAME)
        public void cachesNameForLanguagesItIsAvailableInStartingWithAvailableOne(NameRemover marketName)
            throws Exception {
            val languageA = ENGLISH;
            val languageB = FRENCH;

            DescMarket faultyDescription = marketName.remove(oddEvenMarketDescription(languageB));
            val dataProvider = providingList(
                in(languageA),
                oddEvenMarketDescription(languageA),
                in(languageB),
                faultyDescription
            );

            val cache = stubbingOutDataProvidersAndScheduler().with(dataProvider).build();

            val nameInLangA = cache
                .getMarketDescriptor(ODD_EVEN_MARKET_ID, IRRELEVANT, new Languages.BestEffort(languageA))
                .getName(languageA);
            val noNameInLangeB = cache
                .getMarketDescriptor(ODD_EVEN_MARKET_ID, IRRELEVANT, new Languages.BestEffort(languageB))
                .getName(languageB);

            assertThat(nameInLangA).isEqualTo(oddEvenMarketDescription(languageA).getName());
            assertThat(noNameInLangeB).isNull();
        }

        @Test
        public void cachesNameForLanguagesItIsAvailableInStartingWithMissingOne() throws Exception {
            val languageA = ENGLISH;
            val languageB = FRENCH;

            DescMarket faultyDescription = nullifyMarketName(oddEvenMarketDescription(languageB));
            val dataProvider = providingList(
                in(languageA),
                oddEvenMarketDescription(languageA),
                in(languageB),
                faultyDescription
            );

            val cache = stubbingOutDataProvidersAndScheduler().with(dataProvider).build();

            val noNameInLangeB = cache
                .getMarketDescriptor(ODD_EVEN_MARKET_ID, IRRELEVANT, new Languages.BestEffort(languageB))
                .getName(languageB);
            val nameInLangA = cache
                .getMarketDescriptor(ODD_EVEN_MARKET_ID, IRRELEVANT, new Languages.BestEffort(languageA))
                .getName(languageA);

            assertThat(noNameInLangeB).isNull();
            assertThat(nameInLangA).isEqualTo(oddEvenMarketDescription(languageA).getName());
        }

        @Test
        public void cachesNameForLanguagesItIsAvailableInBatchStartingWithAvailableOne() throws Exception {
            val languageA = ENGLISH;
            val languageB = FRENCH;

            DescMarket faultyDescription = nullifyMarketName(oddEvenMarketDescription(languageB));
            val dataProvider = providingList(
                in(languageA),
                oddEvenMarketDescription(languageA),
                in(languageB),
                faultyDescription
            );

            val cache = stubbingOutDataProvidersAndScheduler().with(dataProvider).build();

            MarketDescription desc = cache.getMarketDescriptor(
                ODD_EVEN_MARKET_ID,
                IRRELEVANT,
                new Languages.BestEffort(languageA, languageB)
            );

            assertThat(desc.getName(languageA)).isEqualTo(oddEvenMarketDescription(languageA).getName());
            assertThat(desc.getName(languageB)).isNull();
        }

        @Test
        public void cachesNameForLanguagesItIsAvailableInBatchStartingWithMissingOne() throws Exception {
            val languageA = ENGLISH;
            val languageB = FRENCH;

            DescMarket faultyDescription = nullifyMarketName(oddEvenMarketDescription(languageB));
            val dataProvider = providingList(
                in(languageA),
                oddEvenMarketDescription(languageA),
                in(languageB),
                faultyDescription
            );

            val cache = stubbingOutDataProvidersAndScheduler().with(dataProvider).build();

            val description = cache.getMarketDescriptor(
                ODD_EVEN_MARKET_ID,
                IRRELEVANT,
                new Languages.BestEffort(languageB, languageA)
            );

            assertThat(description.getName(languageB)).isNull();
            assertThat(description.getName(languageA))
                .isEqualTo(oddEvenMarketDescription(languageA).getName());
        }

        private void expectNotToFind(VoidCallables.ThrowingRunnable runnable) {
            assertThatThrownBy(runnable::run).isInstanceOf(CacheItemNotFoundException.class);
        }
    }

    @Nested
    class OnInitialization {

        @Test
        public void automaticallyLoadsMarketDescriptionsForAllLanguages() throws Exception {
            val dataProvider = providingList(
                in(ENGLISH),
                oddEvenMarketDescription(ENGLISH),
                in(FRENCH),
                oddEvenMarketDescription(FRENCH)
            );

            stubbingOutDataProvidersAndScheduler()
                .with(dataProvider)
                .withImmediatelyExecutingTaskScheduler()
                .withPrefetchLanguages(asList(ENGLISH, FRENCH))
                .build();

            verify(dataProvider).getData(ENGLISH);
            verify(dataProvider).getData(FRENCH);
        }
    }
}

class Sources {

    public static Stream<Arguments> nullifyOrEmptyMarketName() {
        return Stream.of(
            Arguments.of(Named.<NameRemover>of("nullified market name", Sources::nullifyMarketName)),
            Arguments.of(Named.<NameRemover>of("empty market name", Sources::emptyMarketName))
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

    public interface NameRemover {
        DescMarket remove(DescMarket market);
    }
}
