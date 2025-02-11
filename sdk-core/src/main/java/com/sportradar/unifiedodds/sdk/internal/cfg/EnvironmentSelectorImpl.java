/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */

package com.sportradar.unifiedodds.sdk.internal.cfg;

import com.google.common.base.Preconditions;
import com.sportradar.unifiedodds.sdk.cfg.ConfigurationBuilder;
import com.sportradar.unifiedodds.sdk.cfg.CustomConfigurationBuilder;
import com.sportradar.unifiedodds.sdk.cfg.Environment;
import com.sportradar.unifiedodds.sdk.cfg.EnvironmentSelector;

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
