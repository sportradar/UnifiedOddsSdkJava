/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */
package com.sportradar.unifiedodds.sdk.domain.language;

import static java.util.Arrays.asList;
import static java.util.Locale.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatException;

import com.sportradar.unifiedodds.sdk.internal.caching.Languages;
import lombok.val;
import org.junit.jupiter.api.Test;

class LanguagesBestEffortTest {

    @Test
    void createsFromList() {
        val languages = asList(ENGLISH, GERMAN, FRENCH, JAPANESE);

        val actual = new Languages.BestEffort(languages);

        assertThat(actual.getLanguages()).isEqualTo(languages);
    }

    @Test
    void createsWithSingleLanguage() {
        val aLanguage = UK;

        val actual = new Languages.BestEffort(aLanguage);

        assertThat(actual.getLanguages()).containsOnly(aLanguage);
    }

    @Test
    void createsFromVarargs() {
        val actual = new Languages.BestEffort(GERMAN, FRENCH, JAPANESE);

        assertThat(actual.getLanguages()).containsExactly(GERMAN, FRENCH, JAPANESE);
    }

    @Test
    void languageListIsImmutable() {
        val actual = new Languages.BestEffort(GERMAN, FRENCH, JAPANESE);

        assertThatException()
            .isThrownBy(() -> actual.getLanguages().add(ENGLISH))
            .isInstanceOf(UnsupportedOperationException.class);

        assertThatException()
            .isThrownBy(() -> actual.getLanguages().remove(FRENCH))
            .isInstanceOf(UnsupportedOperationException.class);

        assertThatException()
            .isThrownBy(() -> actual.getLanguages().removeAll(asList(FRENCH)))
            .isInstanceOf(UnsupportedOperationException.class);

        assertThatException()
            .isThrownBy(() -> actual.getLanguages().addAll(asList(ENGLISH)))
            .isInstanceOf(UnsupportedOperationException.class);
    }
}
