/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */
package com.sportradar.unifiedodds.sdk.internal.impl;

import com.sportradar.unifiedodds.sdk.internal.di.HttpClientFactory;
import java.util.concurrent.TimeUnit;
import lombok.val;
import org.apache.hc.client5.http.impl.async.CloseableHttpAsyncClient;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;

public class HttpClients {

    public static CloseableHttpClient createHttpClientFor(SdkInternalConfiguration cfg) {
        int maxTimeoutInMillis = Math.toIntExact(
            TimeUnit.MILLISECONDS.convert(cfg.getHttpClientTimeout(), TimeUnit.SECONDS)
        );
        return new HttpClientFactory()
            .create(maxTimeoutInMillis, cfg.getHttpClientMaxConnTotal(), cfg.getHttpClientMaxConnPerRoute());
    }

    public static CloseableHttpAsyncClient createStartedAsyncHttpClientFor(SdkInternalConfiguration cfg) {
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
