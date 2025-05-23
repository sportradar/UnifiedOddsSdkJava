package com.sportradar.unifiedodds.sdk.internal.di;

import org.apache.hc.client5.http.config.ConnectionConfig;
import org.apache.hc.client5.http.config.RequestConfig;
import org.apache.hc.client5.http.impl.async.CloseableHttpAsyncClient;
import org.apache.hc.client5.http.impl.async.HttpAsyncClientBuilder;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.HttpClientBuilder;
import org.apache.hc.client5.http.impl.io.PoolingHttpClientConnectionManager;
import org.apache.hc.client5.http.impl.io.PoolingHttpClientConnectionManagerBuilder;
import org.apache.hc.client5.http.impl.nio.PoolingAsyncClientConnectionManager;
import org.apache.hc.client5.http.impl.nio.PoolingAsyncClientConnectionManagerBuilder;
import org.apache.hc.core5.util.Timeout;

public class HttpClientFactory {

    public CloseableHttpClient create(
        int maxTimeoutInMillis,
        int connectionPoolSize,
        int maxConcurrentConnectionsPerRoute
    ) {
        Timeout timout = Timeout.ofMilliseconds(maxTimeoutInMillis);

        RequestConfig requestConfig;
        requestConfig = RequestConfig.custom().setConnectionRequestTimeout(timout).build();

        ConnectionConfig connectionConfig = ConnectionConfig
            .custom()
            .setConnectTimeout(timout)
            .setSocketTimeout(timout)
            .build();

        PoolingHttpClientConnectionManager connectionManager = PoolingHttpClientConnectionManagerBuilder
            .create()
            .setMaxConnTotal(connectionPoolSize)
            .setMaxConnPerRoute(maxConcurrentConnectionsPerRoute)
            .setDefaultConnectionConfig(connectionConfig)
            .build();
        return HttpClientBuilder
            .create()
            .useSystemProperties()
            .setConnectionManager(connectionManager)
            .setDefaultRequestConfig(requestConfig)
            .build();
    }

    public CloseableHttpAsyncClient createAsync(
        int maxTimeoutInMillis,
        int connectionPoolSize,
        int maxConcurrentConnectionsPerRoute
    ) {
        Timeout timout = Timeout.ofMilliseconds(maxTimeoutInMillis);

        RequestConfig requestConfig;
        requestConfig = RequestConfig.custom().setConnectionRequestTimeout(timout).build();

        ConnectionConfig connectionConfig = ConnectionConfig
            .custom()
            .setConnectTimeout(timout)
            .setSocketTimeout(timout)
            .build();

        PoolingAsyncClientConnectionManager connectionManager = PoolingAsyncClientConnectionManagerBuilder
            .create()
            .setMaxConnTotal(connectionPoolSize)
            .setMaxConnPerRoute(maxConcurrentConnectionsPerRoute)
            .setDefaultConnectionConfig(connectionConfig)
            .build();
        return HttpAsyncClientBuilder
            .create()
            .useSystemProperties()
            .setConnectionManager(connectionManager)
            .setDefaultRequestConfig(requestConfig)
            .build();
    }
}
