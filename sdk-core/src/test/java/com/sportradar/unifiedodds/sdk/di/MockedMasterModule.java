package com.sportradar.unifiedodds.sdk.di;

import com.sportradar.unifiedodds.sdk.SDKGlobalEventsListener;
import com.sportradar.unifiedodds.sdk.SDKInternalConfiguration;
import org.mockito.Mockito;

public class MockedMasterModule extends MasterInjectionModule {
    public MockedMasterModule() {
        super(
                Mockito.mock(SDKGlobalEventsListener.class),
                Mockito.mock(SDKInternalConfiguration.class),
                null
        );
    }

    public MockedMasterModule(SDKInternalConfiguration config) {
        super(
                Mockito.mock(SDKGlobalEventsListener.class),
                config,
                null
        );
    }
}
