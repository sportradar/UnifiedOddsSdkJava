/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */

package com.sportradar.unifiedodds.sdk.impl;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import lombok.val;
import org.apache.hc.client5.http.impl.classic.CloseableHttpResponse;
import org.apache.hc.core5.http.HttpEntity;
import org.apache.hc.core5.http.message.StatusLine;

public class ClosableHttpResponseStubs {

    private static final int HTTP_OK = 200;

    private ClosableHttpResponseStubs() {}

    public static CloseableHttpResponse httpOk(final String content) throws IOException {
        val response = mock(CloseableHttpResponse.class);
        when(response.getCode()).thenReturn(HTTP_OK);

        HttpEntity entity = entityWithContent(content);
        when(response.getEntity()).thenReturn(entity);

        return response;
    }

    private static HttpEntity entityWithContent(String content) throws IOException {
        HttpEntity entity = mock(HttpEntity.class);
        when(entity.getContent()).thenReturn(new ByteArrayInputStream(content.getBytes()));
        return entity;
    }

    public static CloseableHttpResponse emptyResponseWithCode(final int httpCode, final String content)
        throws IOException {
        val response = mock(CloseableHttpResponse.class);

        when(response.getCode()).thenReturn(httpCode);

        HttpEntity entity = entityWithContent(content);
        when(response.getEntity()).thenReturn(entity);

        return response;
    }
}
