/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */

package com.sportradar.utils;

import com.google.common.base.Preconditions;

import java.util.Collection;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * An utility class that contains various methods which perform common language tasks
 */
public final class SdkHelper {
    /**
     * Calculates and returns the missing locales within the provided {@link List}
     *
     * @param have - a {@link List} that contains all the available locales
     * @param want - a {@link List} of locales that are required
     * @return - returns a {@link List} of missing locales
     */
    public static List<Locale> findMissingLocales(Collection<Locale> have, List<Locale> want) {
        Preconditions.checkNotNull(have);
        Preconditions.checkNotNull(want);

        return want.stream().filter(locale -> !have.contains(locale)).collect(Collectors.toList());
    }

    /**
     * Filters out the translated data not needed
     *
     * @param data the data to be filtered
     * @param filterLocales the requested locales
     * @return the filtered map data set
     */
    public static Map<Locale, String> filterLocales(Map<Locale, String> data, List<Locale> filterLocales) {
        Preconditions.checkNotNull(data);
        Preconditions.checkNotNull(filterLocales);

        return data.entrySet().stream()
                .filter(entry ->
                        filterLocales.contains(entry.getKey()))
                .collect(
                        Collectors.toMap(
                                Map.Entry::getKey,
                                Map.Entry::getValue)
                );
    }

    /**
     * Get the abbreviation from the input string
     * @param input input text to be abbreviated
     * @param length of the abbreviation
     * @return the abbreviated input string in upper case
     */
    public static String getAbbreviationFromName(String input, int length)
    {
        if (length < 1)
        {
            length = Integer.MAX_VALUE;
        }
        if (input == null || input.isEmpty())
        {
            return null;
        }
        return input.length() > length ? input.substring(0, length).toUpperCase() : input.toUpperCase();
    }
}
