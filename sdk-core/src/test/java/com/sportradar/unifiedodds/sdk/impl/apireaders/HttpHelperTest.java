/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */

package com.sportradar.unifiedodds.sdk.impl.apireaders;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowableOfType;
import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.sportradar.unifiedodds.sdk.SdkInternalConfiguration;
import com.sportradar.unifiedodds.sdk.exceptions.internal.CommunicationException;
import com.sportradar.unifiedodds.sdk.impl.Deserializer;
import com.sportradar.unifiedodds.sdk.impl.UserAgentProvider;
import java.io.IOException;
import org.apache.hc.client5.http.classic.methods.HttpUriRequest;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.core5.http.io.HttpClientResponseHandler;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

public class HttpHelperTest {

    private static final int NO_STATUS = -1;
    private static final String ANY_URL = "http://anyUrl.com";

    @Nested
    public class WhenIoExceptionHappensDuringPost {

        private final SdkInternalConfiguration config = mock(SdkInternalConfiguration.class);
        private final CloseableHttpClient httpClient = mock(CloseableHttpClient.class);
        private final MessageAndActionExtractor messageExtractor = mock(MessageAndActionExtractor.class);
        private final UserAgentProvider userAgent = mock(UserAgentProvider.class);
        private HttpHelper httpHelper = new HttpHelper(config, httpClient, messageExtractor, userAgent);

        @Test
        public void urlShouldBePreservedWhenIoExceptionHappensDuringPost() throws IOException {
            String providedUrl = "https://providedUrl.com";
            when(httpClient.execute(any(HttpUriRequest.class), any(HttpClientResponseHandler.class)))
                .thenThrow(IOException.class);

            CommunicationException exception = catchThrowableOfType(
                () -> httpHelper.post(providedUrl),
                CommunicationException.class
            );

            assertEquals(providedUrl, exception.getUrl());
        }

        @Test
        public void noStatusCodeShouldBePreservedWhenIoExceptionHappensDuringPost() throws IOException {
            when(httpClient.execute(any(HttpUriRequest.class), any(HttpClientResponseHandler.class)))
                .thenThrow(IOException.class);

            CommunicationException exception = catchThrowableOfType(
                () -> httpHelper.post(ANY_URL),
                CommunicationException.class
            );

            assertEquals(NO_STATUS, exception.getHttpStatusCode());
        }

        @Test
        public void rootCauseShouldBePreservedWhenIoExceptionHappensDuringPost() throws IOException {
            IOException ioException = new IOException();
            when(httpClient.execute(any(HttpUriRequest.class), any(HttpClientResponseHandler.class)))
                .thenThrow(ioException);

            CommunicationException exception = catchThrowableOfType(
                () -> httpHelper.post(ANY_URL),
                CommunicationException.class
            );

            assertThat(exception).hasRootCause(ioException);
        }

        @Test
        public void messageShouldExplainThatIoExceptionHappensDuringHttpCall() throws IOException {
            IOException ioException = new IOException();
            when(httpClient.execute(any(HttpUriRequest.class), any(HttpClientResponseHandler.class)))
                .thenThrow(ioException);

            CommunicationException exception = catchThrowableOfType(
                () -> httpHelper.post(ANY_URL),
                CommunicationException.class
            );

            assertThat(exception.getMessage()).contains("Problems executing POST request");
        }
    }

    @Nested
    public class WhenIoExceptionHappensDuringPut {

        private final SdkInternalConfiguration config = mock(SdkInternalConfiguration.class);
        private final CloseableHttpClient httpClient = mock(CloseableHttpClient.class);
        private final MessageAndActionExtractor messageExtractor = mock(MessageAndActionExtractor.class);
        private final UserAgentProvider userAgent = mock(UserAgentProvider.class);
        private HttpHelper httpHelper = new HttpHelper(config, httpClient, messageExtractor, userAgent);

        @Test
        public void urlShouldBePreserved() throws IOException {
            String providedUrl = "https://providedUrl.com";
            when(httpClient.execute(any(HttpUriRequest.class), any(HttpClientResponseHandler.class)))
                .thenThrow(IOException.class);

            CommunicationException exception = catchThrowableOfType(
                () -> httpHelper.put(providedUrl),
                CommunicationException.class
            );

            assertEquals(providedUrl, exception.getUrl());
        }

        @Test
        public void noStatusCodeShouldBePreserved() throws IOException {
            when(httpClient.execute(any(HttpUriRequest.class), any(HttpClientResponseHandler.class)))
                .thenThrow(IOException.class);

            CommunicationException exception = catchThrowableOfType(
                () -> httpHelper.put(ANY_URL),
                CommunicationException.class
            );

            assertEquals(NO_STATUS, exception.getHttpStatusCode());
        }

        @Test
        public void rootCauseShouldBePreserved() throws IOException {
            IOException ioException = new IOException();
            when(httpClient.execute(any(HttpUriRequest.class), any(HttpClientResponseHandler.class)))
                .thenThrow(ioException);

            CommunicationException exception = catchThrowableOfType(
                () -> httpHelper.put(ANY_URL),
                CommunicationException.class
            );

            assertThat(exception).hasRootCause(ioException);
        }

        @Test
        public void messageShouldExplainTheProblem() throws IOException {
            IOException ioException = new IOException();
            when(httpClient.execute(any(HttpUriRequest.class), any(HttpClientResponseHandler.class)))
                .thenThrow(ioException);

            CommunicationException exception = catchThrowableOfType(
                () -> httpHelper.put(ANY_URL),
                CommunicationException.class
            );

            assertThat(exception.getMessage()).contains("Problems performing PUT request");
        }
    }

    @Nested
    public class WhenIoExceptionHappensDuringDelete {

        private final SdkInternalConfiguration config = mock(SdkInternalConfiguration.class);
        private final CloseableHttpClient httpClient = mock(CloseableHttpClient.class);
        private final MessageAndActionExtractor messageExtractor = mock(MessageAndActionExtractor.class);
        private final UserAgentProvider userAgent = mock(UserAgentProvider.class);
        private HttpHelper httpHelper = new HttpHelper(config, httpClient, messageExtractor, userAgent);

        @Test
        public void urlShouldBePreserved() throws IOException {
            String providedUrl = "https://providedUrl.com";
            when(httpClient.execute(any(HttpUriRequest.class), any(HttpClientResponseHandler.class)))
                .thenThrow(IOException.class);

            CommunicationException exception = catchThrowableOfType(
                () -> httpHelper.delete(providedUrl),
                CommunicationException.class
            );

            assertEquals(providedUrl, exception.getUrl());
        }

        @Test
        public void noStatusCodeShouldBePreserved() throws IOException {
            when(httpClient.execute(any(HttpUriRequest.class), any(HttpClientResponseHandler.class)))
                .thenThrow(IOException.class);

            CommunicationException exception = catchThrowableOfType(
                () -> httpHelper.delete(ANY_URL),
                CommunicationException.class
            );

            assertEquals(NO_STATUS, exception.getHttpStatusCode());
        }

        @Test
        public void rootCauseShouldBePreserved() throws IOException {
            IOException ioException = new IOException();
            when(httpClient.execute(any(HttpUriRequest.class), any(HttpClientResponseHandler.class)))
                .thenThrow(ioException);

            CommunicationException exception = catchThrowableOfType(
                () -> httpHelper.delete(ANY_URL),
                CommunicationException.class
            );

            assertThat(exception).hasRootCause(ioException);
        }

        @Test
        public void messageShouldExplainTheProblem() throws IOException {
            IOException ioException = new IOException();
            when(httpClient.execute(any(HttpUriRequest.class), any(HttpClientResponseHandler.class)))
                .thenThrow(ioException);

            CommunicationException exception = catchThrowableOfType(
                () -> httpHelper.delete(ANY_URL),
                CommunicationException.class
            );

            assertThat(exception.getMessage()).contains("Problems executing DELETE request");
        }
    }
}
