/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */
package com.sportradar.utils.domain.names;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

public class LanguagesTest {

    @Nested
    public class SingleLanguage {

        private final int sampleSize = 100;
        private List<Locale> languages = Stream
            .generate(() -> Languages.any())
            .limit(sampleSize)
            .distinct()
            .collect(Collectors.toList());

        @Test
        public void anyLanguageGeneratesNotAlwaysTheSameLanguages() {
            assertThat(languages).hasSizeGreaterThan(1);
        }

        @Test
        public void anyLanguageDoesNotGenerateNullLanguages() {
            assertThat(languages).doesNotContainNull();
        }
    }
}
