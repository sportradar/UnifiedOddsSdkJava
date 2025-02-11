package com.sportradar.unifiedodds.sdk.internal.conn;

import com.google.common.collect.ImmutableMap;
import com.google.inject.Binder;
import com.google.inject.Module;
import com.google.inject.Provides;
import com.google.inject.Singleton;
import com.google.inject.name.Named;
import com.sportradar.uf.sportsapi.datamodel.*;
import com.sportradar.unifiedodds.sdk.internal.impl.*;
import com.sportradar.unifiedodds.sdk.internal.impl.apireaders.HttpHelper;
import com.sportradar.unifiedodds.sdk.internal.impl.apireaders.MessageAndActionExtractor;
import com.sportradar.unifiedodds.sdk.internal.impl.apireaders.WhoAmIReader;
import com.sportradar.unifiedodds.sdk.internal.shared.TestHttpHelper;
import com.sportradar.unifiedodds.sdk.managers.SportDataProvider;
import java.util.Optional;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.mockito.Mockito;

@SuppressWarnings({ "ClassFanOutComplexity" })
public class SdkTestModule implements Module {

    private Optional<SportDataProvider> sportDataProvider;

    public SdkTestModule() {}

    public SdkTestModule(Optional<SportDataProvider> sportDataProvider) {
        this.sportDataProvider = sportDataProvider;
    }

    @Override
    public void configure(Binder binder) {
        WhoAmIReader stubWhoAmIReader = Mockito.mock(WhoAmIReader.class);

        binder.bind(WhoAmIReader.class).toInstance(stubWhoAmIReader);
    }

    /**
     * Provides the http client used to fetch data from the API
     */
    @Provides
    @Singleton
    @Named("RecoveryHttpHelper")
    private HttpHelper provideRecoveryHttpHelper(
        SdkInternalConfiguration config,
        @Named("RecoveryHttpClient") CloseableHttpClient httpClient,
        MessageAndActionExtractor messageExtractor,
        UserAgentProvider userAgentProvider
    ) {
        return new TestHttpHelper(config, httpClient, messageExtractor, userAgentProvider);
    }

    @Provides
    @Singleton
    public DataProvider<Producers> providesProducersDataProvider() {
        return new TestingDataProvider("test/rest/producers_conn.xml");
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
