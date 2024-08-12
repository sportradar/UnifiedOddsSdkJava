/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */
package com.sportradar.unifiedodds.sdk.caching.exportable;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import lombok.val;
import org.junit.jupiter.api.Test;

public class ExportableVenueCiTest {

    public static final String ANY_ID = "anyId";
    public static final Map<Locale, String> ANY_NAMES = new HashMap<>();
    public static final int ANY_CAPACITY = 3;
    public static final String ANY_COUNTRY = "es";
    public static final String ANY_COORDINATES = "5;6";
    public static final ArrayList<Locale> ANY_LANGUAGES = new ArrayList<>();
    public static final String ANY_STATE = "anyState";

    @Test
    public void nullCoursesArePreservedAsEmptyList() {
        val venue = new ExportableVenueCi(
            ANY_ID,
            ANY_NAMES,
            ANY_NAMES,
            ANY_NAMES,
            ANY_CAPACITY,
            ANY_COUNTRY,
            ANY_COORDINATES,
            ANY_LANGUAGES,
            ANY_STATE,
            null
        );

        assertThat(venue.getCourses()).isEmpty();
    }
}
