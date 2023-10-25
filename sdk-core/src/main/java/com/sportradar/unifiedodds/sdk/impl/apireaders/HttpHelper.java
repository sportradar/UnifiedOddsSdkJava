/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */
package com.sportradar.unifiedodds.sdk.impl.apireaders;

import com.google.common.base.Preconditions;
import com.google.common.base.Stopwatch;
import com.google.inject.Inject;
import com.sportradar.unifiedodds.sdk.LoggerDefinitions;
import com.sportradar.unifiedodds.sdk.SdkInternalConfiguration;
import com.sportradar.unifiedodds.sdk.exceptions.internal.CommunicationException;
import com.sportradar.unifiedodds.sdk.impl.UserAgentProvider;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import org.apache.hc.client5.http.classic.methods.HttpDelete;
import org.apache.hc.client5.http.classic.methods.HttpPost;
import org.apache.hc.client5.http.classic.methods.HttpPut;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.core5.http.ClassicHttpRequest;
import org.apache.hc.core5.http.ClassicHttpResponse;
import org.apache.hc.core5.http.HttpStatus;
import org.apache.hc.core5.http.ParseException;
import org.apache.hc.core5.http.io.entity.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@SuppressWarnings("ClassFanOutComplexity")
public class HttpHelper {

    private static final Logger LOGGER = LoggerFactory.getLogger(HttpHelper.class);
    private static final Logger TRAFFIC_LOGGER = LoggerFactory.getLogger(
        LoggerDefinitions.UfSdkRestTrafficLog.class
    );
    private static final String EMPTY_RESPONSE = "EMPTY_RESPONSE";
    private final SdkInternalConfiguration config;
    private final CloseableHttpClient httpClient;
    private final MessageAndActionExtractor messageExtractor;
    private final UserAgentProvider userAgent;

    @Inject
    public HttpHelper(
        SdkInternalConfiguration config,
        CloseableHttpClient httpClient,
        MessageAndActionExtractor messageExtractor,
        UserAgentProvider userAgent
    ) {
        Preconditions.checkNotNull(config);
        Preconditions.checkNotNull(httpClient);
        Preconditions.checkNotNull(messageExtractor);
        Preconditions.checkNotNull(userAgent, "userAgent");

        this.config = config;
        this.httpClient = httpClient;
        this.messageExtractor = messageExtractor;
        this.userAgent = userAgent;
    }

    public ResponseData post(String path) throws CommunicationException {
        LOGGER.info("POST request: " + path);
        HttpPost httpPost = new HttpPost(path);
        try {
            return executeRequest(httpPost, path);
        } catch (CommunicationException e) {
            throw new CommunicationException(
                "Problems executing POST request",
                path,
                e.getHttpStatusCode(),
                e
            );
        }
    }

    public ResponseData put(String path) throws CommunicationException {
        LOGGER.info("PUT request: " + path);
        HttpPut httpPut = new HttpPut(path);
        try {
            return executeRequest(httpPut, path);
        } catch (CommunicationException e) {
            throw new CommunicationException(
                "Problems performing PUT request",
                path,
                e.getHttpStatusCode(),
                e
            );
        }
    }

    public ResponseData delete(String path) throws CommunicationException {
        LOGGER.info("DELETE request: {}", path);
        HttpDelete httpDelete = new HttpDelete(path);
        try {
            return executeRequest(httpDelete, path);
        } catch (CommunicationException e) {
            throw new CommunicationException(
                "Problems executing DELETE request",
                path,
                e.getHttpStatusCode(),
                e
            );
        }
    }

    public ResponseData executeRequest(ClassicHttpRequest httpRequest, String path)
        throws CommunicationException {
        Stopwatch timer = Stopwatch.createStarted();
        ResponseData responseData;
        try {
            httpRequest.addHeader("x-access-token", config.getAccessToken());
            httpRequest.addHeader("User-Agent", userAgent.asHeaderValue());
            responseData =
                httpClient.execute(
                    httpRequest,
                    httpResponse -> handleHttpResponse(httpRequest, httpResponse, path, timer)
                );
        } catch (IOException e) {
            TRAFFIC_LOGGER.info(
                "Request[{}]: {}, FAILED({}), ex:",
                httpRequest.getMethod(),
                path,
                timer.stop(),
                e
            );
            throw new CommunicationException("An exception occurred while performing HTTP request", path, e);
        }
        return responseData;
    }

    private ResponseData handleHttpResponse(
        ClassicHttpRequest httpRequest,
        ClassicHttpResponse httpResponse,
        String path,
        Stopwatch timer
    ) throws IOException, ParseException {
        int statusCode = httpResponse.getCode();
        String responseContent = httpResponse.getEntity() == null
            ? null
            : EntityUtils.toString(httpResponse.getEntity(), StandardCharsets.UTF_8).replace("\n", "");

        ResponseData responseDataFromRequest = new ResponseData(
            statusCode,
            responseContent == null
                ? null
                : new ByteArrayInputStream(responseContent.getBytes(StandardCharsets.UTF_8)),
            messageExtractor
        );
        logRequestStatus(responseDataFromRequest, httpRequest, path, timer, responseContent);
        return responseDataFromRequest;
    }

    private void logRequestStatus(
        ResponseData responseData,
        ClassicHttpRequest httpRequest,
        String path,
        Stopwatch timer,
        String responseContent
    ) {
        String errorMessage;

        if (responseData.isSuccessful()) {
            errorMessage = "Request[{}]: {}, response code - OK[{}]({}): {}";
        } else {
            errorMessage = "Request[{}]: {}, response code - FAILED[{}]({}): {}";
        }
        TRAFFIC_LOGGER.info(
            errorMessage,
            httpRequest.getMethod(),
            path,
            responseData.getStatusCode(),
            timer.stop(),
            responseContent
        );
    }

    public static class ResponseData {

        private final Integer statusCode;
        private final boolean successStatus;
        private final String message;

        ResponseData(
            Integer statusCode,
            InputStream httpResponseContent,
            MessageAndActionExtractor messageExtractor
        ) {
            Preconditions.checkNotNull(messageExtractor);

            this.statusCode = statusCode;
            this.successStatus = statusCode == HttpStatus.SC_OK || statusCode == HttpStatus.SC_ACCEPTED;
            this.message =
                httpResponseContent == null ? EMPTY_RESPONSE : messageExtractor.parse(httpResponseContent);
        }

        public ResponseData(Integer statusCode, String message) {
            this.statusCode = statusCode;
            this.successStatus = statusCode == HttpStatus.SC_OK || statusCode == HttpStatus.SC_ACCEPTED;
            this.message = message;
        }

        public Integer getStatusCode() {
            return statusCode;
        }

        public boolean isSuccessful() {
            return successStatus;
        }

        public String getMessage() {
            return message;
        }
    }
}
