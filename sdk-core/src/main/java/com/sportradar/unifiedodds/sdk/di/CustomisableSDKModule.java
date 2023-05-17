/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */

package com.sportradar.unifiedodds.sdk.di;

import com.google.inject.AbstractModule;
import com.google.inject.Binder;
import com.google.inject.Singleton;
import com.google.inject.TypeLiteral;
import com.google.inject.name.Names;
import com.sportradar.uf.sportsapi.datamodel.MarketDescriptions;
import com.sportradar.unifiedodds.sdk.SnapshotRequestManager;
import com.sportradar.unifiedodds.sdk.impl.*;

/**
 * An injection module which is used to customise some of the SDK internal components
 */
@SuppressWarnings({ "AbbreviationAsWordInName", "LineLength" })
public class CustomisableSDKModule extends AbstractModule {

    /**
     * Configures a {@link Binder} via the exposed methods.
     */
    @Override
    protected final void configure() {
        bind(ChannelMessageConsumer.class).to(provideMessageConsumerImplementationClass());
        bind(SnapshotRequestManager.class)
            .to(provideSnapshotRequestSchedulerImplementationClass())
            .in(Singleton.class);

        bind(new TypeLiteral<ObservableDataProvider<MarketDescriptions>>() {})
            .annotatedWith(Names.named("AdditionalMarketMappingsProvider"))
            .to(providesAdditionalMarketMappingsProviderClass())
            .in(Singleton.class);
    }

    /**
     * Binds the message receiver that should be used to process received messages
     */
    protected Class<? extends ChannelMessageConsumer> provideMessageConsumerImplementationClass() {
        return ChannelMessageConsumerImpl.class;
    }

    /**
     * Binds the snapshot request manager that should be used to manage recovery requests
     */
    protected Class<? extends SnapshotRequestManager> provideSnapshotRequestSchedulerImplementationClass() {
        return DefaultSnapshotRequestManager.class;
    }

    /**
     * Binds the optional additional market mappings provider
     */
    protected Class<? extends ObservableDataProvider<MarketDescriptions>> providesAdditionalMarketMappingsProviderClass() {
        return DefaultAdditionalMarketMappingsProvider.class;
    }
}
