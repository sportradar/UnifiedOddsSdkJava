/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */
package com.sportradar.unifiedodds.sdk.internal.impl;

import static org.mockito.Mockito.mock;

import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;

public class LogFastHttpDataFetcherWithStubbedHttpClientTest
    extends HttpDataFetcherWithStubbedHttpClientTest {

    @Override
    public HttpDataFetcher createHttpDataFetcher(
        SdkInternalConfiguration config,
        CloseableHttpClient httpClient,
        UnifiedOddsStatistics statsBean,
        HttpResponseHandler httpResponseHandler
    ) {
        return new LogFastHttpDataFetcher(
            config,
            httpClient,
            statsBean,
            httpResponseHandler,
            mock(UserAgentProvider.class)
        );
    }
}
