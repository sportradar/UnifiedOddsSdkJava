/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */

package com.sportradar.unifiedodds.sdk.di;

import com.google.inject.AbstractModule;
import com.google.inject.Binder;
import com.google.inject.Provides;
import com.google.inject.Singleton;
import com.google.inject.name.Named;
import com.sportradar.uf.datamodel.UfCashout;
import com.sportradar.uf.sportsapi.datamodel.BookmakerDetails;
import com.sportradar.unifiedodds.sdk.SdkInternalConfiguration;
import com.sportradar.unifiedodds.sdk.cfg.Environment;
import com.sportradar.unifiedodds.sdk.impl.*;
import com.sportradar.unifiedodds.sdk.impl.apireaders.WhoAmIReader;
import java.util.Locale;

/**
 * The DI module in charge of special API readers
 */
@SuppressWarnings({ "MagicNumber" })
public class ReadersModule extends AbstractModule {

    /**
     * Configures a {@link Binder} via the exposed methods.
     */
    @Override
    protected void configure() {}

    @Provides
    private DataProvider<UfCashout> providesCashOutDataProvider(
        SdkInternalConfiguration cfg,
        LogHttpDataFetcher httpDataFetcher,
        @Named("MessageDeserializer") Deserializer deserializer
    ) {
        return new DataProvider<>("/probabilities/%s", cfg, httpDataFetcher, deserializer);
    }
}
