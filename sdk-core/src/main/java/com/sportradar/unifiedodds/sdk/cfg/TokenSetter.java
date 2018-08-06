/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */

package com.sportradar.unifiedodds.sdk.cfg;

/**
 * Defines methods implemented by classes taking care of the 1st step when building configuration - setting the token.
 */
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
}
