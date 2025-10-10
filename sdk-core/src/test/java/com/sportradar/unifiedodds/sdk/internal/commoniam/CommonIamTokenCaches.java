/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */
package com.sportradar.unifiedodds.sdk.internal.commoniam;

import static java.util.Optional.ofNullable;
import static org.mockito.Mockito.mock;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sportradar.unifiedodds.sdk.cfg.UofConfiguration;
import com.sportradar.unifiedodds.sdk.internal.di.HttpClientFactory;
import com.sportradar.unifiedodds.sdk.internal.impl.SdkInternalConfiguration;
import com.sportradar.unifiedodds.sdk.internal.impl.TimeUtils;
import com.sportradar.unifiedodds.sdk.internal.impl.TimeUtilsImpl;
import java.util.concurrent.TimeUnit;
import lombok.val;
import org.apache.hc.client5.http.impl.async.CloseableHttpAsyncClient;

@SuppressWarnings({ "ClassDataAbstractionCoupling", "ClassFanOutComplexity" })
public class CommonIamTokenCaches {

    public static CommonIamTokenCacheBuilder createCommonIamTokenCache() {
        return new CommonIamTokenCacheBuilder();
    }

    public static final class CommonIamTokenCacheBuilder {

        private UofConfiguration configuration;
        private SdkInternalConfiguration deprecatedConfiguration;
        private TimeUtils timeUtils;

        @SuppressWarnings("HiddenField")
        public CommonIamTokenCacheBuilder with(UofConfiguration configuration) {
            this.configuration = configuration;
            return this;
        }

        @SuppressWarnings("HiddenField")
        public CommonIamTokenCacheBuilder with(SdkInternalConfiguration deprecatedConfiguration) {
            this.deprecatedConfiguration = deprecatedConfiguration;
            return this;
        }

        @SuppressWarnings("HiddenField")
        public CommonIamTokenCacheBuilder with(TimeUtils timeUtils) {
            this.timeUtils = timeUtils;
            return this;
        }

        public CommonIamTokenCache build() {
            val config = ofNullable(this.configuration).orElse(mock(UofConfiguration.class));
            val deprecatedConfig = ofNullable(this.deprecatedConfiguration)
                .orElse(mock(SdkInternalConfiguration.class));
            val timeUtilsInstance = ofNullable(this.timeUtils).orElse(mock(TimeUtilsImpl.class));
            val client = createStartedAsyncHttpClientFor(deprecatedConfig);
            val objectMapper = new ObjectMapper()
                .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

            return new CommonIamTokenCache(config, client, timeUtilsInstance, objectMapper);
        }

        public CloseableHttpAsyncClient createStartedAsyncHttpClientFor(SdkInternalConfiguration cfg) {
            int maxTimeoutInMillis = Math.toIntExact(
                TimeUnit.MILLISECONDS.convert(cfg.getHttpClientTimeout(), TimeUnit.SECONDS)
            );
            val client = new HttpClientFactory()
                .createAsync(
                    maxTimeoutInMillis,
                    cfg.getHttpClientMaxConnTotal(),
                    cfg.getHttpClientMaxConnPerRoute()
                );
            client.start();
            return client;
        }
    }
}
