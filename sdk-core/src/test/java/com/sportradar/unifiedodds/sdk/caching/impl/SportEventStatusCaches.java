/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */
package com.sportradar.unifiedodds.sdk.caching.impl;

import static java.util.Optional.empty;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.google.common.cache.CacheBuilder;
import com.sportradar.unifiedodds.sdk.caching.MatchStatusValues;
import com.sportradar.unifiedodds.sdk.caching.NamedValuesProvider;
import com.sportradar.unifiedodds.sdk.caching.SportEventCache;
import com.sportradar.utils.domain.names.LanguageHolder;
import java.util.Optional;

public class SportEventStatusCaches {

    public static class BuilderStubbingOutSportEventCache {

        private Optional<SportEventCache> sportEventCache = empty();

        public static BuilderStubbingOutSportEventCache stubbingOutSportEventCache() {
            return new BuilderStubbingOutSportEventCache();
        }

        @SuppressWarnings("HiddenField")
        public BuilderStubbingOutSportEventCache with(SportEventCache sportEventCache) {
            this.sportEventCache = Optional.of(sportEventCache);
            return this;
        }

        public SportEventStatusCacheImpl build() {
            return new SportEventStatusCacheImpl(
                CacheBuilder.newBuilder().build(),
                sportEventCache.orElse(mock(SportEventCache.class)),
                CacheBuilder.newBuilder().build()
            );
        }
    }
}
