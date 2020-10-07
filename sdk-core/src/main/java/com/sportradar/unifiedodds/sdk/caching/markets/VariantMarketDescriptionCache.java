/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */

package com.sportradar.unifiedodds.sdk.caching.markets;

import com.google.common.base.Preconditions;
import com.google.common.base.Strings;
import com.google.common.cache.Cache;
import com.sportradar.uf.sportsapi.datamodel.DescMarket;
import com.sportradar.uf.sportsapi.datamodel.MarketDescriptions;
import com.sportradar.unifiedodds.sdk.caching.ci.markets.MarketDescriptionCI;
import com.sportradar.unifiedodds.sdk.entities.markets.MarketDescription;
import com.sportradar.unifiedodds.sdk.exceptions.internal.CacheItemNotFoundException;
import com.sportradar.unifiedodds.sdk.exceptions.internal.DataProviderException;
import com.sportradar.unifiedodds.sdk.exceptions.internal.IllegalCacheStateException;
import com.sportradar.unifiedodds.sdk.impl.DataProvider;
import com.sportradar.unifiedodds.sdk.impl.markets.MappingValidatorFactory;
import com.sportradar.unifiedodds.sdk.impl.markets.MarketDescriptionImpl;
import com.sportradar.utils.SdkHelper;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Single variant market description cache
 */
public class VariantMarketDescriptionCache implements MarketDescriptionCache {
    private final Cache<String, MarketDescriptionCI> cache;
    private final DataProvider<MarketDescriptions> dataProvider;
    private final MappingValidatorFactory mappingValidatorFactory;
    private final ReentrantLock lock = new ReentrantLock();
    private final boolean simpleVariantCaching;
    private Map<String,Date> fetchedVariants = new ConcurrentHashMap<>();
    private Date lastTimeFetchedVariantsWereCleared;

    public VariantMarketDescriptionCache(Cache<String, MarketDescriptionCI> cache,
                                         DataProvider<MarketDescriptions> dataProvider,
                                         MappingValidatorFactory mappingValidatorFactory,
                                         boolean simpleVariantCaching) {
        Preconditions.checkNotNull(cache);
        Preconditions.checkNotNull(dataProvider);
        Preconditions.checkNotNull(mappingValidatorFactory);

        this.cache = cache;
        this.dataProvider = dataProvider;
        this.mappingValidatorFactory = mappingValidatorFactory;
        this.simpleVariantCaching = simpleVariantCaching;
        this.lastTimeFetchedVariantsWereCleared = new Date();
    }

    @Override
    public MarketDescription getMarketDescriptor(int marketId, String variant, List<Locale> locales) throws CacheItemNotFoundException, IllegalCacheStateException {
        Preconditions.checkArgument(marketId > 0);
        Preconditions.checkArgument(!Strings.isNullOrEmpty(variant));
        Preconditions.checkNotNull(locales);
        Preconditions.checkArgument(!locales.isEmpty());

        MarketDescriptionCI marketCI;
        try {
            marketCI = cache.get(getCacheKey(marketId, variant), () -> loadMarketDescriptorData(null, marketId, variant, locales));
        } catch (ExecutionException e) {
            throw new CacheItemNotFoundException("The requested market descriptor could not be found", e);
        }

        List<Locale> missingLocales;
        try {
            lock.lock();
            missingLocales = getMissingLocales(marketCI, locales);
        } finally {
            lock.unlock();
        }

        if (!missingLocales.isEmpty()) {
            try {
                lock.lock();
                if(!isFetchingAllowed(marketId, variant, missingLocales))
                {
                    return new MarketDescriptionImpl(marketCI, locales);
                }
                missingLocales = getMissingLocales(marketCI, locales);
                if (!missingLocales.isEmpty()) {

                    loadMarketDescriptorData(marketCI, marketId, variant, missingLocales);

                    for (Locale l : missingLocales) {
                        fetchedVariants.put(getFetchedVariantsKey(marketId, variant, l), new Date());
                    }
                }
            } finally {
                lock.unlock();
            }
        }

        return new MarketDescriptionImpl(marketCI, locales);
    }

    @Override
    public boolean loadMarketDescriptions() {
        return true;
    }

    @Override
    public void deleteCacheItem(int marketId, String variant) {
        String cacheId  = getCacheKey(marketId, variant);
        cache.invalidate(cacheId);
    }

    @Override
    public void updateCacheItem(int marketId, String variant) {
        String cacheId  = getCacheKey(marketId, variant);
        MarketDescriptionCI description = cache.getIfPresent(cacheId);
        if (description != null) {
            description.setLastDataReceived(new Date());
        }
    }

    private MarketDescriptionCI loadMarketDescriptorData(MarketDescriptionCI existingMarketDescriptor, int marketId, String variant, List<Locale> locales) throws IllegalCacheStateException {
        Preconditions.checkNotNull(locales);
        Preconditions.checkArgument(!Strings.isNullOrEmpty(variant));
        Preconditions.checkArgument(!locales.isEmpty());

        try {
            for (Locale mLoc : locales) {
                MarketDescriptions data = dataProvider.getData(mLoc, String.valueOf(marketId), variant);
                if (data == null || data.getMarket().size() != 1) {
                    throw new IllegalCacheStateException("Received variant market[" + marketId + " " + variant + "] response with invalid market entry count");
                }

                DescMarket descMarket = data.getMarket().get(0);
                if (existingMarketDescriptor == null) {
                    existingMarketDescriptor = new MarketDescriptionCI(descMarket, mappingValidatorFactory, mLoc, SdkHelper.VariantMarketSingleCache);
                } else {
                    existingMarketDescriptor.merge(descMarket, mLoc);
                }
            }

            return existingMarketDescriptor;
        } catch (DataProviderException ex) {
            throw new IllegalCacheStateException("An error occurred while fetching variant market[" + marketId + " " + variant + "] data", ex);
        }
    }

    private String getCacheKey(int id, String variant) {
        if (simpleVariantCaching) {
            return variant;
        }

        return id + "_" + variant;
    }

    private List<Locale> getMissingLocales(MarketDescriptionCI item, List<Locale> requiredLocales) {
        Preconditions.checkNotNull(requiredLocales);
        Preconditions.checkArgument(!requiredLocales.isEmpty());

        if (item == null) {
            return requiredLocales;
        }

        return SdkHelper.findMissingLocales(item.getCachedLocales(), requiredLocales);
    }

    private boolean isFetchingAllowed(int marketId, String variant, List<Locale> locales)
    {
        for (Locale l : locales) {
        if(isFetchingAllowed(marketId, variant, l))
        {
            return true;
        }
    }
        return false;
    }

    private boolean isFetchingAllowed(int marketId, String variant, Locale locale)
    {
        if(fetchedVariants.size() > 1000)
        {
            clearFetchedVariants();
        }

        String cacheKey = getFetchedVariantsKey(marketId, variant, locale);
        if (!fetchedVariants.containsKey(cacheKey))
        {
            return true;
        }
        Date date = fetchedVariants.get(cacheKey);
        if (SdkHelper.getTimeDifferenceInSeconds(new Date(), date) > 30)
        {
            return true;
        }
        clearFetchedVariants();

        return false;
    }

    private String getFetchedVariantsKey(int marketId, String variant, Locale locale)
    {
        return getCacheKey(marketId, variant) + "_" + locale;
    }

    /**
     * clear records from fetchedVariants once a min
     */
    private void clearFetchedVariants()
    {
        if (SdkHelper.getTimeDifferenceInSeconds(new Date(), lastTimeFetchedVariantsWereCleared) > 60)
        {
            Set<String> keys = fetchedVariants.keySet();

            for (String key : keys) {
                Date currDate = fetchedVariants.get(key);
                if (SdkHelper.getTimeDifferenceInSeconds(new Date(), currDate) > 30) {
                    fetchedVariants.remove(key);
                }
            }
            lastTimeFetchedVariantsWereCleared = new Date();
        }
    }
}
