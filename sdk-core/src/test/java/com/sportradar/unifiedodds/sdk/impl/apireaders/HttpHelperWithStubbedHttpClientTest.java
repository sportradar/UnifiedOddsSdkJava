/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */

package com.sportradar.unifiedodds.sdk.impl.apireaders;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.sportradar.unifiedodds.sdk.SdkInternalConfiguration;
import com.sportradar.unifiedodds.sdk.exceptions.internal.CommunicationException;
import com.sportradar.unifiedodds.sdk.impl.UserAgentProvider;
import com.sportradar.unifiedodds.sdk.shared.CloseableHttpClientFixture;
import com.sportradar.unifiedodds.sdk.shared.SportsApiXmlResponseProvider;
import java.io.IOException;
import lombok.val;
import org.apache.hc.core5.http.ProtocolException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

public abstract class HttpHelperWithStubbedHttpClientTest {

    private static final String ANY_MESSAGE = "anyMessage";
    private static final int HTTP_OK = 200;
    private static final int HTTP_ACCEPTED = 202;
    private final SdkInternalConfiguration config = mock(SdkInternalConfiguration.class);
    private final CloseableHttpClientFixture httpClient = new CloseableHttpClientFixture();
    private final MessageAndActionExtractor messageExtractor = mock(MessageAndActionExtractor.class);
    private final UserAgentProvider userAgentProvider = mock(UserAgentProvider.class);
    private final InvokeHelpersHttpMethod httpMethod = httpMethodInvocationOn(
        new HttpHelper(config, httpClient, messageExtractor, userAgentProvider)
    );
    private final SportsApiXmlResponseProvider xmlResponses = new SportsApiXmlResponseProvider();
    private final String anyPath = "/path/resource";
    private final int successfulStatusCode = 200;
    private final int anyHttpCode = 200;
    private final int httpClientError = 404;

    abstract InvokeHelpersHttpMethod httpMethodInvocationOn(HttpHelper httpHelper);

    @Test
    public void shouldNotInstantiateWithNullArguments() {
        assertThatThrownBy(() -> new HttpHelper(null, httpClient, messageExtractor, userAgentProvider))
            .isInstanceOf(NullPointerException.class);
        assertThatThrownBy(() -> new HttpHelper(config, null, messageExtractor, userAgentProvider))
            .isInstanceOf(NullPointerException.class);
        assertThatThrownBy(() -> new HttpHelper(config, httpClient, null, userAgentProvider))
            .isInstanceOf(NullPointerException.class);
        assertThatThrownBy(() -> new HttpHelper(config, httpClient, messageExtractor, null))
            .isInstanceOf(NullPointerException.class)
            .hasMessageContaining("userAgent");
    }

    @Test
    public void submitsUserAgent() throws CommunicationException, IOException, ProtocolException {
        val userAgent = "specified user agent";
        when(userAgentProvider.asHeaderValue()).thenReturn(userAgent);
        httpClient.setupHttpResponseAndHttpEntity(anyHttpCode, xmlResponses.any());
        when(messageExtractor.parse(any())).thenReturn("any");

        httpMethod.invoke(anyPath);

        httpClient.verifyUserAgentWas(userAgent);
    }

    @Test
    public void httpHelperShouldReturnResponseDataWithStatusCodeAndEmptyMessageIfHttpEntityIsNull()
        throws CommunicationException {
        httpClient.setupHttpResponseWithEmptyEntity(successfulStatusCode);

        HttpHelper.ResponseData actualResponseData = httpMethod.invoke(anyPath);

        assertThat(actualResponseData.getStatusCode()).isEqualTo(successfulStatusCode);
    }

    @Test
    public void indicatesNoResponseIfHttpEntityIsNull() throws CommunicationException {
        httpClient.setupHttpResponseWithEmptyEntity(successfulStatusCode);
        String emptyResponseString = "EMPTY_RESPONSE";

        HttpHelper.ResponseData actualResponseData = httpMethod.invoke(anyPath);

        assertThat(actualResponseData.getMessage()).isEqualTo(emptyResponseString);
    }

    @Test
    public void extractsMessageFromResponse() throws CommunicationException, IOException {
        val message = "some message";
        httpClient.setupHttpResponseAndHttpEntity(
            successfulStatusCode,
            xmlResponses.createResponseContainingSuccessMessage()
        );
        when(messageExtractor.parse(any())).thenReturn(message);

        HttpHelper.ResponseData actualResponseData = httpMethod.invoke(anyPath);

        assertThat(actualResponseData.getMessage()).isEqualTo(message);
    }

    @Test
    public void preservesStatusCode() throws CommunicationException, IOException {
        httpClient.setupHttpResponseAndHttpEntity(
            successfulStatusCode,
            xmlResponses.createResponseContainingSuccessMessage()
        );
        when(messageExtractor.parse(any())).thenReturn(ANY_MESSAGE);

        HttpHelper.ResponseData actualResponseData = httpMethod.invoke(anyPath);

        assertThat(actualResponseData.getStatusCode()).isEqualTo(successfulStatusCode);
    }

    @Test
    public void parsesContentOnClientErrorToo() throws CommunicationException, IOException {
        val message = "some message";
        httpClient.setupHttpResponseAndHttpEntity(
            httpClientError,
            xmlResponses.createResponseContainingSuccessMessage()
        );
        when(messageExtractor.parse(any())).thenReturn(message);

        HttpHelper.ResponseData actualResponseData = httpMethod.invoke(anyPath);

        assertThat(actualResponseData.getMessage()).isEqualTo(message);
    }

    @Test
    public void preserveHttpCodeOnClientErrors() throws IOException, CommunicationException {
        httpClient.setupHttpResponseAndHttpEntity(httpClientError, xmlResponses.createNotFoundResponse());
        String message = "failure message";
        when(messageExtractor.parse(any())).thenReturn(message);

        HttpHelper.ResponseData actualResponseData = httpMethod.invoke(anyPath);

        assertThat(actualResponseData.getStatusCode()).isEqualTo(httpClientError);
    }

    private static Object[] successfulHttpCodes() {
        return new Object[][] { { HTTP_OK }, { HTTP_ACCEPTED } };
    }

    @ParameterizedTest
    @MethodSource("successfulHttpCodes")
    public void shouldIndicateWhetherHttpCodeWasSuccessful(int successfulHttpCode)
        throws CommunicationException, IOException {
        httpClient.setupHttpResponseAndHttpEntity(
            successfulHttpCode,
            xmlResponses.createResponseContainingSuccessMessage()
        );
        when(messageExtractor.parse(any())).thenReturn(ANY_MESSAGE);

        HttpHelper.ResponseData actualResponseData = httpMethod.invoke(anyPath);

        assertThat(actualResponseData.isSuccessful()).isTrue();
    }

    static interface InvokeHelpersHttpMethod {
        public HttpHelper.ResponseData invoke(String path) throws CommunicationException;
    }
}
