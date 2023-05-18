/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */

package com.sportradar.unifiedodds.sdk.impl;

import static com.sportradar.unifiedodds.sdk.impl.ClosableHttpResponseStubs.httpOk;
import static org.assertj.core.api.Assertions.*;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.mockito.Mockito.*;

import com.sportradar.uf.sportsapi.datamodel.Response;
import com.sportradar.unifiedodds.sdk.SDKInternalConfiguration;
import com.sportradar.unifiedodds.sdk.exceptions.internal.CommunicationException;
import com.sportradar.unifiedodds.sdk.exceptions.internal.DeserializationException;
import java.io.IOException;
import lombok.val;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.impl.client.CloseableHttpClient;
import org.junit.Test;
import org.mockito.ArgumentCaptor;

public abstract class HttpDataFetcherTest {

    private static final String ANY = "any";
    private static final String NO_CONTENT = "";
    private static final String TOKEN = "someToken";
    private static final String ANY_URI = "https://sportradar.com";
    private final CloseableHttpClient httpClient = mock(CloseableHttpClient.class);
    private Deserializer deserializer = mock(Deserializer.class);
    private SDKInternalConfiguration config = mock(SDKInternalConfiguration.class);
    private UnifiedOddsStatistics stats = mock(UnifiedOddsStatistics.class);
    private HttpDataFetcher httpFetcher = createHttpDataFetcher(config, httpClient, stats, deserializer);

    public abstract HttpDataFetcher createHttpDataFetcher(
        SDKInternalConfiguration config,
        CloseableHttpClient httpClient,
        UnifiedOddsStatistics statsBean,
        Deserializer apiDeserializer
    );

    @Test
    public void apiResponseContainingNoContentShouldResultInExceptionExplainingThat()
        throws IOException, DeserializationException {
        val httpOk = httpOk(NO_CONTENT);
        when(httpClient.execute(any())).thenReturn(httpOk);
        when(deserializer.deserialize(any())).thenReturn(parsedMessageAndAction());
        when(config.getAccessToken()).thenReturn(TOKEN);

        CommunicationException exception = catchThrowableOfType(
            () -> httpFetcher.get(ANY_URI),
            CommunicationException.class
        );

        assertEquals("Invalid server response. Message=no message", exception.getMessage());
    }

    @Test
    public void apiResponseContainingNoContentShouldResultInExceptionPreservingUrl()
        throws IOException, DeserializationException {
        val httpOk = httpOk(NO_CONTENT);
        when(httpClient.execute(any())).thenReturn(httpOk);
        when(deserializer.deserialize(any())).thenReturn(parsedMessageAndAction());
        when(config.getAccessToken()).thenReturn(TOKEN);
        String prividedUri = "https://providedUri.com";

        CommunicationException exception = catchThrowableOfType(
            () -> httpFetcher.get(prividedUri),
            CommunicationException.class
        );

        assertEquals(prividedUri, exception.getUrl());
    }

    @Test
    public void apiResponseContainingNoContentShouldResultInExceptionPreservingHttpCode()
        throws IOException, DeserializationException {
        final int okHttpCode = 200;
        val httpOk = ClosableHttpResponseStubs.emptyResponseWithCode(okHttpCode, NO_CONTENT);
        when(httpClient.execute(any())).thenReturn(httpOk);
        when(deserializer.deserialize(any())).thenReturn(parsedMessageAndAction());
        when(config.getAccessToken()).thenReturn(TOKEN);

        CommunicationException exception = catchThrowableOfType(
            () -> httpFetcher.get(ANY_URI),
            CommunicationException.class
        );

        assertEquals(okHttpCode, exception.getHttpStatusCode());
    }

    @Test
    public void apiResponseContainingNoContentShouldResultInExceptionWithNoCause()
        throws IOException, DeserializationException {
        final CloseableHttpResponse httpOk = ClosableHttpResponseStubs.httpOk(NO_CONTENT);
        when(httpClient.execute(any())).thenReturn(httpOk);
        when(deserializer.deserialize(any())).thenReturn(parsedMessageAndAction());
        when(config.getAccessToken()).thenReturn(TOKEN);

        CommunicationException exception = catchThrowableOfType(
            () -> httpFetcher.get(ANY_URI),
            CommunicationException.class
        );

        assertNull(exception.getCause());
    }

    @Test
    public void apiCallsResultingInIoIssuesShouldExplainThatInException() throws IOException {
        when(httpClient.execute(any())).thenThrow(new IOException());
        when(config.getAccessToken()).thenReturn(TOKEN);

        CommunicationException exception = catchThrowableOfType(
            () -> httpFetcher.get(ANY_URI),
            CommunicationException.class
        );

        assertThat(exception.getMessage()).contains("There was a problem");
    }

    @Test
    public void apiCallsResultingInIoIssuesShouldPreserveUrl() throws IOException {
        when(httpClient.execute(any())).thenThrow(new IOException());
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
        when(httpClient.execute(any())).thenThrow(new IOException());
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
        when(httpClient.execute(any())).thenThrow(rootException);
        when(config.getAccessToken()).thenReturn(TOKEN);

        CommunicationException exception = catchThrowableOfType(
            () -> httpFetcher.get(ANY_URI),
            CommunicationException.class
        );

        assertEquals(rootException, exception.getCause());
    }

    @Test
    public void shouldSubmitHttpGetRequestWithToken()
        throws IOException, DeserializationException, CommunicationException {
        val httpOk = httpOk(ANY);
        when(httpClient.execute(any())).thenReturn(httpOk);
        when(deserializer.deserialize(any())).thenReturn(parsedMessageAndAction());
        when(config.getAccessToken()).thenReturn(TOKEN);

        httpFetcher.get(ANY_URI);

        verifyRequestSubmittedHasToken(TOKEN);
    }

    @Test
    public void shouldSubmitHttpPostRequestWithToken()
        throws IOException, DeserializationException, CommunicationException {
        val httpOk = httpOk(ANY);
        when(httpClient.execute(any())).thenReturn(httpOk);
        when(deserializer.deserialize(any())).thenReturn(parsedMessageAndAction());
        when(config.getAccessToken()).thenReturn(TOKEN);

        httpFetcher.post(ANY_URI, mock(HttpEntity.class));

        verifyRequestSubmittedHasToken(TOKEN);
    }

    private void verifyRequestSubmittedHasToken(final String token) throws IOException {
        val request = ArgumentCaptor.forClass(HttpRequestBase.class);
        verify(httpClient).execute(request.capture());
        assertEquals(token, request.getValue().getFirstHeader("x-access-token").getValue());
    }

    private Response parsedMessageAndAction() {
        Response parsed = new Response();
        parsed.setMessage(ANY);
        parsed.setAction(ANY);
        return parsed;
    }
}
