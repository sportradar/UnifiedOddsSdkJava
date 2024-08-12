/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */
package com.sportradar.unifiedodds.sdk.impl.entities;

import static com.sportradar.utils.Urns.Venues.urnForAnyVenue;
import static java.util.Arrays.asList;
import static org.assertj.core.api.Assertions.assertThat;

import com.sportradar.uf.sportsapi.datamodel.SapiCourse;
import com.sportradar.uf.sportsapi.datamodel.SapiHole;
import com.sportradar.unifiedodds.sdk.caching.ci.CourseCi;
import com.sportradar.utils.Urn;
import com.sportradar.utils.domain.names.Languages;
import java.util.Locale;
import org.junit.jupiter.api.Test;

public class CourseImplTest {

    @Test
    public void preservesNoHoles() {
        SapiCourse sapiCourse = new SapiCourse();

        CourseImpl course = new CourseImpl(new CourseCi(sapiCourse, Languages.any()));

        assertThat(course.getHoles()).isEmpty();
    }

    @Test
    public void preservesOneHole() {
        final int par = 6;
        SapiCourse sapiCourse = new SapiCourse();
        sapiCourse.getHole().add(sapiHoleWithPar(par));

        CourseImpl course = new CourseImpl(new CourseCi(sapiCourse, Languages.any()));

        assertThat(course.getHoles().get(0).getPar()).isEqualTo(par);
    }

    @Test
    public void preservesMultipleHoles() {
        final int parHole1 = 6;
        final int parHole2 = 4;
        SapiCourse sapiCourse = new SapiCourse();
        sapiCourse.getHole().addAll(asList(sapiHoleWithPar(parHole1), sapiHoleWithPar(parHole2)));

        CourseImpl course = new CourseImpl(new CourseCi(sapiCourse, Languages.any()));

        assertThat(course.getHoles().get(0).getPar()).isEqualTo(parHole1);
        assertThat(course.getHoles().get(1).getPar()).isEqualTo(parHole2);
    }

    @Test
    public void preservesId() {
        final Urn id = urnForAnyVenue();
        SapiCourse sapiCourse = new SapiCourse();
        sapiCourse.setId(id.toString());

        CourseImpl course = new CourseImpl(new CourseCi(sapiCourse, Languages.any()));

        assertThat(course.getId()).isEqualTo(id);
    }

    @Test
    public void preservesName() {
        String name = "someName";
        Locale language = Languages.any();
        SapiCourse sapiCourse = new SapiCourse();
        sapiCourse.setName(name);

        CourseImpl course = new CourseImpl(new CourseCi(sapiCourse, language));

        assertThat(course.getName(language)).isEqualTo(name);
        assertThat(course.getNames().get(language)).isEqualTo(name);
    }

    private static SapiHole sapiHoleWithPar(int par) {
        SapiHole sapiHole = new SapiHole();
        sapiHole.setPar(par);
        return sapiHole;
    }
}
