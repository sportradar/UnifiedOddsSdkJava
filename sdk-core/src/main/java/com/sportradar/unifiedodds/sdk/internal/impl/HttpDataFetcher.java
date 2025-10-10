/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */

package com.sportradar.unifiedodds.sdk.internal.impl;

import com.google.common.base.Preconditions;
import com.sportradar.unifiedodds.sdk.cfg.UofConfiguration;
import com.sportradar.unifiedodds.sdk.exceptions.CommunicationException;
import com.sportradar.unifiedodds.sdk.internal.commoniam.OAuth2Token;
import com.sportradar.unifiedodds.sdk.internal.commoniam.OAuth2TokenCache;
import com.sportradar.unifiedodds.sdk.internal.impl.http.ApiResponseHandlingException;
import com.sportradar.utils.jacoco.ExcludeFromJacocoGeneratedReportUnreachableCode;
import com.sportradar.utils.jacoco.ExcludeFromJacocoGeneratedReportUntestableCheckedException;
import java.io.*;
import java.util.Optional;
import java.util.concurrent.*;
import org.apache.hc.client5.http.async.methods.SimpleHttpResponse;
import org.apache.hc.client5.http.async.methods.SimpleRequestBuilder;
import org.apache.hc.client5.http.classic.methods.HttpGet;
import org.apache.hc.client5.http.classic.methods.HttpPost;
import org.apache.hc.client5.http.impl.async.CloseableHttpAsyncClient;
import org.apache.hc.core5.concurrent.FutureCallback;
import org.apache.hc.core5.http.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@SuppressWarnings({ "ClassFanOutComplexity", "ParameterNumber" })
abstract class HttpDataFetcher {

    private static final String TRACE_ID_HEADER = "trace-id";
    private static final Logger LOGGER = LoggerFactory.getLogger(HttpDataFetcher.class);
    private final UofConfiguration uofConfiguration;
    private final CloseableHttpAsyncClient httpClient;
    private final UnifiedOddsStatistics statsBean;
    private final HttpResponseHandler responseHandler;
    private final long timeoutSeconds;
    private final UserAgentProvider userAgentProvider;
    private final TraceIdProvider traceIdProvider;
    private final OAuth2TokenCache oauthTokenCache;

    HttpDataFetcher(
        UofConfiguration uofConfiguration,
        CloseableHttpAsyncClient httpClient,
        UnifiedOddsStatistics statsBean,
        HttpResponseHandler responseHandler,
        UserAgentProvider userAgentProvider,
        TraceIdProvider traceIdProvider,
        long timeoutSeconds,
        OAuth2TokenCache oauthTokenCache
    ) {
        Preconditions.checkNotNull(uofConfiguration);
        Preconditions.checkNotNull(httpClient);
        Preconditions.checkNotNull(statsBean);
        Preconditions.checkNotNull(responseHandler);
        Preconditions.checkNotNull(userAgentProvider, "userAgentProvider");
        Preconditions.checkNotNull(traceIdProvider, "traceIdProvider");
        Preconditions.checkArgument(timeoutSeconds > 0, "timeout cannot be 0");
        Preconditions.checkNotNull(oauthTokenCache, "oauthTokenCache");

        this.uofConfiguration = uofConfiguration;
        this.httpClient = httpClient;
        this.statsBean = statsBean;
        this.responseHandler = responseHandler;
        this.userAgentProvider = userAgentProvider;
        this.traceIdProvider = traceIdProvider;
        this.timeoutSeconds = timeoutSeconds;
        this.oauthTokenCache = oauthTokenCache;
    }

    public HttpData get(String path) throws CommunicationException {
        return send(new HttpGet(path), path);
    }

    public HttpData post(String path, HttpEntity content) throws CommunicationException {
        HttpPost httpPost = new HttpPost(path);
        httpPost.setEntity(content);
        return send(httpPost, path);
    }

    protected HttpData send(ClassicHttpRequest httpRequest, String path) throws CommunicationException {
        statsBean.onStreamingHttpGet(path);

        httpRequest.addHeader("User-Agent", userAgentProvider.asHeaderValue());
        httpRequest.addHeader(TRACE_ID_HEADER, traceIdProvider.generateTraceId());

        try {
            if (uofConfiguration.getClientAuthentication() != null) {
                OAuth2Token token = oauthTokenCache.getToken();
                httpRequest.addHeader("Authorization", token.getTokenType() + " " + token.getAccessToken());
            } else if (uofConfiguration.getAccessToken() != null) {
                httpRequest.addHeader("x-access-token", uofConfiguration.getAccessToken());
            }

            Optional<byte[]> requestBody = readBody(httpRequest, path);
            SimpleRequestBuilder request = SimpleRequestBuilder.copy(httpRequest);
            requestBody.ifPresent(bytes ->
                request.setBody(bytes, ContentType.parse(httpRequest.getEntity().getContentType()))
            );

            Future<SimpleHttpResponse> future = httpClient.execute(request.build(), noopFutureCallback());
            SimpleHttpResponse response = getResponseWithTimeout(future, path);
            return responseHandler.extractHttpDataFromHttpResponse(response, path);
        } catch (ExecutionException ex) {
            throw new CommunicationException("There was a problem retrieving the requested data", path, ex);
        } catch (ApiResponseHandlingException e) {
            throw new CommunicationException(e.getMessage(), path, e.getHttpStatusCode(), e);
        } catch (OAuth2TokenCache.OAuth2TokenRetrievalHttpException e) {
            throw new CommunicationException(
                "Failed to retrieve OAuth2 access token",
                e.getPath(),
                e.getStatusCode(),
                e
            );
        } catch (OAuth2TokenCache.OAuth2TokenRetrievalException e) {
            throw new CommunicationException("Failed to retrieve OAuth2 access token", "", e);
        } catch (TimeoutException ex) {
            throw new CommunicationException(
                "API response taking too long to complete - timeout reached",
                path,
                ex
            );
        }
    }

    @ExcludeFromJacocoGeneratedReportUntestableCheckedException
    protected String extractTraceId(ClassicHttpRequest request) {
        Header header = null;
        try {
            header = request.getHeader(TRACE_ID_HEADER);
        } catch (ProtocolException e) {
            return null;
        }
        return header != null ? header.getValue() : null;
    }

    @ExcludeFromJacocoGeneratedReportUntestableCheckedException
    private SimpleHttpResponse getResponseWithTimeout(Future<SimpleHttpResponse> execute, String path)
        throws ExecutionException, TimeoutException, CommunicationException {
        try {
            return execute.get(timeoutSeconds, TimeUnit.SECONDS);
        } catch (InterruptedException ex) {
            LOGGER.warn("Waiting for the API call interrupted", ex);
            Thread.currentThread().interrupt();
            throw new CommunicationException(
                "There was a problem retrieving the requested data. Execution was interrupted",
                path,
                ex
            );
        }
    }

    @ExcludeFromJacocoGeneratedReportUntestableCheckedException
    private Optional<byte[]> readBody(ClassicHttpRequest httpRequest, String path)
        throws CommunicationException {
        if (httpRequest.getEntity() == null) {
            return Optional.empty();
        }
        try (InputStream requestBodyStream = httpRequest.getEntity().getContent()) {
            return Optional.of(toByteArray(requestBodyStream));
        } catch (IOException ex) {
            throw new CommunicationException("There was a problem serializing request data", path, ex);
        }
    }

    @SuppressWarnings("MagicNumber")
    private byte[] toByteArray(InputStream input) throws IOException {
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        byte[] data = new byte[1024];
        int bytesRead;
        while ((bytesRead = input.read(data, 0, data.length)) != -1) {
            buffer.write(data, 0, bytesRead);
        }
        return buffer.toByteArray();
    }

    private FutureCallback<SimpleHttpResponse> noopFutureCallback() {
        return new FutureCallback<SimpleHttpResponse>() {
            @Override
            public void completed(SimpleHttpResponse o) {
                // execution is handled in the blocking way so this method is not needed
            }

            @Override
            public void failed(Exception e) {
                // execution is handled in the blocking way so this method is not needed
            }

            @Override
            @ExcludeFromJacocoGeneratedReportUnreachableCode
            public void cancelled() {
                // execution is handled in the blocking way so this method is not needed
            }
        };
    }
}
