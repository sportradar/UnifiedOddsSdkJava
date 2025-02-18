/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */
package com.sportradar.unifiedodds.sdk.internal.impl;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mock;

import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.junit.jupiter.api.Test;

public class LogFastHttpDataFetcherTest extends HttpDataFetcherTest {

    @Override
    public HttpDataFetcher createHttpDataFetcher(
        SdkInternalConfiguration config,
        CloseableHttpClient httpClient,
        UnifiedOddsStatistics statsBean,
        HttpResponseHandler httpResponseHandler,
        UserAgentProvider userAgentProvider
    ) {
        return new LogFastHttpDataFetcher(
            config,
            httpClient,
            statsBean,
            httpResponseHandler,
            userAgentProvider
        );
    }

    @Test
    public void failsToCreateWithNullUserAgentProvider() {
        assertThatThrownBy(() ->
                new LogFastHttpDataFetcher(
                    mock(SdkInternalConfiguration.class),
                    mock(CloseableHttpClient.class),
                    mock(UnifiedOddsStatistics.class),
                    mock(HttpResponseHandler.class),
                    null
                )
            )
            .isInstanceOf(NullPointerException.class)
            .hasMessageContaining("userAgentProvider");
    }
}
