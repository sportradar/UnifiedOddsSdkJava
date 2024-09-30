/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */
package com.sportradar.unifiedodds.sdk.impl.oddsentities.markets;

import static java.util.Locale.FRENCH;

import com.sportradar.utils.domain.names.LanguageHolder;
import java.util.Arrays;

@SuppressWarnings("MagicNumber")
public enum Ordinals {
    FIRST(1, "1st", "1er"),
    SECOND(2, "2nd", "2e"),
    THIRD(3, "3rd", "3e"),
    FOURTH(4, "4th", "4e");

    private int number;
    private String english;
    private String german;

    Ordinals(int number, String english, String german) {
        this.number = number;
        this.english = english;
        this.german = german;
    }

    public static String ordinal(int value, LanguageHolder language) {
        return Arrays
            .stream(values())
            .filter(ordinal -> ordinal.number == value)
            .findFirst()
            .map(ordinal -> language.get().equals(FRENCH) ? ordinal.german : ordinal.english)
            .orElseThrow(() ->
                new IllegalArgumentException(language.get() + " not supported by the Ordinals fixture")
            );
    }
}
