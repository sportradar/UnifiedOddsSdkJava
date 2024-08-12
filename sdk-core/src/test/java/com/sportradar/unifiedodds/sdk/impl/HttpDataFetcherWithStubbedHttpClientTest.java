/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */

package com.sportradar.unifiedodds.sdk.impl;

import static org.junit.Assert.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.sportradar.unifiedodds.sdk.SdkInternalConfiguration;
import com.sportradar.unifiedodds.sdk.exceptions.internal.CommunicationException;
import com.sportradar.unifiedodds.sdk.shared.CloseableHttpClientFixture;
import java.io.IOException;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.core5.http.HttpEntity;
import org.apache.hc.core5.http.ParseException;
import org.junit.jupiter.api.Test;

public abstract class HttpDataFetcherWithStubbedHttpClientTest {

    private final CloseableHttpClientFixture httpClient = new CloseableHttpClientFixture();
    private final SdkInternalConfiguration config = mock(SdkInternalConfiguration.class);
    private final UnifiedOddsStatistics stats = mock(UnifiedOddsStatistics.class);
    private final HttpResponseHandler responseDataHandler = mock(HttpResponseHandler.class);
    private final HttpDataFetcher httpDataFetcher = createHttpDataFetcher(
        config,
        httpClient,
        stats,
        responseDataHandler
    );
    private final String anyPath = "/some/path";

    public abstract HttpDataFetcher createHttpDataFetcher(
        SdkInternalConfiguration config,
        CloseableHttpClient httpClient,
        UnifiedOddsStatistics statsBean,
        HttpResponseHandler httpResponseHandler
    );

    @Test
    public void shouldReturnHttpDataForSuccessfulGetRequest()
        throws IOException, ParseException, CommunicationException {
        when(responseDataHandler.extractHttpDataFromHttpResponse(any(), any()))
            .thenReturn(mock(HttpData.class));
        HttpData returnedHttpData = httpDataFetcher.get(anyPath);

        assertNotNull(returnedHttpData);
    }

    @Test
    public void shouldReturnHttpDataForSuccessfulPostRequest() throws Exception {
        when(responseDataHandler.extractHttpDataFromHttpResponse(any(), any()))
            .thenReturn(mock(HttpData.class));

        HttpData returnedHttpData = httpDataFetcher.post(anyPath, mock(HttpEntity.class));

        assertNotNull(returnedHttpData);
    }
}
