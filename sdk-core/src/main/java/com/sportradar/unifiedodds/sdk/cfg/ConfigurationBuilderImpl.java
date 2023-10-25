/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */

package com.sportradar.unifiedodds.sdk.cfg;

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
        configuration.validateMinimumSettings();
        configuration.acquireBookmakerDetailsAndProducerData();

        return configuration;
    }
}
