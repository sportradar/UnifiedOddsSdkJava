/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */

package com.sportradar.unifiedodds.sdk.cfg;

import com.sportradar.unifiedodds.sdk.SDKConfigurationPropertiesReader;
import com.sportradar.unifiedodds.sdk.SDKConfigurationYamlReader;
import com.sportradar.utils.SdkHelper;

import java.util.ArrayList;

/**
 * A basic implementation of the {@link ConfigurationBuilder}
 */
class ConfigurationBuilderImpl extends RecoveryConfigurationBuilderImpl<ConfigurationBuilder> implements ConfigurationBuilder {
    private final String accessToken;
    private final String messagingHost;
    private final String apiHost;
    private final int messagingPort;
    private final boolean useMessagingSsl;
    private final boolean useApiSsl;
    private final Environment environment;

    ConfigurationBuilderImpl(String accessToken, String messagingHost, String apiHost, int messagingPort, boolean useMessagingSsl, boolean useApiSsl, SDKConfigurationPropertiesReader sdkConfigurationPropertiesReader, SDKConfigurationYamlReader sdkConfigurationYamlReader, Environment environment) {
        super(sdkConfigurationPropertiesReader, sdkConfigurationYamlReader);

        this.accessToken = accessToken;
        this.messagingHost = messagingHost;
        this.apiHost = apiHost;
        this.messagingPort = messagingPort;
        this.useMessagingSsl = useMessagingSsl;
        this.useApiSsl = useApiSsl;
        this.environment = environment;
    }

    /**
     * Builds and returns a {@link OddsFeedConfiguration} instance
     *
     * @return the constructed {@link OddsFeedConfiguration} instance
     */
    @Override
    public OddsFeedConfiguration build() {

        defaultLocale = SdkHelper.checkConfigurationLocales(defaultLocale, getSupportedLocales());

        return new OddsFeedConfiguration(
                accessToken,
                defaultLocale,
                new ArrayList<>(getSupportedLocales()),
                messagingHost,
                apiHost,
                maxInactivitySeconds,
                maxRecoveryExecutionTimeMinutes,
                useMessagingSsl,
                useApiSsl,
                messagingPort,
                null,
                null,
                nodeId,
                environment == Environment.Staging,
                new ArrayList<>(disabledProducers),
                exceptionHandlingStrategy,
                environment,
                null);
    }
}
