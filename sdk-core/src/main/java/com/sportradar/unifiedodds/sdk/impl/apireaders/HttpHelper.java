/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */

package com.sportradar.unifiedodds.sdk.impl.apireaders;

import com.google.common.base.Preconditions;
import com.google.common.base.Stopwatch;
import com.google.inject.Inject;
import com.google.inject.name.Named;
import com.sportradar.uf.sportsapi.datamodel.APIPageNotFound;
import com.sportradar.uf.sportsapi.datamodel.Response;
import com.sportradar.unifiedodds.sdk.LoggerDefinitions;
import com.sportradar.unifiedodds.sdk.SDKInternalConfiguration;
import com.sportradar.unifiedodds.sdk.exceptions.internal.CommunicationException;
import com.sportradar.unifiedodds.sdk.exceptions.internal.DeserializationException;
import com.sportradar.unifiedodds.sdk.impl.Deserializer;
import org.apache.http.HttpStatus;
import org.apache.http.client.methods.*;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

public class HttpHelper {
    private static final Logger logger = LoggerFactory.getLogger(HttpHelper.class);
    private static final Logger trafficLogger = LoggerFactory.getLogger(LoggerDefinitions.UFSdkRestTrafficLog.class);
    private static final String EMPTY_RESPONSE = "EMPTY_RESPONSE";
    private final SDKInternalConfiguration config;
    private final CloseableHttpClient httpClient;
    private final Deserializer apiDeserializer;

    @Inject
    public HttpHelper(SDKInternalConfiguration config, CloseableHttpClient httpClient, @Named("ApiJaxbDeserializer") Deserializer apiDeserializer) {
        Preconditions.checkNotNull(config);
        Preconditions.checkNotNull(httpClient);
        Preconditions.checkNotNull(apiDeserializer);

        this.config = config;
        this.httpClient = httpClient;
        this.apiDeserializer = apiDeserializer;
    }

    public ResponseData post(String path) throws CommunicationException {
        logger.info("POST request: " + path);
        HttpPost httpPost = new HttpPost(path);
        try {
            return executeRequest(httpPost, path, "POST");
        } catch (CommunicationException e) {
            throw new CommunicationException("Problems executing POST: " + path, e);
        } finally {
            httpPost.releaseConnection();
        }
    }

    public ResponseData put(String path) throws CommunicationException {
        logger.info("PUT request: " + path);
        HttpPut httpPut = new HttpPut(path);
        try {
            return executeRequest(httpPut, path, "PUT");
        } catch (CommunicationException e) {
            throw new CommunicationException("Problems performing PUT on : " + path, e);
        } finally {
            httpPut.releaseConnection();
        }
    }

    public ResponseData delete(String path) throws CommunicationException {
        logger.info("DELETE request: {}", path);
        HttpDelete httpDelete = new HttpDelete(path);
        try {
            return executeRequest(httpDelete, path, "DELETE");
        } catch (CommunicationException e) {
            throw new CommunicationException("Problems executing DELETE: " + path, e);
        } finally {
            httpDelete.releaseConnection();
        }
    }

    private ResponseData executeRequest(HttpUriRequest httpRequest, String path, String type) throws CommunicationException {
        Stopwatch timer = Stopwatch.createStarted();
        Integer statusCode;
        String responseContent;
        CloseableHttpResponse resp = null;
        ResponseData responseData;
        try {
            httpRequest.addHeader("x-access-token", config.getAccessToken());
            resp = httpClient.execute(httpRequest);

            statusCode = resp.getStatusLine().getStatusCode();

            // content string used to log data
            responseContent = resp.getEntity() == null ?
                    null :
                    EntityUtils.toString(resp.getEntity(), StandardCharsets.UTF_8).replace("\n", "");

            // return response object
            responseData = new ResponseData(statusCode,
                    responseContent == null ? null : new ByteArrayInputStream(responseContent.getBytes()),
                    apiDeserializer);
        } catch (IOException e) {
            trafficLogger.info("Request[{}]: {}, FAILED({}), ex:", type, path, timer.stop(), e);
            throw new CommunicationException("An exception occurred while performing HTTP request", e);
        } finally {
            try {
                if (resp != null) {
                    resp.close();
                }
            } catch (IOException e) {
                logger.info("Response closure failed, with ex: {}", e.getMessage());
            }
        }

        if (responseData.isSuccessful()) {
            trafficLogger.info("Request[{}]: {}, response code - OK[{}]({}): {}", type, path, statusCode, timer.stop(), responseContent);
        } else {
            trafficLogger.info("Request[{}]: {}, response code - FAILED[{}]({}): {}", type, path, statusCode, timer.stop(), responseContent);
        }

        return responseData;
    }

    public static class ResponseData {
        private final Integer statusCode;
        private final boolean successStatus;
        private final String message;

        ResponseData(Integer statusCode, InputStream httpResponseContent, Deserializer deserializer) {
            Preconditions.checkNotNull(deserializer);

            this.statusCode = statusCode;
            this.successStatus = statusCode == HttpStatus.SC_OK || statusCode == HttpStatus.SC_ACCEPTED;
            this.message = httpResponseContent == null ? EMPTY_RESPONSE : tryDeserializeResponseMessage(deserializer, httpResponseContent);
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

    public static String tryDeserializeResponseMessage(Deserializer apiDeserializer, InputStream httpResponseContent) {
        Preconditions.checkNotNull(apiDeserializer);
        Preconditions.checkNotNull(httpResponseContent);

        String errMsg;
        try {
            Object deserializedResponse = apiDeserializer.deserialize(httpResponseContent);
            if (deserializedResponse instanceof APIPageNotFound) {
                errMsg = ((APIPageNotFound) deserializedResponse).getMessage();
            } else if (deserializedResponse instanceof Response) {
                String message = ((Response) deserializedResponse).getMessage();
                String action = ((Response) deserializedResponse).getAction();

                StringBuilder sb = new StringBuilder();
                if (message != null) {
                    sb.append(message);
                }

                if (action != null) {
                    if (message != null) {
                        sb.append(", ");
                    }
                    sb.append(action);
                }

                errMsg = sb.toString();
            } else {
                errMsg = "Unknown response format, " + deserializedResponse.getClass().getName();
            }
        } catch (DeserializationException e) {
            errMsg = "No specific message";
        }

        return errMsg;
    }
}
