/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */
package com.sportradar.unifiedodds.sdk.caching.ci.matchers;

import static java.util.Collections.singletonList;
import static java.util.Optional.ofNullable;

import com.sportradar.unifiedodds.sdk.internal.caching.ci.CourseCi;
import com.sportradar.unifiedodds.sdk.internal.caching.ci.VenueCi;
import com.sportradar.utils.Urn;
import com.sportradar.utils.domain.names.LanguageHolder;
import com.sportradar.utils.domain.names.TranslationHolder;
import java.util.Arrays;
import lombok.val;
import org.assertj.core.api.AbstractAssert;
import org.assertj.core.api.Assertions;

public class VenueCiAssert extends AbstractAssert<VenueCiAssert, VenueCi> {

    public VenueCiAssert(VenueCi venueCi) {
        super(venueCi, VenueCiAssert.class);
    }

    public static VenueCiAssert assertThat(VenueCi venueCi) {
        return new VenueCiAssert(venueCi);
    }

    public VenueCiAssert containsOnlyCoursesWithIds(Urn... ids) {
        val idList = ofNullable(ids).map(Arrays::asList).orElse(singletonList(null));
        for (int i = 0; i < idList.size(); i++) {
            Assertions.assertThat(actual.getCourses().get(i).getId()).isEqualTo(idList.get(i));
        }
        Assertions.assertThat(actual.getCourses()).hasSize(idList.size());
        return this;
    }

    public VenueCiAssert containsCourse(IndexHolder index, TranslationHolder translation) {
        final CourseCi firstCourse = actual.getCourses().get(index.get());
        TranslationsAssert.assertThat(firstCourse.getName()).hasTranslation(translation);
        return this;
    }

    public VenueCiAssert containsCourse(IndexHolder index, LanguageHolder noTranslationTo) {
        final CourseCi firstCourse = actual.getCourses().get(index.get());
        TranslationsAssert.assertThat(firstCourse.getName()).isNotTranslatedTo(noTranslationTo);
        return this;
    }

    public VenueCiAssert containsAmountOfCourses(int amount) {
        Assertions.assertThat(actual.getCourses()).hasSize(amount);
        return this;
    }
}
