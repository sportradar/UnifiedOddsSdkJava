/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */
package com.sportradar.unifiedodds.sdk.caching.ci;

import static com.sportradar.unifiedodds.sdk.caching.ci.matchers.IndexHolder.atIndex;
import static com.sportradar.unifiedodds.sdk.caching.ci.matchers.VenueCiAssert.assertThat;
import static com.sportradar.utils.Urns.Venues.urnForAnyVenue;
import static com.sportradar.utils.Urns.unique;
import static com.sportradar.utils.domain.names.LanguageHolder.in;
import static com.sportradar.utils.domain.names.LanguageHolder.withNoTranslationTo;
import static com.sportradar.utils.domain.names.TranslationHolder.with;
import static java.util.Arrays.asList;
import static java.util.Locale.ENGLISH;
import static java.util.Locale.FRENCH;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.sportradar.uf.sportsapi.datamodel.SapiCourse;
import com.sportradar.uf.sportsapi.datamodel.SapiVenue;
import com.sportradar.utils.Urn;
import com.sportradar.utils.domain.UniqueObjects;
import com.sportradar.utils.domain.names.Languages;
import com.sportradar.utils.domain.names.Names;
import java.util.Locale;
import lombok.val;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

public class VenueCiCourseCiNameTest {

    public static final String DIFFERENT_LANGUAGES =
        "com.sportradar.unifiedodds.sdk.caching.ci.VenueCiCourseCiNameTest#differentLanguages";
    public static final String DIFFERENT_MERGING_LANGUAGES =
        "com.sportradar.unifiedodds.sdk.caching.ci.VenueCiCourseCiNameTest#differentLanguages";

    private static final UniqueObjects<String> UNIQUE_NAMES = unique(() -> Names.any());
    private static final String NAME = UNIQUE_NAMES.getOne();
    private static final String ANOTHER_NAME = UNIQUE_NAMES.getOne();

    private VenueCiCourseCiNameTest() {}

    public static SapiCourse sapiCoursesWithId(String id) {
        SapiCourse sapiCourse = new SapiCourse();
        sapiCourse.setId(id);
        return sapiCourse;
    }

    public static SapiCourse sapiCoursesWithName(String name) {
        SapiCourse sapiCourse = new SapiCourse();
        sapiCourse.setName(name);
        return sapiCourse;
    }

    public static SapiCourse sapiCoursesWithIdAndName(Urn id, String name) {
        SapiCourse sapiCourse = new SapiCourse();
        sapiCourse.setName(name);
        sapiCourse.setId(id.toString());
        return sapiCourse;
    }

    private static SapiVenue sapiVenueWithAnyIdAnd(SapiCourse... courses) {
        SapiVenue venue = sapiVenueWithAnyId();
        venue.getCourse().addAll(asList(courses));
        return venue;
    }

    private static SapiVenue sapiVenueWithAnyId() {
        SapiVenue sapiVenue = new SapiVenue();
        sapiVenue.setId(urnForAnyVenue().toString());
        return sapiVenue;
    }

    public static Object[] differentLanguages() {
        return new Object[][] { { FRENCH }, { ENGLISH } };
    }

    @Nested
    public class GivenNoMergeWillOccurWhenConstructingCi {

        @ParameterizedTest
        @MethodSource(DIFFERENT_LANGUAGES)
        public void thenCiPreservesName(Locale language) {
            SapiVenue sapiVenue = sapiVenueWithAnyIdAnd(sapiCoursesWithName(NAME));

            val venueCi = new VenueCi(sapiVenue, language);

            assertThat(venueCi).containsCourse(atIndex(0), with(NAME, in(language)));
        }

        @Test
        public void thenCiDiscardsNullName() {
            SapiVenue sapiVenue = sapiVenueWithAnyIdAnd(new SapiCourse());

            val venueCi = new VenueCi(sapiVenue, ENGLISH);

            assertThat(venueCi).containsCourse(atIndex(0), withNoTranslationTo(ENGLISH));
        }

        @ParameterizedTest
        @MethodSource(DIFFERENT_LANGUAGES)
        public void thenCiPreservesNameAfterImportingExportedVenueCi(Locale language) throws Exception {
            SapiVenue sapiVenue = sapiVenueWithAnyIdAnd(sapiCoursesWithName(NAME));
            val venueCi = new VenueCi(sapiVenue, language);

            VenueCi importedVenueCi = VenueCis.exportSerializeAndUseConstructorToReimport(venueCi);

            assertThat(importedVenueCi).containsCourse(atIndex(0), with(NAME, in(language)));
        }

        @Test
        public void thenCiPreservesNoNameAfterImportingExportedVenueCi() throws Exception {
            SapiVenue sapiVenue = sapiVenueWithAnyIdAnd(new SapiCourse());
            val venueCi = new VenueCi(sapiVenue, Languages.any());

            VenueCi importedVenueCi = VenueCis.exportSerializeAndUseConstructorToReimport(venueCi);

            assertThat(importedVenueCi).containsCourse(atIndex(0), withNoTranslationTo(ENGLISH));
        }

        @Test
        public void thenCiPreservesNameForMultipleCourses() {
            SapiVenue sapiVenue = sapiVenueWithAnyIdAnd(
                sapiCoursesWithName(NAME),
                sapiCoursesWithName(ANOTHER_NAME)
            );

            val venueCi = new VenueCi(sapiVenue, ENGLISH);

            assertThat(venueCi)
                .containsCourse(atIndex(0), with(NAME, in(ENGLISH)))
                .containsCourse(atIndex(1), with(ANOTHER_NAME, in(ENGLISH)));
        }
    }

    @Nested
    public class GivenMergeWillOccur {

        @Nested
        public class AndNoCoursesWereProvidedPreviously {

            @ParameterizedTest
            @MethodSource(DIFFERENT_MERGING_LANGUAGES)
            public void thenMergingPreservesName(Locale language) {
                val venueCi = new VenueCi(sapiVenueWithAnyId(), ENGLISH);
                SapiVenue sapiVenue = sapiVenueWithAnyIdAnd(sapiCoursesWithName(NAME));

                venueCi.merge(sapiVenue, language);

                assertThat(venueCi).containsCourse(atIndex(0), with(NAME, in(language)));
            }

            @ParameterizedTest
            @MethodSource(DIFFERENT_MERGING_LANGUAGES)
            public void thenMergingDiscardsNullName(Locale language) {
                val venueCi = new VenueCi(sapiVenueWithAnyId(), language);
                SapiVenue sapiVenue = sapiVenueWithAnyIdAnd(new SapiCourse());

                venueCi.merge(sapiVenue, ENGLISH);

                assertThat(venueCi).containsCourse(atIndex(0), withNoTranslationTo(ENGLISH));
            }

            @ParameterizedTest
            @MethodSource(DIFFERENT_MERGING_LANGUAGES)
            public void thenMergingPreservesNamesForMultipleNewCourses(Locale language) {
                val venueCi = new VenueCi(sapiVenueWithAnyId(), language);
                SapiVenue sapiVenue = sapiVenueWithAnyIdAnd(
                    sapiCoursesWithName(NAME),
                    sapiCoursesWithName(ANOTHER_NAME)
                );

                venueCi.merge(sapiVenue, ENGLISH);

                assertThat(venueCi)
                    .containsCourse(atIndex(0), with(NAME, in(ENGLISH)))
                    .containsCourse(atIndex(1), with(ANOTHER_NAME, in(ENGLISH)));
            }
        }

        @Nested
        public class AndOneCourseWasProvidedPreviously {

            @Nested
            public class WithoutAnyTranslations {

                @ParameterizedTest
                @MethodSource(DIFFERENT_MERGING_LANGUAGES)
                public void thenMergingIntroducesTranslation(Locale language) {
                    SapiVenue oldCourses = sapiVenueWithAnyIdAnd(new SapiCourse());
                    val venueCi = new VenueCi(oldCourses, ENGLISH);
                    SapiVenue sapiVenue = sapiVenueWithAnyIdAnd(sapiCoursesWithName(NAME));

                    venueCi.merge(sapiVenue, language);

                    assertThat(venueCi).containsCourse(atIndex(0), with(NAME, in(language)));
                }

                @Test
                public void thenMergingKeepsCourseUntranslated() {
                    SapiVenue oldCourses = sapiVenueWithAnyIdAnd(new SapiCourse());
                    val venueCi = new VenueCi(oldCourses, ENGLISH);
                    SapiVenue sapiVenue = sapiVenueWithAnyIdAnd(new SapiCourse());

                    venueCi.merge(sapiVenue, ENGLISH);

                    assertThat(venueCi).containsCourse(atIndex(0), withNoTranslationTo(ENGLISH));
                }
            }

            @Nested
            public class WithSingleTranslations {

                @ParameterizedTest
                @MethodSource(DIFFERENT_LANGUAGES)
                public void thenMergingReplacesNameForSameLanguage(Locale language) {
                    SapiVenue oldCourses = sapiVenueWithAnyIdAnd(sapiCoursesWithName(NAME));
                    val venueCi = new VenueCi(oldCourses, language);
                    SapiVenue sapiVenue = sapiVenueWithAnyIdAnd(sapiCoursesWithName(ANOTHER_NAME));

                    venueCi.merge(sapiVenue, language);

                    assertThat(venueCi).containsCourse(atIndex(0), with(ANOTHER_NAME, in(language)));
                }

                @Test
                public void thenMergingReplacesTranslationWithNull() {
                    SapiVenue oldCourses = sapiVenueWithAnyIdAnd(sapiCoursesWithName(NAME));
                    val venueCi = new VenueCi(oldCourses, ENGLISH);
                    SapiVenue sapiVenue = sapiVenueWithAnyIdAnd(new SapiCourse());

                    venueCi.merge(sapiVenue, ENGLISH);

                    assertThat(venueCi).containsCourse(atIndex(0), with(null, in(ENGLISH)));
                }

                @Test
                public void thenMergingAddsAdditionalTranslation() {
                    SapiVenue oldCourses = sapiVenueWithAnyIdAnd(sapiCoursesWithName(NAME));
                    val venueCi = new VenueCi(oldCourses, ENGLISH);
                    SapiVenue sapiVenue = sapiVenueWithAnyIdAnd(sapiCoursesWithName(ANOTHER_NAME));

                    venueCi.merge(sapiVenue, FRENCH);

                    assertThat(venueCi)
                        .containsCourse(atIndex(0), with(NAME, in(ENGLISH)))
                        .containsCourse(atIndex(0), with(ANOTHER_NAME, in(FRENCH)));
                }

                @Test
                public void thenPreservesMultipleTranslationsAfterExportImport() throws Exception {
                    SapiVenue oldCourses = sapiVenueWithAnyIdAnd(sapiCoursesWithName(NAME));
                    val venueCi = new VenueCi(oldCourses, ENGLISH);
                    SapiVenue sapiVenue = sapiVenueWithAnyIdAnd(sapiCoursesWithName(ANOTHER_NAME));
                    venueCi.merge(sapiVenue, FRENCH);

                    VenueCi importedVenueCi = VenueCis.exportSerializeAndUseConstructorToReimport(venueCi);

                    assertThat(importedVenueCi)
                        .containsCourse(atIndex(0), with(NAME, in(ENGLISH)))
                        .containsCourse(atIndex(0), with(ANOTHER_NAME, in(FRENCH)));
                }
            }
        }

        @Nested
        public class AndMultipleCoursesWereProvidedPreviously {

            private final Urn id = Urn.parse("sr:venue:1");
            private final Urn anotherId = Urn.parse("sr:venue:2");

            private final String frenchName = "French Anapa Mars";
            private final String anotherFrenchName = "French Daidalos Shyama";

            @Nested
            public class AndCoursesContainsUniqueIds {

                @Test
                public void thenMergingAddsAdditionalTranslationToAllCourses() {
                    SapiVenue oldCourses = sapiVenueWithAnyIdAnd(
                        sapiCoursesWithIdAndName(id, NAME),
                        sapiCoursesWithIdAndName(anotherId, ANOTHER_NAME)
                    );
                    val venueCi = new VenueCi(oldCourses, ENGLISH);
                    SapiVenue sapiVenue = sapiVenueWithAnyIdAnd(
                        sapiCoursesWithIdAndName(id, frenchName),
                        sapiCoursesWithIdAndName(anotherId, anotherFrenchName)
                    );

                    venueCi.merge(sapiVenue, FRENCH);

                    assertThat(venueCi)
                        .containsCourse(atIndex(0), with(NAME, in(ENGLISH)))
                        .containsCourse(atIndex(0), with(frenchName, in(FRENCH)))
                        .containsCourse(atIndex(1), with(ANOTHER_NAME, in(ENGLISH)))
                        .containsCourse(atIndex(1), with(anotherFrenchName, in(FRENCH)));
                }

                @Test
                public void thenMergingRemovesCourse() {
                    SapiVenue oldCourses = sapiVenueWithAnyIdAnd(
                        sapiCoursesWithIdAndName(id, NAME),
                        sapiCoursesWithIdAndName(anotherId, ANOTHER_NAME)
                    );
                    val venueCi = new VenueCi(oldCourses, ENGLISH);
                    SapiVenue sapiVenue = sapiVenueWithAnyIdAnd(sapiCoursesWithIdAndName(id, frenchName));

                    venueCi.merge(sapiVenue, FRENCH);

                    assertThat(venueCi)
                        .containsAmountOfCourses(1)
                        .containsCourse(atIndex(0), with(NAME, in(ENGLISH)))
                        .containsCourse(atIndex(0), with(frenchName, in(FRENCH)));
                }

                @Test
                public void thenMergingAddsCourse() {
                    SapiVenue oldCourses = sapiVenueWithAnyIdAnd(sapiCoursesWithIdAndName(id, NAME));
                    val venueCi = new VenueCi(oldCourses, ENGLISH);
                    SapiVenue sapiVenue = sapiVenueWithAnyIdAnd(
                        sapiCoursesWithIdAndName(id, frenchName),
                        sapiCoursesWithIdAndName(anotherId, anotherFrenchName)
                    );

                    venueCi.merge(sapiVenue, FRENCH);

                    assertThat(venueCi)
                        .containsCourse(atIndex(0), with(NAME, in(ENGLISH)))
                        .containsCourse(atIndex(0), with(frenchName, in(FRENCH)))
                        .containsCourse(atIndex(1), with(anotherFrenchName, in(FRENCH)));
                }

                @Test
                public void thenMergesByIdNotByOrderOfTheList() {
                    SapiVenue oldCourses = sapiVenueWithAnyIdAnd(
                        sapiCoursesWithIdAndName(anotherId, ANOTHER_NAME),
                        sapiCoursesWithIdAndName(id, NAME)
                    );
                    val venueCi = new VenueCi(oldCourses, ENGLISH);
                    SapiVenue sapiVenue = sapiVenueWithAnyIdAnd(
                        sapiCoursesWithIdAndName(id, frenchName),
                        sapiCoursesWithIdAndName(anotherId, anotherFrenchName)
                    );

                    venueCi.merge(sapiVenue, FRENCH);

                    assertThat(venueCi)
                        .containsCourse(atIndex(0), with(NAME, in(ENGLISH)))
                        .containsCourse(atIndex(0), with(frenchName, in(FRENCH)))
                        .containsCourse(atIndex(1), with(ANOTHER_NAME, in(ENGLISH)))
                        .containsCourse(atIndex(1), with(anotherFrenchName, in(FRENCH)));
                }
            }

            @Nested
            public class AndInflightCoursesContainsDuplicateIds {

                @Test
                public void thenReplicatesPreviousTranslationsToInflightCourses() {
                    SapiVenue oldCourses = sapiVenueWithAnyIdAnd(sapiCoursesWithIdAndName(id, NAME));
                    val venueCi = new VenueCi(oldCourses, ENGLISH);
                    SapiVenue sapiVenue = sapiVenueWithAnyIdAnd(
                        sapiCoursesWithIdAndName(id, frenchName),
                        sapiCoursesWithIdAndName(id, anotherFrenchName)
                    );

                    venueCi.merge(sapiVenue, FRENCH);

                    assertThat(venueCi)
                        .containsCourse(atIndex(0), with(NAME, in(ENGLISH)))
                        .containsCourse(atIndex(0), with(frenchName, in(FRENCH)))
                        .containsCourse(atIndex(1), with(NAME, in(ENGLISH)))
                        .containsCourse(atIndex(1), with(anotherFrenchName, in(FRENCH)));
                }
            }

            @Nested
            public class AndPreviouslyExistingCoursesContainsDuplicateIds {

                @Test
                public void thenTranslationsAreGatheredOnlyFromFirstOneMeanwhileTranslationsFromOthersAreLost() {
                    SapiVenue oldCourses = sapiVenueWithAnyIdAnd(
                        sapiCoursesWithIdAndName(id, NAME),
                        sapiCoursesWithIdAndName(id, ANOTHER_NAME)
                    );
                    val venueCi = new VenueCi(oldCourses, ENGLISH);
                    SapiVenue sapiVenue = sapiVenueWithAnyIdAnd(sapiCoursesWithIdAndName(id, frenchName));

                    venueCi.merge(sapiVenue, FRENCH);

                    assertThat(venueCi)
                        .containsAmountOfCourses(1)
                        .containsCourse(atIndex(0), with(NAME, in(ENGLISH)))
                        .containsCourse(atIndex(0), with(frenchName, in(FRENCH)));
                }
            }
        }
    }
}
