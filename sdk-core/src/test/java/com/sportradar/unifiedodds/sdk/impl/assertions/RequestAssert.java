/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */

package com.sportradar.unifiedodds.sdk.impl.assertions;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;

import java.io.IOException;
import lombok.val;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.core5.http.ClassicHttpRequest;
import org.apache.hc.core5.http.io.HttpClientResponseHandler;
import org.assertj.core.api.AbstractAssert;
import org.assertj.core.api.Assertions;
import org.mockito.ArgumentCaptor;

public class RequestAssert extends AbstractAssert<RequestAssert, CloseableHttpClient> {

    private RequestAssert(CloseableHttpClient httpClient) {
        super(httpClient, RequestAssert.class);
    }

    public static RequestAssert assertThat(CloseableHttpClient httpClient) {
        return new RequestAssert(httpClient);
    }

    public RequestAssert hasSentRequestWithHeader(String headerName, String headerValue) throws IOException {
        ArgumentCaptor<ClassicHttpRequest> requestCaptor = ArgumentCaptor.forClass(ClassicHttpRequest.class);
        verify(this.actual).execute(requestCaptor.capture(), any(HttpClientResponseHandler.class));
        val capturedRequest = requestCaptor.getValue();
        val traceActualHeader = capturedRequest.getFirstHeader(headerName);

        Assertions.assertThat(traceActualHeader).isNotNull();
        Assertions.assertThat(traceActualHeader.getValue()).isEqualTo(headerValue);

        return this;
    }
}
