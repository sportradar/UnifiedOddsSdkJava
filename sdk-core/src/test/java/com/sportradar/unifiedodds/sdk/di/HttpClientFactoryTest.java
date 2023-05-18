package com.sportradar.unifiedodds.sdk.di;

import static org.junit.Assert.assertNotNull;

import org.apache.http.impl.client.CloseableHttpClient;
import org.junit.Test;

@SuppressWarnings({ "MagicNumber" })
public class HttpClientFactoryTest {

    private final HttpClientFactory factory = new HttpClientFactory();

    @Test
    public void should_create_http_client() {
        int maxTimeoutInMillis = 5000;
        int connectionPoolSize = 2;
        int maxConcurrentConnectionsPerRoute = 3;

        CloseableHttpClient client = factory.create(
            maxTimeoutInMillis,
            connectionPoolSize,
            maxConcurrentConnectionsPerRoute
        );

        assertNotNull(client);
    }
}
