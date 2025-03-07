/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */
package com.sportradar.unifiedodds.sdk.internal.impl;

import static java.util.Optional.ofNullable;
import static org.mockito.Mockito.mock;

import java.time.Instant;
import org.apache.hc.client5.http.impl.async.CloseableHttpAsyncClient;

public class HttpDataFetchers {

    public static LogHttpDataFetcherBuilder createLogDataFetcher() {
        return new LogHttpDataFetcherBuilder();
    }

    public static LogFastHttpDataFetcherBuilder createLogFastDataFetcher() {
        return new LogFastHttpDataFetcherBuilder();
    }

    public static HttpDataFetcherWith20sFutureTimeoutBuilder createDataFetcherWith20sRequestTimeout() {
        return new HttpDataFetcherWith20sFutureTimeoutBuilder();
    }

    public static final class LogHttpDataFetcherBuilder {

        private CloseableHttpAsyncClient httpClient;
        private SdkInternalConfiguration configuration;
        private UserAgentProvider userAgentProvider;

        public LogHttpDataFetcherBuilder with(CloseableHttpAsyncClient client) {
            this.httpClient = client;
            return this;
        }

        public LogHttpDataFetcherBuilder with(SdkInternalConfiguration cfg) {
            this.configuration = cfg;
            return this;
        }

        public LogHttpDataFetcherBuilder with(UserAgentProvider provider) {
            this.userAgentProvider = provider;
            return this;
        }

        public LogHttpDataFetcher build() {
            return new LogHttpDataFetcher(
                ofNullable(configuration).orElse(mock(SdkInternalConfiguration.class)),
                ofNullable(httpClient).orElse(mock(CloseableHttpAsyncClient.class)),
                new UnifiedOddsStatistics(),
                new HttpResponseHandler(),
                ofNullable(userAgentProvider).orElse(new UserAgentProvider("1.0.0", Instant.now()))
            );
        }
    }

    public static final class LogFastHttpDataFetcherBuilder {

        private CloseableHttpAsyncClient httpClient;
        private SdkInternalConfiguration configuration;

        public LogFastHttpDataFetcherBuilder with(CloseableHttpAsyncClient client) {
            this.httpClient = client;
            return this;
        }

        public LogFastHttpDataFetcherBuilder with(SdkInternalConfiguration cfg) {
            this.configuration = cfg;
            return this;
        }

        public LogFastHttpDataFetcher build() {
            return new LogFastHttpDataFetcher(
                ofNullable(configuration).orElse(mock(SdkInternalConfiguration.class)),
                ofNullable(httpClient).orElse(mock(CloseableHttpAsyncClient.class)),
                new UnifiedOddsStatistics(),
                new HttpResponseHandler(),
                new UserAgentProvider("1.0.0", Instant.now())
            );
        }
    }

    @SuppressWarnings("MagicNumber")
    public static final class HttpDataFetcherWith20sFutureTimeoutBuilder {

        private CloseableHttpAsyncClient httpClient;
        private SdkInternalConfiguration configuration;

        public HttpDataFetcherWith20sFutureTimeoutBuilder with(CloseableHttpAsyncClient client) {
            this.httpClient = client;
            return this;
        }

        public HttpDataFetcherWith20sFutureTimeoutBuilder with(SdkInternalConfiguration cfg) {
            this.configuration = cfg;
            return this;
        }

        public HttpDataFetcherWithCustomTimeout build() {
            return new HttpDataFetcherWithCustomTimeout(
                ofNullable(configuration).orElse(mock(SdkInternalConfiguration.class)),
                ofNullable(httpClient).orElse(mock(CloseableHttpAsyncClient.class)),
                new UnifiedOddsStatistics(),
                new HttpResponseHandler(),
                new UserAgentProvider("1.0.0", Instant.now()),
                20
            );
        }
    }

    public static final class HttpDataFetcherWithCustomTimeout extends HttpDataFetcher {

        HttpDataFetcherWithCustomTimeout(
            SdkInternalConfiguration config,
            CloseableHttpAsyncClient httpClient,
            UnifiedOddsStatistics statsBean,
            HttpResponseHandler responseHandler,
            UserAgentProvider userAgentProvider,
            long timeoutSeconds
        ) {
            super(config, httpClient, statsBean, responseHandler, userAgentProvider, timeoutSeconds);
        }
    }
}
