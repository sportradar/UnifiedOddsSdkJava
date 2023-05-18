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

import com.sportradar.unifiedodds.sdk.SDKInternalConfiguration;
import com.sportradar.unifiedodds.sdk.exceptions.internal.CommunicationException;
import com.sportradar.unifiedodds.sdk.impl.Deserializer;
import com.sportradar.unifiedodds.sdk.impl.apireaders.HttpHelper.ResponseData;
import java.io.IOException;
import junitparams.JUnitParamsRunner;
import junitparams.Parameters;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.impl.client.CloseableHttpClient;
import org.junit.Test;
import org.junit.experimental.runners.Enclosed;
import org.junit.runner.RunWith;

@RunWith(Enclosed.class)
public class HttpHelperTest {

    private static final int NO_STATUS = -1;
    private static final String ANY_URL = "http://anyUrl.com";

    public static class WhenIoExceptionHappensDuringPost {

        private final SDKInternalConfiguration config = mock(SDKInternalConfiguration.class);
        private final CloseableHttpClient httpClient = mock(CloseableHttpClient.class);
        private final Deserializer deserialiser = mock(Deserializer.class);
        private HttpHelper httpHelper = new HttpHelper(config, httpClient, deserialiser);

        @Test
        public void urlShouldBePreservedWhenIoExceptionHappensDuringPost() throws IOException {
            String providedUrl = "https://providedUrl.com";
            when(httpClient.execute(any(HttpUriRequest.class))).thenThrow(IOException.class);

            CommunicationException exception = catchThrowableOfType(
                () -> httpHelper.post(providedUrl),
                CommunicationException.class
            );

            assertEquals(providedUrl, exception.getUrl());
        }

        @Test
        public void noStatusCodeShouldBePreservedWhenIoExceptionHappensDuringPost() throws IOException {
            when(httpClient.execute(any(HttpUriRequest.class))).thenThrow(IOException.class);

            CommunicationException exception = catchThrowableOfType(
                () -> httpHelper.post(ANY_URL),
                CommunicationException.class
            );

            assertEquals(NO_STATUS, exception.getHttpStatusCode());
        }

        @Test
        public void rootCauseShouldBePreservedWhenIoExceptionHappensDuringPost() throws IOException {
            IOException ioException = new IOException();
            when(httpClient.execute(any(HttpUriRequest.class))).thenThrow(ioException);

            CommunicationException exception = catchThrowableOfType(
                () -> httpHelper.post(ANY_URL),
                CommunicationException.class
            );

            assertThat(exception).hasRootCause(ioException);
        }

        @Test
        public void messageShouldExplainThatIoExceptionHappensDuringHttpCall() throws IOException {
            IOException ioException = new IOException();
            when(httpClient.execute(any(HttpUriRequest.class))).thenThrow(ioException);

            CommunicationException exception = catchThrowableOfType(
                () -> httpHelper.post(ANY_URL),
                CommunicationException.class
            );

            assertThat(exception.getMessage()).contains("Problems executing POST request");
        }
    }

    public static class WhenIoExceptionHappensDuringPut {

        private final SDKInternalConfiguration config = mock(SDKInternalConfiguration.class);
        private final CloseableHttpClient httpClient = mock(CloseableHttpClient.class);
        private final Deserializer deserialiser = mock(Deserializer.class);
        private HttpHelper httpHelper = new HttpHelper(config, httpClient, deserialiser);

        @Test
        public void urlShouldBePreserved() throws IOException {
            String providedUrl = "https://providedUrl.com";
            when(httpClient.execute(any(HttpUriRequest.class))).thenThrow(IOException.class);

            CommunicationException exception = catchThrowableOfType(
                () -> httpHelper.put(providedUrl),
                CommunicationException.class
            );

            assertEquals(providedUrl, exception.getUrl());
        }

        @Test
        public void noStatusCodeShouldBePreserved() throws IOException {
            when(httpClient.execute(any(HttpUriRequest.class))).thenThrow(IOException.class);

            CommunicationException exception = catchThrowableOfType(
                () -> httpHelper.put(ANY_URL),
                CommunicationException.class
            );

            assertEquals(NO_STATUS, exception.getHttpStatusCode());
        }

        @Test
        public void rootCauseShouldBePreserved() throws IOException {
            IOException ioException = new IOException();
            when(httpClient.execute(any(HttpUriRequest.class))).thenThrow(ioException);

            CommunicationException exception = catchThrowableOfType(
                () -> httpHelper.put(ANY_URL),
                CommunicationException.class
            );

            assertThat(exception).hasRootCause(ioException);
        }

        @Test
        public void messageShouldExplainTheProblem() throws IOException {
            IOException ioException = new IOException();
            when(httpClient.execute(any(HttpUriRequest.class))).thenThrow(ioException);

            CommunicationException exception = catchThrowableOfType(
                () -> httpHelper.put(ANY_URL),
                CommunicationException.class
            );

            assertThat(exception.getMessage()).contains("Problems performing PUT request");
        }
    }

    public static class WhenIoExceptionHappensDuringDelete {

        private final SDKInternalConfiguration config = mock(SDKInternalConfiguration.class);
        private final CloseableHttpClient httpClient = mock(CloseableHttpClient.class);
        private final Deserializer deserialiser = mock(Deserializer.class);
        private HttpHelper httpHelper = new HttpHelper(config, httpClient, deserialiser);

        @Test
        public void urlShouldBePreserved() throws IOException {
            String providedUrl = "https://providedUrl.com";
            when(httpClient.execute(any(HttpUriRequest.class))).thenThrow(IOException.class);

            CommunicationException exception = catchThrowableOfType(
                () -> httpHelper.delete(providedUrl),
                CommunicationException.class
            );

            assertEquals(providedUrl, exception.getUrl());
        }

        @Test
        public void noStatusCodeShouldBePreserved() throws IOException {
            when(httpClient.execute(any(HttpUriRequest.class))).thenThrow(IOException.class);

            CommunicationException exception = catchThrowableOfType(
                () -> httpHelper.delete(ANY_URL),
                CommunicationException.class
            );

            assertEquals(NO_STATUS, exception.getHttpStatusCode());
        }

        @Test
        public void rootCauseShouldBePreserved() throws IOException {
            IOException ioException = new IOException();
            when(httpClient.execute(any(HttpUriRequest.class))).thenThrow(ioException);

            CommunicationException exception = catchThrowableOfType(
                () -> httpHelper.delete(ANY_URL),
                CommunicationException.class
            );

            assertThat(exception).hasRootCause(ioException);
        }

        @Test
        public void messageShouldExplainTheProblem() throws IOException {
            IOException ioException = new IOException();
            when(httpClient.execute(any(HttpUriRequest.class))).thenThrow(ioException);

            CommunicationException exception = catchThrowableOfType(
                () -> httpHelper.delete(ANY_URL),
                CommunicationException.class
            );

            assertThat(exception.getMessage()).contains("Problems executing DELETE request");
        }
    }
}
