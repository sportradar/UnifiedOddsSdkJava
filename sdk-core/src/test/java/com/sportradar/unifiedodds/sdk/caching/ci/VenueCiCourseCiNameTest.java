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
import com.sportradar.utils.Urns;
import com.sportradar.utils.domain.UniqueObjects;
import com.sportradar.utils.domain.names.Languages;
import com.sportradar.utils.domain.names.Names;
import java.util.Locale;
import junitparams.JUnitParamsRunner;
import junitparams.Parameters;
import lombok.val;
import org.junit.Test;
import org.junit.experimental.runners.Enclosed;
import org.junit.runner.RunWith;

@RunWith(Enclosed.class)
public class VenueCiCourseCiNameTest {

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

    @RunWith(JUnitParamsRunner.class)
    public static class GivenNoMergeWillOccurWhenConstructingCi {

        @Test
        @Parameters(method = "differentLanguages")
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

        @Test
        @Parameters(method = "differentLanguages")
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

        public static Object[] differentLanguages() {
            return new Object[][] { { FRENCH }, { ENGLISH } };
        }
    }

    @RunWith(Enclosed.class)
    public static class GivenMergeWillOccur {

        @RunWith(JUnitParamsRunner.class)
        public static class AndNoCoursesWereProvidedPreviously {

            @Test
            @Parameters(method = "differentMergingLanguages")
            public void thenMergingPreservesName(Locale language) {
                val venueCi = new VenueCi(sapiVenueWithAnyId(), ENGLISH);
                SapiVenue sapiVenue = sapiVenueWithAnyIdAnd(sapiCoursesWithName(NAME));

                venueCi.merge(sapiVenue, language);

                assertThat(venueCi).containsCourse(atIndex(0), with(NAME, in(language)));
            }

            @Test
            @Parameters(method = "differentMergingLanguages")
            public void thenMergingDiscardsNullName(Locale language) {
                val venueCi = new VenueCi(sapiVenueWithAnyId(), language);
                SapiVenue sapiVenue = sapiVenueWithAnyIdAnd(new SapiCourse());

                venueCi.merge(sapiVenue, ENGLISH);

                assertThat(venueCi).containsCourse(atIndex(0), withNoTranslationTo(ENGLISH));
            }

            @Test
            @Parameters(method = "differentMergingLanguages")
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

            public static Object[] differentMergingLanguages() {
                return new Object[][] { { ENGLISH }, { FRENCH } };
            }
        }

        @RunWith(Enclosed.class)
        public static class AndOneCourseWasProvidedPreviously {

            @RunWith(JUnitParamsRunner.class)
            public static class WithoutAnyTranslations {

                @Test
                @Parameters(method = "differentMergingLanguages")
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

                public static Object[] differentMergingLanguages() {
                    return new Object[][] { { ENGLISH }, { FRENCH } };
                }
            }

            @RunWith(JUnitParamsRunner.class)
            public static class WithSingleTranslations {

                @Test
                @Parameters(method = "differentLanguages")
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

                public static Object[] differentLanguages() {
                    return new Object[][] { { ENGLISH }, { FRENCH } };
                }
            }
        }

        @RunWith(Enclosed.class)
        public static class AndMultipleCoursesWereProvidedPreviously {

            private static final UniqueObjects<Urn> UNIQUE_IDS = unique(() -> Urns.Venues.urnForAnyVenue());
            private static final Urn ID = UNIQUE_IDS.getOne();
            private static final Urn ANOTHER_ID = UNIQUE_IDS.getOne();

            private static final String FRENCH_NAME = "French " + UNIQUE_NAMES.getOne();
            private static final String ANOTHER_FRENCH_NAME = "French " + UNIQUE_NAMES.getOne();

            public static class AndCoursesContainsUniqueIds {

                @Test
                public void thenMergingAddsAdditionalTranslationToAllCourses() {
                    SapiVenue oldCourses = sapiVenueWithAnyIdAnd(
                        sapiCoursesWithIdAndName(ID, NAME),
                        sapiCoursesWithIdAndName(ANOTHER_ID, ANOTHER_NAME)
                    );
                    val venueCi = new VenueCi(oldCourses, ENGLISH);
                    SapiVenue sapiVenue = sapiVenueWithAnyIdAnd(
                        sapiCoursesWithIdAndName(ID, FRENCH_NAME),
                        sapiCoursesWithIdAndName(ANOTHER_ID, ANOTHER_FRENCH_NAME)
                    );

                    venueCi.merge(sapiVenue, FRENCH);

                    assertThat(venueCi)
                        .containsCourse(atIndex(0), with(NAME, in(ENGLISH)))
                        .containsCourse(atIndex(0), with(FRENCH_NAME, in(FRENCH)))
                        .containsCourse(atIndex(1), with(ANOTHER_NAME, in(ENGLISH)))
                        .containsCourse(atIndex(1), with(ANOTHER_FRENCH_NAME, in(FRENCH)));
                }

                @Test
                public void thenMergingRemovesCourse() {
                    SapiVenue oldCourses = sapiVenueWithAnyIdAnd(
                        sapiCoursesWithIdAndName(ID, NAME),
                        sapiCoursesWithIdAndName(ANOTHER_ID, ANOTHER_NAME)
                    );
                    val venueCi = new VenueCi(oldCourses, ENGLISH);
                    SapiVenue sapiVenue = sapiVenueWithAnyIdAnd(sapiCoursesWithIdAndName(ID, FRENCH_NAME));

                    venueCi.merge(sapiVenue, FRENCH);

                    assertThat(venueCi)
                        .containsAmountOfCourses(1)
                        .containsCourse(atIndex(0), with(NAME, in(ENGLISH)))
                        .containsCourse(atIndex(0), with(FRENCH_NAME, in(FRENCH)));
                }

                @Test
                public void thenMergingAddsCourse() {
                    SapiVenue oldCourses = sapiVenueWithAnyIdAnd(sapiCoursesWithIdAndName(ID, NAME));
                    val venueCi = new VenueCi(oldCourses, ENGLISH);
                    SapiVenue sapiVenue = sapiVenueWithAnyIdAnd(
                        sapiCoursesWithIdAndName(ID, FRENCH_NAME),
                        sapiCoursesWithIdAndName(ANOTHER_ID, ANOTHER_FRENCH_NAME)
                    );

                    venueCi.merge(sapiVenue, FRENCH);

                    assertThat(venueCi)
                        .containsCourse(atIndex(0), with(NAME, in(ENGLISH)))
                        .containsCourse(atIndex(0), with(FRENCH_NAME, in(FRENCH)))
                        .containsCourse(atIndex(1), with(ANOTHER_FRENCH_NAME, in(FRENCH)));
                }

                @Test
                public void thenMergesByIdNotByOrderOfTheList() {
                    SapiVenue oldCourses = sapiVenueWithAnyIdAnd(
                        sapiCoursesWithIdAndName(ANOTHER_ID, ANOTHER_NAME),
                        sapiCoursesWithIdAndName(ID, NAME)
                    );
                    val venueCi = new VenueCi(oldCourses, ENGLISH);
                    SapiVenue sapiVenue = sapiVenueWithAnyIdAnd(
                        sapiCoursesWithIdAndName(ID, FRENCH_NAME),
                        sapiCoursesWithIdAndName(ANOTHER_ID, ANOTHER_FRENCH_NAME)
                    );

                    venueCi.merge(sapiVenue, FRENCH);

                    assertThat(venueCi)
                        .containsCourse(atIndex(0), with(NAME, in(ENGLISH)))
                        .containsCourse(atIndex(0), with(FRENCH_NAME, in(FRENCH)))
                        .containsCourse(atIndex(1), with(ANOTHER_NAME, in(ENGLISH)))
                        .containsCourse(atIndex(1), with(ANOTHER_FRENCH_NAME, in(FRENCH)));
                }
            }

            public static class AndInflightCoursesContainsDuplicateIds {

                @Test
                public void thenReplicatesPreviousTranslationsToInflightCourses() {
                    SapiVenue oldCourses = sapiVenueWithAnyIdAnd(sapiCoursesWithIdAndName(ID, NAME));
                    val venueCi = new VenueCi(oldCourses, ENGLISH);
                    SapiVenue sapiVenue = sapiVenueWithAnyIdAnd(
                        sapiCoursesWithIdAndName(ID, FRENCH_NAME),
                        sapiCoursesWithIdAndName(ID, ANOTHER_FRENCH_NAME)
                    );

                    venueCi.merge(sapiVenue, FRENCH);

                    assertThat(venueCi)
                        .containsCourse(atIndex(0), with(NAME, in(ENGLISH)))
                        .containsCourse(atIndex(0), with(FRENCH_NAME, in(FRENCH)))
                        .containsCourse(atIndex(1), with(NAME, in(ENGLISH)))
                        .containsCourse(atIndex(1), with(ANOTHER_FRENCH_NAME, in(FRENCH)));
                }
            }

            public static class AndPreviouslyExistingCoursesContainsDuplicateIds {

                @Test
                public void thenTranslationsAreGatheredOnlyFromFirstOneMeanwhileTranslationsFromOthersAreLost() {
                    SapiVenue oldCourses = sapiVenueWithAnyIdAnd(
                        sapiCoursesWithIdAndName(ID, NAME),
                        sapiCoursesWithIdAndName(ID, ANOTHER_NAME)
                    );
                    val venueCi = new VenueCi(oldCourses, ENGLISH);
                    SapiVenue sapiVenue = sapiVenueWithAnyIdAnd(sapiCoursesWithIdAndName(ID, FRENCH_NAME));

                    venueCi.merge(sapiVenue, FRENCH);

                    assertThat(venueCi)
                        .containsAmountOfCourses(1)
                        .containsCourse(atIndex(0), with(NAME, in(ENGLISH)))
                        .containsCourse(atIndex(0), with(FRENCH_NAME, in(FRENCH)));
                }
            }
        }
    }
}
