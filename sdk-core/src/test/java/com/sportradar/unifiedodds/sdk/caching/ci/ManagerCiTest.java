/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */
package com.sportradar.unifiedodds.sdk.caching.ci;

import static java.util.Locale.ENGLISH;
import static java.util.Locale.FRENCH;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.sportradar.uf.sportsapi.datamodel.SapiManager;
import java.util.Locale;
import lombok.val;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

public class ManagerCiTest {

    private static final String UNDER_20_EN = "Under 20";
    private static final String UNDER_20_FR = "moins de 20 ans";

    @Test
    public void getsNameInMultipleLanguages() {
        val manager = new ManagerCi(namedSapiManager(UNDER_20_EN), ENGLISH);
        manager.merge(namedSapiManager(UNDER_20_FR), FRENCH);

        Assertions.assertThat(manager.getNames().get(ENGLISH)).isEqualTo(UNDER_20_EN);
        Assertions.assertThat(manager.getNames().get(FRENCH)).isEqualTo(UNDER_20_FR);
    }

    @ParameterizedTest
    @MethodSource("translations")
    public void getsNameInTheOnlyLanguageAvailable(Locale language, String translation) {
        val manager = new ManagerCi(namedSapiManager(translation), language);

        Assertions.assertThat(manager.getNames().get(language)).isEqualTo(translation);
    }

    private static Object[] translations() {
        return new Object[][] { { ENGLISH, UNDER_20_EN }, { FRENCH, UNDER_20_FR } };
    }

    @Test
    public void doesNotGetNameInUnavailableLanguage() {
        val manager = new ManagerCi(namedSapiManager(UNDER_20_EN), ENGLISH);

        Assertions.assertThat(manager.getNames().containsKey(FRENCH)).isFalse();
    }

    @Test
    public void namesShouldBeImmutable() {
        val manager = new ManagerCi(namedSapiManager(UNDER_20_EN), ENGLISH);

        assertThatThrownBy(() -> manager.getNames().put(FRENCH, "any"))
            .isInstanceOf(UnsupportedOperationException.class);
    }

    private static SapiManager namedSapiManager(String name) {
        val manager = new SapiManager();
        manager.setName(name);
        manager.setId("sr:anyid:1");
        return manager;
    }
}
