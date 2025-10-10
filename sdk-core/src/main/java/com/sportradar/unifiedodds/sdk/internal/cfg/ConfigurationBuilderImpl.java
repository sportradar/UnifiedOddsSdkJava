/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */

package com.sportradar.unifiedodds.sdk.internal.cfg;

import com.google.common.base.Preconditions;
import com.sportradar.unifiedodds.sdk.cfg.ConfigurationBuilder;
import com.sportradar.unifiedodds.sdk.cfg.Environment;
import com.sportradar.unifiedodds.sdk.cfg.UofConfiguration;

/**
 * A basic implementation of the {@link ConfigurationBuilder}
 */
@SuppressWarnings({ "ParameterNumber" })
class ConfigurationBuilderImpl
    extends RecoveryConfigurationBuilderImpl<ConfigurationBuilder>
    implements ConfigurationBuilder {

    ConfigurationBuilderImpl(
        UofConfigurationImpl uofConfiguration,
        SdkConfigurationPropertiesReader sdkConfigurationPropertiesReader,
        SdkConfigurationYamlReader sdkConfigurationYamlReader
    ) {
        super(uofConfiguration, sdkConfigurationPropertiesReader, sdkConfigurationYamlReader);
    }

    /**
     * Builds and returns a {@link UofConfigurationImpl} instance
     *
     * @return the constructed {@link UofConfigurationImpl} instance
     */
    @Override
    public UofConfiguration build() {
        verifyNotReplayWithAuthenticationConfigured();

        configuration.validateMinimumSettings();
        configuration.acquireBookmakerDetailsAndProducerData();

        return configuration;
    }

    private void verifyNotReplayWithAuthenticationConfigured() {
        boolean isReplay =
            configuration.getEnvironment() == Environment.Replay ||
            configuration.getEnvironment() == Environment.GlobalReplay;
        boolean hasAuthConfigured = configuration.getClientAuthentication() != null;
        Preconditions.checkArgument(
            !hasAuthConfigured || !isReplay,
            "Client Authentication is not supported in Replay environments"
        );
    }
}
