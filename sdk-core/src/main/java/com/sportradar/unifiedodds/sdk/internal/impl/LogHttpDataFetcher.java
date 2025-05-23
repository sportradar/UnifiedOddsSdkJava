/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */

package com.sportradar.unifiedodds.sdk.internal.impl;

import com.google.common.base.Stopwatch;
import com.google.inject.Inject;
import com.sportradar.unifiedodds.sdk.LoggerDefinitions;
import com.sportradar.unifiedodds.sdk.exceptions.CommunicationException;
import java.util.concurrent.TimeUnit;
import org.apache.hc.client5.http.impl.async.CloseableHttpAsyncClient;
import org.apache.hc.core5.http.ClassicHttpRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Wrapper class for the {@link HttpDataFetcher} with the sole purpose of API request logging
 */
@SuppressWarnings({ "ConstantName" })
public class LogHttpDataFetcher extends HttpDataFetcher {

    private static final Logger logger = LoggerFactory.getLogger(LogHttpDataFetcher.class);
    private static final Logger trafficLogger = LoggerFactory.getLogger(
        LoggerDefinitions.UfSdkRestTrafficLog.class
    );

    @Inject
    public LogHttpDataFetcher(
        SdkInternalConfiguration config,
        CloseableHttpAsyncClient httpClient,
        UnifiedOddsStatistics statsBean,
        HttpResponseHandler httpResponseHandler,
        UserAgentProvider userAgentProvider,
        TraceIdProvider traceIdProvider
    ) {
        super(
            config,
            httpClient,
            statsBean,
            httpResponseHandler,
            userAgentProvider,
            traceIdProvider,
            config.getHttpClientTimeout()
        );
    }

    @Override
    protected HttpData send(ClassicHttpRequest request, String path) throws CommunicationException {
        logger.info("Fetching data from: " + path);

        Stopwatch timer = Stopwatch.createStarted();
        HttpData result;
        try {
            result = super.send(request, path);
        } catch (CommunicationException e) {
            String traceId = extractTraceId(request);
            trafficLogger.info(
                "Request[DataFetcher]: traceId - {}, {}, response - FAILED({} ms), ex:",
                traceId,
                path,
                timer.stop().elapsed(TimeUnit.MILLISECONDS),
                e
            );
            throw e;
        }

        if (trafficLogger.isInfoEnabled()) {
            String cleanResponse = result.getResponse() == null
                ? null
                : result.getResponse().replace("\n", "");
            String traceId = extractTraceId(request);

            trafficLogger.info(
                "Request[DataFetcher]: traceId - {}, {}, response - OK({} ms): {}",
                traceId,
                path,
                timer.stop().elapsed(TimeUnit.MILLISECONDS),
                cleanResponse
            );
        }

        return result;
    }
}
