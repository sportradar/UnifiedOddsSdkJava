/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */

package com.sportradar.unifiedodds.sdk.impl.markets;

import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;
import com.google.inject.Inject;
import com.google.inject.name.Named;
import com.sportradar.unifiedodds.sdk.ExceptionHandlingStrategy;
import com.sportradar.unifiedodds.sdk.MarketDescriptionManager;
import com.sportradar.unifiedodds.sdk.SDKInternalConfiguration;
import com.sportradar.unifiedodds.sdk.caching.markets.InvariantMarketDescriptionCache;
import com.sportradar.unifiedodds.sdk.caching.markets.MarketDescriptionProvider;
import com.sportradar.unifiedodds.sdk.entities.markets.MarketDescription;
import com.sportradar.unifiedodds.sdk.entities.markets.MarketMappingData;
import com.sportradar.unifiedodds.sdk.exceptions.ObjectNotFoundException;
import com.sportradar.unifiedodds.sdk.exceptions.internal.CacheItemNotFoundException;
import com.sportradar.unifiedodds.sdk.exceptions.internal.IllegalCacheStateException;
import com.sportradar.unifiedodds.sdk.oddsentities.Producer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.stream.Collectors;

public class MarketManagerImpl implements MarketDescriptionManager {
    private final static Logger logger = LoggerFactory.getLogger(MarketManagerImpl.class);
    private final SDKInternalConfiguration config;
    private final MarketDescriptionProvider marketDescriptionProvider;
    private final InvariantMarketDescriptionCache invariantMarketDescriptionCache;
    private final ExceptionHandlingStrategy exceptionHandlingStrategy;

    @Inject
    public MarketManagerImpl(SDKInternalConfiguration config, MarketDescriptionProvider marketDescriptionProvider, @Named("InvariantMarketCache") InvariantMarketDescriptionCache invariantMarketDescriptionCache) {
        Preconditions.checkNotNull(config);
        Preconditions.checkNotNull(marketDescriptionProvider);
        Preconditions.checkNotNull(invariantMarketDescriptionCache);

        this.config = config;
        this.marketDescriptionProvider = marketDescriptionProvider;
        this.invariantMarketDescriptionCache = invariantMarketDescriptionCache;
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
            return invariantMarketDescriptionCache.getAllInvariantMarketDescriptions(Lists.newArrayList(config.getDefaultLocale()));
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
            return invariantMarketDescriptionCache.getAllInvariantMarketDescriptions(Lists.newArrayList(locale));
        } catch (CacheItemNotFoundException | IllegalCacheStateException e) {
            if (exceptionHandlingStrategy == ExceptionHandlingStrategy.Throw) {
                throw new ObjectNotFoundException("Market descriptions(" + locale+ ") could not be provided", e);
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
            marketDescriptor = marketDescriptionProvider.getMarketDescription(marketId, null, Lists.newArrayList(config.getDefaultLocale()), false);
        } catch (CacheItemNotFoundException e) {
            if (exceptionHandlingStrategy == ExceptionHandlingStrategy.Throw) {
                throw new ObjectNotFoundException("Market mappings for " + marketId+ " could not be provided", e);
            } else {
                logger.warn("Market mappings for the marketId: {} could not be provided, ex:", marketId, e);
                return null;
            }
        }

        return marketDescriptor.getMappings().stream()
                .filter(m -> m.getProducerIds().contains(producer.getId()))
                .collect(Collectors.toList());
    }

    @Override
    public List<MarketMappingData> getMarketMapping(int marketId, Map<String, String> specifiers, Producer producer) {
        MarketDescription marketDescriptor;
        try {
            marketDescriptor = marketDescriptionProvider.getMarketDescription(marketId, specifiers, Lists.newArrayList(config.getDefaultLocale()), false);
        } catch (CacheItemNotFoundException e) {
            if (exceptionHandlingStrategy == ExceptionHandlingStrategy.Throw) {
                throw new ObjectNotFoundException("Market mappings for " + marketId+ " could not be provided, specifiers: " + specifiers, e);
            } else {
                logger.warn("Market mappings for the marketId: {} could not be provided, specifiers: [{}]. ex:", marketId, specifiers, e);
                return null;
            }
        }

        return marketDescriptor.getMappings().stream()
                .filter(m -> m.getProducerIds().contains(producer.getId()))
                .collect(Collectors.toList());
    }
}
