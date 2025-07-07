/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */
package com.sportradar.unifiedodds.sdk.caching.impl.ci;

import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.sportradar.unifiedodds.sdk.internal.caching.CompetitorCi;
import com.sportradar.utils.Urn;
import com.sportradar.utils.domain.names.LanguageHolder;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import lombok.val;

public class CompetitorCis {

    public static CompetitorCiBuilder competitorCi() {
        return new CompetitorCiBuilder();
    }

    public static class CompetitorCiBuilder {

        private final Map<Locale, String> names = new HashMap<>();
        private Urn id;

        public CompetitorCiBuilder withName(LanguageHolder language, String name) {
            this.names.put(language.get(), name);
            return this;
        }

        @SuppressWarnings("HiddenField")
        public CompetitorCiBuilder withId(Urn id) {
            this.id = id;
            return this;
        }

        public CompetitorCi build() {
            val ci = mock(CompetitorCi.class);
            when(ci.getId()).thenReturn(Optional.ofNullable(id).orElse(Urn.parse("sr:competitor:12345")));
            when(ci.getNames(anyList())).thenReturn(names);
            return ci;
        }
    }
}
