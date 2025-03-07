/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */
package com.sportradar.unifiedodds.sdk.internal.impl;

import static org.mockito.Mockito.mock;

import javax.xml.bind.JAXBException;
import org.apache.hc.client5.http.impl.async.CloseableHttpAsyncClient;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;

public class LogHttpDataFetcherIT extends HttpDataFetcherIT {

    public LogHttpDataFetcherIT() throws JAXBException {}

    @Override
    public HttpDataFetcher createHttpDataFetcher(
        SdkInternalConfiguration config,
        CloseableHttpAsyncClient httpClient,
        UnifiedOddsStatistics statsBean,
        HttpResponseHandler httpResponseHandler
    ) {
        return new LogHttpDataFetcher(
            config,
            httpClient,
            statsBean,
            httpResponseHandler,
            mock(UserAgentProvider.class)
        );
    }
}
