/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */
package com.sportradar.utils.domain.names;

import java.util.Locale;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Getter
public class TranslationHolder {

    private final String word;
    private final Locale language;

    public static TranslationHolder with(String word, LanguageHolder language) {
        return new TranslationHolder(word, language.get());
    }

    public static TranslationHolder of(String word, LanguageHolder language) {
        return new TranslationHolder(word, language.get());
    }
}
