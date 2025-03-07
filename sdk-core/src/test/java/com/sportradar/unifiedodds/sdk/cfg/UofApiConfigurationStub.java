/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */
package com.sportradar.unifiedodds.sdk.cfg;

import java.time.Duration;

public class UofApiConfigurationStub implements UofApiConfiguration {

    private Duration httpClientTimeout;
    private Duration httpClientRecoveryTimeout;
    private String host;
    private Duration httpClientFastFailingTimeout;

    @Override
    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    @Override
    public int getPort() {
        return 0;
    }

    @Override
    public boolean getUseSsl() {
        return false;
    }

    public void setHttpClientTimeout(Duration duration) {
        this.httpClientTimeout = duration;
    }

    @Override
    public Duration getHttpClientTimeout() {
        return httpClientTimeout;
    }

    public void setHttpClientRecoveryTimeout(Duration duration) {
        this.httpClientRecoveryTimeout = duration;
    }

    @Override
    public Duration getHttpClientRecoveryTimeout() {
        return httpClientRecoveryTimeout;
    }

    @Override
    public Duration getHttpClientFastFailingTimeout() {
        return httpClientFastFailingTimeout;
    }

    public void setHttpClientFastFailingTimeout(Duration duration) {
        this.httpClientFastFailingTimeout = duration;
    }

    @Override
    public int getHttpClientMaxConnTotal() {
        return 0;
    }

    @Override
    public int getHttpClientMaxConnPerRoute() {
        return 0;
    }

    @Override
    public String getReplayHost() {
        return null;
    }
}
