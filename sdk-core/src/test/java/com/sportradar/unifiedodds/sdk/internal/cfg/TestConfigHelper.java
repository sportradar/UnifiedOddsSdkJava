package com.sportradar.unifiedodds.sdk.internal.cfg;

import static org.assertj.core.api.Assertions.assertThat;

import com.sportradar.unifiedodds.sdk.cfg.UofApiConfigurationStub;

public class TestConfigHelper {

    public static void setHostAndPort(String authorityOfUri, UofApiConfigurationStub apiConfig) {
        apiConfig.setHost(getHostFrom(authorityOfUri));
        apiConfig.setPort(getPortFrom(authorityOfUri));
    }

    public static String getHostFrom(String authorityOfUri) {
        String[] hostAndPort = authorityOfUri.split(":");
        assertThat(hostAndPort.length).isEqualTo(2);

        String host = hostAndPort[0];
        assertThat(host).isNotEmpty();

        return host;
    }

    public static int getPortFrom(String authorityOfUri) {
        String[] hostAndPort = authorityOfUri.split(":");
        assertThat(hostAndPort.length).isEqualTo(2);

        String port = hostAndPort[1];
        assertThat(port).isNotEmpty();

        return Integer.parseInt(port);
    }
}
