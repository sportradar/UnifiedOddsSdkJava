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
import com.sportradar.utils.Urn;
import com.sportradar.utils.domain.UniqueObjects;
import com.sportradar.utils.domain.names.Languages;
import java.util.Locale;
import lombok.val;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

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

    @Nested
    public class GivenNoMergeWillOccur {

        private final UniqueObjects<Urn> uniqueVenueIds = unique(() -> urnForAnyVenue());
        private final Urn id = uniqueVenueIds.getOne();
        private final Urn anotherId = uniqueVenueIds.getOne();

        @Test
        public void thenCiPreservesIdProvidedOnConstruction() {
            SapiVenue sapiVenue = sapiVenueWithAnyIdAndCourses(sapiCoursesWithId(id.toString()));

            val venueCi = new VenueCi(sapiVenue, Languages.any());

            assertThat(venueCi.getCourses().get(0).getId()).isEqualTo(id);
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
            SapiVenue sapiVenue = sapiVenueWithAnyIdAndCourses(sapiCoursesWithId(id.toString()));
            val venueCi = new VenueCi(sapiVenue, Languages.any());

            VenueCi importedVenueCi = VenueCis.exportSerializeAndUseConstructorToReimport(venueCi);

            assertThat(importedVenueCi.getCourses().get(0).getId()).isEqualTo(id);
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
                sapiCoursesWithId(id.toString()),
                sapiCoursesWithId(anotherId.toString())
            );

            val venueCi = new VenueCi(sapiVenue, Languages.any());

            assertThat(venueCi.getCourses().get(0).getId()).isEqualTo(id);
            assertThat(venueCi.getCourses().get(1).getId()).isEqualTo(anotherId);
        }
    }

    @Nested
    public class GivenMergeWillOccur {

        private final Locale language = Languages.any();

        private final UniqueObjects<Urn> uniqueIds = unique(() -> urnForAnyVenue());
        private final Urn id = uniqueIds.getOne();
        private final Urn anotherId = uniqueIds.getOne();

        @Nested
        public class AndNoCoursesWereProvidedPreviously {

            @Test
            public void whenMergingWithSingleNewCourseThenPreservesItsId() {
                val venueCi = new VenueCi(sapiVenueWithAnyId(), language);
                SapiVenue sapiVenue = sapiVenueWithAnyIdAndCourses(sapiCoursesWithId(id.toString()));

                venueCi.merge(sapiVenue, language);

                assertThat(venueCi.getCourses().get(0).getId()).isEqualTo(id);
            }

            @Test
            public void whenMergingWithSingleNewCourseThenPreservesNullId() {
                val venueCi = new VenueCi(sapiVenueWithAnyId(), language);
                SapiVenue sapiVenue = sapiVenueWithAnyIdAndCourses(new SapiCourse());

                venueCi.merge(sapiVenue, language);

                assertThat(venueCi.getCourses().get(0).getId()).isNull();
            }

            @Test
            public void thenInflightVenueToBeMergedMustHaveCoursesWithValidUrnId() {
                val venueCi = new VenueCi(sapiVenueWithAnyId(), language);
                SapiVenue sapiVenue = sapiVenueWithAnyIdAndCourses(sapiCoursesWithId("invalidUrn"));

                assertThatThrownBy(() -> venueCi.merge(sapiVenue, language))
                    .isInstanceOf(UnsupportedUrnFormatException.class);
            }

            @Test
            public void whenMergingWithMultipleNewCourseThenPreservesNewCourseIds() {
                val venueCi = new VenueCi(sapiVenueWithAnyId(), language);
                SapiVenue sapiVenue = sapiVenueWithAnyIdAndCourses(
                    sapiCoursesWithId(id.toString()),
                    sapiCoursesWithId(anotherId.toString())
                );

                venueCi.merge(sapiVenue, language);

                assertThat(venueCi.getCourses().get(0).getId()).isEqualTo(id);
                assertThat(venueCi.getCourses().get(1).getId()).isEqualTo(anotherId);
            }
        }

        @Nested
        public class AndSomeCoursesWereProvidedPreviously {

            @Test
            public void thenMergingPreservesIdsForCoursesAlsoExistingInInflightRequest() {
                SapiVenue oldCourses = sapiVenueWithAnyIdAndCourses(sapiCoursesWithId(id.toString()));
                val venueCi = new VenueCi(oldCourses, language);
                SapiVenue sapiVenue = sapiVenueWithAnyIdAndCourses(sapiCoursesWithId(id.toString()));

                venueCi.merge(sapiVenue, language);

                assertThat(venueCi.getCourses().get(0).getId()).isEqualTo(id);
            }

            @Test
            public void thenCreatesIdsForCoursesWhenMergingInCoursesOnlyExistingInInflightRequest() {
                SapiVenue oldCourses = sapiVenueWithAnyIdAndCourses(sapiCoursesWithId(id.toString()));
                val venueCi = new VenueCi(oldCourses, language);
                SapiVenue sapiVenue = sapiVenueWithAnyIdAndCourses(
                    sapiCoursesWithId(id.toString()),
                    sapiCoursesWithId(anotherId.toString())
                );

                venueCi.merge(sapiVenue, language);

                assertThat(venueCi.getCourses().get(0).getId()).isEqualTo(id);
                assertThat(venueCi.getCourses().get(1).getId()).isEqualTo(anotherId);
            }
        }
    }
}
