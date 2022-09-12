/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */

package com.sportradar.unifiedodds.sdk.impl;

import com.google.common.base.Stopwatch;
import com.google.inject.Inject;
import com.google.inject.name.Named;
import com.sportradar.unifiedodds.sdk.LoggerDefinitions;
import com.sportradar.unifiedodds.sdk.SDKInternalConfiguration;
import com.sportradar.unifiedodds.sdk.exceptions.internal.CommunicationException;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.impl.client.CloseableHttpClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.TimeUnit;

/**
 * Wrapper class for the {@link HttpDataFetcher} with the sole purpose of API request logging
 */
public class LogFastHttpDataFetcher extends HttpDataFetcher {
    private final static Logger logger = LoggerFactory.getLogger(LogFastHttpDataFetcher.class);
    private final static Logger trafficLogger = LoggerFactory.getLogger(LoggerDefinitions.UFSdkRestTrafficLog.class);

    @Inject
    public LogFastHttpDataFetcher(SDKInternalConfiguration config,
                                  @Named("FastHttpClient") CloseableHttpClient httpClient,
                                  UnifiedOddsStatistics statsBean,
                                  @Named("SportsApiJaxbDeserializer") Deserializer apiDeserializer) {
        super(config, httpClient, statsBean, apiDeserializer);
    }

    @Override
    protected HttpData send(HttpRequestBase request) throws CommunicationException {
        String path = request.getURI().toString();
        logger.info("Fetching data from: " + path);

        Stopwatch timer = Stopwatch.createStarted();
        HttpData result;
        try {
            result = super.send(request);
        } catch (CommunicationException e) {
            trafficLogger.info("Request[DataFetcher]: {}, response - FAILED({} ms), ex:", path, timer.stop().elapsed(TimeUnit.MILLISECONDS), e);
            throw new CommunicationException("HTTP request failed(" + path + ")", e);
        }

        if (trafficLogger.isInfoEnabled()) {
            String cleanResponse = result.getResponse() == null ? null : result.getResponse().replace("\n", "");
            trafficLogger.info("Request[DataFetcher]: {}, response - OK({} ms): {}", path, timer.stop().elapsed(TimeUnit.MILLISECONDS), cleanResponse);
        }

        return result;
    }
}
