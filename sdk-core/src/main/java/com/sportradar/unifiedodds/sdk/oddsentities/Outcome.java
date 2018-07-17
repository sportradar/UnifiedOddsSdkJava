/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */

package com.sportradar.unifiedodds.sdk.oddsentities;

import java.util.Locale;

/**
 * Describes an outcome for a particular market
 */
public interface Outcome {
    /**
     * Returns the outcome id
     *
     * @return - the outcome id
     */
    String getId();

    /**
     * Returns the outcome name
     *
     * @return - the outcome name
     */
    String getName();

    /**
     * Returns the outcome name translated in the provided locale
     *
     * @param locale - the locale in which the name should be translated
     * @return - the outcome name translated in the provided {@link Locale}
     */
    String getName(Locale locale);

    /**
     * Returns the description of this outcome
     *
     * @return - the description of this outcome
     */
    OutcomeDefinition getOutcomeDefinition();
}
