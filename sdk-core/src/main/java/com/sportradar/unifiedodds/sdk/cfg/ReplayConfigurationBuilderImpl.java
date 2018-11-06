/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */

package com.sportradar.unifiedodds.sdk.cfg;

import com.sportradar.unifiedodds.sdk.SDKConfigurationPropertiesReader;
import com.sportradar.unifiedodds.sdk.SDKConfigurationYamlReader;
import com.sportradar.utils.SdkHelper;

import java.util.ArrayList;

/**
 * A basic implementation of the {@link ReplayConfigurationBuilder}
 */
class ReplayConfigurationBuilderImpl extends ConfigurationBuilderBaseImpl<ReplayConfigurationBuilder> implements ReplayConfigurationBuilder {
    private final String accessToken;
    private final String messagingHost;
    private final int messagingPort;
    private final String apiHost;
    private final boolean useMessagingSsl;
    private final boolean useApiSsl;
    private final Environment environment;

    ReplayConfigurationBuilderImpl(String accessToken, String messagingHost, String apiHost, int messagingPort, boolean useMessagingSsl, boolean useApiSsl, SDKConfigurationPropertiesReader sdkConfigurationPropertiesReader, SDKConfigurationYamlReader sdkConfigurationYamlReader, Environment environment) {
        super(sdkConfigurationPropertiesReader, sdkConfigurationYamlReader);

        this.accessToken = accessToken;
        this.messagingHost = messagingHost;
        this.messagingPort = messagingPort;
        this.apiHost = apiHost;
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

        SdkHelper.checkConfigurationLocales(defaultLocale, getSupportedLocales());

        return new OddsFeedConfiguration(
                accessToken,
                defaultLocale,
                new ArrayList<>(getSupportedLocales()),
                messagingHost,
                apiHost,
                20, // its not used by the SDK ifs its in replay mode
                30, // its not used by the SDK ifs its in replay mode
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
