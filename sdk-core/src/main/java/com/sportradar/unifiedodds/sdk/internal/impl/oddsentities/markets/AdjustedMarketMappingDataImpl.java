/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */

package com.sportradar.unifiedodds.sdk.internal.impl.oddsentities.markets;

import com.google.common.base.Preconditions;
import com.sportradar.unifiedodds.sdk.entities.markets.MarketMappingData;
import com.sportradar.unifiedodds.sdk.entities.markets.OutcomeMappingData;
import com.sportradar.utils.Urn;
import java.util.Map;
import java.util.Set;

/**
 * An abstract class used as a base for mapping adjustments
 */
abstract class AdjustedMarketMappingDataImpl implements MarketMappingData {

    private final MarketMappingData mapping;

    AdjustedMarketMappingDataImpl(MarketMappingData mapping) {
        Preconditions.checkNotNull(mapping);

        this.mapping = mapping;
    }

    /**
     * The associated producer ids
     *
     * @return the producer ids for which the mappings are valid
     */
    @Override
    public Set<Integer> getProducerIds() {
        return mapping.getProducerIds();
    }

    /**
     * The sport id for which the mapping data is valid
     *
     * @return the sport id for which the mapping data is valid
     */
    @Override
    public Urn getSportId() {
        return mapping.getSportId();
    }

    /**
     * Returns the market id
     *
     * @return the market id
     */
    @Override
    public String getMarketId() {
        return mapping.getMarketId();
    }

    /**
     * Returns the market type id
     *
     * @return the market type id
     */
    @Override
    public int getMarketTypeId() {
        return mapping.getMarketTypeId();
    }

    /**
     * Returns the market sub type id
     *
     * @return the market sub type id
     */
    @Override
    public Integer getMarketSubTypeId() {
        return mapping.getMarketSubTypeId();
    }

    /**
     * Returns the "special odds value"
     *
     * @return the "special odds value"
     */
    @Override
    public String getSovTemplate() {
        return mapping.getSovTemplate();
    }

    /**
     * Returns a {@link Map} of valid outcome mappings for this {@link MarketMappingData}
     *
     * @return a {@link Map} of valid outcome mappings
     */
    @Override
    public Map<String, OutcomeMappingData> getOutcomeMappings() {
        return mapping.getOutcomeMappings();
    }

    /**
     * Returns a {@link String} describing for which specifier values the mapping is valid
     *
     * @return a {@link String} describing for which specifier values the mapping is valid
     */
    @Override
    public String getValidFor() {
        return mapping.getValidFor();
    }

    /**
     * Indicates if the mapping data is usable with the provided values
     *
     * @param producerId a valid producerId
     * @param sportId    a valid {@link Urn} sport identifier
     * @param specifiers a {@link Map} of feed message specifiers
     * @return <code>true</code> if the mapping data is valid for the provided values; otherwise <code>false</code>
     */
    @Override
    public boolean canMap(int producerId, Urn sportId, Map<String, String> specifiers) {
        return mapping.canMap(producerId, sportId, specifiers);
    }
}
