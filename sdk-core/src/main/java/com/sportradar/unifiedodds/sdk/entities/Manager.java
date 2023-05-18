/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */

package com.sportradar.unifiedodds.sdk.entities;

import com.sportradar.utils.URN;
import java.util.Locale;

/**
 * Defines methods used to access manager properties
 */
public interface Manager {
    /**
     * Returns the manager identifier
     *
     * @return the manager identifier
     */
    URN getId();

    /**
     * Returns the translated manager name
     *
     * @param locale the locale in which the name should be provided
     * @return the translated manager name
     */
    String getName(Locale locale);

    /**
     * Returns the translated nationality
     *
     * @param locale the locale in which the nationality should be provided
     * @return the translated nationality
     */
    String getNationality(Locale locale);

    /**
     * Returns the country code
     *
     * @return the country code
     */
    String getCountryCode();
}
