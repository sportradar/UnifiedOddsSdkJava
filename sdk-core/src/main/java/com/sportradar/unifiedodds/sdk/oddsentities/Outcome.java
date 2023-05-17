/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */

package com.sportradar.unifiedodds.sdk.oddsentities;

import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * Describes an outcome for a particular market
 */
@SuppressWarnings({ "LineLength" })
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

    /**
     * @param locales the list of {@link Locale} in which the name should be returned
     * @return - the names of the market translated in the specified {@link Locale} (specifier placeholders are replaced with actual
     * values)
     */
    default Map<Locale, String> getNames(List<Locale> locales) {
        return null;
    }
}
