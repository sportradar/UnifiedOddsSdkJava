/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */

package com.sportradar.unifiedodds.sdk.impl.custombetentities;

import com.google.common.base.Preconditions;
import com.sportradar.uf.custombet.datamodel.CAPICalculationResponse;
import com.sportradar.unifiedodds.sdk.custombetentities.Calculation;

/**
 * Implements methods used to provide a probability calculation
 */
public class CalculationImpl implements Calculation {

    private final double odds;
    private final double probability;

    public CalculationImpl(CAPICalculationResponse calculation) {
        Preconditions.checkNotNull(calculation);
        Preconditions.checkNotNull(calculation.getCalculation());

        this.odds = calculation.getCalculation().getOdds();
        this.probability = calculation.getCalculation().getProbability();
    }

    /**
     * Gets the odds
     *
     * @return the odds
     */
    @Override
    public double getOdds() {
        return odds;
    }

    /**
     * Gets the probability
     *
     * @return the probability
     */
    @Override
    public double getProbability() {
        return probability;
    }
}
