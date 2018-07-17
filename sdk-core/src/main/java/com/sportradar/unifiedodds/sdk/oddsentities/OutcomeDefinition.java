/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */

package com.sportradar.unifiedodds.sdk.oddsentities;

import java.util.Locale;

/**
 * Defines methods to access outcome data which is not directly associated with a feed message
 */
public interface OutcomeDefinition {
    /**
     * The template from which the outcome name was build
     * @return - the template from which the outcome name was build
     */
    String getNameTemplate();

    /**
     * The template from which the outcome name was build translated in the provided {@link Locale}
     *
     * @param locale - the {@link Locale} in which the name template should be translated
     * @return - the template from which the outcome name was build
     */
    String getNameTemplate(Locale locale);
}
