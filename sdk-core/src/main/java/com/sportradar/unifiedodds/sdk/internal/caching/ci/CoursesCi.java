/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */
package com.sportradar.unifiedodds.sdk.internal.caching.ci;

import static java.util.stream.Collectors.toList;

import com.sportradar.uf.sportsapi.datamodel.SapiCourse;
import com.sportradar.unifiedodds.sdk.oddsentities.exportable.ExportableCourseCi;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

public class CoursesCi {

    private List<CourseCi> courses;

    public CoursesCi(List<SapiCourse> sapiCourses, Locale locale) {
        this.courses = parseCourses(sapiCourses, locale);
    }

    private CoursesCi(List<ExportableCourseCi> courses) {
        this.courses = courses.stream().map(CourseCi::new).collect(toList());
    }

    public static CoursesCi importFrom(List<ExportableCourseCi> courses) {
        return new CoursesCi(courses);
    }

    public void merge(List<SapiCourse> sapiCourses, Locale language) {
        mergeCoursesOnlyIfSourceIsAwareOfLatestState(sapiCourses, language);
    }

    public List<CourseCi> get() {
        return courses;
    }

    public List<ExportableCourseCi> export() {
        return courses.stream().map(CourseCi::export).collect(toList());
    }

    private void mergeCoursesOnlyIfSourceIsAwareOfLatestState(List<SapiCourse> sapiCourses, Locale language) {
        if (isSourceAwareOfStateOfCourses(sapiCourses)) {
            this.courses = mergeOldCoursesIntoNewOnes(sapiCourses, language);
        }
    }

    private boolean isSourceAwareOfStateOfCourses(List<SapiCourse> sapiCourses) {
        return !sapiCourses.isEmpty();
    }

    private List<CourseCi> mergeOldCoursesIntoNewOnes(List<SapiCourse> sapiCourses, Locale language) {
        List<CourseCi> inflightCourses = parseCourses(sapiCourses, language);
        mergeCurrentCoursesInto(inflightCourses);
        return inflightCourses;
    }

    private List<CourseCi> parseCourses(List<SapiCourse> sapiCourses, Locale language) {
        return sapiCourses.stream().map(c -> new CourseCi(c, language)).collect(toList());
    }

    private void mergeCurrentCoursesInto(List<CourseCi> inflightCourses) {
        inflightCourses.forEach(inflightCourse ->
            this.courses.stream()
                .filter(current -> Objects.equals(current.getId(), inflightCourse.getId()))
                .findFirst()
                .ifPresent(current -> inflightCourse.mergeWithoutOverriding(current))
        );
    }
}
