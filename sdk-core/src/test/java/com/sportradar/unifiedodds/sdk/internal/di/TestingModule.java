package com.sportradar.unifiedodds.sdk.internal.di;

import com.google.common.collect.ImmutableMap;
import com.google.inject.Binder;
import com.google.inject.Module;
import com.google.inject.Provides;
import com.google.inject.Singleton;
import com.google.inject.name.Named;
import com.sportradar.uf.sportsapi.datamodel.*;
import com.sportradar.unifiedodds.sdk.internal.impl.DataProvider;
import com.sportradar.unifiedodds.sdk.internal.impl.TestingDataProvider;
import com.sportradar.unifiedodds.sdk.internal.impl.TestingSummaryDataProvider;
import com.sportradar.unifiedodds.sdk.internal.impl.apireaders.WhoAmIReader;
import com.sportradar.unifiedodds.sdk.managers.SportDataProvider;
import java.util.Optional;
import org.mockito.Mockito;

public class TestingModule implements Module {

    private Optional<SportDataProvider> sportDataProvider;

    public TestingModule() {
        this(Optional.of(Mockito.mock(SportDataProvider.class)));
    }

    public TestingModule(Optional<SportDataProvider> sportDataProvider) {
        this.sportDataProvider = sportDataProvider;
    }

    @Override
    public void configure(Binder binder) {
        WhoAmIReader stubWhoAmIReader = Mockito.mock(WhoAmIReader.class);

        binder.bind(WhoAmIReader.class).toInstance(stubWhoAmIReader);

        sportDataProvider.ifPresent(value -> binder.bind(SportDataProvider.class).toInstance(value));
    }

    @Provides
    @Singleton
    public DataProvider<Producers> providesProducersDataProvider() {
        return new TestingDataProvider("test/rest/producers.xml");
    }

    @Provides
    @Singleton
    public DataProvider<MarketDescriptions> providesMarketsDataProvider() {
        return new TestingDataProvider("test/rest/invariant_market_descriptions.en.xml");
    }

    @Provides
    @Singleton
    @Named("BettingStatusDataProvider")
    protected DataProvider providesBettingStatusDataProvider() {
        return new TestingDataProvider("test/rest/betting_status.xml");
    }

    @Provides
    @Singleton
    @Named("BetStopReasonDataProvider")
    protected DataProvider providesBetStopReasonDataProvider() {
        return new TestingDataProvider("test/rest/betstop_reasons.xml");
    }

    @Provides
    @Singleton
    protected DataProvider<SapiCompetitorProfileEndpoint> providesCompetitorProfileEndpointProvider() {
        return new TestingDataProvider<>("test/rest/profiles/en.competitor.3700.xml");
    }

    @Provides
    @Singleton
    @Named("ListSportEventsDataProvider")
    protected DataProvider<SapiScheduleEndpoint> providesScheduleEndpointProvider() {
        return new TestingDataProvider<>("test/rest/events.xml");
    }

    @Provides
    @Singleton
    @Named("TournamentScheduleProvider")
    protected DataProvider<Object> providesTournamentScheduleProvider() {
        return new TestingDataProvider<>("test/rest/tournament_schedule.en.xml");
    }

    @Provides
    @Singleton
    @Named("DateScheduleEndpointDataProvider")
    protected DataProvider<SapiScheduleEndpoint> providesDateScheduleProvider() {
        return new TestingDataProvider<>("test/rest/schedule.en.xml");
    }

    @Provides
    @Singleton
    @Named("SummaryEndpointDataProvider")
    protected DataProvider<Object> providesSummaryEndpointProvider() {
        return new TestingSummaryDataProvider<>(
            ImmutableMap.of(
                "match",
                "test/rest/match_summary.xml",
                "stage",
                "test/rest/race_summary.xml",
                "tournament",
                "test/rest/summaries/summary_sr_tournament_1030.en.xml",
                "tournament40",
                "test/rest/tournament_info.xml"
            )
        );
    }

    @Provides
    @Singleton
    protected DataProvider<SapiLotterySchedule> providesLotteryScheduleProvider() {
        return new TestingDataProvider<>("test/rest/wns/lottery_schedule.en.xml");
    }
}
