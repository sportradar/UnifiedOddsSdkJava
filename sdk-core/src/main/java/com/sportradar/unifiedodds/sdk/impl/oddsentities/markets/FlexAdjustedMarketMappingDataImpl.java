/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */

package com.sportradar.unifiedodds.sdk.impl.oddsentities.markets;

import com.sportradar.unifiedodds.sdk.entities.markets.MarketMappingData;
import com.sportradar.unifiedodds.sdk.entities.markets.OutcomeMappingData;
import java.util.Map;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A flex adjustment class used to modify the "raw" mapping data
 */
@SuppressWarnings({ "ConstantName" })
class FlexAdjustedMarketMappingDataImpl extends AdjustedMarketMappingDataImpl implements MarketMappingData {

    private static final Logger logger = LoggerFactory.getLogger(FlexAdjustedMarketMappingDataImpl.class);

    private final Map<String, String> marketSpecifiers;

    FlexAdjustedMarketMappingDataImpl(MarketMappingData mapping, Map<String, String> marketSpecifiers) {
        super(mapping);
        this.marketSpecifiers = marketSpecifiers;
    }

    /**
     * Returns a {@link Map} of valid adjusted outcome mappings for this {@link MarketMappingData}
     *
     * @return a {@link Map} of valid adjusted outcome mappings
     */
    @Override
    public Map<String, OutcomeMappingData> getOutcomeMappings() {
        if (marketSpecifiers == null) {
            logger.warn(
                "Processing mapping adjustments for a flex market without specifiers, outcome mappings will be skipped"
            );
            return super.getOutcomeMappings();
        }

        return super
            .getOutcomeMappings()
            .values()
            .stream()
            .map(mOutcome -> new FlexAdjustedOutcomeMappingDataImpl(mOutcome, marketSpecifiers))
            .collect(Collectors.toMap(AdjustmentOutcomeMappingDataImpl::getOutcomeId, v -> v));
    }
}
