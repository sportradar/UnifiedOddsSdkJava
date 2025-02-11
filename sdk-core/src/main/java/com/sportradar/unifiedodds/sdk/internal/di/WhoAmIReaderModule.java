/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */
package com.sportradar.unifiedodds.sdk.internal.di;

import com.google.inject.*;
import com.google.inject.name.Named;
import com.sportradar.uf.sportsapi.datamodel.BookmakerDetails;
import com.sportradar.unifiedodds.sdk.internal.impl.DataProvider;
import com.sportradar.unifiedodds.sdk.internal.impl.Deserializer;
import com.sportradar.unifiedodds.sdk.internal.impl.LogHttpDataFetcher;
import com.sportradar.unifiedodds.sdk.internal.impl.SdkInternalConfiguration;
import com.sportradar.unifiedodds.sdk.internal.impl.apireaders.WhoAmIReader;

public class WhoAmIReaderModule extends AbstractModule {

    private SdkInternalConfiguration config;

    WhoAmIReaderModule(SdkInternalConfiguration config) {
        this.config = config;
    }

    @Override
    public void configure() {
        bind(WhoAmIReader.class).in(Singleton.class);
    }

    @Provides
    @Named("ConfigDataProvider")
    private DataProvider<BookmakerDetails> providesConfigDataProvider(
        LogHttpDataFetcher httpDataFetcher,
        @Named("SportsApiJaxbDeserializer") Deserializer deserializer
    ) {
        return new DataProvider<>("/users/whoami.xml", config, httpDataFetcher, deserializer);
    }

    @Provides
    @Named("ProductionDataProvider")
    private DataProvider<BookmakerDetails> providesProductionDataProvider(
        BookmakerDetailsProviderFactory factory
    ) {
        return factory.targetingProduction();
    }

    @Provides
    private BookmakerDetailsProviderFactory bookmakerDetailsProviderFactory(
        LogHttpDataFetcher httpDataFetcher,
        @Named("SportsApiJaxbDeserializer") Deserializer deserializer
    ) {
        return new BookmakerDetailsProviderFactory(httpDataFetcher, deserializer, config);
    }

    @Provides
    @Named("IntegrationDataProvider")
    private DataProvider<BookmakerDetails> providesIntegrationDataProvider(
        BookmakerDetailsProviderFactory factory
    ) {
        return factory.targetingIntegration();
    }
}
