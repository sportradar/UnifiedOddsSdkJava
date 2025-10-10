/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */
package com.sportradar.unifiedodds.sdk.internal.impl;

import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.wireMockConfig;
import static com.sportradar.unifiedodds.sdk.conn.ApiSimulator.ApiStubDelay.toBeDelayedBy;
import static com.sportradar.unifiedodds.sdk.internal.impl.DataProviders.createDataProviderFor;
import static com.sportradar.unifiedodds.sdk.internal.impl.Deserializers.sportsApiDeserializer;
import static com.sportradar.unifiedodds.sdk.testutil.generic.naturallanguage.Prepositions.*;
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
import com.sportradar.unifiedodds.sdk.cfg.UofApiConfigurationStub;
import com.sportradar.unifiedodds.sdk.cfg.UofConfiguration;
import com.sportradar.unifiedodds.sdk.cfg.UofConfigurationStub;
import com.sportradar.unifiedodds.sdk.conn.ApiSimulator;
import com.sportradar.unifiedodds.sdk.exceptions.CommunicationException;
import com.sportradar.unifiedodds.sdk.impl.CompletableFutureAssert;
import com.sportradar.unifiedodds.sdk.internal.cfg.TestConfigHelper;
import com.sportradar.unifiedodds.sdk.internal.exceptions.DataProviderException;
import com.sportradar.unifiedodds.sdk.internal.impl.DataProviders.HttpFetcherType;
import com.sportradar.unifiedodds.sdk.testutil.generic.concurrent.VoidCallables;
import com.sportradar.unifiedodds.sdk.testutil.rabbit.integration.BaseUrl;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicBoolean;
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
import org.junit.jupiter.params.provider.EnumSource;

@Slf4j
@SuppressWarnings({ "MagicNumber", "IllegalCatch", "ClassFanOutComplexity" })
public class DataProviderTimeoutTest {

    @RegisterExtension
    private static WireMockExtension wireMock = WireMockExtension
        .newInstance()
        .options(wireMockConfig().dynamicPort().notifier(new ConsoleNotifier(true)))
        .build();

    private BaseUrl apiBaseUrl;
    private ApiSimulator apiSimulator;
    private AtomicBoolean httpExecutionStarted = new AtomicBoolean(false);

    @BeforeEach
    void setup() throws Exception {
        apiBaseUrl = BaseUrl.of("localhost", wireMock.getPort());
        apiSimulator = new ApiSimulator(wireMock.getRuntimeInfo().getWireMock());
    }

    private static String localhost(int port) {
        return "localhost:" + port;
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

        @EnumSource(HttpFetcherType.class)
        @ParameterizedTest
        void respectsConfiguredResponseTimeoutForResponsesWhichProduceBytesButDoNotCompleteWithinGivenTimeout(
            HttpFetcherType httpFetcherType
        ) {
            val cfg = uofConfigForApiWith1sClientTimeoutNoSslOn(localhost(serverPort));
            val deprecatedCfg = internalConfigForApiWith1sClientTimeoutNoSslOn(localhost(serverPort));
            val provider = createDataProviderFor("/sports/en/sports.xml")
                .with(cfg)
                .with(deprecatedCfg)
                .with(httpFetcherType)
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
            val cfg = uofConfigForApiWith1sClientTimeoutNoSslOn(apiBaseUrl.get());
            val deprecatedCfg = internalConfigForApiWith1sClientTimeoutNoSslOn(apiBaseUrl.get());
            val provider = createDataProviderFor("/sports/en/sports.xml")
                .with(cfg)
                .with(deprecatedCfg)
                .withForcefullyOverriddenFetcherTimeoutInSeconds(20)
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
            val cfg = uofConfigForApiWith1sClientTimeoutNoSslOn(localhost(busySocketPort));
            val deprecatedCfg = internalConfigForApiWith1sClientTimeoutNoSslOn(localhost(busySocketPort));
            val provider = createDataProviderFor("/sports/en/sports.xml")
                .with(cfg)
                .with(deprecatedCfg)
                .withForcefullyOverriddenFetcherTimeoutInSeconds(20)
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

    private SdkInternalConfiguration internalConfigForApiWith1sClientTimeoutNoSslOn(String anyFreePort) {
        SdkInternalConfiguration cfg = mock(SdkInternalConfiguration.class);
        when(cfg.getApiHostAndPort()).thenReturn(anyFreePort);
        when(cfg.getUseApiSsl()).thenReturn(false);
        when(cfg.getHttpClientTimeout()).thenReturn(1);
        when(cfg.getFastHttpClientTimeout()).thenReturn(1L);
        return cfg;
    }

    private UofConfiguration uofConfigForApiWith1sClientTimeoutNoSslOn(String hostAndPort) {
        UofConfigurationStub config = new UofConfigurationStub();
        UofApiConfigurationStub apiConfig = (UofApiConfigurationStub) config.getApi();
        TestConfigHelper.setHostAndPort(from(hostAndPort), to(apiConfig));
        apiConfig.setUseSsl(false);
        apiConfig.setHttpClientTimeout(Duration.ofSeconds(1));
        apiConfig.setHttpClientFastFailingTimeout(Duration.ofSeconds(1));
        return config;
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
}
