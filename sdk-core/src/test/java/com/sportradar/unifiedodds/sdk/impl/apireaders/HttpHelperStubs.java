/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */
package com.sportradar.unifiedodds.sdk.impl.apireaders;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

import com.sportradar.unifiedodds.sdk.internal.impl.apireaders.HttpHelper;
import lombok.SneakyThrows;
import lombok.val;
import org.apache.hc.core5.http.HttpStatus;
import org.mockito.ArgumentCaptor;

public class HttpHelperStubs {

    @SneakyThrows
    public static HttpHelper acceptingPostRequests() {
        val httpClient = mock(HttpHelper.class);
        when(httpClient.post(anyString()))
            .thenReturn(new HttpHelper.ResponseData(HttpStatus.SC_ACCEPTED, "message accepted"));
        return httpClient;
    }

    @SneakyThrows
    public static UrlAssert verifyUrlPostedTo(HttpHelper httpClient) {
        ArgumentCaptor<String> url = ArgumentCaptor.forClass(String.class);
        verify(httpClient).post(url.capture());

        return UrlAssert.assertThat(url.getValue());
    }
}
