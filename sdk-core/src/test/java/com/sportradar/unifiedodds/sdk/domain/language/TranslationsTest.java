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
import com.sportradar.utils.domain.names.Languages;
import com.sportradar.utils.domain.names.Names;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

public class TranslationsTest {

    public static final String DIFFERENT_TRANSLATIONS =
        "com.sportradar.unifiedodds.sdk.domain.language.TranslationsTest#differentTranslations";

    public static final String ENGLISH_WORD = "word";
    public static final String FRENCH_WORD = "palabra";

    private TranslationsTest() {}

    public static Object[] differentTranslations() {
        return new Object[][] { { ENGLISH, ENGLISH_WORD }, { FRENCH, FRENCH_WORD } };
    }

    @Nested
    public class Creation {

        @ParameterizedTest
        @MethodSource(DIFFERENT_TRANSLATIONS)
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
    }

    @Nested
    public class AdditionOfSingleTranslation {

        private final Locale existingLanguage = CANADA;
        private final Locale newLanguage = CHINA;
        private final String existingTranslation = "canadian word";
        private final String newTranslation = "chinese word";
        private final Translations translations = new Translations(existingLanguage, existingTranslation);

        @Test
        public void preservesPreviousTranslations() {
            translations.add(newLanguage, Names.any());

            assertThat(translations).hasTranslation(of(existingTranslation, in(existingLanguage)));
        }

        @Test
        public void addsTranslation() {
            translations.add(newLanguage, newTranslation);

            assertThat(translations).hasTranslation(of(newTranslation, in(newLanguage)));
        }

        @Test
        public void replacesTranslation() {
            translations.add(existingLanguage, newTranslation);

            assertThat(translations).hasTranslation(of(newTranslation, in(existingLanguage)));
            assertThat(translations.export()).hasSize(1);
        }

        @Test
        public void preservesTranslationWhenAddingNull() {
            translations.add(existingLanguage, null);

            assertThat(translations).hasTranslation(of(null, in(existingLanguage)));
            assertThat(translations.export()).hasSize(1);
        }

        @Test
        public void throwsOnNullLanguage() {
            assertThatThrownBy(() -> translations.add(null, Names.any()))
                .isInstanceOf(NullPointerException.class);
        }

        @Test
        public void preservesNullTranslations() {
            translations.add(newLanguage, null);

            assertThat(translations).hasTranslation(of(null, in(newLanguage)));
            assertThat(translations.export()).containsKey(newLanguage);
        }
    }

    @Nested
    public class AdditionOfMultipleTranslations {

        private final Locale existingLanguage = CANADA;
        private final Locale anotherExistingLanguage = Locale.GERMAN;
        private final Locale newLanguage = CHINA;
        private final Locale anotherNewLanguage = Locale.ITALIAN;
        private final String existingTranslation = "canadian word";
        private final String anotherExistingTranslation = "ich bin";
        private final String newTranslation = "chinese word";
        private final String anotherNewTranslation = "italiano";

        @Test
        public void doesNotReplaceNullTranslations() {
            Translations translations = new Translations(existingLanguage, null);

            translations.addAllWithoutOverriding(new Translations(existingLanguage, newTranslation));

            assertThat(translations).hasTranslation(of(null, in(existingLanguage)));
        }

        @Test
        public void doesNotReplaceExistingTranslations() {
            Translations translations = new Translations(existingLanguage, existingTranslation);

            translations.addAllWithoutOverriding(new Translations(existingLanguage, newTranslation));

            assertThat(translations).hasTranslation(of(existingTranslation, in(existingLanguage)));
        }

        @Test
        public void addingPreservesPreviousTranslation() {
            Translations translations = new Translations(existingLanguage, existingTranslation);

            translations.addAllWithoutOverriding(new Translations(newLanguage, newTranslation));

            assertThat(translations).hasTranslation(of(existingTranslation, in(existingLanguage)));
        }

        @Test
        public void addsTranslation() {
            Translations translations = new Translations(existingLanguage, existingTranslation);

            translations.addAllWithoutOverriding(new Translations(newLanguage, newTranslation));

            assertThat(translations).hasTranslation(of(newTranslation, in(newLanguage)));
        }

        @Test
        public void preservesMultiplePreviousTranslations() {
            Translations translations = new Translations(existingLanguage, existingTranslation);
            translations.add(anotherExistingLanguage, anotherExistingTranslation);

            translations.addAllWithoutOverriding(new Translations(newLanguage, newTranslation));

            assertThat(translations).hasTranslation(of(existingTranslation, in(existingLanguage)));
            assertThat(translations)
                .hasTranslation(of(anotherExistingTranslation, in(anotherExistingLanguage)));
        }

        @Test
        public void addsMultipleTranslations() {
            Translations translations = new Translations(existingLanguage, existingTranslation);

            Translations inflight = new Translations(newLanguage, newTranslation);
            inflight.add(anotherNewLanguage, anotherNewTranslation);
            translations.addAllWithoutOverriding(inflight);

            assertThat(translations).hasTranslation(of(newTranslation, in(newLanguage)));
            assertThat(translations).hasTranslation(of(anotherNewTranslation, in(anotherNewLanguage)));
        }

        @Test
        public void throwsOnNullLanguage() {
            Translations translations = new Translations(existingLanguage, existingTranslation);

            assertThatThrownBy(() -> translations.add(null, Names.any()))
                .isInstanceOf(NullPointerException.class);
        }

        @Test
        public void preservesNullTranslation() {
            Translations translations = new Translations(existingLanguage, existingTranslation);

            translations.add(newLanguage, null);

            assertThat(translations).hasTranslation(of(null, in(newLanguage)));
            assertThat(translations.export()).containsKey(newLanguage);
        }
    }

    @Nested
    public class ReconstructsFromSnapshot {

        private final Locale language = CHINA;
        private final Locale anotherLanguage = Locale.ITALIAN;
        private final String translation = "chinese word";
        private final String anotherTranslation = "italiano";

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
            Translations translations = Translations.importFrom(Maps.of(language, null));

            assertThat(translations).hasTranslation(with(null, in(language)));
        }

        @Test
        public void createsForSingleTranslation() {
            Translations translations = Translations.importFrom(Maps.of(language, translation));

            assertThat(translations).hasTranslation(with(translation, in(language)));
        }

        @Test
        public void addsMultipleTranslations() {
            Translations translations = Translations.importFrom(
                Maps.of(language, translation, anotherLanguage, anotherTranslation)
            );

            assertThat(translations).hasTranslation(with(translation, in(language)));
            assertThat(translations).hasTranslation(with(anotherTranslation, in(anotherLanguage)));
        }
    }

    @Nested
    public class SnapshotImmutability {

        @Test
        public void snapshotIsImmutable() {
            Map<Locale, String> snapshot = new Translations(Languages.any(), Names.any()).export();

            assertThatThrownBy(() -> snapshot.put(ENGLISH, Names.any()))
                .isInstanceOf(UnsupportedOperationException.class);
        }
    }
}
