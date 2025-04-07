/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */
package com.sportradar.unifiedodds.sdk.internal.impl;

import static java.util.Optional.ofNullable;
import static org.mockito.Mockito.mock;

import com.sportradar.unifiedodds.sdk.internal.impl.apireaders.HttpHelper;
import com.sportradar.unifiedodds.sdk.internal.impl.apireaders.MessageAndActionExtractor;
import java.time.Instant;
import org.apache.hc.client5.http.impl.async.CloseableHttpAsyncClient;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;

@SuppressWarnings({ "MultipleStringLiterals" })
public class HttpDataFetchers {

    public static LogHttpDataFetcherBuilder createLogDataFetcher() {
        return new LogHttpDataFetcherBuilder();
    }

    public static LogFastHttpDataFetcherBuilder createLogFastDataFetcher() {
        return new LogFastHttpDataFetcherBuilder();
    }

    public static HttpHelperBuilder createHttpHelperBuilder() {
        return new HttpHelperBuilder();
    }

    public static HttpDataFetcherWith20sFutureTimeoutBuilder createDataFetcherWith20sRequestTimeout() {
        return new HttpDataFetcherWith20sFutureTimeoutBuilder();
    }

    public static final class LogHttpDataFetcherBuilder {

        private CloseableHttpAsyncClient httpClient;
        private SdkInternalConfiguration configuration;
        private UserAgentProvider userAgentProvider;
        private TraceIdProvider traceIdProvider;

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

        public LogHttpDataFetcherBuilder with(TraceIdProvider provider) {
            this.traceIdProvider = provider;
            return this;
        }

        public LogHttpDataFetcher build() {
            return new LogHttpDataFetcher(
                ofNullable(configuration).orElse(mock(SdkInternalConfiguration.class)),
                ofNullable(httpClient).orElse(mock(CloseableHttpAsyncClient.class)),
                new UnifiedOddsStatistics(),
                new HttpResponseHandler(),
                ofNullable(userAgentProvider).orElse(new UserAgentProvider("1.0.0", Instant.now())),
                ofNullable(traceIdProvider).orElse(new TraceIdProvider())
            );
        }
    }

    public static final class LogFastHttpDataFetcherBuilder {

        private CloseableHttpAsyncClient httpClient;
        private SdkInternalConfiguration configuration;
        private TraceIdProvider traceIdProvider;

        public LogFastHttpDataFetcherBuilder with(CloseableHttpAsyncClient client) {
            this.httpClient = client;
            return this;
        }

        public LogFastHttpDataFetcherBuilder with(SdkInternalConfiguration cfg) {
            this.configuration = cfg;
            return this;
        }

        public LogFastHttpDataFetcherBuilder with(TraceIdProvider provider) {
            this.traceIdProvider = provider;
            return this;
        }

        public LogFastHttpDataFetcher build() {
            return new LogFastHttpDataFetcher(
                ofNullable(configuration).orElse(mock(SdkInternalConfiguration.class)),
                ofNullable(httpClient).orElse(mock(CloseableHttpAsyncClient.class)),
                new UnifiedOddsStatistics(),
                new HttpResponseHandler(),
                new UserAgentProvider("1.0.0", Instant.now()),
                ofNullable(traceIdProvider).orElse(new TraceIdProvider())
            );
        }
    }

    public static final class HttpHelperBuilder {

        private CloseableHttpClient httpClient;
        private SdkInternalConfiguration configuration;
        private UserAgentProvider userAgentProvider;
        private TraceIdProvider traceIdProvider;

        public HttpHelperBuilder with(CloseableHttpClient client) {
            this.httpClient = client;
            return this;
        }

        public HttpHelperBuilder with(SdkInternalConfiguration cfg) {
            this.configuration = cfg;
            return this;
        }

        public HttpHelperBuilder with(UserAgentProvider provider) {
            this.userAgentProvider = provider;
            return this;
        }

        public HttpHelperBuilder with(TraceIdProvider provider) {
            this.traceIdProvider = provider;
            return this;
        }

        public HttpHelper build() {
            return new HttpHelper(
                ofNullable(configuration).orElse(mock(SdkInternalConfiguration.class)),
                ofNullable(httpClient).orElse(mock(CloseableHttpClient.class)),
                mock(MessageAndActionExtractor.class),
                ofNullable(userAgentProvider).orElse(new UserAgentProvider("1.0.0", Instant.now())),
                ofNullable(traceIdProvider).orElse(new TraceIdProvider())
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
                new TraceIdProvider(),
                20
            );
        }
    }

    @SuppressWarnings({ "ParameterNumber" })
    public static final class HttpDataFetcherWithCustomTimeout extends HttpDataFetcher {

        HttpDataFetcherWithCustomTimeout(
            SdkInternalConfiguration config,
            CloseableHttpAsyncClient httpClient,
            UnifiedOddsStatistics statsBean,
            HttpResponseHandler responseHandler,
            UserAgentProvider userAgentProvider,
            TraceIdProvider traceIdProvider,
            long timeoutSeconds
        ) {
            super(
                config,
                httpClient,
                statsBean,
                responseHandler,
                userAgentProvider,
                traceIdProvider,
                timeoutSeconds
            );
        }
    }
}
