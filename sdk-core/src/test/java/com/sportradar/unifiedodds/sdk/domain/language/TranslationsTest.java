/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */
package com.sportradar.unifiedodds.sdk.domain.language;

import static com.sportradar.unifiedodds.sdk.caching.ci.matchers.TranslationsAssert.assertThat;
import static com.sportradar.utils.domain.names.LanguageHolder.in;
import static com.sportradar.utils.domain.names.TranslationHolder.of;
import static com.sportradar.utils.domain.names.TranslationHolder.with;
import static java.util.Locale.CANADA;
import static java.util.Locale.CHINA;
import static java.util.Locale.ENGLISH;
import static java.util.Locale.FRENCH;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.sportradar.unifiedodds.sdk.testutil.generic.collections.Maps;
import com.sportradar.unifiedodds.sdk.testutil.javautil.Languages;
import com.sportradar.utils.domain.names.Names;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import junitparams.JUnitParamsRunner;
import junitparams.Parameters;
import org.junit.Test;
import org.junit.experimental.runners.Enclosed;
import org.junit.runner.RunWith;

@RunWith(Enclosed.class)
public class TranslationsTest {

    public static final String ENGLISH_WORD = "word";
    public static final String FRENCH_WORD = "palabra";

    private TranslationsTest() {}

    @RunWith(JUnitParamsRunner.class)
    public static class Creation {

        @Test
        @Parameters(method = "differentTranslations")
        public void createsTranslation(Locale language, String translation) {
            Translations translations = new Translations(language, translation);

            assertThat(translations).hasTranslation(of(translation, in(language)));
        }

        @Test
        public void throwsOnNullLanguage() {
            assertThatThrownBy(() -> new Translations(null, Names.any()))
                .isInstanceOf(NullPointerException.class);
        }

        @Test
        public void preservesNullTranslation() {
            Translations translations = new Translations(ENGLISH, null);

            assertThat(translations).hasTranslation(of(null, in(ENGLISH)));
            assertThat(translations.export()).containsKey(ENGLISH);
        }

        public static Object[] differentTranslations() {
            return new Object[][] { { ENGLISH, ENGLISH_WORD }, { FRENCH, FRENCH_WORD } };
        }
    }

    @RunWith(JUnitParamsRunner.class)
    public static class AdditionOfSingleTranslation {

        private static final Locale EXISTING_LANGUAGE = CANADA;
        private static final Locale NEW_LANGUAGE = CHINA;
        private static final String EXISTING_TRANSLATION = "canadian word";
        private static final String NEW_TRANSLATION = "chinese word";
        private final Translations translations = new Translations(EXISTING_LANGUAGE, EXISTING_TRANSLATION);

        @Test
        public void preservesPreviousTranslations() {
            translations.add(NEW_LANGUAGE, Names.any());

            assertThat(translations).hasTranslation(of(EXISTING_TRANSLATION, in(EXISTING_LANGUAGE)));
        }

        @Test
        public void addsTranslation() {
            translations.add(NEW_LANGUAGE, NEW_TRANSLATION);

            assertThat(translations).hasTranslation(of(NEW_TRANSLATION, in(NEW_LANGUAGE)));
        }

        @Test
        public void replacesTranslation() {
            translations.add(EXISTING_LANGUAGE, NEW_TRANSLATION);

            assertThat(translations).hasTranslation(of(NEW_TRANSLATION, in(EXISTING_LANGUAGE)));
            assertThat(translations.export()).hasSize(1);
        }

        @Test
        public void preservesTranslationWhenAddingNull() {
            translations.add(EXISTING_LANGUAGE, null);

            assertThat(translations).hasTranslation(of(null, in(EXISTING_LANGUAGE)));
            assertThat(translations.export()).hasSize(1);
        }

        @Test
        public void throwsOnNullLanguage() {
            assertThatThrownBy(() -> translations.add(null, Names.any()))
                .isInstanceOf(NullPointerException.class);
        }

        @Test
        public void preservesNullTranslations() {
            translations.add(NEW_LANGUAGE, null);

            assertThat(translations).hasTranslation(of(null, in(NEW_LANGUAGE)));
            assertThat(translations.export()).containsKey(NEW_LANGUAGE);
        }

        public static Object[] differentTranslations() {
            return new Object[][] { { ENGLISH, ENGLISH_WORD }, { FRENCH, FRENCH_WORD } };
        }
    }

    @RunWith(JUnitParamsRunner.class)
    public static class AdditionOfMultipleTranslations {

        private static final Locale EXISTING_LANGUAGE = CANADA;
        private static final Locale ANOTHER_EXISTING_LANGUAGE = Locale.GERMAN;
        private static final Locale NEW_LANGUAGE = CHINA;
        private static final Locale ANOTHER_NEW_LANGUAGE = Locale.ITALIAN;
        private static final String EXISTING_TRANSLATION = "canadian word";
        private static final String ANOTHER_EXISTING_TRANSLATION = "ich bin";
        private static final String NEW_TRANSLATION = "chinese word";
        private static final String ANOTHER_NEW_TRANSLATION = "italiano";

        @Test
        public void doesNotReplaceNullTranslations() {
            Translations translations = new Translations(EXISTING_LANGUAGE, null);

            translations.addAllWithoutOverriding(new Translations(EXISTING_LANGUAGE, NEW_TRANSLATION));

            assertThat(translations).hasTranslation(of(null, in(EXISTING_LANGUAGE)));
        }

        @Test
        public void doesNotReplaceExistingTranslations() {
            Translations translations = new Translations(EXISTING_LANGUAGE, EXISTING_TRANSLATION);

            translations.addAllWithoutOverriding(new Translations(EXISTING_LANGUAGE, NEW_TRANSLATION));

            assertThat(translations).hasTranslation(of(EXISTING_TRANSLATION, in(EXISTING_LANGUAGE)));
        }

        @Test
        public void addingPreservesPreviousTranslation() {
            Translations translations = new Translations(EXISTING_LANGUAGE, EXISTING_TRANSLATION);

            translations.addAllWithoutOverriding(new Translations(NEW_LANGUAGE, NEW_TRANSLATION));

            assertThat(translations).hasTranslation(of(EXISTING_TRANSLATION, in(EXISTING_LANGUAGE)));
        }

        @Test
        public void addsTranslation() {
            Translations translations = new Translations(EXISTING_LANGUAGE, EXISTING_TRANSLATION);

            translations.addAllWithoutOverriding(new Translations(NEW_LANGUAGE, NEW_TRANSLATION));

            assertThat(translations).hasTranslation(of(NEW_TRANSLATION, in(NEW_LANGUAGE)));
        }

        @Test
        public void preservesMultiplePreviousTranslations() {
            Translations translations = new Translations(EXISTING_LANGUAGE, EXISTING_TRANSLATION);
            translations.add(ANOTHER_EXISTING_LANGUAGE, ANOTHER_EXISTING_TRANSLATION);

            translations.addAllWithoutOverriding(new Translations(NEW_LANGUAGE, NEW_TRANSLATION));

            assertThat(translations).hasTranslation(of(EXISTING_TRANSLATION, in(EXISTING_LANGUAGE)));
            assertThat(translations)
                .hasTranslation(of(ANOTHER_EXISTING_TRANSLATION, in(ANOTHER_EXISTING_LANGUAGE)));
        }

        @Test
        public void addsMultipleTranslations() {
            Translations translations = new Translations(EXISTING_LANGUAGE, EXISTING_TRANSLATION);

            Translations inflight = new Translations(NEW_LANGUAGE, NEW_TRANSLATION);
            inflight.add(ANOTHER_NEW_LANGUAGE, ANOTHER_NEW_TRANSLATION);
            translations.addAllWithoutOverriding(inflight);

            assertThat(translations).hasTranslation(of(NEW_TRANSLATION, in(NEW_LANGUAGE)));
            assertThat(translations).hasTranslation(of(ANOTHER_NEW_TRANSLATION, in(ANOTHER_NEW_LANGUAGE)));
        }

        @Test
        public void throwsOnNullLanguage() {
            Translations translations = new Translations(EXISTING_LANGUAGE, EXISTING_TRANSLATION);

            assertThatThrownBy(() -> translations.add(null, Names.any()))
                .isInstanceOf(NullPointerException.class);
        }

        @Test
        public void preservesNullTranslation() {
            Translations translations = new Translations(EXISTING_LANGUAGE, EXISTING_TRANSLATION);

            translations.add(NEW_LANGUAGE, null);

            assertThat(translations).hasTranslation(of(null, in(NEW_LANGUAGE)));
            assertThat(translations.export()).containsKey(NEW_LANGUAGE);
        }

        public static Object[] differentTranslations() {
            return new Object[][] { { ENGLISH, ENGLISH_WORD }, { FRENCH, FRENCH_WORD } };
        }
    }

    @RunWith(JUnitParamsRunner.class)
    public static class ReconstructsFromSnapshot {

        private static final Locale LANGUAGE = CHINA;
        private static final Locale ANOTHER_LANGUAGE = Locale.ITALIAN;
        private static final String TRANSLATION = "chinese word";
        private static final String ANOTHER_TRANSLATION = "italiano";

        @Test
        public void failsToCreateFromNullSnapshot() {
            assertThatThrownBy(() -> Translations.importFrom(null)).isInstanceOf(NullPointerException.class);
        }

        @Test
        public void createsForZeroTranslations() {
            Translations translations = Translations.importFrom(new HashMap());

            assertThat(translations.export()).isEmpty();
        }

        @Test
        public void makesItsOwnCopyOfTranslations() {
            Map outsideMap = new HashMap();
            Translations translations = Translations.importFrom(outsideMap);

            outsideMap.put(Languages.any(), Names.any());

            assertThat(translations.export()).isEmpty();
        }

        @Test
        public void failsToCreateForNullLanguage() {
            assertThatThrownBy(() -> Translations.importFrom(Maps.of(null, Names.any())))
                .isInstanceOf(NullPointerException.class);
        }

        @Test
        public void preservesNullTranslation() {
            Translations translations = Translations.importFrom(Maps.of(LANGUAGE, null));

            assertThat(translations).hasTranslation(with(null, in(LANGUAGE)));
        }

        @Test
        public void createsForSingleTranslation() {
            Translations translations = Translations.importFrom(Maps.of(LANGUAGE, TRANSLATION));

            assertThat(translations).hasTranslation(with(TRANSLATION, in(LANGUAGE)));
        }

        @Test
        public void addsMultipleTranslations() {
            Translations translations = Translations.importFrom(
                Maps.of(LANGUAGE, TRANSLATION, ANOTHER_LANGUAGE, ANOTHER_TRANSLATION)
            );

            assertThat(translations).hasTranslation(with(TRANSLATION, in(LANGUAGE)));
            assertThat(translations).hasTranslation(with(ANOTHER_TRANSLATION, in(ANOTHER_LANGUAGE)));
        }

        public static Object[] differentTranslations() {
            return new Object[][] { { ENGLISH, ENGLISH_WORD }, { FRENCH, FRENCH_WORD } };
        }
    }

    public static class SnapshotImmutability {

        @Test
        public void snapshotIsImmutable() {
            Map<Locale, String> snapshot = new Translations(Languages.any(), Names.any()).export();

            assertThatThrownBy(() -> snapshot.put(ENGLISH, Names.any()))
                .isInstanceOf(UnsupportedOperationException.class);
        }
    }
}
