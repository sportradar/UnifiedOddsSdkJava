/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */

package com.sportradar.unifiedodds.sdk.impl;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.sportradar.unifiedodds.sdk.SdkInternalConfiguration;
import com.sportradar.unifiedodds.sdk.exceptions.internal.CommunicationException;
import java.io.IOException;
import org.apache.hc.client5.http.classic.methods.HttpGet;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.core5.http.io.HttpClientResponseHandler;
import org.junit.jupiter.api.Test;

public class HttpDataFetcherLoggersTest {

    private final SdkInternalConfiguration config = mock(SdkInternalConfiguration.class);

    private final CloseableHttpClient httpClient = mock(CloseableHttpClient.class);
    private final UnifiedOddsStatistics statsBean = mock(UnifiedOddsStatistics.class);
    private final HttpResponseHandler responseDataHandler = mock(HttpResponseHandler.class);

    @Test
    public void logFastHttpDataFetcherShouldThrowCommunicationErrorForFailedHttpRequest() throws IOException {
        LogFastHttpDataFetcher httpDataFetcher = new LogFastHttpDataFetcher(
            config,
            httpClient,
            statsBean,
            responseDataHandler,
            mock(UserAgentProvider.class)
        );
        httpDataFetcherShouldThrowCommunicationErrorForFailedHttpRequest(httpDataFetcher);
    }

    @Test
    public void logHttpDataFetcherShouldThrowCommunicationErrorForFailedHttpRequest() throws IOException {
        LogHttpDataFetcher httpDataFetcher = new LogHttpDataFetcher(
            config,
            httpClient,
            statsBean,
            responseDataHandler,
            mock(UserAgentProvider.class)
        );
        httpDataFetcherShouldThrowCommunicationErrorForFailedHttpRequest(httpDataFetcher);
    }

    private void httpDataFetcherShouldThrowCommunicationErrorForFailedHttpRequest(
        HttpDataFetcher httpDataFetcher
    ) throws IOException {
        when(httpClient.execute(any(), any(HttpClientResponseHandler.class))).thenThrow(new IOException());

        String anyPath = "/some/path";
        HttpGet anyHttpRequest = new HttpGet(anyPath);

        String expectedCommunicationErrorMessage = "There was a problem retrieving the requested data";
        assertThatThrownBy(() -> httpDataFetcher.send(anyHttpRequest, anyPath))
            .isInstanceOf(CommunicationException.class)
            .hasMessageContaining(expectedCommunicationErrorMessage);
    }
}
