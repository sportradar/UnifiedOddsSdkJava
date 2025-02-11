/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */

package com.sportradar.unifiedodds.sdk.internal.impl.oddsentities.markets;

import com.google.common.base.Preconditions;
import com.sportradar.unifiedodds.sdk.entities.markets.OutcomeMappingData;
import java.util.Locale;

/**
 * An abstract class used as a base for outcome mapping adjustments
 */
abstract class AdjustmentOutcomeMappingDataImpl implements OutcomeMappingData {

    private final OutcomeMappingData outcomeMappingData;

    AdjustmentOutcomeMappingDataImpl(OutcomeMappingData outcomeMappingData) {
        Preconditions.checkNotNull(outcomeMappingData);

        this.outcomeMappingData = outcomeMappingData;
    }

    /**
     * Returns the associated outcome identifier
     *
     * @return - the associated outcome identifier
     */
    @Override
    public String getOutcomeId() {
        return outcomeMappingData.getOutcomeId();
    }

    /**
     * Returns the mapped outcome id
     *
     * @return - the mapped outcome id
     */
    @Override
    public String getProducerOutcomeId() {
        return outcomeMappingData.getOutcomeId();
    }

    /**
     * Returns the mapped outcome name
     *
     * @param locale - the {@link Locale} in which the name should be returned
     * @return - the translated mapped outcome name
     */
    @Override
    public String getProducerOutcomeName(Locale locale) {
        return outcomeMappingData.getProducerOutcomeName(locale);
    }
}
