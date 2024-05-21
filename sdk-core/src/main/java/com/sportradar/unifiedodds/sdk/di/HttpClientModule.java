/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */

package com.sportradar.unifiedodds.sdk.di;

import com.google.inject.Binder;
import com.google.inject.Module;
import com.google.inject.Provides;
import com.google.inject.Singleton;
import com.google.inject.name.Named;
import com.sportradar.unifiedodds.sdk.RuntimeConfiguration;
import com.sportradar.unifiedodds.sdk.SdkInternalConfiguration;
import com.sportradar.unifiedodds.sdk.impl.Deserializer;
import com.sportradar.unifiedodds.sdk.impl.UserAgentProvider;
import com.sportradar.unifiedodds.sdk.impl.apireaders.HttpHelper;
import com.sportradar.unifiedodds.sdk.impl.apireaders.MessageAndActionExtractor;
import java.util.concurrent.TimeUnit;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;

public class HttpClientModule implements Module {

    private final HttpClientFactory httpClientFactory;
    private SdkInternalConfiguration configuration;

    HttpClientModule(SdkInternalConfiguration configuration) {
        this.configuration = configuration;
        httpClientFactory = new HttpClientFactory();
    }

    @Override
    public void configure(Binder binder) {}

    /**
     * Provides the http client used to fetch data from the API
     */
    @Provides
    @Singleton
    CloseableHttpClient provideHttpClient() {
        int maxTimeoutInMillis = Math.toIntExact(
            TimeUnit.MILLISECONDS.convert(configuration.getHttpClientTimeout(), TimeUnit.SECONDS)
        );
        int connectionPoolSize = configuration.getHttpClientMaxConnTotal();
        int maxConcurrentConnectionsPerRoute = configuration.getHttpClientMaxConnPerRoute();

        return httpClientFactory.create(
            maxTimeoutInMillis,
            connectionPoolSize,
            maxConcurrentConnectionsPerRoute
        );
    }

    @Provides
    @Singleton
    @Named("FastHttpClient")
    CloseableHttpClient provideCriticalHttpClient() {
        int maxTimeoutInMillis = (int) RuntimeConfiguration.getFastHttpClientTimeout().toMillis();
        int connectionPoolSize = configuration.getHttpClientMaxConnTotal();
        int maxConcurrentConnectionsPerRoute = configuration.getHttpClientMaxConnPerRoute();

        return httpClientFactory.create(
            maxTimeoutInMillis,
            connectionPoolSize,
            maxConcurrentConnectionsPerRoute
        );
    }

    /**
     * Provides the http client used to fetch data from the API
     */
    @Provides
    @Singleton
    @Named("RecoveryHttpClient")
    CloseableHttpClient provideRecoveryHttpClient() {
        int maxTimeoutInMillis = Math.toIntExact(
            TimeUnit.MILLISECONDS.convert(configuration.getRecoveryHttpClientTimeout(), TimeUnit.SECONDS)
        );
        int connectionPoolSize = configuration.getRecoveryHttpClientMaxConnTotal();
        int maxConcurrentConnectionsPerRoute = configuration.getRecoveryHttpClientMaxConnPerRoute();

        return httpClientFactory.create(
            maxTimeoutInMillis,
            connectionPoolSize,
            maxConcurrentConnectionsPerRoute
        );
    }

    /**
     * Provides the http client used to fetch data from the API
     */
    @Provides
    @Named("RecoveryHttpHelper")
    private HttpHelper provideRecoveryHttpHelper(
        SdkInternalConfiguration config,
        @Named("RecoveryHttpClient") CloseableHttpClient httpClient,
        @Named("SportsApiJaxbDeserializer") Deserializer apiDeserializer,
        UserAgentProvider userAgentProvider
    ) {
        return new HttpHelper(config, httpClient, new MessageAndActionExtractor(), userAgentProvider);
    }
}
