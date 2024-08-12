/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */
package com.sportradar.unifiedodds.sdk.impl;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mock;

import com.sportradar.unifiedodds.sdk.SdkInternalConfiguration;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.junit.jupiter.api.Test;

public class LogHttpDataFetcherTest extends HttpDataFetcherTest {

    @Override
    public HttpDataFetcher createHttpDataFetcher(
        SdkInternalConfiguration config,
        CloseableHttpClient httpClient,
        UnifiedOddsStatistics statsBean,
        HttpResponseHandler httpResponseHandler,
        UserAgentProvider userAgentProvider
    ) {
        return new LogHttpDataFetcher(config, httpClient, statsBean, httpResponseHandler, userAgentProvider);
    }

    @Test
    public void failsToCreateWithNullUserAgentProvider() {
        assertThatThrownBy(() ->
                new LogHttpDataFetcher(
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
