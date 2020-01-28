package com.sportradar.unifiedodds.sdk.di;

import com.google.inject.Binder;
import com.google.inject.Module;
import com.google.inject.Provides;
import com.google.inject.Singleton;
import com.google.inject.name.Named;
import com.sportradar.uf.sportsapi.datamodel.MarketDescriptions;
import com.sportradar.uf.sportsapi.datamodel.SAPICompetitorProfileEndpoint;
import com.sportradar.unifiedodds.sdk.SportsInfoManager;
import com.sportradar.unifiedodds.sdk.impl.DataProvider;
import com.sportradar.unifiedodds.sdk.impl.SDKProducerManager;
import com.sportradar.unifiedodds.sdk.impl.TestingDataProvider;
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
        SDKProducerManager stubProducerManager = Mockito.mock(SDKProducerManager.class);
        WhoAmIReader stubWhoAmIReader = Mockito.mock(WhoAmIReader.class);

        binder.bind(SDKProducerManager.class).toInstance(stubProducerManager);
        binder.bind(WhoAmIReader.class).toInstance(stubWhoAmIReader);

        sportsInfoManager.ifPresent(
                value -> binder.bind(SportsInfoManager.class).toInstance(value)
        );
    }

    @Provides @Singleton
    public DataProvider<MarketDescriptions> providesMarketsDataProvider() {
        return new TestingDataProvider("test/rest/invariant_market_descriptions.en.xml");
    }

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
}
