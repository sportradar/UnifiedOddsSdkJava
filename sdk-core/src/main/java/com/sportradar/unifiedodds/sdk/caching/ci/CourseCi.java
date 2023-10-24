/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */
package com.sportradar.unifiedodds.sdk.caching.ci;

import static com.sportradar.utils.stream.optional.NonNullMapper.ifNotNull;
import static java.util.stream.Collectors.toList;

import com.sportradar.uf.sportsapi.datamodel.SapiCourse;
import com.sportradar.unifiedodds.sdk.caching.exportable.ExportableCourseCi;
import com.sportradar.unifiedodds.sdk.domain.language.Translations;
import com.sportradar.utils.Urn;
import java.util.List;
import java.util.Locale;

public class CourseCi {

    private final List<HoleCi> holes;

    private final Urn id;

    private final Translations name;

    public CourseCi(SapiCourse course, Locale language) {
        this.holes = course.getHole().stream().map(HoleCi::new).collect(toList());
        id = ifNotNull(course.getId()).map(Urn::parse);
        name = new Translations(language, course.getName());
    }

    public CourseCi(ExportableCourseCi course) {
        this.holes = course.getHoles().stream().map(HoleCi::new).collect(toList());
        id = course.getId();
        name = Translations.importFrom(course.getName());
    }

    public List<HoleCi> getHoles() {
        return holes;
    }

    public ExportableCourseCi export() {
        return new ExportableCourseCi(
            id,
            name.export(),
            holes.stream().map(HoleCi::export).collect(toList())
        );
    }

    public Urn getId() {
        return id;
    }

    public Translations getName() {
        return name;
    }

    public void mergeWithoutOverriding(CourseCi courseCi) {
        name.addAllWithoutOverriding(courseCi.getName());
    }
}
