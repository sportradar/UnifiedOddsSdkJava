/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */

package com.sportradar.unifiedodds.sdk.caching.markets;

import com.google.common.base.Preconditions;
import com.google.common.cache.Cache;
import com.sportradar.uf.sportsapi.datamodel.DescVariant;
import com.sportradar.uf.sportsapi.datamodel.VariantDescriptions;
import com.sportradar.unifiedodds.sdk.caching.ci.markets.VariantDescriptionCi;
import com.sportradar.unifiedodds.sdk.exceptions.internal.CacheItemNotFoundException;
import com.sportradar.unifiedodds.sdk.exceptions.internal.DataProviderException;
import com.sportradar.unifiedodds.sdk.exceptions.internal.IllegalCacheStateException;
import com.sportradar.unifiedodds.sdk.impl.DataProvider;
import com.sportradar.unifiedodds.sdk.impl.SdkTaskScheduler;
import com.sportradar.unifiedodds.sdk.impl.markets.MappingValidatorFactory;
import com.sportradar.utils.SdkHelper;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantLock;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created on 14/12/2017.
 * // TODO @eti: Javadoc
 */
@SuppressWarnings(
    {
        "ConstantName",
        "IllegalCatch",
        "LambdaBodyLength",
        "LineLength",
        "MagicNumber",
        "MultipleStringLiterals",
        "NPathComplexity",
    }
)
public class VariantDescriptionCacheImpl implements VariantDescriptionCache {

    private static final Logger logger = LoggerFactory.getLogger(VariantDescriptionCacheImpl.class);

    private final Cache<String, VariantDescriptionCi> cache;
    private final DataProvider<VariantDescriptions> dataProvider;
    private final MappingValidatorFactory mappingValidatorFactory;
    private final List<Locale> prefetchLocales;
    private final List<Locale> fetchedLocales;
    private final ReentrantLock fetchLock = new ReentrantLock();
    private boolean hasTimerElapsedOnce;

    public VariantDescriptionCacheImpl(
        Cache<String, VariantDescriptionCi> cache,
        DataProvider<VariantDescriptions> dataProvider,
        MappingValidatorFactory mappingValidatorFactory,
        SdkTaskScheduler scheduler,
        List<Locale> prefetchLocales
    ) {
        Preconditions.checkNotNull(cache);
        Preconditions.checkNotNull(dataProvider);
        Preconditions.checkNotNull(mappingValidatorFactory);
        Preconditions.checkNotNull(scheduler);
        Preconditions.checkNotNull(prefetchLocales);

        this.cache = cache;
        this.dataProvider = dataProvider;
        this.mappingValidatorFactory = mappingValidatorFactory;
        this.prefetchLocales = prefetchLocales;
        this.fetchedLocales = Collections.synchronizedList(new ArrayList<>());

        scheduler.scheduleAtFixedRate(
            "VariantDescriptionsTask",
            this::onTimerElapsed,
            5,
            60 * 60 * 6L,
            TimeUnit.SECONDS
        );
    }

    @Override
    public VariantDescriptionCi getVariantDescription(String id, List<Locale> locales)
        throws IllegalCacheStateException, CacheItemNotFoundException {
        Preconditions.checkNotNull(id);
        Preconditions.checkNotNull(locales);

        return getVariantDescriptionInternal(id, locales);
    }

    private void onTimerElapsed() {
        logger.info("Executing variant market cache refresh");
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
            "Loading variant market descriptions for [{}] (timer).",
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
                "An error occurred while periodically fetching variant descriptions for languages [{}]",
                locales2fetch.stream().map(Locale::getLanguage).collect(Collectors.joining(",")),
                e
            );
        } finally {
            fetchLock.unlock();
        }
    }

    @Override
    public boolean loadMarketDescriptions() {
        try {
            fetchedLocales.clear();
            logger.debug(
                "Loading variant market descriptions for [{}] (user request).",
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

    private VariantDescriptionCi getVariantDescriptionInternal(String id, List<Locale> locales2fetch)
        throws CacheItemNotFoundException, IllegalCacheStateException {
        Preconditions.checkNotNull(id);
        Preconditions.checkNotNull(locales2fetch);

        VariantDescriptionCi ifPresent = cache.getIfPresent(id);
        if (ifPresent != null && getMissingLocales(ifPresent, locales2fetch).isEmpty()) {
            return ifPresent;
        }

        fetchLock.lock();
        try {
            ifPresent = cache.getIfPresent(id);
            if (ifPresent != null && getMissingLocales(ifPresent, locales2fetch).isEmpty()) {
                return ifPresent;
            }

            // validate the missing global locales so the SDK doesn't request infinite api requests, if a producer starts sending "unknown" markets
            List<Locale> missingGlobalLocales = SdkHelper.findMissingLocales(fetchedLocales, locales2fetch);
            if (!missingGlobalLocales.isEmpty()) {
                fetchMissingData(getMissingLocales(ifPresent, locales2fetch));
            }
        } finally {
            fetchLock.unlock();
        }

        ifPresent = cache.getIfPresent(id);
        if (ifPresent == null || !getMissingLocales(ifPresent, locales2fetch).isEmpty()) {
            throw new CacheItemNotFoundException(
                "After successful variant list fetch, the cache item should be complete[" +
                id +
                "], but its missing"
            );
        }

        return ifPresent;
    }

    private void fetchMissingData(List<Locale> missingLocales) throws IllegalCacheStateException {
        Preconditions.checkNotNull(missingLocales);

        try {
            for (Locale missingLocale : missingLocales) {
                merge(missingLocale, dataProvider.getData(missingLocale));
            }
        } catch (DataProviderException e) {
            throw new IllegalCacheStateException(
                "An error occurred while fetching variant descriptors in [" + missingLocales + "]",
                e
            );
        }
    }

    private void merge(Locale dataLocale, VariantDescriptions endpointData) {
        Preconditions.checkNotNull(dataLocale);
        Preconditions.checkNotNull(endpointData);
        boolean createNew = fetchedLocales.isEmpty();

        List<DescVariant> variant = endpointData.getVariant();
        variant.forEach(market -> {
            String id = market.getId();

            VariantDescriptionCi ifPresent = cache.getIfPresent(id);
            if (createNew || ifPresent == null) {
                ifPresent =
                    new VariantDescriptionCi(
                        market,
                        mappingValidatorFactory,
                        dataLocale,
                        SdkHelper.VariantMarketListCache
                    );
                cache.put(id, ifPresent);
            } else {
                ifPresent.merge(market, dataLocale);
            }
        });

        if (!fetchedLocales.contains(dataLocale)) {
            fetchedLocales.add(dataLocale);
        }
    }

    private List<Locale> getMissingLocales(VariantDescriptionCi item, List<Locale> requiredLocales) {
        Preconditions.checkNotNull(requiredLocales);
        Preconditions.checkArgument(!requiredLocales.isEmpty());

        if (item == null) {
            return requiredLocales;
        }

        return SdkHelper.findMissingLocales(item.getCachedLocales(), requiredLocales);
    }
}
