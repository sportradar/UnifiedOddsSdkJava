/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */
package com.sportradar.unifiedodds.sdk.impl.markets;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.*;

import com.sportradar.unifiedodds.sdk.SdkInternalConfiguration;
import com.sportradar.unifiedodds.sdk.caching.markets.InvariantMarketDescriptionCache;
import com.sportradar.unifiedodds.sdk.caching.markets.MarketDescriptionCache;
import com.sportradar.unifiedodds.sdk.caching.markets.MarketDescriptionProvider;
import com.sportradar.unifiedodds.sdk.caching.markets.VariantDescriptionCache;
import com.sportradar.unifiedodds.sdk.oddsentities.Market;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import org.junit.jupiter.api.Test;

public class MarketManagerImplTest {

    private static final List<Locale> LIST_OF_2_LOCALES = new ArrayList<>(
        Arrays.asList(Locale.ENGLISH, Locale.FRENCH)
    );
    private static final String ANY_MARKET_NAME = "1x2;";
    private static final int ANY_THREAD_POOL_SIZE = 25;
    private static final int AMOUNT_OF_MARKETS_TO_PREFETCH = 100;
    private final SdkInternalConfiguration config = mock(SdkInternalConfiguration.class);
    private final MarketDescriptionProvider marketDescriptionProvider = mock(MarketDescriptionProvider.class);
    private final InvariantMarketDescriptionCache invariantMarketDescriptionCache = mock(
        InvariantMarketDescriptionCache.class
    );
    private final VariantDescriptionCache variantMarketDescriptionListCache = mock(
        VariantDescriptionCache.class
    );
    private final MarketDescriptionCache variantMarketDescriptionCache = mock(MarketDescriptionCache.class);
    private final MarketManagerImpl marketManager = new MarketManagerImpl(
        config,
        marketDescriptionProvider,
        invariantMarketDescriptionCache,
        variantMarketDescriptionListCache,
        variantMarketDescriptionCache
    );

    @Test
    public void parallelPrefetchVariantMarketDescriptionsShouldReturnZeroWhenNoMarketsInMarketList() {
        List<Market> emptyMarketList = new ArrayList<>();

        long actualTimeStamp = marketManager.parallelPrefetchVariantMarketDescriptions(
            emptyMarketList,
            false,
            1
        );

        long expectedTimestamp = 0;

        assertEquals(expectedTimestamp, actualTimeStamp);
    }

    @Test
    public void parallelPrefetchVariantMarketDescriptionsShouldReturnZeroWhenMarketListIsNull() {
        List<Market> emptyMarketList = null;

        long actualTimeStamp = marketManager.parallelPrefetchVariantMarketDescriptions(
            emptyMarketList,
            false,
            1
        );

        long expectedTimestamp = 0;

        assertEquals(expectedTimestamp, actualTimeStamp);
    }

    @Test
    public void parallelPrefetchVariantMarketDescriptionsShouldInvokeGetNameFor100NormalMarketsAnd2Languages() {
        List<Market> marketList = new ArrayList<>();
        Market anyMarket = mock(Market.class);

        setupParallelPrefetchVariantMarketDescriptions(marketList, anyMarket, AMOUNT_OF_MARKETS_TO_PREFETCH);

        boolean shouldFetchForOnlyVariantMarkets = false;
        marketManager.parallelPrefetchVariantMarketDescriptions(
            marketList,
            shouldFetchForOnlyVariantMarkets,
            ANY_THREAD_POOL_SIZE
        );

        int expectedAmountOfInvocations = AMOUNT_OF_MARKETS_TO_PREFETCH * LIST_OF_2_LOCALES.size();
        verify(anyMarket, times(expectedAmountOfInvocations)).getName(any());
    }

    @Test
    public void parallelPrefetchVariantMarketDescriptionsShouldInvokeGetNameOnlyFor100VariantMarketsAnd2Languages() {
        List<Market> marketList = new ArrayList<>();
        Market anyVariantMarket = mock(Market.class);

        setupParallelPrefetchVariantMarketDescriptions(
            marketList,
            anyVariantMarket,
            AMOUNT_OF_MARKETS_TO_PREFETCH
        );

        Market anyNonVariantMarket = mock(Market.class);
        marketList.add(anyNonVariantMarket);

        Map<String, String> mapOfAnySpecifier = new HashMap<>();
        mapOfAnySpecifier.put("variant", "anySpecifier");

        when(anyVariantMarket.getSpecifiers()).thenReturn(mapOfAnySpecifier);
        when(anyNonVariantMarket.getSpecifiers()).thenReturn(null);

        boolean shouldFetchOnlyVariantMarkets = true;
        marketManager.parallelPrefetchVariantMarketDescriptions(
            marketList,
            shouldFetchOnlyVariantMarkets,
            ANY_THREAD_POOL_SIZE
        );

        int expectedInvocationsForVariantMarket = AMOUNT_OF_MARKETS_TO_PREFETCH * LIST_OF_2_LOCALES.size();
        int expectedInvocationsForNonVariantMarket = 0;
        verify(anyVariantMarket, times(expectedInvocationsForVariantMarket)).getName(any());
        verify(anyNonVariantMarket, times(expectedInvocationsForNonVariantMarket)).getName(any());
    }

    @Test
    public void parallelPrefetchVariantMarketDescriptionsWithOnlyMarketsShouldDelegateWithOnlyVariantMarkets() {
        List<Market> marketList = new ArrayList<>();
        Market anyMarket = mock(Market.class);
        int amountOfMarkets = 1;

        setupParallelPrefetchVariantMarketDescriptions(marketList, anyMarket, amountOfMarkets);

        Map<String, String> mapOfAnySpecifier = new HashMap<>();
        mapOfAnySpecifier.put("variant", "anySpecifier");
        when(anyMarket.getSpecifiers()).thenReturn(mapOfAnySpecifier);

        marketManager.parallelPrefetchVariantMarketDescriptions(marketList);

        int expectedInvocations = amountOfMarkets * LIST_OF_2_LOCALES.size();

        verify(anyMarket, times(expectedInvocations)).getName(any());
    }

    @Test
    public void parallelPrefetchVariantMarketDescriptionsShouldDelegateWithMarketsAndOnlyVariantMarketSetToFalse() {
        List<Market> marketList = new ArrayList<>();
        Market anyMarket = mock(Market.class);
        int amountOfMarkets = 1;

        setupParallelPrefetchVariantMarketDescriptions(marketList, anyMarket, amountOfMarkets);

        boolean shouldFetchForVariantMarketsOnly = false;
        marketManager.parallelPrefetchVariantMarketDescriptions(marketList, shouldFetchForVariantMarketsOnly);

        int expectedInvocations = amountOfMarkets * LIST_OF_2_LOCALES.size();
        verify(anyMarket, times(expectedInvocations)).getName(any());
    }

    private void setupParallelPrefetchVariantMarketDescriptions(
        List<Market> marketList,
        Market anyMarket,
        int amountOfMarkets
    ) {
        for (int i = 0; i < amountOfMarkets; i++) {
            marketList.add(anyMarket);
        }
        when(config.getDesiredLocales()).thenReturn(LIST_OF_2_LOCALES);
        when(anyMarket.getName(any())).thenReturn(ANY_MARKET_NAME);
    }
}
