package com.sportradar.unifiedodds.sdk.internal.di;

import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.google.inject.name.Named;
import com.sportradar.uf.sportsapi.datamodel.*;
import com.sportradar.unifiedodds.sdk.internal.impl.*;

/**
 * Created on 2019-03-29
 *
 * @author e.roznik
 */
@SuppressWarnings({ "LineLength", "MultipleStringLiterals" })
public class DataProvidersModule extends AbstractModule {

    @Override
    protected void configure() {
        // empty
    }

    @Provides
    @Named("SummaryEndpointDataProvider")
    private ExecutionPathDataProvider<Object> provideSummaryEndpointDataProvider(
        @Named(
            "TimeCriticalSummaryEndpointDataProvider"
        ) DataProvider<Object> timeCriticalSummaryDataprovider,
        @Named(
            "NonTimeCriticalSummaryEndpointDataProvider"
        ) DataProvider<Object> nonTimeCriticalSummaryDataprovider
    ) {
        return new ExecutionPathDataProvider<>(
            timeCriticalSummaryDataprovider,
            nonTimeCriticalSummaryDataprovider
        );
    }

    @Provides
    @Named("TimeCriticalSummaryEndpointDataProvider")
    private DataProvider<Object> provideSummaryEndpointDataProviderTimeCritical(
        SdkInternalConfiguration cfg,
        LogFastHttpDataFetcher httpDataFetcher,
        @Named("SportsApiJaxbDeserializer") Deserializer deserializer
    ) {
        String nodeIdStr = getNodeIdQueryString(cfg);

        String replaySummary = baseUrl(cfg) + "/replay/sports/%s/sport_events/%s/summary.xml" + nodeIdStr;

        return new DataProvider<>(
            cfg.isReplaySession() ? replaySummary : "/sports/%s/sport_events/%s/summary.xml",
            cfg,
            httpDataFetcher,
            deserializer
        );
    }

    @Provides
    @Named("NonTimeCriticalSummaryEndpointDataProvider")
    private DataProvider<Object> provideSummaryEndpointDataProviderNonTimeCritical(
        SdkInternalConfiguration cfg,
        LogHttpDataFetcher httpDataFetcher,
        @Named("SportsApiJaxbDeserializer") Deserializer deserializer
    ) {
        String nodeIdStr = getNodeIdQueryString(cfg);

        String replaySummary = baseUrl(cfg) + "/replay/sports/%s/sport_events/%s/summary.xml" + nodeIdStr;

        return new DataProvider<>(
            cfg.isReplaySession() ? replaySummary : "/sports/%s/sport_events/%s/summary.xml",
            cfg,
            httpDataFetcher,
            deserializer
        );
    }

    @Provides
    @Named("FixtureEndpointDataProvider")
    private DataProvider<SapiFixturesEndpoint> provideFixtureEndpointDataProvider(
        SdkInternalConfiguration cfg,
        LogHttpDataFetcher httpDataFetcher,
        @Named("SportsApiJaxbDeserializer") Deserializer deserializer
    ) {
        return fixturesEndpointProvider(cfg, httpDataFetcher, deserializer, "fixture.xml");
    }

    @Provides
    @Named("FixtureChangeFixtureEndpointDataProvider")
    private DataProvider<SapiFixturesEndpoint> provideFixtureChangeFixtureEndpointDataProvider(
        SdkInternalConfiguration cfg,
        LogHttpDataFetcher httpDataFetcher,
        @Named("SportsApiJaxbDeserializer") Deserializer deserializer
    ) {
        return fixturesEndpointProvider(cfg, httpDataFetcher, deserializer, "fixture_change_fixture.xml");
    }

    @Provides
    private DataProvider<SapiFixtureChangesEndpoint> provideFixtureChangesDataProvider(
        SdkInternalConfiguration cfg,
        LogHttpDataFetcher httpDataFetcher,
        @Named("SportsApiJaxbDeserializer") Deserializer deserializer
    ) {
        return new DataProvider<>("/sports/%s/fixtures/changes.xml%s", cfg, httpDataFetcher, deserializer);
    }

    @Provides
    private DataProvider<SapiResultChangesEndpoint> provideResultChangesDataProvider(
        SdkInternalConfiguration cfg,
        LogHttpDataFetcher httpDataFetcher,
        @Named("SportsApiJaxbDeserializer") Deserializer deserializer
    ) {
        return new DataProvider<>("/sports/%s/results/changes.xml%s", cfg, httpDataFetcher, deserializer);
    }

    @Provides
    private DataProvider<SapiTournamentsEndpoint> provideAllTournamentsEndpointDataProvider(
        SdkInternalConfiguration cfg,
        LogHttpDataFetcher httpDataFetcher,
        @Named("SportsApiJaxbDeserializer") Deserializer deserializer
    ) {
        return new DataProvider<>("/sports/%s/tournaments.xml", cfg, httpDataFetcher, deserializer);
    }

    @Provides
    private DataProvider<SapiSportsEndpoint> provideSportsEndpointDataProvider(
        SdkInternalConfiguration cfg,
        LogHttpDataFetcher httpDataFetcher,
        @Named("SportsApiJaxbDeserializer") Deserializer deserializer
    ) {
        return new DataProvider<>("/sports/%s/sports.xml", cfg, httpDataFetcher, deserializer);
    }

    @Provides
    @Named("DateScheduleEndpointDataProvider")
    private DataProvider<SapiScheduleEndpoint> provideDateScheduleEndpointDataProvider(
        SdkInternalConfiguration cfg,
        LogHttpDataFetcher httpDataFetcher,
        @Named("SportsApiJaxbDeserializer") Deserializer deserializer
    ) {
        return new DataProvider<>("/sports/%s/schedules/%s/schedule.xml", cfg, httpDataFetcher, deserializer);
    }

    @Provides
    @Named("TournamentScheduleProvider")
    private DataProvider<Object> provideTournamentScheduleEndpointDataProvider(
        SdkInternalConfiguration cfg,
        LogHttpDataFetcher httpDataFetcher,
        @Named("SportsApiJaxbDeserializer") Deserializer deserializer
    ) {
        return new DataProvider<>(
            "/sports/%s/tournaments/%s/schedule.xml",
            cfg,
            httpDataFetcher,
            deserializer
        );
    }

    @Provides
    private DataProvider<SapiPlayerProfileEndpoint> providePlayerProfileEndpointDataProvider(
        SdkInternalConfiguration cfg,
        LogFastHttpDataFetcher httpDataFetcher,
        @Named("SportsApiJaxbDeserializer") Deserializer deserializer
    ) {
        return new DataProvider<>("/sports/%s/players/%s/profile.xml", cfg, httpDataFetcher, deserializer);
    }

    @Provides
    private DataProvider<SapiCompetitorProfileEndpoint> provideCompetitorProfileEndpointDataProvider(
        SdkInternalConfiguration cfg,
        LogFastHttpDataFetcher httpDataFetcher,
        @Named("SportsApiJaxbDeserializer") Deserializer deserializer
    ) {
        return new DataProvider<>(
            "/sports/%s/competitors/%s/profile.xml",
            cfg,
            httpDataFetcher,
            deserializer
        );
    }

    @Provides
    private DataProvider<SapiSimpleTeamProfileEndpoint> provideSimpleTeamProfileEndpointDataProvider(
        SdkInternalConfiguration cfg,
        LogFastHttpDataFetcher httpDataFetcher,
        @Named("SportsApiJaxbDeserializer") Deserializer deserializer
    ) {
        return new DataProvider<>(
            "/sports/%s/competitors/%s/profile.xml",
            cfg,
            httpDataFetcher,
            deserializer
        );
    }

    @Provides
    private DataProvider<SapiTournamentSeasons> provideTournamentSeasonsEndpointDataProvider(
        SdkInternalConfiguration cfg,
        LogHttpDataFetcher httpDataFetcher,
        @Named("SportsApiJaxbDeserializer") Deserializer deserializer
    ) {
        return new DataProvider<>(
            "/sports/%s/tournaments/%s/seasons.xml",
            cfg,
            httpDataFetcher,
            deserializer
        );
    }

    @Provides
    private DataProvider<SapiMatchTimelineEndpoint> provideMatchTimelineEndpointDataProvider(
        SdkInternalConfiguration cfg,
        LogHttpDataFetcher httpDataFetcher,
        @Named("SportsApiJaxbDeserializer") Deserializer deserializer
    ) {
        String nodeIdStr = getNodeIdQueryString(cfg);

        String replayTimeline = baseUrl(cfg) + "/replay/sports/%s/sport_events/%s/timeline.xml" + nodeIdStr;

        return new DataProvider<>(
            cfg.isReplaySession() ? replayTimeline : "/sports/%s/sport_events/%s/timeline.xml",
            cfg,
            httpDataFetcher,
            deserializer
        );
    }

    @Provides
    private DataProvider<SapiSportCategoriesEndpoint> provideSportCategoriesEndpointProvider(
        SdkInternalConfiguration cfg,
        LogHttpDataFetcher httpDataFetcher,
        @Named("SportsApiJaxbDeserializer") Deserializer deserializer
    ) {
        return new DataProvider<>("/sports/%s/sports/%s/categories.xml", cfg, httpDataFetcher, deserializer);
    }

    @Provides
    private DataProvider<SapiLotteries> provideLotteriesDataProvider(
        SdkInternalConfiguration cfg,
        LogHttpDataFetcher httpDataFetcher,
        @Named("SportsApiJaxbDeserializer") Deserializer deserializer
    ) {
        return new DataProvider<>("/wns/%s/lotteries.xml", cfg, httpDataFetcher, deserializer);
    }

    @Provides
    private DataProvider<SapiDrawSummary> provideDrawSummaryProvider(
        SdkInternalConfiguration cfg,
        LogHttpDataFetcher httpDataFetcher,
        @Named("SportsApiJaxbDeserializer") Deserializer deserializer
    ) {
        return new DataProvider<>("/wns/%s/sport_events/%s/summary.xml", cfg, httpDataFetcher, deserializer);
    }

    @Provides
    private DataProvider<SapiDrawFixtures> provideDrawFixtureProvider(
        SdkInternalConfiguration cfg,
        LogHttpDataFetcher httpDataFetcher,
        @Named("SportsApiJaxbDeserializer") Deserializer deserializer
    ) {
        return new DataProvider<>("/wns/%s/sport_events/%s/fixture.xml", cfg, httpDataFetcher, deserializer);
    }

    @Provides
    private DataProvider<SapiLotterySchedule> provideLotteryScheduleProvider(
        SdkInternalConfiguration cfg,
        LogHttpDataFetcher httpDataFetcher,
        @Named("SportsApiJaxbDeserializer") Deserializer deserializer
    ) {
        return new DataProvider<>("/wns/%s/lotteries/%s/schedule.xml", cfg, httpDataFetcher, deserializer);
    }

    @Provides
    @Named("ListSportEventsDataProvider")
    private DataProvider<SapiScheduleEndpoint> provideListSportEventsProvider(
        SdkInternalConfiguration cfg,
        LogHttpDataFetcher httpDataFetcher,
        @Named("SportsApiJaxbDeserializer") Deserializer deserializer
    ) {
        return new DataProvider<>(
            "/sports/%s/schedules/pre/schedule.xml?start=%s&limit=%s",
            cfg,
            httpDataFetcher,
            deserializer
        );
    }

    @Provides
    private DataProvider<SapiSportTournamentsEndpoint> provideSportTournamentsEndpointDataProvider(
        SdkInternalConfiguration cfg,
        LogHttpDataFetcher httpDataFetcher,
        @Named("SportsApiJaxbDeserializer") Deserializer deserializer
    ) {
        return new DataProvider<>("/sports/%s/sports/%s/tournaments.xml", cfg, httpDataFetcher, deserializer);
    }

    @Provides
    private DataProvider<SapiStagePeriodEndpoint> periodSummaryDataProvider(
        SdkInternalConfiguration cfg,
        LogHttpDataFetcher httpDataFetcher,
        @Named("SportsApiJaxbDeserializer") Deserializer deserializer
    ) {
        //host/v1/sports/en/sport_events/sr:stage:{id}/period_summary.xml?competitors=sr:competitor:{id}&competitors=sr:competitor:{id}&periods=2&periods=3&periods=4
        return new DataProvider<>(
            "/sports/%s/sport_events/%s/period_summary.xml%s",
            cfg,
            httpDataFetcher,
            deserializer
        );
    }

    //Helpers:

    private static DataProvider<SapiFixturesEndpoint> fixturesEndpointProvider(
        SdkInternalConfiguration cfg,
        LogHttpDataFetcher httpDataFetcher,
        Deserializer deserializer,
        String filename
    ) {
        String nodeIdStr = getNodeIdQueryString(cfg);

        String replayFixture = baseUrl(cfg) + "/replay/sports/%s/sport_events/%s/fixture.xml" + nodeIdStr;

        return new DataProvider<>(
            cfg.isReplaySession() ? replayFixture : "/sports/%s/sport_events/%s/" + filename,
            cfg,
            httpDataFetcher,
            deserializer
        );
    }

    private static String baseUrl(SdkInternalConfiguration cfg) {
        String httpHttps = cfg.getUseApiSsl() ? "https" : "http";
        return httpHttps + "://" + cfg.getApiHostAndPort() + "/v1";
    }

    private static String getNodeIdQueryString(SdkInternalConfiguration cfg) {
        return cfg.getSdkNodeId() != null && cfg.getSdkNodeId() != 0 ? "?node_id=" + cfg.getSdkNodeId() : "";
    }
}
