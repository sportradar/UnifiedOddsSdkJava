/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */
package com.sportradar.unifiedodds.sdk.impl.entities;

import static org.mockito.Mockito.mock;

import com.google.common.cache.Cache;
import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Provides;
import com.sportradar.unifiedodds.sdk.SDKInternalConfiguration;
import com.sportradar.unifiedodds.sdk.caching.DataRouterManager;
import com.sportradar.unifiedodds.sdk.caching.SportEventCI;
import com.sportradar.unifiedodds.sdk.caching.impl.SportEventCacheImpl;
import com.sportradar.unifiedodds.sdk.caching.impl.ci.CacheItemFactory;
import com.sportradar.unifiedodds.sdk.impl.MappingTypeProvider;
import com.sportradar.utils.URN;

public class SportEvenCacheToProxyDataRouterManagerOnly {

    private SportEvenCacheToProxyDataRouterManagerOnly() {}

    public static SportEventCacheImpl create(final DataRouterManager dataRouterManager) {
        return Guice
            .createInjector(new SportEvenCacheToProxyDataRouterManagerModule(dataRouterManager))
            .getInstance(SportEventCacheImpl.class);
    }

    private static class SportEvenCacheToProxyDataRouterManagerModule extends AbstractModule {

        private DataRouterManager dataRouterManager;

        public SportEvenCacheToProxyDataRouterManagerModule(final DataRouterManager dataRouterManager) {
            this.dataRouterManager = dataRouterManager;
        }

        @Override
        protected void configure() {}

        @Provides
        public CacheItemFactory mockCacheItemFactory() {
            return mock(CacheItemFactory.class);
        }

        @Provides
        public MappingTypeProvider mockMappingTypeProvider() {
            return mock(MappingTypeProvider.class);
        }

        @Provides
        public DataRouterManager mockDataRouterManager() {
            return dataRouterManager;
        }

        @Provides
        public SDKInternalConfiguration mockSdkConfig() {
            return mock(SDKInternalConfiguration.class);
        }

        @Provides
        public Cache<URN, SportEventCI> mockGuavaCache() {
            return mock(Cache.class);
        }
    }
}
