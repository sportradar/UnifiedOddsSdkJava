/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */
package com.sportradar.unifiedodds.sdk.integrationtest.unifiedapi;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.wireMockConfig;
import static com.sportradar.utils.Urns.SportEvents.urnForAnyTournament;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.github.tomakehurst.wiremock.junit.WireMockRule;
import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Provides;
import com.google.inject.name.Named;
import com.sportradar.uf.custombet.datamodel.CAPIAvailableSelections;
import com.sportradar.uf.custombet.datamodel.CAPICalculationResponse;
import com.sportradar.uf.custombet.datamodel.CAPIFilteredCalculationResponse;
import com.sportradar.uf.sportsapi.datamodel.*;
import com.sportradar.unifiedodds.sdk.SDKInternalConfiguration;
import com.sportradar.unifiedodds.sdk.caching.DataRouter;
import com.sportradar.unifiedodds.sdk.caching.DataRouterManager;
import com.sportradar.unifiedodds.sdk.caching.impl.DataRouterManagerImpl;
import com.sportradar.unifiedodds.sdk.exceptions.internal.CommunicationException;
import com.sportradar.unifiedodds.sdk.impl.*;
import com.sportradar.utils.URN;
import java.util.Locale;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import org.apache.http.impl.client.HttpClientBuilder;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

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
        final URN tournamentUrn = urnForAnyTournament();
        wireMock.stubFor(
            get(urlPathEqualTo(tournamentScheduleFor(tournamentUrn, inEnglish))).willReturn(notFound())
        );

        assertThatThrownBy(() -> unifiedApi.requestEventsFor(inEnglish, tournamentUrn))
            .isInstanceOf(CommunicationException.class);
    }

    @Test
    public void nonParsableXmlShouldResultInCommunicationException() {
        final URN tournamentUrn = urnForAnyTournament();
        wireMock.stubFor(
            get(urlPathEqualTo(tournamentScheduleFor(tournamentUrn, inEnglish)))
                .willReturn(ok("non-xml-response"))
        );

        assertThatThrownBy(() -> unifiedApi.requestEventsFor(inEnglish, tournamentUrn))
            .isInstanceOf(CommunicationException.class);
    }

    private String tournamentScheduleFor(final URN tournamentUrn, final Locale language) {
        return String.format("/v1" + TOURNAMENT_SCHEDULE_PATH_FORMAT, language, tournamentUrn.toString());
    }

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
            bind(SDKInternalConfiguration.class).toInstance(mock(SDKInternalConfiguration.class));
            bind(SDKTaskScheduler.class).toInstance(mock(SDKTaskScheduler.class));
            bind(SDKProducerManager.class).toInstance(mock(SDKProducerManager.class));
            bind(DataRouter.class).toInstance(mock(DataRouter.class));
        }

        @Provides
        @Named("SummaryEndpointDataProvider")
        private DataProvider<Object> summaries() {
            return mock(DataProvider.class);
        }

        @Provides
        @Named("FixtureEndpointDataProvider")
        private DataProvider<SAPIFixturesEndpoint> fixtures() {
            return mock(DataProvider.class);
        }

        @Provides
        @Named("FixtureChangeFixtureEndpointDataProvider")
        private DataProvider<SAPIFixturesEndpoint> fixtureChangeFixtures() {
            return mock(DataProvider.class);
        }

        @Provides
        private DataProvider<SAPITournamentsEndpoint> tournaments() {
            return mock(DataProvider.class);
        }

        @Provides
        @Named("DateScheduleEndpointDataProvider")
        private DataProvider<SAPIScheduleEndpoint> schedulesByDate() {
            return mock(DataProvider.class);
        }

        @Provides
        @Named("TournamentScheduleProvider")
        private DataProvider<Object> tournamentSchedules() {
            return new DataProvider<>(
                TOURNAMENT_SCHEDULE_PATH_FORMAT,
                configWiremockHostAndEnglishByDefault(),
                new LogHttpDataFetcher(
                    configWithToken(),
                    HttpClientBuilder.create().build(),
                    mock(UnifiedOddsStatistics.class),
                    deserializer
                ),
                deserializer
            );
        }

        private SDKInternalConfiguration configWithToken() {
            SDKInternalConfiguration config = mock(SDKInternalConfiguration.class);
            when(config.getAccessToken()).thenReturn("someToken");
            return config;
        }

        private SDKInternalConfiguration configWiremockHostAndEnglishByDefault() {
            SDKInternalConfiguration config = mock(SDKInternalConfiguration.class);
            when(config.getApiHostAndPort()).thenReturn(apiHost);
            when(config.getUseApiSsl()).thenReturn(false);
            when(config.getDefaultLocale()).thenReturn(Locale.ENGLISH);
            return config;
        }

        @Provides
        private DataProvider<SAPISportsEndpoint> sports() {
            return mock(DataProvider.class);
        }

        @Provides
        private DataProvider<SAPIPlayerProfileEndpoint> playerProfiles() {
            return mock(DataProvider.class);
        }

        @Provides
        private DataProvider<SAPICompetitorProfileEndpoint> competitorProfiles() {
            return mock(DataProvider.class);
        }

        @Provides
        private DataProvider<SAPISimpleTeamProfileEndpoint> simpleTeamProfiles() {
            return mock(DataProvider.class);
        }

        @Provides
        private DataProvider<SAPITournamentSeasons> tournamentSeasons() {
            return mock(DataProvider.class);
        }

        @Provides
        private DataProvider<SAPIMatchTimelineEndpoint> matchTimelines() {
            return mock(DataProvider.class);
        }

        @Provides
        private DataProvider<SAPISportCategoriesEndpoint> sportCategories() {
            return mock(DataProvider.class);
        }

        @Provides
        private DataProvider<SAPIDrawSummary> drawSummaries() {
            return mock(DataProvider.class);
        }

        @Provides
        private DataProvider<SAPIDrawFixtures> drawFixtures() {
            return mock(DataProvider.class);
        }

        @Provides
        private DataProvider<SAPILotteries> lotteries() {
            return mock(DataProvider.class);
        }

        @Provides
        private DataProvider<SAPILotterySchedule> lotterySchedules() {
            return mock(DataProvider.class);
        }

        @Provides
        private DataProvider<CAPIAvailableSelections> availableSelections() {
            return mock(DataProvider.class);
        }

        @Provides
        private DataProvider<CAPICalculationResponse> calculations() {
            return mock(DataProvider.class);
        }

        @Provides
        private DataProvider<CAPIFilteredCalculationResponse> filteredCalculations() {
            return mock(DataProvider.class);
        }

        @Provides
        private DataProvider<SAPIFixtureChangesEndpoint> fixtureChanges() {
            return mock(DataProvider.class);
        }

        @Provides
        private DataProvider<SAPIResultChangesEndpoint> resultChanges() {
            return mock(DataProvider.class);
        }

        @Provides
        @Named("ListSportEventsDataProvider")
        private DataProvider<SAPIScheduleEndpoint> schedules() {
            return mock(DataProvider.class);
        }

        @Provides
        private DataProvider<SAPISportTournamentsEndpoint> sportTournaments() {
            return mock(DataProvider.class);
        }

        @Provides
        private DataProvider<SAPIStagePeriodEndpoint> stagePeriods() {
            return mock(DataProvider.class);
        }
    }
}
