/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */
package com.sportradar.utils.domain.names;

import java.util.Locale;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;

@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class LanguageHolder {

    private final Locale language;

    public static LanguageHolder in(Locale locale) {
        return new LanguageHolder(locale);
    }

    public static LanguageHolder withNoTranslationTo(Locale locale) {
        return new LanguageHolder(locale);
    }

    public Locale get() {
        return language;
    }
}
