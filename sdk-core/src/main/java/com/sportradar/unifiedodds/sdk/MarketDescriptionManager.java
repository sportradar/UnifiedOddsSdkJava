/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */

package com.sportradar.unifiedodds.sdk;

import com.sportradar.unifiedodds.sdk.entities.markets.MarketDescription;
import com.sportradar.unifiedodds.sdk.entities.markets.MarketMappingData;
import com.sportradar.unifiedodds.sdk.oddsentities.Producer;

import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * Used to obtain information about available markets and get translations for markets and outcomes
 * including outrights
 */
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
}
