package com.sportradar.unifiedodds.sdk.di;

import com.google.inject.Binder;
import com.google.inject.Module;
import com.google.inject.Provides;
import com.google.inject.Singleton;
import com.google.inject.name.Named;
import com.sportradar.uf.sportsapi.datamodel.MarketDescriptions;
import com.sportradar.unifiedodds.sdk.SportsInfoManager;
import com.sportradar.unifiedodds.sdk.impl.DataProvider;
import com.sportradar.unifiedodds.sdk.impl.SDKProducerManager;
import com.sportradar.unifiedodds.sdk.impl.TestingDataProvider;
import com.sportradar.unifiedodds.sdk.impl.apireaders.WhoAmIReader;
import org.mockito.Mockito;

public class TestingModule implements Module {
    @Override
    public void configure(Binder binder) {
        SDKProducerManager stubProducerManager = Mockito.mock(SDKProducerManager.class);
        WhoAmIReader stubWhoAmIReader = Mockito.mock(WhoAmIReader.class);
        SportsInfoManager stubSportsInfoManager = Mockito.mock(SportsInfoManager.class);

        binder.bind(SDKProducerManager.class).toInstance(stubProducerManager);
        binder.bind(WhoAmIReader.class).toInstance(stubWhoAmIReader);
        binder.bind(SportsInfoManager.class).toInstance(stubSportsInfoManager);
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
}
