package com.sportradar.unifiedodds.sdk.di;

import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.google.inject.name.Named;
import com.sportradar.uf.sportsapi.datamodel.*;
import com.sportradar.unifiedodds.sdk.SDKInternalConfiguration;
import com.sportradar.unifiedodds.sdk.impl.DataProvider;
import com.sportradar.unifiedodds.sdk.impl.Deserializer;
import com.sportradar.unifiedodds.sdk.impl.LogHttpDataFetcher;
import com.sportradar.unifiedodds.sdk.impl.UnifiedFeedConstants;

/**
 * Created on 2019-03-29
 *
 * @author e.roznik
 */
public class DataProvidersModule extends AbstractModule {

    @Override
    protected void configure() {
        // empty
    }

    @Provides
    @Named("SummaryEndpointDataProvider")
    private DataProvider<Object> provideSummaryEndpointDataProvider(SDKInternalConfiguration cfg,
                                                                    LogHttpDataFetcher httpDataFetcher,
                                                                    @Named("SportsApiJaxbDeserializer") Deserializer deserializer) {
        String nodeIdStr = cfg.getSdkNodeId() != null && cfg.getSdkNodeId() != 0
                ? "?node_id=" + cfg.getSdkNodeId()
                : "";

        String httpHttps = cfg.getUseApiSsl() ? "https" : "http";
        String replaySummary = httpHttps + "://" + UnifiedFeedConstants.PRODUCTION_API_HOST + "/v1/replay/sports/%s/sport_events/%s/summary.xml" + nodeIdStr;

        return new DataProvider<>(
                cfg.isReplaySession()
                        ? replaySummary
                        : "/sports/%s/sport_events/%s/summary.xml",
                cfg,
                httpDataFetcher,
                deserializer
        );
    }

    @Provides @Named("FixtureEndpointDataProvider")
    private DataProvider<SAPIFixturesEndpoint> provideFixtureEndpointDataProvider(SDKInternalConfiguration cfg,
                                                                                  LogHttpDataFetcher httpDataFetcher,
                                                                                  @Named("SportsApiJaxbDeserializer") Deserializer deserializer) {
        return new DataProvider<>(
                "/sports/%s/sport_events/%s/fixture.xml",
                cfg,
                httpDataFetcher,
                deserializer
        );
    }

    @Provides @Named("FixtureChangeFixtureEndpointDataProvider")
    private DataProvider<SAPIFixturesEndpoint> provideFixtureChangeFixtureEndpointDataProvider(SDKInternalConfiguration cfg,
                                                                                               LogHttpDataFetcher httpDataFetcher,
                                                                                               @Named("SportsApiJaxbDeserializer") Deserializer deserializer) {
        return new DataProvider<>(
                "/sports/%s/sport_events/%s/fixture_change_fixture.xml",
                cfg,
                httpDataFetcher,
                deserializer
        );
    }

    @Provides
    private DataProvider<SAPIFixtureChangesEndpoint> provideFixtureChangesDataProvider(SDKInternalConfiguration cfg,
                                                                                                     LogHttpDataFetcher httpDataFetcher,
                                                                                                     @Named("SportsApiJaxbDeserializer") Deserializer deserializer) {
        return new DataProvider<>(
                "/sports/%s/fixtures/changes.xml",
                cfg,
                httpDataFetcher,
                deserializer
        );
    }

    @Provides
    private DataProvider<SAPITournamentsEndpoint> provideAllTournamentsEndpointDataProvider(SDKInternalConfiguration cfg,
                                                                                            LogHttpDataFetcher httpDataFetcher,
                                                                                            @Named("SportsApiJaxbDeserializer") Deserializer deserializer) {
        return new DataProvider<>(
                "/sports/%s/tournaments.xml",
                cfg,
                httpDataFetcher,
                deserializer
        );
    }

    @Provides
    private DataProvider<SAPISportsEndpoint> provideSportsEndpointDataProvider(SDKInternalConfiguration cfg,
                                                                               LogHttpDataFetcher httpDataFetcher,
                                                                               @Named("SportsApiJaxbDeserializer") Deserializer deserializer) {
        return new DataProvider<>(
                "/sports/%s/sports.xml",
                cfg,
                httpDataFetcher,
                deserializer
        );
    }

    @Provides @Named("DateScheduleEndpointDataProvider")
    private DataProvider<SAPIScheduleEndpoint> provideDateScheduleEndpointDataProvider(SDKInternalConfiguration cfg,
                                                                                       LogHttpDataFetcher httpDataFetcher,
                                                                                       @Named("SportsApiJaxbDeserializer") Deserializer deserializer) {
        return new DataProvider<>(
                "/sports/%s/schedules/%s/schedule.xml",
                cfg,
                httpDataFetcher,
                deserializer
        );
    }

    @Provides @Named("TournamentScheduleProvider")
    private DataProvider<Object> provideTournamentScheduleEndpointDataProvider(SDKInternalConfiguration cfg,
                                                                               LogHttpDataFetcher httpDataFetcher,
                                                                               @Named("SportsApiJaxbDeserializer") Deserializer deserializer) {
        return new DataProvider<>(
                "/sports/%s/tournaments/%s/schedule.xml",
                cfg,
                httpDataFetcher,
                deserializer
        );
    }

    @Provides
    private DataProvider<SAPIPlayerProfileEndpoint> providePlayerProfileEndpointDataProvider(SDKInternalConfiguration cfg,
                                                                                             LogHttpDataFetcher httpDataFetcher,
                                                                                             @Named("SportsApiJaxbDeserializer") Deserializer deserializer) {
        return new DataProvider<>(
                "/sports/%s/players/%s/profile.xml",
                cfg,
                httpDataFetcher,
                deserializer
        );
    }

    @Provides
    private DataProvider<SAPICompetitorProfileEndpoint> provideCompetitorProfileEndpointDataProvider(SDKInternalConfiguration cfg,
                                                                                                     LogHttpDataFetcher httpDataFetcher,
                                                                                                     @Named("SportsApiJaxbDeserializer") Deserializer deserializer) {
        return new DataProvider<>(
                "/sports/%s/competitors/%s/profile.xml",
                cfg,
                httpDataFetcher,
                deserializer
        );
    }

    @Provides
    private DataProvider<SAPISimpleTeamProfileEndpoint> provideSimpleTeamProfileEndpointDataProvider(SDKInternalConfiguration cfg,
                                                                                                     LogHttpDataFetcher httpDataFetcher,
                                                                                                     @Named("SportsApiJaxbDeserializer") Deserializer deserializer) {
        return new DataProvider<>(
                "/sports/%s/competitors/%s/profile.xml",
                cfg,
                httpDataFetcher,
                deserializer
        );
    }

    @Provides
    private DataProvider<SAPITournamentSeasons> provideTournamentSeasonsEndpointDataProvider(SDKInternalConfiguration cfg,
                                                                                             LogHttpDataFetcher httpDataFetcher,
                                                                                             @Named("SportsApiJaxbDeserializer") Deserializer deserializer) {
        return new DataProvider<>(
                "/sports/%s/tournaments/%s/seasons.xml",
                cfg,
                httpDataFetcher,
                deserializer
        );
    }

    @Provides
    private DataProvider<SAPIMatchTimelineEndpoint> provideMatchTimelineEndpointDataProvider(SDKInternalConfiguration cfg,
                                                                                             LogHttpDataFetcher httpDataFetcher,
                                                                                             @Named("SportsApiJaxbDeserializer") Deserializer deserializer) {
        return new DataProvider<>(
                "/sports/%s/sport_events/%s/timeline.xml",
                cfg,
                httpDataFetcher,
                deserializer
        );
    }

    @Provides
    private DataProvider<SAPISportCategoriesEndpoint> provideSportCategoriesEndpointProvider(SDKInternalConfiguration cfg,
                                                                                             LogHttpDataFetcher httpDataFetcher,
                                                                                             @Named("SportsApiJaxbDeserializer") Deserializer deserializer) {
        return new DataProvider<>(
                "/sports/%s/sports/%s/categories.xml",
                cfg,
                httpDataFetcher,
                deserializer
        );
    }

    @Provides
    private DataProvider<SAPILotteries> provideLotteriesDataProvider(SDKInternalConfiguration cfg,
                                                                     LogHttpDataFetcher httpDataFetcher,
                                                                     @Named("SportsApiJaxbDeserializer") Deserializer deserializer) {
        return new DataProvider<>(
                "/wns/sports/%s/lotteries.xml",
                cfg,
                httpDataFetcher,
                deserializer
        );
    }

    @Provides
    private DataProvider<SAPIDrawSummary> provideDrawSummaryProvider(SDKInternalConfiguration cfg,
                                                                     LogHttpDataFetcher httpDataFetcher,
                                                                     @Named("SportsApiJaxbDeserializer") Deserializer deserializer) {
        return new DataProvider<>(
                "/wns/sports/%s/sport_events/%s/summary.xml",
                cfg,
                httpDataFetcher,
                deserializer
        );
    }

    @Provides
    private DataProvider<SAPIDrawFixtures> provideDrawFixtureProvider(SDKInternalConfiguration cfg,
                                                                      LogHttpDataFetcher httpDataFetcher,
                                                                      @Named("SportsApiJaxbDeserializer") Deserializer deserializer) {
        return new DataProvider<>(
                "/wns/sports/%s/sport_events/%s/fixture.xml",
                cfg,
                httpDataFetcher,
                deserializer
        );
    }

    @Provides
    private DataProvider<SAPILotterySchedule> provideLotteryScheduleProvider(SDKInternalConfiguration cfg,
                                                                             LogHttpDataFetcher httpDataFetcher,
                                                                             @Named("SportsApiJaxbDeserializer") Deserializer deserializer) {
        return new DataProvider<>(
                "/wns/sports/%s/lotteries/%s/schedule.xml",
                cfg,
                httpDataFetcher,
                deserializer
        );
    }

    @Provides @Named("ListSportEventsDataProvider")
    private DataProvider<SAPIScheduleEndpoint> provideListSportEventsProvider(SDKInternalConfiguration cfg,
                                                                              LogHttpDataFetcher httpDataFetcher,
                                                                              @Named("SportsApiJaxbDeserializer") Deserializer deserializer) {
        return new DataProvider<>(
                "/sports/%s/schedules/pre/schedule.xml?start=%s&limit=%s",
                cfg,
                httpDataFetcher,
                deserializer
        );
    }
}
