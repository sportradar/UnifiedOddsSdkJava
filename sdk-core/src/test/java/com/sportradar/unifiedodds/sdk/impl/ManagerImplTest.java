/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */
package com.sportradar.unifiedodds.sdk.impl;

import static com.google.common.collect.ImmutableMap.of;
import static com.sportradar.unifiedodds.sdk.impl.entities.ManagerAssertions.assertThat;
import static java.util.Locale.ENGLISH;
import static java.util.Locale.FRENCH;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.sportradar.unifiedodds.sdk.caching.ci.ManagerCi;
import java.util.Locale;
import lombok.val;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

public class ManagerImplTest {

    private static final String UNDER_20_EN = "Under 20";
    private static final String UNDER_20_FR = "moins de 20 ans";
    private final ManagerCi managerCi = mock(ManagerCi.class);

    @Test
    public void getsNameInMultipleLanguages() {
        when(managerCi.getNames()).thenReturn(of(ENGLISH, UNDER_20_EN, FRENCH, UNDER_20_FR));
        val manager = new ManagerImpl(managerCi);

        assertThat(manager).hasNameTranslated(ENGLISH, UNDER_20_EN);
        assertThat(manager).hasNameTranslated(FRENCH, UNDER_20_FR);
    }

    @ParameterizedTest
    @MethodSource("translations")
    public void getsNameInTheOnlyLanguageAvailable(Locale language, String translation) {
        when(managerCi.getNames()).thenReturn(of(language, translation));
        val manager = new ManagerImpl(managerCi);

        assertThat(manager).hasNameTranslated(language, translation);
    }

    private static Object[] translations() {
        return new Object[][] { { ENGLISH, UNDER_20_EN }, { FRENCH, UNDER_20_FR } };
    }

    @Test
    public void doesNotGetNameInUnavailableLanguage() {
        when(managerCi.getNames()).thenReturn(of(ENGLISH, UNDER_20_EN));
        val manager = new ManagerImpl(managerCi);

        assertThat(manager).hasNameNotTranslatedTo(FRENCH);
    }

    @Test
    public void getsNullAsNameWhenNoLanguagesAreAvailable() {
        val manager = new ManagerImpl(managerCi);

        assertThat(manager).hasNameNotTranslatedTo(FRENCH);
    }
}
