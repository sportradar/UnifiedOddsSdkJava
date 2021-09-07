/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */

package com.sportradar.unifiedodds.sdk.di;

import com.google.inject.AbstractModule;
import com.google.inject.Binder;
import com.sportradar.unifiedodds.sdk.CustomBetSelectionBuilder;
import com.sportradar.unifiedodds.sdk.CustomBetSelectionBuilderImpl;

/**
 * The DI module in charge of EventChangeManager
 */
public class EventChangeManagerModule extends AbstractModule {
    /**
     * Configures a {@link Binder} via the exposed methods.
     */
    @Override
    protected void configure() {
        bind(CustomBetSelectionBuilder.class).to(CustomBetSelectionBuilderImpl.class);
    }

}
