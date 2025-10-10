/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */
package com.sportradar.unifiedodds.sdk.internal.impl;

import static org.mockito.Mockito.mock;

import com.sportradar.unifiedodds.sdk.cfg.UofConfiguration;
import com.sportradar.unifiedodds.sdk.internal.commoniam.OAuth2TokenCache;
import javax.xml.bind.JAXBException;
import org.apache.hc.client5.http.impl.async.CloseableHttpAsyncClient;

public class LogHttpDataFetcherIT extends HttpDataFetcherIT {

    public LogHttpDataFetcherIT() throws JAXBException {}

    @Override
    public HttpDataFetcher createHttpDataFetcher(
        UofConfiguration config,
        CloseableHttpAsyncClient httpClient,
        UnifiedOddsStatistics statsBean,
        HttpResponseHandler httpResponseHandler
    ) {
        return new LogHttpDataFetcher(
            config,
            httpClient,
            statsBean,
            httpResponseHandler,
            mock(UserAgentProvider.class),
            mock(TraceIdProvider.class),
            mock(OAuth2TokenCache.class)
        );
    }
}
