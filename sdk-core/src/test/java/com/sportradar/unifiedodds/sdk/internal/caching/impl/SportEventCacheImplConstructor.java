/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */
package com.sportradar.unifiedodds.sdk.internal.caching.impl;

import com.google.common.cache.Cache;
import com.sportradar.unifiedodds.sdk.internal.caching.DataRouterManager;
import com.sportradar.unifiedodds.sdk.internal.caching.SportEventCi;
import com.sportradar.unifiedodds.sdk.internal.caching.impl.ci.CacheItemFactory;
import com.sportradar.unifiedodds.sdk.internal.impl.MappingTypeProvider;
import com.sportradar.unifiedodds.sdk.internal.impl.SdkInternalConfiguration;
import com.sportradar.utils.Urn;

public class SportEventCacheImplConstructor {

    private SportEventCacheImplConstructor() {}

    public static SportEventCacheImpl create(
        CacheItemFactory cacheItemFactory,
        MappingTypeProvider mappingTypeProvider,
        DataRouterManager dataRouterManager,
        SdkInternalConfiguration sdkInternalConfiguration,
        Cache<Urn, SportEventCi> sportEventsCache
    ) {
        return new SportEventCacheImpl(
            cacheItemFactory,
            mappingTypeProvider,
            dataRouterManager,
            sdkInternalConfiguration,
            sportEventsCache
        );
    }
}
