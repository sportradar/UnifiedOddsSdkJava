/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */
package com.sportradar.unifiedodds.sdk.internal.impl;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.sportradar.unifiedodds.sdk.internal.di.HttpClientFactory;
import java.io.IOException;
import java.util.concurrent.TimeUnit;
import lombok.val;
import org.apache.hc.client5.http.classic.methods.HttpUriRequest;
import org.apache.hc.client5.http.impl.async.CloseableHttpAsyncClient;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.core5.http.io.HttpClientResponseHandler;

public class HttpClients {

    public static CloseableHttpClient createHttpClientFor(SdkInternalConfiguration cfg) {
        int maxTimeoutInMillis = Math.toIntExact(
            TimeUnit.MILLISECONDS.convert(cfg.getHttpClientTimeout(), TimeUnit.SECONDS)
        );
        return new HttpClientFactory()
            .create(maxTimeoutInMillis, cfg.getHttpClientMaxConnTotal(), cfg.getHttpClientMaxConnPerRoute());
    }

    public static CloseableHttpClient createHttpClientThatThrowsIoException() throws IOException {
        val httpClient = mock(CloseableHttpClient.class);
        when(httpClient.execute(any(HttpUriRequest.class), any(HttpClientResponseHandler.class)))
            .thenThrow(IOException.class);

        return httpClient;
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
