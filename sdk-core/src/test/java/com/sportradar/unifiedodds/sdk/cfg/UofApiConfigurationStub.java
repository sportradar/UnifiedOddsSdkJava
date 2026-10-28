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
    private boolean useSsl;
    private int port;
    private int httpClientMaxConnTotal;
    private int httpClientMaxConnPerRoute;

    @Override
    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public void setPort(int port) {
        this.port = port;
    }

    @Override
    public int getPort() {
        return port;
    }

    public void setUseSsl(boolean useSsl) {
        this.useSsl = useSsl;
    }

    @Override
    public boolean getUseSsl() {
        return useSsl;
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

    public void setHttpClientMaxConnTotal(int httpClientMaxConnTotal) {
        this.httpClientMaxConnTotal = httpClientMaxConnTotal;
    }

    @Override
    public int getHttpClientMaxConnTotal() {
        return httpClientMaxConnTotal;
    }

    public void setHttpClientMaxConnPerRoute(int httpClientMaxConnPerRoute) {
        this.httpClientMaxConnPerRoute = httpClientMaxConnPerRoute;
    }

    @Override
    public int getHttpClientMaxConnPerRoute() {
        return httpClientMaxConnPerRoute;
    }

    @Override
    public String getReplayHost() {
        return null;
    }
}
