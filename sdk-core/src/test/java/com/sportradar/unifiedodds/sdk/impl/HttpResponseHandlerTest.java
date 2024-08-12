/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */
package com.sportradar.unifiedodds.sdk.impl;

import static org.assertj.core.api.Assertions.*;
import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.sportradar.uf.sportsapi.datamodel.Response;
import com.sportradar.unifiedodds.sdk.exceptions.internal.CommunicationException;
import com.sportradar.unifiedodds.sdk.exceptions.internal.DeserializationException;
import com.sportradar.unifiedodds.sdk.impl.http.ApiResponseHandlingException;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import lombok.val;
import org.apache.hc.client5.http.impl.classic.CloseableHttpResponse;
import org.apache.hc.core5.http.Header;
import org.apache.hc.core5.http.HttpEntity;
import org.apache.hc.core5.http.ParseException;
import org.junit.jupiter.api.Test;

public class HttpResponseHandlerTest {

    private static final String ANY_RESPONSE_BODY = "any";
    private static final String EMPTY_RESPONSE_BODY = "";
    private final HttpResponseHandler responseHandler = new HttpResponseHandler();

    private final CloseableHttpResponse httpResponse = mock(CloseableHttpResponse.class);
    private final String path = "/any/path";
    private final int successfulResponseCode = 200;
    private final int failedResponseCode = 404;
    private final String anySuccessfulResponseMessage = "successful response";
    private final String whoAmIPath = "/whoami.xml";

    @Test
    public void shouldReturnHttpDataWhenRequestIsSuccessful()
        throws IOException, ParseException, CommunicationException {
        setupHttpResponseAndHttpEntity(successfulResponseCode, anySuccessfulResponseMessage);

        HttpData actualHttpData = responseHandler.extractHttpDataFromHttpResponse(httpResponse, path);

        HttpData expectedHttpData = createWithMessageAndNoHeaders(anySuccessfulResponseMessage);
        assertEquals(expectedHttpData.getResponse(), actualHttpData.getResponse());
    }

    @Test
    public void shouldReturnHttpWhenRequestHasAcceptedHttpStatus()
        throws IOException, ParseException, CommunicationException {
        final int acceptedResponseCode = 202;
        setupHttpResponseAndHttpEntity(acceptedResponseCode, anySuccessfulResponseMessage);

        HttpData actualHttpData = responseHandler.extractHttpDataFromHttpResponse(httpResponse, path);

        HttpData expectedHttpData = createWithMessageAndNoHeaders(anySuccessfulResponseMessage);
        assertEquals(expectedHttpData.getResponse(), actualHttpData.getResponse());
    }

    @Test
    public void shouldReturnHttpDataForFailedWhoAmIRequestWithForbiddenStatusCode() throws Exception {
        final int forbiddenStatus = 403;
        String forbiddenResponseString = "forbidden";
        setupHttpResponseAndHttpEntity(forbiddenStatus, forbiddenResponseString);

        HttpData actualHttpData = responseHandler.extractHttpDataFromHttpResponse(httpResponse, whoAmIPath);

        HttpData expectedHttpData = createWithMessageAndNoHeaders(forbiddenResponseString);
        assertEquals(expectedHttpData.getResponse(), actualHttpData.getResponse());
    }

    @Test
    public void shouldThrowExceptionWhenWhoAmIRequestFailsAndNotForbiddenStatus() throws Exception {
        String anyFailedResponseMessage = "failed response";
        setupHttpResponseAndHttpEntity(
            failedResponseCode,
            "<response><message>" + anyFailedResponseMessage + "</message></response>"
        );

        String expectedExceptionCauseMessage = createExpectedExceptionMessage(
            failedResponseCode,
            anyFailedResponseMessage
        );

        assertThatThrownBy(() -> responseHandler.extractHttpDataFromHttpResponse(httpResponse, whoAmIPath))
            .hasMessage(expectedExceptionCauseMessage);
    }

    @Test
    public void failingDueToEmptyResponseContainsExplanatoryMessage() throws IOException {
        String noMessageErrorFromHttpResponseHandler = "no message";
        setupHttpResponseAndHttpEntity(successfulResponseCode, EMPTY_RESPONSE_BODY);

        String expectedExceptionCauseMessage = createExpectedExceptionMessage(
            successfulResponseCode,
            noMessageErrorFromHttpResponseHandler
        );

        assertThatThrownBy(() -> responseHandler.extractHttpDataFromHttpResponse(httpResponse, path))
            .hasMessage(expectedExceptionCauseMessage);
    }

    @Test
    public void failingDueToEmptyResponsePreservesHttpCode() throws IOException {
        setupHttpResponseAndHttpEntity(successfulResponseCode, EMPTY_RESPONSE_BODY);

        val exception = catchThrowableOfType(
            () -> responseHandler.extractHttpDataFromHttpResponse(httpResponse, path),
            ApiResponseHandlingException.class
        );
        assertThat(exception.getHttpStatusCode()).isEqualTo(successfulResponseCode);
    }

    @Test
    public void failingDueToEmptyResponsePreservesUrl() throws IOException {
        setupHttpResponseAndHttpEntity(successfulResponseCode, EMPTY_RESPONSE_BODY);

        val exception = catchThrowableOfType(
            () -> responseHandler.extractHttpDataFromHttpResponse(httpResponse, path),
            ApiResponseHandlingException.class
        );
        assertThat(exception.getUrl()).isEqualTo(path);
    }

    @Test
    public void shouldThrowExceptionForFailedResponse() throws Exception {
        String errorMessage = "failed response";
        setupHttpResponseAndHttpEntity(
            failedResponseCode,
            "<response><message>" + errorMessage + "</message></response>"
        );

        String expectedExceptionCauseMessage = createExpectedExceptionMessage(
            failedResponseCode,
            errorMessage
        );

        assertThatThrownBy(() -> responseHandler.extractHttpDataFromHttpResponse(httpResponse, path))
            .hasMessage(expectedExceptionCauseMessage);
    }

    @Test
    public void failingDueToUnexpectedHttpCodeCarriesExplanatoryMessage() throws Exception {
        String emptyResponseString = "";
        String noMessageErrorFromMessageAndActionExtractor = "No specific message";
        setupHttpResponseAndHttpEntity(failedResponseCode, emptyResponseString);

        String expectedExceptionCauseMessage = createExpectedExceptionMessage(
            failedResponseCode,
            noMessageErrorFromMessageAndActionExtractor
        );

        assertThatThrownBy(() -> responseHandler.extractHttpDataFromHttpResponse(httpResponse, path))
            .hasMessage(expectedExceptionCauseMessage);
    }

    @Test
    public void failingDueToUnexpectedHttpCodePreservesStatusCode() throws Exception {
        setupHttpResponseAndHttpEntity(failedResponseCode, ANY_RESPONSE_BODY);

        val exception = catchThrowableOfType(
            () -> responseHandler.extractHttpDataFromHttpResponse(httpResponse, path),
            ApiResponseHandlingException.class
        );

        assertThat(exception.getHttpStatusCode()).isEqualTo(failedResponseCode);
    }

    @Test
    public void failingDueToUnexpectedHttpCodePreservesUrl() throws Exception {
        setupHttpResponseAndHttpEntity(failedResponseCode, ANY_RESPONSE_BODY);

        val exception = catchThrowableOfType(
            () -> responseHandler.extractHttpDataFromHttpResponse(httpResponse, path),
            ApiResponseHandlingException.class
        );

        assertThat(exception.getUrl()).isEqualTo(path);
    }

    @Test
    public void preservesStatusCodeOnFailureToRetrieveResponseContentOfSuccessfulResponse()
        throws IOException {
        setupHttpResponseAndHttpEntity(successfulResponseCode, anySuccessfulResponseMessage);
        responseThrowingOnGettingContent(IOException.class);

        val exception = catchThrowableOfType(
            () -> responseHandler.extractHttpDataFromHttpResponse(httpResponse, path),
            ApiResponseHandlingException.class
        );

        assertThat(exception.getHttpStatusCode()).isEqualTo(successfulResponseCode);
    }

    @Test
    public void preservesOriginalExceptionOnFailureToRetrieveResponseContentOfSuccessfulResponse()
        throws IOException {
        setupHttpResponseAndHttpEntity(successfulResponseCode, anySuccessfulResponseMessage);
        responseThrowingOnGettingContent(IOException.class);

        val exception = catchThrowableOfType(
            () -> responseHandler.extractHttpDataFromHttpResponse(httpResponse, path),
            ApiResponseHandlingException.class
        );

        assertThat(exception.getCause()).isInstanceOf(IOException.class);
    }

    @Test
    public void preservesUrlOnFailureToRetrieveResponseContentOfSuccessfulResponse() throws IOException {
        setupHttpResponseAndHttpEntity(successfulResponseCode, anySuccessfulResponseMessage);
        responseThrowingOnGettingContent(IOException.class);

        val exception = catchThrowableOfType(
            () -> responseHandler.extractHttpDataFromHttpResponse(httpResponse, path),
            ApiResponseHandlingException.class
        );

        assertThat(exception.getUrl()).isEqualTo(path);
    }

    @Test
    public void preservesStatusCodeOnFailureToRetrieveResponseContentOfUnsuccessfulResponse()
        throws IOException {
        setupHttpResponseAndHttpEntity(failedResponseCode, anySuccessfulResponseMessage);
        responseThrowingOnGettingContent(IOException.class);

        val exception = catchThrowableOfType(
            () -> responseHandler.extractHttpDataFromHttpResponse(httpResponse, path),
            ApiResponseHandlingException.class
        );

        assertThat(exception.getHttpStatusCode()).isEqualTo(failedResponseCode);
    }

    @Test
    public void preservesOriginalExceptionOnFailureToRetrieveResponseContentOfUnsuccessfulResponse()
        throws IOException {
        setupHttpResponseAndHttpEntity(failedResponseCode, anySuccessfulResponseMessage);
        responseThrowingOnGettingContent(IOException.class);

        val exception = catchThrowableOfType(
            () -> responseHandler.extractHttpDataFromHttpResponse(httpResponse, path),
            ApiResponseHandlingException.class
        );

        assertThat(exception.getCause()).isInstanceOf(IOException.class);
    }

    @Test
    public void preservesUrlOnFailureToRetrieveResponseContentOfUnsuccessfulResponse() throws IOException {
        setupHttpResponseAndHttpEntity(failedResponseCode, anySuccessfulResponseMessage);
        responseThrowingOnGettingContent(IOException.class);

        val exception = catchThrowableOfType(
            () -> responseHandler.extractHttpDataFromHttpResponse(httpResponse, path),
            ApiResponseHandlingException.class
        );

        assertThat(exception.getUrl()).isEqualTo(path);
    }

    private void responseThrowingOnGettingContent(Class<IOException> aClass) throws IOException {
        HttpEntity entity = mock(HttpEntity.class);
        when(entity.getContent()).thenThrow(aClass);
        when(httpResponse.getEntity()).thenReturn(entity);
    }

    private void setupHttpResponseAndHttpEntity(int statusCode, String anyXmlResponse) throws IOException {
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

    private String createExpectedExceptionMessage(int statusCode, String errorMessage) {
        return "Invalid server response w/status code: " + statusCode + ", message: " + errorMessage;
    }

    private HttpData createWithMessageAndNoHeaders(String message) {
        Header[] headerArray = new Header[0];
        return new HttpData(message, headerArray);
    }

    private Response createResponseWithMessage(String message) {
        Response response = new Response();
        response.setMessage(message);
        return response;
    }
}
