/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */
package com.sportradar.unifiedodds.sdk.integrationtest.unifiedapi;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.wireMockConfig;
import static com.sportradar.unifiedodds.sdk.internal.cfg.TestConfigHelper.setHostAndPort;
import static com.sportradar.unifiedodds.sdk.testutil.generic.naturallanguage.Prepositions.from;
import static com.sportradar.utils.Urns.SportEvents.urnForAnyTournament;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.github.tomakehurst.wiremock.junit.WireMockRule;
import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Provides;
import com.google.inject.name.Named;
import com.sportradar.uf.custombet.datamodel.CapiAvailableSelections;
import com.sportradar.uf.custombet.datamodel.CapiCalculationResponse;
import com.sportradar.uf.custombet.datamodel.CapiFilteredCalculationResponse;
import com.sportradar.uf.sportsapi.datamodel.*;
import com.sportradar.unifiedodds.sdk.cfg.UofApiConfigurationStub;
import com.sportradar.unifiedodds.sdk.cfg.UofConfiguration;
import com.sportradar.unifiedodds.sdk.cfg.UofConfigurationStub;
import com.sportradar.unifiedodds.sdk.exceptions.CommunicationException;
import com.sportradar.unifiedodds.sdk.internal.caching.DataRouter;
import com.sportradar.unifiedodds.sdk.internal.caching.DataRouterManager;
import com.sportradar.unifiedodds.sdk.internal.caching.impl.DataRouterManagerImpl;
import com.sportradar.unifiedodds.sdk.internal.common.telemetry.TelemetryFactory;
import com.sportradar.unifiedodds.sdk.internal.commoniam.OAuth2TokenCache;
import com.sportradar.unifiedodds.sdk.internal.impl.*;
import com.sportradar.utils.Urn;
import java.time.Duration;
import java.util.Locale;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import lombok.val;
import org.apache.hc.client5.http.impl.async.HttpAsyncClientBuilder;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

@SuppressWarnings("MagicNumber")
public class DataRouterManagerImplRequestingScheduledSportEventIdsForTournamentIT {

    private static final String TOURNAMENT_SCHEDULE_PATH_FORMAT = "/sports/%s/tournaments/%s/schedule.xml";

    @Rule
    public final WireMockRule wireMock = new WireMockRule(wireMockConfig().dynamicPort());

    private final Locale inEnglish = Locale.ENGLISH;
    private DataRouterManager unifiedApi;

    @Before
    public void setUpForTournamentScheduleCalls() throws JAXBException {
        final String wiremockHost = "localhost:" + wireMock.port();
        unifiedApi =
            Guice
                .createInjector(new TournamentScheduleDaoModule(wiremockHost))
                .getInstance(DataRouterManagerImpl.class);
    }

    @Test
    public void genericScheduleNotFoundCaseShouldBeTreatedAsCommunicationIssue() {
        final Urn tournamentUrn = urnForAnyTournament();
        wireMock.stubFor(
            get(urlPathEqualTo(tournamentScheduleFor(tournamentUrn, inEnglish))).willReturn(notFound())
        );

        assertThatThrownBy(() -> unifiedApi.requestEventsFor(inEnglish, tournamentUrn))
            .isInstanceOf(CommunicationException.class);
    }

    @Test
    public void nonParsableXmlShouldResultInCommunicationException() {
        final Urn tournamentUrn = urnForAnyTournament();
        wireMock.stubFor(
            get(urlPathEqualTo(tournamentScheduleFor(tournamentUrn, inEnglish)))
                .willReturn(ok("non-xml-response"))
        );

        assertThatThrownBy(() -> unifiedApi.requestEventsFor(inEnglish, tournamentUrn))
            .isInstanceOf(CommunicationException.class);
    }

    private String tournamentScheduleFor(final Urn tournamentUrn, final Locale language) {
        return String.format("/v1" + TOURNAMENT_SCHEDULE_PATH_FORMAT, language, tournamentUrn.toString());
    }

    @SuppressWarnings("ClassFanOutComplexity")
    public static class TournamentScheduleDaoModule extends AbstractModule {

        private final String apiHost;
        private final DeserializerImpl deserializer = new DeserializerImpl(
            JAXBContext.newInstance("com.sportradar.uf.sportsapi.datamodel")
        );

        private TournamentScheduleDaoModule(final String apiHost) throws JAXBException {
            this.apiHost = apiHost;
        }

        @Override
        protected void configure() {
            bind(SdkInternalConfiguration.class).toInstance(mock(SdkInternalConfiguration.class));
            bind(SdkTaskScheduler.class).toInstance(mock(SdkTaskScheduler.class));
            bind(SdkProducerManager.class).toInstance(mock(SdkProducerManager.class));
            bind(DataRouter.class).toInstance(mock(DataRouter.class));
        }

        @Provides
        @Named("SummaryEndpointDataProvider")
        private ExecutionPathDataProvider<Object> summaries() {
            return mock(ExecutionPathDataProvider.class);
        }

        @Provides
        @Named("FixtureEndpointDataProvider")
        private DataProvider<SapiFixturesEndpoint> fixtures() {
            return mock(DataProvider.class);
        }

        @Provides
        @Named("FixtureChangeFixtureEndpointDataProvider")
        private DataProvider<SapiFixturesEndpoint> fixtureChangeFixtures() {
            return mock(DataProvider.class);
        }

        @Provides
        private DataProvider<SapiTournamentsEndpoint> tournaments() {
            return mock(DataProvider.class);
        }

        @Provides
        @Named("DateScheduleEndpointDataProvider")
        private DataProvider<SapiScheduleEndpoint> schedulesByDate() {
            return mock(DataProvider.class);
        }

        @Provides
        @Named("TournamentScheduleProvider")
        private DataProvider<Object> tournamentSchedules() {
            val cfg = uofConfigWithToken5sNormalAnd1sFastTimeoutsWiremockHostAndEnglish();
            val deprecatedCfg = internalConfigWithToken5sNormalAnd1sFastTimeoutsWiremockHostAndEnglish();
            return new DataProvider<>(
                TOURNAMENT_SCHEDULE_PATH_FORMAT,
                deprecatedCfg,
                new LogHttpDataFetcher(
                    cfg,
                    HttpAsyncClientBuilder.create().build(),
                    mock(UnifiedOddsStatistics.class),
                    new HttpResponseHandler(),
                    mock(UserAgentProvider.class),
                    mock(TraceIdProvider.class),
                    mock(OAuth2TokenCache.class)
                ),
                deserializer
            );
        }

        private SdkInternalConfiguration internalConfigWithToken5sNormalAnd1sFastTimeoutsWiremockHostAndEnglish() {
            SdkInternalConfiguration config = mock(SdkInternalConfiguration.class);
            when(config.getApiHostAndPort()).thenReturn(apiHost);
            when(config.getUseApiSsl()).thenReturn(false);
            when(config.getHttpClientTimeout()).thenReturn(5000);
            when(config.getFastHttpClientTimeout()).thenReturn(1000L);
            when(config.getDefaultLocale()).thenReturn(Locale.ENGLISH);
            when(config.getAccessToken()).thenReturn("someToken");
            return config;
        }

        private UofConfiguration uofConfigWithToken5sNormalAnd1sFastTimeoutsWiremockHostAndEnglish() {
            UofConfigurationStub config = new UofConfigurationStub();
            UofApiConfigurationStub apiConfig = (UofApiConfigurationStub) config.getApi();
            apiConfig.setHttpClientTimeout(Duration.ofSeconds(5));
            apiConfig.setHttpClientFastFailingTimeout(Duration.ofSeconds(1));
            setHostAndPort(from(apiHost), apiConfig);
            apiConfig.setUseSsl(false);
            config.setDefaultLanguage(Locale.ENGLISH);
            config.setAccessToken("someToken");
            return config;
        }

        @Provides
        private DataProvider<SapiSportsEndpoint> sports() {
            return mock(DataProvider.class);
        }

        @Provides
        private DataProvider<SapiPlayerProfileEndpoint> playerProfiles() {
            return mock(DataProvider.class);
        }

        @Provides
        private DataProvider<SapiCompetitorProfileEndpoint> competitorProfiles() {
            return mock(DataProvider.class);
        }

        @Provides
        private DataProvider<SapiSimpleTeamProfileEndpoint> simpleTeamProfiles() {
            return mock(DataProvider.class);
        }

        @Provides
        private DataProvider<SapiTournamentSeasons> tournamentSeasons() {
            return mock(DataProvider.class);
        }

        @Provides
        private DataProvider<SapiMatchTimelineEndpoint> matchTimelines() {
            return mock(DataProvider.class);
        }

        @Provides
        private DataProvider<SapiSportCategoriesEndpoint> sportCategories() {
            return mock(DataProvider.class);
        }

        @Provides
        private DataProvider<SapiDrawSummary> drawSummaries() {
            return mock(DataProvider.class);
        }

        @Provides
        private DataProvider<SapiDrawFixtures> drawFixtures() {
            return mock(DataProvider.class);
        }

        @Provides
        private DataProvider<SapiLotteries> lotteries() {
            return mock(DataProvider.class);
        }

        @Provides
        private DataProvider<SapiLotterySchedule> lotterySchedules() {
            return mock(DataProvider.class);
        }

        @Provides
        private DataProvider<CapiAvailableSelections> availableSelections() {
            return mock(DataProvider.class);
        }

        @Provides
        private DataProvider<CapiCalculationResponse> calculations() {
            return mock(DataProvider.class);
        }

        @Provides
        private DataProvider<CapiFilteredCalculationResponse> filteredCalculations() {
            return mock(DataProvider.class);
        }

        @Provides
        private DataProvider<SapiFixtureChangesEndpoint> fixtureChanges() {
            return mock(DataProvider.class);
        }

        @Provides
        private DataProvider<SapiResultChangesEndpoint> resultChanges() {
            return mock(DataProvider.class);
        }

        @Provides
        @Named("ListSportEventsDataProvider")
        private DataProvider<SapiScheduleEndpoint> schedules() {
            return mock(DataProvider.class);
        }

        @Provides
        private DataProvider<SapiSportTournamentsEndpoint> sportTournaments() {
            return mock(DataProvider.class);
        }

        @Provides
        private DataProvider<SapiStagePeriodEndpoint> stagePeriods() {
            return mock(DataProvider.class);
        }

        @Provides
        @Named("InternalSdkTelemetryFactory")
        private TelemetryFactory provideTelemetryFactory() {
            return mock(TelemetryFactory.class);
        }
    }
}
