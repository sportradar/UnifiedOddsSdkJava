/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */
package com.sportradar.unifiedodds.sdk.internal.impl;

import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.wireMockConfig;
import static com.sportradar.unifiedodds.sdk.CapiCustomBet.getCalculationWithHarmonization;
import static com.sportradar.unifiedodds.sdk.conn.ApiSimulator.BodyCondition.forRequestBody;
import static com.sportradar.unifiedodds.sdk.conn.ApiSimulator.HeaderEquality.requiringHeader;
import static com.sportradar.unifiedodds.sdk.conn.SapiSports.allSports;
import static com.sportradar.unifiedodds.sdk.conn.SapiTournaments.Euro2024.euro2024TournamentInfo;
import static com.sportradar.unifiedodds.sdk.internal.cfg.TestConfigHelper.setHostAndPort;
import static com.sportradar.unifiedodds.sdk.internal.impl.DataProviders.createDataProviderFor;
import static com.sportradar.unifiedodds.sdk.internal.impl.Deserializers.customBetApiDeserializer;
import static com.sportradar.unifiedodds.sdk.internal.impl.Deserializers.sportsApiDeserializer;
import static com.sportradar.unifiedodds.sdk.testutil.generic.naturallanguage.Prepositions.*;
import static java.util.stream.Collectors.toList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatException;
import static org.awaitility.Awaitility.await;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.github.tomakehurst.wiremock.common.ConsoleNotifier;
import com.github.tomakehurst.wiremock.junit5.WireMockExtension;
import com.sportradar.uf.custombet.datamodel.CapiCalculationResponse;
import com.sportradar.uf.custombet.datamodel.CapiResponse;
import com.sportradar.uf.custombet.datamodel.CapiSelectionType;
import com.sportradar.uf.custombet.datamodel.CapiSelections;
import com.sportradar.uf.sportsapi.datamodel.SapiSportsEndpoint;
import com.sportradar.uf.sportsapi.datamodel.SapiTournamentsEndpoint;
import com.sportradar.unifiedodds.sdk.cfg.UofApiConfigurationStub;
import com.sportradar.unifiedodds.sdk.cfg.UofConfiguration;
import com.sportradar.unifiedodds.sdk.cfg.UofConfigurationStub;
import com.sportradar.unifiedodds.sdk.conn.ApiSimulator;
import com.sportradar.unifiedodds.sdk.exceptions.CommunicationException;
import com.sportradar.unifiedodds.sdk.internal.exceptions.DataProviderException;
import com.sportradar.unifiedodds.sdk.internal.exceptions.DeserializationException;
import com.sportradar.unifiedodds.sdk.internal.impl.DataProviders.HttpFetcherType;
import com.sportradar.unifiedodds.sdk.internal.impl.http.ApiResponseHandlingException;
import com.sportradar.unifiedodds.sdk.testutil.rabbit.integration.BaseUrl;
import com.sportradar.utils.Urns;
import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.*;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

@Slf4j
@SuppressWarnings({ "MagicNumber", "IllegalCatch", "MultipleStringLiterals", "ClassFanOutComplexity" })
public class DataProviderTest {

    @RegisterExtension
    private static WireMockExtension wireMock = WireMockExtension
        .newInstance()
        .options(wireMockConfig().dynamicPort().notifier(new ConsoleNotifier(true)))
        .build();

    private BaseUrl apiBaseUrl;
    private ApiSimulator apiSimulator;

    @BeforeEach
    void setup() throws Exception {
        apiBaseUrl = BaseUrl.of("localhost", wireMock.getPort());
        apiSimulator = new ApiSimulator(wireMock.getRuntimeInfo().getWireMock());
    }

    @Nested
    class PositiveResponses {

        @EnumSource(HttpFetcherType.class)
        @ParameterizedTest
        void returnsDataForGetRequest(HttpFetcherType httpFetcherType) throws Exception {
            val cfg = uofConfigForApiWith1sClientTimeoutNoSslOn(apiBaseUrl.get());
            val deprecatedCfg = internalConfigForApiWith1sClientTimeoutNoSslOn(apiBaseUrl.get());
            val provider = createDataProviderFor("/sports/de/sports.xml")
                .with(cfg)
                .with(deprecatedCfg)
                .with(httpFetcherType)
                .with(sportsApiDeserializer())
                .<SapiSportsEndpoint>build();

            apiSimulator.stubAllSports(Locale.GERMAN);

            val allSports = provider.getData();

            assertThat(idAndNameListOf(allSports)).isEqualTo(idAndNameListOf(allSports()));
        }

        @EnumSource(HttpFetcherType.class)
        @ParameterizedTest
        void returnsDataForGetWithArgsRequestWithChineseUnicodeCharacters(HttpFetcherType httpFetcherType)
            throws Exception {
            val cfg = uofConfigForApiWith1sClientTimeoutNoSslOn(apiBaseUrl.get());
            val deprecatedCfg = internalConfigForApiWith1sClientTimeoutNoSslOn(apiBaseUrl.get());
            val provider = createDataProviderFor("/sports/zh/sports.xml")
                .with(cfg)
                .with(deprecatedCfg)
                .with(httpFetcherType)
                .with(sportsApiDeserializer())
                .<SapiSportsEndpoint>build();

            apiSimulator.stubAllSports(Locale.CHINESE);

            val allSports = provider.getData("any-value");

            assertThat(idAndNameListOf(allSports)).isEqualTo(idAndNameListOf(allSports(Locale.CHINESE)));
        }

        @EnumSource(HttpFetcherType.class)
        @ParameterizedTest
        void returnsDataForGetWithAdditionalInfoRequest(HttpFetcherType httpFetcherType) throws Exception {
            val cfg = uofConfigForApiWith1sClientTimeoutNoSslOn(apiBaseUrl.get());
            val deprecatedCfg = internalConfigForApiWith1sClientTimeoutNoSslOn(apiBaseUrl.get());
            val provider = createDataProviderFor("/sports/%s/sports.xml")
                .with(cfg)
                .with(deprecatedCfg)
                .with(httpFetcherType)
                .with(sportsApiDeserializer())
                .<SapiSportsEndpoint>build();

            apiSimulator.stubAllSports(Locale.GERMAN);

            val allSports = provider.getDataWithAdditionalInfo(Locale.GERMAN);

            assertThat(idAndNameListOf(allSports.getData())).isEqualTo(idAndNameListOf(allSports()));
        }

        @EnumSource(HttpFetcherType.class)
        @ParameterizedTest
        void returnsDataForPostRequest(HttpFetcherType httpFetcherType) throws Exception {
            val cfg = uofConfigForApiWith1sClientTimeoutNoSslOn(apiBaseUrl.get());
            val deprecatedCfg = internalConfigForApiWith1sClientTimeoutNoSslOn(apiBaseUrl.get());
            val provider = createDataProviderFor("/custombet/calculate")
                .with(cfg)
                .with(deprecatedCfg)
                .with(httpFetcherType)
                .with(customBetApiDeserializer())
                .<CapiCalculationResponse>build();

            val selections = customBetSelectionsForHarmonization();
            apiSimulator.stubCustomBetCalculate(
                getCalculationWithHarmonization(true),
                forRequestBody(selections)
            );

            val calculationResponse = provider.postData(selections);

            assertThat(calculationResponse.getCalculation().getOdds())
                .isEqualTo(getCalculationWithHarmonization(true).getCalculation().getOdds());
        }

        @EnumSource(HttpFetcherType.class)
        @ParameterizedTest
        void sendsAccessTokenAsHeader(HttpFetcherType httpFetcherType) throws Exception {
            val cfg = uofConfigForApiWithTokenAnd1sClientTimeoutNoSslOn(apiBaseUrl.get());
            val deprecatedCfg = internalConfigForApiWithTokenAnd1sClientTimeoutNoSslOn(apiBaseUrl.get());
            val provider = createDataProviderFor("/sports/de/tournaments.xml")
                .with(cfg)
                .with(deprecatedCfg)
                .with(httpFetcherType)
                .with(sportsApiDeserializer())
                .<SapiTournamentsEndpoint>build();

            val tournament = euro2024TournamentInfo().getTournament();
            apiSimulator.stubAllTournaments(
                in(Locale.GERMAN),
                tournament,
                requiringHeader("x-access-token", cfg.getAccessToken())
            );

            SapiTournamentsEndpoint data = provider.getData();

            assertThat(data.getTournament()).extracting("id").containsOnly(tournament.getId());
        }

        @Test
        void sendsUserAgentAsHeader() throws Exception {
            val cfg = uofConfigForApiWithTokenAnd1sClientTimeoutNoSslOn(apiBaseUrl.get());
            val deprecatedCfg = internalConfigForApiWithTokenAnd1sClientTimeoutNoSslOn(apiBaseUrl.get());
            val userAgentProvider = new UserAgentProvider(
                "Java-SDK 9.10.11",
                Instant.ofEpochMilli(1740586231)
            );
            val provider = createDataProviderFor("/sports/de/tournaments.xml")
                .with(cfg)
                .with(deprecatedCfg)
                .with(userAgentProvider)
                .with(sportsApiDeserializer())
                .<SapiTournamentsEndpoint>build();

            val tournament = euro2024TournamentInfo().getTournament();
            apiSimulator.stubAllTournaments(
                in(Locale.GERMAN),
                tournament,
                requiringHeader("user-agent", userAgentProvider.asHeaderValue())
            );

            SapiTournamentsEndpoint data = provider.getData();

            assertThat(data.getTournament()).extracting("id").containsOnly(tournament.getId());
        }

        private List<String> idAndNameListOf(SapiSportsEndpoint allSports) {
            return allSports.getSport().stream().map(s -> s.getId() + "-" + s.getName()).collect(toList());
        }

        public CapiSelections customBetSelectionsForHarmonization() {
            CapiSelectionType eventSelection = new CapiSelectionType();
            eventSelection.setId(Urns.SportEvents.getForAnyMatch().toString());
            eventSelection.setMarketId(19);
            eventSelection.setSpecifiers("total=1.5");
            eventSelection.setOutcomeId("12");
            eventSelection.setOdds(1.2);

            CapiSelections selections = new CapiSelections();
            selections.getSelections().add(eventSelection);
            return selections;
        }
    }

    @Nested
    class Concurrency {

        private final int tenThreads = 10;
        private final ExecutorService executor = Executors.newFixedThreadPool(tenThreads);

        @AfterEach
        void tearDown() {
            executor.shutdown();
        }

        @EnumSource(HttpFetcherType.class)
        @ParameterizedTest
        void returnsDataForGetRequestWhenMultipleThreadsAreAccessingTheSameDataProvider(
            HttpFetcherType httpFetcherType
        ) throws Exception {
            val config = uofConfigForApiWith10MaxConnectionsAnd1sClientTimeoutNoSslOn(apiBaseUrl.get());
            val deprecatedCfg = internalConfigForApiWith10MaxConnectionsAnd1sClientTimeoutNoSslOn(
                apiBaseUrl.get()
            );
            val provider = createDataProviderFor("/sports/de/sports.xml")
                .with(config)
                .with(deprecatedCfg)
                .with(httpFetcherType)
                .with(sportsApiDeserializer())
                .<SapiSportsEndpoint>build();

            apiSimulator.stubAllSports(Locale.GERMAN);

            Callable<SapiSportsEndpoint> getData = () -> provider.getData();

            List<SapiSportsEndpoint> results = repeatTimes(tenThreads, getData);

            assertThat(results)
                .isNotEmpty()
                .allSatisfy(sports ->
                    assertThat(idAndNameListOf(sports)).isEqualTo(idAndNameListOf(allSports()))
                );
        }

        private <T> List<T> repeatTimes(int threads, Callable<T> getData)
            throws InterruptedException, ExecutionException {
            val results = new ArrayList<Future<T>>();
            for (int i = 0; i < threads; i++) {
                results.add(executor.submit(() -> getData.call()));
            }

            await().atMost(5, TimeUnit.SECONDS).until(() -> results.stream().allMatch(Future::isDone));

            List<T> sports = new ArrayList<>();
            for (val result : results) {
                sports.add(result.get());
            }
            return sports;
        }
    }

    @Nested
    class DataProviderErrors {

        @EnumSource(HttpFetcherType.class)
        @ParameterizedTest
        void throwsDeserializationExceptionWhenPostRequestBodyCannotBeSerializedUsingProvidedSerializer(
            HttpFetcherType httpFetcherType
        ) throws Exception {
            val cfg = uofConfigForApiWith1sClientTimeoutNoSslOn(apiBaseUrl.get());
            val deprecatedCfg = internalConfigForApiWith1sClientTimeoutNoSslOn(apiBaseUrl.get());
            val provider = createDataProviderFor("/custombet/calculate")
                .with(cfg)
                .with(deprecatedCfg)
                .with(httpFetcherType)
                .with(sportsApiDeserializer())
                .build();

            apiSimulator.returnNotFoundForCustomBetCalculate();

            assertThatException()
                .isThrownBy(() -> provider.postData(anyNonSapiSerializableXmlElement()))
                .isInstanceOf(DataProviderException.class)
                .withCauseInstanceOf(DeserializationException.class);
        }

        @EnumSource(HttpFetcherType.class)
        @ParameterizedTest
        void throwsDeserializationExceptionWhenResponseBodyCannotBeSerializedUsingProvidedSerializer(
            HttpFetcherType httpFetcherType
        ) throws Exception {
            val cfg = uofConfigForApiWith1sClientTimeoutNoSslOn(apiBaseUrl.get());
            val deprecatedCfg = internalConfigForApiWith1sClientTimeoutNoSslOn(apiBaseUrl.get());
            val provider = createDataProviderFor("/users/whoami.xml")
                .with(cfg)
                .with(deprecatedCfg)
                .with(httpFetcherType)
                .with(customBetApiDeserializer())
                .build();

            apiSimulator.defineBookmaker();

            assertThatException()
                .isThrownBy(() -> provider.getData())
                .isInstanceOf(DataProviderException.class)
                .withCauseInstanceOf(DeserializationException.class);
        }

        @EnumSource(HttpFetcherType.class)
        @ParameterizedTest
        void throwsApiResponseHandlingExceptionWhenHttpResponseHasNoBodyAtAll(
            HttpFetcherType httpFetcherType
        ) throws Exception {
            val cfg = uofConfigForApiWith1sClientTimeoutNoSslOn(apiBaseUrl.get());
            val deprecatedCfg = internalConfigForApiWith1sClientTimeoutNoSslOn(apiBaseUrl.get());
            val provider = createDataProviderFor("/users/whoami.xml")
                .with(cfg)
                .with(deprecatedCfg)
                .with(httpFetcherType)
                .with(customBetApiDeserializer())
                .build();

            apiSimulator.stubWhoAmIWithEmptyResponseBody();

            assertThatException()
                .isThrownBy(() -> provider.getData())
                .isInstanceOf(DataProviderException.class)
                .withRootCauseInstanceOf(ApiResponseHandlingException.class)
                .havingCause()
                .isInstanceOf(CommunicationException.class)
                .satisfies(ce -> {
                    val communicationException = (CommunicationException) ce;
                    assertThat(communicationException.getUrl())
                        .isEqualTo("http://" + apiBaseUrl.get() + "/v1/users/whoami.xml");
                    assertThat(communicationException.getHttpStatusCode()).isEqualTo(200);
                });
        }

        @EnumSource(HttpFetcherType.class)
        @ParameterizedTest
        void throwsApiResponseHandlingExceptionWhenHttpStatus404(HttpFetcherType httpFetcherType)
            throws Exception {
            val cfg = uofConfigForApiWith1sClientTimeoutNoSslOn(apiBaseUrl.get());
            val deprecatedCfg = internalConfigForApiWith1sClientTimeoutNoSslOn(apiBaseUrl.get());
            val provider = createDataProviderFor("/custombet/calculate")
                .with(cfg)
                .with(deprecatedCfg)
                .with(httpFetcherType)
                .with(customBetApiDeserializer())
                .build();

            apiSimulator.returnNotFoundForCustomBetCalculate();

            assertThatException()
                .isThrownBy(() -> provider.getData())
                .isInstanceOf(DataProviderException.class)
                .withRootCauseInstanceOf(ApiResponseHandlingException.class)
                .havingCause()
                .isInstanceOf(CommunicationException.class)
                .satisfies(ce -> {
                    val communicationException = (CommunicationException) ce;
                    assertThat(communicationException.getUrl())
                        .isEqualTo("http://" + apiBaseUrl.get() + "/v1/custombet/calculate");
                    assertThat(communicationException.getHttpStatusCode()).isEqualTo(404);
                });
        }

        @EnumSource(HttpFetcherType.class)
        @ParameterizedTest
        void throwsApiResponseHandlingExceptionWhenBadRequestWithEmptyErrorMessage(
            HttpFetcherType httpFetcherType
        ) throws Exception {
            val cfg = uofConfigForApiWith1sClientTimeoutNoSslOn(apiBaseUrl.get());
            val deprecatedCfg = internalConfigForApiWith1sClientTimeoutNoSslOn(apiBaseUrl.get());
            val provider = createDataProviderFor("/sports/de/sports.xml")
                .with(cfg)
                .with(deprecatedCfg)
                .with(httpFetcherType)
                .with(sportsApiDeserializer())
                .<SapiSportsEndpoint>build();

            apiSimulator.stubAllSportsWithEmptyErrorResponse(Locale.GERMAN);

            assertThatException()
                .isThrownBy(() -> provider.getData())
                .isInstanceOf(DataProviderException.class)
                .withRootCauseInstanceOf(ApiResponseHandlingException.class)
                .havingCause()
                .isInstanceOf(CommunicationException.class)
                .satisfies(ce -> {
                    val communicationException = (CommunicationException) ce;
                    assertThat(communicationException.getUrl())
                        .isEqualTo("http://" + apiBaseUrl.get() + "/v1/sports/de/sports.xml");
                    assertThat(communicationException.getHttpStatusCode()).isEqualTo(400);
                    assertThat(communicationException.getMessage()).contains("no message");
                });
        }

        @EnumSource(HttpFetcherType.class)
        @ParameterizedTest
        void throwsExceptionWhenDataProviderInitializedWithConfigurationWithoutTimeout(
            HttpFetcherType httpFetcherType
        ) throws Exception {
            val deprecatedCfg = internalConfigWithBothHttpTimeoutsSetToZero();
            val cfg = uofConfigWithBothHttpTimeoutsSetToZero();

            assertThatException()
                .isThrownBy(() ->
                    createDataProviderFor("/sports/de/sports.xml")
                        .with(deprecatedCfg)
                        .with(cfg)
                        .with(httpFetcherType)
                        .build()
                )
                .isInstanceOf(IllegalArgumentException.class)
                .withMessage("timeout cannot be 0");
        }

        private SdkInternalConfiguration internalConfigWithBothHttpTimeoutsSetToZero() {
            SdkInternalConfiguration cfg = mock(SdkInternalConfiguration.class);
            when(cfg.getHttpClientTimeout()).thenReturn(0);
            when(cfg.getFastHttpClientTimeout()).thenReturn(0L);
            return cfg;
        }

        private UofConfiguration uofConfigWithBothHttpTimeoutsSetToZero() {
            UofConfigurationStub config = new UofConfigurationStub();
            UofApiConfigurationStub apiConfig = (UofApiConfigurationStub) config.getApi();
            apiConfig.setHttpClientTimeout(Duration.ZERO);
            apiConfig.setHttpClientFastFailingTimeout(Duration.ZERO);
            return config;
        }

        private CapiResponse anyNonSapiSerializableXmlElement() {
            return new CapiResponse();
        }
    }

    private List<String> idAndNameListOf(SapiSportsEndpoint sapiResponse) {
        return sapiResponse.getSport().stream().map(s -> s.getId() + "-" + s.getName()).collect(toList());
    }

    private SdkInternalConfiguration internalConfigForApiWithTokenAnd1sClientTimeoutNoSslOn(
        String authorityOfUri
    ) {
        SdkInternalConfiguration cfg = internalConfigForApiWith10MaxConnectionsAnd1sClientTimeoutNoSslOn(
            authorityOfUri
        );
        when(cfg.getAccessToken()).thenReturn("some-token-1234");
        return cfg;
    }

    private UofConfiguration uofConfigForApiWithTokenAnd1sClientTimeoutNoSslOn(String authorityOfUri) {
        UofConfigurationStub config = uofConfigForApiWith1sClientTimeoutNoSslOn(authorityOfUri);
        config.setAccessToken("some-token-1234");
        return config;
    }

    private SdkInternalConfiguration internalConfigForApiWith10MaxConnectionsAnd1sClientTimeoutNoSslOn(
        String authorityOfUri
    ) {
        SdkInternalConfiguration cfg = internalConfigForApiWith1sClientTimeoutNoSslOn(authorityOfUri);
        when(cfg.getHttpClientMaxConnTotal()).thenReturn(10);
        when(cfg.getHttpClientMaxConnPerRoute()).thenReturn(10);
        return cfg;
    }

    private UofConfiguration uofConfigForApiWith10MaxConnectionsAnd1sClientTimeoutNoSslOn(
        String authorityOfUri
    ) {
        UofConfigurationStub config = uofConfigForApiWith1sClientTimeoutNoSslOn(authorityOfUri);
        UofApiConfigurationStub apiConfig = (UofApiConfigurationStub) config.getApi();
        apiConfig.setHttpClientMaxConnTotal(10);
        apiConfig.setHttpClientMaxConnPerRoute(10);
        return config;
    }

    private SdkInternalConfiguration internalConfigForApiWith1sClientTimeoutNoSslOn(String host) {
        SdkInternalConfiguration cfg = mock(SdkInternalConfiguration.class);
        when(cfg.getApiHostAndPort()).thenReturn(host);
        when(cfg.getUseApiSsl()).thenReturn(false);
        when(cfg.getHttpClientTimeout()).thenReturn(1);
        when(cfg.getFastHttpClientTimeout()).thenReturn(1L);
        return cfg;
    }

    private UofConfigurationStub uofConfigForApiWith1sClientTimeoutNoSslOn(String authorityOfUri) {
        UofApiConfigurationStub apiConfig = new UofApiConfigurationStub();
        apiConfig.setHttpClientTimeout(Duration.ofSeconds(1));
        apiConfig.setHttpClientFastFailingTimeout(Duration.ofSeconds(1));
        apiConfig.setUseSsl(false);
        setHostAndPort(from(authorityOfUri), to(apiConfig));
        UofConfigurationStub config = new UofConfigurationStub();
        config.setApi(apiConfig);
        return config;
    }
}
