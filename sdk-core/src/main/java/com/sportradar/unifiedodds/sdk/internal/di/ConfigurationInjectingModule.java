/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */
package com.sportradar.unifiedodds.sdk.internal.di;

import com.google.inject.AbstractModule;
import com.sportradar.unifiedodds.sdk.cfg.UofConfiguration;
import com.sportradar.unifiedodds.sdk.internal.cfg.UofConfigurationImpl;
import com.sportradar.unifiedodds.sdk.internal.impl.SdkInternalConfiguration;
import com.sportradar.unifiedodds.sdk.internal.impl.util.files.ResourceReader;

@SuppressWarnings({ "ClassDataAbstractionCoupling" })
public class ConfigurationInjectingModule extends AbstractModule {

    private SdkInternalConfiguration internalConfiguration;
    private UofConfiguration configuration;

    public ConfigurationInjectingModule(
        UofConfiguration configuration,
        SdkInternalConfiguration internalConfiguration
    ) {
        this.configuration = configuration;
        this.internalConfiguration = internalConfiguration;
    }

    @Override
    public void configure() {
        binder().disableCircularProxies();

        bind(SdkInternalConfiguration.class).toInstance(internalConfiguration);
        bind(UofConfiguration.class).toInstance(configuration);
        bind(UofConfigurationImpl.class).toInstance((UofConfigurationImpl) configuration);

        install(new GlobalVariablesModule(new ResourceReader()));
        install(new NoopJmxModule());
        install(new MetricsModule());
        install(new DeserializerModule());
        install(new HttpClientModule(internalConfiguration));
        install(new WhoAmIReaderModule(internalConfiguration));
        install(new ProducersDataProviderModule(internalConfiguration));
    }
}
