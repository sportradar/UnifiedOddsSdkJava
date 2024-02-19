/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */
package com.sportradar.unifiedodds.sdk.caching.ci.matchers;

import com.sportradar.unifiedodds.sdk.domain.language.Translations;
import com.sportradar.utils.domain.names.LanguageHolder;
import com.sportradar.utils.domain.names.TranslationHolder;
import java.util.Locale;
import org.assertj.core.api.AbstractAssert;
import org.assertj.core.api.Assertions;

public class TranslationsAssert extends AbstractAssert<TranslationsAssert, Translations> {

    public TranslationsAssert(Translations translations) {
        super(translations, TranslationsAssert.class);
    }

    public static TranslationsAssert assertThat(Translations translations) {
        return new TranslationsAssert(translations);
    }

    public TranslationsAssert hasTranslation(TranslationHolder translation) {
        Locale language = translation.getLanguage();
        String word = translation.getWord();
        Assertions.assertThat(actual.getFor(language)).isEqualTo(word);
        Assertions.assertThat(actual.export().get(language)).isEqualTo(word);
        return this;
    }

    public TranslationsAssert isNotTranslatedTo(LanguageHolder language) {
        Assertions.assertThat(actual.getFor(language.get())).isNull();
        Assertions.assertThat(actual.export().get(language.get())).isNull();
        return this;
    }
}
