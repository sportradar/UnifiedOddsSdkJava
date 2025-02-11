/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */

package com.sportradar.unifiedodds.sdk.cfg;

/**
 * Defines methods implemented by classes taking care of the 1st step when building configuration - setting the token.
 */
@SuppressWarnings({ "LineLength" })
public interface TokenSetter {
    /**
     * Sets the access token used to access feed resources (AMQP broker, Sports API, ...)
     *
     * @param token the access token used to access feed resources
     * @return the {@link EnvironmentSelector} instance allowing the selection of target environment
     */
    EnvironmentSelector setAccessToken(String token);

    /**
     * Sets the access token used to access feed resources (AMQP broker, Sports API, ...) to value read from the sdk properties
     *
     * The properties file should be named "UFSdkConfiguration.properties" and localed in the application resources folder
     *
     * @return the {@link EnvironmentSelector} instance allowing the selection of target environment
     */
    EnvironmentSelector setAccessTokenFromSdkProperties();

    /**
     * Sets the access token used to access feed resources (AMQP broker, Sports API, ...) to value read from the application.yml
     *
     * The YAML file should be named "application.properties" and localed in the application resources folder
     *
     * @return the {@link EnvironmentSelector} instance allowing the selection of target environment
     */
    EnvironmentSelector setAccessTokenFromApplicationYaml();

    /**
     * Sets the access token used to access feed resources (AMQP broker, Sports API, ...) to value read from system variable "uf.accesstoken"
     * <p>You can set the access token with the following JVM argument -Duf.accesstoken=your-access-token</p>
     *
     * @return the {@link EnvironmentSelector} instance allowing the selection of target environment
     */
    EnvironmentSelector setAccessTokenFromSystemVar();

    /**
     * Sets the general configuration properties to values read from configuration file. Only value which can be set
     * through {@link ConfigurationBuilderBase} methods are set. Any values already set by methods on the current instance
     * are overridden. Builds and returns a {@link UofConfiguration} instance
     *
     * The properties file should be named "UFSdkConfiguration.properties" and localed in the application resources folder
     *
     * @return builds and returns a {@link UofConfiguration} instance
     */
    UofConfiguration buildConfigFromSdkProperties();

    /**
     * Sets the general configuration properties to values read from configuration file. Only value which can be set
     * through {@link ConfigurationBuilderBase} methods are set. Any values already set by methods on the current instance
     * are overridden. Builds and returns a {@link UofConfiguration} instance
     *
     * The YAML file should be named "application.yml" and localed in the application resources folder
     *
     * @return builds and returns a {@link UofConfiguration} instance
     */
    UofConfiguration buildConfigFromApplicationYml();
}
