package com.sportradar.unifiedodds.sdk.di;

import static org.mockito.Mockito.mock;

import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.util.Modules;
import com.sportradar.unifiedodds.sdk.SdkInternalConfiguration;
import com.sportradar.unifiedodds.sdk.cfg.Environment;
import com.sportradar.unifiedodds.sdk.cfg.UofConfiguration;
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
