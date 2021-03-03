package com.sportradar.unifiedodds.sdk.di;

import com.google.common.collect.ImmutableMap;
import com.google.inject.Binder;
import com.google.inject.Module;
import com.google.inject.Provides;
import com.google.inject.Singleton;
import com.google.inject.name.Named;
import com.sportradar.uf.sportsapi.datamodel.*;
import com.sportradar.unifiedodds.sdk.SportsInfoManager;
import com.sportradar.unifiedodds.sdk.impl.DataProvider;
import com.sportradar.unifiedodds.sdk.impl.TestingDataProvider;
import com.sportradar.unifiedodds.sdk.impl.TestingSummaryDataProvider;
import com.sportradar.unifiedodds.sdk.impl.apireaders.WhoAmIReader;
import org.mockito.Mockito;

import java.util.Optional;

public class TestingModule implements Module {
    private Optional<SportsInfoManager> sportsInfoManager;

    public TestingModule() {
        this(Optional.of(Mockito.mock(SportsInfoManager.class)));
    }

    public TestingModule(Optional<SportsInfoManager> sportsInfoManager) {
        this.sportsInfoManager = sportsInfoManager;
    }

    @Override
    public void configure(Binder binder) {
        WhoAmIReader stubWhoAmIReader = Mockito.mock(WhoAmIReader.class);

        binder.bind(WhoAmIReader.class).toInstance(stubWhoAmIReader);

        sportsInfoManager.ifPresent(
                value -> binder.bind(SportsInfoManager.class).toInstance(value)
        );
    }

    @Provides @Singleton
    public DataProvider<Producers> providesProducersDataProvider() {
        return new TestingDataProvider("test/rest/producers.xml");
    }

    @Provides @Singleton
    public DataProvider<MarketDescriptions> providesMarketsDataProvider() { return new TestingDataProvider("test/rest/invariant_market_descriptions.en.xml"); }

    @Provides @Singleton @Named("BettingStatusDataProvider")
    protected DataProvider providesBettingStatusDataProvider() {
        return new TestingDataProvider("test/rest/betting_status.xml");
    }

    @Provides @Singleton @Named("BetStopReasonDataProvider")
    protected DataProvider providesBetStopReasonDataProvider() {
        return new TestingDataProvider("test/rest/betstop_reasons.xml");
    }

    @Provides @Singleton
    protected DataProvider<SAPICompetitorProfileEndpoint> providesCompetitorProfileEndpointProvider() {
        return new TestingDataProvider<>("test/rest/profiles/en.competitor.3700.xml");
    }

    @Provides @Singleton @Named("ListSportEventsDataProvider")
    protected DataProvider<SAPIScheduleEndpoint> providesScheduleEndpointProvider() {
        return new TestingDataProvider<>("test/rest/events.xml");
    }

    @Provides @Singleton @Named("TournamentScheduleProvider")
    protected DataProvider<Object> providesTournamentScheduleProvider() {
        return new TestingDataProvider<>("test/rest/tournament_schedule.en.xml");
    }

    @Provides @Singleton @Named("DateScheduleEndpointDataProvider")
    protected DataProvider<SAPIScheduleEndpoint> providesDateScheduleProvider() {
        return new TestingDataProvider<>("test/rest/schedule.en.xml");
    }

    @Provides @Singleton @Named("SummaryEndpointDataProvider")
    protected DataProvider<Object> providesSummaryEndpointProvider() {
        return new TestingSummaryDataProvider<>(ImmutableMap.of(
                "match", "test/rest/match_summary.xml",
                "stage", "test/rest/race_summary.xml",
                "tournament", "test/rest/summaries/summary_sr_tournament_1030.en.xml",
                "tournament40", "test/rest/tournament_info.xml"
        ));
    }

    @Provides @Singleton
    protected DataProvider<SAPILotterySchedule> providesLotteryScheduleProvider() {
        return new TestingDataProvider<>("test/rest/wns/lottery_schedule.en.xml");
    }
}
