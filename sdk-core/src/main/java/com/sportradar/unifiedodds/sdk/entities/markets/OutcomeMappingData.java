/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */

package com.sportradar.unifiedodds.sdk.entities.markets;

import java.util.Locale;

/**
 * Defines methods used to access a specific outcome mapping data
 */
public interface OutcomeMappingData {
    /**
     * Returns the associated outcome identifier
     *
     * @return - the associated outcome identifier
     */
    String getOutcomeId();

    /**
     * Returns the mapped outcome id
     *
     * @return - the mapped outcome id
     */
    String getProducerOutcomeId();

    /**
     * Returns the mapped outcome name
     *
     * @param locale - the {@link Locale} in which the name should be returned
     * @return - the translated mapped outcome name
     */
    String getProducerOutcomeName(Locale locale);
}
