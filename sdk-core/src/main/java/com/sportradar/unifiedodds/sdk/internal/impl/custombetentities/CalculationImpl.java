/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */

package com.sportradar.unifiedodds.sdk.internal.impl.custombetentities;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableList;
import com.sportradar.uf.custombet.datamodel.CapiCalculationResponse;
import com.sportradar.unifiedodds.sdk.entities.custombet.AvailableSelections;
import com.sportradar.unifiedodds.sdk.entities.custombet.Calculation;
import com.sportradar.utils.SdkHelper;
import java.text.ParseException;
import java.util.Date;
import java.util.List;

/**
 * Implements methods used to provide a probability calculation
 */
@SuppressWarnings({ "EmptyCatchBlock" })
public class CalculationImpl implements Calculation {

    private final double odds;
    private final double probability;
    private final List<AvailableSelections> availableSelectionsList;
    private final Date generatedAt;
    private final Boolean isHarmonization;

    public CalculationImpl(CapiCalculationResponse calculation) {
        Preconditions.checkNotNull(calculation);
        Preconditions.checkNotNull(calculation.getCalculation());

        this.odds = calculation.getCalculation().getOdds();
        this.probability = calculation.getCalculation().getProbability();
        this.availableSelectionsList =
            calculation.getAvailableSelections() != null
                ? calculation
                    .getAvailableSelections()
                    .getEvents()
                    .stream()
                    .map(m -> new AvailableSelectionsImpl(m, calculation.getGeneratedAt()))
                    .collect(ImmutableList.toImmutableList())
                : ImmutableList.of();

        Date date = new Date();
        try {
            date = SdkHelper.toDate(calculation.getGeneratedAt());
        } catch (ParseException e) {
            // ignore
        }
        this.generatedAt = date;
        this.isHarmonization = calculation.getCalculation().isHarmonization();
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

    /**
     * Returns list of available selections
     *
     * @return list of available selections
     */
    @Override
    public List<AvailableSelections> getAvailableSelections() {
        return availableSelectionsList;
    }

    /**
     * Returns the date when API response was generated
     *
     * @return the date when API response was generated
     */
    @Override
    public Date getGeneratedAt() {
        return generatedAt;
    }

    @Override
    public Boolean isHarmonization() {
        return isHarmonization;
    }
}
