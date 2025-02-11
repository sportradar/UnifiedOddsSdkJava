/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */

package com.sportradar.unifiedodds.sdk.internal.impl;

import com.google.inject.Inject;
import com.google.inject.name.Named;
import com.sportradar.uf.sportsapi.datamodel.MarketDescriptions;
import com.sportradar.unifiedodds.sdk.internal.exceptions.DataProviderException;

/**
 * Created on 07/11/2018.
 * // TODO @eti: Javadoc
 */
public final class DefaultAdditionalMarketMappingsProvider
    extends ObservableDataProvider<MarketDescriptions> {

    @Inject
    DefaultAdditionalMarketMappingsProvider(
        SdkInternalConfiguration config,
        LogHttpDataFetcher logHttpDataFetcher,
        @Named("SportsApiJaxbDeserializer") Deserializer deserializer
    ) {
        super("no-op-uri", config, logHttpDataFetcher, deserializer);
    }

    @Override
    public MarketDescriptions getData() throws DataProviderException {
        return null;
    }

    @Override
    public void registerWatcher(Class watcherClazz, DataProviderWatcher watcher) {
        // No-op
    }

    @Override
    public boolean logErrors() {
        return false;
    }
}
