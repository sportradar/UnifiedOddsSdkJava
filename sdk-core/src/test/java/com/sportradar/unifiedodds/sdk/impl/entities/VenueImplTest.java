/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */
package com.sportradar.unifiedodds.sdk.impl.entities;

import static java.util.Arrays.asList;
import static org.assertj.core.api.Assertions.assertThat;

import com.sportradar.uf.sportsapi.datamodel.SapiCourse;
import com.sportradar.uf.sportsapi.datamodel.SapiHole;
import com.sportradar.uf.sportsapi.datamodel.SapiVenue;
import com.sportradar.unifiedodds.sdk.caching.ci.VenueCi;
import com.sportradar.utils.Urn;
import com.sportradar.utils.Urns;
import com.sportradar.utils.domain.names.Languages;
import org.junit.jupiter.api.Test;

public class VenueImplTest {

    @Test
    public void preservesNoCourses() {
        SapiVenue dtoVenue = dtoVenueWithAnyId();

        VenueImpl userExposedEntity = new VenueImpl(
            new VenueCi(dtoVenue, Languages.any()),
            asList(Languages.any())
        );

        assertThat(userExposedEntity.getCourses()).isEmpty();
    }

    @Test
    public void preservesOneCourse() {
        final int par = 6;
        SapiVenue dtoVenue = dtoVenueWithAnyId();
        dtoVenue.getCourse().add(dtoCourseWithOneHoleWithPar(par));

        VenueImpl userExposedEntity = new VenueImpl(
            new VenueCi(dtoVenue, Languages.any()),
            asList(Languages.any())
        );

        assertThat(userExposedEntity.getCourses().get(0).getHoles().get(0).getPar()).isEqualTo(par);
    }

    @Test
    public void preservesMultipleCourses() {
        final int parHole1 = 6;
        final int parHole2 = 4;
        SapiVenue dtoVenue = dtoVenueWithAnyId();
        dtoVenue
            .getCourse()
            .addAll(asList(dtoCourseWithOneHoleWithPar(parHole1), dtoCourseWithOneHoleWithPar(parHole2)));

        VenueImpl userExposedEntity = new VenueImpl(
            new VenueCi(dtoVenue, Languages.any()),
            asList(Languages.any())
        );

        assertThat(userExposedEntity.getCourses().get(0).getHoles().get(0).getPar()).isEqualTo(parHole1);
        assertThat(userExposedEntity.getCourses().get(1).getHoles().get(0).getPar()).isEqualTo(parHole2);
    }

    @Test
    public void preservesCourseId() {
        final Urn id = Urns.Venues.urnForAnyVenue();
        SapiVenue dtoVenue = dtoVenueWithAnyId();
        dtoVenue.getCourse().add(dtoCourseWithId(id.toString()));

        VenueImpl userExposedEntity = new VenueImpl(
            new VenueCi(dtoVenue, Languages.any()),
            asList(Languages.any())
        );

        assertThat(userExposedEntity.getCourses().get(0).getId()).isEqualTo(id);
    }

    private static SapiVenue dtoVenueWithAnyId() {
        SapiVenue dtoVenue = new SapiVenue();
        dtoVenue.setId(Urns.Venues.urnForAnyVenue().toString());
        return dtoVenue;
    }

    private static SapiCourse dtoCourseWithOneHoleWithPar(int par) {
        SapiCourse course = new SapiCourse();
        course.getHole().add(sapiHoleWithPar(par));
        return course;
    }

    private static SapiHole sapiHoleWithPar(int par) {
        SapiHole sapiHole = new SapiHole();
        sapiHole.setPar(par);
        return sapiHole;
    }

    private static SapiCourse dtoCourseWithId(String id) {
        SapiCourse course = new SapiCourse();
        course.setId(id);
        return course;
    }
}
