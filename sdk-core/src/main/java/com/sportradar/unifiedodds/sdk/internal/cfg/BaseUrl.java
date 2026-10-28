/*
 * Copyright (C) Sportradar AG. See LICENSE for full license governing this code
 */
package com.sportradar.unifiedodds.sdk.internal.cfg;

public class BaseUrl {

    private static final int HTTPS_PORT = 443;
    private static final int HTTP_PORT = 80;
    private boolean useSsl = true;
    private String host;
    private int port;

    public static BaseUrl baseUrl() {
        return new BaseUrl();
    }

    public BaseUrl setPort(int newPort) {
        this.port = newPort;
        return this;
    }

    public int getPort() {
        return port;
    }

    public BaseUrl setHost(String newHost) {
        if (newHost.contains("://")) {
            throw new IllegalArgumentException(
                "Host should not contain scheme prefix (http:// or https://). Found: " + newHost
            );
        }
        if (newHost.contains(":")) {
            throw new IllegalArgumentException(
                "Host should not contain port. Use setPort() method instead. Found: " + newHost
            );
        }
        this.host = newHost;
        return this;
    }

    public String getHost() {
        return host;
    }

    public BaseUrl setUseSsl(boolean useSsl) {
        this.useSsl = useSsl;
        return this;
    }

    public boolean isUseSsl() {
        return useSsl;
    }

    public String getHostAndPort() {
        int defaultPort = useSsl ? HTTPS_PORT : HTTP_PORT;
        if (port == 0 || port == defaultPort) {
            return host;
        }
        return host + ":" + port;
    }

    public String toString() {
        String scheme = useSsl ? "https://" : "http://";
        return scheme + getHostAndPort();
    }
}
