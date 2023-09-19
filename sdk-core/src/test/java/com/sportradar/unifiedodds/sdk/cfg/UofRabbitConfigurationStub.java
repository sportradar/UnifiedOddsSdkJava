/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */
package com.sportradar.unifiedodds.sdk.cfg;

import java.time.Duration;

public class UofRabbitConfigurationStub implements UofRabbitConfiguration {

    private String host;

    public UofRabbitConfigurationStub setHost(String host) {
        this.host = host;
        return this;
    }

    @Override
    public String getHost() {
        return host;
    }

    @Override
    public int getPort() {
        return 0;
    }

    @Override
    public boolean getUseSsl() {
        return false;
    }

    @Override
    public String getUsername() {
        return null;
    }

    @Override
    public String getPassword() {
        return null;
    }

    @Override
    public String getVirtualHost() {
        return null;
    }

    @Override
    public Duration getConnectionTimeout() {
        return null;
    }

    @Override
    public Duration getHeartBeat() {
        return null;
    }
}
