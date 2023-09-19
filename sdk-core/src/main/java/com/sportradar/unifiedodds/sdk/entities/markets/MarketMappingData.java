/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */

package com.sportradar.unifiedodds.sdk.entities.markets;

import com.sportradar.utils.Urn;
import java.util.Map;
import java.util.Set;

/**
 * Defines methods used to access market mapping data provided by the API
 */
public interface MarketMappingData {
    /**
     * The associated producer ids
     *
     * @return the producer ids for which the mappings are valid
     */
    Set<Integer> getProducerIds();

    /**
     * The sport id for which the mapping data is valid
     *
     * @return the sport id for which the mapping data is valid
     */
    Urn getSportId();

    /**
     * Returns the market id
     *
     * @return the market id
     */
    String getMarketId();

    /**
     * Returns the market type id
     *
     * @return the market type id
     */
    int getMarketTypeId();

    /**
     * Returns the market sub type id
     *
     * @return the market sub type id
     */
    Integer getMarketSubTypeId();

    /**
     * Returns the "special odds value"
     *
     * @return the "special odds value"
     */
    String getSovTemplate();

    /**
     * Returns a {@link Map} of valid outcome mappings for this {@link MarketMappingData}
     *
     * @return a {@link Map} of valid outcome mappings
     */
    Map<String, OutcomeMappingData> getOutcomeMappings();

    /**
     * Returns a {@link String} describing for which specifier values the mapping is valid
     *
     * @return a {@link String} describing for which specifier values the mapping is valid
     */
    String getValidFor();

    /**
     * Indicates if the mapping data is usable with the provided values
     *
     * @param producerId a valid producerId
     * @param sportId a valid {@link Urn} sport identifier
     * @param specifiers a {@link Map} of feed message specifiers
     * @return <code>true</code> if the mapping data is valid for the provided values; otherwise <code>false</code>
     */
    boolean canMap(int producerId, Urn sportId, Map<String, String> specifiers);
}
