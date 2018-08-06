/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */

package com.sportradar.unifiedodds.sdk.impl.oddsentities.markets;

import com.google.common.base.Preconditions;
import com.sportradar.unifiedodds.sdk.entities.markets.OutcomeMappingData;
import com.sportradar.unifiedodds.sdk.impl.markets.FlexMarketHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Locale;
import java.util.Map;

/**
 * A flex adjustment class used to modify the "raw" outcome mapping data
 */
class FlexAdjustedOutcomeMappingDataImpl extends AdjustmentOutcomeMappingDataImpl implements OutcomeMappingData {
    private static final Logger logger = LoggerFactory.getLogger(FlexAdjustedOutcomeMappingDataImpl.class);

    private final Map<String, String> marketSpecifiers;

    FlexAdjustedOutcomeMappingDataImpl(OutcomeMappingData outcomeMappingData, Map<String, String> marketSpecifiers) {
        super(outcomeMappingData);

        Preconditions.checkNotNull(marketSpecifiers);

        this.marketSpecifiers = marketSpecifiers;
    }

    /**
     * Returns the adjusted mapped outcome name
     *
     * @param locale - the {@link Locale} in which the name should be returned
     * @return - the translated mapped outcome name
     */
    @Override
    public String getProducerOutcomeName(Locale locale) {
        String producerOutcomeName = super.getProducerOutcomeName(locale);

        try {
            return FlexMarketHelper.getName(producerOutcomeName, marketSpecifiers);
        } catch (IllegalArgumentException e) {
            logger.error("Flex market score adjustment failed for outcomeId: {}, producerOutcomeName: {}. Returning un-adjusted outcome name. Exc:", getOutcomeId(), producerOutcomeName, e);
            return producerOutcomeName;
        }
    }
}
