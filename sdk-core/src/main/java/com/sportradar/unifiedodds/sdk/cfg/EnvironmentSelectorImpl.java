/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */

package com.sportradar.unifiedodds.sdk.cfg;

import com.google.common.base.Preconditions;
import com.google.common.base.Strings;
import com.sportradar.unifiedodds.sdk.SDKConfigurationPropertiesReader;
import com.sportradar.unifiedodds.sdk.SDKConfigurationYamlReader;
import com.sportradar.unifiedodds.sdk.impl.EnvironmentManager;

/**
 * A basic implementation of the {@link EnvironmentSelector}
 */
class EnvironmentSelectorImpl implements EnvironmentSelector {
    private final String accessToken;
    private final SDKConfigurationPropertiesReader sdkConfigurationPropertiesReader;
    private final SDKConfigurationYamlReader sdkConfigurationYamlReader;

    EnvironmentSelectorImpl(String accessToken, SDKConfigurationPropertiesReader sdkConfigurationPropertiesReader, SDKConfigurationYamlReader sdkConfigurationYamlReader) {
        Preconditions.checkArgument(!Strings.isNullOrEmpty(accessToken));
        Preconditions.checkNotNull(sdkConfigurationPropertiesReader);
        Preconditions.checkNotNull(sdkConfigurationYamlReader);

        this.accessToken = accessToken;
        this.sdkConfigurationPropertiesReader = sdkConfigurationPropertiesReader;
        this.sdkConfigurationYamlReader = sdkConfigurationYamlReader;
    }

    /**
     * Returns a {@link ConfigurationBuilder} with properties set to values needed to access integration environment
     *
     * @return a {@link ConfigurationBuilder} with properties set to values needed to access integration environment
     */
    @Override
    public ConfigurationBuilder selectIntegration() { return selectEnvironment(Environment.Integration); }

    /**
     * Returns a {@link ConfigurationBuilder} with properties set to values needed to access production environment
     *
     * @return a {@link ConfigurationBuilder} with properties set to values needed to access production environment
     */
    @Override
    public ConfigurationBuilder selectProduction() { return selectEnvironment(Environment.Production); }

    /**
     * Returns a {@link ReplayConfigurationBuilder} with properties set to values needed to access replay server
     *
     * @return a {@link ReplayConfigurationBuilder} with properties set to values needed to access replay server
     */
    @Override
    public ReplayConfigurationBuilder selectReplay() {
        String messagingHost = EnvironmentManager.getMqHost(Environment.Replay);
        String apiHost = EnvironmentManager.getApiHost(Environment.Replay);

        return new ReplayConfigurationBuilderImpl(accessToken,
                messagingHost,
                apiHost,
                EnvironmentManager.DEFAULT_MQ_HOST_PORT,
                true,
                true,
                sdkConfigurationPropertiesReader,
                sdkConfigurationYamlReader,
                Environment.Replay);
    }

    /**
     * Returns a {@link CustomConfigurationBuilder} allowing the properties to be set to custom values (usefull for testing with local AMQP)
     *
     * @return a {@link CustomConfigurationBuilder} allowing the properties to be set to custom values
     */
    @Override
    public CustomConfigurationBuilder selectCustom() {
        String messagingHost = EnvironmentManager.getMqHost(Environment.Integration);
        String apiHost = EnvironmentManager.getApiHost(Environment.Integration);
        int apiPort = EnvironmentManager.getApiPort(Environment.Integration);

        return new CustomConfigurationBuilderImpl(accessToken,
                messagingHost,
                apiHost,
                apiPort,
                EnvironmentManager.DEFAULT_MQ_HOST_PORT,
                true,
                true,
                sdkConfigurationPropertiesReader,
                sdkConfigurationYamlReader,
                Environment.Custom);
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
        String messagingHost = EnvironmentManager.getMqHost(Environment.Integration);
        String apiHost = EnvironmentManager.getApiHost(Environment.Integration);
        int apiPort = EnvironmentManager.getApiPort(Environment.Integration);

        if(!environment.equals(Environment.Custom)){
            messagingHost = EnvironmentManager.getMqHost(environment);
            apiHost = EnvironmentManager.getApiHost(environment);
            apiPort = EnvironmentManager.getApiPort(environment);
        }

        return new ConfigurationBuilderImpl(accessToken,
                                            messagingHost,
                                            apiHost,
                                            apiPort,
                                            EnvironmentManager.DEFAULT_MQ_HOST_PORT,
                                            true,
                                            true,
                                            sdkConfigurationPropertiesReader,
                                            sdkConfigurationYamlReader,
                                            environment);
    }
}
