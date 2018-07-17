/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */

package com.sportradar.unifiedodds.sdk.entities;

import java.util.Locale;

/**
 * Event delay info
 */
public interface DelayedInfo {
    /**
     * Returns the delay info identifier
     *
     * @return the delay info identifier
     */
    int getId();

    /**
     * Returns the delay info description in the specified {@link Locale}
     *
     * @param locale the {@link Locale} in which the data should be provided
     * @return the delay info description in the specified {@link Locale}
     */
    String getDescription(Locale locale);
}
