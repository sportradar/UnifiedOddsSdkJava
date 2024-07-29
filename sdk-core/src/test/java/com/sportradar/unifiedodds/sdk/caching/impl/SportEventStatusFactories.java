/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */
package com.sportradar.unifiedodds.sdk.caching.impl;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.google.common.cache.CacheBuilder;
import com.sportradar.unifiedodds.sdk.caching.MatchStatusValues;
import com.sportradar.unifiedodds.sdk.caching.NamedValuesProvider;
import com.sportradar.unifiedodds.sdk.caching.SportEventCache;
import com.sportradar.unifiedodds.sdk.caching.SportEventStatusCache;
import com.sportradar.unifiedodds.sdk.impl.SportEventStatusFactory;
import com.sportradar.unifiedodds.sdk.impl.SportEventStatusFactoryImpl;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;

public class SportEventStatusFactories {

    @AllArgsConstructor(access = AccessLevel.PRIVATE)
    public static class BuilderStubbingOutStatusValueCache {

        private SportEventStatusCache cache;

        public static SportEventStatusFactories.BuilderStubbingOutStatusValueCache stubbingOutStatusValueCacheWith(
            SportEventStatusCache cache
        ) {
            return new SportEventStatusFactories.BuilderStubbingOutStatusValueCache(cache);
        }

        public SportEventStatusFactory build() {
            return new SportEventStatusFactoryImpl(cache, noOpMatchStatusValuesProvider());
        }

        private static NamedValuesProvider noOpMatchStatusValuesProvider() {
            NamedValuesProvider matchStatusValueProvider = mock(NamedValuesProvider.class);
            when(matchStatusValueProvider.getMatchStatuses()).thenReturn(MatchStatusValues.createNoOp());
            return matchStatusValueProvider;
        }
    }
}
