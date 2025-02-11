/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */
package com.sportradar.unifiedodds.sdk.caching.impl;

import static java.util.Optional.empty;
import static org.mockito.Mockito.mock;

import com.google.common.cache.CacheBuilder;
import com.sportradar.unifiedodds.sdk.internal.caching.SportEventCache;
import com.sportradar.unifiedodds.sdk.internal.caching.impl.SportEventStatusCacheImpl;
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
