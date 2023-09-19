/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */

package com.sportradar.unifiedodds.sdk.entities;

import java.util.Locale;
import java.util.Map;

/**
 * Defines methods used to access draw result information
 */
public interface DrawResult {
    /**
     * Returns the value of the draw
     *
     * @return the value of the draw
     */
    Integer getValue();

    /**
     * Returns the name of the draw result
     *
     * @param locale the {@link Locale} in which the data should be provided
     * @return the name of the draw result
     */
    String getName(Locale locale);

    /**
     * Returns the name of the draw result
     */
    Map<Locale, String> getNames();
}
