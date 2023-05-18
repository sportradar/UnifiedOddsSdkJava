/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */

package com.sportradar.unifiedodds.sdk.impl.oddsentities.markets;

import com.sportradar.uf.datamodel.UFOutcomeActive;
import com.sportradar.unifiedodds.sdk.impl.markets.NameProvider;
import com.sportradar.unifiedodds.sdk.oddsentities.AdditionalProbabilities;
import com.sportradar.unifiedodds.sdk.oddsentities.OutcomeDefinition;
import com.sportradar.unifiedodds.sdk.oddsentities.OutcomeProbabilities;
import java.util.Locale;

/**
 * Created on 16/10/2017.
 * // TODO @eti: Javadoc
 */
@SuppressWarnings({ "ParameterNumber" })
public class OutcomeProbabilitiesImpl extends OutcomeImpl implements OutcomeProbabilities {

    private final boolean active;
    private final double probability;
    private final AdditionalProbabilities additionalProbabilities;

    OutcomeProbabilitiesImpl(
        String id,
        NameProvider nameProvider,
        OutcomeDefinition outcomeDefinition,
        Locale defaultLocale,
        UFOutcomeActive active,
        Double probabilities,
        AdditionalProbabilities additionalProbabilities
    ) {
        super(id, nameProvider, outcomeDefinition, defaultLocale);
        this.active = active == null || active == UFOutcomeActive.ACTIVE;
        this.probability = probabilities == null ? Double.NaN : probabilities;
        this.additionalProbabilities = additionalProbabilities;
    }

    /**
     * If this outcome is deactivated this will return false
     *
     * @return true if the outcome is active, false if it is deactivated and should be removed
     */
    @Override
    public boolean isActive() {
        return active;
    }

    /**
     * The probability for this outcome (how likely is this outcome)
     *
     * @return the likelihood of this outcome happening in percent (i.e. 0-100)
     */
    @Override
    public double getProbability() {
        return probability;
    }

    /**
     * Additional probability attributes for markets which potentially will be (partly) refunded
     * @return additional probability attributes for markets which potentially will be (partly) refunded
     */
    @Override
    public AdditionalProbabilities getAdditionalProbabilities() {
        return additionalProbabilities;
    }
}
