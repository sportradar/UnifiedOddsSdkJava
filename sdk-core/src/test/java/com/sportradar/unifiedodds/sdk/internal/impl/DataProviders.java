/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */
package com.sportradar.unifiedodds.sdk.internal.impl;

import static java.util.Optional.ofNullable;
import static org.mockito.Mockito.mock;

public class DataProviders {

    public static DataProvidersBuilder createDataProviderFor(String url) {
        return new DataProvidersBuilder(url);
    }

    public static final class DataProvidersBuilder {

        private final String url;
        private SdkInternalConfiguration configuration;
        private Deserializer deserializer;
        private HttpDataFetcher dataFetcher;

        public DataProvidersBuilder(String apiUrl) {
            this.url = apiUrl;
        }

        public DataProvidersBuilder with(SdkInternalConfiguration config) {
            this.configuration = config;
            return this;
        }

        public DataProvidersBuilder with(Deserializer responseDeserializer) {
            this.deserializer = responseDeserializer;
            return this;
        }

        public DataProvidersBuilder with(HttpDataFetcher fetcher) {
            this.dataFetcher = fetcher;
            return this;
        }

        public <T> DataProvider<T> build() {
            return new DataProvider<>(
                url,
                ofNullable(configuration).orElse(mock(SdkInternalConfiguration.class)),
                ofNullable(dataFetcher).orElse(mock(LogHttpDataFetcher.class)),
                ofNullable(deserializer).orElse(mock(Deserializer.class))
            );
        }
    }
}
