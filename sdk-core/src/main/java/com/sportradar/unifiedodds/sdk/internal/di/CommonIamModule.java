/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */

package com.sportradar.unifiedodds.sdk.internal.di;

import com.google.inject.AbstractModule;
import com.google.inject.Singleton;
import com.google.inject.name.Names;
import com.sportradar.unifiedodds.sdk.internal.commoniam.CommonIamTokenCache;
import com.sportradar.unifiedodds.sdk.internal.commoniam.OAuth2TokenCache;
import com.sportradar.unifiedodds.sdk.internal.commoniam.ResourceAudience;

public class CommonIamModule extends AbstractModule {

    @Override
    protected void configure() {
        bind(ResourceAudience.class)
            .annotatedWith(Names.named("UfRestApiAudience"))
            .toInstance(ResourceAudience.UF_REST_API);
        bind(OAuth2TokenCache.class).to(CommonIamTokenCache.class).in(Singleton.class);
    }
}
