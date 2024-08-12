/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */
package com.sportradar.unifiedodds.sdk.caching.ci;

import static java.util.Arrays.asList;
import static org.assertj.core.api.Assertions.assertThat;

import com.sportradar.uf.sportsapi.datamodel.SapiCourse;
import com.sportradar.uf.sportsapi.datamodel.SapiHole;
import com.sportradar.utils.domain.names.Languages;
import lombok.val;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

public class CourseCiTest {

    private static SapiHole dtoHoleWithPar(int par) {
        SapiHole sapiHole = new SapiHole();
        sapiHole.setPar(par);
        return sapiHole;
    }

    @Nested
    public class WhenConstructedFromDto {

        @Test
        public void preservesNoHoles() {
            SapiCourse courseDto = new SapiCourse();

            CourseCi course = new CourseCi(courseDto, Languages.any());

            assertThat(course.getHoles()).isEmpty();
        }

        @Test
        public void preservesOneHole() {
            final int par = 6;
            SapiCourse dtoCourse = new SapiCourse();
            dtoCourse.getHole().add(dtoHoleWithPar(par));

            val course = new CourseCi(dtoCourse, Languages.any());

            assertThat(course.getHoles().get(0).getPar()).isEqualTo(par);
        }

        @Test
        public void preservesMultipleHoles() {
            final int parHole1 = 6;
            final int parHole2 = 4;
            SapiCourse sapiCourse = new SapiCourse();
            sapiCourse.getHole().addAll(asList(dtoHoleWithPar(parHole1), dtoHoleWithPar(parHole2)));

            val course = new CourseCi(sapiCourse, Languages.any());

            assertThat(course.getHoles().get(0).getPar()).isEqualTo(parHole1);
            assertThat(course.getHoles().get(1).getPar()).isEqualTo(parHole2);
        }
    }

    @Nested
    public class WhenReImportedAfterExporting {

        @Test
        public void preservesNoHoles() throws Exception {
            SapiCourse courseDto = new SapiCourse();
            CourseCi course = new CourseCi(courseDto, Languages.any());

            val reImportedCourse = CourseCis.exportSerializeAndUseConstructorToReimport(course);

            assertThat(reImportedCourse.getHoles()).isEmpty();
        }

        @Test
        public void preservesOneHole() throws Exception {
            final int par = 6;
            SapiCourse dtoCourse = new SapiCourse();
            dtoCourse.getHole().add(dtoHoleWithPar(par));
            val course = new CourseCi(dtoCourse, Languages.any());

            val reImportedCourse = CourseCis.exportSerializeAndUseConstructorToReimport(course);

            assertThat(reImportedCourse.getHoles().get(0).getPar()).isEqualTo(par);
        }

        @Test
        public void preservesMultipleHoles() throws Exception {
            final int parHole1 = 6;
            final int parHole2 = 4;
            SapiCourse sapiCourse = new SapiCourse();
            sapiCourse.getHole().addAll(asList(dtoHoleWithPar(parHole1), dtoHoleWithPar(parHole2)));
            val course = new CourseCi(sapiCourse, Languages.any());

            val reImportedCourse = CourseCis.exportSerializeAndUseConstructorToReimport(course);

            assertThat(reImportedCourse.getHoles().get(0).getPar()).isEqualTo(parHole1);
            assertThat(reImportedCourse.getHoles().get(1).getPar()).isEqualTo(parHole2);
        }
    }
}
