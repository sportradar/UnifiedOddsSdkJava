/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */

package com.sportradar.unifiedodds.sdk.impl.markets;

import com.google.common.base.Preconditions;
import com.google.common.base.Stopwatch;
import com.google.common.collect.Lists;
import com.google.inject.Inject;
import com.google.inject.name.Named;
import com.sportradar.unifiedodds.sdk.ExceptionHandlingStrategy;
import com.sportradar.unifiedodds.sdk.LoggerDefinitions;
import com.sportradar.unifiedodds.sdk.MarketDescriptionManager;
import com.sportradar.unifiedodds.sdk.SDKInternalConfiguration;
import com.sportradar.unifiedodds.sdk.caching.markets.InvariantMarketDescriptionCache;
import com.sportradar.unifiedodds.sdk.caching.markets.MarketDescriptionCache;
import com.sportradar.unifiedodds.sdk.caching.markets.MarketDescriptionProvider;
import com.sportradar.unifiedodds.sdk.caching.markets.VariantDescriptionCache;
import com.sportradar.unifiedodds.sdk.entities.markets.MarketDescription;
import com.sportradar.unifiedodds.sdk.entities.markets.MarketMappingData;
import com.sportradar.unifiedodds.sdk.exceptions.ObjectNotFoundException;
import com.sportradar.unifiedodds.sdk.exceptions.internal.CacheItemNotFoundException;
import com.sportradar.unifiedodds.sdk.exceptions.internal.IllegalCacheStateException;
import com.sportradar.unifiedodds.sdk.oddsentities.Market;
import com.sportradar.unifiedodds.sdk.oddsentities.Producer;
import com.sportradar.utils.SdkHelper;
import java.util.*;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@SuppressWarnings(
    {
        "ClassFanOutComplexity",
        "ConstantName",
        "CyclomaticComplexity",
        "IllegalCatch",
        "LineLength",
        "MethodLength",
        "VariableDeclarationUsageDistance",
    }
)
public class MarketManagerImpl implements MarketDescriptionManager {

    private static final Logger logger = LoggerFactory.getLogger(MarketManagerImpl.class);
    private static final Logger interactionLogger = LoggerFactory.getLogger(
        LoggerDefinitions.UFSdkClientInteractionLog.class
    );
    private final SDKInternalConfiguration config;
    private final MarketDescriptionProvider marketDescriptionProvider;
    private final InvariantMarketDescriptionCache invariantMarketDescriptionCache;
    private final VariantDescriptionCache variantMarketDescriptionListCache;
    private final MarketDescriptionCache variantMarketDescriptionCache;
    private final ExceptionHandlingStrategy exceptionHandlingStrategy;

    @Inject
    public MarketManagerImpl(
        SDKInternalConfiguration config,
        MarketDescriptionProvider marketDescriptionProvider,
        @Named("InvariantMarketCache") InvariantMarketDescriptionCache invariantMarketDescriptionCache,
        VariantDescriptionCache variantMarketDescriptionListCache,
        @Named("VariantMarketCache") MarketDescriptionCache variantMarketDescriptionCache
    ) {
        Preconditions.checkNotNull(config);
        Preconditions.checkNotNull(marketDescriptionProvider);
        Preconditions.checkNotNull(invariantMarketDescriptionCache);
        Preconditions.checkNotNull(variantMarketDescriptionCache);

        this.config = config;
        this.marketDescriptionProvider = marketDescriptionProvider;
        this.invariantMarketDescriptionCache = invariantMarketDescriptionCache;
        this.variantMarketDescriptionListCache = variantMarketDescriptionListCache;
        this.variantMarketDescriptionCache = variantMarketDescriptionCache;
        this.exceptionHandlingStrategy = config.getExceptionHandlingStrategy();
    }

    /**
     * Get the list of all available market descriptions.
     *
     * @return a list of available market descriptions
     */
    @Override
    public List<MarketDescription> getMarketDescriptions() {
        try {
            return invariantMarketDescriptionCache.getAllInvariantMarketDescriptions(
                Lists.newArrayList(config.getDefaultLocale())
            );
        } catch (CacheItemNotFoundException | IllegalCacheStateException e) {
            if (exceptionHandlingStrategy == ExceptionHandlingStrategy.Throw) {
                throw new ObjectNotFoundException("Market descriptions could not be provided", e);
            } else {
                logger.warn("Market descriptions with the default locale could not be provided, ex:", e);
                return null;
            }
        }
    }

    @Override
    public List<MarketDescription> getMarketDescriptions(Locale locale) {
        try {
            return invariantMarketDescriptionCache.getAllInvariantMarketDescriptions(
                Lists.newArrayList(locale)
            );
        } catch (CacheItemNotFoundException | IllegalCacheStateException e) {
            if (exceptionHandlingStrategy == ExceptionHandlingStrategy.Throw) {
                throw new ObjectNotFoundException(
                    "Market descriptions(" + locale + ") could not be provided",
                    e
                );
            } else {
                logger.warn("Market descriptions with the {} locale could not be provided, ex:", locale, e);
                return null;
            }
        }
    }

    @Override
    public List<MarketMappingData> getMarketMapping(int marketId, Producer producer) {
        MarketDescription marketDescriptor;
        try {
            marketDescriptor =
                marketDescriptionProvider.getMarketDescription(
                    marketId,
                    null,
                    Lists.newArrayList(config.getDefaultLocale()),
                    false
                );
        } catch (CacheItemNotFoundException e) {
            if (exceptionHandlingStrategy == ExceptionHandlingStrategy.Throw) {
                throw new ObjectNotFoundException(
                    "Market mappings for " + marketId + " could not be provided",
                    e
                );
            } else {
                logger.warn("Market mappings for the marketId: {} could not be provided, ex:", marketId, e);
                return null;
            }
        }

        if (marketDescriptor.getMappings() == null) {
            return Collections.emptyList();
        }

        List<MarketMappingData> mappings = marketDescriptor
            .getMappings()
            .stream()
            .filter(m -> m.getProducerIds().contains(producer.getId()))
            .collect(Collectors.toList());

        if (mappings.size() > 1) {
            for (MarketMappingData mapping : mappings) {
                if (mapping.getMarketId().equals(String.valueOf(marketDescriptor.getId()))) {
                    return Arrays.asList(mapping);
                }
            }
            logger.warn(
                "MarketId:{}, producer:{}, sportId:{}, specifiers={} has too many mappings [{}].",
                marketDescriptor.getId(),
                producer.getId(),
                0,
                SdkHelper.specifierKeyListToString(marketDescriptor.getSpecifiers()),
                mappings.size()
            );
            int i = 0;
            for (MarketMappingData mapping : mappings) {
                logger.debug(
                    "MarketId:{}, producer:{}, sportId:{}, specifiers={}, mapping[{}]: {}",
                    marketDescriptor.getId(),
                    producer.getId(),
                    0,
                    SdkHelper.specifierKeyListToString(marketDescriptor.getSpecifiers()),
                    i,
                    mapping
                );
                i++;
            }
        }

        return mappings;
    }

    @Override
    public List<MarketMappingData> getMarketMapping(
        int marketId,
        Map<String, String> specifiers,
        Producer producer
    ) {
        MarketDescription marketDescriptor;
        try {
            marketDescriptor =
                marketDescriptionProvider.getMarketDescription(
                    marketId,
                    specifiers,
                    Lists.newArrayList(config.getDefaultLocale()),
                    false
                );
        } catch (CacheItemNotFoundException e) {
            if (exceptionHandlingStrategy == ExceptionHandlingStrategy.Throw) {
                throw new ObjectNotFoundException(
                    "Market mappings for " + marketId + " could not be provided, specifiers: " + specifiers,
                    e
                );
            } else {
                logger.warn(
                    "Market mappings for the marketId: {} could not be provided, specifiers: [{}]. ex:",
                    marketId,
                    specifiers,
                    e
                );
                return null;
            }
        }

        if (marketDescriptor.getMappings() == null) {
            return Collections.emptyList();
        }

        List<MarketMappingData> mappings = marketDescriptor
            .getMappings()
            .stream()
            .filter(m -> m.getProducerIds().contains(producer.getId()))
            .collect(Collectors.toList());

        if (mappings.size() > 1) {
            for (MarketMappingData mapping : mappings) {
                if (mapping.getMarketId().equals(String.valueOf(marketDescriptor.getId()))) {
                    return Arrays.asList(mapping);
                }
            }
            logger.warn(
                "MarketId:{}, producer:{}, sportId:{}, specifiers={} has too many mappings [{}].",
                marketDescriptor.getId(),
                producer.getId(),
                0,
                SdkHelper.specifierKeyListToString(marketDescriptor.getSpecifiers()),
                mappings.size()
            );
            int i = 0;
            for (MarketMappingData mapping : mappings) {
                logger.debug(
                    "MarketId:{}, producer:{}, sportId:{}, specifiers={}, mapping[{}]: {}",
                    marketDescriptor.getId(),
                    producer.getId(),
                    0,
                    SdkHelper.specifierKeyListToString(marketDescriptor.getSpecifiers()),
                    i,
                    mapping
                );
                i++;
            }
        }

        return mappings;
    }

    /**
     * Loads the invariant and variant list of market descriptions from the Sports API
     *
     * @return true if the action succeeded
     */
    @Override
    public boolean loadMarketDescriptions() {
        boolean a = invariantMarketDescriptionCache.loadMarketDescriptions();
        boolean b = variantMarketDescriptionListCache.loadMarketDescriptions();
        return a && b;
    }

    /**
     * Deletes the variant market description from cache
     * @param marketId the market id used to delete variant market description from the cache
     * @param variantValue the variant value used to delete variant market description from the cache
     */
    @Override
    public void deleteVariantMarketDescriptionFromCache(int marketId, String variantValue) {
        variantMarketDescriptionCache.deleteCacheItem(marketId, variantValue);
    }

    /**
     * Prefetch variant market descriptions in parallel
     * Useful when list of markets on feed message contains many variant markets which calls single variant market description api endpoint
     *
     * @param markets the list of markets to be checked and fetched
     * @param onlyVariantMarkets prefetch only variant markets or all markets in the list (default: true)
     * @param threadPoolSize the size of the fixed thread pool (default: 100)
     * @return the time needed for processing in ms
     */
    @Override
    public long parallelPrefetchVariantMarketDescriptions(
        List<? extends Market> markets,
        boolean onlyVariantMarkets,
        int threadPoolSize
    ) {
        if (markets == null || markets.isEmpty()) {
            interactionLogger.info("Prefetching variant market description called for 0 markets");
            return 0;
        }

        Stopwatch stopwatch = Stopwatch.createStarted();
        ExecutorService threadPool = Executors.newFixedThreadPool(threadPoolSize);
        List<Callable<String>> tasks = new ArrayList<>();
        for (Market market : markets) {
            if (onlyVariantMarkets) {
                if (market.getSpecifiers() != null && market.getSpecifiers().containsKey("variant")) {
                    for (Locale l : config.getDesiredLocales()) {
                        tasks.add(() -> market.getName(l));
                    }
                }
            } else {
                for (Locale l : config.getDesiredLocales()) {
                    tasks.add(() -> market.getName(l));
                }
            }
        }
        try {
            interactionLogger.info(
                "Prefetching variant market descriptions called for {} markets. Tasks: {}.",
                markets.size(),
                tasks.size()
            );
            threadPool.invokeAll(tasks);
        } catch (InterruptedException e) {
            e.printStackTrace();
            Thread.currentThread().interrupt();
        } catch (Exception ex) {
            interactionLogger.error("Error prefetching variant market descriptions.", ex);
        }

        threadPool.shutdown();
        stopwatch.stop();
        interactionLogger.info(
            "Prefetching variant market descriptions for {} markets. Tasks: {}. Took {} ms.",
            markets.size(),
            tasks.size(),
            stopwatch.elapsed(TimeUnit.MILLISECONDS)
        );
        return stopwatch.elapsed(TimeUnit.MILLISECONDS);
    }
}
