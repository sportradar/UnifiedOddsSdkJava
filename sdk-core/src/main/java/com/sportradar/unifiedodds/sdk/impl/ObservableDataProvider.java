/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */

package com.sportradar.unifiedodds.sdk.impl;

import com.sportradar.unifiedodds.sdk.SdkInternalConfiguration;
import com.sportradar.unifiedodds.sdk.exceptions.internal.DataProviderException;
import java.util.Locale;

/**
 * An utility class which extends {@link DataProvider} which adds the support to listen for possible data changes
 */
@SuppressWarnings({ "ClassTypeParameterName", "OverloadMethodsDeclarationOrder" })
public abstract class ObservableDataProvider<TOut> extends DataProvider<TOut> {

    public ObservableDataProvider(
        String uriFormat,
        SdkInternalConfiguration config,
        LogHttpDataFetcher logHttpDataFetcher,
        Deserializer deserializer
    ) {
        super(uriFormat, config, logHttpDataFetcher, deserializer);
    }

    @Override
    public abstract TOut getData() throws DataProviderException;

    public abstract void registerWatcher(Class watcherClazz, DataProviderWatcher watcher);

    public abstract boolean logErrors();

    @Override
    public TOut getData(String... args) throws DataProviderException {
        return getData();
    }

    @Override
    public TOut getData(Locale locale, String... args) throws DataProviderException {
        return getData();
    }

    @Override
    public DataWrapper<TOut> getDataWithAdditionalInfo(Locale locale, String... args)
        throws DataProviderException {
        return new DataWrapper<>(getData(), null);
    }

    public interface DataProviderWatcher {
        void onDataChanged();
    }
}
