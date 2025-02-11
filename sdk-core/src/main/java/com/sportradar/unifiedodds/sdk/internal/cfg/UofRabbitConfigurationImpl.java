/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */

package com.sportradar.unifiedodds.sdk.internal.cfg;

import com.google.common.base.Strings;
import com.sportradar.unifiedodds.sdk.cfg.UofRabbitConfiguration;
import com.sportradar.unifiedodds.sdk.internal.impl.EnvironmentManager;
import java.time.Duration;
import java.util.StringJoiner;

public class UofRabbitConfigurationImpl implements UofRabbitConfiguration {

    private String host;
    private int port;
    private boolean useSsl;
    private String username;
    private String password;
    private String virtualHost;
    private Duration connectionTimeout;
    private Duration heartBeat;

    public UofRabbitConfigurationImpl() {
        useSsl = true;
        connectionTimeout = Duration.ofSeconds(ConfigLimit.RABBIT_CONNECTION_TIMEOUT_DEFAULT);
        heartBeat = Duration.ofSeconds(ConfigLimit.RABBIT_HEARTBEAT_DEFAULT);
    }

    @Override
    public String getHost() {
        return host;
    }

    @Override
    public int getPort() {
        return port;
    }

    @Override
    public boolean getUseSsl() {
        return useSsl;
    }

    @Override
    public String getUsername() {
        return username;
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public String getVirtualHost() {
        return virtualHost;
    }

    @Override
    public Duration getConnectionTimeout() {
        return connectionTimeout;
    }

    @Override
    public Duration getHeartBeat() {
        return heartBeat;
    }

    public void setHost(String messagingHost) {
        if (!Strings.isNullOrEmpty(messagingHost)) {
            this.host = messagingHost;
        }
    }

    public void setPort(int messagingPort) {
        if (messagingPort > 0) {
            this.port = messagingPort;
        }
    }

    public void useSsl(boolean messagingUseSsl) {
        this.useSsl = messagingUseSsl;

        if (
            port == 0 ||
            port == EnvironmentManager.DEFAULT_MQ_HOST_PORT ||
            port == EnvironmentManager.DEFAULT_MQ_HOST_PORT + 1
        ) {
            this.port =
                useSsl
                    ? EnvironmentManager.DEFAULT_MQ_HOST_PORT
                    : EnvironmentManager.DEFAULT_MQ_HOST_PORT + 1;
        }
    }

    public void setUsername(String messagingUsername) {
        if (!Strings.isNullOrEmpty(messagingUsername)) {
            this.username = messagingUsername;
        }
    }

    public void setPassword(String messagingPassword) {
        if (!Strings.isNullOrEmpty(messagingPassword)) {
            this.password = messagingPassword;
        }
    }

    public void setVirtualHost(String messagingVirtualHost) {
        if (!Strings.isNullOrEmpty(messagingVirtualHost)) {
            this.virtualHost = messagingVirtualHost;
        }
    }

    void setConnectionTimeout(int timeout) {
        if (
            timeout >= ConfigLimit.RABBIT_CONNECTION_TIMEOUT_MIN &&
            timeout <= ConfigLimit.RABBIT_CONNECTION_TIMEOUT_MAX
        ) {
            connectionTimeout = Duration.ofSeconds(timeout);
            return;
        }

        String msg = String.format("Invalid timeout value for ConnectionTimeout: %s s.", timeout);
        throw new IllegalArgumentException(msg);
    }

    void setHeartBeat(int timeout) {
        if (timeout >= ConfigLimit.RABBIT_HEARTBEAT_MIN && timeout <= ConfigLimit.RABBIT_HEARTBEAT_MAX) {
            heartBeat = Duration.ofSeconds(timeout);
            return;
        }

        String msg = String.format("Invalid timeout value for HeartBeat: %s s.", timeout);
        throw new IllegalArgumentException(msg);
    }

    @Override
    public String toString() {
        return new StringJoiner(", ", "RabbitConfiguration{", "}")
            .add("host=" + host)
            .add("port=" + port)
            .add("useSsl=" + useSsl)
            .add("username=" + username)
            .add("password=" + password)
            .add("virtualHost=" + virtualHost)
            .add("connectionTimeout=" + connectionTimeout.getSeconds())
            .add("heartBeat=" + heartBeat.getSeconds())
            .toString();
    }
}
