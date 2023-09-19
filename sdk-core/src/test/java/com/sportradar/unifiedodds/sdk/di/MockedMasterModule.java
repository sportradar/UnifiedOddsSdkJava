package com.sportradar.unifiedodds.sdk.di;

import static org.mockito.Mockito.mock;

import com.sportradar.unifiedodds.sdk.SdkInternalConfiguration;
import com.sportradar.unifiedodds.sdk.UofGlobalEventsListener;
import com.sportradar.unifiedodds.sdk.cfg.UofConfiguration;
import com.sportradar.unifiedodds.sdk.shared.StubUofConfiguration;

public class MockedMasterModule extends MasterInjectionModule {

    public MockedMasterModule() {
        super(
            mock(UofGlobalEventsListener.class),
            mock(SdkInternalConfiguration.class),
            new StubUofConfiguration(),
            null
        );
    }

    public MockedMasterModule(SdkInternalConfiguration internalConfig, UofConfiguration uofConfiguration) {
        super(mock(UofGlobalEventsListener.class), internalConfig, uofConfiguration, null);
    }
}
