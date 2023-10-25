/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */

package com.sportradar.unifiedodds.sdk.di;

import com.google.inject.AbstractModule;
import com.google.inject.Binder;
import com.google.inject.Provides;
import com.google.inject.name.Named;
import com.sportradar.uf.custombet.datamodel.CapiAvailableSelections;
import com.sportradar.uf.custombet.datamodel.CapiCalculationResponse;
import com.sportradar.uf.custombet.datamodel.CapiFilteredCalculationResponse;
import com.sportradar.unifiedodds.sdk.CustomBetSelectionBuilder;
import com.sportradar.unifiedodds.sdk.CustomBetSelectionBuilderImpl;
import com.sportradar.unifiedodds.sdk.SdkInternalConfiguration;
import com.sportradar.unifiedodds.sdk.impl.DataProvider;
import com.sportradar.unifiedodds.sdk.impl.Deserializer;
import com.sportradar.unifiedodds.sdk.impl.LogHttpDataFetcher;

/**
 * The DI module in charge of custom bet API readers
 */
public class CustomBetModule extends AbstractModule {

    /**
     * Configures a {@link Binder} via the exposed methods.
     */
    @Override
    protected void configure() {
        bind(CustomBetSelectionBuilder.class).to(CustomBetSelectionBuilderImpl.class);
    }

    @Provides
    private DataProvider<CapiAvailableSelections> providesAvailableSelectionsDataProvider(
        SdkInternalConfiguration cfg,
        LogHttpDataFetcher httpDataFetcher,
        @Named("CustomBetApiJaxbDeserializer") Deserializer deserializer
    ) {
        return new DataProvider<>("/custombet/%2$s/available_selections", cfg, httpDataFetcher, deserializer);
    }

    @Provides
    private DataProvider<CapiCalculationResponse> providesCalculateProbabilityDataProvider(
        SdkInternalConfiguration cfg,
        LogHttpDataFetcher httpDataFetcher,
        @Named("CustomBetApiJaxbDeserializer") Deserializer deserializer
    ) {
        return new DataProvider<>("/custombet/calculate", cfg, httpDataFetcher, deserializer);
    }

    @Provides
    private DataProvider<CapiFilteredCalculationResponse> providesCalculateProbabilityFilterDataProvider(
        SdkInternalConfiguration cfg,
        LogHttpDataFetcher httpDataFetcher,
        @Named("CustomBetApiJaxbDeserializer") Deserializer deserializer
    ) {
        return new DataProvider<>("/custombet/calculate-filter", cfg, httpDataFetcher, deserializer);
    }
}
