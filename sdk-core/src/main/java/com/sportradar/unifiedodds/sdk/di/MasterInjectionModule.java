/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */

package com.sportradar.unifiedodds.sdk.di;

import com.google.common.base.Preconditions;
import com.google.inject.AbstractModule;
import com.google.inject.Module;
import com.sportradar.unifiedodds.sdk.SdkInternalConfiguration;
import com.sportradar.unifiedodds.sdk.UofGlobalEventsListener;
import com.sportradar.unifiedodds.sdk.cfg.UofConfiguration;
import com.sportradar.unifiedodds.sdk.cfg.UofConfigurationImpl;
import com.sportradar.unifiedodds.sdk.impl.util.files.ResourceReader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The master SDK {@link Module} implementation used to set-up the dependency injection container
 */
@SuppressWarnings({ "ClassDataAbstractionCoupling", "ClassFanOutComplexity", "ConstantName" })
public class MasterInjectionModule extends AbstractModule {

    private static final Logger logger = LoggerFactory.getLogger(MasterInjectionModule.class);
    private final UofGlobalEventsListener sdkListener;
    private final SdkInternalConfiguration configInternal;
    private final CustomisableSdkModule customisableSdkModule;
    private final UofConfiguration uofConfiguration;

    public MasterInjectionModule(
        UofGlobalEventsListener sdkListener,
        SdkInternalConfiguration sdkInternalConfiguration,
        UofConfiguration uofConfiguration,
        CustomisableSdkModule customisableSdkModule
    ) {
        Preconditions.checkNotNull(sdkListener, "sdkListener cannot be a null reference");
        Preconditions.checkNotNull(uofConfiguration, "config cannot be a null reference");

        this.sdkListener = sdkListener;
        this.uofConfiguration = uofConfiguration;
        this.configInternal = sdkInternalConfiguration;

        this.customisableSdkModule = customisableSdkModule;
    }

    @Override
    protected void configure() {
        // disable circular proxies, since they are a sign of insufficiently granular decomposition/bad practice
        binder().disableCircularProxies();

        bind(SdkInternalConfiguration.class).toInstance(configInternal);
        bind(UofConfiguration.class).toInstance(uofConfiguration);
        bind(UofConfigurationImpl.class).toInstance((UofConfigurationImpl) uofConfiguration);

        InternalCachesProvider internalCachesProvider = new InternalCachesProviderImpl(
            uofConfiguration.getCache()
        );
        bind(InternalCachesProvider.class).toInstance(internalCachesProvider);

        install(new GlobalVariablesModule(new ResourceReader()));
        install(new JmxModule());
        install(new DeserializerModule());
        install(new HttpClientModule(configInternal));
        install(new WhoAmIReaderModule(configInternal));
        install(new ProducersDataProviderModule(configInternal));
        install(new GeneralModule(sdkListener));
        install(new ReadersModule());
        install(new DataProvidersModule());
        install(new CachingModule(internalCachesProvider));
        install(new CustomBetModule());
        install(new MessageProcessorPipeline());
        install(new MarketsModule());
        install(new EventChangeManagerModule());

        if (customisableSdkModule == null) {
            install(new CustomisableSdkModule());
        } else {
            logger.warn(
                "Installing user customisable injection module: {}",
                customisableSdkModule.getClass()
            );
            install(customisableSdkModule);
        }
    }
}
