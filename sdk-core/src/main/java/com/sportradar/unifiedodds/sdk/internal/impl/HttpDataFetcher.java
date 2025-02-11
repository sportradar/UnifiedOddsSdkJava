/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */

package com.sportradar.unifiedodds.sdk.internal.impl;

import com.google.common.base.Preconditions;
import com.sportradar.unifiedodds.sdk.exceptions.CommunicationException;
import com.sportradar.unifiedodds.sdk.internal.impl.http.ApiResponseHandlingException;
import java.io.IOException;
import org.apache.hc.client5.http.classic.methods.HttpGet;
import org.apache.hc.client5.http.classic.methods.HttpPost;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.core5.http.ClassicHttpRequest;
import org.apache.hc.core5.http.HttpEntity;

abstract class HttpDataFetcher {

    private final SdkInternalConfiguration config;
    private final CloseableHttpClient httpClient;
    private final UnifiedOddsStatistics statsBean;
    private final HttpResponseHandler responseHandler;
    private UserAgentProvider userAgentProvider;

    HttpDataFetcher(
        SdkInternalConfiguration config,
        CloseableHttpClient httpClient,
        UnifiedOddsStatistics statsBean,
        HttpResponseHandler responseHandler,
        UserAgentProvider userAgentProvider
    ) {
        Preconditions.checkNotNull(config);
        Preconditions.checkNotNull(httpClient);
        Preconditions.checkNotNull(statsBean);
        Preconditions.checkNotNull(responseHandler);
        Preconditions.checkNotNull(userAgentProvider, "userAgentProvider");

        this.config = config;
        this.httpClient = httpClient;
        this.statsBean = statsBean;
        this.responseHandler = responseHandler;
        this.userAgentProvider = userAgentProvider;
    }

    public HttpData get(String path) throws CommunicationException {
        return send(new HttpGet(path), path);
    }

    public HttpData post(String path, HttpEntity content) throws CommunicationException {
        HttpPost httpPost = new HttpPost(path);
        httpPost.setEntity(content);
        return send(httpPost, path);
    }

    protected HttpData send(ClassicHttpRequest httpRequest, String path) throws CommunicationException {
        if (statsBean != null) {
            statsBean.onStreamingHttpGet(path);
        }
        httpRequest.addHeader("x-access-token", config.getAccessToken());
        httpRequest.addHeader("User-Agent", userAgentProvider.asHeaderValue());
        try {
            return httpClient.execute(
                httpRequest,
                httpResponse -> responseHandler.extractHttpDataFromHttpResponse(httpResponse, path)
            );
        } catch (IOException e) {
            throw new CommunicationException("There was a problem retrieving the requested data", path, e);
        } catch (ApiResponseHandlingException e) {
            throw new CommunicationException(e.getMessage(), path, e.getHttpStatusCode(), e);
        }
    }
}
