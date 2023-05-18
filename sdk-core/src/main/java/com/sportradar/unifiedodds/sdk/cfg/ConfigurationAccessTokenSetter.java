/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */

package com.sportradar.unifiedodds.sdk.cfg;

/**
 * The first configuration builder step in which you need to provide/set the access token provided by Sportradar
 */
@SuppressWarnings({ "LineLength" })
public interface ConfigurationAccessTokenSetter {
    /**
     * Set your access token, provided by Sportradar (without this set you will not be able to
     * connect to the Sportradar AMQP broker and the SportsAPI)
     *
     * @param accessToken the access token provided by Sportradar
     * @return the current instance {@link OddsFeedConfigurationBuilder}
     */
    OddsFeedConfigurationBuilder setAccessToken(String accessToken);

    /**
     * Try to set your access token, provided by Sportradar, trough the system variable "uf.accesstoken".
     * You can set the access token with the following JVM argument -Duf.accesstoken=your-access-token.
     *
     * If you do not want to set the access token as a system variable, use {@link #setAccessToken(String)}
     *
     * @return the current instance {@link OddsFeedConfigurationBuilder}
     */
    OddsFeedConfigurationBuilder setAccessTokenFromSystemVar();

    /**
     * Try to set your access token as provided by Sportradar trough the SDK properties, "uf.sdk.accessToken"
     * The properties file should be named "UFSdkConfiguration.properties" and localed in the application resources folder
     *
     * @return the current instance {@link OddsFeedConfigurationBuilder}
     */
    OddsFeedConfigurationBuilder setAccessTokenFromSdkProperties();
}
