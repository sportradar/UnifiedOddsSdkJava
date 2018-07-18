/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */

package com.sportradar.unifiedodds.sdk.di;

import com.google.inject.AbstractModule;
import com.google.inject.Binder;
import com.sportradar.unifiedodds.sdk.impl.markets.*;
import com.sportradar.unifiedodds.sdk.impl.oddsentities.markets.MarketFactory;
import com.sportradar.unifiedodds.sdk.impl.oddsentities.markets.MarketFactoryImpl;

/**
 * The DI module in charge of market related classes
 */
public class MarketsModule extends AbstractModule {
    /**
     * Configures a {@link Binder} via the exposed methods.
     */
    @Override
    protected void configure() {
        bind(MarketFactory.class).to(MarketFactoryImpl.class);
        bind(NameProviderFactory.class).to(NameProviderFactoryImpl.class);
        bind(NameExpressionFactory.class).to(NameExpressionFactoryImpl.class);
        bind(OperandFactory.class).to(OperandFactoryImpl.class);
    }
}
