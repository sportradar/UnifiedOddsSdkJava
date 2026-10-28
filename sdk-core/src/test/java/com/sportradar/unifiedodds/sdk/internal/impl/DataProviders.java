/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */
package com.sportradar.unifiedodds.sdk.internal.impl;

import static com.sportradar.unifiedodds.sdk.internal.commoniam.OAuth2TokenCacheFixtures.failingWithOAuth2TokenRetrievalException;
import static java.util.Optional.ofNullable;
import static org.mockito.Mockito.mock;

import com.sportradar.unifiedodds.sdk.cfg.UofConfiguration;
import com.sportradar.unifiedodds.sdk.internal.commoniam.OAuth2TokenCache;
import com.sportradar.unifiedodds.sdk.internal.di.HttpClientFactory;
import java.util.concurrent.TimeUnit;
import lombok.val;
import org.apache.hc.client5.http.impl.async.CloseableHttpAsyncClient;
import org.jetbrains.annotations.NotNull;

@SuppressWarnings({ "ClassDataAbstractionCoupling", "ClassFanOutComplexity" })
public class DataProviders {

    public static DataProvidersBuilder createDataProviderFor(String url) {
        return new DataProvidersBuilder(url);
    }

    public static final class DataProvidersBuilder {

        private final String url;
        private SdkInternalConfiguration deprecatedConfiguration;
        private UofConfiguration config;
        private Deserializer deserializer;
        private OAuth2TokenCache tokenCache;
        private HttpFetcherType httpFetcherType;
        private Integer forcefullyOverriddenTimeoutInSeconds;
        private UserAgentProvider userAgentProvider;

        public DataProvidersBuilder(String apiUrl) {
            this.url = apiUrl;
        }

        @SuppressWarnings("HiddenField")
        public DataProvidersBuilder with(SdkInternalConfiguration config) {
            this.deprecatedConfiguration = config;
            return this;
        }

        @SuppressWarnings("HiddenField")
        public DataProvidersBuilder with(UofConfiguration config) {
            this.config = config;
            return this;
        }

        @SuppressWarnings("HiddenField")
        public DataProvidersBuilder with(Deserializer responseDeserializer) {
            this.deserializer = responseDeserializer;
            return this;
        }

        public DataProvidersBuilder with(HttpFetcherType flavor) {
            this.httpFetcherType = flavor;
            return this;
        }

        @SuppressWarnings("HiddenField")
        public DataProvidersBuilder with(OAuth2TokenCache tokenCache) {
            this.tokenCache = tokenCache;
            return this;
        }

        @SuppressWarnings("HiddenField")
        public DataProvidersBuilder with(UserAgentProvider userAgentProvider) {
            this.userAgentProvider = userAgentProvider;
            return this;
        }

        @SuppressWarnings({ "HiddenField" })
        public DataProvidersBuilder withForcefullyOverriddenFetcherTimeoutInSeconds(
            Integer forcefullyOverriddenTimeoutInSeconds
        ) {
            this.forcefullyOverriddenTimeoutInSeconds = forcefullyOverriddenTimeoutInSeconds;
            return this;
        }

        @SuppressWarnings("HiddenField")
        public <T> DataProvider<T> build() {
            HttpDataFetcher dataFetcher;
            val deprecatedConfiguration = ofNullable(this.deprecatedConfiguration)
                .orElse(mock(SdkInternalConfiguration.class));

            dataFetcher = createDataFetcher(deprecatedConfiguration);

            return new DataProvider<>(
                url,
                deprecatedConfiguration,
                dataFetcher,
                ofNullable(deserializer).orElse(mock(Deserializer.class))
            );
        }

        @NotNull
        @SuppressWarnings("HiddenField")
        private HttpDataFetcher createDataFetcher(SdkInternalConfiguration deprecatedConfiguration) {
            val httpClient = createStartedAsyncHttpClientFor(deprecatedConfiguration);
            val statistics = new UnifiedOddsStatistics();
            val responseHandler = new HttpResponseHandler();
            val traceIdProvider = new TraceIdProvider();
            val config = ofNullable(this.config).orElse(mock(UofConfiguration.class));
            val userAgentProvider = ofNullable(this.userAgentProvider).orElse(mock(UserAgentProvider.class));
            val tokenCache = ofNullable(this.tokenCache).orElse(failingWithOAuth2TokenRetrievalException());
            if (httpFetcherType == HttpFetcherType.FAST_HTTP_FETCHER) {
                return new LogFastHttpDataFetcher(
                    config,
                    httpClient,
                    statistics,
                    responseHandler,
                    userAgentProvider,
                    traceIdProvider,
                    tokenCache
                );
            } else if (forcefullyOverriddenTimeoutInSeconds != null) {
                return new HttpDataFetcherWithCustomTimeout(
                    config,
                    httpClient,
                    statistics,
                    responseHandler,
                    userAgentProvider,
                    traceIdProvider,
                    forcefullyOverriddenTimeoutInSeconds,
                    tokenCache
                );
            } else {
                return new LogHttpDataFetcher(
                    config,
                    httpClient,
                    statistics,
                    responseHandler,
                    userAgentProvider,
                    traceIdProvider,
                    tokenCache
                );
            }
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

    public static enum HttpFetcherType {
        NORMAL_HTTP_FETCHER,
        FAST_HTTP_FETCHER,
    }

    @SuppressWarnings({ "ParameterNumber" })
    public static final class HttpDataFetcherWithCustomTimeout extends HttpDataFetcher {

        HttpDataFetcherWithCustomTimeout(
            UofConfiguration config,
            CloseableHttpAsyncClient httpClient,
            UnifiedOddsStatistics statsBean,
            HttpResponseHandler responseHandler,
            UserAgentProvider userAgentProvider,
            TraceIdProvider traceIdProvider,
            long timeoutSeconds,
            OAuth2TokenCache oauthTokenCache
        ) {
            super(
                config,
                httpClient,
                statsBean,
                responseHandler,
                userAgentProvider,
                traceIdProvider,
                timeoutSeconds,
                oauthTokenCache
            );
        }
    }
}
