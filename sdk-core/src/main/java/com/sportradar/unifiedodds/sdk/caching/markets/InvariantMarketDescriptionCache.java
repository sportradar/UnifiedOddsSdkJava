/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */

package com.sportradar.unifiedodds.sdk.caching.markets;

import com.google.common.base.Preconditions;
import com.google.common.cache.Cache;
import com.sportradar.uf.sportsapi.datamodel.MarketDescriptions;
import com.sportradar.unifiedodds.sdk.caching.ci.markets.MarketDescriptionCI;
import com.sportradar.unifiedodds.sdk.entities.markets.MarketDescription;
import com.sportradar.unifiedodds.sdk.exceptions.internal.CacheItemNotFoundException;
import com.sportradar.unifiedodds.sdk.exceptions.internal.DataProviderException;
import com.sportradar.unifiedodds.sdk.exceptions.internal.IllegalCacheStateException;
import com.sportradar.unifiedodds.sdk.impl.DataProvider;
import com.sportradar.unifiedodds.sdk.impl.SDKTaskScheduler;
import com.sportradar.unifiedodds.sdk.impl.markets.MappingValidatorFactory;
import com.sportradar.unifiedodds.sdk.impl.markets.MarketDescriptionImpl;
import com.sportradar.utils.LanguageHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantLock;
import java.util.stream.Collectors;

/**
 * Created on 14/06/2017.
 * // TODO @eti: Javadoc
 */
public class InvariantMarketDescriptionCache implements MarketDescriptionCache {
    private static final Logger logger = LoggerFactory.getLogger(InvariantMarketDescriptionCache.class);

    private final Cache<String, MarketDescriptionCI> cache;
    private final DataProvider<MarketDescriptions> dataProvider;
    private final MappingValidatorFactory mappingValidatorFactory;
    private final List<Locale> prefetchLocales;
    private final List<Locale> fetchedLocales;
    private final ReentrantLock fetchLock = new ReentrantLock();
    private boolean hasTimerElapsedOnce;

    public InvariantMarketDescriptionCache(Cache<String, MarketDescriptionCI> cache,
                                           DataProvider<MarketDescriptions> dataProvider,
                                           MappingValidatorFactory mappingValidatorFactory,
                                           SDKTaskScheduler scheduler,
                                           List<Locale> prefetchLocales) {
        this.cache = cache;
        this.dataProvider = dataProvider;
        this.mappingValidatorFactory = mappingValidatorFactory;
        this.prefetchLocales = prefetchLocales;
        this.fetchedLocales = new ArrayList<>();

        scheduler.scheduleAtFixedRate("InvariantMarketCacheRefreshTask", this::onTimerElapsed, 5, 60 * 60 * 6, TimeUnit.SECONDS);
    }

    @Override
    public MarketDescription getMarketDescriptor(int marketId, String variant, List<Locale> locales) throws IllegalCacheStateException, CacheItemNotFoundException {
        Preconditions.checkArgument(marketId > 0);

        String processingCacheId = String.valueOf(marketId);
        MarketDescriptionCI cachedItem = getMarketInternal(processingCacheId, locales);

        return new MarketDescriptionImpl(cachedItem, locales);
    }

    public List<MarketDescription> getAllInvariantMarketDescriptions(List<Locale> locales) throws IllegalCacheStateException, CacheItemNotFoundException {
        Preconditions.checkNotNull(locales);
        Preconditions.checkArgument(!locales.isEmpty());

        // ensure all locales are present & fetch them if needed
        MarketDescriptionCI cachedItem = getMarketInternal("1", locales);

        return cache.asMap().values().stream()
                .map(ci -> new MarketDescriptionImpl(ci, locales))
                .collect(Collectors.toList());
    }

    private void onTimerElapsed() {
        List<Locale> locales2fetch;

        if (hasTimerElapsedOnce) {
            locales2fetch = prefetchLocales;
        } else {
            locales2fetch = prefetchLocales.stream()
                    .filter(pLocale -> !fetchedLocales.contains(pLocale)).collect(Collectors.toList());
        }

        fetchLock.lock();
        try {
            if (hasTimerElapsedOnce) {
                fetchedLocales.clear();
                cache.invalidateAll();
            }
            if (!locales2fetch.isEmpty()) {
                fetchMissingData(locales2fetch);
            }
        } catch (Exception e) { // so the timer does not die
            logger.warn("An error occurred while periodically fetching market description for languages [{}]",
                    locales2fetch.stream().map(Locale::toString).collect(Collectors.joining(", ")),
                    e);
        } finally {
            fetchLock.unlock();
        }

        hasTimerElapsedOnce = true;
    }

    private MarketDescriptionCI getMarketInternal(String id, List<Locale> locales) throws IllegalCacheStateException, CacheItemNotFoundException {
        Preconditions.checkNotNull(locales);
        Preconditions.checkArgument(!locales.isEmpty());

        MarketDescriptionCI description = cache.getIfPresent(id);
        if (description != null && getMissingLocales(description, locales).isEmpty()) {
            return description;
        }

        fetchLock.lock();
        try {
            description = cache.getIfPresent(id);
            List<Locale> missingLocales = getMissingLocales(description, locales);
            if (missingLocales.isEmpty()) {
                return description;
            }

            // validate the missing global locales so the SDK doesn't request infinite api requests, if a producer starts sending "unknown" markets
            List<Locale> missingGlobalLocales = LanguageHelper.findMissingLocales(fetchedLocales, locales);
            if (!missingGlobalLocales.isEmpty()) {
                fetchMissingData(getMissingLocales(description, locales));
            }
        } finally {
            fetchLock.unlock();
        }

        description = cache.getIfPresent(id);
        if (description == null || !getMissingLocales(description, locales).isEmpty()) {
            throw new CacheItemNotFoundException("After successful market fetch, the cache item should be complete[" + id + "], but its missing");
        }

        return description;
    }

    private void fetchMissingData(List<Locale> missingLocales) throws IllegalCacheStateException {
        Preconditions.checkNotNull(missingLocales);

        try {
            for (Locale missingLocale : missingLocales) {
                merge(missingLocale, dataProvider.getData(missingLocale));
            }
        } catch (DataProviderException e) {
            throw new IllegalCacheStateException("An error occurred while fetching invariant descriptors in [" + missingLocales + "]", e);
        }
    }

    private void merge(Locale locale, MarketDescriptions data) {
        Preconditions.checkNotNull(locale);
        Preconditions.checkNotNull(data);

        data.getMarket().forEach(market -> {
            String processingCacheItemId = String.valueOf(market.getId());
            MarketDescriptionCI cachedItem = cache.getIfPresent(processingCacheItemId);
            if (cachedItem == null) {
                cachedItem = new MarketDescriptionCI(market, mappingValidatorFactory, locale);
                cache.put(processingCacheItemId, cachedItem);
            } else {
                cachedItem.merge(market, locale);
            }
        });
        fetchedLocales.add(locale);
    }

    private List<Locale> getMissingLocales(MarketDescriptionCI item, List<Locale> requiredLocales) {
        Preconditions.checkNotNull(requiredLocales);
        Preconditions.checkArgument(!requiredLocales.isEmpty());

        if (item == null) {
            return requiredLocales;
        }

        return LanguageHelper.findMissingLocales(item.getCachedLocales(), requiredLocales);
    }
}
