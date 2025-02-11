/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */
package com.sportradar.unifiedodds.sdk.caching.impl.ci;

import com.google.common.cache.Cache;
import com.sportradar.unifiedodds.sdk.internal.caching.DataRouterManager;
import com.sportradar.unifiedodds.sdk.internal.caching.impl.ci.CacheItemFactory;
import com.sportradar.unifiedodds.sdk.internal.caching.impl.ci.CacheItemFactoryImpl;
import com.sportradar.unifiedodds.sdk.internal.impl.SdkInternalConfiguration;
import com.sportradar.utils.Urn;
import java.util.Date;

public class CacheItemFactoryImplConstructor {

    private CacheItemFactoryImplConstructor() {}

    public static CacheItemFactory create(
        DataRouterManager dataRouterManager,
        SdkInternalConfiguration sdkInternalConfiguration,
        Cache<Urn, Date> fixtureTimestampCache
    ) {
        return new CacheItemFactoryImpl(dataRouterManager, sdkInternalConfiguration, fixtureTimestampCache);
    }
}
