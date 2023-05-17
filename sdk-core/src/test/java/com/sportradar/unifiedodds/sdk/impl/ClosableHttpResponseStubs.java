/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */

package com.sportradar.unifiedodds.sdk.impl;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import lombok.val;
import org.apache.http.HttpEntity;
import org.apache.http.StatusLine;
import org.apache.http.client.methods.CloseableHttpResponse;

public class ClosableHttpResponseStubs {

    private static final int HTTP_OK = 200;

    private ClosableHttpResponseStubs() {}

    public static CloseableHttpResponse httpOk(final String content) throws IOException {
        val response = mock(CloseableHttpResponse.class);

        final StatusLine statusLine = statusOf(HTTP_OK);
        when(response.getStatusLine()).thenReturn(statusLine);

        HttpEntity entity = entityWithContent(content);
        when(response.getEntity()).thenReturn(entity);

        return response;
    }

    private static StatusLine statusOf(int statusCode) {
        val statusLine = mock(StatusLine.class);
        when(statusLine.getStatusCode()).thenReturn(statusCode);
        return statusLine;
    }

    private static HttpEntity entityWithContent(String content) throws IOException {
        HttpEntity entity = mock(HttpEntity.class);
        when(entity.getContent()).thenReturn(new ByteArrayInputStream(content.getBytes()));
        return entity;
    }

    public static CloseableHttpResponse emptyResponseWithCode(final int httpCode, final String content)
        throws IOException {
        val response = mock(CloseableHttpResponse.class);

        final StatusLine statusLine = statusOf(httpCode);
        when(response.getStatusLine()).thenReturn(statusLine);

        HttpEntity entity = entityWithContent(content);
        when(response.getEntity()).thenReturn(entity);

        return response;
    }
}
