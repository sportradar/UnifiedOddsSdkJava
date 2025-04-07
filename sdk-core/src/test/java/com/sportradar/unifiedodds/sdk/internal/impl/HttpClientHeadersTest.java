/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */

package com.sportradar.unifiedodds.sdk.internal.impl;

import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.wireMockConfig;
import static com.sportradar.unifiedodds.sdk.conn.ApiSimulator.ApiStubDelay.toBeDelayedBy;
import static com.sportradar.unifiedodds.sdk.conn.ApiSimulator.HeaderEquality.forHeader;
import static com.sportradar.unifiedodds.sdk.conn.SapiTournaments.Euro2024.euro2024TournamentInfo;
import static com.sportradar.unifiedodds.sdk.internal.impl.DataProviders.createDataProviderFor;
import static com.sportradar.unifiedodds.sdk.internal.impl.Deserializers.sportsApiDeserializer;
import static com.sportradar.unifiedodds.sdk.internal.impl.HttpClients.createStartedAsyncHttpClientFor;
import static com.sportradar.unifiedodds.sdk.internal.impl.HttpDataFetchers.createLogDataFetcher;
import static com.sportradar.unifiedodds.sdk.internal.impl.HttpDataFetchers.createLogFastDataFetcher;
import static com.sportradar.unifiedodds.sdk.testutil.generic.naturallanguage.Prepositions.in;
import static java.time.temporal.ChronoUnit.SECONDS;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.read.ListAppender;
import com.github.tomakehurst.wiremock.common.ConsoleNotifier;
import com.github.tomakehurst.wiremock.junit5.WireMockExtension;
import com.sportradar.uf.sportsapi.datamodel.SapiTournamentsEndpoint;
import com.sportradar.unifiedodds.sdk.LoggerDefinitions;
import com.sportradar.unifiedodds.sdk.conn.ApiSimulator;
import com.sportradar.unifiedodds.sdk.impl.assertions.LogsAssert;
import com.sportradar.unifiedodds.sdk.internal.exceptions.DataProviderException;
import com.sportradar.unifiedodds.sdk.testutil.rabbit.integration.BaseUrl;
import java.util.Locale;
import java.util.stream.Stream;
import lombok.val;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@SuppressWarnings({ "ClassFanOutComplexity", "ConstantName", "MagicNumber", "IllegalCatch" })
public class HttpClientHeadersTest {

    @RegisterExtension
    private static final WireMockExtension wireMock = WireMockExtension
        .newInstance()
        .options(wireMockConfig().dynamicPort().notifier(new ConsoleNotifier(true)))
        .build();

    private static final String DATA_FETCHERS =
        "com.sportradar.unifiedodds.sdk.internal.impl.HttpClientHeadersTest#dataFetchers";
    private static final String SPORTS_EN_TOURNAMENTS_URL = "/sports/en/tournaments.xml";
    private static final String TRACE_HEADER_NAME = "trace-id";
    private static final String TRACE_ID_NORMAL = "trace-id-123-normal";
    private static final String TRACE_ID_CRITICAL = "trace-id-123-critical";

    private static final TraceIdProvider normalTraceIdProvider = mock(TraceIdProvider.class);
    private static final TraceIdProvider criticalTraceIdProvider = mock(TraceIdProvider.class);

    private final TraceIdProvider traceIdProvider = mock(TraceIdProvider.class);

    private ListAppender<ILoggingEvent> logAppender;
    private BaseUrl apiBaseUrl;
    private ApiSimulator apiSimulator;

    @BeforeAll
    static void initTraceProviders() {
        when(normalTraceIdProvider.generateTraceId()).thenReturn(TRACE_ID_NORMAL);
        when(criticalTraceIdProvider.generateTraceId()).thenReturn(TRACE_ID_CRITICAL);
    }

    @BeforeEach
    void initTestContext() {
        apiBaseUrl = BaseUrl.of("localhost", wireMock.getPort());
        apiSimulator = new ApiSimulator(wireMock.getRuntimeInfo().getWireMock());
        logAppender = attachLogAppender();
    }

    @MethodSource(DATA_FETCHERS)
    @ParameterizedTest(name = "{0}")
    void sendTraceIdHeaderOnSuccessfulRequest(
        String description,
        DataFetcherProvider providerFactory,
        String traceId
    ) throws Exception {
        val cfg = createConfig(apiBaseUrl.get());
        val provider = createDataProviderFor(SPORTS_EN_TOURNAMENTS_URL)
            .with(cfg)
            .with(providerFactory.getFor(cfg))
            .with(sportsApiDeserializer())
            .<SapiTournamentsEndpoint>build();

        val tournament = euro2024TournamentInfo().getTournament();
        apiSimulator.stubAllTournaments(
            in(Locale.ENGLISH),
            tournament,
            forHeader(TRACE_HEADER_NAME, traceId)
        );

        val result = provider.getData(Locale.ENGLISH);

        assertThat(result).isNotNull();
        LogsAssert.assertThat(logAppender).hasLogLineContaining(traceId);
    }

    @MethodSource(DATA_FETCHERS)
    @ParameterizedTest(name = "{0}")
    void sendTraceIdHeaderOnFailedRequest(
        String description,
        DataFetcherProvider providerFactory,
        String traceId
    ) throws Exception {
        val cfg = createConfig(apiBaseUrl.get());
        val provider = createDataProviderFor(SPORTS_EN_TOURNAMENTS_URL)
            .with(cfg)
            .with(providerFactory.getFor(cfg))
            .with(sportsApiDeserializer())
            .<SapiTournamentsEndpoint>build();

        apiSimulator.stubAllTournamentsWithBadRequestErrorResponse(
            Locale.ENGLISH,
            forHeader(TRACE_HEADER_NAME, traceId)
        );

        assertThatThrownBy(() -> provider.getData(Locale.ENGLISH)).isInstanceOf(DataProviderException.class);
        LogsAssert.assertThat(logAppender).hasLogLineContaining(traceId);
    }

    @MethodSource(DATA_FETCHERS)
    @ParameterizedTest(name = "{0}")
    void sendTraceIdHeaderOnTimeoutRequest(
        String description,
        DataFetcherProvider providerFactory,
        String traceId
    ) throws Exception {
        val timeoutInSeconds = 1;
        val cfg = createConfig(apiBaseUrl.get(), timeoutInSeconds);
        val provider = createDataProviderFor(SPORTS_EN_TOURNAMENTS_URL)
            .with(cfg)
            .with(providerFactory.getFor(cfg))
            .with(sportsApiDeserializer())
            .<SapiTournamentsEndpoint>build();

        apiSimulator.stubApiGetRequest(
            SPORTS_EN_TOURNAMENTS_URL,
            forHeader(TRACE_HEADER_NAME, traceId),
            toBeDelayedBy(timeoutInSeconds + 1, SECONDS)
        );

        assertThatThrownBy(() -> provider.getData(Locale.ENGLISH)).isInstanceOf(DataProviderException.class);
        LogsAssert.assertThat(logAppender).hasLogLineContaining(traceId);
    }

    @Test
    void sendNewTraceIdHeaderPerRequest() throws Exception {
        val traceId1 = "randomTraceId-1";
        val traceId2 = "randomTraceId-2";
        val cfg = createConfig(apiBaseUrl.get());
        val dataFetcher = createLogDataFetcher()
            .with(createStartedAsyncHttpClientFor(cfg))
            .with(cfg)
            .with(traceIdProvider)
            .build();
        val provider = createDataProviderFor(SPORTS_EN_TOURNAMENTS_URL)
            .with(cfg)
            .with(dataFetcher)
            .with(sportsApiDeserializer())
            .<SapiTournamentsEndpoint>build();

        when(traceIdProvider.generateTraceId()).thenReturn(traceId1);
        val tournamentData1 = getTournamentDataWithTraceId(traceId1, provider);
        when(traceIdProvider.generateTraceId()).thenReturn(traceId2);
        val tournamentData2 = getTournamentDataWithTraceId(traceId2, provider);

        assertThat(tournamentData1).isNotNull();
        assertThat(tournamentData2).isNotNull();
        LogsAssert.assertThat(logAppender).hasLogLineContaining(traceId1);
        LogsAssert.assertThat(logAppender).hasLogLineContaining(traceId2);
    }

    @Test
    void sendNewTraceIdHeaderPerFastRequest() throws Exception {
        val traceId1 = "randomTraceId-1";
        val traceId2 = "randomTraceId-2";
        val cfg = createConfig(apiBaseUrl.get());
        val dataFetcher = createLogFastDataFetcher()
            .with(createStartedAsyncHttpClientFor(cfg))
            .with(cfg)
            .with(traceIdProvider)
            .build();
        val provider = createDataProviderFor(SPORTS_EN_TOURNAMENTS_URL)
            .with(cfg)
            .with(dataFetcher)
            .with(sportsApiDeserializer())
            .<SapiTournamentsEndpoint>build();

        when(traceIdProvider.generateTraceId()).thenReturn(traceId1);
        val tournamentData1 = getTournamentDataWithTraceId(traceId1, provider);
        when(traceIdProvider.generateTraceId()).thenReturn(traceId2);
        val tournamentData2 = getTournamentDataWithTraceId(traceId2, provider);

        assertThat(tournamentData1).isNotNull();
        assertThat(tournamentData2).isNotNull();
        LogsAssert.assertThat(logAppender).hasLogLineContaining(traceId1);
        LogsAssert.assertThat(logAppender).hasLogLineContaining(traceId2);
    }

    private SapiTournamentsEndpoint getTournamentDataWithTraceId(
        String traceId,
        DataProvider<SapiTournamentsEndpoint> provider
    ) throws DataProviderException {
        val anyTournament = euro2024TournamentInfo().getTournament();
        apiSimulator.stubAllTournaments(
            in(Locale.ENGLISH),
            anyTournament,
            forHeader(TRACE_HEADER_NAME, traceId)
        );
        return provider.getData(Locale.ENGLISH);
    }

    private SdkInternalConfiguration createConfig(String host) {
        return createConfig(host, 5);
    }

    private SdkInternalConfiguration createConfig(String host, long timeoutInSeconds) {
        val cfg = mock(SdkInternalConfiguration.class);
        when(cfg.getApiHostAndPort()).thenReturn(host);
        when(cfg.getUseApiSsl()).thenReturn(false);
        when(cfg.getHttpClientTimeout()).thenReturn((int) timeoutInSeconds);
        when(cfg.getFastHttpClientTimeout()).thenReturn(timeoutInSeconds);
        return cfg;
    }

    private ListAppender<ILoggingEvent> attachLogAppender() {
        Logger logger = LoggerFactory.getLogger(LoggerDefinitions.UfSdkRestTrafficLog.class);
        val logbackLogger = (ch.qos.logback.classic.Logger) logger;

        val appender = new ListAppender<ILoggingEvent>();
        appender.start();
        logbackLogger.addAppender(appender);

        return appender;
    }

    private static Stream<Arguments> dataFetchers() {
        return Stream.of(
            Arguments.arguments(
                "NormalDataFetcher",
                (DataFetcherProvider) cfg ->
                    createLogDataFetcher()
                        .with(createStartedAsyncHttpClientFor(cfg))
                        .with(cfg)
                        .with(normalTraceIdProvider)
                        .build(),
                TRACE_ID_NORMAL
            ),
            Arguments.arguments(
                "FastDataFetcher",
                (DataFetcherProvider) cfg ->
                    createLogFastDataFetcher()
                        .with(createStartedAsyncHttpClientFor(cfg))
                        .with(cfg)
                        .with(criticalTraceIdProvider)
                        .build(),
                TRACE_ID_CRITICAL
            )
        );
    }

    interface DataFetcherProvider {
        HttpDataFetcher getFor(SdkInternalConfiguration cfg);
    }
}
