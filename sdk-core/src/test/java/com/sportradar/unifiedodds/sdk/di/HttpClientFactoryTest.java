package com.sportradar.unifiedodds.sdk.di;

import static org.junit.Assert.assertNotNull;

import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.junit.jupiter.api.Test;

@SuppressWarnings({ "MagicNumber" })
public class HttpClientFactoryTest {

    private final HttpClientFactory factory = new HttpClientFactory();

    @Test
    public void httpClientFactoryShouldCreateClosableHttpClientWithGivenMaxConnTotal() {
        int anyMaxTimout = 100;
        int anyConnectionPoolSize = 2;
        int anyMaxConcurrentConnectionPerRote = 3;

        CloseableHttpClient client = factory.create(
            anyMaxTimout,
            anyConnectionPoolSize,
            anyMaxConcurrentConnectionPerRote
        );

        assertNotNull(client);
    }
}
