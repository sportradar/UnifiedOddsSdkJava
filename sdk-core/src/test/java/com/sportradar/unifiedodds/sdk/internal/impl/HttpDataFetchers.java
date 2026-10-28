/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */
package com.sportradar.unifiedodds.sdk.internal.impl;

import static com.sportradar.unifiedodds.sdk.internal.commoniam.OAuth2TokenCacheFixtures.failingWithOAuth2TokenRetrievalException;
import static java.util.Optional.ofNullable;
import static org.mockito.Mockito.mock;

import com.sportradar.unifiedodds.sdk.cfg.UofConfiguration;
import com.sportradar.unifiedodds.sdk.cfg.UofConfigurationStub;
import com.sportradar.unifiedodds.sdk.internal.commoniam.OAuth2TokenCache;
import com.sportradar.unifiedodds.sdk.internal.di.HttpClientFactory;
import com.sportradar.unifiedodds.sdk.internal.impl.apireaders.HttpHelper;
import com.sportradar.unifiedodds.sdk.internal.impl.apireaders.MessageAndActionExtractor;
import java.time.Instant;
import java.util.concurrent.TimeUnit;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;

@SuppressWarnings({ "MultipleStringLiterals" })
public class HttpDataFetchers {

    public static HttpHelperBuilder createHttpHelperBuilder() {
        return new HttpHelperBuilder();
    }

    public static final class HttpHelperBuilder {

        private SdkInternalConfiguration deprecatedConfiguration;
        private UserAgentProvider userAgentProvider;
        private UofConfiguration config;
        private OAuth2TokenCache tokenCache;

        public HttpHelperBuilder with(SdkInternalConfiguration cfg) {
            this.deprecatedConfiguration = cfg;
            return this;
        }

        @SuppressWarnings("HiddenField")
        public HttpHelperBuilder with(UofConfigurationStub config) {
            this.config = config;
            return this;
        }

        public HttpHelperBuilder with(UserAgentProvider provider) {
            this.userAgentProvider = provider;
            return this;
        }

        @SuppressWarnings("HiddenField")
        public HttpHelperBuilder with(OAuth2TokenCache tokenCache) {
            this.tokenCache = tokenCache;
            return this;
        }

        public HttpHelper build() {
            return new HttpHelper(
                ofNullable(deprecatedConfiguration).orElse(mock(SdkInternalConfiguration.class)),
                ofNullable(config).orElse(mock(UofConfiguration.class)),
                ofNullable(tokenCache).orElse(failingWithOAuth2TokenRetrievalException()),
                createHttpClientFor(deprecatedConfiguration),
                mock(MessageAndActionExtractor.class),
                ofNullable(userAgentProvider).orElse(new UserAgentProvider("1.0.0", Instant.now())),
                new TraceIdProvider()
            );
        }

        private static CloseableHttpClient createHttpClientFor(SdkInternalConfiguration cfg) {
            int maxTimeoutInMillis = Math.toIntExact(
                TimeUnit.MILLISECONDS.convert(cfg.getHttpClientTimeout(), TimeUnit.SECONDS)
            );
            return new HttpClientFactory()
                .create(
                    maxTimeoutInMillis,
                    cfg.getHttpClientMaxConnTotal(),
                    cfg.getHttpClientMaxConnPerRoute()
                );
        }
    }
}
