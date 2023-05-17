/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */
package com.sportradar.unifiedodds.sdk.impl;

import com.sportradar.unifiedodds.sdk.SDKInternalConfiguration;
import org.apache.http.impl.client.CloseableHttpClient;

public class LogHttpDataFetcherTest extends HttpDataFetcherTest {

    @Override
    public HttpDataFetcher createHttpDataFetcher(
        SDKInternalConfiguration config,
        CloseableHttpClient httpClient,
        UnifiedOddsStatistics statsBean,
        Deserializer apiDeserializer
    ) {
        return new LogHttpDataFetcher(config, httpClient, statsBean, apiDeserializer);
    }
}
