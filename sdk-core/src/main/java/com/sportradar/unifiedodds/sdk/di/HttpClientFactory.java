package com.sportradar.unifiedodds.sdk.di;

import org.apache.hc.client5.http.config.ConnectionConfig;
import org.apache.hc.client5.http.config.RequestConfig;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.HttpClientBuilder;
import org.apache.hc.client5.http.impl.io.PoolingHttpClientConnectionManager;
import org.apache.hc.client5.http.impl.io.PoolingHttpClientConnectionManagerBuilder;
import org.apache.hc.core5.util.Timeout;

class HttpClientFactory {

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
}
