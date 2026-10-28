/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */

package com.sportradar.unifiedodds.sdk.internal.di;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.google.inject.Singleton;
import com.google.inject.name.Named;
import com.google.inject.name.Names;
import com.sportradar.unifiedodds.sdk.cfg.UofConfiguration;
import com.sportradar.unifiedodds.sdk.internal.commoniam.CommonIamTokenCache;
import com.sportradar.unifiedodds.sdk.internal.commoniam.OAuth2TokenCache;
import com.sportradar.unifiedodds.sdk.internal.commoniam.ResourceAudience;
import com.sportradar.unifiedodds.sdk.internal.impl.TimeUtils;
import org.apache.hc.client5.http.impl.async.CloseableHttpAsyncClient;

public class CommonIamModule extends AbstractModule {

    @Provides
    @Singleton
    @Named("OAuth2TokenCacheForApiCalls")
    OAuth2TokenCache provideCommonIamCacheForApiCalls(
        UofConfiguration configuration,
        @Named("FastHttpClient") CloseableHttpAsyncClient httpClient,
        TimeUtils timeUtils,
        ObjectMapper objectMapper
    ) {
        return new CommonIamTokenCache(
            configuration,
            httpClient,
            timeUtils,
            objectMapper,
            ResourceAudience.UF_REST_API
        );
    }

    @Provides
    @Named("OAuth2TokenCacheForRabbitMq")
    OAuth2TokenCache provideCommonIamCacheForRabbitMq(
        UofConfiguration configuration,
        @Named("FastHttpClient") CloseableHttpAsyncClient httpClient,
        TimeUtils timeUtils,
        ObjectMapper objectMapper
    ) {
        return new CommonIamTokenCache(
            configuration,
            httpClient,
            timeUtils,
            objectMapper,
            ResourceAudience.UF_RABBIT_MQ
        );
    }
}
