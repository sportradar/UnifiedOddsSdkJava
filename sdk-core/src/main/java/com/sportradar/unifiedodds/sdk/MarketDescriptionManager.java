/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */

package com.sportradar.unifiedodds.sdk;

import com.sportradar.unifiedodds.sdk.entities.markets.MarketDescription;
import com.sportradar.unifiedodds.sdk.entities.markets.MarketMappingData;
import com.sportradar.unifiedodds.sdk.oddsentities.Market;
import com.sportradar.unifiedodds.sdk.oddsentities.Producer;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * Used to obtain information about available markets and get translations for markets and outcomes
 * including outrights
 */
@SuppressWarnings({ "LineLength", "MagicNumber" })
public interface MarketDescriptionManager {
    /**
     * Returns the list of all available static market descriptions.
     *
     * @return a list of available static market descriptions
     */
    List<MarketDescription> getMarketDescriptions();

    /**
     * Returns the list of all available static market descriptions in the provided {@link Locale}
     *
     * @param locale the language in which the market static descriptions should be translated
     * @return a list of available market descriptions in the provided {@link Locale}
     */
    List<MarketDescription> getMarketDescriptions(Locale locale);

    /**
     * Returns a list of available mappings for the provided marketId/producer combination
     *
     * @param marketId the id of the market for which you need the mapping
     * @param producer the producer for which you need the mapping
     * @return a list of valid mappings for the provided marketId/producer combination
     */
    List<MarketMappingData> getMarketMapping(int marketId, Producer producer);

    /**
     * Returns a list of available market mappings(including possible variant mappings) for the
     * provided marketId/producer combination
     *
     * @param marketId the id of the market for which you need the mapping
     * @param specifiers the associated market specifiers
     * @param producer the producer for which you need the mapping
     * @return a list of valid mappings for the provided marketId/producer combination
     */
    List<MarketMappingData> getMarketMapping(int marketId, Map<String, String> specifiers, Producer producer);

    /**
     * Loads the invariant and variant list of market descriptions from the Sports API
     * @return true if the action succeeded
     */
    boolean loadMarketDescriptions();

    /**
     * Deletes the variant market description from cache
     * @param marketId the market id used to delete variant market description from the cache
     * @param variantValue the variant value used to delete variant market description from the cache
     */
    void deleteVariantMarketDescriptionFromCache(int marketId, String variantValue);

    /**
     * Prefetch variant market descriptions in parallel
     * Useful when list of markets on feed message contains many variant markets which calls single variant market description api endpoint
     * Only call this if name of the market is needed. If only id part is, then this is not needed.
     *
     * @param markets the list of markets to be checked and fetched
     * @return the time needed for processing in ms
     */
    long parallelPrefetchVariantMarketDescriptions(List<? extends Market> markets);

    /**
     * Prefetch variant market descriptions in parallel
     * Useful when list of markets on feed message contains many variant markets which calls single variant market description api endpoint
     * Only call this if name of the market is needed. If only id part is, then this is not needed.
     *
     * @param markets the list of markets to be checked and fetched
     * @param onlyVariantMarkets prefetch only variant markets or all markets in the list (default: true)
     * @return the time needed for processing in ms
     */
    long parallelPrefetchVariantMarketDescriptions(
        List<? extends Market> markets,
        boolean onlyVariantMarkets
    );

    /**
     * Prefetch variant market descriptions in parallel
     * Useful when list of markets on feed message contains many variant markets which calls single variant market description api endpoint
     * Only call this if name of the market is needed. If only id part is, then this is not needed.
     *
     * @param markets the list of markets to be checked and fetched
     * @param onlyVariantMarkets prefetch only variant markets or all markets in the list (default: true)
     * @param threadPoolSize the size of the fixed thread pool (default: 100)
     * @return the time needed for processing in ms
     */
    long parallelPrefetchVariantMarketDescriptions(
        List<? extends Market> markets,
        boolean onlyVariantMarkets,
        int threadPoolSize
    );
}
