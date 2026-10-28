/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */
package com.sportradar.unifiedodds.sdk;

import static com.sportradar.unifiedodds.sdk.internal.cfg.BaseUrl.baseUrl;
import static com.sportradar.unifiedodds.sdk.internal.cfg.ConfigHelper.getHostAndPort;

import com.sportradar.unifiedodds.sdk.cfg.UofClientAuthentication;
import com.sportradar.unifiedodds.sdk.internal.cfg.BaseUrl;

public class UofClientAuthenticationAssertions {

    private final UofClientAuthentication.PrivateKeyJwt clientAuthentication;

    public UofClientAuthenticationAssertions(UofClientAuthentication.PrivateKeyJwt clientAuthentication) {
        this.clientAuthentication = clientAuthentication;
    }

    public static UofClientAuthenticationAssertions assertThat(
        UofClientAuthentication.PrivateKeyJwt clientAuthentication
    ) {
        return new UofClientAuthenticationAssertions(clientAuthentication);
    }

    public UofClientAuthenticationAssertions hasHost(String expectedHost) {
        org.assertj.core.api.Assertions.assertThat(clientAuthentication.getHost()).isEqualTo(expectedHost);
        return this;
    }

    public UofClientAuthenticationAssertions hasPort(int expectedPort) {
        org.assertj.core.api.Assertions.assertThat(clientAuthentication.getPort()).isEqualTo(expectedPort);
        return this;
    }

    public UofClientAuthenticationAssertions hasHostAndPortAsUrlSegmentEqualTo(String expectedHostAndPort) {
        String actualHostAndPort = getHostAndPort(clientAuthentication);
        org.assertj.core.api.Assertions.assertThat(actualHostAndPort).isEqualTo(expectedHostAndPort);

        BaseUrl configuredBaseUrl = baseUrl()
            .setUseSsl(clientAuthentication.getUseSsl())
            .setHost(clientAuthentication.getHost())
            .setPort(clientAuthentication.getPort());
        org.assertj.core.api.Assertions
            .assertThat(configuredBaseUrl.getHostAndPort())
            .isEqualTo(actualHostAndPort);
        return this;
    }

    public void hasAuthServerOriginEqualTo(String urlOrigin) {
        BaseUrl configuredBaseUrl = baseUrl()
            .setUseSsl(clientAuthentication.getUseSsl())
            .setHost(clientAuthentication.getHost())
            .setPort(clientAuthentication.getPort());
        org.assertj.core.api.Assertions.assertThat(configuredBaseUrl.toString()).isEqualTo(urlOrigin);
    }

    public UofClientAuthenticationAssertions hasUseSsl(boolean useSsl) {
        org.assertj.core.api.Assertions.assertThat(clientAuthentication.getUseSsl()).isEqualTo(useSsl);
        return this;
    }
}
