/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */

package com.sportradar.unifiedodds.sdk.entities.markets;

import java.util.Locale;

/**
 * Represents an outcome description
 */
public interface OutcomeDescription {
    /**
     * Returns the outcome identifier
     *
     * @return the outcome identifier
     */
    String getId();

    /**
     * Returns the outcome name translated in the provided {@link Locale}
     *
     * @param locale the {@link Locale} in which the outcome name should be returned
     * @return the outcome name translated in the provided {@link Locale}
     */
    String getName(Locale locale);

    /**
     * Returns the outcome name translated in the provided {@link Locale}
     *
     * @param locale the {@link Locale} in which the outcome name should be returned
     * @return the outcome name translated in the provided {@link Locale}
     */
    String getDescription(Locale locale);
}
