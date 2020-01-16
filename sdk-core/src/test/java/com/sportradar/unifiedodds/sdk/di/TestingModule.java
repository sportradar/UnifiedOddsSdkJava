package com.sportradar.unifiedodds.sdk.di;

import com.google.inject.Binder;
import com.google.inject.Module;
import com.sportradar.unifiedodds.sdk.SportsInfoManager;
import com.sportradar.unifiedodds.sdk.impl.SDKProducerManager;
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
}
