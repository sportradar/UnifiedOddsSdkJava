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
import org.apache.http.impl.client.CloseableHttpClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Wrapper class for the {@link HttpDataFetcher} with the sole purpose of API request logging
 */
public class LogHttpDataFetcher extends HttpDataFetcher {
    private final static Logger logger = LoggerFactory.getLogger(LogHttpDataFetcher.class);
    private final static Logger trafficLogger = LoggerFactory.getLogger(LoggerDefinitions.UFSdkRestTrafficLog.class);

    @Inject
    public LogHttpDataFetcher(SDKInternalConfiguration config, CloseableHttpClient httpClient, UnifiedOddsStatistics statsBean, @Named("ApiJaxbDeserializer") Deserializer apiDeserializer) {
        super(config, httpClient, statsBean, apiDeserializer);
    }

    @Override
    public HttpData get(String path) throws CommunicationException {
        logger.info("Fetching data from: " + path);

        Stopwatch timer = Stopwatch.createStarted();
        HttpData result;
        try {
            result = super.get(path);
        } catch (CommunicationException e) {
            trafficLogger.info("Request[DataFetcher]: {}, response - FAILED({}), ex:", path, timer.stop(), e);
            throw new CommunicationException("HTTP request failed(" + path + ")", e);
        }

        if (trafficLogger.isInfoEnabled()) {
            String cleanResponse = result.getResponse() == null ? null : result.getResponse().replace("\n", "");
            trafficLogger.info("Request[DataFetcher]: {}, response - OK({}): {}", path, timer.stop(), cleanResponse);
        }

        return result;
    }
}
