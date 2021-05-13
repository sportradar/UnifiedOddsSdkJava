/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */

package com.sportradar.unifiedodds.sdk.oddsentities;

/**
 * Defines methods used to access data on outcomes with probabilities
 */
public interface OutcomeProbabilities extends Outcome {
    /**
     * If this outcome is deactivated this will return false
     *
     * @return true if the outcome is active, false if it is deactivated and should be removed
     */
    boolean isActive();

    /**
     * The probability for this outcome (how likely is this outcome)
     *
     * @return the likelihood of this outcome happening in percent (i.e. 0-100)
     */
    double getProbability();

    /**
     * Additional probability attributes for markets which potentially will be (partly) refunded
     * @return additional probability attributes for markets which potentially will be (partly) refunded
     */
    default AdditionalProbabilities getAdditionalProbabilities()
    {
        throw new UnsupportedOperationException("Method not implemented. Use derived type.");
    }
}
