package com.sportradar.unifiedodds.sdk.di;

import static com.sportradar.unifiedodds.sdk.cfg.Environment.Integration;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.sportradar.unifiedodds.sdk.UofGlobalEventsListener;
import com.sportradar.unifiedodds.sdk.cfg.Environment;
import com.sportradar.unifiedodds.sdk.cfg.UofConfiguration;
import com.sportradar.unifiedodds.sdk.internal.di.MasterInjectionModule;
import com.sportradar.unifiedodds.sdk.internal.impl.SdkInternalConfiguration;
import com.sportradar.unifiedodds.sdk.shared.StubUofConfiguration;

public class MockedMasterModule extends MasterInjectionModule {

    public MockedMasterModule() {
        this(getMockedSdkInternalConfiguration(Integration), new StubUofConfiguration());
    }

    public MockedMasterModule(SdkInternalConfiguration internalConfig, UofConfiguration uofConfiguration) {
        super(mock(UofGlobalEventsListener.class), internalConfig, uofConfiguration, null);
    }

    private static SdkInternalConfiguration getMockedSdkInternalConfiguration(Environment environment) {
        SdkInternalConfiguration internalConfig = mock(SdkInternalConfiguration.class);
        when(internalConfig.getEnvironment()).thenReturn(environment);

        return internalConfig;
    }
}
