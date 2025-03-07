package com.sportradar.unifiedodds.sdk.di;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.util.Modules;
import com.sportradar.unifiedodds.sdk.cfg.Environment;
import com.sportradar.unifiedodds.sdk.cfg.UofConfiguration;
import com.sportradar.unifiedodds.sdk.internal.di.MasterInjectionModule;
import com.sportradar.unifiedodds.sdk.internal.di.TestingModule;
import com.sportradar.unifiedodds.sdk.internal.impl.SdkInternalConfiguration;
import com.sportradar.unifiedodds.sdk.shared.StubUofConfiguration;

@SuppressWarnings({ "MagicNumber" })
public class TestInjectorFactory {

    private final UofConfiguration config;
    private final SdkInternalConfiguration internalConfig;

    public TestInjectorFactory(SdkInternalConfiguration internalConfig, UofConfiguration config) {
        this.config = config;
        this.internalConfig = internalConfig;
    }

    public TestInjectorFactory() {
        StubUofConfiguration stubConfig = new StubUofConfiguration();
        stubConfig.setEnvironment(Environment.Integration);
        stubConfig.resetNbrSetEnvironmentCalled();
        internalConfig = mock(SdkInternalConfiguration.class);
        when(internalConfig.getEnvironment()).thenReturn(Environment.Integration);
        when(internalConfig.getHttpClientTimeout()).thenReturn(10);
        when(internalConfig.getFastHttpClientTimeout()).thenReturn(5L);
        config = stubConfig;
    }

    public Injector create() {
        Injector injector = Guice.createInjector(
            Modules.override(createMasterInjectionModule()).with(new TestingModule())
        );
        return injector;
    }

    private MasterInjectionModule createMasterInjectionModule() {
        return new MockedMasterModule(internalConfig, config);
    }
}
