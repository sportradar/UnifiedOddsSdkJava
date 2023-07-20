/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */
package com.sportradar.unifiedodds.sdk.testutil.javautil;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.junit.Test;

public class LanguagesTest {

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
