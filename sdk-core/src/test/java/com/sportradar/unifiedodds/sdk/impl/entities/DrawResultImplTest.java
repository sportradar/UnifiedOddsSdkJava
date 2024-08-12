/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */
package com.sportradar.unifiedodds.sdk.impl.entities;

import static com.sportradar.unifiedodds.sdk.impl.entities.DrawResultAssertions.assertThat;
import static java.util.Collections.emptyMap;
import static java.util.Locale.ENGLISH;
import static java.util.Locale.FRENCH;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import lombok.val;
import org.apache.groovy.util.Maps;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

public class DrawResultImplTest {

    private static final String UNDER_20_EN = "Under 20";
    private static final String UNDER_20_FR = "moins de 20 ans";
    private static final int ANY_VALUE = 58;

    @Test
    public void getsNameInMultipleLanguages() {
        val name = Maps.of(ENGLISH, UNDER_20_EN, FRENCH, UNDER_20_FR);
        val draw = new DrawResultImpl(ANY_VALUE, name);

        assertThat(draw).hasNameTranslated(ENGLISH, UNDER_20_EN);
        assertThat(draw).hasNameTranslated(FRENCH, UNDER_20_FR);
    }

    @ParameterizedTest
    @MethodSource("translations")
    public void getsNameInTheOnlyLanguageAvailable(Locale language, String translation) {
        val name = Maps.of(language, translation);
        val draw = new DrawResultImpl(ANY_VALUE, name);

        assertThat(draw).hasNameTranslated(language, translation);
    }

    private static Object[] translations() {
        return new Object[][] { { ENGLISH, UNDER_20_EN }, { FRENCH, UNDER_20_FR } };
    }

    @Test
    public void getsNullAsNameInUnavailableLanguage() {
        val name = Maps.of(ENGLISH, UNDER_20_EN);
        val draw = new DrawResultImpl(ANY_VALUE, name);

        assertThat(draw).hasNameNotTranslatedTo(FRENCH);
    }

    @Test
    public void getsNullAsNameWhenNoLanguagesAreAvailable() {
        Map<Locale, String> notTranslatedName = emptyMap();
        val draw = new DrawResultImpl(ANY_VALUE, notTranslatedName);

        assertThat(draw).hasNameNotTranslatedTo(FRENCH);
    }

    @Test
    public void namesShouldBeImmutable() {
        Map<Locale, String> notTranslatedName = new HashMap<>();
        val draw = new DrawResultImpl(ANY_VALUE, notTranslatedName);

        assertThatThrownBy(() -> draw.getNames().put(ENGLISH, "any"))
            .isInstanceOf(UnsupportedOperationException.class);
    }
}
