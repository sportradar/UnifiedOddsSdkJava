/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */
package com.sportradar.unifiedodds.sdk.impl;

import static org.assertj.core.api.Assertions.*;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.*;

import com.sportradar.unifiedodds.sdk.SdkInternalConfiguration;
import com.sportradar.unifiedodds.sdk.exceptions.internal.CommunicationException;
import com.sportradar.unifiedodds.sdk.impl.http.ApiResponseHandlingException;
import java.io.IOException;
import lombok.val;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.core5.http.ClassicHttpRequest;
import org.apache.hc.core5.http.HttpEntity;
import org.apache.hc.core5.http.io.HttpClientResponseHandler;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

public abstract class HttpDataFetcherTest {

    private static final String TOKEN = "someToken";
    private static final String ANY_URI = "https://sportradar.com";
    private static final int ANY_STATUS = 400;
    private final CloseableHttpClient httpClient = mock(CloseableHttpClient.class);
    private final SdkInternalConfiguration config = mock(SdkInternalConfiguration.class);
    private final UnifiedOddsStatistics stats = mock(UnifiedOddsStatistics.class);
    private final HttpResponseHandler responseDataHandler = mock(HttpResponseHandler.class);
    private final UserAgentProvider userAgentProvider = mock(UserAgentProvider.class);
    private HttpDataFetcher httpFetcher = createHttpDataFetcher(
        config,
        httpClient,
        stats,
        responseDataHandler,
        userAgentProvider
    );

    public abstract HttpDataFetcher createHttpDataFetcher(
        SdkInternalConfiguration config,
        CloseableHttpClient httpClient,
        UnifiedOddsStatistics statsBean,
        HttpResponseHandler httpResponseHandler,
        UserAgentProvider userAgentProvider
    );

    @Test
    public void failuresCausedByResponseHandlingIssuesPreservesExplanatoryMessage() throws IOException {
        String causalMessage = "causal message";
        when(httpClient.execute(any(), any(HttpClientResponseHandler.class)))
            .thenThrow(new ApiResponseHandlingException(causalMessage, ANY_URI, ANY_STATUS));
        when(config.getAccessToken()).thenReturn(TOKEN);

        CommunicationException exception = catchThrowableOfType(
            () -> httpFetcher.get(ANY_URI),
            CommunicationException.class
        );

        assertEquals(causalMessage, exception.getMessage());
    }

    @Test
    public void failuresCausedByResponseHandlingIssuesPreservesUrl() throws IOException {
        String url = "http://providedUrl.com";
        val cause = new ApiResponseHandlingException(ANY_URI, url, ANY_STATUS);
        when(httpClient.execute(any(), any(HttpClientResponseHandler.class))).thenThrow(cause);
        when(config.getAccessToken()).thenReturn(TOKEN);

        CommunicationException exception = catchThrowableOfType(
            () -> httpFetcher.get(url),
            CommunicationException.class
        );

        assertEquals(url, exception.getUrl());
    }

    @Test
    public void failuresCausedByResponseHandlingIssuesHttpStatusCode() throws IOException {
        final int statusCode = 403;
        val cause = new ApiResponseHandlingException(ANY_URI, ANY_URI, statusCode);
        when(httpClient.execute(any(), any(HttpClientResponseHandler.class))).thenThrow(cause);
        when(config.getAccessToken()).thenReturn(TOKEN);

        CommunicationException exception = catchThrowableOfType(
            () -> httpFetcher.get(ANY_URI),
            CommunicationException.class
        );

        assertEquals(statusCode, exception.getHttpStatusCode());
    }

    @Test
    public void failuresCausedByResponseHandlingIssuesArePreserved() throws IOException {
        val cause = new ApiResponseHandlingException(ANY_URI, ANY_URI, ANY_STATUS);
        when(httpClient.execute(any(), any(HttpClientResponseHandler.class))).thenThrow(cause);
        when(config.getAccessToken()).thenReturn(TOKEN);

        CommunicationException exception = catchThrowableOfType(
            () -> httpFetcher.get(ANY_URI),
            CommunicationException.class
        );

        assertEquals(cause, exception.getCause());
    }

    @Test
    public void apiCallsResultingInIoIssuesShouldPreserveUrl() throws IOException {
        when(httpClient.execute(any(), any(HttpClientResponseHandler.class))).thenThrow(new IOException());
        when(config.getAccessToken()).thenReturn(TOKEN);
        String providedUri = "https://providedUri.com";

        CommunicationException exception = catchThrowableOfType(
            () -> httpFetcher.get(providedUri),
            CommunicationException.class
        );

        assertEquals(providedUri, exception.getUrl());
    }

    @Test
    public void apiCallsResultingInIoIssuesShouldNotContainHttpCode() throws IOException {
        when(httpClient.execute(any(), any(HttpClientResponseHandler.class))).thenThrow(new IOException());
        when(config.getAccessToken()).thenReturn(TOKEN);
        final int notSetHttpCode = -1;

        CommunicationException exception = catchThrowableOfType(
            () -> httpFetcher.get(ANY_URI),
            CommunicationException.class
        );

        assertEquals(notSetHttpCode, exception.getHttpStatusCode());
    }

    @Test
    public void apiCallsResultingInIoIssuesShouldHaveContainItAsCausalException() throws IOException {
        IOException rootException = new IOException();
        when(httpClient.execute(any(), any(HttpClientResponseHandler.class))).thenThrow(rootException);
        when(config.getAccessToken()).thenReturn(TOKEN);

        CommunicationException exception = catchThrowableOfType(
            () -> httpFetcher.get(ANY_URI),
            CommunicationException.class
        );

        assertEquals(rootException, exception.getCause());
    }

    @Test
    public void shouldSubmitHttpGetRequestWithToken() throws IOException, CommunicationException {
        val anyReturnedHttpData = mock(HttpData.class);
        when(httpClient.execute(any(), any(HttpClientResponseHandler.class))).thenReturn(anyReturnedHttpData);
        when(config.getAccessToken()).thenReturn(TOKEN);

        httpFetcher.get(ANY_URI);

        verifyRequestSubmittedHasToken(TOKEN);
    }

    @Test
    public void shouldSubmitHttpPostRequestWithToken() throws IOException, CommunicationException {
        HttpData mockedHttpData = mock(HttpData.class);
        when(httpClient.execute(any(), any(HttpClientResponseHandler.class))).thenReturn(mockedHttpData);
        when(config.getAccessToken()).thenReturn(TOKEN);

        httpFetcher.post(ANY_URI, mock(HttpEntity.class));

        verifyRequestSubmittedHasToken(TOKEN);
    }

    @Test
    public void shouldSubmitHttpGetRequestWithUserAgent() throws IOException, CommunicationException {
        val anyReturnedHttpData = mock(HttpData.class);
        when(httpClient.execute(any(), any(HttpClientResponseHandler.class))).thenReturn(anyReturnedHttpData);
        String userAgentHeaderValue = "UserAgentHeaderValue";
        when(userAgentProvider.asHeaderValue()).thenReturn(userAgentHeaderValue);

        httpFetcher.get(ANY_URI);

        verifyRequestSubmittedHasUserAgentHeader(userAgentHeaderValue);
    }

    @Test
    public void shouldSubmitHttpPostRequestWithUserAgent() throws IOException, CommunicationException {
        HttpData mockedHttpData = mock(HttpData.class);
        when(httpClient.execute(any(), any(HttpClientResponseHandler.class))).thenReturn(mockedHttpData);
        String userAgentHeaderValue = "UserAgentHeaderValue";
        when(userAgentProvider.asHeaderValue()).thenReturn(userAgentHeaderValue);

        httpFetcher.post(ANY_URI, mock(HttpEntity.class));

        verifyRequestSubmittedHasUserAgentHeader(userAgentHeaderValue);
    }

    @Test
    public void sendShouldThrowExceptionWhenExceptionIsThrownDuringResponseHandling() throws IOException {
        when(httpClient.execute(any(), any(HttpClientResponseHandler.class))).thenThrow(IOException.class);
        String anyPath = "some/path";
        String expectedErrorMessage = "There was a problem retrieving the requested data";
        assertThatThrownBy(() -> httpFetcher.get(anyPath)).hasMessageContaining(expectedErrorMessage);
    }

    private void verifyRequestSubmittedHasToken(final String token) throws IOException {
        val request = ArgumentCaptor.forClass(ClassicHttpRequest.class);
        verify(httpClient).execute(request.capture(), any(HttpClientResponseHandler.class));
        assertEquals(token, request.getValue().getFirstHeader("x-access-token").getValue());
    }

    private void verifyRequestSubmittedHasUserAgentHeader(String headerValue) throws IOException {
        val request = ArgumentCaptor.forClass(ClassicHttpRequest.class);
        verify(httpClient).execute(request.capture(), any(HttpClientResponseHandler.class));
        assertEquals(headerValue, request.getValue().getFirstHeader("User-Agent").getValue());
    }
}
