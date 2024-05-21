/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */
package com.sportradar.unifiedodds.sdk.domain.language;

import com.google.common.collect.ImmutableList;
import java.util.*;

public final class Languages {

    public static final class BestEffort {

        private final List<Locale> languages;

        public BestEffort(Locale language, Locale... otherLanguages) {
            this.languages = ImmutableList.<Locale>builder().add(language).add(otherLanguages).build();
        }

        public BestEffort(List<Locale> languages) {
            this.languages = ImmutableList.copyOf(languages);
        }

        public List<Locale> getLanguages() {
            return languages;
        }
    }
}
