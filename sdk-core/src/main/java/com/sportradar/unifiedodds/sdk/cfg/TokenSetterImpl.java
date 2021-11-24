/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */

package com.sportradar.unifiedodds.sdk.cfg;

import com.google.common.base.Preconditions;
import com.google.common.base.Strings;
import com.sportradar.unifiedodds.sdk.SDKConfigurationPropertiesReader;
import com.sportradar.unifiedodds.sdk.SDKConfigurationYamlReader;

import java.util.Optional;

/**
 * The default implementation of the {@link TokenSetter}
 */
public class TokenSetterImpl implements TokenSetter {
    private final SDKConfigurationPropertiesReader sdkConfigurationPropertiesReader;
    private final SDKConfigurationYamlReader sdkConfigurationYamlReader;

    public TokenSetterImpl(SDKConfigurationPropertiesReader sdkConfigurationPropertiesReader, SDKConfigurationYamlReader sdkConfigurationYamlReader) {
        Preconditions.checkNotNull(sdkConfigurationPropertiesReader);
        Preconditions.checkNotNull(sdkConfigurationYamlReader);

        this.sdkConfigurationPropertiesReader = sdkConfigurationPropertiesReader;
        this.sdkConfigurationYamlReader = sdkConfigurationYamlReader;
    }

    /**
     * Sets the access token used to access feed resources (AMQP broker, Sports API, ...)
     *
     * @param token the access token used to access feed resources
     * @return the {@link EnvironmentSelector} instance allowing the selection of target environment
     */
    @Override
    public EnvironmentSelector setAccessToken(String token) {
        Preconditions.checkArgument(!Strings.isNullOrEmpty(token), "Access token can not be null/empty");

        return new EnvironmentSelectorImpl(token, sdkConfigurationPropertiesReader, sdkConfigurationYamlReader);
    }

    /**
     * Sets the access token used to access feed resources (AMQP broker, Sports API, ...) to value read from the sdk properties
     *
     * The properties file should be named "UFSdkConfiguration.properties" and localed in the application resources folder
     *
     * @return the {@link EnvironmentSelector} instance allowing the selection of target environment
     */
    @Override
    public EnvironmentSelector setAccessTokenFromSdkProperties() {
        Optional<String> ifPresent = sdkConfigurationPropertiesReader.readAccessToken();

        String token = ifPresent.orElseThrow(() -> new IllegalArgumentException("Could not read the access token from the SDK properties(uf.sdk.accessToken)"));

        return new EnvironmentSelectorImpl(token, sdkConfigurationPropertiesReader, sdkConfigurationYamlReader);
    }

    /**
     * Sets the access token used to access feed resources (AMQP broker, Sports API, ...) to value read from the application.yml
     *
     * The YAML file should be named "application.properties" and localed in the application resources folder
     *
     * @return the {@link EnvironmentSelector} instance allowing the selection of target environment
     */
    @Override
    public EnvironmentSelector setAccessTokenFromApplicationYaml() {
        Optional<String> ifPresent = sdkConfigurationYamlReader.readAccessToken();

        String token = ifPresent.orElseThrow(() -> new IllegalArgumentException("Could not read the access token from the SDK YAML file(sportradar.sdk.uf.accessToken)"));

        return new EnvironmentSelectorImpl(token, sdkConfigurationPropertiesReader, sdkConfigurationYamlReader);
    }

    /**
     * Sets the access token used to access feed resources (AMQP broker, Sports API, ...) to value read from system variable "uf.accesstoken"
     * <p>You can set the access token with the following JVM argument -Duf.accesstoken=your-access-token</p>
     *
     * @return the {@link EnvironmentSelector} instance allowing the selection of target environment
     */
    @Override
    public EnvironmentSelector setAccessTokenFromSystemVar() {
        String token = System.getProperty("uf.accesstoken");
        if (token == null) {
            token = System.getenv("uf.accesstoken");
        }
        Preconditions.checkArgument(!Strings.isNullOrEmpty(token), "Token system variable uf.accesstoken not found");

        return new EnvironmentSelectorImpl(token, sdkConfigurationPropertiesReader, sdkConfigurationYamlReader);
    }

    /**
     * Sets the general configuration properties to values read from configuration file. Only value which can be set
     * through {@link ConfigurationBuilderBase} methods are set. Any values already set by methods on the current instance
     * are overridden. Builds and returns a {@link OddsFeedConfiguration} instance
     * <p>
     * The properties file should be named "UFSdkConfiguration.properties" and localed in the application resources folder
     *
     * @return builds and returns a {@link OddsFeedConfiguration} instance
     */
    @Override
    public OddsFeedConfiguration buildConfigFromSdkProperties() {
        Environment ufEnvironment = sdkConfigurationPropertiesReader.readUfEnvironment();
        if(ufEnvironment.equals(Environment.Custom)){
            return setAccessTokenFromSdkProperties().selectCustom().loadConfigFromSdkProperties().build();
        }
        return setAccessTokenFromSdkProperties().selectEnvironment(ufEnvironment).loadConfigFromSdkProperties().build();
    }

    /**
     * Sets the general configuration properties to values read from configuration file. Only value which can be set
     * through {@link ConfigurationBuilderBase} methods are set. Any values already set by methods on the current instance
     * are overridden. Builds and returns a {@link OddsFeedConfiguration} instance
     * <p>
     * The YAML file should be named "application.yml" and localed in the application resources folder
     *
     * @return builds and returns a {@link OddsFeedConfiguration} instance
     */
    @Override
    public OddsFeedConfiguration buildConfigFromApplicationYml() {
        Environment ufEnvironment = sdkConfigurationYamlReader.readUfEnvironment();
        return setAccessTokenFromApplicationYaml().selectEnvironment(ufEnvironment).loadConfigFromApplicationYml().build();
    }
}
