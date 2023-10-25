/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */

package com.sportradar.unifiedodds.sdk.caching.markets;

import com.google.common.base.Preconditions;
import com.google.common.cache.Cache;
import com.sportradar.uf.sportsapi.datamodel.DescMarket;
import com.sportradar.uf.sportsapi.datamodel.MarketDescriptions;
import com.sportradar.unifiedodds.sdk.caching.ci.markets.MarketDescriptionCi;
import com.sportradar.unifiedodds.sdk.entities.markets.MarketDescription;
import com.sportradar.unifiedodds.sdk.exceptions.internal.CacheItemNotFoundException;
import com.sportradar.unifiedodds.sdk.exceptions.internal.DataProviderException;
import com.sportradar.unifiedodds.sdk.exceptions.internal.IllegalCacheStateException;
import com.sportradar.unifiedodds.sdk.impl.DataProvider;
import com.sportradar.unifiedodds.sdk.impl.ObservableDataProvider;
import com.sportradar.unifiedodds.sdk.impl.SdkTaskScheduler;
import com.sportradar.unifiedodds.sdk.impl.markets.MappingValidatorFactory;
import com.sportradar.unifiedodds.sdk.impl.markets.MarketDescriptionImpl;
import com.sportradar.utils.SdkHelper;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantLock;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created on 14/06/2017.
 * // TODO @eti: Javadoc
 */
@SuppressWarnings(
    {
        "ClassFanOutComplexity",
        "ConstantName",
        "IllegalCatch",
        "LambdaBodyLength",
        "LineLength",
        "MagicNumber",
        "MultipleStringLiterals",
        "ReturnCount",
    }
)
public class InvariantMarketDescriptionCache implements MarketDescriptionCache {

    private static final Logger logger = LoggerFactory.getLogger(InvariantMarketDescriptionCache.class);

    private final Cache<String, MarketDescriptionCi> cache;
    private final DataProvider<MarketDescriptions> dataProvider;
    private final ObservableDataProvider<MarketDescriptions> additionalMappingsProvider;
    private final MappingValidatorFactory mappingValidatorFactory;
    private final List<Locale> prefetchLocales;
    private final List<Locale> fetchedLocales;
    private final ReentrantLock fetchLock = new ReentrantLock();
    private boolean hasTimerElapsedOnce;

    public InvariantMarketDescriptionCache(
        Cache<String, MarketDescriptionCi> cache,
        DataProvider<MarketDescriptions> dataProvider,
        ObservableDataProvider<MarketDescriptions> additionalMappingsProvider,
        MappingValidatorFactory mappingValidatorFactory,
        SdkTaskScheduler scheduler,
        List<Locale> prefetchLocales
    ) {
        Preconditions.checkNotNull(cache);
        Preconditions.checkNotNull(dataProvider);
        Preconditions.checkNotNull(additionalMappingsProvider);
        Preconditions.checkNotNull(mappingValidatorFactory);
        Preconditions.checkNotNull(scheduler);
        Preconditions.checkNotNull(prefetchLocales);

        this.cache = cache;
        this.dataProvider = dataProvider;
        this.additionalMappingsProvider = additionalMappingsProvider;
        this.mappingValidatorFactory = mappingValidatorFactory;
        this.prefetchLocales = prefetchLocales;
        this.fetchedLocales = new ArrayList<>();

        scheduler.scheduleAtFixedRate(
            "InvariantMarketCacheRefreshTask",
            this::onTimerElapsed,
            5,
            60 * 60 * 6L,
            TimeUnit.SECONDS
        );

        additionalMappingsProvider.registerWatcher(this.getClass(), this::additionalMappingsChanged);
    }

    @Override
    public MarketDescription getMarketDescriptor(int marketId, String variant, List<Locale> locales)
        throws IllegalCacheStateException, CacheItemNotFoundException {
        Preconditions.checkArgument(marketId > 0);

        String processingCacheId = String.valueOf(marketId);
        MarketDescriptionCi cachedItem = getMarketInternal(processingCacheId, locales);

        return new MarketDescriptionImpl(cachedItem, locales);
    }

    @Override
    public boolean loadMarketDescriptions() {
        try {
            fetchedLocales.clear();
            logger.debug(
                "Loading invariant market descriptions for [{}] (user request).",
                prefetchLocales.stream().map(Locale::getLanguage).collect(Collectors.joining(","))
            );
            fetchMissingData(prefetchLocales);
        } catch (Exception e) {
            logger.warn(
                "An error occurred while fetching market description for languages [{}]",
                prefetchLocales.stream().map(Locale::getLanguage).collect(Collectors.joining(",")),
                e
            );
            return false;
        }
        return true;
    }

    @Override
    public void deleteCacheItem(int marketId, String variant) {
        String processingCacheItemId = String.valueOf(marketId);
        cache.invalidate(processingCacheItemId);
    }

    @Override
    public void updateCacheItem(int marketId, String variant) {
        MarketDescriptionCi description = cache.getIfPresent(String.valueOf(marketId));
        if (description != null) {
            description.setLastDataReceived(new Date());
        }
    }

    public List<MarketDescription> getAllInvariantMarketDescriptions(List<Locale> locales)
        throws IllegalCacheStateException, CacheItemNotFoundException {
        Preconditions.checkNotNull(locales);
        Preconditions.checkArgument(!locales.isEmpty());

        // ensure all locales are present & fetch them if needed
        MarketDescriptionCi cachedItem = getMarketInternal("1", locales);

        return cache
            .asMap()
            .values()
            .stream()
            .map(ci -> new MarketDescriptionImpl(ci, locales))
            .collect(Collectors.toList());
    }

    private void onTimerElapsed() {
        logger.info("Executing invariant market cache refresh");

        List<Locale> locales2fetch;

        if (hasTimerElapsedOnce) {
            locales2fetch = prefetchLocales;
        } else {
            locales2fetch =
                prefetchLocales
                    .stream()
                    .filter(pLocale -> !fetchedLocales.contains(pLocale))
                    .collect(Collectors.toList());
        }
        logger.debug(
            "Loading invariant market descriptions for [{}] (timer).",
            locales2fetch.stream().map(Locale::getLanguage).collect(Collectors.joining(","))
        );
        fetchLock.lock();
        try {
            if (hasTimerElapsedOnce) {
                fetchedLocales.clear();
            }
            if (!locales2fetch.isEmpty()) {
                fetchMissingData(locales2fetch);
            }
            hasTimerElapsedOnce = true;
        } catch (Exception e) { // so the timer does not die
            logger.warn(
                "An error occurred while periodically fetching market description for languages [{}]",
                locales2fetch.stream().map(Locale::getLanguage).collect(Collectors.joining(",")),
                e
            );
        } finally {
            fetchLock.unlock();
        }
    }

    private MarketDescriptionCi getMarketInternal(String id, List<Locale> locales)
        throws IllegalCacheStateException, CacheItemNotFoundException {
        Preconditions.checkNotNull(locales);
        Preconditions.checkArgument(!locales.isEmpty());

        MarketDescriptionCi description = cache.getIfPresent(id);
        if (description != null && getMissingLocales(description, locales).isEmpty()) {
            return description;
        }

        try {
            fetchLock.lock();
            description = cache.getIfPresent(id);
            List<Locale> missingLocales = getMissingLocales(description, locales);
            if (missingLocales.isEmpty()) {
                return description;
            }

            // validate the missing global locales so the SDK doesn't request infinite api requests, if a producer starts sending "unknown" markets
            List<Locale> missingGlobalLocales = SdkHelper.findMissingLocales(fetchedLocales, locales);
            if (!missingGlobalLocales.isEmpty()) {
                fetchMissingData(getMissingLocales(description, locales));
            }
        } finally {
            fetchLock.unlock();
        }

        description = cache.getIfPresent(id);
        if (description == null || !getMissingLocales(description, locales).isEmpty()) {
            throw new CacheItemNotFoundException(
                "After successful market fetch, the cache item should be complete[" +
                id +
                "], but its missing"
            );
        }

        return description;
    }

    private void fetchMissingData(List<Locale> missingLocales) throws IllegalCacheStateException {
        Preconditions.checkNotNull(missingLocales);

        try {
            for (Locale missingLocale : missingLocales) {
                merge(missingLocale, dataProvider.getData(missingLocale));
            }
            initStaticMappingsEnrichment();
        } catch (DataProviderException e) {
            throw new IllegalCacheStateException(
                "An error occurred while fetching invariant descriptors in [" + missingLocales + "]",
                e
            );
        }
    }

    private void merge(Locale locale, MarketDescriptions data) {
        Preconditions.checkNotNull(locale);
        Preconditions.checkNotNull(data);
        boolean createNew = fetchedLocales.isEmpty();

        data
            .getMarket()
            .forEach(market -> {
                String processingCacheItemId = String.valueOf(market.getId());
                MarketDescriptionCi cachedItem = cache.getIfPresent(processingCacheItemId);
                if (createNew || cachedItem == null) {
                    cachedItem =
                        new MarketDescriptionCi(
                            market,
                            mappingValidatorFactory,
                            locale,
                            SdkHelper.InVariantMarketListCache
                        );
                    cache.put(processingCacheItemId, cachedItem);
                } else {
                    cachedItem.merge(market, locale);
                }
            });
        if (!fetchedLocales.contains(locale)) {
            fetchedLocales.add(locale);
        }
    }

    private void initStaticMappingsEnrichment() {
        try {
            MarketDescriptions data = additionalMappingsProvider.getData();
            if (data == null || data.getMarket() == null) {
                if (additionalMappingsProvider.logErrors()) {
                    logger.warn("Additional mappings provider returned null data");
                }
                return;
            }

            enrichStaticMappings(data.getMarket());
        } catch (Exception e) {
            if (additionalMappingsProvider.logErrors()) {
                logger.warn(
                    "An exception occurred while enriching static mappings with additional mappings, exc:",
                    e
                );
            }
        }
    }

    private void enrichStaticMappings(List<DescMarket> markets) {
        Preconditions.checkNotNull(markets);

        markets.forEach(m -> {
            String processingCacheItemId = String.valueOf(m.getId());
            MarketDescriptionCi cachedItem = cache.getIfPresent(processingCacheItemId);
            if (cachedItem == null) {
                if (additionalMappingsProvider.logErrors()) {
                    logger.warn("Handling additional mappings for unknown market: {}", m.getId());
                }
                return;
            }

            if (
                m.getMappings() == null ||
                m.getMappings().getMapping() == null ||
                m.getMappings().getMapping().isEmpty()
            ) {
                if (additionalMappingsProvider.logErrors()) {
                    logger.warn("Handling empty/null additional mappings for market: {}", m.getId());
                }
                return;
            }

            cachedItem.mergeAdditionalMappings(m.getMappings().getMapping());
        });
    }

    private void additionalMappingsChanged() {
        logger.info("Additional mappings callback invoked - triggering cache refresh");

        onTimerElapsed();
    }

    private List<Locale> getMissingLocales(MarketDescriptionCi item, List<Locale> requiredLocales) {
        Preconditions.checkNotNull(requiredLocales);
        Preconditions.checkArgument(!requiredLocales.isEmpty());

        if (item == null) {
            return requiredLocales;
        }

        return SdkHelper.findMissingLocales(item.getCachedLocales(), requiredLocales);
    }
}
