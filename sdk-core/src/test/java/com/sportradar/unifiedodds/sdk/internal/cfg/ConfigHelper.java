package com.sportradar.unifiedodds.sdk.internal.cfg;

import com.sportradar.unifiedodds.sdk.cfg.UofApiConfiguration;
import com.sportradar.unifiedodds.sdk.cfg.UofClientAuthentication;

public class ConfigHelper {

    public static String getApiHostAndPort(UofApiConfiguration config) {
        return getHostAndPort(config.getHost(), config.getPort());
    }

    public static String getHostAndPort(UofClientAuthentication.PrivateKeyJwt config) {
        return getHostAndPort(config.getHost(), config.getPort());
    }

    private static String getHostAndPort(String host, int port) {
        final int defaultHttpPort = 80;
        String portString = port == defaultHttpPort || port == 0 ? "" : ":" + port;
        return host + portString;
    }
}
