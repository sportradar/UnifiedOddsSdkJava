/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */

package com.sportradar.unifiedodds.sdk.internal.impl;

import static com.github.tomakehurst.wiremock.client.WireMock.exactly;
import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.wireMockConfig;
import static com.sportradar.unifiedodds.sdk.CapiCustomBet.getCalculationWithHarmonization;
import static com.sportradar.unifiedodds.sdk.conn.ApiSimulator.ApiStubDelay.toBeDelayedBy;
import static com.sportradar.unifiedodds.sdk.conn.ApiSimulator.BodyCondition.forRequestBody;
import static com.sportradar.unifiedodds.sdk.conn.ApiSimulator.HeaderEquality.*;
import static com.sportradar.unifiedodds.sdk.conn.SapiTournaments.Euro2024.euro2024TournamentInfo;
import static com.sportradar.unifiedodds.sdk.internal.cfg.TestConfigHelper.setHostAndPort;
import static com.sportradar.unifiedodds.sdk.internal.commoniam.OAuth2TokenCacheFixtures.*;
import static com.sportradar.unifiedodds.sdk.internal.impl.DataProviders.createDataProviderFor;
import static com.sportradar.unifiedodds.sdk.internal.impl.Deserializers.customBetApiDeserializer;
import static com.sportradar.unifiedodds.sdk.internal.impl.Deserializers.sportsApiDeserializer;
import static com.sportradar.unifiedodds.sdk.testutil.generic.naturallanguage.Prepositions.*;
import static java.time.temporal.ChronoUnit.SECONDS;
import static java.util.Locale.ENGLISH;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

import com.github.tomakehurst.wiremock.common.ConsoleNotifier;
import com.github.tomakehurst.wiremock.junit5.WireMockExtension;
import com.sportradar.uf.custombet.datamodel.CapiCalculationResponse;
import com.sportradar.uf.custombet.datamodel.CapiSelectionType;
import com.sportradar.uf.custombet.datamodel.CapiSelections;
import com.sportradar.uf.sportsapi.datamodel.BookmakerDetails;
import com.sportradar.uf.sportsapi.datamodel.SapiTournamentExtended;
import com.sportradar.uf.sportsapi.datamodel.SapiTournamentsEndpoint;
import com.sportradar.unifiedodds.sdk.LoggerDefinitions;
import com.sportradar.unifiedodds.sdk.cfg.UofApiConfigurationStub;
import com.sportradar.unifiedodds.sdk.cfg.UofConfigurationStub;
import com.sportradar.unifiedodds.sdk.cfg.UofPrivateKeyJwtAuthenticationStub;
import com.sportradar.unifiedodds.sdk.conn.ApiSimulator;
import com.sportradar.unifiedodds.sdk.internal.commoniam.OAuth2Token;
import com.sportradar.unifiedodds.sdk.internal.commoniam.OAuth2TokenCache;
import com.sportradar.unifiedodds.sdk.internal.exceptions.DataProviderException;
import com.sportradar.unifiedodds.sdk.internal.impl.DataProviders.HttpFetcherType;
import com.sportradar.unifiedodds.sdk.internal.impl.rabbitconnection.LogsMock;
import com.sportradar.unifiedodds.sdk.testutil.rabbit.integration.BaseUrl;
import com.sportradar.utils.Urns;
import java.time.Duration;
import java.util.List;
import java.util.stream.Collectors;
import lombok.val;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

@SuppressWarnings(
    { "ClassFanOutComplexity", "ConstantName", "MagicNumber", "IllegalCatch", "MultipleStringLiterals" }
)
public class DataProviderHeadersTest {

    @RegisterExtension
    private static final WireMockExtension wireMock = WireMockExtension
        .newInstance()
        .options(wireMockConfig().dynamicPort().notifier(new ConsoleNotifier(true)))
        .build();

    private static final String SPORTS_EN_TOURNAMENTS_URL = "/sports/en/tournaments.xml";
    private static final String CUSTOM_BET_CALCULATE_URL = "/custombet/calculate";
    private static final String TRACE_HEADER_NAME = "trace-id";
    private static final String USERS_WHOAMI_XML = "/users/whoami.xml";
    private static final String V1_PATH_PREFIX_REQUIRED_FOR_ASSERTIONS_ONLY = "/v1";

    private final LogsMock logs = LogsMock.createCapturingFor(LoggerDefinitions.UfSdkRestTrafficLog.class);

    private BaseUrl apiBaseUrl;
    private ApiSimulator apiSimulator;

    @BeforeEach
    void initTestContext() {
        apiBaseUrl = BaseUrl.of("localhost", wireMock.getPort());
        apiSimulator = new ApiSimulator(wireMock.getRuntimeInfo().getWireMock());
    }

    @Nested
    public class AuthorizationRelated {

        @Test
        void clientAuthorizationIsPreferredAuthenticationMethodOverAccessToken() throws Exception {
            val config = uofConfigurationWith1sClientTimeoutNoSslAndUnifiedApiOn(apiBaseUrl.get());
            config.setClientAuthentication(new UofPrivateKeyJwtAuthenticationStub());
            config.setAccessToken("some-access-token");

            val deprecatedCfg = internalConfigWith1sClientTimeoutNoSslAndUnifiedApiOn(apiBaseUrl.get());

            val tokenCache = providingBearerToken("abc123");
            DataProvider<BookmakerDetails> provider = createDataProviderFor(USERS_WHOAMI_XML)
                .with(config)
                .with(deprecatedCfg)
                .with(sportsApiDeserializer())
                .with(tokenCache)
                .build();
            apiSimulator.defineBookmaker(
                requiringHeader("Authorization", "Bearer abc123"),
                requiringNoHeader("x-access-token")
            );

            assertThat(provider.getData().getBookmakerId()).isNotNull();
        }

        @Test
        void sendsOAuthAuthorizationHeaderWhenClientAuthenticationIsConfigured() throws Exception {
            val config = uofConfigurationWith1sClientTimeoutNoSslAndUnifiedApiOn(apiBaseUrl.get());
            config.setClientAuthentication(new UofPrivateKeyJwtAuthenticationStub());
            val deprecatedCfg = internalConfigWith1sClientTimeoutNoSslAndUnifiedApiOn(apiBaseUrl.get());

            val tokenCache = providingBearerToken("abc123");
            DataProvider<BookmakerDetails> provider = createDataProviderFor(USERS_WHOAMI_XML)
                .with(config)
                .with(deprecatedCfg)
                .with(sportsApiDeserializer())
                .with(tokenCache)
                .build();
            apiSimulator.defineBookmaker(requiringHeader("Authorization", "Bearer abc123"));

            assertThat(provider.getData().getBookmakerId()).isNotNull();
        }

        @Test
        void attemptingToGetTokenFailsWithDataProviderExceptionWrapperAroundOAuthHttpException() {
            val config = uofConfigurationWith1sClientTimeoutNoSslAndUnifiedApiOn(apiBaseUrl.get());
            config.setClientAuthentication(new UofPrivateKeyJwtAuthenticationStub());
            val deprecatedCfg = internalConfigWith1sClientTimeoutNoSslAndUnifiedApiOn(apiBaseUrl.get());

            val failingTokenCache = failingWithOAuth2TokenRetrievalHttpException("/oauth/token", 500);

            DataProvider<BookmakerDetails> provider = createDataProviderFor(USERS_WHOAMI_XML)
                .with(config)
                .with(deprecatedCfg)
                .with(sportsApiDeserializer())
                .with(failingTokenCache)
                .build();

            assertThatThrownBy(provider::getData)
                .isInstanceOf(DataProviderException.class)
                .satisfies(exception -> {
                    val dpe = (DataProviderException) exception;
                    assertThat(dpe.getMessage())
                        .contains("The requested data was not accessible on the provided URL");
                    assertThat(dpe.tryExtractCommunicationExceptionHttpStatusCode(400)).isEqualTo(500);
                    assertThat(dpe.tryExtractCommunicationExceptionUrl("")).isEqualTo("/oauth/token");
                })
                .hasRootCauseInstanceOf(OAuth2TokenCache.OAuth2TokenRetrievalHttpException.class);
        }

        @Test
        void attemptingToGetTokenFailsWithDataProviderExceptionWrapperAroundOAuthException() {
            val config = uofConfigurationWith1sClientTimeoutNoSslAndUnifiedApiOn(apiBaseUrl.get());
            config.setClientAuthentication(new UofPrivateKeyJwtAuthenticationStub());
            val deprecatedCfg = internalConfigWith1sClientTimeoutNoSslAndUnifiedApiOn(apiBaseUrl.get());
            val failingTokenCache = failingWithOAuth2TokenRetrievalException();

            DataProvider<BookmakerDetails> provider = createDataProviderFor(USERS_WHOAMI_XML)
                .with(config)
                .with(deprecatedCfg)
                .with(sportsApiDeserializer())
                .with(failingTokenCache)
                .build();
            assertThatThrownBy(provider::getData)
                .isInstanceOf(DataProviderException.class)
                .hasRootCauseInstanceOf(OAuth2TokenCache.OAuth2TokenRetrievalException.class);
        }

        @Test
        void doesNotSendAuthorizationHeaderWhenClientAuthenticationIsNotConfigured() throws Exception {
            val config = uofConfigurationWith1sClientTimeoutNoSslAndUnifiedApiOn(apiBaseUrl.get());
            config.setClientAuthentication(null);
            val deprecatedCfg = internalConfigWith1sClientTimeoutNoSslAndUnifiedApiOn(apiBaseUrl.get());

            val tokenCache = mock(OAuth2TokenCache.class);
            DataProvider<BookmakerDetails> provider = createDataProviderFor(USERS_WHOAMI_XML)
                .with(config)
                .with(deprecatedCfg)
                .with(sportsApiDeserializer())
                .with(tokenCache)
                .build();
            apiSimulator.defineBookmaker(requiringNoHeader("Authorization"));

            assertThat(provider.getData().getBookmakerId()).isNotNull();

            verify(tokenCache, never()).getToken();
        }

        @Test
        void sendsAccessTokenHeader() throws Exception {
            val config = uofConfigurationWith1sClientTimeoutNoSslAndUnifiedApiOn(apiBaseUrl.get());
            val deprecatedCfg = internalConfigWith1sClientTimeoutNoSslAndUnifiedApiOn(apiBaseUrl.get());
            config.setAccessToken("some-token-1234");
            DataProvider<BookmakerDetails> provider = createDataProviderFor(USERS_WHOAMI_XML)
                .with(config)
                .with(deprecatedCfg)
                .with(sportsApiDeserializer())
                .build();
            apiSimulator.defineBookmaker(requiringHeader("x-access-token", "some-token-1234"));

            val bookmaker = provider.getData();

            assertThat(bookmaker.getBookmakerId()).isNotNull();
        }

        @Test
        void doesNotSendAccessTokenHeaderWhenAccessTokenIsNotConfigured() throws Exception {
            val config = uofConfigurationWith1sClientTimeoutNoSslAndUnifiedApiOn(apiBaseUrl.get());
            val deprecatedCfg = internalConfigWith1sClientTimeoutNoSslAndUnifiedApiOn(apiBaseUrl.get());
            config.setAccessToken(null);
            DataProvider<BookmakerDetails> provider = createDataProviderFor(USERS_WHOAMI_XML)
                .with(config)
                .with(deprecatedCfg)
                .with(sportsApiDeserializer())
                .build();
            apiSimulator.defineBookmaker(requiringNoHeader("x-access-token"));

            assertThat(provider.getData().getBookmakerId()).isNotNull();
        }

        @Nested
        class Retries {

            @Test
            void apiGetRequestRejectedWith401FailsAfterOneRetryWithNewTokenWhichAlsoResultsIn401() {
                val config = uofConfigurationWith1sClientTimeoutNoSslAndUnifiedApiOn(apiBaseUrl.get());
                config.setClientAuthentication(new UofPrivateKeyJwtAuthenticationStub());
                val deprecatedCfg = internalConfigWith1sClientTimeoutNoSslAndUnifiedApiOn(apiBaseUrl.get());

                val tokenCache = builder()
                    .providingBearerToken("abc123")
                    .afterFirstInvalidationProviding("newToken")
                    .build();
                DataProvider<SapiTournamentsEndpoint> provider = createDataProviderFor(
                    SPORTS_EN_TOURNAMENTS_URL
                )
                    .with(config)
                    .with(deprecatedCfg)
                    .with(sportsApiDeserializer())
                    .with(tokenCache)
                    .build();
                apiSimulator.stubAllTournamentsWithUnauthorizedErrorResponse(in(ENGLISH));

                assertThatThrownBy(provider::getData).isInstanceOf(DataProviderException.class);

                apiSimulator.verifyAllTournamentsCalled(
                    exactly(1),
                    with(ENGLISH),
                    requiringHeader("Authorization", "Bearer abc123")
                );
                apiSimulator.verifyAllTournamentsCalled(
                    exactly(1),
                    with(ENGLISH),
                    requiringHeader("Authorization", "Bearer newToken")
                );
            }

            @Test
            void apiGetRequestRejectedWith401SucceedsAfterOneRetry() throws Exception {
                val config = uofConfigurationWith1sClientTimeoutNoSslAndUnifiedApiOn(apiBaseUrl.get());
                config.setClientAuthentication(new UofPrivateKeyJwtAuthenticationStub());
                val deprecatedCfg = internalConfigWith1sClientTimeoutNoSslAndUnifiedApiOn(apiBaseUrl.get());

                val tokenCache = builder()
                    .providingBearerToken("firstToken")
                    .afterFirstInvalidationProviding("secondToken")
                    .build();
                DataProvider<SapiTournamentsEndpoint> provider = createDataProviderFor(
                    SPORTS_EN_TOURNAMENTS_URL
                )
                    .with(config)
                    .with(deprecatedCfg)
                    .with(sportsApiDeserializer())
                    .with(tokenCache)
                    .build();
                val tournament = euro2024TournamentInfo().getTournament();
                apiSimulator.stubAllTournamentsWithUnauthorizedErrorResponse(
                    in(ENGLISH),
                    requiringHeader("Authorization", "Bearer firstToken")
                );
                apiSimulator.stubAllTournaments(
                    in(ENGLISH),
                    tournament,
                    requiringHeader("Authorization", "Bearer secondToken")
                );

                val actual = provider.getData(ENGLISH);

                assertThat(actual.getTournament()).extracting("id").containsOnly(tournament.getId());
            }

            @Test
            void apiPostRequestRejectedWith401FailsAfterOneRetryWithNewTokenWhichAlsoResultsIn401() {
                val config = uofConfigurationWith1sClientTimeoutNoSslAndUnifiedApiOn(apiBaseUrl.get());
                config.setClientAuthentication(new UofPrivateKeyJwtAuthenticationStub());
                val deprecatedCfg = internalConfigWith1sClientTimeoutNoSslAndUnifiedApiOn(apiBaseUrl.get());

                val tokenCache = builder()
                    .providingBearerToken("firstCbToken")
                    .afterFirstInvalidationProviding("secondCbToken")
                    .build();
                DataProvider<SapiTournamentsEndpoint> provider = createDataProviderFor(
                    CUSTOM_BET_CALCULATE_URL
                )
                    .with(config)
                    .with(deprecatedCfg)
                    .with(customBetApiDeserializer())
                    .with(tokenCache)
                    .build();

                apiSimulator.stubCustomBetCalculateWithUnauthorizedResponse();

                assertThatThrownBy(() -> provider.postData(new CapiSelections()))
                    .isInstanceOf(DataProviderException.class);

                apiSimulator.verifyCustomBetCalculateCalled(
                    exactly(1),
                    requiringHeader("Authorization", "Bearer firstCbToken")
                );
                apiSimulator.verifyCustomBetCalculateCalled(
                    exactly(1),
                    requiringHeader("Authorization", "Bearer secondCbToken")
                );
            }

            @Test
            void apiPostRequestRejectedWith401SucceedsAfterOneRetry() throws Exception {
                val config = uofConfigurationWith1sClientTimeoutNoSslAndUnifiedApiOn(apiBaseUrl.get());
                config.setClientAuthentication(new UofPrivateKeyJwtAuthenticationStub());
                val deprecatedCfg = internalConfigWith1sClientTimeoutNoSslAndUnifiedApiOn(apiBaseUrl.get());

                val tokenCache = builder()
                    .providingBearerToken("firstCbToken")
                    .afterFirstInvalidationProviding("secondCbToken")
                    .build();
                DataProvider<CapiCalculationResponse> provider = createDataProviderFor(
                    CUSTOM_BET_CALCULATE_URL
                )
                    .with(config)
                    .with(deprecatedCfg)
                    .with(customBetApiDeserializer())
                    .with(tokenCache)
                    .build();

                apiSimulator.stubCustomBetCalculateWithUnauthorizedResponse(
                    requiringHeader("Authorization", "Bearer firstCbToken")
                );
                val selections = customBetSelections();
                apiSimulator.stubCustomBetCalculate(
                    getCalculationWithHarmonization(true),
                    forRequestBody(selections),
                    requiringHeader("Authorization", "Bearer secondCbToken")
                );

                val actual = provider.postData(selections);

                assertThat(actual.getCalculation().getOdds())
                    .isEqualTo(getCalculationWithHarmonization(true).getCalculation().getOdds());
            }

            private OAuth2Token withAccessToken(String expected) {
                return argThat(t -> t.getAccessToken().equals(expected));
            }

            public CapiSelections customBetSelections() {
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
    }

    @Nested
    public class TraceId {

        @EnumSource(HttpFetcherType.class)
        @ParameterizedTest
        void sendTraceIdHeaderOnSuccessfulRequest(HttpFetcherType httpFetcherType) throws Exception {
            val cfg = uofConfigurationWith1sClientTimeoutNoSslAndUnifiedApiOn(apiBaseUrl.get());
            val deprecatedCfg = internalConfigWith1sClientTimeoutNoSslAndUnifiedApiOn(apiBaseUrl.get());
            val provider = createDataProviderFor(SPORTS_EN_TOURNAMENTS_URL)
                .with(cfg)
                .with(deprecatedCfg)
                .with(httpFetcherType)
                .with(sportsApiDeserializer())
                .<SapiTournamentsEndpoint>build();

            val tournament = euro2024TournamentInfo().getTournament();
            stubTournamentRequiringTraceIdUuidHeader(tournament);

            val result = provider.getData(ENGLISH);
            assertThat(result).isNotNull();

            val traceId = getTheOnlyTraceIdForAllTournamentsRequest();
            logs.verifyLoggedLineContaining(traceId);
        }

        @EnumSource(HttpFetcherType.class)
        @ParameterizedTest
        void sendTraceIdHeaderOnFailedRequest(HttpFetcherType httpFetcherType) {
            val cfg = uofConfigurationWith1sClientTimeoutNoSslAndUnifiedApiOn(apiBaseUrl.get());
            val deprecatedCfg = internalConfigWith1sClientTimeoutNoSslAndUnifiedApiOn(apiBaseUrl.get());
            val provider = createDataProviderFor(SPORTS_EN_TOURNAMENTS_URL)
                .with(cfg)
                .with(deprecatedCfg)
                .with(httpFetcherType)
                .with(sportsApiDeserializer())
                .<SapiTournamentsEndpoint>build();

            apiSimulator.stubAllTournamentsWithBadRequestErrorResponse(
                ENGLISH,
                requiringHeaderWithAnyUuidValue(TRACE_HEADER_NAME)
            );

            assertThatThrownBy(() -> provider.getData(ENGLISH)).isInstanceOf(DataProviderException.class);

            val traceId = getTheOnlyTraceIdForAllTournamentsRequest();
            logs.verifyLoggedLineContaining(traceId);
        }

        @EnumSource(HttpFetcherType.class)
        @ParameterizedTest
        void sendTraceIdHeaderOnTimeoutRequest(HttpFetcherType httpFetcherType) {
            val cfg = uofConfigurationWith1sClientTimeoutNoSslAndUnifiedApiOn(apiBaseUrl.get());
            val deprecatedCfg = internalConfigWith1sClientTimeoutNoSslAndUnifiedApiOn(apiBaseUrl.get());
            val provider = createDataProviderFor(SPORTS_EN_TOURNAMENTS_URL)
                .with(cfg)
                .with(deprecatedCfg)
                .with(httpFetcherType)
                .with(sportsApiDeserializer())
                .<SapiTournamentsEndpoint>build();

            apiSimulator.stubApiGetRequest(
                SPORTS_EN_TOURNAMENTS_URL,
                requiringHeaderWithAnyUuidValue(TRACE_HEADER_NAME),
                toBeDelayedBy(cfg.getApi().getHttpClientTimeout().getSeconds() + 1, SECONDS)
            );

            assertThatThrownBy(() -> provider.getData(ENGLISH)).isInstanceOf(DataProviderException.class);

            val traceId = getTheOnlyTraceIdForAllTournamentsRequest();
            logs.verifyLoggedLineContaining(traceId);
        }

        @EnumSource(HttpFetcherType.class)
        @ParameterizedTest
        void sendNewTraceIdHeaderPerRequest(HttpFetcherType httpFetcherType) throws Exception {
            val cfg = uofConfigurationWith1sClientTimeoutNoSslAndUnifiedApiOn(apiBaseUrl.get());
            val deprecatedCfg = internalConfigWith1sClientTimeoutNoSslAndUnifiedApiOn(apiBaseUrl.get());
            val provider = createDataProviderFor(SPORTS_EN_TOURNAMENTS_URL)
                .with(cfg)
                .with(deprecatedCfg)
                .with(httpFetcherType)
                .with(sportsApiDeserializer())
                .<SapiTournamentsEndpoint>build();

            val tournament = euro2024TournamentInfo().getTournament();
            stubTournamentRequiringTraceIdUuidHeader(tournament);
            val tournamentData1 = provider.getData(ENGLISH);
            stubTournamentRequiringTraceIdUuidHeader(tournament);
            val tournamentData2 = provider.getData(ENGLISH);

            val traceIds = getTraceIdsForAllTournamentsRequests();
            assertThat(traceIds.stream().distinct()).hasSize(traceIds.size());

            assertThat(tournamentData1).isNotNull();
            assertThat(tournamentData2).isNotNull();
            traceIds.forEach(logs::verifyLoggedLineContaining);
        }
    }

    private static List<String> getTraceIdsForAllTournamentsRequests() {
        val events = wireMock.getAllServeEvents();
        return events
            .stream()
            .filter(e -> e.getRequest().getUrl().equals(SPORTS_EN_TOURNAMENTS_URL))
            .map(e -> e.getRequest().getHeader("trace-id"))
            .collect(Collectors.toList());
    }

    private static String getTheOnlyTraceIdForAllTournamentsRequest() {
        val events = wireMock.getAllServeEvents();
        val traceIds = events
            .stream()
            .filter(e ->
                e
                    .getRequest()
                    .getUrl()
                    .equals(V1_PATH_PREFIX_REQUIRED_FOR_ASSERTIONS_ONLY + SPORTS_EN_TOURNAMENTS_URL)
            )
            .map(e -> e.getRequest().getHeader("trace-id"))
            .collect(Collectors.toList());
        assertThat(traceIds).hasSize(1);
        return traceIds.get(0);
    }

    private void stubTournamentRequiringTraceIdUuidHeader(SapiTournamentExtended tournament) {
        apiSimulator.stubAllTournaments(
            in(ENGLISH),
            tournament,
            requiringHeaderWithAnyUuidValue(TRACE_HEADER_NAME)
        );
    }

    private SdkInternalConfiguration internalConfigWith1sClientTimeoutNoSslAndUnifiedApiOn(
        String authorityOfUri
    ) {
        val cfg = mock(SdkInternalConfiguration.class);
        when(cfg.getApiHostAndPort()).thenReturn(authorityOfUri);
        when(cfg.getUseApiSsl()).thenReturn(false);
        when(cfg.getHttpClientTimeout()).thenReturn(1);
        when(cfg.getFastHttpClientTimeout()).thenReturn(1L);
        return cfg;
    }

    private UofConfigurationStub uofConfigurationWith1sClientTimeoutNoSslAndUnifiedApiOn(
        String authorityOfUri
    ) {
        val apiConfig = new UofApiConfigurationStub();
        apiConfig.setHttpClientTimeout(Duration.ofSeconds(1));
        apiConfig.setHttpClientFastFailingTimeout(Duration.ofSeconds(1));
        apiConfig.setUseSsl(false);
        setHostAndPort(from(authorityOfUri), to(apiConfig));
        UofConfigurationStub config = new UofConfigurationStub();
        config.setApi(apiConfig);
        return config;
    }
}
