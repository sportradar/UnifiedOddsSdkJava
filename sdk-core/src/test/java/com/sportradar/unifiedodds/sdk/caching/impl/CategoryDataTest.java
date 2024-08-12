/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */
package com.sportradar.unifiedodds.sdk.caching.impl;

import static com.sportradar.utils.Urns.Categories.urnForAnyCategory;
import static java.util.Arrays.asList;
import static java.util.Collections.emptyMap;
import static java.util.Locale.ENGLISH;
import static java.util.Locale.FRENCH;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.Assert.assertNull;

import com.sportradar.utils.Urn;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import lombok.val;
import org.apache.groovy.util.Maps;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

public class CategoryDataTest {

    private static final String UNDER_20_EN = "Under 20";
    private static final String UNDER_20_FR = "moins de 20 ans";
    private static final List<Urn> ANY_TOURNAMENTS = asList();
    private static final String ANY_COUNTRY = "Germany";

    @Test
    public void getsNameInTheOnlyLanguageAvailable() {
        val nameInEnglish = Maps.of(ENGLISH, UNDER_20_EN);
        val category = new CategoryData(urnForAnyCategory(), nameInEnglish, ANY_TOURNAMENTS, ANY_COUNTRY);

        val name = category.getNames();

        assertThat(name.get(ENGLISH)).isEqualTo(UNDER_20_EN);
    }

    @Test
    public void getsNameInMultipleLanguages() {
        val name = Maps.of(ENGLISH, UNDER_20_EN, FRENCH, UNDER_20_FR);
        val category = new CategoryData(urnForAnyCategory(), name, ANY_TOURNAMENTS, ANY_COUNTRY);

        val names = category.getNames();

        assertThat(names.get(ENGLISH)).isEqualTo(UNDER_20_EN);
        assertThat(names.get(FRENCH)).isEqualTo(UNDER_20_FR);
    }

    @ParameterizedTest
    @MethodSource("translations")
    public void getsNameInDesiredLanguage(Locale language, String translation) {
        val namesInSingleLanguage = Maps.of(language, translation);
        val category = new CategoryData(
            urnForAnyCategory(),
            namesInSingleLanguage,
            ANY_TOURNAMENTS,
            ANY_COUNTRY
        );

        assertThat(category.getName(language)).isEqualTo(translation);
    }

    private static Object[] translations() {
        return new Object[][] { { ENGLISH, UNDER_20_EN }, { FRENCH, UNDER_20_FR } };
    }

    @Test
    public void getsNullAsNameInUnavailableLanguage() {
        val namesInEnglish = Maps.of(ENGLISH, UNDER_20_EN);
        val category = new CategoryData(urnForAnyCategory(), namesInEnglish, ANY_TOURNAMENTS, ANY_COUNTRY);

        assertNull(category.getName(FRENCH));
    }

    @Test
    public void getsNullAsNameWhenNoLanguagesAreAvailable() {
        Map<Locale, String> notTranslatedName = emptyMap();
        val category = new CategoryData(urnForAnyCategory(), notTranslatedName, ANY_TOURNAMENTS, ANY_COUNTRY);

        assertNull(category.getName(FRENCH));
    }
}
