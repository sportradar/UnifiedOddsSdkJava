/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */
package com.sportradar.unifiedodds.sdk.internal.impl;

import com.google.common.base.Strings;
import com.sportradar.unifiedodds.sdk.internal.impl.apireaders.MessageAndActionExtractor;
import com.sportradar.unifiedodds.sdk.internal.impl.http.ApiResponseHandlingException;
import com.sportradar.utils.jacoco.ExcludeFromJacocoGeneratedReportUntestableCheckedException;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import org.apache.hc.client5.http.async.methods.SimpleHttpResponse;
import org.apache.hc.core5.http.ClassicHttpResponse;
import org.apache.hc.core5.http.HttpResponse;
import org.apache.hc.core5.http.HttpStatus;
import org.apache.hc.core5.http.ParseException;
import org.apache.hc.core5.http.io.entity.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class HttpResponseHandler {

    private static final Logger LOGGER = LoggerFactory.getLogger(HttpResponseHandler.class);
    private static final String EMPTY_ERROR_MESSAGE = "no message";

    HttpData extractHttpDataFromHttpResponse(ClassicHttpResponse httpResponse, String path) {
        try {
            return parseResponse(httpResponse, path);
        } catch (IOException | ParseException e) {
            throw new ApiResponseHandlingException(path, httpResponse.getCode(), e);
        }
    }

    HttpData extractHttpDataFromHttpResponse(SimpleHttpResponse httpResponse, String path) {
        return parseResponse(httpResponse, path);
    }

    private HttpData parseResponse(SimpleHttpResponse httpResponse, String path) {
        validateStatusCode(httpResponse, path, () -> getErrorMessage(httpResponse, path));
        String responseString = parseBody(httpResponse, path, httpResponse.getCode());
        return new HttpData(responseString, httpResponse.getHeaders());
    }

    private HttpData parseResponse(ClassicHttpResponse httpResponse, String path)
        throws IOException, ParseException {
        validateStatusCode(httpResponse, path, () -> getErrorMessage(httpResponse, path));
        String responseString = parseBody(httpResponse, path, httpResponse.getCode());
        return new HttpData(responseString, httpResponse.getHeaders());
    }

    private String parseBody(ClassicHttpResponse httpResponse, String path, int statusCode)
        throws IOException, ParseException {
        String responseString = EntityUtils.toString(httpResponse.getEntity(), StandardCharsets.UTF_8);
        if (Strings.isNullOrEmpty(responseString)) {
            throw new ApiResponseHandlingException(
                formatUncheckedExceptionMessage(EMPTY_ERROR_MESSAGE, statusCode),
                path,
                statusCode
            );
        }
        return responseString;
    }

    private String parseBody(SimpleHttpResponse httpResponse, String path, int statusCode) {
        String responseString = new String(httpResponse.getBodyBytes(), StandardCharsets.UTF_8);
        if (Strings.isNullOrEmpty(responseString)) {
            throw new ApiResponseHandlingException(
                formatUncheckedExceptionMessage(EMPTY_ERROR_MESSAGE, statusCode),
                path,
                statusCode
            );
        }
        return responseString;
    }

    private void validateStatusCode(
        HttpResponse httpResponse,
        String path,
        ErrorMessageSupplier errorMessageSupplier
    ) {
        int statusCode = httpResponse.getCode();

        // the whoami endpoint is a special case since we are interested in the response even with forbidden code
        boolean isWhoAmI = path.endsWith("whoami.xml");
        if (
            !responseHasSuccessfulStatusCode(statusCode) &&
            !isWhoAmIWithForbiddenStatusCode(statusCode, isWhoAmI)
        ) {
            String errorFromResponse = errorMessageSupplier.get();
            String errorMessage = Strings.isNullOrEmpty(errorFromResponse)
                ? EMPTY_ERROR_MESSAGE
                : errorFromResponse;
            logErrorMessage(httpResponse, errorMessage, path);
            throw new ApiResponseHandlingException(
                formatUncheckedExceptionMessage(errorMessage, statusCode),
                path,
                statusCode
            );
        }
    }

    private void logErrorMessage(HttpResponse httpResponse, String errorMessage, String path) {
        LOGGER.warn(
            "Bad API response: {} {} {}, message: '{}' {}",
            httpResponse.getVersion(),
            httpResponse.getCode(),
            httpResponse.getReasonPhrase(),
            errorMessage,
            path
        );
    }

    private String getErrorMessage(ClassicHttpResponse httpResponse, String path) {
        try {
            return new MessageAndActionExtractor().parse(httpResponse.getEntity().getContent());
        } catch (IOException e) {
            throw new ApiResponseHandlingException(path, httpResponse.getCode(), e);
        }
    }

    @ExcludeFromJacocoGeneratedReportUntestableCheckedException
    private String getErrorMessage(SimpleHttpResponse httpResponse, String path) {
        try (
            ByteArrayInputStream bodyInputStream = new ByteArrayInputStream(
                httpResponse.getBody().getBodyBytes()
            )
        ) {
            return new MessageAndActionExtractor().parse(bodyInputStream);
        } catch (IOException e) {
            throw new ApiResponseHandlingException(path, httpResponse.getCode(), e);
        }
    }

    private String formatUncheckedExceptionMessage(String errorMessage, int responseStatusCode) {
        return "Invalid server response w/status code: " + responseStatusCode + ", message: " + errorMessage;
    }

    private boolean responseHasSuccessfulStatusCode(int statusCode) {
        return statusCode == HttpStatus.SC_OK || statusCode == HttpStatus.SC_ACCEPTED;
    }

    private boolean isWhoAmIWithForbiddenStatusCode(int statusCode, boolean isWhoAmI) {
        return isWhoAmI && statusCode == HttpStatus.SC_FORBIDDEN;
    }

    private interface ErrorMessageSupplier {
        String get();
    }
}
