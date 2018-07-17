/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */

package com.sportradar.unifiedodds.sdk.entities;

import com.sportradar.utils.URN;

import java.util.Locale;
import java.util.Map;

/**
 * An interface providing methods to access player details
 */
public interface Player {

    /**
     * Returns the unique {@link URN} identifier representing the current {@link Player} instance
     *
     * @return - the unique {@link URN} identifier representing the current {@link Player} instance
     */
    URN getId();

    /**
     * Returns an unmodifiable map of available translated names
     *
     * @return - an unmodifiable map of available translated names
     */
    Map<Locale, String> getNames();

    /**
     * Returns the name of the player in the specified language
     *
     * @param locale - a {@link Locale} specifying the language of the returned name
     * @return - the name of the player in the specified language
     */
    String getName(Locale locale);
}
