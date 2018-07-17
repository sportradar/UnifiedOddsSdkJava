/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */

package com.sportradar.unifiedodds.sdk.entities;

import java.util.Locale;

/**
 * A derive of the {@link NamedValue} which contains values with names/descriptions that are translatable
 */
public interface LocalizedNamedValue extends NamedValue {
    /**
     * Returns the current instance description in the requested {@link Locale}
     *
     * @param locale - the {@link Locale} in which the description should be provided
     * @return - the current instance description in the requested {@link Locale} if available;
     *           otherwise null
     */
    String getDescription(Locale locale);
}
