/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */

package com.sportradar.unifiedodds.sdk.caching.markets;

import static java.lang.String.format;

import com.google.common.base.Preconditions;
import com.google.common.base.Strings;
import com.google.common.cache.Cache;
import com.sportradar.uf.sportsapi.datamodel.DescMarket;
import com.sportradar.uf.sportsapi.datamodel.MarketDescriptions;
import com.sportradar.unifiedodds.sdk.caching.ci.markets.MarketDescriptionCi;
import com.sportradar.unifiedodds.sdk.domain.language.Languages;
import com.sportradar.unifiedodds.sdk.entities.markets.MarketDescription;
import com.sportradar.unifiedodds.sdk.exceptions.internal.CacheItemNotFoundException;
import com.sportradar.unifiedodds.sdk.exceptions.internal.DataProviderException;
import com.sportradar.unifiedodds.sdk.exceptions.internal.IllegalCacheStateException;
import com.sportradar.unifiedodds.sdk.impl.DataProvider;
import com.sportradar.unifiedodds.sdk.impl.TimeUtils;
import com.sportradar.unifiedodds.sdk.impl.markets.MappingValidatorFactory;
import com.sportradar.unifiedodds.sdk.impl.markets.MarketDescriptionImpl;
import com.sportradar.utils.SdkHelper;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.locks.ReentrantLock;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Single variant market description cache
 */
@SuppressWarnings({ "ClassFanOutComplexity", "ConstantName", "MagicNumber", "ParameterAssignment" })
public class VariantMarketDescriptionCache implements MarketDescriptionCache {

    private static final Logger logger = LoggerFactory.getLogger(VariantMarketDescriptionCache.class);
    private static final String CACHE_KEY_SEPARATOR = "_";
    private final Cache<String, MarketDescriptionCi> cache;
    private final DataProvider<MarketDescriptions> dataProvider;
    private final MappingValidatorFactory mappingValidatorFactory;
    private final ReentrantLock lock = new ReentrantLock();
    private final TimeUtils time;
    private final Config config;
    private Map<String, Date> fetchedVariants = new ConcurrentHashMap<>();
    private Date lastTimeFetchedVariantsWereCleared;

    public VariantMarketDescriptionCache(
        Cache<String, MarketDescriptionCi> cache,
        DataProvider<MarketDescriptions> dataProvider,
        MappingValidatorFactory mappingValidatorFactory,
        TimeUtils time,
        Config config
    ) {
        Preconditions.checkNotNull(cache);
        Preconditions.checkNotNull(dataProvider);
        Preconditions.checkNotNull(mappingValidatorFactory);
        Preconditions.checkNotNull(time);
        Preconditions.checkNotNull(config);

        this.cache = cache;
        this.dataProvider = dataProvider;
        this.mappingValidatorFactory = mappingValidatorFactory;
        this.time = time;
        this.config = config;
        this.lastTimeFetchedVariantsWereCleared = new Date(time.now());
    }

    @Override
    public MarketDescription getMarketDescriptor(
        int marketId,
        String variant,
        Languages.BestEffort bestEffort
    ) throws CacheItemNotFoundException {
        Preconditions.checkArgument(marketId > 0);
        Preconditions.checkArgument(!Strings.isNullOrEmpty(variant));
        Preconditions.checkNotNull(bestEffort);
        List<Locale> locales = bestEffort.getLanguages();
        Preconditions.checkNotNull(locales);
        Preconditions.checkArgument(!locales.isEmpty());

        MarketDescriptionCi marketCi;
        try {
            marketCi =
                cache.get(
                    getCacheKey(marketId, variant),
                    () -> loadMarketDescriptorData(null, marketId, variant, locales)
                );
        } catch (ExecutionException e) {
            throw new CacheItemNotFoundException("The requested market descriptor could not be found", e);
        }

        List<Locale> localesToFetch;
        try {
            lock.lock();
            localesToFetch = missingOrFaultyLanguages(locales, marketCi);
        } finally {
            lock.unlock();
        }

        if (!localesToFetch.isEmpty()) {
            try {
                lock.lock();
                localesToFetch = missingOrFaultyLanguages(locales, marketCi);
                if (!localesToFetch.isEmpty()) {
                    try {
                        loadMarketDescriptorData(marketCi, marketId, variant, localesToFetch);
                    } catch (IllegalCacheStateException e) {
                        logger.info(
                            format("variant market[%d %s] failed to be loaded", marketId, variant),
                            e
                        );
                    }
                }
            } finally {
                lock.unlock();
            }
        }

        return new MarketDescriptionImpl(marketCi, locales);
    }

    private static List<Locale> missingOrFaultyLanguages(List<Locale> locales, MarketDescriptionCi marketCi) {
        return locales
            .stream()
            .filter(l -> missingLanguages(l, marketCi) || faultyLanguage(l, marketCi))
            .collect(Collectors.toList());
    }

    private static boolean faultyLanguage(Locale l, MarketDescriptionCi marketCi) {
        boolean languageIsFaulty = marketCi.getName(l) == null || anyOutcomeIsMissingName(l, marketCi);
        return marketCi.getCachedLocales().contains(l) && languageIsFaulty;
    }

    private static boolean anyOutcomeIsMissingName(Locale l, MarketDescriptionCi marketCi) {
        if (marketCi.getOutcomes() != null) {
            return marketCi.getOutcomes().stream().map(o -> o.getName(l)).anyMatch(""::equals);
        } else {
            return false;
        }
    }

    private static boolean missingLanguages(Locale l, MarketDescriptionCi marketCi) {
        return !marketCi.getCachedLocales().contains(l);
    }

    @Override
    public boolean loadMarketDescriptions() {
        return true;
    }

    @Override
    public void deleteCacheItem(int marketId, String variant) {
        String cacheId = getCacheKey(marketId, variant);
        if (cache.asMap().containsKey(cacheId)) {
            logger.debug("Delete variant market: {}", cacheId);
            cache.invalidate(cacheId);
            invalidateFetchedVariants(marketId, variant);
        }
    }

    private void invalidateFetchedVariants(int marketId, String variant) {
        String marketDescriptionCacheKey = getCacheKey(marketId, variant);
        Set<String> fetchedVariantsCacheKeys = fetchedVariants.keySet();
        fetchedVariantsCacheKeys
            .stream()
            .filter(startingWith(marketDescriptionCacheKey))
            .forEach(fetchedVariants::remove);
    }

    private Predicate<String> startingWith(String cacheKeyPrefix) {
        return v -> v.startsWith(cacheKeyPrefix + CACHE_KEY_SEPARATOR);
    }

    @Override
    public void updateCacheItem(int marketId, String variant) {
        String cacheId = getCacheKey(marketId, variant);
        MarketDescriptionCi description = cache.getIfPresent(cacheId);
        if (description != null) {
            description.setLastDataReceived(new Date());
        }
    }

    private MarketDescriptionCi loadMarketDescriptorData(
        MarketDescriptionCi existingMarketDescriptor,
        int marketId,
        String variant,
        List<Locale> locales
    ) throws IllegalCacheStateException {
        Preconditions.checkNotNull(locales);
        Preconditions.checkArgument(!Strings.isNullOrEmpty(variant));
        Preconditions.checkArgument(!locales.isEmpty());

        if (!isFetchingAllowed(marketId, variant, locales)) {
            throw new IllegalCacheStateException(
                format("Fetching of variant market[%d %s] data is throttled", marketId, variant)
            );
        }

        try {
            for (Locale mLoc : locales) {
                fetchedVariants.put(getFetchedVariantsKey(marketId, variant, mLoc), new Date(time.now()));
                MarketDescriptions data = dataProvider.getData(mLoc, String.valueOf(marketId), variant);

                validateExactlyOneMarketReceived(marketId, variant, data);
                validateMarketHasNonZeroId(marketId, variant, data);

                DescMarket descMarket = data.getMarket().get(0);
                if (existingMarketDescriptor == null) {
                    existingMarketDescriptor =
                        new MarketDescriptionCi(
                            descMarket,
                            mappingValidatorFactory,
                            mLoc,
                            SdkHelper.VariantMarketSingleCache
                        );
                } else {
                    existingMarketDescriptor.merge(descMarket, mLoc);
                }
            }

            return existingMarketDescriptor;
        } catch (DataProviderException ex) {
            throw new IllegalCacheStateException(
                format("An error occurred while fetching variant market[%d, %s] data", marketId, variant),
                ex
            );
        }
    }

    private static void validateExactlyOneMarketReceived(
        int marketId,
        String variant,
        MarketDescriptions data
    ) throws IllegalCacheStateException {
        if (data == null || data.getMarket().size() != 1) {
            throw new IllegalCacheStateException(
                format(
                    "Received variant market[%d, %s] response with invalid market entry count",
                    marketId,
                    variant
                )
            );
        }
    }

    private static void validateMarketHasNonZeroId(
        int marketId,
        String variant,
        MarketDescriptions descriptions
    ) throws IllegalCacheStateException {
        List<DescMarket> markets = descriptions.getMarket();
        if (markets.size() == 1 && hasIdZero(markets.get(0))) {
            throw new IllegalCacheStateException(
                format(
                    "For requested variant market[%d, %s] received a response with invalid market[id=%d]",
                    marketId,
                    variant,
                    markets.get(0).getId()
                )
            );
        }
    }

    private static boolean hasIdZero(DescMarket descMarket) {
        return descMarket != null && descMarket.getId() == 0;
    }

    private String getCacheKey(int id, String variant) {
        return id + CACHE_KEY_SEPARATOR + variant;
    }

    private boolean isFetchingAllowed(int marketId, String variant, List<Locale> locales) {
        for (Locale l : locales) {
            if (isFetchingAllowed(marketId, variant, l)) {
                return true;
            }
        }
        return false;
    }

    private boolean isFetchingAllowed(int marketId, String variant, Locale locale) {
        if (fetchedVariants.size() > config.getNumberOfVariantsToStartAggressiveCleanUpMemoryFrom()) {
            clearFetchedVariants();
        }

        String cacheKey = getFetchedVariantsKey(marketId, variant, locale);
        Date date = fetchedVariants.get(cacheKey);
        if (date == null) {
            return true;
        }
        if (SdkHelper.getTimeDifferenceInSeconds(new Date(time.now()), date) > 30) {
            return true;
        }
        clearFetchedVariants();

        return false;
    }

    private String getFetchedVariantsKey(int marketId, String variant, Locale locale) {
        return getCacheKey(marketId, variant) + CACHE_KEY_SEPARATOR + locale;
    }

    /**
     * clear records from fetchedVariants once a min
     */
    private void clearFetchedVariants() {
        if (
            SdkHelper.getTimeDifferenceInSeconds(new Date(time.now()), lastTimeFetchedVariantsWereCleared) >
            60
        ) {
            Set<String> keys = fetchedVariants.keySet();

            for (String key : keys) {
                Date currDate = fetchedVariants.get(key);
                if (
                    currDate != null &&
                    SdkHelper.getTimeDifferenceInSeconds(new Date(time.now()), currDate) > 30
                ) {
                    fetchedVariants.remove(key);
                }
            }
            lastTimeFetchedVariantsWereCleared = new Date(time.now());
        }
    }

    public static class Config {

        public int getNumberOfVariantsToStartAggressiveCleanUpMemoryFrom() {
            return 1000;
        }
    }
}
