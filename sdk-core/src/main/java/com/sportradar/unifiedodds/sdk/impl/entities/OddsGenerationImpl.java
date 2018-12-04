/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */

package com.sportradar.unifiedodds.sdk.impl.entities;

import com.google.common.base.Preconditions;
import com.sportradar.uf.datamodel.UFOddsGenerationProperties;
import com.sportradar.unifiedodds.sdk.oddsentities.OddsGeneration;

/**
 * Represents odds generation properties
 */
public class OddsGenerationImpl implements OddsGeneration {

    private final Double expectedTotals;
    private final Double expectedSupremacy;

    public OddsGenerationImpl(UFOddsGenerationProperties oddsGenerationProperties)
    {
        Preconditions.checkNotNull(oddsGenerationProperties);

        expectedTotals = oddsGenerationProperties.getExpectedTotals();
        expectedSupremacy = oddsGenerationProperties.getExpectedSupremacy();
    }

    /**
     * Returns the expected totals (how many goals are expected in total?)
     *
     * @return the expected totals (how many goals are expected in total?)
     */
    @Override
    public Double getExpectedTotals() {
        return expectedTotals;
    }

    /**
     * Returns the expected supremacy (how big is the expected goal supremacy)
     *
     * @return the expected supremacy (how big is the expected goal supremacy)
     */
    @Override
    public Double getExpectedSupremacy() {
        return expectedSupremacy;
    }
}
