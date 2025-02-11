/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */
package com.sportradar.unifiedodds.sdk.impl.entities;

import static org.mockito.Mockito.mock;

import com.google.common.cache.Cache;
import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Provides;
import com.sportradar.unifiedodds.sdk.internal.caching.DataRouterManager;
import com.sportradar.unifiedodds.sdk.internal.caching.SportEventCi;
import com.sportradar.unifiedodds.sdk.internal.caching.impl.SportEventCacheImpl;
import com.sportradar.unifiedodds.sdk.internal.caching.impl.ci.CacheItemFactory;
import com.sportradar.unifiedodds.sdk.internal.impl.MappingTypeProvider;
import com.sportradar.unifiedodds.sdk.internal.impl.SdkInternalConfiguration;
import com.sportradar.utils.Urn;

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
        public SdkInternalConfiguration mockSdkConfig() {
            return mock(SdkInternalConfiguration.class);
        }

        @Provides
        public Cache<Urn, SportEventCi> mockGuavaCache() {
            return mock(Cache.class);
        }
    }
}
