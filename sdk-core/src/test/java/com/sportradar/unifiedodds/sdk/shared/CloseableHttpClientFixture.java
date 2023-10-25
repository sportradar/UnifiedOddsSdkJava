/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */
package com.sportradar.unifiedodds.sdk.shared;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.CloseableHttpResponse;
import org.apache.hc.core5.http.*;
import org.apache.hc.core5.http.protocol.HttpContext;
import org.apache.hc.core5.io.CloseMode;

public class CloseableHttpClientFixture extends CloseableHttpClient {

    private final CloseableHttpResponse httpResponse = mock(CloseableHttpResponse.class);
    private ClassicHttpRequest lastRequest;

    @Override
    public CloseableHttpResponse doExecute(
        HttpHost httpHost,
        ClassicHttpRequest classicHttpRequest,
        HttpContext httpContext
    ) {
        lastRequest = classicHttpRequest;
        return httpResponse;
    }

    @Override
    public void close() throws IOException {}

    @Override
    public void close(CloseMode closeMode) {}

    public void setupHttpResponseAndHttpEntity(int statusCode, String anyXmlResponse) throws IOException {
        HttpEntity httpEntity = mock(HttpEntity.class);

        when(httpResponse.getCode()).thenReturn(statusCode);
        when(httpResponse.getEntity()).thenReturn(httpEntity);

        ByteArrayInputStream inputStreamWithSomeXmlResponse = new ByteArrayInputStream(
            anyXmlResponse.getBytes()
        );

        when(httpEntity.getContent()).thenReturn(inputStreamWithSomeXmlResponse);
        Header[] anyEmptyHeaderArray = new Header[0];
        when(httpResponse.getHeaders()).thenReturn(anyEmptyHeaderArray);
    }

    public void setupHttpResponseWithEmptyEntity(int anyStatusCode) {
        when(httpResponse.getCode()).thenReturn(anyStatusCode);
        when(httpResponse.getEntity()).thenReturn(null);
    }

    public void verifyUserAgentWas(String expectedUserAgent) throws ProtocolException {
        Header userAgent = lastRequest.getHeader("User-Agent");
        assertThat(userAgent).isNotNull();
        assertThat(userAgent.getValue()).isEqualTo(expectedUserAgent);
    }
}
