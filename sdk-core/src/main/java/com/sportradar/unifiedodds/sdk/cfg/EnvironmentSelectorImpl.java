/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */

package com.sportradar.unifiedodds.sdk.cfg;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableList;
import com.sportradar.unifiedodds.sdk.entities.BookmakerDetails;
import com.sportradar.unifiedodds.sdk.exceptions.InvalidBookmakerDetailsException;
import com.sportradar.unifiedodds.sdk.impl.ProducerData;
import com.sportradar.unifiedodds.sdk.impl.ProducerDataProvider;
import com.sportradar.unifiedodds.sdk.impl.ProducerImpl;
import com.sportradar.unifiedodds.sdk.impl.apireaders.WhoAmIReader;
import com.sportradar.unifiedodds.sdk.oddsentities.Producer;
import java.util.List;
import java.util.function.Function;

/**
 * A basic implementation of the {@link EnvironmentSelector}
 */
@SuppressWarnings({ "LineLength" })
class EnvironmentSelectorImpl implements EnvironmentSelector {

    private final UofConfigurationImpl configuration;
    private final SdkConfigurationPropertiesReader configurationPropertiesReader;
    private final SdkConfigurationYamlReader configurationYamlReader;

    EnvironmentSelectorImpl(
        UofConfigurationImpl config,
        SdkConfigurationPropertiesReader sdkConfigurationPropertiesReader,
        SdkConfigurationYamlReader sdkConfigurationYamlReader
    ) {
        Preconditions.checkNotNull(config);
        Preconditions.checkNotNull(sdkConfigurationPropertiesReader);
        Preconditions.checkNotNull(sdkConfigurationYamlReader);

        this.configuration = config;
        this.configurationPropertiesReader = sdkConfigurationPropertiesReader;
        this.configurationYamlReader = sdkConfigurationYamlReader;
    }

    @Override
    public ConfigurationBuilder selectReplay() {
        configuration.updateSdkEnvironment(Environment.Replay);

        return new ConfigurationBuilderImpl(
            configuration,
            configurationPropertiesReader,
            configurationYamlReader
        );
    }

    /**
     * Returns a {@link CustomConfigurationBuilder} allowing the properties to be set to custom values (usefull for testing with local AMQP)
     *
     * @return a {@link CustomConfigurationBuilder} allowing the properties to be set to custom values
     */
    @Override
    public CustomConfigurationBuilder selectCustom() {
        //populate connection settings and then override only needed
        configuration.updateSdkEnvironment(Environment.Integration);
        configuration.updateSdkEnvironment(Environment.Custom);

        return new CustomConfigurationBuilderImpl(
            configuration,
            configurationPropertiesReader,
            configurationYamlReader
        );
    }

    /**
     * Returns a {@link ConfigurationBuilder} with properties set to values needed to access specified environment.
     * (for accessing replay or custom server use selectReplay or selectCustom)
     *
     * @param environment a {@link Environment} specifying to which environment to connect
     * @return a {@link ConfigurationBuilder} with properties set to values needed to access specified environment
     */
    @Override
    public ConfigurationBuilder selectEnvironment(Environment environment) {
        configuration.updateSdkEnvironment(environment);

        return new ConfigurationBuilderImpl(
            configuration,
            configurationPropertiesReader,
            configurationYamlReader
        );
    }
}
