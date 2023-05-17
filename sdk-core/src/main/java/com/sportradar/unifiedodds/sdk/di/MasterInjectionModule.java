/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */

package com.sportradar.unifiedodds.sdk.di;

import com.google.common.base.Preconditions;
import com.google.inject.AbstractModule;
import com.google.inject.Module;
import com.sportradar.unifiedodds.sdk.SDKGlobalEventsListener;
import com.sportradar.unifiedodds.sdk.SDKInternalConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The master SDK {@link Module} implementation used to set-up the dependency injection container
 */
@SuppressWarnings(
    { "AbbreviationAsWordInName", "ClassDataAbstractionCoupling", "ClassFanOutComplexity", "ConstantName" }
)
public class MasterInjectionModule extends AbstractModule {

    private static final Logger logger = LoggerFactory.getLogger(MasterInjectionModule.class);
    private final SDKGlobalEventsListener sdkListener;
    private final SDKInternalConfiguration config;
    private final CustomisableSDKModule customisableSDKModule;

    public MasterInjectionModule(
        SDKGlobalEventsListener sdkListener,
        SDKInternalConfiguration config,
        CustomisableSDKModule customisableSDKModule
    ) {
        Preconditions.checkNotNull(sdkListener, "sdkListener cannot be a null reference");
        Preconditions.checkNotNull(config, "config cannot be a null reference");

        this.sdkListener = sdkListener;
        this.config = config;
        this.customisableSDKModule = customisableSDKModule;
    }

    @Override
    protected void configure() {
        // disable circular proxies, since they are a sign of insufficiently granular decomposition/bad practice
        binder().disableCircularProxies();

        bind(SDKInternalConfiguration.class).toInstance(config);

        InternalCachesProvider internalCachesProvider = new InternalCachesProviderImpl();
        bind(InternalCachesProvider.class).toInstance(internalCachesProvider);

        install(new GeneralModule(sdkListener, config, new HttpClientFactory()));
        install(new ReadersModule());
        install(new DataProvidersModule());
        install(new CachingModule(internalCachesProvider));
        install(new CustomBetModule());
        install(new MessageProcessorPipeline());
        install(new MarketsModule());
        install(new EventChangeManagerModule());

        if (customisableSDKModule == null) {
            install(new CustomisableSDKModule());
        } else {
            logger.warn(
                "Installing user customisable injection module: {}",
                customisableSDKModule.getClass()
            );
            install(customisableSDKModule);
        }
    }
}
