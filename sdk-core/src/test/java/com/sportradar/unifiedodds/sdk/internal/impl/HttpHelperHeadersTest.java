/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */

package com.sportradar.unifiedodds.sdk.internal.impl;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.wireMockConfig;
import static com.sportradar.unifiedodds.sdk.conn.ApiSimulator.ApiStubDelay.toBeDelayedBy;
import static com.sportradar.unifiedodds.sdk.conn.ApiSimulator.HeaderEquality.forHeader;
import static com.sportradar.unifiedodds.sdk.internal.impl.HttpClients.*;
import static com.sportradar.unifiedodds.sdk.internal.impl.HttpDataFetchers.*;
import static java.time.temporal.ChronoUnit.SECONDS;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.read.ListAppender;
import com.github.tomakehurst.wiremock.common.ConsoleNotifier;
import com.github.tomakehurst.wiremock.junit5.WireMockExtension;
import com.sportradar.unifiedodds.sdk.LoggerDefinitions;
import com.sportradar.unifiedodds.sdk.conn.ApiSimulator;
import com.sportradar.unifiedodds.sdk.exceptions.CommunicationException;
import com.sportradar.unifiedodds.sdk.impl.assertions.LogsAssert;
import com.sportradar.unifiedodds.sdk.impl.assertions.RequestAssert;
import java.io.IOException;
import java.net.SocketTimeoutException;
import lombok.val;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@SuppressWarnings({ "ClassFanOutComplexity", "ConstantName", "MagicNumber" })
public class HttpHelperHeadersTest {

    @RegisterExtension
    private static final WireMockExtension wireMock = WireMockExtension
        .newInstance()
        .options(wireMockConfig().dynamicPort().notifier(new ConsoleNotifier(true)))
        .build();

    private static final String ANY_URL = "/v1/api/any-url.xml";
    private static final String TRACE_HEADER_NAME = "trace-id";
    private static final String TRACE_ID = "trace-id-123";

    private final TraceIdProvider traceIdProvider = mock(TraceIdProvider.class);

    private ListAppender<ILoggingEvent> logAppender;
    private ApiSimulator apiSimulator;
    private String url;

    @BeforeEach
    void initTestContext() {
        url = "http://localhost:" + wireMock.getPort() + ANY_URL;
        apiSimulator = new ApiSimulator(wireMock.getRuntimeInfo().getWireMock());
        when(traceIdProvider.generateTraceId()).thenReturn(TRACE_ID);
        attachLogAppender();
    }

    @Test
    void sendTraceIdHeaderOnSuccessfulPostRequest() throws Exception {
        val cfg = createConfig();
        val httpHelper = createHttpHelperBuilder()
            .with(cfg)
            .with(traceIdProvider)
            .with(createHttpClientFor(cfg))
            .build();

        apiSimulator.stubPostRequest(ANY_URL, forHeader(TRACE_HEADER_NAME, TRACE_ID));

        val responseData = httpHelper.post(url);

        assertThat(responseData.getStatusCode()).isEqualTo(200);
        LogsAssert.assertThat(logAppender).hasLogLineContaining(TRACE_ID);
    }

    @Test
    void sendTraceIdHeaderOnFailedPostRequest() throws Exception {
        val cfg = createConfig();
        val httpHelper = createHttpHelperBuilder()
            .with(cfg)
            .with(traceIdProvider)
            .with(createHttpClientFor(cfg))
            .build();

        apiSimulator.stubFailedPostRequest(ANY_URL, forHeader(TRACE_HEADER_NAME, TRACE_ID));

        val responseData = httpHelper.post(url);

        assertThat(responseData.getStatusCode()).isEqualTo(400);
        LogsAssert.assertThat(logAppender).hasLogLineContaining(TRACE_ID);
    }

    @Test
    void sendTraceIdHeaderOnSuccessfulPutRequest() throws Exception {
        val cfg = createConfig();
        val httpHelper = createHttpHelperBuilder()
            .with(cfg)
            .with(traceIdProvider)
            .with(createHttpClientFor(cfg))
            .build();

        apiSimulator.stubPutRequest(ANY_URL, forHeader(TRACE_HEADER_NAME, TRACE_ID));

        val responseData = httpHelper.put(url);

        assertThat(responseData.getStatusCode()).isEqualTo(200);
        LogsAssert.assertThat(logAppender).hasLogLineContaining(TRACE_ID);
    }

    @Test
    void sendTraceIdHeaderOnFailedPutRequest() throws Exception {
        val cfg = createConfig();
        val httpHelper = createHttpHelperBuilder()
            .with(cfg)
            .with(traceIdProvider)
            .with(createHttpClientFor(cfg))
            .build();

        apiSimulator.stubFailedPutRequest(ANY_URL, forHeader(TRACE_HEADER_NAME, TRACE_ID));

        val responseData = httpHelper.put(url);

        assertThat(responseData.getStatusCode()).isEqualTo(400);
        LogsAssert.assertThat(logAppender).hasLogLineContaining(TRACE_ID);
    }

    @Test
    void sendTraceIdHeaderOnSuccessfulDeleteRequest() throws Exception {
        val cfg = createConfig();
        val httpHelper = createHttpHelperBuilder()
            .with(cfg)
            .with(traceIdProvider)
            .with(createHttpClientFor(cfg))
            .build();

        apiSimulator.stubDeleteRequest(ANY_URL, forHeader(TRACE_HEADER_NAME, TRACE_ID));

        val responseData = httpHelper.delete(url);

        assertThat(responseData.getStatusCode()).isEqualTo(200);
        LogsAssert.assertThat(logAppender).hasLogLineContaining(TRACE_ID);
    }

    @Test
    void sendTraceIdHeaderOnFailedDeleteRequest() throws Exception {
        val cfg = createConfig();
        val httpHelper = createHttpHelperBuilder()
            .with(cfg)
            .with(traceIdProvider)
            .with(createHttpClientFor(cfg))
            .build();

        apiSimulator.stubFailedDeleteRequest(ANY_URL, forHeader(TRACE_HEADER_NAME, TRACE_ID));

        val responseData = httpHelper.delete(url);

        assertThat(responseData.getStatusCode()).isEqualTo(400);
        LogsAssert.assertThat(logAppender).hasLogLineContaining(TRACE_ID);
    }

    @Test
    void sendTraceIdHeaderOnPostRequestWhenExceptionIsThrown() throws Exception {
        val cfg = createConfig();
        val httpClient = createHttpClientThatThrowsIoException();
        val httpHelper = createHttpHelperBuilder().with(cfg).with(traceIdProvider).with(httpClient).build();

        apiSimulator.stubPostRequest(ANY_URL, forHeader(TRACE_HEADER_NAME, TRACE_ID));

        assertThatThrownBy(() -> httpHelper.post(url))
            .isInstanceOf(CommunicationException.class)
            .hasRootCauseInstanceOf(IOException.class);
        RequestAssert.assertThat(httpClient).hasSentRequestWithHeader(TRACE_HEADER_NAME, TRACE_ID);
        LogsAssert.assertThat(logAppender).hasLogLineContaining(TRACE_ID);
    }

    @Test
    void sendTraceIdHeaderOnPutRequestWhenExceptionIsThrown() throws Exception {
        val cfg = createConfig();
        val httpClient = createHttpClientThatThrowsIoException();
        val httpHelper = createHttpHelperBuilder().with(cfg).with(traceIdProvider).with(httpClient).build();

        apiSimulator.stubPutRequest(ANY_URL, forHeader(TRACE_HEADER_NAME, TRACE_ID));

        assertThatThrownBy(() -> httpHelper.put(url))
            .isInstanceOf(CommunicationException.class)
            .hasRootCauseInstanceOf(IOException.class);
        RequestAssert.assertThat(httpClient).hasSentRequestWithHeader(TRACE_HEADER_NAME, TRACE_ID);
        LogsAssert.assertThat(logAppender).hasLogLineContaining(TRACE_ID);
    }

    @Test
    void sendTraceIdHeaderOnDeleteRequestWhenExceptionIsThrown() throws Exception {
        val cfg = createConfig();
        val httpClient = createHttpClientThatThrowsIoException();
        val httpHelper = createHttpHelperBuilder().with(cfg).with(traceIdProvider).with(httpClient).build();

        apiSimulator.stubDeleteRequest(ANY_URL, forHeader(TRACE_HEADER_NAME, TRACE_ID));

        assertThatThrownBy(() -> httpHelper.delete(url))
            .isInstanceOf(CommunicationException.class)
            .hasRootCauseInstanceOf(IOException.class);
        RequestAssert.assertThat(httpClient).hasSentRequestWithHeader(TRACE_HEADER_NAME, TRACE_ID);
        LogsAssert.assertThat(logAppender).hasLogLineContaining(TRACE_ID);
    }

    @Test
    void sendTraceIdHeaderOnPostRequestWhenTimeoutIsThrown() throws Exception {
        val cfg = createConfig(1);
        val httpClient = createHttpClientFor(cfg);
        val httpHelper = createHttpHelperBuilder().with(cfg).with(traceIdProvider).with(httpClient).build();

        apiSimulator.stubPostRequest(
            ANY_URL,
            forHeader(TRACE_HEADER_NAME, TRACE_ID),
            toBeDelayedBy(cfg.getHttpClientTimeout() + 1, SECONDS)
        );

        assertThatThrownBy(() -> httpHelper.post(url))
            .isInstanceOf(CommunicationException.class)
            .hasRootCauseInstanceOf(SocketTimeoutException.class);

        wireMock.verify(
            postRequestedFor(urlEqualTo(ANY_URL)).withHeader(TRACE_HEADER_NAME, equalTo(TRACE_ID))
        );
        LogsAssert.assertThat(logAppender).hasLogLineContaining(TRACE_ID);
    }

    @Test
    void sendTraceIdHeaderOnPutRequestWhenTimeoutIsThrown() throws Exception {
        val cfg = createConfig(1);
        val httpClient = createHttpClientFor(cfg);
        val httpHelper = createHttpHelperBuilder().with(cfg).with(traceIdProvider).with(httpClient).build();

        apiSimulator.stubPutRequest(
            ANY_URL,
            forHeader(TRACE_HEADER_NAME, TRACE_ID),
            toBeDelayedBy(cfg.getHttpClientTimeout() + 1, SECONDS)
        );

        assertThatThrownBy(() -> httpHelper.put(url))
            .isInstanceOf(CommunicationException.class)
            .hasRootCauseInstanceOf(SocketTimeoutException.class);

        wireMock.verify(
            putRequestedFor(urlEqualTo(ANY_URL)).withHeader(TRACE_HEADER_NAME, equalTo(TRACE_ID))
        );
        LogsAssert.assertThat(logAppender).hasLogLineContaining(TRACE_ID);
    }

    @Test
    void sendTraceIdHeaderOnDeleteRequestWhenTimeoutIsThrown() throws Exception {
        val cfg = createConfig(1);
        val httpClient = createHttpClientFor(cfg);
        val httpHelper = createHttpHelperBuilder().with(cfg).with(traceIdProvider).with(httpClient).build();

        apiSimulator.stubDeleteRequest(
            ANY_URL,
            forHeader(TRACE_HEADER_NAME, TRACE_ID),
            toBeDelayedBy(cfg.getHttpClientTimeout() + 1, SECONDS)
        );

        assertThatThrownBy(() -> httpHelper.delete(url))
            .isInstanceOf(CommunicationException.class)
            .hasRootCauseInstanceOf(SocketTimeoutException.class);

        wireMock.verify(
            deleteRequestedFor(urlEqualTo(ANY_URL)).withHeader(TRACE_HEADER_NAME, equalTo(TRACE_ID))
        );
        LogsAssert.assertThat(logAppender).hasLogLineContaining(TRACE_ID);
    }

    private SdkInternalConfiguration createConfig() {
        return createConfig(5);
    }

    private SdkInternalConfiguration createConfig(int timeoutInSeconds) {
        val cfg = mock(SdkInternalConfiguration.class);
        when(cfg.getUseApiSsl()).thenReturn(false);
        when(cfg.getHttpClientTimeout()).thenReturn(timeoutInSeconds);
        when(cfg.getRecoveryHttpClientTimeout()).thenReturn(timeoutInSeconds);
        when(cfg.getFastHttpClientTimeout()).thenReturn((long) timeoutInSeconds);
        return cfg;
    }

    private void attachLogAppender() {
        Logger logger = LoggerFactory.getLogger(LoggerDefinitions.UfSdkRestTrafficLog.class);
        val logbackLogger = (ch.qos.logback.classic.Logger) logger;

        logAppender = new ListAppender<>();
        logAppender.start();
        logbackLogger.addAppender(logAppender);
    }
}
