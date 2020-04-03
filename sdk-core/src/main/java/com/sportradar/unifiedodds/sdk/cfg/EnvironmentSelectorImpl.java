/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */

package com.sportradar.unifiedodds.sdk.cfg;

import com.google.common.base.Preconditions;
import com.google.common.base.Strings;
import com.sportradar.unifiedodds.sdk.SDKConfigurationPropertiesReader;
import com.sportradar.unifiedodds.sdk.SDKConfigurationYamlReader;
import com.sportradar.unifiedodds.sdk.impl.UnifiedFeedConstants;

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
    public ConfigurationBuilder selectIntegration() {
        String messagingHost = UnifiedFeedConstants.INTEGRATION_MESSAGING_HOST;
        String apiHost = UnifiedFeedConstants.INTEGRATION_API_HOST;
        int messagingPort = 5671;

        return new ConfigurationBuilderImpl(accessToken,
                messagingHost,
                apiHost,
                messagingPort,
                true,
                true,
                sdkConfigurationPropertiesReader,
                sdkConfigurationYamlReader,
                Environment.Integration);
    }

    /**
     * Returns a {@link ConfigurationBuilder} with properties set to values needed to access production environment
     *
     * @return a {@link ConfigurationBuilder} with properties set to values needed to access production environment
     */
    @Override
    public ConfigurationBuilder selectProduction() {
        String messagingHost = UnifiedFeedConstants.PRODUCTION_MESSAGING_HOST;
        String apiHost = UnifiedFeedConstants.PRODUCTION_API_HOST;
        int messagingPort = 5671;

        return new ConfigurationBuilderImpl(accessToken,
                messagingHost,
                apiHost,
                messagingPort,
                true,
                true,
                sdkConfigurationPropertiesReader,
                sdkConfigurationYamlReader,
                Environment.Production);
    }

    /**
     * Returns a {@link ReplayConfigurationBuilder} with properties set to values needed to access replay server
     *
     * @return a {@link ReplayConfigurationBuilder} with properties set to values needed to access replay server
     */
    @Override
    public ReplayConfigurationBuilder selectReplay() {
        String messagingHost = UnifiedFeedConstants.REPLAY_MESSAGING_HOST;
        // It will be overwritten by the WhoAmIReader to INTEGRATION if needed (depending on the token)
        String apiHost = UnifiedFeedConstants.PRODUCTION_API_HOST;
        int messagingPort = 5671;

        return new ReplayConfigurationBuilderImpl(accessToken,
                messagingHost,
                apiHost,
                messagingPort,
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
        String messagingHost = UnifiedFeedConstants.INTEGRATION_MESSAGING_HOST;
        String apiHost = UnifiedFeedConstants.INTEGRATION_API_HOST;
        int messagingPort = 5671;

        return new CustomConfigurationBuilderImpl(accessToken,
                messagingHost,
                apiHost,
                messagingPort,
                true,
                true,
                sdkConfigurationPropertiesReader,
                sdkConfigurationYamlReader,
                Environment.Custom);
    }
}
