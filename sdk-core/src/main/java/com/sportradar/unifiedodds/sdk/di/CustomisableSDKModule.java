/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */

package com.sportradar.unifiedodds.sdk.di;

import com.google.inject.AbstractModule;
import com.google.inject.Binder;
import com.sportradar.unifiedodds.sdk.impl.ChannelMessageConsumer;
import com.sportradar.unifiedodds.sdk.impl.ChannelMessageConsumerImpl;

/**
 * An injection module which is used to customise some of the SDK internal components
 */
public class CustomisableSDKModule extends AbstractModule {
    /**
     * Configures a {@link Binder} via the exposed methods.
     */
    @Override
    protected final void configure() {

        bind(ChannelMessageConsumer.class).to(provideMessageConsumerImplementationClass());
    }

    /**
     * Binds the message receiver that should be used to process received messages
     */
    protected Class<? extends ChannelMessageConsumer> provideMessageConsumerImplementationClass() {
        return ChannelMessageConsumerImpl.class;
    }
}
