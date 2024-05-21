/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */
package com.sportradar.unifiedodds.sdk.impl;

import com.google.common.base.Strings;
import com.google.inject.Inject;
import com.google.inject.name.Named;
import com.sportradar.unifiedodds.sdk.impl.apireaders.HttpHelper;
import com.sportradar.unifiedodds.sdk.impl.apireaders.MessageAndActionExtractor;
import com.sportradar.unifiedodds.sdk.impl.http.ApiResponseHandlingException;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import org.apache.hc.core5.http.ClassicHttpResponse;
import org.apache.hc.core5.http.HttpStatus;
import org.apache.hc.core5.http.ParseException;
import org.apache.hc.core5.http.io.entity.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class HttpResponseHandler {

    private static final Logger LOGGER = LoggerFactory.getLogger(HttpResponseHandler.class);

    HttpData extractHttpDataFromHttpResponse(ClassicHttpResponse httpResponse, String path) {
        try {
            return parseResponse(httpResponse, path);
        } catch (IOException | ParseException e) {
            throw new ApiResponseHandlingException(path, httpResponse.getCode(), e);
        }
    }

    private HttpData parseResponse(ClassicHttpResponse httpResponse, String path)
        throws IOException, ParseException {
        validateStatusCode(httpResponse, path);
        String responseString = parseBody(httpResponse, path, httpResponse.getCode());
        return new HttpData(responseString, httpResponse.getHeaders());
    }

    private String parseBody(ClassicHttpResponse httpResponse, String path, int statusCode)
        throws IOException, ParseException {
        String responseString = EntityUtils.toString(httpResponse.getEntity(), StandardCharsets.UTF_8);
        if (Strings.isNullOrEmpty(responseString)) {
            String emptyErrorMessage = "no message";
            throw new ApiResponseHandlingException(
                formatUncheckedExceptionMessage(emptyErrorMessage, statusCode),
                path,
                statusCode
            );
        }
        return responseString;
    }

    private void validateStatusCode(ClassicHttpResponse httpResponse, String path) throws IOException {
        int statusCode = httpResponse.getCode();

        // the whoami endpoint is a special case since we are interested in the response even with forbidden code
        boolean isWhoAmI = path.endsWith("whoami.xml");
        if (
            !responseHasSuccessfulStatusCode(statusCode) &&
            !isWhoAmIWithForbiddenStatusCode(statusCode, isWhoAmI)
        ) {
            String errorMessage = logAndReturnErrorMessage(httpResponse, path);
            throw new ApiResponseHandlingException(
                formatUncheckedExceptionMessage(errorMessage, statusCode),
                path,
                statusCode
            );
        }
    }

    private String logAndReturnErrorMessage(ClassicHttpResponse httpResponse, String path)
        throws IOException {
        String errorMessage = new MessageAndActionExtractor().parse(httpResponse.getEntity().getContent());
        LOGGER.warn(
            "Bad API response: " +
            httpResponse.getVersion() +
            " " +
            httpResponse.getCode() +
            " " +
            httpResponse.getReasonPhrase() +
            ", message: '" +
            errorMessage +
            "' " +
            path
        );
        errorMessage = Strings.isNullOrEmpty(errorMessage) ? "no message" : errorMessage;
        return errorMessage;
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
}
