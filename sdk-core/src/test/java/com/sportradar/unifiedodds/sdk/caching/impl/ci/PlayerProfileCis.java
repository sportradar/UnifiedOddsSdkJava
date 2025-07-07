/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */
package com.sportradar.unifiedodds.sdk.caching.impl.ci;

import static java.util.Optional.ofNullable;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.sportradar.unifiedodds.sdk.internal.caching.PlayerProfileCi;
import com.sportradar.utils.Urn;
import com.sportradar.utils.domain.names.LanguageHolder;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import lombok.val;

public class PlayerProfileCis {

    public static PlayerProfileCiBuilder playerProfileCi() {
        return new PlayerProfileCiBuilder();
    }

    public static class PlayerProfileCiBuilder {

        private final Map<Locale, String> names = new HashMap<>();
        private Urn id;

        public PlayerProfileCiBuilder withName(LanguageHolder language, String name) {
            this.names.put(language.get(), name);
            return this;
        }

        @SuppressWarnings("HiddenField")
        public PlayerProfileCiBuilder withId(Urn id) {
            this.id = id;
            return this;
        }

        public PlayerProfileCi build() {
            val ci = mock(PlayerProfileCi.class);
            when(ci.getId()).thenReturn(ofNullable(id).orElse(Urn.parse("sr:player:9988")));
            when(ci.getNames(anyList())).thenReturn(names);
            return ci;
        }
    }
}
