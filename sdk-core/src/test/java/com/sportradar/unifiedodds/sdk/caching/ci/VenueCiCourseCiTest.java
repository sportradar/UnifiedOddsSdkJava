/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */
package com.sportradar.unifiedodds.sdk.caching.ci;

import static com.sportradar.unifiedodds.sdk.caching.ci.matchers.VenueCiAssert.assertThat;
import static com.sportradar.utils.Urns.Venues.urnForAnyVenue;
import static com.sportradar.utils.Urns.unique;
import static org.assertj.core.api.Assertions.assertThat;

import com.sportradar.uf.sportsapi.datamodel.SapiCourse;
import com.sportradar.uf.sportsapi.datamodel.SapiVenue;
import com.sportradar.utils.Urn;
import com.sportradar.utils.domain.UniqueObjects;
import com.sportradar.utils.domain.names.Languages;
import java.util.Locale;
import lombok.val;
import org.junit.Test;
import org.junit.experimental.runners.Enclosed;
import org.junit.runner.RunWith;

@RunWith(Enclosed.class)
public class VenueCiCourseCiTest {

    private static final UniqueObjects<Urn> UNIQUE_URNS = unique(() -> urnForAnyVenue());
    private static final Urn ID = UNIQUE_URNS.getOne();
    private static final Urn ANOTHER_ID = UNIQUE_URNS.getOne();

    private VenueCiCourseCiTest() {}

    public static SapiCourse sapiCoursesWithId(String id) {
        SapiCourse sapiCourse = new SapiCourse();
        sapiCourse.setId(id);
        return sapiCourse;
    }

    private static SapiVenue sapiVenueWithAnyIdAndCourses(SapiCourse... courses) {
        SapiVenue oldCourses = sapiVenueWithAnyId();
        for (SapiCourse course : courses) {
            oldCourses.getCourse().add(course);
        }
        return oldCourses;
    }

    private static SapiVenue sapiVenueWithAnyId() {
        SapiVenue sapiVenue = new SapiVenue();
        sapiVenue.setId(urnForAnyVenue().toString());
        return sapiVenue;
    }

    @RunWith(Enclosed.class)
    public static class GivenNoMergeWillOccur {

        public static class WhenZeroCoursesAreProvidedOnConstruction {

            @Test
            public void thenPreservesNoCourses() {
                SapiVenue sapiVenue = sapiVenueWithAnyId();

                val venueCi = new VenueCi(sapiVenue, Languages.any());

                assertThat(venueCi.getCourses()).isEmpty();
            }

            @Test
            public void thenPreservesNoCoursesAfterImportingExportedVenueCi() throws Exception {
                SapiVenue sapiVenue = sapiVenueWithAnyId();
                val venueCi = new VenueCi(sapiVenue, Languages.any());

                VenueCi reImportedVenueCi = VenueCis.exportSerializeAndUseConstructorToReimport(venueCi);

                assertThat(reImportedVenueCi.getCourses()).isEmpty();
            }
        }

        public static class WhenSingleCourseIsProvidedOnConstruction {

            @Test
            public void thenPreservesOneCourse() {
                SapiVenue sapiVenue = sapiVenueWithAnyIdAndCourses(sapiCoursesWithId(ID.toString()));

                val venueCi = new VenueCi(sapiVenue, Languages.any());

                assertThat(venueCi).containsOnlyCoursesWithIds(ID);
            }

            @Test
            public void thenPreservesOneCourseWithoutId() {
                SapiVenue sapiVenue = sapiVenueWithAnyIdAndCourses(new SapiCourse());

                val venueCi = new VenueCi(sapiVenue, Languages.any());

                assertThat(venueCi).containsOnlyCoursesWithIds(null);
            }

            @Test
            public void thenPreservesOneCourseAfterImportingExportedVenueCi() throws Exception {
                SapiVenue sapiVenue = sapiVenueWithAnyIdAndCourses(sapiCoursesWithId(ID.toString()));
                val venueCi = new VenueCi(sapiVenue, Languages.any());

                VenueCi importedVenueCi = VenueCis.exportSerializeAndUseConstructorToReimport(venueCi);

                assertThat(importedVenueCi).containsOnlyCoursesWithIds(ID);
            }

            @Test
            public void thenPreservesOneCourseWithoutIdAfterImportingExportedVenueCi() throws Exception {
                SapiVenue sapiVenue = sapiVenueWithAnyIdAndCourses(new SapiCourse());
                val venueCi = new VenueCi(sapiVenue, Languages.any());

                VenueCi importedVenueCi = VenueCis.exportSerializeAndUseConstructorToReimport(venueCi);

                assertThat(importedVenueCi).containsOnlyCoursesWithIds(null);
            }
        }

        public static class WhenMultipleCoursesAreProvidedOnConstruction {

            @Test
            public void thenPreservesThem() {
                SapiVenue sapiVenue = sapiVenueWithAnyIdAndCourses(
                    sapiCoursesWithId(ID.toString()),
                    sapiCoursesWithId(ANOTHER_ID.toString())
                );

                val venueCi = new VenueCi(sapiVenue, Languages.any());

                assertThat(venueCi).containsOnlyCoursesWithIds(ID, ANOTHER_ID);
            }

            @Test
            public void thenPreservesThemWhenOneOfThemIsWithoutId() {
                SapiVenue sapiVenue = sapiVenueWithAnyIdAndCourses(
                    sapiCoursesWithId(ID.toString()),
                    new SapiCourse()
                );

                val venueCi = new VenueCi(sapiVenue, Languages.any());

                assertThat(venueCi).containsOnlyCoursesWithIds(ID, null);
            }

            @Test
            public void thenPreservesMultipleCoursesWithoutIds() {
                SapiVenue sapiVenue = sapiVenueWithAnyIdAndCourses(new SapiCourse(), new SapiCourse());

                val venueCi = new VenueCi(sapiVenue, Languages.any());

                assertThat(venueCi).containsOnlyCoursesWithIds(null, null);
            }

            @Test
            public void thenPreservesThemAfterImportingExportedVenueCi() throws Exception {
                SapiVenue sapiVenue = sapiVenueWithAnyIdAndCourses(
                    sapiCoursesWithId(ID.toString()),
                    sapiCoursesWithId(ANOTHER_ID.toString())
                );
                val venueCi = new VenueCi(sapiVenue, Languages.any());

                VenueCi importedVenueCi = VenueCis.exportSerializeAndUseConstructorToReimport(venueCi);

                assertThat(importedVenueCi).containsOnlyCoursesWithIds(ID, ANOTHER_ID);
            }

            @Test
            public void thenPreservesMultipleCoursesWithoutIdAfterImportingExportedVenueCi()
                throws Exception {
                SapiVenue sapiVenue = sapiVenueWithAnyIdAndCourses(new SapiCourse(), new SapiCourse());
                val venueCi = new VenueCi(sapiVenue, Languages.any());

                VenueCi importedVenueCi = VenueCis.exportSerializeAndUseConstructorToReimport(venueCi);

                assertThat(importedVenueCi).containsOnlyCoursesWithIds(null, null);
            }
        }
    }

    @RunWith(Enclosed.class)
    public static class GivenMergeWillOccur {

        public static final Locale LANGUAGE = Languages.any();

        public static class AndNoCoursesWereProvidedPreviously {

            @Test
            public void whenMergingWith0NewCoursesThenPreserves0Courses() {
                val venueCi = new VenueCi(sapiVenueWithAnyId(), LANGUAGE);

                venueCi.merge(sapiVenueWithAnyId(), LANGUAGE);

                assertThat(venueCi.getCourses()).isEmpty();
            }

            @Test
            public void whenMergingWithSingleNewCourseThenPreservesThisNewCourse() {
                val venueCi = new VenueCi(sapiVenueWithAnyId(), LANGUAGE);
                SapiVenue sapiVenue = sapiVenueWithAnyIdAndCourses(sapiCoursesWithId(ID.toString()));

                venueCi.merge(sapiVenue, LANGUAGE);

                assertThat(venueCi).containsOnlyCoursesWithIds(ID);
            }

            @Test
            public void whenMergingWithSingleNewCourseWithoutIdThenPreservesThisNewCourse() {
                val venueCi = new VenueCi(sapiVenueWithAnyId(), LANGUAGE);
                SapiVenue sapiVenue = sapiVenueWithAnyIdAndCourses(new SapiCourse());

                venueCi.merge(sapiVenue, LANGUAGE);

                assertThat(venueCi).containsOnlyCoursesWithIds(null);
            }

            @Test
            public void whenMergingWithMultipleNewCoursesThenPreservesTheseNewCourses() {
                val venueCi = new VenueCi(sapiVenueWithAnyId(), LANGUAGE);
                SapiVenue sapiVenue = sapiVenueWithAnyIdAndCourses(
                    sapiCoursesWithId(ID.toString()),
                    sapiCoursesWithId(ANOTHER_ID.toString())
                );

                venueCi.merge(sapiVenue, LANGUAGE);

                assertThat(venueCi).containsOnlyCoursesWithIds(ID, ANOTHER_ID);
            }

            @Test
            public void whenMergingWithMultipleNewCoursesOneOfWhichHasNoIdThenPreservesAllTheseNewCourses() {
                val venueCi = new VenueCi(sapiVenueWithAnyId(), LANGUAGE);
                SapiVenue sapiVenue = sapiVenueWithAnyIdAndCourses(
                    sapiCoursesWithId(ID.toString()),
                    new SapiCourse()
                );

                venueCi.merge(sapiVenue, LANGUAGE);

                assertThat(venueCi).containsOnlyCoursesWithIds(ID, null);
            }

            @Test
            public void whenMergingWithMultipleNewCoursesWithoutIdsThenPreservesTheseNewCourses() {
                val venueCi = new VenueCi(sapiVenueWithAnyId(), LANGUAGE);
                SapiVenue sapiVenue = sapiVenueWithAnyIdAndCourses(new SapiCourse(), new SapiCourse());

                venueCi.merge(sapiVenue, LANGUAGE);

                assertThat(venueCi).containsOnlyCoursesWithIds(null, null);
            }
        }

        public static class AndSomeCoursesWereProvidedPreviously {

            @Test
            public void whenMergingWith0NewCoursesThenDoesNotReplaceOldOnesIndicatingThatSourceNotKnowsTheState() {
                SapiVenue sapiVenue = sapiVenueWithAnyIdAndCourses(sapiCoursesWithId(ID.toString()));
                val venueCi = new VenueCi(sapiVenue, LANGUAGE);

                venueCi.merge(sapiVenueWithAnyId(), LANGUAGE);

                assertThat(venueCi).containsOnlyCoursesWithIds(ID);
            }

            @Test
            public void whenMergingSingleCourseContainingSameIdAsInflightRequestThenPreservesThisCourses() {
                SapiVenue oldCourses = sapiVenueWithAnyIdAndCourses(sapiCoursesWithId(ID.toString()));
                val venueCi = new VenueCi(oldCourses, LANGUAGE);
                SapiVenue newCourses = sapiVenueWithAnyIdAndCourses(sapiCoursesWithId(ID.toString()));

                venueCi.merge(newCourses, LANGUAGE);

                assertThat(venueCi).containsOnlyCoursesWithIds(ID);
            }

            @Test
            public void whenMergingMultipleCoursesContainingSameIdsAsInflightRequestThenPreservesTheseCourses() {
                SapiVenue oldCourses = sapiVenueWithAnyIdAndCourses(
                    sapiCoursesWithId(ID.toString()),
                    sapiCoursesWithId(ANOTHER_ID.toString())
                );
                val venueCi = new VenueCi(oldCourses, LANGUAGE);
                SapiVenue newCourses = sapiVenueWithAnyIdAndCourses(
                    sapiCoursesWithId(ID.toString()),
                    sapiCoursesWithId(ANOTHER_ID.toString())
                );

                venueCi.merge(newCourses, LANGUAGE);

                assertThat(venueCi).containsOnlyCoursesWithIds(ID, ANOTHER_ID);
            }

            @Test
            public void whenInflightRequestContainsCoursesWithCurrentlyNotExistingIdsThanTheyArePreserved() {
                SapiVenue oldCourses = sapiVenueWithAnyIdAndCourses(sapiCoursesWithId(ID.toString()));
                val venueCi = new VenueCi(oldCourses, LANGUAGE);
                SapiVenue newCourses = sapiVenueWithAnyIdAndCourses(
                    sapiCoursesWithId(ID.toString()),
                    sapiCoursesWithId(ANOTHER_ID.toString())
                );

                venueCi.merge(newCourses, LANGUAGE);

                assertThat(venueCi).containsOnlyCoursesWithIds(ID, ANOTHER_ID);
            }

            @Test
            public void whenInflightRequestNotContainsCoursesWithCurrentlyExistingIdsThanTheyAreRemoved() {
                SapiVenue oldCourses = sapiVenueWithAnyIdAndCourses(
                    sapiCoursesWithId(ID.toString()),
                    sapiCoursesWithId(ANOTHER_ID.toString())
                );
                val venueCi = new VenueCi(oldCourses, LANGUAGE);
                SapiVenue newCourses = sapiVenueWithAnyIdAndCourses(sapiCoursesWithId(ID.toString()));

                venueCi.merge(newCourses, LANGUAGE);

                assertThat(venueCi).containsOnlyCoursesWithIds(ID);
            }

            @Test
            public void whenInflightRequestContainsCoursesWithoutIdsAndCiDoesNotHaveSuchOnesThenCiPreservesThem() {
                SapiVenue oldCourses = sapiVenueWithAnyIdAndCourses(sapiCoursesWithId(ID.toString()));
                val venueCi = new VenueCi(oldCourses, LANGUAGE);
                SapiVenue newCourses = sapiVenueWithAnyIdAndCourses(
                    sapiCoursesWithId(ID.toString()),
                    new SapiCourse()
                );

                venueCi.merge(newCourses, LANGUAGE);

                assertThat(venueCi).containsOnlyCoursesWithIds(ID, null);
            }

            @Test
            public void whenInflightRequestContainsCoursesWithIdsAndCiHasSomeWithoutThenCiDropsCoursesWithoutIds() {
                SapiVenue oldCourses = sapiVenueWithAnyIdAndCourses(
                    sapiCoursesWithId(ID.toString()),
                    new SapiCourse()
                );
                val venueCi = new VenueCi(oldCourses, LANGUAGE);
                SapiVenue newCourses = sapiVenueWithAnyIdAndCourses(sapiCoursesWithId(ID.toString()));

                venueCi.merge(newCourses, LANGUAGE);

                assertThat(venueCi).containsOnlyCoursesWithIds(ID);
            }

            @Test
            public void whenInflightRequestContainsOneCoursWithoutIdAndCiHasOneWithoutIdThanTheyAreNotDuplicated() {
                SapiVenue oldCourses = sapiVenueWithAnyIdAndCourses(new SapiCourse());
                val venueCi = new VenueCi(oldCourses, LANGUAGE);
                SapiVenue newCourses = sapiVenueWithAnyIdAndCourses(new SapiCourse());

                venueCi.merge(newCourses, LANGUAGE);

                assertThat(venueCi).containsOnlyCoursesWithIds(null);
            }

            @Test
            public void whenInflightRequestContainsMoreCourseWithoutIdThanCiCurrentlyHasThenInflightArePreserved() {
                SapiVenue oldCourses = sapiVenueWithAnyIdAndCourses(new SapiCourse());
                val venueCi = new VenueCi(oldCourses, LANGUAGE);
                SapiVenue newCourses = sapiVenueWithAnyIdAndCourses(new SapiCourse(), new SapiCourse());

                venueCi.merge(newCourses, LANGUAGE);

                assertThat(venueCi).containsOnlyCoursesWithIds(null, null);
            }

            @Test
            public void whenInflightRequestContainsLessCourseWithoutIdThanCiCurrentlyHasThenCiDropsRedundantOnes() {
                SapiVenue oldCourses = sapiVenueWithAnyIdAndCourses(new SapiCourse(), new SapiCourse());
                val venueCi = new VenueCi(oldCourses, LANGUAGE);
                SapiVenue newCourses = sapiVenueWithAnyIdAndCourses(new SapiCourse());

                venueCi.merge(newCourses, LANGUAGE);

                assertThat(venueCi).containsOnlyCoursesWithIds(null);
            }
        }
    }
}
