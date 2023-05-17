package com.sportradar.unifiedodds.sdk.di;

import static org.mockito.Mockito.when;

import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.util.Modules;
import com.sportradar.unifiedodds.sdk.SDKInternalConfiguration;
import org.mockito.Mockito;

@SuppressWarnings({ "MagicNumber" })
public class TestInjectorFactory {

    private final SDKInternalConfiguration config;

    public TestInjectorFactory(SDKInternalConfiguration config) {
        this.config = config;
    }

    public TestInjectorFactory() {
        config = Mockito.mock(SDKInternalConfiguration.class);
        when(config.getAPIHost()).thenReturn("api.betradar.com");
        when(config.getAPIPort()).thenReturn(80); // ensure port is > 0 so validation doesn't fail
        when(config.getApiHostAndPort()).thenReturn("mq.betradar.com");
    }

    public Injector create() {
        Injector injector = Guice.createInjector(
            Modules.override(createMasterInjectionModule()).with(new TestingModule())
        );
        return injector;
    }

    private MasterInjectionModule createMasterInjectionModule() {
        return new MockedMasterModule(config);
    }
}
