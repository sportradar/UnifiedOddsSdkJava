/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */
package com.sportradar.unifiedodds.sdk.internal.impl.entities;

import static java.util.stream.Collectors.toList;

import com.sportradar.unifiedodds.sdk.entities.Course;
import com.sportradar.unifiedodds.sdk.entities.Hole;
import com.sportradar.unifiedodds.sdk.internal.caching.ci.CourseCi;
import com.sportradar.utils.Urn;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class CourseImpl implements Course {

    private final List<Hole> holes;
    private final Urn id;
    private Map<Locale, String> nameTranslations;

    CourseImpl(CourseCi courseCi) {
        holes = courseCi.getHoles().stream().map(HoleImpl::new).collect(toList());
        id = courseCi.getId();
        nameTranslations = courseCi.getName().export();
    }

    @Override
    public List<Hole> getHoles() {
        return holes;
    }

    @Override
    public Urn getId() {
        return id;
    }

    @Override
    public String getName(Locale language) {
        return nameTranslations.get(language);
    }

    @Override
    public Map<Locale, String> getNames() {
        return nameTranslations;
    }
}
