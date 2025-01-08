/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */
package com.sportradar.unifiedodds.sdk.impl.apireaders;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

import lombok.SneakyThrows;
import lombok.val;
import org.apache.hc.core5.http.HttpStatus;
import org.assertj.core.api.AbstractStringAssert;
import org.assertj.core.api.Assertions;
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
    public static AbstractStringAssert<?> verifyUrlPostedTo(HttpHelper httpClient) {
        ArgumentCaptor<String> url = ArgumentCaptor.forClass(String.class);
        verify(httpClient).post(url.capture());

        return Assertions.assertThat(url.getValue());
    }
}
