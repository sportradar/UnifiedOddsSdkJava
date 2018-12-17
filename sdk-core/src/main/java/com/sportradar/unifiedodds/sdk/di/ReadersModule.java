/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */

package com.sportradar.unifiedodds.sdk.di;

import com.google.inject.AbstractModule;
import com.google.inject.Binder;
import com.google.inject.Provides;
import com.google.inject.Singleton;
import com.google.inject.name.Named;
import com.sportradar.uf.datamodel.UFCashout;
import com.sportradar.uf.sportsapi.datamodel.BookmakerDetails;
import com.sportradar.unifiedodds.sdk.SDKInternalConfiguration;
import com.sportradar.unifiedodds.sdk.impl.DataProvider;
import com.sportradar.unifiedodds.sdk.impl.Deserializer;
import com.sportradar.unifiedodds.sdk.impl.LogHttpDataFetcher;
import com.sportradar.unifiedodds.sdk.impl.UnifiedFeedConstants;
import com.sportradar.unifiedodds.sdk.impl.apireaders.WhoAmIReader;

import java.util.Locale;

/**
 * The DI module in charge of special API readers
 */
public class ReadersModule extends AbstractModule {
    /**
     * Configures a {@link Binder} via the exposed methods.
     */
    @Override
    protected void configure() {
        bind(WhoAmIReader.class).in(Singleton.class);
    }

    @Provides
    private DataProvider<UFCashout> providesCashOutDataProvider(SDKInternalConfiguration cfg,
                                                                LogHttpDataFetcher httpDataFetcher,
                                                                @Named("MessageDeserializer") Deserializer deserializer) {
        return new DataProvider<>("/probabilities/%s",
                cfg,
                httpDataFetcher,
                deserializer);
    }

    @Provides @Named("ConfigDataProvider")
    private DataProvider<BookmakerDetails> providesConfigDataProvider(SDKInternalConfiguration cfg,
                                                                      LogHttpDataFetcher httpDataFetcher,
                                                                      @Named("ApiJaxbDeserializer") Deserializer deserializer) {
        return new DataProvider<>(
                "/users/whoami.xml",
                cfg,
                httpDataFetcher,
                deserializer
        );
    }

    @Provides @Named("ProductionDataProvider")
    private DataProvider<BookmakerDetails> providesProductionDataProvider(LogHttpDataFetcher httpDataFetcher,
                                                                          @Named("ApiJaxbDeserializer") Deserializer deserializer) {
        return new DataProvider<>(
                "/users/whoami.xml",
                UnifiedFeedConstants.PRODUCTION_API_HOST,
                true,
                Locale.ENGLISH,
                httpDataFetcher,
                deserializer
        );
    }

    @Provides @Named("IntegrationDataProvider")
    private DataProvider<BookmakerDetails> providesIntegrationDataProvider(LogHttpDataFetcher httpDataFetcher,
                                                                           @Named("ApiJaxbDeserializer") Deserializer deserializer) {
        return new DataProvider<>(
                "/users/whoami.xml",
                UnifiedFeedConstants.INTEGRATION_API_HOST,
                true,
                Locale.ENGLISH,
                httpDataFetcher,
                deserializer
        );
    }
}
