/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */

package com.sportradar.unifiedodds.sdk.entities;

import com.sportradar.utils.URN;

import java.util.Locale;
import java.util.Map;

/**
 * Defines methods implemented by classes representing a sport
 */
public interface SportSummary {
    /**
     * Returns an {@link URN} uniquely identifying the sport represented by the current instance
     *
     * @return - an {@link URN} uniquely identifying the sport represented by the current instance
     */
    URN getId();

    /**
     * Returns the name of the current {@link SportSummary} instance in the specified language
     *
     * @param l - a {@link Locale} specifying the language in which the name should be translated
     * @return - the name of the current {@link SportSummary} instance in the specified language
     */
    String getName(Locale l);

    /**
     * Returns an unmodifiable {@link Map} containing translated sport names
     *
     * @return - an unmodifiable {@link Map} containing translated sport names
     */
    Map<Locale, String> getNames();
}
