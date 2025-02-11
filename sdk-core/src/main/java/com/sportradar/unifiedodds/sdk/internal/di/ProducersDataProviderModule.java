/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */
package com.sportradar.unifiedodds.sdk.internal.di;

import com.google.inject.Binder;
import com.google.inject.Module;
import com.google.inject.Provides;
import com.google.inject.Singleton;
import com.google.inject.name.Named;
import com.sportradar.uf.sportsapi.datamodel.Producers;
import com.sportradar.unifiedodds.sdk.internal.impl.DataProvider;
import com.sportradar.unifiedodds.sdk.internal.impl.Deserializer;
import com.sportradar.unifiedodds.sdk.internal.impl.LogHttpDataFetcher;
import com.sportradar.unifiedodds.sdk.internal.impl.ProducerDataProvider;
import com.sportradar.unifiedodds.sdk.internal.impl.ProducerDataProviderImpl;
import com.sportradar.unifiedodds.sdk.internal.impl.SdkInternalConfiguration;

public class ProducersDataProviderModule implements Module {

    private SdkInternalConfiguration config;

    ProducersDataProviderModule(SdkInternalConfiguration config) {
        this.config = config;
    }

    @Override
    public void configure(Binder binder) {
        binder.bind(ProducerDataProvider.class).to(ProducerDataProviderImpl.class).in(Singleton.class);
    }

    @Provides
    @Singleton
    private DataProvider<Producers> providesProducersDataProvider(
        LogHttpDataFetcher logHttpDataFetcher,
        @Named("SportsApiJaxbDeserializer") Deserializer deserializer
    ) {
        return new DataProvider<>("/descriptions/producers.xml", config, logHttpDataFetcher, deserializer);
    }
}
