/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */
package com.sportradar.unifiedodds.sdk.caching.ci;

import static com.sportradar.utils.Urns.Venues.urnForAnyVenue;
import static com.sportradar.utils.Urns.unique;
import static java.util.Arrays.asList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.sportradar.uf.sportsapi.datamodel.SapiCourse;
import com.sportradar.uf.sportsapi.datamodel.SapiVenue;
import com.sportradar.unifiedodds.sdk.exceptions.UnsupportedUrnFormatException;
import com.sportradar.unifiedodds.sdk.testutil.javautil.Languages;
import com.sportradar.utils.Urn;
import com.sportradar.utils.domain.UniqueObjects;
import java.util.Locale;
import lombok.val;
import org.junit.Test;
import org.junit.experimental.runners.Enclosed;
import org.junit.runner.RunWith;

@RunWith(Enclosed.class)
public class VenueCiCourseCiIdTest {

    private VenueCiCourseCiIdTest() {}

    public static SapiCourse sapiCoursesWithId(String id) {
        SapiCourse sapiCourse = new SapiCourse();
        sapiCourse.setId(id);
        return sapiCourse;
    }

    private static SapiVenue sapiVenueWithAnyIdAndCourses(SapiCourse... courses) {
        SapiVenue venue = sapiVenueWithAnyId();
        venue.getCourse().addAll(asList(courses));
        return venue;
    }

    private static SapiVenue sapiVenueWithAnyId() {
        SapiVenue sapiVenue = new SapiVenue();
        sapiVenue.setId(urnForAnyVenue().toString());
        return sapiVenue;
    }

    public static class GivenNoMergeWillOccur {

        private static final UniqueObjects<Urn> UNIQUE_VENUE_IDS = unique(() -> urnForAnyVenue());
        private static final Urn ID = UNIQUE_VENUE_IDS.getOne();
        private static final Urn ANOTHER_ID = UNIQUE_VENUE_IDS.getOne();

        @Test
        public void thenCiPreservesIdProvidedOnConstruction() {
            SapiVenue sapiVenue = sapiVenueWithAnyIdAndCourses(sapiCoursesWithId(ID.toString()));

            val venueCi = new VenueCi(sapiVenue, Languages.any());

            assertThat(venueCi.getCourses().get(0).getId()).isEqualTo(ID);
        }

        @Test
        public void thenCiCanContainNullIdProvidedOnConstruction() {
            SapiVenue sapiVenue = sapiVenueWithAnyIdAndCourses(new SapiCourse());

            VenueCi venueCi = new VenueCi(sapiVenue, Languages.any());

            assertThat(venueCi.getCourses().get(0).getId()).isNull();
        }

        @Test
        public void itMustHaveValidUrnIdProvidedOnConstruction() {
            SapiVenue sapiVenue = sapiVenueWithAnyIdAndCourses(sapiCoursesWithId("invalidUrn"));

            assertThatThrownBy(() -> new VenueCi(sapiVenue, Languages.any()))
                .isInstanceOf(UnsupportedUrnFormatException.class);
        }

        @Test
        public void thenCiPreservesIdAfterImportingExportedVenueCi() throws Exception {
            SapiVenue sapiVenue = sapiVenueWithAnyIdAndCourses(sapiCoursesWithId(ID.toString()));
            val venueCi = new VenueCi(sapiVenue, Languages.any());

            VenueCi importedVenueCi = VenueCis.exportSerializeAndUseConstructorToReimport(venueCi);

            assertThat(importedVenueCi.getCourses().get(0).getId()).isEqualTo(ID);
        }

        @Test
        public void thenCiPreservesNullIdAfterImportingExportedVenueCi() throws Exception {
            SapiVenue sapiVenue = sapiVenueWithAnyIdAndCourses(new SapiCourse());
            val venueCi = new VenueCi(sapiVenue, Languages.any());

            VenueCi importedVenueCi = VenueCis.exportSerializeAndUseConstructorToReimport(venueCi);

            assertThat(importedVenueCi.getCourses().get(0).getId()).isNull();
        }

        @Test
        public void thenCiPreservesIdForMultipleCoursesProvidedOnConstruction() {
            SapiVenue sapiVenue = sapiVenueWithAnyIdAndCourses(
                sapiCoursesWithId(ID.toString()),
                sapiCoursesWithId(ANOTHER_ID.toString())
            );

            val venueCi = new VenueCi(sapiVenue, Languages.any());

            assertThat(venueCi.getCourses().get(0).getId()).isEqualTo(ID);
            assertThat(venueCi.getCourses().get(1).getId()).isEqualTo(ANOTHER_ID);
        }
    }

    @RunWith(Enclosed.class)
    public static class GivenMergeWillOccur {

        public static final Locale LANGUAGE = Languages.any();

        private static final UniqueObjects<Urn> UNIQUE_IDS = unique(() -> urnForAnyVenue());
        private static final Urn ID = UNIQUE_IDS.getOne();
        private static final Urn ANOTHER_ID = UNIQUE_IDS.getOne();

        public static class AndNoCoursesWereProvidedPreviously {

            @Test
            public void whenMergingWithSingleNewCourseThenPreservesItsId() {
                val venueCi = new VenueCi(sapiVenueWithAnyId(), LANGUAGE);
                SapiVenue sapiVenue = sapiVenueWithAnyIdAndCourses(sapiCoursesWithId(ID.toString()));

                venueCi.merge(sapiVenue, LANGUAGE);

                assertThat(venueCi.getCourses().get(0).getId()).isEqualTo(ID);
            }

            @Test
            public void whenMergingWithSingleNewCourseThenPreservesNullId() {
                val venueCi = new VenueCi(sapiVenueWithAnyId(), LANGUAGE);
                SapiVenue sapiVenue = sapiVenueWithAnyIdAndCourses(new SapiCourse());

                venueCi.merge(sapiVenue, LANGUAGE);

                assertThat(venueCi.getCourses().get(0).getId()).isNull();
            }

            @Test
            public void thenInflightVenueToBeMergedMustHaveCoursesWithValidUrnId() {
                val venueCi = new VenueCi(sapiVenueWithAnyId(), LANGUAGE);
                SapiVenue sapiVenue = sapiVenueWithAnyIdAndCourses(sapiCoursesWithId("invalidUrn"));

                assertThatThrownBy(() -> venueCi.merge(sapiVenue, LANGUAGE))
                    .isInstanceOf(UnsupportedUrnFormatException.class);
            }

            @Test
            public void whenMergingWithMultipleNewCourseThenPreservesNewCourseIds() {
                val venueCi = new VenueCi(sapiVenueWithAnyId(), LANGUAGE);
                SapiVenue sapiVenue = sapiVenueWithAnyIdAndCourses(
                    sapiCoursesWithId(ID.toString()),
                    sapiCoursesWithId(ANOTHER_ID.toString())
                );

                venueCi.merge(sapiVenue, LANGUAGE);

                assertThat(venueCi.getCourses().get(0).getId()).isEqualTo(ID);
                assertThat(venueCi.getCourses().get(1).getId()).isEqualTo(ANOTHER_ID);
            }
        }

        public static class AndSomeCoursesWereProvidedPreviously {

            @Test
            public void thenMergingPreservesIdsForCoursesAlsoExistingInInflightRequest() {
                SapiVenue oldCourses = sapiVenueWithAnyIdAndCourses(sapiCoursesWithId(ID.toString()));
                val venueCi = new VenueCi(oldCourses, LANGUAGE);
                SapiVenue sapiVenue = sapiVenueWithAnyIdAndCourses(sapiCoursesWithId(ID.toString()));

                venueCi.merge(sapiVenue, LANGUAGE);

                assertThat(venueCi.getCourses().get(0).getId()).isEqualTo(ID);
            }

            @Test
            public void thenCreatesIdsForCoursesWhenMergingInCoursesOnlyExistingInInflightRequest() {
                SapiVenue oldCourses = sapiVenueWithAnyIdAndCourses(sapiCoursesWithId(ID.toString()));
                val venueCi = new VenueCi(oldCourses, LANGUAGE);
                SapiVenue sapiVenue = sapiVenueWithAnyIdAndCourses(
                    sapiCoursesWithId(ID.toString()),
                    sapiCoursesWithId(ANOTHER_ID.toString())
                );

                venueCi.merge(sapiVenue, LANGUAGE);

                assertThat(venueCi.getCourses().get(0).getId()).isEqualTo(ID);
                assertThat(venueCi.getCourses().get(1).getId()).isEqualTo(ANOTHER_ID);
            }
        }
    }
}
