/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */
package com.sportradar.unifiedodds.sdk.impl;

import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.wireMockConfig;
import static com.sportradar.unifiedodds.sdk.conn.ApiSimulator.ApiStubDelay.toBeDelayedBy;
import static com.sportradar.unifiedodds.sdk.impl.DataProviders.createDataProviderFor;
import static com.sportradar.unifiedodds.sdk.impl.Deserializers.sportsApiDeserializer;
import static com.sportradar.unifiedodds.sdk.impl.HttpClients.createStartedAsyncHttpClientFor;
import static com.sportradar.unifiedodds.sdk.impl.HttpDataFetchers.*;
import static com.sportradar.unifiedodds.sdk.testutil.generic.naturallanguage.Prepositions.in;
import static java.time.temporal.ChronoUnit.SECONDS;
import static java.util.Locale.ENGLISH;
import static java.util.concurrent.Executors.newFixedThreadPool;
import static java.util.concurrent.Executors.newSingleThreadExecutor;
import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.github.tomakehurst.wiremock.common.ConsoleNotifier;
import com.github.tomakehurst.wiremock.junit5.WireMockExtension;
import com.sportradar.unifiedodds.sdk.SdkInternalConfiguration;
import com.sportradar.unifiedodds.sdk.conn.ApiSimulator;
import com.sportradar.unifiedodds.sdk.exceptions.internal.CommunicationException;
import com.sportradar.unifiedodds.sdk.exceptions.internal.DataProviderException;
import com.sportradar.unifiedodds.sdk.testutil.generic.concurrent.VoidCallables;
import com.sportradar.unifiedodds.sdk.testutil.rabbit.integration.BaseUrl;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Stream;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.apache.commons.io.IOUtils;
import org.apache.hc.client5.http.ConnectTimeoutException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

@Slf4j
@SuppressWarnings({ "MagicNumber", "IllegalCatch" })
public class DataProviderTimeoutTest {

    @RegisterExtension
    private static WireMockExtension wireMock = WireMockExtension
        .newInstance()
        .options(wireMockConfig().dynamicPort().notifier(new ConsoleNotifier(true)))
        .build();

    private static final String DATA_FETCHERS =
        "com.sportradar.unifiedodds.sdk.impl.DataProviderTimeoutTest#dataFetchers";

    private BaseUrl apiBaseUrl;
    private ApiSimulator apiSimulator;
    private AtomicBoolean httpExecutionStarted = new AtomicBoolean(false);

    @BeforeEach
    void setup() throws Exception {
        apiBaseUrl = BaseUrl.of("localhost", wireMock.getPort());
        apiSimulator = new ApiSimulator(wireMock.getRuntimeInfo().getWireMock());
    }

    @Nested
    class ResponseTimeout {

        private ExecutorService socketExecutor = newSingleThreadExecutor();
        private ExecutorService serverRequestsExecutor = newFixedThreadPool(10);
        private ServerSocket serverSocket;
        private int serverPort;
        private AtomicBoolean shouldRunServer = new AtomicBoolean(true);

        @BeforeEach
        void setup() throws Exception {
            serverSocket = new ServerSocket(0);
            serverPort = serverSocket.getLocalPort();

            startHttpServerAndStartListeningToIncomingRequests();
        }

        private void startHttpServerAndStartListeningToIncomingRequests() {
            socketExecutor.execute(() -> {
                try {
                    while (shouldRunServer.get()) {
                        Socket clientSocket = serverSocket.accept();
                        serverRequestsExecutor.execute(() -> handleClient(clientSocket));
                    }
                } catch (IOException e) {
                    log.warn("problem while running server", e);
                }
            });
        }

        private void handleClient(Socket clientSocket) {
            try {
                byte[] chunk = new byte[256];
                for (int i = 0; i < 20; i++) {
                    clientSocket.getOutputStream().write(chunk);
                    clientSocket.getOutputStream().flush();
                    Thread.sleep(500); // Delay to simulate streaming
                }
                clientSocket.close();
            } catch (IOException | InterruptedException e) {
                log.warn("Exception while handling client", e);
            }
        }

        @AfterEach
        void cleanup() throws Exception {
            serverSocket.close();
            shouldRunServer.set(false);
            socketExecutor.shutdownNow();
            serverRequestsExecutor.shutdownNow();
        }

        @MethodSource(DATA_FETCHERS)
        @ParameterizedTest(name = "{0}")
        void respectsConfiguredResponseTimeoutForResponsesWhichProduceBytesButDoNotCompleteWithinGivenTimeout(
            String description,
            DataFetcherProvider dataFetcherProvider
        ) throws Exception {
            val cfg = configurationForApiWith1sClientTimeoutNoSslOn("localhost:" + serverPort);
            val provider = createDataProviderFor("/sports/en/sports.xml")
                .with(cfg)
                .with(dataFetcherProvider.getFor(cfg))
                .with(sportsApiDeserializer())
                .build();

            val getDataFuture = runAsync(() -> provider.getData());

            awaitRunAsyncCallIsPickedUpForExecution();

            CompletableFutureAssert
                .assertThat(getDataFuture)
                .completesExceptionallyWithin(5, TimeUnit.SECONDS)
                .satisfies(e -> {
                    assertThat(e).isInstanceOf(DataProviderException.class);
                    assertThat(e.getCause()).isInstanceOf(CommunicationException.class);
                    assertThat(e.getCause())
                        .hasMessage("API response taking too long to complete - timeout reached");
                    assertThat(e).hasRootCauseInstanceOf(TimeoutException.class);
                });
        }
    }

    @Nested
    class SocketTimeout {

        @Test
        void respectsConfiguredHttpClientTimeout() throws Exception {
            val cfg = configurationForApiWith1sClientTimeoutNoSslOn(apiBaseUrl.get());
            val httpClientWith1sTimeout = createStartedAsyncHttpClientFor(cfg);
            val provider = createDataProviderFor("/sports/en/sports.xml")
                .with(cfg)
                .with(
                    createDataFetcherWith20sRequestTimeout().with(httpClientWith1sTimeout).with(cfg).build()
                )
                .with(sportsApiDeserializer())
                .build();

            apiSimulator.stubAllSports(in(ENGLISH), toBeDelayedBy(5, SECONDS));

            val getDataFuture = runAsync(() -> provider.getData());

            awaitRunAsyncCallIsPickedUpForExecution();

            CompletableFutureAssert
                .assertThat(getDataFuture)
                .completesExceptionallyWithin(5, TimeUnit.SECONDS)
                .satisfies(e -> {
                    assertThat(e).isInstanceOf(DataProviderException.class);
                    assertThat(e.getCause()).isInstanceOf(CommunicationException.class);
                    assertThat(e).rootCause().isInstanceOfAny(SocketTimeoutException.class);
                });
        }
    }

    @Nested
    class ConnectTimeout {

        private ExecutorService socketSaturatingExecutor = newFixedThreadPool(50);
        private ServerSocket busySocket;
        private List<Socket> connectingSockets = new ArrayList<>();
        private int busySocketPort;

        @BeforeEach
        void setup() throws Exception {
            busySocket = createSocketWithFilledBacklog();
            busySocketPort = busySocket.getLocalPort();
        }

        @AfterEach
        void cleanup() throws Exception {
            busySocket.close();
            connectingSockets.forEach(IOUtils::closeQuietly);
            socketSaturatingExecutor.shutdown();
        }

        @Test
        void respectsConfiguredHttpClientTimeout() throws Exception {
            val cfg = configurationForApiWith1sClientTimeoutNoSslOn("localhost:" + busySocketPort);
            val httpClientWith1sTimeout = createStartedAsyncHttpClientFor(cfg);
            val provider = createDataProviderFor("/sports/en/sports.xml")
                .with(cfg)
                .with(
                    createDataFetcherWith20sRequestTimeout().with(httpClientWith1sTimeout).with(cfg).build()
                )
                .with(sportsApiDeserializer())
                .build();

            val getDataFuture = runAsync(() -> provider.getData());

            awaitRunAsyncCallIsPickedUpForExecution();

            CompletableFutureAssert
                .assertThat(getDataFuture)
                .completesExceptionallyWithin(5, TimeUnit.SECONDS)
                .satisfies(e -> {
                    assertThat(e).isInstanceOf(DataProviderException.class);
                    assertThat(e.getCause()).isInstanceOf(CommunicationException.class);
                    assertThat(e).rootCause().isInstanceOfAny(ConnectTimeoutException.class);
                });
        }

        private ServerSocket createSocketWithFilledBacklog() throws Exception {
            int anyPort = 0;
            int singleSlotBacklogSize = 1;
            ServerSocket socket = new ServerSocket(anyPort, singleSlotBacklogSize);
            saturateSocketSoThatItDoesNotAcceptMoreConnections(socket);
            return socket;
        }

        private void saturateSocketSoThatItDoesNotAcceptMoreConnections(ServerSocket socket)
            throws Exception {
            val oneConnectionCompleted = new AtomicBoolean(false);
            int reliableNumberOfConcurrentConnectionsToSaturateOnGitLab = 50;
            for (int i = 0; i < reliableNumberOfConcurrentConnectionsToSaturateOnGitLab; i++) {
                socketSaturatingExecutor.execute(() -> {
                    try {
                        Socket s = new Socket();
                        s.connect(socket.getLocalSocketAddress());
                        connectingSockets.add(s);
                        oneConnectionCompleted.set(true);
                    } catch (IOException e) {
                        log.warn("Exception while saturating the socket", e);
                    }
                });
            }
            await().until(oneConnectionCompleted::get);
        }
    }

    private void awaitRunAsyncCallIsPickedUpForExecution() {
        await().atMost(10, TimeUnit.SECONDS).untilTrue(httpExecutionStarted);
    }

    private SdkInternalConfiguration configurationForApiWith1sClientTimeoutNoSslOn(String anyFreePort) {
        SdkInternalConfiguration cfg = mock(SdkInternalConfiguration.class);
        when(cfg.getApiHostAndPort()).thenReturn(anyFreePort);
        when(cfg.getUseApiSsl()).thenReturn(false);
        when(cfg.getHttpClientTimeout()).thenReturn(1);
        when(cfg.getFastHttpClientTimeout()).thenReturn(1L);
        return cfg;
    }

    private CompletableFuture<?> runAsync(VoidCallables.ThrowingRunnable runnable) {
        return CompletableFuture.runAsync(() -> {
            try {
                httpExecutionStarted.set(true);
                runnable.run();
            } catch (Exception e) {
                throw new CompletionException(e);
            }
        });
    }

    static Stream<Arguments> dataFetchers() {
        return Stream.of(
            Arguments.arguments(
                "NormalDataFetcher",
                (DataFetcherProvider) cfg ->
                    createLogDataFetcher().with(createStartedAsyncHttpClientFor(cfg)).with(cfg).build()
            ),
            Arguments.arguments(
                "FastDataFetcher",
                (DataFetcherProvider) cfg ->
                    createLogFastDataFetcher().with(createStartedAsyncHttpClientFor(cfg)).with(cfg).build()
            )
        );
    }

    interface DataFetcherProvider {
        HttpDataFetcher getFor(SdkInternalConfiguration cfg);
    }
}
