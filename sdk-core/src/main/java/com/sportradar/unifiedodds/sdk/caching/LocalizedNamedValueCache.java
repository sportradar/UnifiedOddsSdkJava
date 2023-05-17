/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */

package com.sportradar.unifiedodds.sdk.caching;

import com.sportradar.unifiedodds.sdk.entities.LocalizedNamedValue;
import java.util.List;
import java.util.Locale;

/**
 * Defines methods used to retrieve {@link LocalizedNamedValue} objects
 */
public interface LocalizedNamedValueCache {
    /**
     * Gets a {@link LocalizedNamedValue} with the specified translations
     *
     * @param id - the identifier of the localized value
     * @param locales - a {@link List} of {@link Locale} in which the data is required
     * @return - a {@link LocalizedNamedValue} with the specified translations
     */
    LocalizedNamedValue get(int id, List<Locale> locales);

    /**
     * Determines if the specified identifier exists in the current instance type {@link Object}
     *
     * @param id - the identifier to check
     * @return - <code>true</code> if the value exists; otherwise <code>false</code>
     */
    boolean isValueDefined(int id);
}
